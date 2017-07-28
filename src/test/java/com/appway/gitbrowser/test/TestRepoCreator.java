package com.appway.gitbrowser.test;

import com.appway.gitbrowser.Application;
import com.appway.gitbrowser.model.Commit;
import com.appway.gitbrowser.model.Tag;
import com.appway.gitbrowser.pojo.DomainObjectConverter;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
@TestPropertySource(locations = "classpath:application.properties")
public class TestRepoCreator {

	private static final Logger logger = LoggerFactory.getLogger(TestRepoCreator.class);

	private static final File gitTestRepository = new File("target/gittestrepository");
	private static final File SOME_TEST_FILE = new File("target/gittestrepository/Test.txt");

	private Git git;
	private Repository repository;

	public Repository getRepository() {
		return repository;
	}

	@Before
	public void setUp() throws IOException, GitAPIException {

		clearPreviousRun();
		createNewRepositoryFolder();
		repository = initializeRepository();

	}

	@After
	public void tearDown() {

		git.close();
		repository.close();
	}

	@Test
	public void should_have_a_repository() {
		assertNotNull(repository);
	}

	@Test
	public void should_make_a_commit() throws IOException, GitAPIException {

		commitFile();

		assertEquals(1, getCommitCount());
	}

	@Test
	public void should_create_domain_object() throws IOException, GitAPIException, ParseException {

		commitFile();
		Iterable<RevCommit> revCommits = git.log().call();
		Commit commit = DomainObjectConverter.convertFrom(revCommits.iterator().next());

		assertEquals("Added testfile", commit.getMessage());
		assertEquals("TestCommitter", commit.getAuthor());
		assertTrue(commit.getDateTime() < System.currentTimeMillis());
	}

	@Test
	public void should_tag() throws GitAPIException, IOException {

		commitFile();
		createTag();

		List<Ref> tagList = git.tagList().call();
		Tag tag = new Tag(tagList.get(0));

		assertEquals("refs/tags/0.1", tag.getName());
		assertNotNull(tag.getId());

	}

	private void clearPreviousRun() throws IOException {
		// clear previous test repository
		if (gitTestRepository.exists()) {
			FileUtils.deleteDirectory(gitTestRepository);
		}
		if (gitTestRepository.exists()) {
			String message = "Could not delete git test repository";
			logger.error(message);
			throw new IOException(message);
		}
	}

	private void commitFile() throws IOException, GitAPIException {

		FileUtils.write(SOME_TEST_FILE, "First line", Charset.defaultCharset());
		git.add().addFilepattern(SOME_TEST_FILE.getAbsolutePath()).call();
		logger.info("Added file " + SOME_TEST_FILE + " to repository at " + repository.getDirectory());

		git.commit().setMessage("Added testfile").setAuthor("TestCommitter", "testcommitter@somemail")
				.call();
		logger.info("Committed file " + SOME_TEST_FILE + " to repository at " + repository.getDirectory());
	}

	private void createNewRepositoryFolder() throws IOException {

		boolean isGitTestRepositoryCreated = gitTestRepository.mkdir();
		if (!isGitTestRepositoryCreated) {
			String message = "Could not create git test repository";
			logger.error(message);
			throw new IOException(message);
		}
	}

	private void createTag() throws GitAPIException {

		String versionTag = "0.1";
		git.tag().setName(versionTag).call();

		logger.info("Tagged " + versionTag);
	}

	private int getCommitCount() throws GitAPIException {

		int commits = 0;
		Iterable<RevCommit> revCommits = git.log().call();
		for (RevCommit revCommit : revCommits) {
			++commits;
		}
		return commits;
	}

	private Repository initializeRepository() throws GitAPIException {

		git = Git.init().setDirectory(gitTestRepository).call();
		logger.info("Created a new repository at " + git.getRepository().getDirectory());
		return git.getRepository();
	}

}
