package com.appway.gitbrowser.controller;

import com.appway.gitbrowser.model.Commit;
import com.appway.gitbrowser.services.GraphApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
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
}
