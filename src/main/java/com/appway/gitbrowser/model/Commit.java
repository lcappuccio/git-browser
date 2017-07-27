package com.appway.gitbrowser.model;

import org.eclipse.jgit.revwalk.RevCommit;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Commit {

	private String id;
	private Date dateTime;
	private String author;
	private String message;

	public Commit() {
	}

	public Commit(RevCommit revCommit) {
		this.id = revCommit.getId().toString();
		this.dateTime = new Date(revCommit.getCommitTime() * 1000L);
		this.author = revCommit.getAuthorIdent().getName();
		this.message = revCommit.getFullMessage().trim();
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

	/**
	 * Convert commit date to a standard format
	 *
	 * @return
	 * @throws ParseException
	 */
	public String formatCommitDateTime() throws ParseException {

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return simpleDateFormat.format(dateTime);
	}
}
