/**
 * 
 */
package org.tdl.vireo.model.jpa;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tdl.vireo.model.AbstractWorkflowRuleCondition;
import org.tdl.vireo.model.ConditionType;
import org.tdl.vireo.model.MockPerson;
import org.tdl.vireo.model.SettingsRepository;
import org.tdl.vireo.security.SecurityContext;

import play.db.jpa.JPA;
import play.modules.spring.Spring;
import play.test.UnitTest;

/**
 * @author <a href="mailto:gad.krumholz@austin.utexas.edu">Gad Krumholz</a>
 *
 */
public class JpaEmailWorkflowRuleConditionImplTest extends UnitTest {
	
	// Repositories
	public static SecurityContext context = Spring.getBeanOfType(SecurityContext.class);
	public static SettingsRepository settingRepo = Spring.getBeanOfType(JpaSettingsRepositoryImpl.class);
	public static ConditionType condition = ConditionType.Always;
	public static ConditionType condition1 = ConditionType.College;
	public static ConditionType condition2 = ConditionType.Department;
	public static ConditionType condition3 = ConditionType.Program;
	public static ConditionType testCondition = ConditionType.College;
	
	@Before
	public void setup() {
		context.login(MockPerson.getAdministrator());
	}
	
	@After
	public void cleanup() {
		JPA.em().clear();
		context.logout();
		
		JPA.em().getTransaction().rollback();
		JPA.em().getTransaction().begin();
	}
	
	/**
	 * Test creating an email workflow rule
	 */
	@Test
	public void testCreate() {
		
		AbstractWorkflowRuleCondition emwflRuleCondition = settingRepo.createEmailWorkflowRuleCondition(condition);
		
		assertNotNull(emwflRuleCondition);
		assertEquals(condition,emwflRuleCondition.getConditionType());
		
		emwflRuleCondition.delete();
	}
	
	/**
	 * Test creating the email workflow rule without required parameters
	 */
	@Test
	public void testBadCreate() {
		
		try {
			settingRepo.createEmailWorkflowRuleCondition(ConditionType.valueOf(""));
			fail("Able to create blank email workflow rule");
		} catch (IllegalArgumentException iae) {
			/* yay */
		}
	}
	
	/**
	 * Test the id.
	 */
	@Test
	public void testId() {
		
		AbstractWorkflowRuleCondition emwflRuleCondition = settingRepo.createEmailWorkflowRuleCondition(condition).save();

		assertNotNull(emwflRuleCondition.getId());
		
		emwflRuleCondition.delete();
	}
	
	/**
	 * Test retrieval by id.
	 */
	@Test
	public void testFindById() {
		AbstractWorkflowRuleCondition emwflRuleCondition = settingRepo.createEmailWorkflowRuleCondition(condition).save();

		
		AbstractWorkflowRuleCondition retrieved = settingRepo.findEmailWorkflowRuleCondition(emwflRuleCondition.getId());
		
		assertEquals(emwflRuleCondition.getConditionType(), retrieved.getConditionType());
		
		retrieved.delete();
	}
	
	/**
	 * Test retrieving all email workflow rule
	 */
	@Test
	public void testfindAllEmailWorkflowRuleConditions() {

		int initialSize = settingRepo.findAllEmailWorkflowRuleConditions().size();
		
		AbstractWorkflowRuleCondition emwflRuleCondition1 = settingRepo.createEmailWorkflowRuleCondition(condition1).save();
		AbstractWorkflowRuleCondition emwflRuleCondition2 = settingRepo.createEmailWorkflowRuleCondition(condition2).save();

		int postSize = settingRepo.findAllEmailWorkflowRuleConditions().size();
		
		assertEquals(initialSize +2, postSize);
		
		emwflRuleCondition1.delete();
		emwflRuleCondition2.delete();
	}
	
	/**
	 * Test the validation when modifying the condition
	 */
	@Test 
	public void testConditionValidation() {
		AbstractWorkflowRuleCondition emwflRuleCondition = settingRepo.createEmailWorkflowRuleCondition(condition).save();
		AbstractWorkflowRuleCondition test = settingRepo.createEmailWorkflowRuleCondition(testCondition).save();
		
		try {
			test.setConditionType(ConditionType.valueOf(""));
			fail("Able to change condition to blank");
		} catch (IllegalArgumentException iae) {
			/* yay */
		}
		
		// Recover the transaction after a failure.
		JPA.em().getTransaction().rollback();
		JPA.em().getTransaction().begin();
	}
	
	/**
	 * Test the display order attribute.
	 */
	@Test
	public void testOrder() {
		AbstractWorkflowRuleCondition emwflRuleCondition3 = settingRepo.createEmailWorkflowRuleCondition(condition3).save();
		AbstractWorkflowRuleCondition emwflRuleCondition1 = settingRepo.createEmailWorkflowRuleCondition(condition1).save();
		AbstractWorkflowRuleCondition emwflRuleCondition2 = settingRepo.createEmailWorkflowRuleCondition(condition2).save();
		
		emwflRuleCondition1.setDisplayOrder(0);
		emwflRuleCondition2.setDisplayOrder(1);
		emwflRuleCondition3.setDisplayOrder(3);
		
		emwflRuleCondition1.save();
		emwflRuleCondition2.save();
		emwflRuleCondition3.save();
		
		List<AbstractWorkflowRuleCondition> emwflRuleConditions = settingRepo.findAllEmailWorkflowRuleConditions();
		
		int index1 = emwflRuleConditions.indexOf(emwflRuleCondition1);
		int index2 = emwflRuleConditions.indexOf(emwflRuleCondition2);
		int index3 = emwflRuleConditions.indexOf(emwflRuleCondition3);
		
		assertTrue(index3 > index2);
		assertTrue(index2 > index1);

		emwflRuleCondition1.delete();
		emwflRuleCondition2.delete();
		emwflRuleCondition3.delete();
	}
	
	/**
	 * Test that the email workflow rule is persistence
	 */
	@Test
	public void testPersistance() {
		// Commit and reopen a new transaction because some of the other tests
		// may have caused exceptions which set the transaction to be rolled
		// back.
		if (JPA.em().getTransaction().getRollbackOnly())
			JPA.em().getTransaction().rollback();
		else
			JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		AbstractWorkflowRuleCondition emwflRuleCondition = settingRepo.createEmailWorkflowRuleCondition(condition).save();
		
		// Commit and reopen a new transaction.
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		AbstractWorkflowRuleCondition retrieved = settingRepo.findEmailWorkflowRuleCondition(emwflRuleCondition.getId());
		
		assertEquals(emwflRuleCondition.getId(),retrieved.getId());
		assertEquals(emwflRuleCondition.getConditionType(),retrieved.getConditionType());
		
		retrieved.delete();
		
		// Commit and reopen a new transaction.
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
	}
	
	/**
	 * Test that managers have access and other don't.
	 */
	@Test
	public void testAccess() {
		
		context.login(MockPerson.getManager());
		settingRepo.createEmailWorkflowRuleCondition(condition).save().delete();
		
		try {
			context.login(MockPerson.getReviewer());
			settingRepo.createEmailWorkflowRuleCondition(condition).save();
			fail("A reviewer was able to create a new object.");
		} catch (SecurityException se) {
			/* yay */
		}
		context.logout();
	}
	
}