package com.ndnxr.bambi;

import java.io.Serializable;
import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

import com.ndnxr.bambi.BambiLib.TASK_TYPE;
import com.ndnxr.bambi.BambiLib.URGENCY;

public class Task implements Parcelable, Serializable {
	private URGENCY urgency;
	private Date deadline;
	private TASK_TYPE type;
	private Parcelable payload;
	
	// Cell Information
	private int cellSignalStrengthDbm;
	private int cellDataNetworkType;
	
	// Wifi Information
	private int wifiSignalStrengthDbm;

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
		this.deadline = date;
		this.payload = payload;
	}

	public Task(Parcel in) {
		// Re-contruction of object from Parcel
		urgency = (URGENCY) in.readSerializable();
		deadline = (Date) in.readSerializable();
		type = (TASK_TYPE) in.readSerializable();
		payload = in.readParcelable(Email.class.getClassLoader());
		cellSignalStrengthDbm = in.readInt();
		cellDataNetworkType = in.readInt();
		wifiSignalStrengthDbm = in.readInt();
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

	public Date getDeadline() {
		return deadline;
	}

	public void setDeadline(Date deadline) {
		this.deadline = deadline;
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

	public int getCellSignalStrengthDbm() {
		return cellSignalStrengthDbm;
	}

	public void setCellSignalStrengthDbm(int cellSignalStrengthDbm) {
		this.cellSignalStrengthDbm = cellSignalStrengthDbm;
	}

	public int getCellDataNetworkType() {
		return cellDataNetworkType;
	}

	public void setCellDataNetworkType(int cellDataNetworkType) {
		this.cellDataNetworkType = cellDataNetworkType;
	}

	public int getWifiSignalStrengthDbm() {
		return wifiSignalStrengthDbm;
	}

	public void setWifiSignalStrengthDbm(int wifiSignalStrengthDbm) {
		this.wifiSignalStrengthDbm = wifiSignalStrengthDbm;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// Flatten/Serialize Data
		dest.writeSerializable(urgency);
		dest.writeSerializable(deadline);
		dest.writeSerializable(type);
		dest.writeParcelable(payload, flags);
		dest.writeInt(cellSignalStrengthDbm);
		dest.writeInt(cellDataNetworkType);
		dest.writeInt(wifiSignalStrengthDbm);
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
