package com.lcappuccio.gitbrowser.services;

import com.lcappuccio.gitbrowser.model.Commit;
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
	List<Commit> findByMessage(String commitMessage);

	/**
	 * Find common parent of two given commits
	 *
	 * @param commit1
	 * @param commit2
	 * @return
	 */
	List<Commit> findCommonParentsOf(Commit commit1, Commit commit2);

	/**
	 * Find parent commit of a given commit
	 *
	 * @param commit
	 * @return
	 */
	Commit findParentOf(Commit commit);

}
