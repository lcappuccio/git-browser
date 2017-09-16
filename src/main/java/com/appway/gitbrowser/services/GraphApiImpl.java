package com.appway.gitbrowser.services;

import com.appway.gitbrowser.model.Commit;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.graphdb.schema.ConstraintDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PreDestroy;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
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

	@Autowired
	public GraphApiImpl(final String dbFolder, GitApi gitApi) {

		this.gitApi = gitApi;
		graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(new File(dbFolder));
		IndexManager indexManager = graphDb.index();

		LOGGER.info("Creating database in " + dbFolder);
		createIndexes(indexManager);
		createSchema();
		initializeDatabase(gitApi.getAllCommits());

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
		Commit commit = null;
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
	public List<Commit> findByMessage(String commitMessage) {
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

		Collections.sort(commitList);
		return commitList;
	}

	@Override
	public Commit findParentOf(Commit commit) {

		LOGGER.info("Find parent commit of: " + commit.getId());
		Commit parentCommit = null;
		try (Transaction tx = graphDb.beginTx()) {
			Iterator<Node> nodeIterator = indexCommitId.get(GraphProperties.COMMIT_ID.toString(), commit.getId())
					.iterator();
			tx.success();
			while (nodeIterator.hasNext()) {
				Node commitNode = nodeIterator.next();
				Relationship singleRelationship = commitNode.getSingleRelationship(parentRelation, Direction.OUTGOING);
				if (singleRelationship != null) {
					Node endNode = singleRelationship.getEndNode();
					parentCommit = fromNode(endNode);
				}
			}
		}
		return parentCommit;
	}

	/**
	 * Creates indexes with the given IndexManager
	 *
	 * @param indexManager
	 */
	private void createIndexes(IndexManager indexManager) {
		try (Transaction transaction = graphDb.beginTx()) {
			indexCommitId = indexManager.forNodes(GraphProperties.COMMIT_ID.toString());
			indexCommitMessage = indexManager.forNodes(GraphProperties.COMMIT_MESSAGE.toString());
			transaction.success();
			transaction.close();
		}
	}

	/**
	 * Creates the database schema and constraints
	 */
	private void createSchema() {
		try (Transaction transaction = graphDb.beginTx()) {
			Iterator<ConstraintDefinition> constraintDefinitionIterator =
					graphDb.schema().getConstraints(constraintCommitLabel).iterator();
			if (!constraintDefinitionIterator.hasNext()) {
				graphDb.schema().constraintFor(constraintCommitLabel)
						.assertPropertyIsUnique(GraphProperties.COMMIT_ID.toString()).create();
			}
			transaction.success();
			transaction.close();
		}
	}

	/**
	 * Transform Node object to Commit
	 *
	 * @param node
	 * @return
	 */
	private Commit fromNode(Node node) {

		String nodeCommitAuthor = node.getProperty(GraphProperties.COMMIT_AUTHOR.toString()).toString();
		Long nodeCommitDateTime =
				Long.parseLong(node.getProperty(GraphProperties.COMMIT_DATETIME.toString()).toString());
		String nodeCommitId = node.getProperty(GraphProperties.COMMIT_ID.toString()).toString();
		String nodeCommitMessage = node.getProperty(GraphProperties.COMMIT_MESSAGE.toString()).toString();

		return new Commit(nodeCommitId, nodeCommitDateTime, nodeCommitAuthor, nodeCommitMessage);
	}

	/**
	 * Insert commit to the database in a single database transaction for performance reasons
	 *
	 * @param commits
	 */
	private void initializeDatabase(List<Commit> commits) {

		Transaction transaction = graphDb.beginTx();
		for (Commit commit : commits) {
			insertCommit(commit);
			insertRelationship(commit, gitApi.getParentOf(commit));
		}
		transaction.success();
		transaction.close();
		LOGGER.info("Created database with " + commits.size() + " commits");
	}

	/**
	 * Insert a single commit, transaction handling is on client side
	 *
	 * @param commit
	 */
	private void insertCommit(Commit commit) {

		Node commitNode = graphDb.createNode();
		commitNode.setProperty(GraphProperties.COMMIT_AUTHOR.toString(), commit.getAuthor());
		commitNode.setProperty(GraphProperties.COMMIT_DATETIME.toString(), commit.getDateTime());
		commitNode.setProperty(GraphProperties.COMMIT_ID.toString(), commit.getId());
		commitNode.setProperty(GraphProperties.COMMIT_MESSAGE.toString(), commit.getMessage());

		commitNode.addLabel(constraintCommitLabel);

		indexCommitId.add(commitNode, GraphProperties.COMMIT_ID.toString(), commit.getId());
		indexCommitMessage.add(commitNode, GraphProperties.COMMIT_MESSAGE.toString(), commit.getMessage());

	}

	/**
	 * Create the relations between node and its' parent, transaction handling is on client side
	 *
	 * @param commit
	 * @param parentCommit
	 */
	private void insertRelationship(Commit commit, Commit parentCommit) {

		if (parentCommit != null) {
			Iterator<Node> parentNodeIterator =
					indexCommitId.get(GraphProperties.COMMIT_ID.toString(), parentCommit.getId()).iterator();
			Iterator<Node> commitNodeIterator =
					indexCommitId.get(GraphProperties.COMMIT_ID.toString(), commit.getId()).iterator();
			if (parentNodeIterator.hasNext() && commitNodeIterator.hasNext()) {
				Node parentNode = parentNodeIterator.next();
				Node commitNode = commitNodeIterator.next();
				commitNode.createRelationshipTo(parentNode, parentRelation);
			}
		} else {
			LOGGER.info("Commit " + commit.getId() + " has no parent");
		}
	}

	@PreDestroy
	private void close() {
		LOGGER.info("close database");
		graphDb.shutdown();
	}
}
