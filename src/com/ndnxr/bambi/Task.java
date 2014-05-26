package com.ndnxr.bambi;

import java.util.Date;

import com.ndnxr.bambi.BambiLib.TASK_TYPE;
import com.ndnxr.bambi.BambiLib.URGENCY;

public class Task {
	private URGENCY urgency;
	private Date date;
	private TASK_TYPE type;

	public Task(TASK_TYPE type, URGENCY urgency, Date date) {
		super();

		// Error check
		if (type == null) {
			throw new RuntimeException("TASK_TYPE: Cannot be null.");
		}

		// Error check
		if (urgency == null) {
			throw new RuntimeException("URGENCY: Cannot be null.");
		}

		// Process task based on urgency
		switch (urgency) {
		case NOW:
			// TODO Immediate Processing
			break;

		case SCHEDULE:
			// Ensure date is not null
			if (date == null) {
				throw new RuntimeException("SCHEDULE: Date cannot be null.");
			}

			// Throw an Exception if date is in the past
			if (new Date().compareTo(date) < 0) {
				throw new RuntimeException("Unable to schedule a Date in the past.");
			}

			// Set values
			this.urgency = urgency;
			this.date = date;
			
			// TODO: Invoke Bambi Service to Schedule Task
			break;

		case WIFI_ACTIVE:
			// TODO Schedule Wifi Task
			break;

		default:
			break;
		}
	}

	public Task(TASK_TYPE type, URGENCY urgency) {
		this(type, urgency, null);
	}

	public URGENCY getUrgency() {
		return urgency;
	}

	public void setUrgency(URGENCY urgency) {
		this.urgency = urgency;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public TASK_TYPE getType() {
		return type;
	}

	public void setType(TASK_TYPE type) {
		this.type = type;
	}
}
