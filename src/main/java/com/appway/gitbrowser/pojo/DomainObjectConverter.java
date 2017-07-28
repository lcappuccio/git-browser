package com.appway.gitbrowser.pojo;

import com.appway.gitbrowser.model.Commit;
import org.eclipse.jgit.revwalk.RevCommit;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DomainObjectConverter {

	public static Commit convertFrom(RevCommit revCommit) {

		String name = revCommit.getAuthorIdent().getName();
		return new Commit(revCommit.getId().getName(), revCommit.getCommitTime(), name, revCommit.getFullMessage());
	}

	public static RevCommit convertFrom(Commit commit, GitLogContainer gitLogContainer) {

		return gitLogContainer.getRevCommit(commit.getId());
	}

	/**
	 * Convert commit date to a standard format
	 *
	 * @return
	 */
	public static String formatCommitDateTime(Date commitDateTime) {

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return simpleDateFormat.format(commitDateTime);
	}
}
