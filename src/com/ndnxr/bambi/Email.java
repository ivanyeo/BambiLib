package com.ndnxr.bambi;

import android.os.Parcel;
import android.os.Parcelable;

public class Email implements Parcelable {
	private String to, from, cc, bcc, subject, message;
	private String[] filePaths = new String[1];

	// For Serializable interface
	//private static final long serialVersionUID = 0L;

	public Email(String to, String from, String cc, String bcc, String subject,
			String message, String[] filePaths) {
		super();
		this.to = to;
		this.from = from;
		this.cc = cc;
		this.bcc = bcc;
		this.subject = subject;
		this.message = message;
		this.filePaths = filePaths;
	}

	public Email(String to, String from, String subject, String message,
			String[] filePaths) {
		super();
		this.to = to;
		this.from = from;
		this.subject = subject;
		this.message = message;
		this.filePaths = filePaths;
	}

	public Email(String to, String from, String subject, String message) {
		super();
		this.to = to;
		this.from = from;
		this.subject = subject;
		this.message = message;
	}

	public Email(Parcel in) {
		to = in.readString();
		from = in.readString();
		cc = in.readString();
		bcc = in.readString();
		subject = in.readString();
		message = in.readString();
		in.readStringArray(filePaths);
	}

	public String getCc() {
		return cc;
	}

	public void setCc(String cc) {
		this.cc = cc;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getBcc() {
		return bcc;
	}

	public void setBcc(String bcc) {
		this.bcc = bcc;
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

	public String[] getFilePaths() {
		return filePaths;
	}

	public void setFilePaths(String[] filePaths) {
		this.filePaths = filePaths;
	}
	
	@Override
	public String toString() {
		return String.format("%s, %s, %s, %s", to, from, subject, message);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(to);
		dest.writeString(from);
		dest.writeString(cc);
		dest.writeString(bcc);
		dest.writeString(subject);
		dest.writeString(message);
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
}
