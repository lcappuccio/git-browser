package com.lcappuccio.gitbrowser.pojo;

import com.lcappuccio.gitbrowser.model.Commit;
import org.eclipse.jgit.revwalk.RevCommit;

import java.text.SimpleDateFormat;

public class DomainObjectConverter {

	/**
	 * Convert JGit commit object to our domain
	 *
	 * @param revCommit
	 * @return
	 */
	public static Commit convertFrom(RevCommit revCommit) {

		String authorName = revCommit.getAuthorIdent().getName();
		String message = revCommit.getFullMessage().trim();
		return new Commit(revCommit.getId().getName(), revCommit.getCommitTime() * 1000L, authorName, message);
	}

	public static RevCommit convertFrom(Commit commit, GitLogContainer gitLogContainer) {

		return gitLogContainer.getRevCommit(commit.getId());
	}

	/**
	 * Convert commit date to a standard format
	 *
	 * @param commitDateTime
	 * @return
	 */
	public static String formatCommitDateTime(long commitDateTime) {

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return simpleDateFormat.format(commitDateTime);
	}
}
