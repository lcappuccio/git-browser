package com.appway.gitbrowser.test;

import com.appway.gitbrowser.Application;
import com.appway.gitbrowser.model.Commit;
import com.appway.gitbrowser.pojo.DomainObjectConverter;
import com.appway.gitbrowser.services.GraphApi;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;

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
		String commitId = "aa288f8cfc9a54354535a2bab28beb6e874e96ec";

		Commit commitById = sut.findById(commitId);

		assertEquals(commitId, commitById.getId());
		assertFalse(commitById.getAuthor().isEmpty());
		assertFalse(commitById.getMessage().isEmpty());
		assertNotEquals(0, commitById.getDateTime());
		assertEquals("code style", commitById.getMessage());
		assertEquals("2017-07-28 10:49:03",
				DomainObjectConverter.formatCommitDateTime(commitById.getDateTime()));
	}

	@Test
	public void should_find_a_commit_list_by_message() {

		String messageToFind = "style";

		List<Commit> commitsByMessage = sut.findCommitsByMessage(messageToFind);

		assertTrue(commitsByMessage.size() == 5);
		assertEquals("aa288f8cfc9a54354535a2bab28beb6e874e96ec", commitsByMessage.get(0).getId());
		assertEquals("365ad45919894c76c1b64536591e6e99d2e846af", commitsByMessage.get(1).getId());
		assertEquals("a2a30ba7ff037c2020fda353bc76fc78531f699d", commitsByMessage.get(2).getId());
		assertEquals("ac59c25a1789b0a0250a1cc8112668bd9d22257a", commitsByMessage.get(3).getId());
		assertEquals("6da65881632b42a49d90dd3afc75f567fb74a058", commitsByMessage.get(4).getId());
	}

	@Test
	public void should_find_a_commit_by_message() {

		String messageToFind = "repo init";

		List<Commit> commitsByMessage = sut.findCommitsByMessage(messageToFind);

		assertTrue(commitsByMessage.size() == 1);
		assertEquals("c62b0e17dbede79ceca1d2b69399f8045692574b", commitsByMessage.get(0).getId());
		assertEquals("repo init", commitsByMessage.get(0).getMessage());
		assertEquals("2017-07-25 16:25:46",
				DomainObjectConverter.formatCommitDateTime(commitsByMessage.get(0).getDateTime()));
	}

	@Test
	public void should_find_all() {

		List<Commit> all = sut.findAll();

		assertTrue(all.size() > 50);
	}
}
