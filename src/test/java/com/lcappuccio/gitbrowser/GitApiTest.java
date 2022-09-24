package com.lcappuccio.gitbrowser;

import com.lcappuccio.gitbrowser.model.Commit;
import com.lcappuccio.gitbrowser.services.GitApi;
import com.lcappuccio.gitbrowser.services.GraphApi;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {Application.class})
@TestPropertySource(locations = "classpath:application.properties")
class GitApiTest {

	@Autowired
	private GitApi sut;
	@Autowired
	private GraphApi graphApi;

	@Test
	void should_get_all() {

		List<Commit> allCommits = sut.getAllCommits();

		assertTrue(allCommits.size() > 100);
	}

	@Test
	void should_get_parent() {

		String commitId = "2e93bd8b06b8f51feb86eb6b5a55363e3c99fa17";
		Commit childCommit = graphApi.findById(commitId);

		Commit parentCommit = sut.getParentOf(childCommit);

		assertNotNull(parentCommit);
		assertEquals("c62b0e17dbede79ceca1d2b69399f8045692574b", parentCommit.getId());
		assertEquals("repo init", parentCommit.getMessage());
	}

	@Test
	void should_get_parent_as_first_element() {

		String commitId = "c62b0e17dbede79ceca1d2b69399f8045692574b";
		Commit parentCommit = graphApi.findAll().get(0);
		
		assertNotNull(parentCommit);
		assertEquals("c62b0e17dbede79ceca1d2b69399f8045692574b", parentCommit.getId());
		assertEquals("repo init", parentCommit.getMessage());
	}

	@Test
	void should_get_no_parent() {

		String commitId = "c62b0e17dbede79ceca1d2b69399f8045692574b";
		Commit rootCommit = graphApi.findById(commitId);

		Commit parentCommit = sut.getParentOf(rootCommit);

		assertNull(parentCommit);
	}

	@Test
	void should_get_same_parent() {

		String mergeCommitId = "62a458c96ca8c94d2c9d602a183292254e9f81ca";
		Commit mergeCommit = graphApi.findById(mergeCommitId);

		String branchCommitId = "26ba320111350d5e83d9a0bd8b197bb0db5e431a";
		Commit branchCommit = graphApi.findById(branchCommitId);

		Commit parentCommit = sut.getParentOf(mergeCommit);
		Commit otherParentCommit = sut.getParentOf(branchCommit);

		assertEquals("77e4000472c0d2ff2848aed82ac2c8b563ccfa10", parentCommit.getId());
		assertEquals(parentCommit, otherParentCommit);
	}
}
