package com.lcappuccio.gitbrowser.pojo;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Set;

public class GitLogContainer {

	private static final Logger LOGGER = LoggerFactory.getLogger(GitLogContainer.class);
	private final HashMap<String, RevCommit> revCommitMap;

	@Autowired
	public GitLogContainer(Git gitRepository) throws GitAPIException {

		revCommitMap = new HashMap<>();
		Iterable<RevCommit> refCollection = gitRepository.log().call();
		for (RevCommit revCommit : refCollection) {
			String revCommitId = revCommit.getId().getName();
			if (revCommitMap.containsKey(revCommitId)) {
				throw new IllegalArgumentException("Duplicate commit id");
			} else {
				revCommitMap.put(revCommitId, revCommit);
			}
		}
		LOGGER.info("Loaded repository with {} commits", revCommitMap.size());
	}

	public RevCommit getRevCommit(final String commitId) {
		return revCommitMap.get(commitId);
	}

	public Set<String> getCommitIds() {
		return revCommitMap.keySet();
	}
}
