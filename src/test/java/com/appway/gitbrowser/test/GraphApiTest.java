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

		List<Commit> commitsByMessage = sut.findByMessage(messageToFind);

		assertTrue(commitsByMessage.size() > 6);
		int commitsByMessageSize = commitsByMessage.size();
		assertEquals("6da65881632b42a49d90dd3afc75f567fb74a058", commitsByMessage.get(0).getId());
		assertEquals("ac59c25a1789b0a0250a1cc8112668bd9d22257a", commitsByMessage.get(1).getId());
		assertEquals("a2a30ba7ff037c2020fda353bc76fc78531f699d", commitsByMessage.get(2).getId());
	}

	@Test
	public void should_find_a_commit_by_message() {

		String messageToFind = "repo init";

		List<Commit> commitsByMessage = sut.findByMessage(messageToFind);

		assertEquals(1, commitsByMessage.size());
		assertEquals("c62b0e17dbede79ceca1d2b69399f8045692574b", commitsByMessage.get(0).getId());
		assertEquals("repo init", commitsByMessage.get(0).getMessage());
		assertEquals("2017-07-25 16:25:46",
				DomainObjectConverter.formatCommitDateTime(commitsByMessage.get(0).getDateTime()));
	}

	@Test
	public void should_have_parent() {

		String commitId = "2e93bd8b06b8f51feb86eb6b5a55363e3c99fa17";
		Commit childCommit = sut.findById(commitId);

		Commit parentCommit = sut.findParentOf(childCommit);

		assertNotNull(parentCommit);
		assertEquals("c62b0e17dbede79ceca1d2b69399f8045692574b", parentCommit.getId());
		assertEquals("repo init", parentCommit.getMessage());
	}

	@Test
	public void should_have_parent_not_in_branch() {

		String commitId = "c36e5f04e5a9b5d466097d0afa8aa14a2aab532b";
		Commit childCommit = sut.findById(commitId);

		Commit parentCommit = sut.findParentOf(childCommit);

		assertNotNull(parentCommit);
		assertEquals("dd7e3a8c7494a09e4f8f5d568aaf1f402f4775c5", parentCommit.getId());
		assertEquals("fix test", parentCommit.getMessage());
	}

	@Test
	public void should_have_no_parent() {

		String commitId = "c62b0e17dbede79ceca1d2b69399f8045692574b";
		Commit rootCommit = sut.findById(commitId);

		Commit parentCommit = sut.findParentOf(rootCommit);

		assertNull(parentCommit);
	}

	@Test
	public void should_have_same_parent() {

		String mergeCommitId = "62a458c96ca8c94d2c9d602a183292254e9f81ca";
		Commit mergeCommit = sut.findById(mergeCommitId);

		String branchCommitId = "26ba320111350d5e83d9a0bd8b197bb0db5e431a";
		Commit branchCommit = sut.findById(branchCommitId);

		Commit parentCommit = sut.findParentOf(mergeCommit);
		Commit otherParentCommit = sut.findParentOf(branchCommit);

		assertEquals("77e4000472c0d2ff2848aed82ac2c8b563ccfa10", parentCommit.getId());
		assertEquals(parentCommit, otherParentCommit);
	}

	@Test
	public void should_find_all() {

		List<Commit> all = sut.findAll();

		assertTrue(all.size() > 100);
	}

	@Test
	public void should_find_all_parents() {

		Commit commit1 = sut.findById("9ad0dcf070c93300ac90a120c6fbac281c70f2c9");
		Commit commit2 = sut.findById("c0095c91119c94da4a0a2080855ec2e56c64bd0c");
		sut.findCommonParentsOf(commit1, commit2);
	}
}
