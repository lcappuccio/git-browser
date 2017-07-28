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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GraphApiImpl implements GraphApi {

	private static final Logger LOGGER = LoggerFactory.getLogger(GraphApiImpl.class);

	private final GitApi gitApi;
	private final GraphDatabaseService graphDb;
	private final RelationshipType parentRelation =
			RelationshipType.withName(GraphProperties.COMMIT_PARENT.toString());
	private final Label constraintCommitLabel = Label.label(GraphProperties.COMMIT_ID.toString());

	private Index<Node> indexCommitId, indexCommitMessage;
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
			insertCommit(commit);
		}

		addRelationships();

		LOGGER.info("Added " + allCommits.size() + " commits");
	}

	@Override
	public List<Commit> findAll() {

		List<Commit> allCommits = new ArrayList<>();
		LOGGER.info("Find all");
		try (Transaction tx = graphDb.beginTx()) {
			ResourceIterator<Node> nodeResourceIterator = graphDb.getAllNodes().iterator();
			tx.success();
			while (nodeResourceIterator.hasNext()) {
				Node node = nodeResourceIterator.next();
				allCommits.add(fromNode(node));
			}
		}
		return allCommits;
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
				commit = fromNode(node);
			}
		}
		return commit;
	}

	@Override
	public List<Commit> findCommitsByMessage(String commitMessage) {
		List<Commit> commitList = new ArrayList<>();
		LOGGER.info("Find commit message: " + commitMessage);
		try (Transaction tx = graphDb.beginTx()) {
			Iterator<Node> nodeIterator = graphDb.getAllNodes().iterator();
			tx.success();
			while (nodeIterator.hasNext()) {
				Node node = nodeIterator.next();
				String nodeCommitMessage = node.getProperty(GraphProperties.COMMIT_MESSAGE.toString()).toString();

				if (nodeCommitMessage.contains(commitMessage)) {
					commitList.add(fromNode(node));
				}

			}
		}
		return commitList;
	}

	@Override
	public List<Commit> findCommitsThatContainMessage(String textToSearch) {
		throw new NotImplementedException();
	}

	/**
	 * Creates indexes with the given IndexManager
	 *
	 * @param indexManager
	 */
	private void createIndexes(IndexManager indexManager) {
		try (Transaction tx = graphDb.beginTx()) {
			indexCommitId = indexManager.forNodes(GraphProperties.COMMIT_ID.toString());
			indexCommitMessage = indexManager.forNodes(GraphProperties.COMMIT_MESSAGE.toString());
			indexParent = indexManager.forRelationships(GraphProperties.COMMIT_PARENT.toString());
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
	 */
	private synchronized void insertCommit(Commit commit) {

		try (Transaction tx = graphDb.beginTx()) {

			Node commitNode = graphDb.createNode();
			commitNode.setProperty(GraphProperties.COMMIT_AUTHOR.toString(), commit.getAuthor());
			commitNode.setProperty(GraphProperties.COMMIT_DATETIME.toString(), commit.getDateTime());
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
			indexCommitMessage.add(commitNode, GraphProperties.COMMIT_MESSAGE.toString(), commit.getMessage());

			// TODO LC add relationship here

			tx.success();
		}
	}

	/**
	 * Create the relations between node and its' parent
	 */
	private void addRelationships() {

		try (Transaction tx = graphDb.beginTx()) {

			Iterator<Node> nodeIterator = graphDb.getAllNodes().iterator();
			tx.success();
			while (nodeIterator.hasNext()) {
				Node commitNode = nodeIterator.next();
				Node parentNode = null;
				Commit commit = fromNode(commitNode);
				Commit parentCommit = gitApi.getParentOf(commit);
				if (parentCommit != null) {
					Iterator<Node> parentNodeIterator =
							indexCommitId.get(GraphProperties.COMMIT_ID.toString(), parentCommit.getId()).iterator();
					tx.success();
					if (parentNodeIterator.hasNext()) {
						parentNode = nodeIterator.next();
					}
					Relationship parentOf = commitNode.createRelationshipTo(parentNode, parentRelation);
					indexParent.add(parentOf, GraphProperties.COMMIT_PARENT.toString(), commit.getId());
					tx.success();
				}
			}
			tx.success();
		}
	}

	private Commit fromNode(Node node) {

		String nodeCommitAuthor = node.getProperty(GraphProperties.COMMIT_AUTHOR.toString()).toString();
		Long nodeCommitDateTime =
				Long.parseLong(node.getProperty(GraphProperties.COMMIT_DATETIME.toString()).toString());
		String nodeCommitId = node.getProperty(GraphProperties.COMMIT_ID.toString()).toString();
		String nodeCommitMessage = node.getProperty(GraphProperties.COMMIT_MESSAGE.toString()).toString();

		return new Commit(nodeCommitId, nodeCommitDateTime, nodeCommitAuthor, nodeCommitMessage);
	}
}
