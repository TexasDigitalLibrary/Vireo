package org.tdl.vireo.security.impl;

import java.util.EmptyStackException;
import java.util.Stack;

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
	public ThreadLocal<Stack<Boolean>> authorizationStateHistory = new ThreadLocal<Stack<Boolean>>();
	
	@Override
	public void login(Person person) {
		personLocal.set(person);
		
		// Clear any previous history state, and turn the authorization state on.
		resetAuthorizationStack();
		authorizationStateHistory.get().push(true);
	}

	@Override
	public void logout() {
		personLocal.set(null);
		resetAuthorizationStack();
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

	@Override
	public void turnOffAuthorization() {
		
		if (authorizationStateHistory.get() == null) 
			resetAuthorizationStack();
		authorizationStateHistory.get().push(false);
	}

	@Override
	public void restoreAuthorization() {
		
		if (authorizationStateHistory.get() == null || authorizationStateHistory.get().size() == 0)
			throw new IllegalStateException("Unbalanced authorization state, each call to turnOffAuthorization() *must* be paired with exactly one restoreAuthorization()");
		
		authorizationStateHistory.get().pop();
	}

	@Override
	public boolean isAuthorizationActive() {
		try {
			return authorizationStateHistory.get().peek();
		} catch (EmptyStackException ese) {
			return true;
		}
	}

	
	private void resetAuthorizationStack() {
		if (authorizationStateHistory.get() == null) {
			authorizationStateHistory.set(new Stack<Boolean>());
		} else {
			authorizationStateHistory.get().clear();
		}
	}
}
