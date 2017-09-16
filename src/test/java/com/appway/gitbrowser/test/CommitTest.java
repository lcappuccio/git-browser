package com.appway.gitbrowser.test;

import com.appway.gitbrowser.model.Commit;
import org.junit.Test;

import java.util.UUID;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

/**
 * 15/09/2017 15:07
 */
public class CommitTest {

	@Test
	public void should_sort_by_date() throws InterruptedException {

		Commit commit1 = getRandomCommit("TestAuthor", "TestMessage");
		Thread.sleep(50);
		Commit commit2 = getRandomCommit("TestAuthor", "TestMessage");

		assertTrue(commit2.compareTo(commit1) > 0);
	}

	@Test
	public void should_sort_by_id_with_same_date() {

		long systemTime = System.currentTimeMillis();

		Commit commit1 = new Commit("123", systemTime, "TestAuthor", "TestMessage");
		Commit commit2 = new Commit("456", systemTime, "TestAuthor", "TestMessage");

		assertTrue(commit2.compareTo(commit1) > 0);
	}

	@Test
	public void should_sort_be_equals_with_same_id_and_time() {

		long systemTime = System.currentTimeMillis();

		Commit commit1 = new Commit("123", systemTime, "TestAuthor", "TestMessage");
		Commit commit2 = new Commit("123", systemTime, "OtherAuthor", "OtherMessage");

		assertEquals(0, commit2.compareTo(commit1));
	}

	public static Commit getRandomCommit(String commitAuthor, String commitMessage) {

		return new Commit(UUID.randomUUID().toString(), System.currentTimeMillis(), commitAuthor, commitMessage);
	}
}
