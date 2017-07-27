package com.appway.gitbrowser;

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

@SpringBootApplication
public class Application {

	@Value("${database.folder}")
	private String databaseFolder;
	@Value("${repository.folder}")
	private String repositoryFolder;
	private Git gitRepository;

	public static void main(String[] args) {

		SpringApplication.run(Application.class, args);

	}

	@Bean
	public GitApi gitApi() throws GitAPIException {
		gitRepository = Git.init().setDirectory(new File(repositoryFolder)).call();
		return new GitApiImpl(gitRepository.getRepository());
	}

	@Bean
	public GraphApi graphApi() {
		return new GraphApiImpl(databaseFolder);
	}
}
