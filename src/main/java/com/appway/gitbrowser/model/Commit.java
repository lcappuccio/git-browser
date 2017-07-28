package com.appway.gitbrowser.model;

public class Commit {

	private String id;
	private long dateTime;
	private String author;
	private String message;

	public Commit() {
	}

	public Commit(String commitId, long commitEpoch, String commitAuthor, String commitMessage) {
		this.id = commitId;
		this.dateTime = commitEpoch * 1000L;
		this.author = commitAuthor;
		this.message = commitMessage;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public long getDateTime() {
		return dateTime;
	}

	public void setDateTime(long dateTime) {
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
