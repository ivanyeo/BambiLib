package com.ndnxr.bambi;

import java.io.Serializable;

public class Email implements Serializable {
	private String to, from, cc, bcc, subject, message;
	private String[] filePaths;

	private static final long serialVersionUID = 0L;

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
}
