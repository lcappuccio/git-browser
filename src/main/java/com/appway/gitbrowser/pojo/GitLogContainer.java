package com.appway.gitbrowser.pojo;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;

public class GitLogContainer {

	private static final Logger LOGGER = LoggerFactory.getLogger(GitLogContainer.class);
	private final HashMap<String, RevCommit> revCommitList;

	@Autowired
	public GitLogContainer(Git gitRepository) throws GitAPIException {

		revCommitList = new HashMap<>();
		Iterable<RevCommit> refCollection = gitRepository.log().call();
		for (RevCommit revCommit: refCollection) {
			revCommitList.put(revCommit.getId().getName(), revCommit);
			LOGGER.info("Added " + revCommit.getId().getName());
		}
		LOGGER.info("Added " + revCommitList.size() + " commits");
	}

	public RevCommit getRevCommit(final String commitId) {
		return revCommitList.get(commitId);
	}
}
