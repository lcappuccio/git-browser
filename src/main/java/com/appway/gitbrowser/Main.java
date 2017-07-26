package com.appway.gitbrowser;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Main {

	public static void main(String[] args) throws IOException, GitAPIException, ParseException {

		Integer commits = 0;
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		Repository repository = builder.setGitDir(new File("/Users/cappuccio/Documents/Projects/simplexdb/.git"))
				.readEnvironment() // scan environment GIT_* variables
				.findGitDir() // scan up the file system tree
				.build();
		
		Git gitRepository = new Git(repository);
		Iterable<RevCommit> revCommits = gitRepository.log().all().call();
		for (RevCommit revCommit: revCommits) {
			System.out.println("ID " + revCommit.getId());
			System.out.println("Date " + getCommitDate(revCommit.getCommitTime()));
			System.out.println("Author " + revCommit.getAuthorIdent().getName());
			System.out.println("Message " + revCommit.getFullMessage().trim());
			if (revCommit.getParents().length > 0) {
				RevCommit parent = revCommit.getParent(0);
				if (parent != null) {
					System.out.println("Parent " + parent.getId());
					System.out.println("----");
				}
			}
			++commits;
		}

		for (String key : repository.getTags().keySet()) {
			System.out.println(repository.getTags().get(key).getName());
		}

		File workTree = repository.getWorkTree();
		System.out.println(workTree.getAbsolutePath());

		System.out.println("\nCommits " + commits);

		repository.close();
	}

	private static String getCommitDate(int commitTime) throws ParseException {

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return simpleDateFormat.format(new Date(commitTime * 1000L));
	}
}
