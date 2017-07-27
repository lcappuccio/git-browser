package com.appway.gitbrowser.services;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

import java.io.IOException;

public class GitApiImpl implements GitApi {

	private final Repository repository;
	private final Git git;

	public GitApiImpl(Repository repository) {
		this.repository = repository;
		git = new Git(repository);
	}

	@Override
	public RevCommit getParentOf(RevCommit revCommit) throws IOException {

		if (revCommit.getParents().length > 0) {
			RevCommit parent = revCommit.getParent(0);
			if (parent != null) {
				return parent;
			}
		}
		return null;
	}

	@Override
	public Iterable<RevCommit> getAllCommits() throws IOException, GitAPIException {
		return git.log().all().call();
	}
}
