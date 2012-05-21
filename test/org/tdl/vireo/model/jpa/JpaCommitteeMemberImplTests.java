package org.tdl.vireo.model.jpa;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tdl.vireo.model.CommitteeMember;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.RoleType;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.state.StateManager;
import org.tdl.vireo.state.simple.StateManagerImpl;

import play.db.jpa.JPA;
import play.modules.spring.Spring;
import play.test.UnitTest;

/**
 * Test the Jpa implementation of committee member interface.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class JpaCommitteeMemberImplTests extends UnitTest {

	// Persistence repositories
	public static JpaPersonRepositoryImpl personRepo = Spring.getBeanOfType(JpaPersonRepositoryImpl.class);
	public static JpaSubmissionRepositoryImpl subRepo = Spring.getBeanOfType(JpaSubmissionRepositoryImpl.class);
	
	// Share the same person & submission
	public static Person person;
	public static Submission sub;
	
	/**
	 * Create a new person & submission for each test.
	 */
	@Before
	public void setup() {
		person = personRepo.createPerson("netid", "email@email.com", "first", "last", RoleType.NONE).save();
		sub = subRepo.createSubmission(person).save();
	}
	
	/**
	 * Cleanup the person & submission after each test.
	 */
	@After
	public void cleanup() {
		if (sub != null)
			subRepo.findSubmission(sub.getId()).delete();
		
		if (person != null)
			personRepo.findPerson(person.getId()).delete();
	}
	
	/**
	 * Test creating committee members, they can be duplicate.
	 */
	@Test
	public void testCreateMember() {
		CommitteeMember member = sub.addCommitteeMember("first", "last", "middle", false);
		assertEquals("first",member.getFirstName());
		assertEquals("last",member.getLastName());
		assertEquals("middle",member.getMiddleInitial());
		assertFalse(member.isCommitteeChair());
		
		member = sub.addCommitteeMember("first", "last", null, true);
		assertEquals("first",member.getFirstName());
		assertEquals("last",member.getLastName());
		assertNull(member.getMiddleInitial());
		assertTrue(member.isCommitteeChair());
	}
	
	/**
	 * Test creating a committee member, they must have a first and last name.
	 */
	@Test
	public void testBadCreateMember() {
		
		try {
			sub.addCommitteeMember(null, "last", "middle", false);
			fail("able to create a member without a first name.");
		} catch (IllegalArgumentException iae) { /* yay */ }
		
		try {
			sub.addCommitteeMember("first", null, "middle", false);
			fail("able to create a member without a last name.");
		} catch (IllegalArgumentException iae) { /* yay */ }
	}
	
	/**
	 * Test that saved members have ids.
	 */
	@Test
	public void testId() {
		CommitteeMember member = sub.addCommitteeMember("first", "last", "middle", false);
		member.save();
		assertNotNull(member.getId());
	}
	
	/**
	 * Test retrieving by ids.
	 */
	@Test
	public void testFindById() {
		CommitteeMember member = sub.addCommitteeMember("first", "last", "middle", false).save();
		
		CommitteeMember retrieved = subRepo.findCommitteeMember(member.getId());
		
		assertEquals(member.getId(), retrieved.getId());
	}
	
	/**
	 * Test that member is persistatable.
	 */
	@Test
	public void testPersistence() {
		// Commit and reopen a new transaction because some of the other tests
		// may have caused exceptions which set the transaction to be rolled
		// back.
		if (JPA.em().getTransaction().getRollbackOnly())
			JPA.em().getTransaction().rollback();
		else
			JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		CommitteeMember member = sub.addCommitteeMember("first", "last", "middle", false).save();
		
		// Commit and reopen a new transaction.
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		member = subRepo.findCommitteeMember(member.getId());
		assertEquals("first",member.getFirstName());
		assertEquals("last",member.getLastName());
		assertEquals("middle",member.getMiddleInitial());
		assertFalse(member.isCommitteeChair());
		
		
		subRepo.findSubmission(sub.getId()).delete();
		personRepo.findPerson(person.getId()).delete();
		
		sub = null;
		person = null;
		
		// Commit and reopen a new transaction.
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
	}
	
	/**
	 * Test that deleteing a member, removes it from the submission.
	 */
	@Test
	public void testDelete() {
		
		CommitteeMember member1 = sub.addCommitteeMember("first", "last", "middle", false).save();
		CommitteeMember member2 = sub.addCommitteeMember("first", "last", "middle", false).save();

		
		List<CommitteeMember> members = sub.getCommitteeMembers();
		
		assertTrue(members.contains(member1));
		assertTrue(members.contains(member2));
		assertEquals(2,members.size());
		
		member2.delete();
		members = sub.getCommitteeMembers();
		
		assertTrue(members.contains(member1));
		assertEquals(1,members.size());
		
		member1.delete();
		members = sub.getCommitteeMembers();
		
		assertEquals(0,members.size());
	}
	
	/**
	 * Test that committee members preserve their order.
	 */
	@Test
	public void testOrder() {
		
		CommitteeMember member4 = sub.addCommitteeMember("four", "last", "middle", false).save();
		CommitteeMember member3 = sub.addCommitteeMember("three", "last", "middle", false).save();
		CommitteeMember member2 = sub.addCommitteeMember("two", "last", "middle", false).save();
		CommitteeMember member1 = sub.addCommitteeMember("one", "last", "middle", false).save();
	
		member1.setDisplayOrder(1);
		member2.setDisplayOrder(2);
		member3.setDisplayOrder(3);
		member4.setDisplayOrder(4);
		
		member1.save();
		member2.save();
		member3.save();
		member4.save();
		
		// Reload the submission from the database.
		sub.refresh();
		
		List<CommitteeMember> members = sub.getCommitteeMembers();
		assertEquals(member1.getId(),members.get(0).getId());
		assertEquals(member2.getId(),members.get(1).getId());
		assertEquals(member3.getId(),members.get(2).getId());
		assertEquals(member4.getId(),members.get(3).getId());
	}
	
	
	
}
