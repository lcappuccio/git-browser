package com.appway.gitbrowser.controller;

import com.appway.gitbrowser.model.Commit;
import com.appway.gitbrowser.services.GraphApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * @author leo 16/09/2017 11:02
 */
@Controller
@RequestMapping(value = "commit")
public class CommitController {

	private final GraphApi graphApi;

	@Autowired
	public CommitController(GraphApi graphApi) {
		this.graphApi = graphApi;
	}

	@RequestMapping(value = "findall",method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Commit>> findAll() {

		List<Commit> allCommits = graphApi.findAll();
		return new ResponseEntity<>(allCommits, HttpStatus.OK);
	}

	@RequestMapping(value = "findbyid/{id}",method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Commit> findById(@PathVariable(value = "id") final String commitId) {

		Commit commit = graphApi.findById(commitId);
		return new ResponseEntity<>(commit, HttpStatus.OK);
	}

	@RequestMapping(value = "findbymessage/{message}",method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Commit>> findByMessage(@PathVariable("message") final String commitMessage) {

		List<Commit> allCommits = graphApi.findByMessage(commitMessage);
		return new ResponseEntity<>(allCommits, HttpStatus.OK);
	}
}
