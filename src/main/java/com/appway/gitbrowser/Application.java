package com.appway.gitbrowser;

import com.appway.gitbrowser.pojo.GitLogContainer;
import com.appway.gitbrowser.services.GitApi;
import com.appway.gitbrowser.services.GitApiImpl;
import com.appway.gitbrowser.services.GraphApi;
import com.appway.gitbrowser.services.GraphApiImpl;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.File;
import java.io.IOException;

@SpringBootApplication
public class Application {

	@Value("${database.folder}")
	private String databaseFolder;
	@Value("${repository.folder}")
	private String repositoryFolder;

	public static void main(String[] args) {

		SpringApplication.run(Application.class, args);
	}

	@Bean
	public GitLogContainer gitLogContainer() throws GitAPIException {
		return new GitLogContainer(getGitRepository());
	}

	@Bean
	public GitApi gitApi() throws GitAPIException {
		return new GitApiImpl(gitLogContainer());
	}

	@Bean
	public GraphApi graphApi() throws GitAPIException, IOException {
		return new GraphApiImpl(databaseFolder, gitApi());
	}

	@Bean
	public Git getGitRepository() throws GitAPIException {
		return Git.init().setDirectory(new File(repositoryFolder)).call();
	}
}
