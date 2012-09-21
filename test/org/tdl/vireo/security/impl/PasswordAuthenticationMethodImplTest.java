package org.tdl.vireo.security.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.PersonRepository;
import org.tdl.vireo.model.RoleType;
import org.tdl.vireo.security.AuthenticationMethod;
import org.tdl.vireo.security.AuthenticationResult;
import org.tdl.vireo.security.SecurityContext;

import play.db.jpa.JPA;
import play.modules.spring.Spring;
import play.test.UnitTest;

/**
 * Test simple password based authentication against local accounts.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class PasswordAuthenticationMethodImplTest extends UnitTest {
	
	/* Dependencies */
	public static SecurityContext context = Spring.getBeanOfType(SecurityContext.class);
	public static PersonRepository personRepo = Spring.getBeanOfType(PersonRepository.class);
	public static AuthenticationMethod.Explicit method = Spring.getBeanOfType(PasswordAuthenticationMethodImpl.class);

	// predefined persons to test with.
	public Person person1;
	public Person person2;
	
	/**
	 * Create new persons before each test.
	 */
	@Before
	public void setup() {
		// Setup two user accounts;
		context.turnOffAuthorization();
		person1 = personRepo.createPerson("netid", "email@email.com", "first", "last", RoleType.NONE).save();
		person2 = personRepo.createPerson("other", "other@email.com", "other", "another", RoleType.NONE).save();
		
		person1.setPassword("password");
		person2.setPassword("secret");
		
		person1.save();
		person2.save();
		context.restoreAuthorization();
	}
	
	/**
	 * Cleanup all previously created persons after each test.
	 */
	@After
	public void cleanup() {
		// Clean everything up.
		context.turnOffAuthorization();
		personRepo.findPerson(person1.getId()).delete();
		personRepo.findPerson(person2.getId()).delete();
		
		context.restoreAuthorization();
		context.logout();
		JPA.em().getTransaction().rollback();
		JPA.em().getTransaction().begin();
	}
	
	/**
	 * Test all the positives cases that should result in someone successfully
	 * authenticating.
	 */
	@Test
	public void testPositiveCases() {
		
		// Person 1 by netid
		AuthenticationResult result = method.authenticate("netid", "password", null);
		
		assertEquals(AuthenticationResult.SUCCESSFULL, result);
		assertNotNull(context.getPerson());
		assertEquals(person1,context.getPerson());
		
		context.logout();
		
		// Person 2 by netid
		result = method.authenticate("other", "secret", null);
		
		assertEquals(AuthenticationResult.SUCCESSFULL, result);
		assertNotNull(context.getPerson());
		assertEquals(person2,context.getPerson());
		
		// Person 1 by email
		result = method.authenticate("email@email.com", "password", null);
		
		assertEquals(AuthenticationResult.SUCCESSFULL, result);
		assertNotNull(context.getPerson());
		assertEquals(person1,context.getPerson());
		
		context.logout();
		
		// Person 2 by email
		result = method.authenticate("other@email.com", "secret", null);
		
		assertEquals(AuthenticationResult.SUCCESSFULL, result);
		assertNotNull(context.getPerson());
		assertEquals(person2,context.getPerson());
	}
	
	/**
	 * Test most of the negative cases that should result in failures.
	 */
	@Test
	public void testNegativeCases() {
		
		// Wrong password
		AuthenticationResult result = method.authenticate("netid", "not a valid password", null);
		
		assertEquals(AuthenticationResult.BAD_CREDENTIALS, result);
		assertNull(context.getPerson());
		
		
		// Another's password
		result = method.authenticate("netid", "secret", null);
		
		assertEquals(AuthenticationResult.BAD_CREDENTIALS, result);
		assertNull(context.getPerson());
		
		
		// Wrong netid/email
		result = method.authenticate("doesnotexist", "password", null);
		
		assertEquals(AuthenticationResult.BAD_CREDENTIALS, result);
		assertNull(context.getPerson());
	}
	
	/**
	 * Test what happens when invalid input is given.
	 */
	@Test
	public void testInvalidInput() {
		
		assertEquals(AuthenticationResult.MISSING_CREDENTIALS, method.authenticate(null, "password", null));
		assertNull(context.getPerson());

		assertEquals(AuthenticationResult.MISSING_CREDENTIALS, method.authenticate("", "password", null));
		assertNull(context.getPerson());

		assertEquals(AuthenticationResult.MISSING_CREDENTIALS, method.authenticate("netid", null, null));
		assertNull(context.getPerson());
	}
	
	/**
	 * Test what happens when the user does not have a password at all.
	 */
	@Test
	public void testNoPasswordCase() {
		context.turnOffAuthorization();
		person1.setPassword(null);
		person1.save();
		context.restoreAuthorization();
		
		AuthenticationResult result = method.authenticate("netid", "password", null);
		assertEquals(AuthenticationResult.BAD_CREDENTIALS, result);
		assertNull(context.getPerson());
		
		result = method.authenticate("netid", "", null);
		assertEquals(AuthenticationResult.BAD_CREDENTIALS, result);
		assertNull(context.getPerson());

		result = method.authenticate("netid", null, null);
		assertEquals(AuthenticationResult.MISSING_CREDENTIALS, result);
		assertNull(context.getPerson());
	}

}
