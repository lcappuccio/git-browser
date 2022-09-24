package com.lcappuccio.gitbrowser.model;

import java.util.Objects;

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
		if (!Objects.equals(id, commit.id)) return false;
		if (!Objects.equals(author, commit.author)) return false;
		return Objects.equals(message, commit.message);
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
