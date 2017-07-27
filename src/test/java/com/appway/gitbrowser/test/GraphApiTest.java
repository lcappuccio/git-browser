package com.appway.gitbrowser.test;

import com.appway.gitbrowser.Application;
import com.appway.gitbrowser.services.GraphApi;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
@TestPropertySource(locations = "classpath:application.properties")
public class GraphApiTest {

	private static final Logger logger = LoggerFactory.getLogger(GraphApiTest.class);
	private static final String DATABASE_FOLDER = "target" + File.separator + "test_database";
	@Autowired
	private GraphApi graphApi;

	@Before
	public void setUp() throws IOException {

		File databaseFolder = new File(DATABASE_FOLDER);
		clearPreviousRun(databaseFolder);
	}

	@Test
	public void should_have_graph_api() {

		assertNotNull(graphApi);
	}

	private void clearPreviousRun(File databaseFile) throws IOException {
		// clear previous test database
		if (databaseFile.exists()) {
			FileUtils.deleteDirectory(databaseFile);
		}
		if (databaseFile.exists()) {
			String message = "Could not delete test database";
			logger.error(message);
			throw new IOException(message);
		}
	}

}
