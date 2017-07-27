package com.appway.gitbrowser.model;

import java.util.Date;

public class Commit {

	private String id;
	private Date dateTime;
	private String author;
	private String message;

	public Commit() {
	}

	public Commit(String commitId, int commitEpoch, String commitAuthor, String commitMessage) {
		this.id = commitId;
		this.dateTime = new Date(commitEpoch * 1000L);
		this.author = commitAuthor;
		this.message = commitMessage;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getDateTime() {
		return dateTime;
	}

	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
