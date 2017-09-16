package com.appway.gitbrowser.model;

public class Commit implements Comparable<Commit> {

	private String id;
	private long dateTime;
	private String author;
	private String message;

	public Commit() {}

	public Commit(String commitId, long commitEpoch, String commitAuthor, String commitMessage) {
		this.id = commitId;
		this.dateTime = commitEpoch;
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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Commit commit = (Commit) o;

		if (dateTime != commit.dateTime) return false;
		if (id != null ? !id.equals(commit.id) : commit.id != null) return false;
		if (author != null ? !author.equals(commit.author) : commit.author != null) return false;
		return message != null ? message.equals(commit.message) : commit.message == null;
	}

	@Override
	public int hashCode() {
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + (int) (dateTime ^ (dateTime >>> 32));
		result = 31 * result + (author != null ? author.hashCode() : 0);
		result = 31 * result + (message != null ? message.hashCode() : 0);
		return result;
	}

	@Override
	public int compareTo(Commit commit) {
		if (dateTime != commit.getDateTime()) {
			return Long.compare(dateTime, commit.getDateTime());
		} else {
			return id.compareTo(commit.getId());
		}
	}

}
