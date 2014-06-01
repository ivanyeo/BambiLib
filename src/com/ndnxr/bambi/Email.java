package com.ndnxr.bambi;

import java.io.Serializable;
import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class Email implements Parcelable, Serializable {
	/**
	 * Email Version to keep track of during software upgrades. 
	 */
	private static final long serialVersionUID = 1L;
	
	private String username;
	private String password;
	private String serverAddress;
	private String serverPort;
	
	private String from, subject, message;
	private String[] toArray = new String[8];
	private String[] filePaths = new String[8];
	
	public Email(String username, String password, String serverAddress,
			String serverPort, String from, String subject, String message,
			String[] toArray, String[] filePaths) {
		super();
		this.username = username;
		this.password = password;
		this.serverAddress = serverAddress;
		this.serverPort = serverPort;
		this.from = from;
		this.subject = subject;
		this.message = message;
		
		// Copy array over
		if (toArray != null) {
			for (int i=0; i<toArray.length; i++) {
				this.toArray[i] = toArray[i];
			}
		}
		
		if (filePaths != null) {
			for (int i=0; i<filePaths.length; i++) {
				this.filePaths[i] = filePaths[i];
			}
		}
		
	}

	public Email(Parcel in) {
		username = in.readString();
		password = in.readString();
		serverAddress = in.readString();
		serverPort = in.readString();
		
		from = in.readString();
		subject = in.readString();
		message = in.readString();
		
		in.readStringArray(toArray);
		in.readStringArray(filePaths);
	}
	
	@Override
	public String toString() {
		return String.format("%s, %s, %s", from, subject, message);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(username);
		dest.writeString(password);
		dest.writeString(serverAddress);
		dest.writeString(serverPort);
		
		dest.writeString(from);
		dest.writeString(subject);
		dest.writeString(message);
		
		dest.writeStringArray(toArray);
		dest.writeStringArray(filePaths);
	}
	
	public static final Parcelable.Creator<Email> CREATOR = new Parcelable.Creator<Email>() {
		public Email createFromParcel(Parcel in) {
			return new Email(in);
		}

		public Email[] newArray(int size) {
			return new Email[size];
		}
	};

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getServerAddress() {
		return serverAddress;
	}

	public void setServerAddress(String serverAddress) {
		this.serverAddress = serverAddress;
	}

	public String getServerPort() {
		return serverPort;
	}

	public void setServerPort(String serverPort) {
		this.serverPort = serverPort;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String[] getToArray() {
		return toArray;
	}

	public void setToArray(String[] toArray) {
		this.toArray = toArray;
	}

	public String[] getFilePaths() {
		return filePaths;
	}

	public void setFilePaths(String[] filePaths) {
		this.filePaths = filePaths;
	}

}
