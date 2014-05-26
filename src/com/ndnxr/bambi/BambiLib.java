package com.ndnxr.bambi;

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
	
	/**
	 * Sends a requested email as a Task to BambiService.
	 * 
	 * @return true on success; false otherwise.
	 */
	public static boolean sendEmail(Task task, Email email) {
		// TODO Write the email task
		return false;
	}
}
