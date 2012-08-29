package org.tdl.vireo.security.impl;

import org.springframework.beans.factory.BeanNameAware;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.PersonRepository;
import org.tdl.vireo.security.AuthenticationMethod;
import org.tdl.vireo.security.AuthenticationResult;
import org.tdl.vireo.security.SecurityContext;

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
	public String beanName;
	public boolean enabled = false;
	public boolean visible = true;
	public String displayName;
	public String displayDescription;
	public boolean allowNewRegistration = false;
	public boolean allowPasswordRecovery = false;
	public boolean allowUpdatePassword = false;
	public boolean allowUpdateProfile = false;
	
	/**
	 * Spring injected dependencies
	 */
	public PersonRepository personRepo;
	public SecurityContext context;

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
	public boolean isVisible() {
		return visible;
	}
	
	/**
	 * Set the visibility of this authentication method.
	 * 
	 * @param visible
	 *            The new value.
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
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
	public boolean getAllowPasswordRecovery() {
		return allowPasswordRecovery;
	}

	/**
	 * 
	 * @param allow
	 *            Whether to allow users to recover their password via email.
	 */
	public void setAllowPasswordRecovery(boolean allow) {
		this.allowPasswordRecovery = allow;
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
	
	/**
	 * An abstract implementation of Implicit authentication method.
	 */
	public abstract static class AbstractImplicitAuthenticationMethod extends AbstractAuthenticationMethodImpl implements AuthenticationMethod.Implicit {
		
		@Override
		public String getFailureMessage(Request request, AuthenticationResult result) {
			return null;
		}
		
		@Override
		public String logout(Request request, String returnURL) {
			return null;
		}
	}
	
	/**
	 * An abstract implementation of Explicit authentication method.
	 */
	public abstract static class AbstractExplicitAuthenticationMethod extends AbstractAuthenticationMethodImpl implements AuthenticationMethod.Explicit {

		/**
		 * Spring injected configuration
		 */
		public String usernameLabel = "Email Address";
		public String authenticationNote = "Please provide your email address and password to login to the system.";

		@Override
		public String getUsernameLabel() {
			return usernameLabel;
		}

		/**
		 * @param label
		 *            Set the username label for this authentication method.
		 */
		public void setUsernameLabel(String label) {
			this.usernameLabel = label;
		}

		@Override
		public String getAuthenticationNote() {
			return authenticationNote;
		}

		/**
		 * @param note
		 *            Set the authentication note for this method.
		 */
		public void setAuthenticationNote(String note) {
			this.authenticationNote = note;
		}
		
	}

}
