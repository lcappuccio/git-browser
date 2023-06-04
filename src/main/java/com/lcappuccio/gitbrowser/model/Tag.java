package com.lcappuccio.gitbrowser.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
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
