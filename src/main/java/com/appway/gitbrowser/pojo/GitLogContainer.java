package com.appway.gitbrowser.pojo;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class GitLogContainer {

	private static final Logger LOGGER = LoggerFactory.getLogger(GitLogContainer.class);
	private final List<RevCommit> revCommitList;

	@Autowired
	public GitLogContainer(Git gitRepository) throws GitAPIException {

		revCommitList = new ArrayList<>();
		Iterable<RevCommit> refCollection = gitRepository.log().call();
		for (RevCommit revCommit: refCollection) {
			revCommitList.add(revCommit);
			LOGGER.info("Added " + revCommit.getId().getName());
		}
		LOGGER.info("Added " + revCommitList.size() + " commits");
	}

	public List<RevCommit> getRevCommitList() {
		return revCommitList;
	}
}
