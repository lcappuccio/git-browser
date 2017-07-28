package com.appway.gitbrowser.services;

import com.appway.gitbrowser.model.Commit;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.graphdb.index.RelationshipIndex;
import org.neo4j.graphdb.schema.ConstraintDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class GraphApiImpl implements GraphApi {

	private static final Logger LOGGER = LoggerFactory.getLogger(GraphApiImpl.class);

	private final GitApi gitApi;
	private final GraphDatabaseService graphDb;
	private final RelationshipType parentRelation = RelationshipType.withName(GraphProperties.COMMIT_PARENT.toString
			());
	private final Label constraintCommitLabel = Label.label(GraphProperties.COMMIT_ID.toString());

	private Index<Node> indexCommitId;
	private RelationshipIndex indexParent;

	@Autowired
	public GraphApiImpl(final String dbFolder, GitApi gitApi) throws IOException, GitAPIException {
		this.gitApi = gitApi;
		graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(new File(dbFolder));
		IndexManager indexManager = graphDb.index();
		createIndexes(indexManager);
		createSchema();
		LOGGER.info("Creating database at " + dbFolder);

		// Fill database
		List<Commit> allCommits = gitApi.getAllCommits();
		for (Commit commit : allCommits) {
			// Commit parentCommit = gitApi.getParentOf(commit);
			insertCommit(commit);
		}

		LOGGER.info("Added " + allCommits.size() + " commits");
	}

	@Override
	public List<Commit> findAll() {
		throw new NotImplementedException();
	}

	@Override
	public Commit findById(String commitId) {
		Commit commit = new Commit();
		LOGGER.info("Find commit id: " + commitId);
		try (Transaction tx = graphDb.beginTx()) {
			Iterator<Node> nodeIterator = indexCommitId.get(GraphProperties.COMMIT_ID.toString(), commitId).iterator();
			tx.success();
			while (nodeIterator.hasNext()) {
				Node node = nodeIterator.next();
				String nodeCommitId = node.getProperty(GraphProperties.COMMIT_ID.toString()).toString();
				String nodeCommitMessage = node.getProperty(GraphProperties.COMMIT_MESSAGE.toString()).toString();

				commit.setId(nodeCommitId);
				commit.setMessage(nodeCommitMessage);
			}
		}
		return commit;
	}

	@Override
	public List<Commit> findCommitsByMessage(String commitMessage) {
		throw new NotImplementedException();
	}

	@Override
	public List<Commit> findCommitsThatContainMessage(String textToSearch) {
		throw new NotImplementedException();
	}

	/**
	 * Creates indexes with the given IndexManager
	 *
	 * @param indexManager
	 * @return
	 */
	private void createIndexes(IndexManager indexManager) {
		try (Transaction tx = graphDb.beginTx()) {
			indexCommitId = indexManager.forNodes(GraphProperties.COMMIT_ID.toString());
			tx.success();
		}
	}

	/**
	 * Creates the database schema and constraints
	 */
	private void createSchema() {
		try (Transaction tx = graphDb.beginTx()) {
			Iterator<ConstraintDefinition> constraintDefinitionIterator =
					graphDb.schema().getConstraints(constraintCommitLabel).iterator();
			if (!constraintDefinitionIterator.hasNext()) {
				graphDb.schema().constraintFor(constraintCommitLabel)
						.assertPropertyIsUnique(GraphProperties.COMMIT_ID.toString()).create();
				LOGGER.info("Constraint " + GraphProperties.COMMIT_ID.toString());
			}
			tx.success();
		}
	}

	@PreDestroy
	private void close() {
		LOGGER.info("close database");
		graphDb.shutdown();
	}

	/**
	 * Insert commit to the database
	 *
	 * @param commit
	 * @throws IOException
	 * @throws GitAPIException
	 */
	private void insertCommit(Commit commit) throws IOException, GitAPIException {

		try (Transaction tx = graphDb.beginTx()) {

			Node commitNode = graphDb.createNode();
			commitNode.setProperty(GraphProperties.COMMIT_ID.toString(), commit.getId());
			commitNode.setProperty(GraphProperties.COMMIT_MESSAGE.toString(), commit.getMessage());

			try {
				commitNode.addLabel(constraintCommitLabel);
			} catch (ConstraintViolationException ex) {
				String errorMessage = ex.getMessage();
				tx.failure();
				LOGGER.error(commit.getId() + ": " + errorMessage);
				throw new ConstraintViolationException(errorMessage);
			}

			indexCommitId.add(commitNode, GraphProperties.COMMIT_ID.toString(), commit.getId());
			// TODO LC add relationship here

			tx.success();
		}
	}
}
