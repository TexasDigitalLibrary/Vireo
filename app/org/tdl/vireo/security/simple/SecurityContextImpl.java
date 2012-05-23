package org.tdl.vireo.security.simple;

import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.RoleType;
import org.tdl.vireo.security.SecurityContext;

/**
 * Simple ThreadLocal-backed Security context.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class SecurityContextImpl implements SecurityContext {

	
	public ThreadLocal<Person> personLocal = new ThreadLocal<Person>();
	
	@Override
	public void login(Person person) {
		personLocal.set(person);
	}

	@Override
	public void logout() {
		personLocal.set(null);
	}

	@Override
	public Person getPerson() {
		return personLocal.get();
	}

	/**
	 * A sort of private method to return the role of the current user. Should
	 * this be pushed up to the interface level?
	 * 
	 * @return The current user's role or null.
	 */
	public RoleType getRole() {
		if (personLocal.get() != null)
			return personLocal.get().getRole();
		return null;
	}
	
	@Override
	public boolean isAdministrator() {
		RoleType role = getRole();
		if (role == RoleType.ADMINISTRATOR)
			return true;
		return false;
	}

	@Override
	public boolean isManager() {
		RoleType role = getRole();
		if (role == RoleType.ADMINISTRATOR ||
			role == RoleType.MANAGER)
			return true;
		return false;
	}

	@Override
	public boolean isReviewer() {
		RoleType role = getRole();
		if (role == RoleType.ADMINISTRATOR ||
			role == RoleType.MANAGER ||
			role == RoleType.REVIEWER)
			return true;
		return false;
	}

	@Override
	public boolean isStudent() {
		RoleType role = getRole();
		if (role == RoleType.ADMINISTRATOR ||
			role == RoleType.MANAGER ||
			role == RoleType.REVIEWER ||
			role == RoleType.STUDENT)
			return true;
		return false;
	}

	@Override
	public boolean isAuthenticated() {
		return personLocal.get() != null;
	}

}
