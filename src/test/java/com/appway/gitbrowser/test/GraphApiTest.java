package com.appway.gitbrowser.test;

import com.appway.gitbrowser.Application;
import com.appway.gitbrowser.model.Commit;
import com.appway.gitbrowser.services.GraphApi;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
@TestPropertySource(locations = "classpath:application.properties")
public class GraphApiTest {

	@Autowired
	private GraphApi sut;

	@Test
	public void should_have_graph_api() {

		assertNotNull(sut);
	}

	@Test
	public void should_find_a_commit() {

		// first commit in simplexdb
		String commitId = "22090d7b8466832c153e93444a0f4292d1b377d9";

		Commit commitById = sut.findById(commitId);

		assertEquals(commitId, commitById.getId());
	}
}
