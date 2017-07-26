import com.appway.gitbrowser.Application;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
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

import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
@TestPropertySource(locations = "classpath:application.properties")
public class TestRepoCreator {

	private static final Logger logger = LoggerFactory.getLogger(TestRepoCreator.class);
	private static final File gitTestRepository = new File("target/gittestrepository");
	private Repository repository;

	public Repository getRepository() {
		return repository;
	}

	@Before
	public void setUp() throws IOException, GitAPIException {

		// Clear previous test repository
		if (gitTestRepository.exists()) {
			FileUtils.deleteDirectory(gitTestRepository);
		}
		if (gitTestRepository.exists()) {
			String message = "Could not delete git test repository";
			logger.error(message);
			throw new IOException(message);
		}

		// run the init-call
		boolean isGitTestRepositoryCreated = gitTestRepository.mkdir();
		if (!isGitTestRepositoryCreated) {
			String message = "Could not create git test repository";
			logger.error(message);
			throw new IOException(message);
		}

		// The Git-object has a static method to initialize a new repository
		try (Git git = Git.init().setDirectory(gitTestRepository).call()) {
			logger.info("Created a new repository at " + git.getRepository().getDirectory());
			repository = git.getRepository();
		}
	}

	@Test
	public void should_have_a_repository() {
		assertNotNull(repository );
	}
}
