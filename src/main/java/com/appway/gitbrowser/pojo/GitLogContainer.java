package com.appway.gitbrowser.pojo;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class GitLogContainer {

	private final List<RevCommit> revCommitList;

	@Autowired
	public GitLogContainer(Git gitRepository) throws GitAPIException {

		revCommitList = new ArrayList<>();
		Iterable<RevCommit> refCollection = gitRepository.log().call();
		for (RevCommit revCommit: refCollection) {
			revCommitList.add(revCommit);
		}
	}

	public List<RevCommit> getRevCommitList() {
		return revCommitList;
	}
}
