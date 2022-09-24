package com.lcappuccio.gitbrowser.services;

import com.lcappuccio.gitbrowser.model.Commit;
import org.neo4j.dbms.api.DatabaseManagementService;
import org.neo4j.dbms.api.DatabaseManagementServiceBuilder;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.schema.IndexDefinition;
import org.neo4j.graphdb.schema.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PreDestroy;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class GraphApiImpl implements GraphApi {

	private static final Logger LOGGER = LoggerFactory.getLogger(GraphApiImpl.class);

	private final GitApi gitApi;
	private final GraphDatabaseService graphDb;
    private final DatabaseManagementService databaseManagementService;

	private final RelationshipType parentRelation =
			RelationshipType.withName(GraphProperties.COMMIT_PARENT.toString());
	private final Label graphCommitIdLabel = Label.label(GraphProperties.COMMIT_ID.toString());
	private final Label graphCommitMessageLabel = Label.label(GraphProperties.COMMIT_MESSAGE.toString());

    private IndexDefinition indexCommitId;
    private IndexDefinition indexCommitMessage;

	@Autowired
	public GraphApiImpl(final String dbFolder, GitApi gitApi) {

		this.gitApi = gitApi;

        final File databaseFile = new File(dbFolder);
        final Path databasePath = databaseFile.toPath();
        final DatabaseManagementServiceBuilder databaseManagementServiceBuilder =
                new DatabaseManagementServiceBuilder(databasePath);
        databaseManagementService = databaseManagementServiceBuilder.build();
        databaseManagementService.createDatabase("GitDatabase");
        graphDb = databaseManagementService.database("GitDatabase");

        LOGGER.info("Creating database in {}", dbFolder);
		createIndexes();
		createSchema();
		initializeDatabase(gitApi.getAllCommits());

	}

	@Override
	public List<Commit> findAll() {

		List<Commit> allCommits = new ArrayList<>();
		LOGGER.info("Find all");
		try (Transaction transaction = graphDb.beginTx()) {
            final ResourceIterator<Node> nodeResourceIterator = transaction.getAllNodes().iterator();
			transaction.commit();
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
		LOGGER.info("Find commit id {}", commitId);
		try (Transaction transaction = graphDb.beginTx()) {
            final ResourceIterator<Node> nodeResourceIterator = transaction.findNodes(graphCommitIdLabel,
                    GraphProperties.COMMIT_ID.toString(), commit);
			transaction.commit();
			while (nodeResourceIterator.hasNext()) {
				Node node = nodeResourceIterator.next();
				commit = fromNode(node);
			}
		}
		return commit;
	}

	@Override
	public List<Commit> findByMessage(String commitMessage) {
		List<Commit> commitList = new ArrayList<>();
		LOGGER.info("Find commit message {}", commitMessage);
		try (Transaction transaction = graphDb.beginTx()) {
            final ResourceIterator<Node> nodeResourceIterator = transaction.findNodes(graphCommitMessageLabel,
                    GraphProperties.COMMIT_MESSAGE.toString(), commitMessage);
            transaction.commit();
			while (nodeResourceIterator.hasNext()) {
				Node node = nodeResourceIterator.next();
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
	public List<Commit> findCommonParentsOf(Commit commit1, Commit commit2) {

		List<Commit> commonCommits = new ArrayList<>();
		Commit parentOfCommit1 = findParentOf(commit1);
		commonCommits.add(parentOfCommit1);

		while(parentOfCommit1 != null) {
			parentOfCommit1 = findParentOf(parentOfCommit1);
			commonCommits.add(parentOfCommit1);
		}

		List<Commit> parentCommit2List = new ArrayList<>();
		Commit parentOfCommit2 = findParentOf(commit2);
		parentCommit2List.add(parentOfCommit2);

		while(parentOfCommit2 != null) {
			parentOfCommit2 = findParentOf(parentOfCommit2);
			parentCommit2List.add(parentOfCommit2);
		}

		commonCommits.retainAll(parentCommit2List);

		return commonCommits;

	}

	@Override
	public Commit findParentOf(Commit commit) {

		LOGGER.info("Find parent commit of: " + commit.getId());
		Commit parentCommit = null;
		try (Transaction transaction = graphDb.beginTx()) {

            final ResourceIterator<Node> nodeIterator = transaction.findNodes(
                    graphCommitIdLabel,
                    GraphProperties.COMMIT_ID.toString(),
                    commit.getId());
            transaction.commit();

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
	 */
	private void createIndexes() {
		try (Transaction transaction = graphDb.beginTx()) {
            final Schema schema = transaction.schema();
            indexCommitId = schema
                    .indexFor(graphCommitIdLabel)
                    .on(GraphProperties.COMMIT_ID.toString())
                    .create();
            indexCommitMessage = schema
                    .indexFor(graphCommitMessageLabel)
                    .on(GraphProperties.COMMIT_MESSAGE.toString())
                    .create();
			transaction.commit();
            schema.awaitIndexOnline( indexCommitId, 10, TimeUnit.SECONDS );
            schema.awaitIndexOnline( indexCommitMessage, 10, TimeUnit.SECONDS );
		}
	}

	/**
	 * Creates the database schema and constraints
	 */
	private void createSchema() {
		try (Transaction transaction = graphDb.beginTx()) {
            transaction.schema()
                    .constraintFor(graphCommitIdLabel)
                    .assertPropertyIsUnique(GraphProperties.COMMIT_ID.toString())
                    .create();
            transaction.commit();
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
		transaction.commit();
		transaction.close();
		LOGGER.info("Created database with " + commits.size() + " commits");
	}

	/**
	 * Insert a single commit, transaction handling is on client side
	 *
	 * @param commit
	 */
	private void insertCommit(Commit commit) {
        try(Transaction transaction = graphDb.beginTx()) {
            Node commitNode = transaction.createNode();
            commitNode.setProperty(GraphProperties.COMMIT_AUTHOR.toString(), commit.getAuthor());
            commitNode.setProperty(GraphProperties.COMMIT_DATETIME.toString(), commit.getDateTime());
            commitNode.setProperty(GraphProperties.COMMIT_ID.toString(), commit.getId());
            commitNode.setProperty(GraphProperties.COMMIT_MESSAGE.toString(), commit.getMessage());
            commitNode.addLabel(graphCommitIdLabel);
            transaction.commit();
            /*
            indexCommitId.add(commitNode, GraphProperties.COMMIT_ID.toString(), commit.getId());
            indexCommitMessage.add(commitNode, GraphProperties.COMMIT_MESSAGE.toString(), commit.getMessage());
             */
        }
	}

	/**
	 * Create the relations between node and its' parent, transaction handling is on client side
	 *
	 * @param commit
	 * @param parentCommit
	 */
	private void insertRelationship(Commit commit, Commit parentCommit) {
        try(Transaction transaction = graphDb.beginTx()) {
            if (parentCommit != null) {
                Node childNode = transaction.findNode(graphCommitIdLabel,
                        GraphProperties.COMMIT_ID.toString(), commit.getId());
                Node parentNode = transaction.findNode(graphCommitIdLabel,
                        GraphProperties.COMMIT_ID.toString(), parentCommit.getId());
                childNode.createRelationshipTo(parentNode, parentRelation);
            } else {
                LOGGER.info("Commit {} has no parent", commit.getId());
            }
        }
	}

	@PreDestroy
	private void close() {
		LOGGER.info("close database");
		databaseManagementService.shutdown();
	}
}