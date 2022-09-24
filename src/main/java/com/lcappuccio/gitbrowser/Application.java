package com.lcappuccio.gitbrowser;

import com.lcappuccio.gitbrowser.pojo.GitLogContainer;
import com.lcappuccio.gitbrowser.services.GitApi;
import com.lcappuccio.gitbrowser.services.GitApiImpl;
import com.lcappuccio.gitbrowser.services.GraphApi;
import com.lcappuccio.gitbrowser.services.GraphApiImpl;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.io.File;
import java.util.Collections;

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
	public GraphApi graphApi() throws GitAPIException {
		return new GraphApiImpl(databaseFolder, gitApi());
	}

	@Bean
	public Git getGitRepository() throws GitAPIException {
		return Git.init().setDirectory(new File(repositoryFolder)).call();
	}

	@Bean
	public Docket restfulApi() {
		return new Docket(DocumentationType.SWAGGER_2)
				.groupName("restful-api").select().build().apiInfo(apiInfo());
	}

	private ApiInfo apiInfo() {
		return new ApiInfo(
				"GitBrowser",
				"Exposing a Git repo through REST API",
				null,
				null,
				new Contact("Leonardo Cappuccio", null, null), null, null, Collections.emptyList());
	}
}
