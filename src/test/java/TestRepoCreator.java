import com.appway.gitbrowser.Application;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
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

		// add a file
		FileUtils.write(SOME_TEST_FILE, "First line", Charset.defaultCharset());
		git.add().addFilepattern(SOME_TEST_FILE.getAbsolutePath()).call();
		logger.info("Added file " + SOME_TEST_FILE + " to repository at " + repository.getDirectory());
		// and then commit the changes
		git.commit().setMessage("Added testfile").call();
		logger.info("Committed file " + SOME_TEST_FILE + " to repository at " + repository.getDirectory());

		// assert there is a single commit
		Iterable<RevCommit> revCommits = git.log().call();
		int commits = 0;
		for (RevCommit revCommit : revCommits) {
			++commits;
		}
		assertEquals(1, commits);

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

	private void createNewRepositoryFolder() throws IOException {

		boolean isGitTestRepositoryCreated = gitTestRepository.mkdir();
		if (!isGitTestRepositoryCreated) {
			String message = "Could not create git test repository";
			logger.error(message);
			throw new IOException(message);
		}
	}

	private Repository initializeRepository() throws GitAPIException {

		git = Git.init().setDirectory(gitTestRepository).call();
		logger.info("Created a new repository at " + git.getRepository().getDirectory());
		return git.getRepository();
	}

}
