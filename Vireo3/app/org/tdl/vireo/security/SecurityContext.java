package org.tdl.vireo.security;

import org.tdl.vireo.model.Person;

/**
 * This singleton interface keeps track of the current security context that
 * Vireo is operating within.
 * 
 * When a user is authenticated by the UI the security context is notified via
 * the login() method. The login will be kept track of (using a
 * ThreadLocal-based mechanism) of the currently authenticated user. This means
 * that whenever a component needs to access the currently authenticated user
 * they can just query the current security context.
 * 
 * Examples:
 * 
 * SecurityContext context = Spring.getBeanOfType(SecurityContext.class);
 * 
 * Person authenticated = context.getPerson();
 * 
 * Or if you just need to make a quick decision based upon the person's current
 * role you can ask one of the isRole methods such as:
 * 
 * if (context.isAdministrator()) { // Do something very special. }
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public interface SecurityContext {

	/**
	 * After a user's credentials have been verified log the user into the
	 * current security context. This allows other vireo components to access
	 * the currently authenticated user easily.
	 * 
	 * @param person
	 *            The authenticated person.
	 */
	public void login(Person person);
	
	/**
	 * Logout the current user, effectively resetting the security context.
	 */
	public void logout();	

	/**
	 * @return The currently authenticated person, or null if none one is
	 *         authenticated.
	 */
	public Person getPerson();

	/**
	 * @return true if the current user has the role ADMINISTRATOR
	 */
	public boolean isAdministrator();

	/**
	 * @return true if the current user has the role MANAGER or above.
	 */
	public boolean isManager();

	/**
	 * @return true if the current user has the role REVIEWER or above.
	 */
	public boolean isReviewer();

	/**
	 * @return true if the current user has the role STUDENT or above.
	 */
	public boolean isStudent();

	/**
	 * @return true if there is a currently authenticated person.
	 */
	public boolean isAuthenticated();
	
	/**
	 * Turn off the authorization system. The caller *must* always pair this
	 * call with a restoration of authorization.
	 */
	public void turnOffAuthorization();

	/**
	 * Restore the authorization state to it's original state before the paired
	 * turnOffAuthorizations was called.
	 */
	public void restoreAuthorization();

	/**
	 * @return whether authorization checks have been deactivated.
	 */
	public boolean isAuthorizationActive();
}
