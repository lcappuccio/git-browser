package com.appway.gitbrowser;

import com.appway.gitbrowser.services.GitApi;
import com.appway.gitbrowser.services.GitApiImpl;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Main {

	public static void main(String[] args) throws IOException, GitAPIException, ParseException {

		Integer commits = 0;
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		// 		Repository repository = builder.setGitDir(new File("/Users/cappuccio/Documents/Projects_Appway/appway/.git"))
		Repository repository = builder.setGitDir(new File("/Users/cappuccio/Documents/Projects/simplexdb/.git"))
				.readEnvironment() // scan environment GIT_* variables
				.findGitDir() // scan up the file system tree
				.build();

		GitApi gitApi = new GitApiImpl(repository);

		Git gitRepository = new Git(repository);
		Iterable<RevCommit> revCommits = gitApi.getAllCommits();
		for (RevCommit revCommit : revCommits) {
			System.out.println("ID " + revCommit.getId());
			System.out.println("Date " + getCommitDate(revCommit.getCommitTime()));
			System.out.println("Author " + revCommit.getAuthorIdent().getName());
			System.out.println("Message " + revCommit.getFullMessage().trim());
			RevCommit parent = gitApi.getParentOf(revCommit);
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
				System.out.println("Commit: " + rev /* + ", name: " + rev.getName() + ", id: " + rev.getId().getName() */);
			}
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
