package com.lcappuccio.gitbrowser.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class Commit implements Comparable<Commit> {

	private String id;
	private long dateTime;
	private String author;
	private String message;

	public Commit(String commitId, long commitEpoch, String commitAuthor, String commitMessage) {
		this.id = commitId;
		this.dateTime = commitEpoch;
		this.author = commitAuthor;
		this.message = commitMessage;
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
