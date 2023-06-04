package com.lcappuccio.gitbrowser.model;

import lombok.Getter;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;

@Getter
public class Tag {

	private String id;
	private String name;

	public Tag(Ref reference) {
        ObjectId objectId = reference.getObjectId();
        if (objectId!= null) {
            this.id = objectId.toString();
        }
		this.name = reference.getName();
	}
}
