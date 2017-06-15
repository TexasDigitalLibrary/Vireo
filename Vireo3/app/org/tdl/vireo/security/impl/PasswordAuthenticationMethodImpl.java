package org.tdl.vireo.security.impl;

import org.tdl.vireo.model.Person;
import org.tdl.vireo.security.AuthenticationMethod;
import org.tdl.vireo.security.AuthenticationResult;

import play.mvc.Http.Request;

/**
 * The most basic authentication method for local password-based accounts. No
 * external sources are evaluated for authentication. The username may either be
 * a netid/username or an email address.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class PasswordAuthenticationMethodImpl extends
		AbstractAuthenticationMethodImpl.AbstractExplicitAuthenticationMethod implements
		AuthenticationMethod.Explicit {

	/**
	 * Password based authentication.
	 * 
	 * First users will be identified by their netid, if that lookup fails then
	 * we fail back to see if the username is an email address. Once the person
	 * is identified the password is verified. If all that is succesfull then
	 * the Person is logged into the SecurityContext.
	 */
	public AuthenticationResult authenticate(String username, String password,
			Request request) {

		if (username == null || username.length() == 0 || password == null)
			return AuthenticationResult.MISSING_CREDENTIALS;

		// First look a person up by their netid/username
		Person person = personRepo.findPersonByNetId(username);
		if (person == null) {
			// Fall back and see if the username is an email address.
			person = personRepo.findPersonByEmail(username);
		}

		if (person == null)
			return AuthenticationResult.BAD_CREDENTIALS;

		if (!person.validatePassword(password))
			return AuthenticationResult.BAD_CREDENTIALS;

		// Authenticate the person
		context.login(person);
		return AuthenticationResult.SUCCESSFULL;
	}

}
