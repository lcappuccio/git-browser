package com.lcappuccio.gitbrowser.services;

import com.lcappuccio.gitbrowser.model.Commit;
import com.lcappuccio.gitbrowser.pojo.DomainObjectConverter;
import com.lcappuccio.gitbrowser.pojo.GitLogContainer;
import org.eclipse.jgit.revwalk.RevCommit;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class GitApiImpl implements GitApi {

	private final GitLogContainer gitLogContainer;

	@Autowired
	public GitApiImpl(GitLogContainer gitLogContainer) {
		this.gitLogContainer = gitLogContainer;
	}

	@Override
	public List<Commit> getAllCommits() {

		List<Commit> commits = new ArrayList<>();
		Set<String> commitIds = gitLogContainer.getCommitIds();
		for (String commitId : commitIds) {
			RevCommit revCommit = gitLogContainer.getRevCommit(commitId);
			commits.add(DomainObjectConverter.convertFrom(revCommit));
		}

		Collections.sort(commits);
		return commits;
	}

	@Override
	public Commit getParentOf(Commit commit) {

		RevCommit revCommit = DomainObjectConverter.convertFrom(commit, gitLogContainer);

		if (revCommit != null && revCommit.getParents().length > 0) {
			RevCommit parent = revCommit.getParent(0);
			if (parent != null) {
				return DomainObjectConverter.convertFrom(parent);
			}
		}
		return null;
	}
}
