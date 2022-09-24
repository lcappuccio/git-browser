package com.lcappuccio.gitbrowser;

import com.lcappuccio.gitbrowser.model.Commit;
import com.lcappuccio.gitbrowser.pojo.DomainObjectConverter;
import com.lcappuccio.gitbrowser.pojo.GitLogContainer;
import com.lcappuccio.gitbrowser.services.GitApi;
import com.lcappuccio.gitbrowser.services.GitApiImpl;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Main {

	public static void main(String[] args) throws IOException, GitAPIException {

		Integer commits = 0;
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		// Repository repository = builder.setGitDir(new File("/Users/cappuccio/Documents/Projects/test/.git"))
		Repository repository = builder.setGitDir(new File("./.git"))
				.readEnvironment() // scan environment GIT_* variables
				.findGitDir() // scan up the file system tree
				.build();

		Git gitRepository = new Git(repository);
		GitLogContainer gitLogContainer = new GitLogContainer(gitRepository);
		GitApi gitApi = new GitApiImpl(gitLogContainer);

		List<Commit> revCommits = gitApi.getAllCommits();
		for (Commit commit : revCommits) {
			System.out.println("ID " + commit.getId());
			System.out.println("Date " + DomainObjectConverter.formatCommitDateTime(commit.getDateTime()));
			System.out.println("Author " + commit.getAuthor());
			System.out.println("Message " + commit.getMessage());
			Commit parent = gitApi.getParentOf(commit);
			if (parent != null) {
				System.out.println("Parent " + parent.getId());
				System.out.println("----");
			}
			++commits;
		}

		List<Ref> tagReferenceList = gitRepository.tagList().call();
		for (Ref tagReference : tagReferenceList) {
			System.out.println("Tag " + tagReference.getName());
			System.out.println("Tag commit id " + tagReference.getObjectId());

			// fetch all commits for this tag
			LogCommand tagsGitLog = gitRepository.log();
			Ref peelReference = repository.peel(tagReference);
			if (peelReference.getPeeledObjectId() != null) {
				tagsGitLog.add(peelReference.getPeeledObjectId());
			} else {
				tagsGitLog.add(tagReference.getObjectId());
			}
			// and finally...
			Iterable<RevCommit> logs = tagsGitLog.call();
			for (RevCommit rev : logs) {
				System.out.println("Commit: " + rev /* + ", name: " + rev.getName() + ", id: " + rev.getId().getName()
				 */);
			}
		}

		File workTree = repository.getWorkTree();
		System.out.println(workTree.getAbsolutePath());

		System.out.println("\nCommits " + commits);

		repository.close();
	}
}
