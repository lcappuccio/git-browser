package com.lcappuccio.gitbrowser.services;

import com.lcappuccio.gitbrowser.model.Commit;
import org.springframework.stereotype.Service;

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
	List<Commit> getAllCommits();

}
