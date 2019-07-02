package com.script.mail.common;

public class Template {

	private String subject;
	private String toUser;
	private String ccUser;
	private String sccUser;
	private String content;
	
	private String[] attachs;
	
	public String[] getAttachs() {
		return attachs;
	}
	public void setAttachs(String[] attachs) {
		this.attachs = attachs;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getToUser() {
		return toUser;
	}
	public void setToUser(String toUser) {
		this.toUser = toUser;
	}
	public String getCcUser() {
		return ccUser;
	}
	public void setCcUser(String ccUser) {
		this.ccUser = ccUser;
	}
	public String getSccUser() {
		return sccUser;
	}
	public void setSccUser(String sccUser) {
		this.sccUser = sccUser;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	
}
