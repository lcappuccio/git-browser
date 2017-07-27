package com.appway.gitbrowser.services;

import com.appway.gitbrowser.model.Commit;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;

public class GraphApiImpl implements GraphApi {

	@Override
	public List<Commit> findAll() {
		throw new NotImplementedException();
	}

	@Override
	public Commit findById(String commitId) {
		throw new NotImplementedException();
	}

	@Override
	public List<Commit> findCommitsByMessage(String commitMessage) {
		throw new NotImplementedException();
	}

	@Override
	public List<Commit> findCommitsThatContainMessage(String textToSearch) {
		throw new NotImplementedException();
	}
}
