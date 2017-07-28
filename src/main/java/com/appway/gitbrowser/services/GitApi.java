package com.appway.gitbrowser.services;

import com.appway.gitbrowser.model.Commit;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public interface GitApi {

	/**
	 * Get parent of a given commit
	 *
	 * @param commit
	 * @return
	 */
	Commit getParentOf(Commit commit);

	/**
	 * Return the full commit list
	 *
	 * @return
	 */
	List<Commit> getAllCommits() throws GitAPIException, IOException;

}
