package com.ndnxr.bambi;

import java.util.ArrayList;
import java.util.Date;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcelable;
import android.os.RemoteException;
import android.telephony.TelephonyManager;

public class BambiLib {
	// Messages to be passed to the Service by Clients for processing
	static final int MESSAGE_REGISTER_CLIENT = 1;
	static final int MESSAGE_UNREGISTER_CLIENT = 2;
	static final int MESSAGE_REGISTER_SUCCESS = 3;
	static final int MESSAGE_UNREGISTER_SUCCESS = 4;

	// Supported Service Functions
	static final int MESSAGE_SEND_EMAIL = 11;

	// Task Information Functions
	static final int MESSAGE_GET_BAMBI_TASKS = 21;
	static final int MESSAGE_RECEIVE_BAMBI_TASKS = 22;

	// Messages for BambiService
	public static final int MESSAGE_STORE_TASK = 31;
	public static final int MESSAGE_STORE_OTHERS = 32;
	
	// Messages reply from BambiService
	public static final int MESSAGE_PROCESS_TASK_COMPLETE = 41;
	
	// BambiService Message: Key values used for Intent Messages
	public static final String MESSAGE_STORE = "STORE_MESSAGE";
	public static final String MESSAGE_TASK = "TASK";

	// Urgency Enum
	public enum URGENCY {
		URGENT, SCHEDULE, NORMAL,
	}

	// Task Type Enum
	public enum TASK_TYPE {
		EMAIL
	}
	
	// Local Variables
	private Context context = null;
	private final ArrayList<Task> pendingTasks = new ArrayList<Task>();
	private volatile boolean shutdownRequested = false;
	private volatile Object bambiServiceMessengerSyncObject = new Object();
	private volatile Object shutdownSyncObject = new Object();

	/**
	 * Local contructor to establish context.
	 * 
	 * @param context
	 *            Current activity context.
	 */
	public BambiLib(Context context) {
		super();

		// Establish Context
		this.context = context;

		// Bind to Service
		bindBambiService();
	}

	/**
	 * Sends a requested email as a Task to BambiService.
	 * 
	 * @return true on success; false otherwise.
	 */
	public boolean sendEmail(Task task) {
		// Error check
		if (task == null) {
			throw new RuntimeException("sendEmail(): Task cannot be null.");
		}

		// Check that there is a payload and is of type Email
		if (task.getPayload() == null || !(task.getPayload() instanceof Email)) {
			throw new RuntimeException(
					"sendEmail(): Task payload must be an instance of Email.");
		}

		// Schedule task for processing
		return scheduleTask(task);
	}

	/**
	 * Task processing controller as a central logic.
	 * 
	 * @param task
	 *            Task given for processing
	 */
	private boolean processTask(Task task) {
		// Ensure Service is bound
		if (!mIsBambiServiceBound) {
			// Service might have been killed by Kernel, try again later.
			return false;
		}

		// Error Check: Cannot process Task when ServiceConnection is not ready
		if (!isServiceMessengerReady()) {
			Config.Log("mBambiServiceMessenger is null!!");
			return false;
		}

		// Processing according to Task type
		switch (task.getType()) {
		case EMAIL:
			// Get a Message
			Message msg = Message.obtain(null, BambiLib.MESSAGE_SEND_EMAIL);

			// Place Parcelable here, not in the obj of Message.obtain()
			msg.getData()
					.putParcelable(MESSAGE_TASK, (Parcelable) task);

			// Send Message to Service
			try {
				mBambiServiceMessenger.send(msg);
			} catch (RemoteException e) {
				Config.Log("sendEmail(): ERROR: " + e.toString());
			}
			break;

		default:
			throw new RuntimeException("Invalid TASK_TYPE.");
		}

		return true;
	}

	/**
	 * Method that schedule Task according to the URGENCY type.
	 * 
	 * @param task
	 *            Given Task to be scheduled.
	 * 
	 * @return true on successful scheduling; false otherwise
	 */
	private boolean scheduleTask(Task task) {
		switch (task.getUrgency()) {
		case URGENT:
			// Service has not started
			if (!isServiceMessengerReady()) {
				synchronized (pendingTasks) {
					// Append to pending task list
					pendingTasks.add(task);
				}

				return true;
			} else {
				// Begin processing the task
				return processTask(task);
			}
			
		// Fall-through cases: Only difference is the checking of deadline Date object in URGENCY.SCHEDULE
		case SCHEDULE:	// Perform Task on Wifi Active or Before Deadline
			// Error Check: Task deadline cannot be in the past
			if (task.getDeadline() != null && !task.getDeadline().after(new Date())) {
				return false;
			}
		case NORMAL:	// Send only when there is Wifi connection
			// Create BambiEnergyService Intent
			Intent intent = new Intent();
			intent.setComponent(new ComponentName("com.ndnxr.bambi",
					"com.ndnxr.bambi.BambiEnergyService"));
			
			intent.putExtra(BambiLib.MESSAGE_STORE, BambiLib.MESSAGE_STORE_TASK);
			intent.putExtra(BambiLib.MESSAGE_TASK, (Parcelable) task);
			
			// Start service with single message
			context.startService(intent);

			break;
		default:
			throw new RuntimeException("Invalid URGENCY Type.");
		}

		return true;
	}

	/**
	 * Process all tasks that are in the pending tast list.
	 */
	private void processPendingTasks() {
		synchronized (pendingTasks) {
			// Process each task in the list
			for (Task t : pendingTasks) {
				processTask(t);
			}

			// Empty the list
			pendingTasks.clear();
		}
	}

	/**
	 * Method that binds to the BambiEnergyService.
	 */
	private void bindBambiService() {
		// Create Intent for Service
		Intent intent = new Intent();
		intent.setComponent(new ComponentName("com.ndnxr.bambi",
				"com.ndnxr.bambi.BambiEnergyService"));

		// Bind to Service
		context.bindService(intent, mServiceConnection,
				Context.BIND_AUTO_CREATE);

		// Set bound flag
		mIsBambiServiceBound = true;

		// Output Log
		Config.Log("BambiLib::bindBambiService()");
	}

	/**
	 * Method that unbinds from the BambiEnergyService.
	 */
	private void unbindBambiService() {
		// Unregister Service if it has been bound
		if (mIsBambiServiceBound) {
			if (isServiceMessengerReady()) {
				try {
					// Send Message to BambiEnergy Service to UNREGISTER_CLIENT
					Message msg = Message.obtain(null,
							BambiLib.MESSAGE_UNREGISTER_CLIENT);

					msg.replyTo = mClientMessenger;

					mBambiServiceMessenger.send(msg);
				} catch (RemoteException e) {
					// Service has crashed, nothing to do here
					Config.Log("MainActivity::unbindBambiService() Error: " + e);
				}
			}

			// Release connection
			context.unbindService(mServiceConnection);
			mIsBambiServiceBound = false;

			Config.Log("MainActivity::unbindBambiService(): Success");
		}
	}

	/**
	 * Shutdown the BambiLib. This releases the ServiceConnection with the
	 * Service.
	 */
	public void shutdown() {
		synchronized (shutdownSyncObject) {
			// Error check
			if (shutdownRequested == true) {
				return;
			}

			// Set Flag
			shutdownRequested = true;
		}

		synchronized (pendingTasks) {
			// Shutdown when there are no PendingTasks
			if (pendingTasks.size() == 0) {
				unbindBambiService();
			}
		}
	}

	private boolean isServiceMessengerReady() {
		synchronized (bambiServiceMessengerSyncObject) {
			return mBambiServiceMessenger != null;
		}
	}

	/** Messenger connection to BambiEnergyService */
	private volatile Messenger mBambiServiceMessenger = null;

	/** Flag if client is connected to BambiEnergyService */
	private volatile boolean mIsBambiServiceBound = false;

	/** Client ServiceConnection to BambiEnergyService */
	private ServiceConnection mServiceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {

			// Synchronize before update
			synchronized (bambiServiceMessengerSyncObject) {
				// Bound to BambiEnergyService, establish Messenger to the
				// Service
				mBambiServiceMessenger = new Messenger(service);
			}

			// Make request to register client
			try {
				// Get a Message
				Message msg = Message.obtain(null,
						BambiLib.MESSAGE_REGISTER_CLIENT);

				// Set reply Handler
				msg.replyTo = mClientMessenger;

				// Send Message to Service
				mBambiServiceMessenger.send(msg);
			} catch (RemoteException e) {
				// If Service crashes, nothing to do here
			}

			// Synchronize before accessing pendingTasks
			synchronized (pendingTasks) {
				// If there are tasks pending, begin processing them
				if (pendingTasks.size() > 0) {
					processPendingTasks();
				}
			}

			synchronized (shutdownSyncObject) {
				if (shutdownRequested == true) {
					unbindBambiService();
				}
			}

			Config.Log("mServiceConnection::onServiceConnected()");
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			// In event of unexpected disconnection with the Service.
			// Not expecting to get here.
			mServiceConnection = null;
			// mIsBambiServiceBound = false;

			Config.Log("mConnection::onServiceDisconnected()");
		}
	};

	/** Client Message Handler */
	final Messenger mClientMessenger = new Messenger(new ClientHandler());

	/**
	 * Client Message Handler Class
	 */
	class ClientHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case BambiLib.MESSAGE_REGISTER_SUCCESS:
				Config.Log("ClientHandler::handleMessage(): MESSAGE_REGISTER_SUCCESS");
				break;
			case BambiLib.MESSAGE_UNREGISTER_SUCCESS:
				Config.Log("ClientHandler::handleMessage(): MESSAGE_UNREGISTER_SUCCESS");
				break;
			default:
				super.handleMessage(msg);
				break;
			}
		}
	}

}
