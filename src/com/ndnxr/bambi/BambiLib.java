package com.ndnxr.bambi;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

public class BambiLib {
	// Messages to be passed to the Service by Clients for processing
	static final int MESSAGE_REGISTER_CLIENT 	= 1;
	static final int MESSAGE_UNREGISTER_CLIENT 	= 2;
	static final int MESSAGE_REGISTER_SUCCESS	= 3;
	static final int MESSAGE_UNREGISTER_SUCCESS	= 4;
	
	// Supported Service Functions
	static final int MESSAGE_SEND_EMAIL			= 11;
	
	// Task Information Functions
	static final int MESSAGE_GET_BAMBI_TASKS		= 21;
	static final int MESSAGE_RECEIVE_BAMBI_TASKS	= 22;
	
	// Urgency Enum
	public enum URGENCY {
		NOW, SCHEDULE, WIFI_ACTIVE,
	}
	
	// Task Type Enum
	public enum TASK_TYPE {
		EMAIL
	}
	
	// Local Variables
	Context context = null;
	
	/**
	 * Local contructor to establish context.
	 * 
	 * @param context Current activity context.
	 */
	public BambiLib(Context context) {
		super();
		this.context = context;
	}


	/**
	 * Sends a requested email as a Task to BambiService.
	 * 
	 * @return true on success; false otherwise.
	 */
	public boolean sendEmail(Task task, Email email) {
		// Error check
		if (task == null || email == null) {
			throw new RuntimeException("Task and Email cannot be null types.");
		}
		
		// Bind to service
		bindBambiService();
		
		return false;
	}
	

	/**
	 * Method that binds to the BambiEnergyService.
	 */
	private void bindBambiService() {
		// Create Intent for Service
		Intent intent = new Intent();
		intent.setComponent(
				new ComponentName("com.ndnxr.bambi", "com.ndnxr.bambi.BambiEnergyService"));
		
		// Bind to Service
		context.bindService(intent,
				mServiceConnection, Context.BIND_AUTO_CREATE);

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
			if (mBambiServiceMessenger != null) {
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

	/** Messenger connection to BambiEnergyService */
	private Messenger mBambiServiceMessenger = null;

	/** Flag if client is connected to BambiEnergyService */
	private boolean mIsBambiServiceBound = false;

	/** Client ServiceConnection to BambiEnergyService */
	private ServiceConnection mServiceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			// Bound to BambiEnergyService, establish Messenger to the Service
			mBambiServiceMessenger = new Messenger(service);

			// Make request to register client
			try {
				// Get a Message
				Message msg = Message.obtain(null,
						BambiLib.MESSAGE_SEND_EMAIL);
				
				// Set reply Handler
				msg.replyTo = mClientMessenger;
				
				// Send Message to Service
				mBambiServiceMessenger.send(msg);
			} catch (RemoteException e) {
				// If Service crashes, nothing to do here
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
