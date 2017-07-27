package com.appway.gitbrowser;

import com.appway.gitbrowser.services.GraphApi;
import com.appway.gitbrowser.services.GraphApiImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

	@Value("${database.folder}")
	private String databaseFolder;

	public static void main(String[] args) {

		SpringApplication.run(Application.class, args);

	}

	@Bean
	public GraphApi databaseApi() {
		return new GraphApiImpl(databaseFolder);
	}
}
