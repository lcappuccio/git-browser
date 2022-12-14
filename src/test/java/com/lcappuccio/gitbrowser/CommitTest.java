package com.lcappuccio.gitbrowser;

import com.lcappuccio.gitbrowser.model.Commit;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 15/09/2017 15:07
 */
class CommitTest {

	@Test
	void should_sort_by_date() {

		Commit commit1 = getRandomCommit("TestAuthor", "TestMessage");
        await().atMost(50, TimeUnit.MILLISECONDS);
		Commit commit2 = getRandomCommit("TestAuthor", "TestMessage");

		assertTrue(commit2.compareTo(commit1) > 0);
        assertNotEquals(commit1, commit2);
        assertNotEquals(commit1.hashCode(), commit2.hashCode());
	}

	@Test
	void should_sort_by_id_with_same_date() {

		long systemTime = System.currentTimeMillis();

		Commit commit1 = new Commit("123", systemTime, "TestAuthor", "TestMessage");
		Commit commit2 = new Commit("456", systemTime, "TestAuthor", "TestMessage");

		assertTrue(commit2.compareTo(commit1) > 0);
        assertNotEquals(commit1, commit2);
        assertNotEquals(commit1.hashCode(), commit2.hashCode());
	}

	@Test
	void should_sort_be_equals_with_same_id_and_time() {

		long systemTime = System.currentTimeMillis();

		Commit commit1 = new Commit("123", systemTime, "TestAuthor", "TestMessage");
		Commit commit2 = new Commit("123", systemTime, "OtherAuthor", "OtherMessage");

		assertEquals(0, commit2.compareTo(commit1));
        assertNotEquals(commit1, commit2);
        assertNotEquals(commit1.hashCode(), commit2.hashCode());
	}

	static Commit getRandomCommit(String commitAuthor, String commitMessage) {

		return new Commit(UUID.randomUUID().toString(), System.currentTimeMillis(), commitAuthor, commitMessage);
	}
}
