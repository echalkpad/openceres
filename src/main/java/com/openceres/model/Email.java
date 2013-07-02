package com.openceres.model;

public class Email extends BaseModel{
	/**
	 * Generated Serial Version 
	 */
	private static final long serialVersionUID = 8669001228428840267L;
	
	String recipients;
	String sender;
	String subject;
	String contents;
	
	public String getRecipients() {
		return recipients;
	}
	public void setRecipients(String recipients) {
		this.recipients = recipients;
	}
	public String getSender() {
		return sender;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getContents() {
		return contents;
	}
	public void setContents(String contents) {
		this.contents = contents;
	}
	
	
}
