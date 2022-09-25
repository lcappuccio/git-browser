package com.lcappuccio.gitbrowser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lcappuccio.gitbrowser.controller.CommitController;
import com.lcappuccio.gitbrowser.model.Commit;
import com.lcappuccio.gitbrowser.services.GraphApi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
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
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {Application.class})
@TestPropertySource(locations = "classpath:application.properties")
class CommitControllerTest {

	private GraphApi graphApi;
	private MockMvc sut;
	private Commit commit1, commit2;

	@BeforeEach
	public void setSut() {
		graphApi = mock(GraphApi.class);
		commit1 = CommitTest.getRandomCommit("TestAuthor", "TestMessage1");
		commit2 = CommitTest.getRandomCommit("TestAuthor", "TestMessage2");
		List<Commit> commitList = Arrays.asList(commit1, commit2);
		Mockito.when(graphApi.findAll()).thenReturn(commitList);

		CommitController restController = new CommitController(graphApi);

		sut = MockMvcBuilders.standaloneSetup(restController).build();
	}

	@Test
	void should_find_all() throws Exception {

		sut.perform(MockMvcRequestBuilders.get("/commit/findall"))
				.andExpect(status().is(HttpStatus.OK.value()));

		verify(graphApi).findAll();
	}

	@Test
	void should_find_by_id() throws Exception {

		String commitId = commit1.getId();

		sut.perform(MockMvcRequestBuilders.get("/commit/findbyid/" + commitId)).andExpect(status().is
				(HttpStatus.OK.value()));

		verify(graphApi).findById(commitId);
	}

	@Test
	void should_find_by_message() throws Exception {


		sut.perform(MockMvcRequestBuilders.get("/commit/findbymessage/TestMessage1")).andExpect(status().is
				(HttpStatus.OK.value()));

		verify(graphApi).findByMessage("TestMessage1");
	}

	@Test
	void should_find_parent_of() throws Exception {

		String jsonStringFromPerson = jsonStringFromPerson(commit1);

		sut.perform(MockMvcRequestBuilders.get("/commit/findparentof").contentType(MediaType.APPLICATION_JSON)
				.content(jsonStringFromPerson.getBytes()))
				.andExpect(status().is(HttpStatus.OK.value()));

		verify(graphApi).findParentOf(commit1);
	}

	private String jsonStringFromPerson(final Commit commit) throws JsonProcessingException {

		ObjectMapper mapper = new ObjectMapper();

		return mapper.writeValueAsString(commit);

	}
}
