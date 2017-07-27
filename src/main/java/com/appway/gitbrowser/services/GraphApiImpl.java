package com.appway.gitbrowser.services;

import org.eclipse.jgit.revwalk.RevCommit;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.graphdb.index.RelationshipIndex;
import org.neo4j.graphdb.schema.ConstraintDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.File;
import java.util.Iterator;
import java.util.List;

public class GraphApiImpl implements GraphApi {

	private static final Logger LOGGER = LoggerFactory.getLogger(GraphApiImpl.class);

	private final GraphDatabaseService graphDb;
	private final RelationshipType parentRelation = RelationshipType.withName(GraphProperties.COMMIT_PARENT.toString());
	private final Label constraintCommitLabel = Label.label(GraphProperties.COMMIT_ID.toString());

	private Index<Node> indexCommitId;
	private RelationshipIndex indexParent;

	public GraphApiImpl(final String dbFolder) {
		graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(new File(dbFolder));
		IndexManager indexManager = graphDb.index();
		createIndexes(indexManager);
		createSchema();
		LOGGER.info("Creating database at " + dbFolder);
	}

	@Override
	public List<RevCommit> findAll() {
		throw new NotImplementedException();
	}

	@Override
	public RevCommit findById(String commitId) {
		throw new NotImplementedException();
	}

	@Override
	public List<RevCommit> findCommitsByMessage(String commitMessage) {
		throw new NotImplementedException();
	}

	@Override
	public List<RevCommit> findCommitsThatContainMessage(String textToSearch) {
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
}
