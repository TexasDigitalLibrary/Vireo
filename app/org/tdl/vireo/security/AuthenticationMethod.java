package org.tdl.vireo.security;

import org.tdl.vireo.model.Person;

import play.mvc.Http.Request;
import play.mvc.Router.ActionDefinition;

/**
 * This is the generalized authentication interface for Vireo. Create an
 * implementation of either the Implicit, or Explicit inner interfaces to
 * provide a new authentication method for Vireo. All methods fall into two
 * categories of authentication:
 * 
 * <em>Explicit:</em> This is the traditional username and password pair. The
 * user presents their credentials and then they are validated by a data source
 * to either be valid credentials or are rejected. The key features is that the
 * end-user is presented with a challenge (aka enter your username and password)
 * which is then either accepted or denied. In this case the user interacts with
 * a Vireo supplied username/password page, and possible a new registration page
 * depending on how the method is configured.
 * 
 * <em>Implicit:</em> This method is employed when users are authenticated via
 * an external service. This could either be an X.509 certificate that the
 * user's browser presents, shibboleth, or CAS. In all of these cases we don't
 * need to verify who the user is an external trusted service is going to tell
 * us who this person is. In this case the user dosn't see any user interface on
 * Vireo's part, instead they are just transparently authenticated.
 * 
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public interface AuthenticationMethod {

	/**
	 * @return The technical Spring Bean Name of this authentication method so
	 *         that the UI layer can identify this method individually. In
	 *         almost all cases implementors should obtain this by extending
	 *         from the Spring-based BeanNameAware interface.
	 */
	public String getBeanName();
	
	
	/**
	 * @return Whether this authentication method is currently enabled. This
	 *         flag allows for multiple authentication methods to be defined
	 *         within the system, but some may be inoperable until enabled.
	 */
	public boolean isEnabled();
	
	/**
	 * @return Whether this authentication method is visible. Authentication
	 *         methods may enabled (i.e. can be used) but not displayed to users
	 *         as an option. When methods are enabled but not visible the user
	 *         would need to know the correct login url to go directly to the
	 *         login page for that method. This is useful for situations where
	 *         you want all users to go one way, but want to allow a special
	 *         case for administrators to login specially.
	 */
	public boolean isVisible();
	

	/**
	 * @return The publicly displayable name of this authentication method. If
	 *         there are multiple methods available then the user may be asked
	 *         to select which one to use, when that happens this name will be
	 *         displayed in that list. It is highly recommended that this value
	 *         should be configurable coming from either spring's configuration
	 *         or the application.conf.
	 */
	public String getDisplayName();

	/**
	 * @return A publicly displayable description about why a user should select
	 *         this authentication method. It will be displayed along with the
	 *         method's name allow the user to select between multiple
	 *         authentication methods. It is highly recommended that this value
	 *         should be configurable coming from either spring's configuration
	 *         or the application.conf.
	 */
	public String getDisplayDescription();

	/**
	 * Some authentication methods allow for new users to be registered. If this
	 * is the case then they will be asked for an email address, the address
	 * will then be verified with an email token. After verifying their address
	 * the user will be able to fill out a basic profile form and create a new
	 * person object. Once the user has completed that form all authentication
	 * methods will receive a registration() callback to be notified that a new
	 * user has registered.
	 * 
	 * @return True if this method allows new user registrations.
	 */
	public boolean getAllowNewRegistration();
	
	/**
	 * Some authentication methods allow for existing users to recover their
	 * password based upon an email verification. If this is allowed then users
	 * will be able to change their password after verifying their identity
	 * based upon email.
	 * 
	 * 
	 * @return True if this method allows users to recover their password via
	 *         email.
	 */
	public boolean getAllowPasswordRecovery();

	/**
	 * Does this authentication method allow for users to update their password?
	 * 
	 * @param request
	 *            The HTTP request object.
	 * @param person
	 *            The person in question.
	 * @return True if this method allows this user to update their password,
	 *         otherwise false.
	 */
	public boolean getAllowUpdatePassword(Request request, Person person);

	/**
	 * Does this authentication method allow for users to update their profile
	 * information, i.e. firstName, lastName, email address, etc?
	 * 
	 * @param request
	 *            The HTTP request object
	 * @param person
	 *            The person in question
	 * @return True if this method allows this user to update their profile
	 *         information, otherwise false.
	 */
	public boolean getAllowUpdateProfile(Request request, Person person);

	/**
	 * Callback notification when a new user is registered. If any
	 * authentication method allows new user registration then this notification
	 * is called on all authentication methods. This allows for external sources
	 * to be updated or verified when the user is being created.
	 * 
	 * @param request
	 *            The HTTP request object
	 * @param person
	 *            The person being created.
	 */
	public void personCreated(Request request, Person person);

	/**
	 * Callback notification when an existing user has updated their profile
	 * information or password. If any authentication method allows users to
	 * update their password or profile then when this occurs this callback
	 * notification will be called on all authentication methods. This allows
	 * for external sources to be updated or verified when their personal
	 * information is updated.
	 * 
	 * @param request
	 *            The HTTP request object
	 * @param person
	 *            The person who's profile or password has been updated.
	 */
	public void personUpdated(Request request, Person person);

	/**
	 * Implicit authentication method used when external services authenticate
	 * users. Implementors must implement either Implicit or Explicit
	 * authentication interfaces.
	 */
	public interface Implicit extends AuthenticationMethod {

		/**
		 * Start the implicit authentication process. If the process requires
		 * the user to be redirected to an external service then the URL where
		 * the user should be redirected to is to be returned. After users have
		 * successfully authenticated with the external service they must return
		 * to vireo using the returnURL provided.
		 * 
		 * If this method does not require redirection, such as in the case of
		 * X.509 certificates then null should be returned. In that case the
		 * authentication method will be immediately called.
		 * 
		 * 
		 * @param request
		 *            The HTTP request object.
		 * @param returnAction
		 *            The Play action to handle return authentication. 
		 * @return The URL to redirect the user initiating authentication, or
		 *         null if no redirection is required.
		 */
		public String startAuthentication(Request request, ActionDefinition returnAction);

		/**
		 * Authenticate this request. If the is successfully then the user will
		 * be logged into Vireo's SecurityContext, and an
		 * <em>AuthenticationResult.SUCCESSFULL</em> result.
		 * 
		 * If authentication is anything other than successful then one of the
		 * failure result codes should be returned.
		 * 
		 * <em>MISSING_CREDENTIALS</em>: Some or all of the required credentials
		 * were missing from the authentication request.
		 * 
		 * <em>BAD_CREDENTIALS</em>: All the required credentials were available
		 * but they were not be able to be verified as valid.
		 * 
		 * <em>UNKNOWN_FAILURE</em>: This is the catch all case for unforeseen
		 * errors conditions.
		 * 
		 * @param request
		 *            The HTTP request to be authenticated.
		 * 
		 * @return The result of authentication.
		 */
		public AuthenticationResult authenticate(Request request);
		
		/**
		 * After an authentication failure from an implicit login an error
		 * message will be displayed. This message may be customized by the
		 * authentication method to provide additional details about why the
		 * error occurred and maybe some hits as to how to fix it.
		 * 
		 * For example the shibboleth plugin may list the headers that it
		 * received aiding the user, vireo administrator, and shibboleth
		 * administrator in debuging what the problem is.
		 * 
		 * If this method returns null then the UI will display a generalized
		 * message based upon the Authentication Result provided.
		 * 
		 * @param request
		 *            The same request that caused an Authentication failure.
		 * @return An additional error message, or null if there is none.
		 */
		public String getFailureMessage(Request request,
				AuthenticationResult result);

		/**
		 * Logout the user out of this implicit authentication. This may require
		 * redirecting the user off to an external service to complete the
		 * logout. If so this method should return the URL to redirect the user
		 * to, and the included returnURL is where the user should be sent after
		 * successfully logging out.
		 * 
		 * If no redirection is required then null should be returned. This will
		 * still cause the user to be logged out of the application and their
		 * session cleared.
		 * 
		 * @param request
		 *            The HTTP request
		 * @param returnURL
		 *            The URL where the user should be redirected to back to the
		 *            Application after logging.
		 * @return The URL to initiate a logout request, or null if no
		 *         redirection is required.
		 */
		public String logout(Request request, String returnURL);
	}

	/**
	 * Explicit authentication method used when Vireo challenges the user for a
	 * username and password. Implementors must implement either Implicit or
	 * Explicit authentication interfaces.
	 */
	public interface Explicit extends AuthenticationMethod {

		/**
		 * @return The publicly displayable label for the username field. Some
		 *         methods will use "Email Address", others may say "NetId", or
		 *         use local campus terminology that users will recognize.
		 */
		public String getUsernameLabel();

		/**
		 * @return A note to display when asking for the user to provide their
		 *         username and password. This note can explain any special
		 *         instructions for using this authentication method. However it
		 *         should generally ask the user to provide their
		 *         username/password in the form below. The value may contain
		 *         HTML markup.
		 */
		public String getAuthenticationNote();
		
		/**
		 * Authenticate these credentials. If valid credentials have been
		 * presented then the user will be logged into Vireo's SecurityContext,
		 * and a <em>AuthenticationResult.SUCCESSFULL</em> result.
		 * 
		 * Note: The interface does not include a security realm, if that is
		 * needed for this authentication method it should be combined with the
		 * username, thus REALM\\username or something similar to how microsoft
		 * handles the case.
		 * 
		 * If authentication is anything other than successful then one of the
		 * failure result codes should be returned.
		 * 
		 * <em>MISSING_CREDENTIALS</em>: Either the username or password were
		 * missing.
		 * 
		 * <em>BAD_CREDENTIALS</em>: The username and password are present but
		 * they do not match any valid user.
		 * 
		 * <em>UNKNOWN_FAILURE</em>: This is the catch all case for unforeseen
		 * errors conditions.
		 * 
		 * @param username
		 *            The username to be authenticated.
		 * @param password
		 *            The password to be authenticated.
		 * @param request
		 *            The HTTP request.
		 * @return
		 */
		public AuthenticationResult authenticate(String username,
				String password, Request request);

	}
}
