package com.appway.gitbrowser.test;

import com.appway.gitbrowser.Application;
import com.appway.gitbrowser.controller.CommitController;
import com.appway.gitbrowser.model.Commit;
import com.appway.gitbrowser.services.GraphApi;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author leo 16/09/2017 11:06
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
@TestPropertySource(locations = "classpath:application.properties")
public class CommitControllerTest {

	private GraphApi graphApi;
	private MockMvc sut;

	@Before
	public void setSut() {
		graphApi = mock(GraphApi.class);
		Commit commit1 = CommitTest.getRandomCommit("TestAuthor", "TestMessage1");
		Commit commit2 = CommitTest.getRandomCommit("TestAuthor", "TestMessage2");
		List<Commit> commitList = Arrays.asList(commit1, commit2);
		Mockito.when(graphApi.findAll()).thenReturn(commitList);

		CommitController restController = new CommitController(graphApi);

		sut = MockMvcBuilders.standaloneSetup(restController).build();
	}

	@Test
	public void should_find_all() throws Exception {

		ResultActions resultActions = sut.perform(MockMvcRequestBuilders.get("/commit/find"))
				.andExpect(status().is(HttpStatus.OK.value()));

		verify(graphApi).findAll();
	}
}
