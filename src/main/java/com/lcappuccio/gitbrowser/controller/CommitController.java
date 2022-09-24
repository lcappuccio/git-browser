package com.lcappuccio.gitbrowser.controller;

import com.lcappuccio.gitbrowser.model.Commit;
import com.lcappuccio.gitbrowser.services.GraphApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

	@GetMapping(value = "findall", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Commit>> findAll() {

		List<Commit> allCommits = graphApi.findAll();
		return new ResponseEntity<>(allCommits, HttpStatus.OK);
	}

	@GetMapping(value = "findbyid/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Commit> findById(@PathVariable(value = "id") final String commitId) {

		Commit commit = graphApi.findById(commitId);
		return new ResponseEntity<>(commit, HttpStatus.OK);
	}

	@GetMapping(value = "findbymessage/{message}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Commit>> findByMessage(@PathVariable("message") final String commitMessage) {

		List<Commit> allCommits = graphApi.findByMessage(commitMessage);
		return new ResponseEntity<>(allCommits, HttpStatus.OK);
	}

	@GetMapping(value = "findparentof", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Commit> findParentOf(@RequestBody @Validated final Commit commit) {

		Commit parent = graphApi.findParentOf(commit);
		return new ResponseEntity<>(parent, HttpStatus.OK);
	}
}
