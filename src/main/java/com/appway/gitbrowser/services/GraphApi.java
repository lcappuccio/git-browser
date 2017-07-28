package com.appway.gitbrowser.services;

import com.appway.gitbrowser.model.Commit;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
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

}
