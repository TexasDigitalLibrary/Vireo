package org.tdl.vireo.security;

import org.springframework.beans.factory.BeanNameAware;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.PersonRepository;

import play.mvc.Http.Request;

/**
 * This is an abstract authentication method to handle some of the common CRUD
 * work needed to support authentication methods. This abstract implementation
 * of the interface expects most parameters to be injected via Spring for
 * maximum configurability of each method.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 * 
 */
public abstract class AbstractAuthenticationMethodImpl implements
		AuthenticationMethod, BeanNameAware {

	/**
	 * Spring injected configuration for this authentication method.
	 */
	protected String beanName;
	protected boolean enabled = false;
	protected String displayName;
	protected String displayDescription;
	protected boolean allowNewRegistration = false;
	protected boolean allowUpdatePassword = false;
	protected boolean allowUpdateProfile = false;
	
	/**
	 * Spring injected dependencies
	 */
	protected PersonRepository personRepo;
	protected SecurityContext context;

	@Override
	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	@Override
	public String getBeanName() {
		return beanName;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * Enable or disable this authentication method.
	 * 
	 * @param enabled
	 *            The new value.
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * @param displayName
	 *            The publicly displayable name of this authentication method.
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	@Override
	public String getDisplayDescription() {
		return displayDescription;
	}

	/**
	 * 
	 * @param displayDescription
	 *            The publicly displayable description of why a user should
	 *            select this authentication method if there are multiple to
	 *            choose from.
	 */
	public void setDisplayDescription(String displayDescription) {
		this.displayDescription = displayDescription;
	}

	@Override
	public boolean getAllowNewRegistration() {
		return allowNewRegistration;
	}

	/**
	 * 
	 * @param allow
	 *            Whether to allow new users to register from this method.
	 */
	public void setAllowNewRegistration(boolean allow) {
		this.allowNewRegistration = allow;
	}

	@Override
	public boolean getAllowUpdatePassword(Request request, Person person) {
		return allowUpdatePassword;
	}

	/**
	 * 
	 * @param allow
	 *            Wheather to allow users to update their password from this
	 *            method.
	 */
	public void setAllowUpdatePassword(boolean allow) {
		this.allowUpdatePassword = allow;
	}

	@Override
	public boolean getAllowUpdateProfile(Request request, Person person) {
		return allowUpdateProfile;
	}

	/**
	 * 
	 * @param allow
	 *            Wheather to allow users to update their profile from this
	 *            method.
	 */
	public void setAllowUpdateProfile(boolean allow) {
		this.allowUpdateProfile = allow;
	}
	
	/**
	 * @param personRepo
	 *            Inject the person repository dependency
	 */
	public void setPersonRepository(PersonRepository personRepo) {
		this.personRepo = personRepo;
	}

	/**
	 * @param context
	 *            Inject the security context dependency
	 */
	public void setSecurityContext(SecurityContext context) {
		this.context = context;
	}

	@Override
	public void personCreated(Request request, Person person) {
		// Do nothing.
	}

	@Override
	public void personUpdated(Request request, Person person) {
		// Do nothing
	}

	public AuthenticationResult authenticate(String username, String password,
			Request request) {
		// If you're implementing an explicit method you must provide an implementation.
		
		throw new SecurityException("Explicit authentication is not supported by this method.");
	}

	public String startAuthentication(Request request, String returnURL) {
		// If you're implementing an implicit method you must provide an implementation.

		throw new SecurityException("Implicit authentication is not supported by this method.");
	}

	public AuthenticationResult authenticate(Request request) {
		// If you're implementing an implicit method you must provide an implementation.
		
		throw new SecurityException("Implicit authentication is not supported by this method.");
	}
}
