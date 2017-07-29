package com.appway.gitbrowser.services;

import com.appway.gitbrowser.model.Commit;
import com.appway.gitbrowser.pojo.DomainObjectConverter;
import com.appway.gitbrowser.pojo.GitLogContainer;
import org.eclipse.jgit.revwalk.RevCommit;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
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

		commits.sort((o1, o2) -> {
			if (o2.getDateTime() < o1.getDateTime()) {
				return -1;
			} else {
				return 1;
			}
		});
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
