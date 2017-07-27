package com.appway.gitbrowser.services;

import com.appway.gitbrowser.model.Commit;

import java.util.List;

public interface GraphApi {

	/**
	 * Returns the full commit list
	 *
	 * @return
	 */
	List<Commit> findAll();

	/**
	 * Return a commit given the commit id
	 *
	 * @param commitId
	 * @return
	 */
	Commit findById(final String commitId);

	/**
	 * Return a commit list given a specific commitMessage
	 *
	 * @param commitMessage
	 * @return
	 */
	List<Commit> findCommitsByMessage(String commitMessage);

	/**
	 * Return a commit list that contain the given text in their commit message
	 *
	 * @param textToSearch
	 * @return
	 */
	List<Commit> findCommitsThatContainMessage(String textToSearch);

}
