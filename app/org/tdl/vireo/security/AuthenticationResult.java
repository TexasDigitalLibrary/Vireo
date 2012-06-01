package org.tdl.vireo.security;

/**
 * 
 * The result of an authentication method's attempt.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public enum AuthenticationResult {

	/** The authentication was completely successfull */
	SUCCESSFULL,

	/**
	 * The authentication attempt was missing credentials, such as: username,
	 * password, certificate, or shibboleth attributes
	 */
	MISSING_CREDENTIALS,

	/**
	 * The authentication attempt contained credentials but they could not be
	 * validated. I.e. the username/password pair did not match any existing
	 * users, or the certificate was invalid.
	 */
	BAD_CREDENTIALS,

	/**
	 * Catch all error flag for an unforeseen error.
	 */
	UNKNOWN_FAILURE

}
