package org.tdl.vireo.search.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.util.NumericUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tdl.vireo.model.MockActionLog;
import org.tdl.vireo.model.MockAttachment;
import org.tdl.vireo.model.MockCommitteeMember;
import org.tdl.vireo.model.MockCustomActionValue;
import org.tdl.vireo.model.MockPerson;
import org.tdl.vireo.model.MockSubmission;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.RoleType;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.SubmissionRepository;
import org.tdl.vireo.model.jpa.JpaPersonRepositoryImpl;
import org.tdl.vireo.security.SecurityContext;

import play.db.jpa.JPA;
import play.modules.spring.Spring;
import play.test.UnitTest;

public class LuceneIndexerImplTest extends UnitTest {

	// Spring injection
	public LuceneIndexerImpl indexer = Spring.getBeanOfType(LuceneIndexerImpl.class);
	public static SecurityContext context = Spring.getBeanOfType(SecurityContext.class);
	public static JpaPersonRepositoryImpl personRepo = Spring.getBeanOfType(JpaPersonRepositoryImpl.class);
	public SubmissionRepository subRepo = Spring.getBeanOfType(SubmissionRepository.class);
	
	// Test state
	public List<Submission> subs = new ArrayList<Submission>();
	public Person person;
	
	/**
	 * Make sure there are at least 100 submissions before doing any of these
	 * tests.
	 */
	@Before
	public void setup() {
		context.login(MockPerson.getAdministrator());
		person = personRepo.createPerson("netid", "email@email.com", "first", "last", RoleType.NONE).save();
		
		int total = (int) subRepo.findSubmissionsTotal();
		
		for (int i = total; i < 100; i++) {
			Submission sub = subRepo.createSubmission(person).save();
			subs.add(sub);
		}
		indexer.rollback();
		
		JPA.em().getTransaction().commit();
		JPA.em().getTransaction().begin();
		
		// pause for any index job to complete
		while (indexer.isJobRunning())
			Thread.yield();
	}
	
	/**
	 * Clean up any submissions and people that we created for this test. Then
	 * rollback the transaction.
	 */
	@After
	public void cleanup() {
		for (Submission sub : subs) {
			subRepo.findSubmission(sub.getId()).delete();
		}
		personRepo.findPerson(person.getId()).delete();
		context.logout();
		indexer.rollback();
		
		JPA.em().getTransaction().commit();
		JPA.em().getTransaction().begin();
	}
	
	/**
	 * Test that all the proper object types can be added to the indexer and the
	 * appropriate submission is updated. They are Submission, Attachment,
	 * CommitteeMember, CustomActionValue, and ActionLog.
	 */
	@Test
	public void testUpdateAndRollback() {
		
		// Test updating a new submission.
		MockSubmission sub = new MockSubmission();
		indexer.updated(sub.getId());
		assertTrue(indexer.transactionLocal.get().contains(sub.getId()));
		indexer.rollback();
	
		// Test updating an attachment
		MockAttachment attachment = new MockAttachment();
		attachment.submission = sub;
		indexer.updated(attachment);
		assertTrue(indexer.transactionLocal.get().contains(sub.getId()));
		indexer.rollback();
		
		// Test updating a new CommitteMember.
		MockCommitteeMember member = new MockCommitteeMember();
		member.submission = sub;
		indexer.updated(member);
		assertTrue(indexer.transactionLocal.get().contains(sub.getId()));
		indexer.rollback();
		
		// Test updating a custom action
		MockCustomActionValue action = new MockCustomActionValue();
		action.submission = sub;
		indexer.updated(action);
		assertTrue(indexer.transactionLocal.get().contains(sub.getId()));
		indexer.rollback();
		
		// Test updating an action log.
		MockActionLog log = new MockActionLog();
		log.submission = sub;
		indexer.updated(log);
		assertTrue(indexer.transactionLocal.get().contains(sub.getId()));
		indexer.rollback();
	}
	
	/**
	 * Test that the we are able to index a 100 documents, that the job
	 * management works during that time and at least one of the submissions
	 * exists in the index afterwards.
	 */
	@Test
	public void testCommit() throws CorruptIndexException, IOException, InterruptedException {
	
		// Send the first 100 subissions to the current transaction.
		Iterator<Submission> itr = subRepo.findAllSubmissions();
		
		long lastSubId = 0;
		for (int i = 0; i < 100; i++) {
			Submission sub = itr.next();
			assertNotNull(sub);
			indexer.updated(sub);
			
			lastSubId = sub.getId();
		}
		
		// Assert no job is running.
		assertTrue(lastSubId > 0);
		assertFalse(indexer.isJobRunning());
		assertEquals("None",indexer.getCurrentJobLabel());
		assertEquals(-1L, indexer.getCurrentJobProgress());
		assertEquals(-1L, indexer.getCurrentJobTotal());
		assertTrue(indexer.isUpdated(lastSubId));
		
		// Commit the 100 jobs.
		indexer.commit(false);
		
		// Check that a job is running
		assertTrue(indexer.isJobRunning());
		assertEquals("Update Index",indexer.getCurrentJobLabel());
		assertTrue(indexer.getCurrentJobProgress() >= 0 && indexer.getCurrentJobProgress() <= 100);
		assertEquals(100L, indexer.getCurrentJobTotal());
		
		// Wait for the current job to complete
		for (int i = 0; i < 1000; i++) {
			Thread.sleep(10);
			if (!indexer.isJobRunning())
				break;
		}
		assertFalse(indexer.isJobRunning());

		// Check that we can find the documents we indexed.
		IndexReader reader = IndexReader.open(indexer.index);
		IndexSearcher searcher = new IndexSearcher(reader);
		Query query = new TermQuery(new Term("subId",NumericUtils.longToPrefixCoded(lastSubId)));
		
		TopDocs topDocs = searcher.search(query, 1);
		assertNotNull(topDocs);
		assertNotNull(topDocs.scoreDocs);
		assertTrue(topDocs.scoreDocs.length > 0);
		Document doc = searcher.doc(topDocs.scoreDocs[0].doc);
		assertNotNull(doc);
		assertEquals(String.valueOf(lastSubId),doc.get("subId"));
		searcher.close();
	}
	
	/**
	 * Test that the index can be rebuilt.
	 */
	@Test
	public void testRebuild() throws CorruptIndexException, IOException, InterruptedException {
		
		// Assert no job is running
		assertFalse(indexer.isJobRunning());
		assertEquals("None",indexer.getCurrentJobLabel());
		assertEquals(-1L, indexer.getCurrentJobProgress());
		assertEquals(-1L, indexer.getCurrentJobTotal());
		
		// Kickoff rebuilding the index
		indexer.rebuild(false);
		
		// Check that a job is running
		assertTrue(indexer.isJobRunning());
		assertEquals("Rebuild Index",indexer.getCurrentJobLabel());
		assertTrue(indexer.getCurrentJobProgress() >= 0 );
		assertTrue(indexer.getCurrentJobTotal() >= 100 );
		
		// Wait for the current job to complete
		for (int i = 0; i < 1000; i++) {
			Thread.sleep(10);
			if (!indexer.isJobRunning())
				break;
		}
		assertFalse(indexer.isJobRunning());
		
		// Check that the first submission is present in the index.
		Submission sub = subRepo.findAllSubmissions().next();
		IndexReader reader = IndexReader.open(indexer.index);
		IndexSearcher searcher = new IndexSearcher(reader);
		Query query = new TermQuery(new Term("subId",NumericUtils.longToPrefixCoded(sub.getId())));
		
		TopDocs topDocs = searcher.search(query, 1);
		assertNotNull(topDocs);
		assertNotNull(topDocs.scoreDocs);
		assertTrue(topDocs.scoreDocs.length > 0);
		Document doc = searcher.doc(topDocs.scoreDocs[0].doc);
		assertNotNull(doc);
		assertEquals(String.valueOf(sub.getId()),doc.get("subId"));
		
		searcher.close();
	}
	
	
}
