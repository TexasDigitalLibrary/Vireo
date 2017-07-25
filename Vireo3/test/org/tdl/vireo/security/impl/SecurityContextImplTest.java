package org.tdl.vireo.security.impl;

import org.junit.Test;
import org.tdl.vireo.model.MockPerson;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.RoleType;

import play.modules.spring.Spring;
import play.test.UnitTest;

/**
 * Test the simple security context impl. Since it's really hard we're going to
 * skip the multithreaded aspects of this class and just test it as a single
 * thread.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 * 
 */
public class SecurityContextImplTest extends UnitTest {

	public static SecurityContextImpl context = Spring
			.getBeanOfType(SecurityContextImpl.class);

	/**
	 * Test that a user can login to the security context.
	 */
	@Test
	public void testLogin() {
		Person person = new MockPerson();

		context.login(person);

		assertEquals(person, context.getPerson());
	}

	/**
	 * Test that a user can logout.
	 */
	@Test
	public void testLogout() {
		Person person = new MockPerson();

		context.login(person);

		assertEquals(person, context.getPerson());

		context.logout();

		assertNull(context.getPerson());
	}

	/**
	 * Test that all the role shortcuts work properly
	 */
	@Test
	public void testRoles() {
		MockPerson person = new MockPerson();
		person.role = RoleType.ADMINISTRATOR;

		context.login(person);
		assertTrue(context.isAdministrator());
		assertTrue(context.isManager());
		assertTrue(context.isReviewer());
		assertTrue(context.isStudent());
		assertTrue(context.isAuthenticated());

		person.role = RoleType.MANAGER;
		assertFalse(context.isAdministrator());
		assertTrue(context.isManager());
		assertTrue(context.isReviewer());
		assertTrue(context.isStudent());
		assertTrue(context.isAuthenticated());

		person.role = RoleType.REVIEWER;
		assertFalse(context.isAdministrator());
		assertFalse(context.isManager());
		assertTrue(context.isReviewer());
		assertTrue(context.isStudent());
		assertTrue(context.isAuthenticated());

		person.role = RoleType.STUDENT;
		assertFalse(context.isAdministrator());
		assertFalse(context.isManager());
		assertFalse(context.isReviewer());
		assertTrue(context.isStudent());
		assertTrue(context.isAuthenticated());

		person.role = RoleType.NONE;
		assertFalse(context.isAdministrator());
		assertFalse(context.isManager());
		assertFalse(context.isReviewer());
		assertFalse(context.isStudent());
		assertTrue(context.isAuthenticated());

		context.logout();
		assertFalse(context.isAdministrator());
		assertFalse(context.isManager());
		assertFalse(context.isReviewer());
		assertFalse(context.isStudent());
		assertFalse(context.isAuthenticated());
	}
	
	/**
	 * Test that we can turn off authorizations and restore them.
	 */
	@Test
	public void testTurningOffAuthorizationSystem() {
		
		context.logout();
		assertTrue(context.isAuthorizationActive());		
		context.turnOffAuthorization();
		assertFalse(context.isAuthorizationActive());
		context.turnOffAuthorization();
		assertFalse(context.isAuthorizationActive());
		context.restoreAuthorization();
		assertFalse(context.isAuthorizationActive());
		context.restoreAuthorization();
		assertTrue(context.isAuthorizationActive());	
		
		
		// Test that the authoziation state is reset after a login.
		context.turnOffAuthorization();
		assertFalse(context.isAuthorizationActive());
		context.login(MockPerson.getStudent());
		assertTrue(context.isAuthorizationActive());
		
		
	}

}
