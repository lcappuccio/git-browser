package com.appway.gitbrowser.services;

import org.eclipse.jgit.revwalk.RevCommit;

import java.util.List;

public interface GraphApi {

	/**
	 * Returns the full commit list
	 *
	 * @return
	 */
	List<RevCommit> findAll();

	/**
	 * Return a commit given the commit id
	 *
	 * @param commitId
	 * @return
	 */
	RevCommit findById(final String commitId);

	/**
	 * Return a commit list given a specific commitMessage
	 *
	 * @param commitMessage
	 * @return
	 */
	List<RevCommit> findCommitsByMessage(String commitMessage);

	/**
	 * Return a commit list that contain the given text in their commit message
	 *
	 * @param textToSearch
	 * @return
	 */
	List<RevCommit> findCommitsThatContainMessage(String textToSearch);

}
