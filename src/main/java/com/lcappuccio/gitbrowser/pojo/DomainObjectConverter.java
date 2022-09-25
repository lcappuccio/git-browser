package com.lcappuccio.gitbrowser.pojo;

import com.lcappuccio.gitbrowser.model.Commit;
import org.eclipse.jgit.revwalk.RevCommit;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class DomainObjectConverter {

    private DomainObjectConverter() {}

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
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		return simpleDateFormat.format(commitDateTime);
	}
}
