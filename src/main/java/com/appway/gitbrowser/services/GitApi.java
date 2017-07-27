package com.appway.gitbrowser.services;

import com.appway.gitbrowser.model.Commit;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.IOException;
import java.util.List;

public interface GitApi {

	/**
	 * Get parent of a given commit
	 *
	 * @param commit
	 * @return
	 */
	Commit getParentOf(Commit commit) throws IOException, GitAPIException;

	/**
	 * Return the full commit list
	 *
	 * @return
	 */
	List<Commit> getAllCommits() throws IOException, GitAPIException;

}