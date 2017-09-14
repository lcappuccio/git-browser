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
		createIndexes(indexManager);
		createSchema();
		LOGGER.info("Creating database at " + dbFolder);

		insertCommits(gitApi.getAllCommits());

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
		try (Transaction tx = graphDb.beginTx()) {
			indexCommitId = indexManager.forNodes(GraphProperties.COMMIT_ID.toString());
			indexCommitMessage = indexManager.forNodes(GraphProperties.COMMIT_MESSAGE.toString());
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
	 * Insert commit to the database
	 *
	 * @param commits
	 */
	private void insertCommits(List<Commit> commits) {

		try (Transaction tx = graphDb.beginTx()) {
			for (Commit commit : commits) {
				insertCommit(commit);
				insertRelationship(commit, gitApi.getParentOf(commit));
			}
			LOGGER.info("Added " + commits.size() + " commits");
			tx.success();
		} catch (ConstraintViolationException ex) {
			String errorMessage = ex.getMessage();
			LOGGER.error(errorMessage);
			throw new ConstraintViolationException(errorMessage);
		}
	}

	/**
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
	 * Create the relations between node and its' parent
	 *
	 * @param commit
	 * @param parentCommit
	 */
	private void insertRelationship(Commit commit, Commit parentCommit) {

		if (parentCommit == null) {
			return;
		}

		Iterator<Node> parentNodeIterator =
				indexCommitId.get(GraphProperties.COMMIT_ID.toString(), parentCommit.getId()).iterator();
		Iterator<Node> commitNodeIterator =
				indexCommitId.get(GraphProperties.COMMIT_ID.toString(), commit.getId()).iterator();
		if (parentNodeIterator.hasNext() && commitNodeIterator.hasNext()) {
			Node parentNode = parentNodeIterator.next();
			Node commitNode = commitNodeIterator.next();
			commitNode.createRelationshipTo(parentNode, parentRelation);
		}
	}

	@PreDestroy
	private void close() {
		LOGGER.info("close database");
		graphDb.shutdown();
	}
}
