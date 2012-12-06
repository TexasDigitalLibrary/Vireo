package org.tdl.vireo.model.jpa;

import java.util.Iterator;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tdl.vireo.model.ActionLog;
import org.tdl.vireo.model.CommitteeMember;
import org.tdl.vireo.model.CommitteeMemberRoleType;
import org.tdl.vireo.model.DegreeLevel;
import org.tdl.vireo.model.MockPerson;
import org.tdl.vireo.model.NameFormat;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.RoleType;
import org.tdl.vireo.model.SettingsRepository;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.security.SecurityContext;
import org.tdl.vireo.state.State;
import org.tdl.vireo.state.StateManager;

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
	public static SecurityContext context = Spring.getBeanOfType(SecurityContext.class);
	public static StateManager stateManager = Spring.getBeanOfType(StateManager.class);
	public static JpaPersonRepositoryImpl personRepo = Spring.getBeanOfType(JpaPersonRepositoryImpl.class);
	public static JpaSubmissionRepositoryImpl subRepo = Spring.getBeanOfType(JpaSubmissionRepositoryImpl.class);
	public static JpaSettingsRepositoryImpl settingRepo = Spring.getBeanOfType(JpaSettingsRepositoryImpl.class);

	
	// Share the same person & submission
	public static Person person;
	public static Submission sub;
	
	/**
	 * Create a new person & submission for each test.
	 */
	@Before
	public void setup() {
		context.login(MockPerson.getAdministrator());
		person = personRepo.createPerson("netid", "email@email.com", "first", "last", RoleType.NONE).save();
		sub = subRepo.createSubmission(person).save();
	}
	
	/**
	 * Cleanup the person & submission after each test.
	 */
	@After
	public void cleanup() {
		try {
		JPA.em().clear();
		if (sub != null)
			subRepo.findSubmission(sub.getId()).delete();
		
		if (person != null)
			personRepo.findPerson(person.getId()).delete();
		
		context.logout();
		
		JPA.em().getTransaction().rollback();
		JPA.em().getTransaction().begin();
		} catch (RuntimeException re) {
			
		}
	}
	
	/**
	 * Test creating committee members, they can be duplicate.
	 */
	@Test
	public void testCreateMember() {
		CommitteeMember member = sub.addCommitteeMember("first", "last", "middle");
		assertEquals("first",member.getFirstName());
		assertEquals("last",member.getLastName());
		assertEquals("middle",member.getMiddleName());
		assertEquals(0,member.getRoles().size());
		
		member = sub.addCommitteeMember("first", "last", null);
		assertEquals("first",member.getFirstName());
		assertEquals("last",member.getLastName());
		assertNull(member.getMiddleName());
		assertEquals(0,member.getRoles().size());
	}
	
	/**
	 * Test creating a committee member, they must have a first and last name.
	 */
	@Test
	public void testBadCreateMember() {
		
		try {
			sub.addCommitteeMember(null, null, "middle");
			fail("able to create a member without a first or last name.");
		} catch (IllegalArgumentException iae) { /* yay */ }
	}
	
	/**
	 * Test creating a member with just a first or last name.
	 */
	@Test
	public void testCreateSingleNamedMember() {
		CommitteeMember justFirst = sub.addCommitteeMember("first", null, null).save();

		CommitteeMember justLast = sub.addCommitteeMember(null, "last", null).save();
		
		justFirst.delete();
		justLast.delete();
	}
	
	/**
	 * Test that saved members have ids.
	 */
	@Test
	public void testId() {
		CommitteeMember member = sub.addCommitteeMember("first", "last", "middle");
		member.save();
		assertNotNull(member.getId());
	}
	
	/**
	 * Test retrieving by ids.
	 */
	@Test
	public void testFindById() {
		CommitteeMember member = sub.addCommitteeMember("first", "last", "middle").save();
		
		CommitteeMember retrieved = subRepo.findCommitteeMember(member.getId());
		
		assertEquals(member.getId(), retrieved.getId());
	}
	
	/**
	 * Test name formatting
	 */
	@Test 
	public void testStudentNameFormat() {
		CommitteeMember member = sub.addCommitteeMember("First", "Last", "Middle").save();

		
		assertEquals("Last, First Middle",member.getFormattedName(NameFormat.LAST_FIRST_MIDDLE_BIRTH));

	}
	
	/**
	 * Test role formatting
	 */
	@Test 
	public void testRoleFormat() {
		
		CommitteeMemberRoleType type1 = settingRepo.createCommitteeMemberRoleType("Role 1", DegreeLevel.UNDERGRADUATE);
		type1.setDisplayOrder(100);
		type1.save();
		CommitteeMemberRoleType type2 = settingRepo.createCommitteeMemberRoleType("Role 2", DegreeLevel.UNDERGRADUATE);
		type2.setDisplayOrder(0);
		type2.save();
		
		CommitteeMember member = sub.addCommitteeMember("First", "Last", "Middle");
		member.addRole("Role 1");
		member.addRole("Role 2");
		member.save();

		assertEquals("Role 2, Role 1",member.getFormattedRoles());
		
	
		member.delete();
		type1.delete();
		type2.delete();
		
	}
	
	/**
	 * Test has role test
	 */
	@Test 
	public void testHasRole() {
		
		CommitteeMember member = sub.addCommitteeMember("First", "Last", "Middle");
		member.addRole("Role 1");
		member.addRole("Role 2");
		member.save();

		assertTrue(member.hasRole("Role 1","Role 2", "doesnotexist"));
		assertTrue(member.hasRole("Role 1","doesnotexist", "doesnotexist"));
		assertTrue(member.hasRole("doesnotexist", "Role 2","doesnotexist"));
		assertFalse(member.hasRole("doesnotexist","doesnotexist", "doesnotexist"));
		assertFalse(member.hasNoRole());
		
		
		member.getRoles().clear();
		assertTrue(member.hasNoRole());

		
		member.delete();
	}
	
	/**
	 * Test that action logs are generated appropriately.
	 */
	@Test
	public void testActionLogGeneration() {

		State initialState = stateManager.getInitialState();
		State nextState = initialState.getTransitions(sub).get(0);
		sub.setState(nextState);
		sub.save();
		
		CommitteeMember member = sub.addCommitteeMember("First", "Last", "Middle").save();
		member.setFirstName("Changed");
		member.addRole("Role 1");
		member.addRole("Role 2");
		member.save();
		member.delete();
		
		List<ActionLog> logs = subRepo.findActionLog(sub);
		Iterator<ActionLog> logItr = logs.iterator();
		
		sub.delete();
		sub = null;
		
		assertEquals("Committee member 'Changed Middle Last' (Role 1, Role 2) removed", logItr.next().getEntry());
		assertEquals("Committee member 'Changed Middle Last' (Role 1, Role 2) modified", logItr.next().getEntry());
		assertEquals("Committee member 'First Middle Last' added", logItr.next().getEntry());
		assertEquals("Submission status changed to 'Submitted'",logItr.next().getEntry());
		assertEquals("Submission created",logItr.next().getEntry());
		
		assertFalse(logItr.hasNext());
	}
	
	/**
	 * Test that member is persistatable.
	 */
	@Test
	public void testPersistence() {
		
		CommitteeMember member = sub.addCommitteeMember("first", "last", "middle");
		member.addRole("Role 1");
		member.addRole("Role 2");
		member.save();
		
		// Commit and reopen a new transaction.
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		member = subRepo.findCommitteeMember(member.getId());
		assertEquals("first",member.getFirstName());
		assertEquals("last",member.getLastName());
		assertEquals("middle",member.getMiddleName());
		assertEquals(2,member.getRoles().size());
		assertEquals("Role 1",member.getRoles().get(0));
		assertEquals("Role 2",member.getRoles().get(1));
		
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
		
		CommitteeMember member1 = sub.addCommitteeMember("first", "last", "middle").save();
		CommitteeMember member2 = sub.addCommitteeMember("first", "last", "middle");
		member2.addRole("Role 1");
		member2.addRole("Role 2");
		member2.save();

		
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
		
		CommitteeMember member4 = sub.addCommitteeMember("four", "last", "middle").save();
		CommitteeMember member3 = sub.addCommitteeMember("three", "last", "middle").save();
		CommitteeMember member2 = sub.addCommitteeMember("two", "last", "middle").save();
		CommitteeMember member1 = sub.addCommitteeMember("one", "last", "middle").save();
	
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
	
	/**
	 * Test who has access to add/modify/delete members.
	 */
	@Test
	public void testAccess() {		
		// Test that the owner can add a member
		context.login(person);
		CommitteeMember member1 = sub.addCommitteeMember("first", "last", "middle").save();
		member1.setFirstName("changed");
		
		// Test that a reviewer can add a member
		context.login(MockPerson.getReviewer());
		CommitteeMember member2 = sub.addCommitteeMember("first", "last", "middle").save();
		member2.setFirstName("changed");

		// Test that a someone else can not add a member.
		context.login(MockPerson.getStudent());
		try {
			sub.addCommitteeMember("first", "last", "middle").save();
			fail("Someone else was able to add a member to a submission.");
		} catch (SecurityException se) {
			/* yay */
		}	
		context.login(MockPerson.getAdministrator());
	}
	
	
	
}
