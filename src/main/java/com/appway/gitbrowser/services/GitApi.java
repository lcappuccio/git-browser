package com.appway.gitbrowser.services;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;

import java.io.IOException;

public interface GitApi {

	/**
	 * Get parent of a given commit
	 *
	 * @param revCommit
	 * @return
	 */
	RevCommit getParentOf(RevCommit revCommit) throws IOException;

	/**
	 * Return the full commit list
	 *
	 * @return
	 */
	Iterable<RevCommit> getAllCommits() throws IOException, GitAPIException;

}
