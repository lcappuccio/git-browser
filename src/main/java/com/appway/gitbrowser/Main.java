package com.appway.gitbrowser;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.io.IOException;

public class Main {

	public static void main(String[] args) throws IOException {

		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		Repository repository = builder.setGitDir(new File("/Users/cappuccio/Documents/Projects/simplexdb/.git"))
				.readEnvironment() // scan environment GIT_* variables
				.findGitDir() // scan up the file system tree
				.build();
		
		for(String key: repository.getTags().keySet()) {

			System.out.println(repository.getTags().get(key).getName());

		}
	}
}
