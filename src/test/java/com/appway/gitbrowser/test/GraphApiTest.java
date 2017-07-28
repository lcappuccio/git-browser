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

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
	public void should_find_a_commit_by_id() {

		// first commit in simplexdb
		String commitId = "22090d7b8466832c153e93444a0f4292d1b377d9";

		Commit commitById = sut.findById(commitId);

		assertEquals(commitId, commitById.getId());
	}

	@Test
	public void should_find_a_commit_by_message() {

		String messageToFind = "travis";

		List<Commit> commitsByMessage = sut.findCommitsByMessage(messageToFind);

		assertTrue(commitsByMessage.size() == 5);
		assertEquals("73647c69157ac1bdd77001fc3ffb2b6232609bba", commitsByMessage.get(0).getId());
		assertEquals("484e6253cc7a85a532860bccb1f57d0f3ab7ccbd", commitsByMessage.get(1).getId());
		assertEquals("3c0a72207401d4e113eb4caadce3de0c8eee42ed", commitsByMessage.get(2).getId());
		assertEquals("bea5823e343f571bb2373ec0770e5bc9c9675c13", commitsByMessage.get(3).getId());
		assertEquals("49c8f3932694916e99fb7946725e93349cacb941", commitsByMessage.get(4).getId());
	}
}
