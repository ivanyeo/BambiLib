package com.ndnxr.bambi;

import java.io.Serializable;
import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

import com.ndnxr.bambi.BambiLib.TASK_TYPE;
import com.ndnxr.bambi.BambiLib.URGENCY;

public class Task implements Parcelable {
	private URGENCY urgency;
	private Date date;
	private TASK_TYPE type;
	private Parcelable payload;

	public Task(TASK_TYPE type, URGENCY urgency, Date date, Parcelable payload) {
		super();

		// Error check
		if (type == null) {
			throw new RuntimeException("TASK_TYPE: Cannot be null.");
		}

		// Error check
		if (urgency == null) {
			throw new RuntimeException("URGENCY: Cannot be null.");
		}

		// Set values
		this.type = type;
		this.urgency = urgency;
		this.date = date;
		this.payload = payload;
	}

	public Task(Parcel in) {
		// Re-contruction of object from Parcel
		urgency = (URGENCY) in.readSerializable();
		date = (Date) in.readSerializable();
		type = (TASK_TYPE) in.readSerializable();
		payload = in.readParcelable(Email.class.getClassLoader());
	}

	public Object getPayload() {
		return payload;
	}

	public void setPayload(Parcelable payload) {
		this.payload = payload;
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

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// Flatten/Serialize Data
		dest.writeSerializable(urgency);
		dest.writeSerializable(date);
		dest.writeSerializable(type);
		dest.writeParcelable(payload, flags);
	}

	public static final Parcelable.Creator<Task> CREATOR = new Parcelable.Creator<Task>() {
		public Task createFromParcel(Parcel in) {
			return new Task(in);
		}

		public Task[] newArray(int size) {
			return new Task[size];
		}
	};
}
