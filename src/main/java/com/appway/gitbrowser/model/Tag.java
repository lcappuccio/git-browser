package com.appway.gitbrowser.model;

import org.eclipse.jgit.lib.Ref;

public class Tag {

	private String id;
	private String name;

	public Tag() {
	}

	public Tag(Ref reference) {
		this.id = reference.getObjectId().toString();
		this.name = reference.getName();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
