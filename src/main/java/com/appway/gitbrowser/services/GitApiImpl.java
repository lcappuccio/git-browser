package com.appway.gitbrowser.services;

import com.appway.gitbrowser.model.Commit;
import com.appway.gitbrowser.pojo.DomainObjectConverter;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.*;

public class GitApiImpl implements GitApi {

	private final Git git;
	private final Repository repository;

	@Autowired
	public GitApiImpl(Repository repository) {
		this.repository = repository;
		git = new Git(repository);
	}

	public Repository getRepository() {
		return repository;
	}

	@Override
	public Commit getParentOf(Commit commit) throws GitAPIException {

		RevCommit revCommit = DomainObjectConverter.convertFrom(commit, git);

		if (revCommit != null && revCommit.getParents().length > 0) {
			RevCommit parent = revCommit.getParent(0);
			if (parent != null) {
				return DomainObjectConverter.convertFrom(parent);
			}
		}
		return null;
	}

	@Override
	public List<Commit> getAllCommits() throws IOException, GitAPIException {

		List<Commit> commits = new ArrayList<>();
		Iterable<RevCommit> revCommits = git.log().all().call();
		for (RevCommit revCommit: revCommits) {
			commits.add(DomainObjectConverter.convertFrom(revCommit));
		}

		Collections.sort(commits, new Comparator<Commit>() {
			@Override
			public int compare(Commit o1, Commit o2) {
				return o2.getDateTime().compareTo(o1.getDateTime());
			}
		});
		return commits;
	}
}
