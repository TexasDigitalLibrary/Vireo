package controllers;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.PersonRepository;
import org.tdl.vireo.model.RoleType;
import org.tdl.vireo.model.SettingsRepository;
import org.tdl.vireo.security.AuthenticationMethod;
import org.tdl.vireo.security.AuthenticationResult;
import org.tdl.vireo.security.SecurityContext;
import org.tdl.vireo.services.EmailService;
import org.tdl.vireo.services.EmailService.TemplateParameters;
import org.tdl.vireo.services.SystemEmailTemplateService;
import org.tdl.vireo.services.impl.SystemEmailTemplateServiceImpl;

import play.Logger;
import play.Play;
import play.libs.Codec;
import play.libs.Crypto;
import play.modules.spring.Spring;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Finally;
import play.mvc.Router;
import play.mvc.Router.ActionDefinition;

/**
 * Authentication controller provides, you guessed it, user authentication!
 * Tada! In addition to the normal login and logout stuff the controller also
 * provides new user registration, profile management, and forgot password
 * facilities.
 * 
 * The authentication facilities use Vireo's AuthenticationMethod interface to
 * provide plugable authentication mechanisms. You can have just method, or you
 * can optionally provide multiple methods from which the user must pick the
 * best one for them. The methods are broken down into two types: Implicit or
 * Explicit. Implicit authentication methods use an external service such as
 * Shibboleth or CAS while Explicit authentication methods are the traditional
 * username/password just as local accounts or LDAP. This controller supports
 * both types of methods.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class Authentication extends Controller {
	
	// Spring dependencies
	public static SecurityContext context = Spring.getBeanOfType(SecurityContext.class);
	public static EmailService emailService = Spring.getBeanOfType(EmailService.class);
	public static SystemEmailTemplateService systemEmailService = Spring.getBeanOfType(SystemEmailTemplateService.class);
	
	public static PersonRepository personRepo = Spring.getBeanOfType(PersonRepository.class);
	public static SettingsRepository settingRepo = Spring.getBeanOfType(SettingsRepository.class);
	
	// Constants
	public static final String REGISTRATION_TEMPLATE = "SYSTEM New User Registration";
	public static final String RECOVER_TEMPLATE = "SYSTEM Verify Email Address";

	
	
	/**
	 * This method is run before every action in any controller (with the
	 * @With() annotation!). This method checks to see if someone has been
	 * previously authenticated and if so logs them in to the current security
	 * context. When someone is logged in their unique person id will be stored
	 * on the session as "personId". So this method simply checks that parameter
	 * and looks the user in the corresponding repository.
	 * 
	 * In addition if the action is annotated with a @Security() annotation then
	 * this method will perform those minimal security checks. So if the
	 * annotation says that to access the action all users must be at least a
	 * REVIEWER then this method will check that. There are two resulting
	 * conditions, either there is no currently authenticated user or they do
	 * not meet the required access level. In the first case the user would be
	 * sent off to login, remembering where to go after completing
	 * authentication. For the other case where they are already logged in but
	 * don't meet the conditions then they will receive an unauthorized message.
	 */
	@Before(unless = { "loginList", "loginMethod", "loginReturn" })
	public static void securityCheck() {
		
		// Check if we have a personId stored on the session.
		Long personId = null;
		try {
			personId = Long.valueOf(session.get("personId"));
		} catch (RuntimeException re) { /* ignore */ }
		
		// Log the current user in for this web request.
		Person person = null;
		if (personId != null) {
			person = personRepo.findPerson(personId);			
			context.login(person);
		}
		
		// Check if there are any security restrictions for this action.
		Security security = getActionAnnotation(Security.class);
		if (security == null)
			security = getControllerInheritedAnnotation(Security.class);
		
		if (security != null) {
			// This action has been annotated with a restriction.
			
			if (person == null) {
				// No one is logged in, so let's save where we are and redirect them to authenticate.
				flash.put("url", "GET".equals(request.method) ? request.url : null);
				loginList();
			}
			
			RoleType expectedRole = security.value();
			RoleType actualRole = person.getRole();
			
			if (expectedRole.ordinal() > actualRole.ordinal())
				forbidden();
		}
	}

	/**
	 * This just makes sure that the user is logged out of the current thread in
	 * all cases. Even if there is an exception the thread is closed.
	 */
	@Finally
	public static void securityCleanup() {
		context.logout();
	}
	
	
	/**
	 * Provide a list of authentication methods for the user to choose. If there
	 * is only one login method configured then the user is shuttled off to that
	 * authentication method without any fuss.
	 */
	public static void loginList() {
		flash.keep("url");	
		
		// Get the list of all our authentication methods
		List<AuthenticationMethod> enabledMethods = getEnabledAuthenticationMethods();
		
		// Fail if the admin forgot to define ANY authentication methods.
		if (enabledMethods.size() == 0)
			error("No authentication methods are defined or enabled.");
		
		// If there is only one option skip the list and go straight there.
		if (enabledMethods.size() == 1) {
			Map<String,Object> routeArgs = new HashMap<String,Object>();
			routeArgs.put("method",enabledMethods.get(0).getBeanName());
			redirect("Authentication.loginMethod", routeArgs);
		}
				
		render(enabledMethods);
	}
	
	/**
	 * This is the login page for a particular authentication method. The user
	 * will arrive from the loginList method above. There are two cases for this
	 * method either the method is Implicit or Explicit. In the Explicit case
	 * the user is presented with a login screen and when they provide a correct
	 * username/password pair they are logged in. In the Implicit case the user
	 * is redirected to authenticate via an external service.
	 * 
	 * @param methodName
	 *            The spring bean name of the authentication method.
	 */
	public static void loginMethod(String methodName) {
		flash.keep("url");	
		
		notFoundIfNull(methodName);
		if (params.get("submit_cancel") != null)
			Application.index();
		
		// Look up the authentication method and make sure it's valid.
		AuthenticationMethod method = null;
		try {
			method = (AuthenticationMethod) Spring.getBean(methodName);
		} catch (Throwable t) {
			notFound();
		}
		if (!method.isEnabled())
			error("Authentication method '"+methodName+"' is not enabled.");
		
		
		if (method instanceof AuthenticationMethod.Implicit) {
			
			// Implicit authentication like CAS or Shibboleth
			
			AuthenticationMethod.Implicit implicitMethod = (AuthenticationMethod.Implicit) method;
			
			Map<String,Object> routeArgs = new HashMap<String,Object>();
			routeArgs.put("methodName",methodName);
			ActionDefinition routeDefinition = Router.reverse("Authentication.loginReturn", routeArgs);
			routeDefinition.absolute();
			String returnURL = routeDefinition.url;
			
			String initiationRedirect = implicitMethod.startAuthentication(request, returnURL);
			
			if (initiationRedirect == null) {
				loginReturn(methodName);
			} else {
				redirect(initiationRedirect);
			}
		} else if (method instanceof AuthenticationMethod.Explicit) {
			
			// Explicit authentication like Local Passwords or LDAP
			
			AuthenticationMethod.Explicit explicitMethod = (AuthenticationMethod.Explicit) method;

			
			if (params.get("submit_login") != null) {
				
				String username = params.get("username");
				String password = params.get("password");
				
				AuthenticationResult result = explicitMethod.authenticate(username, password, request);

				if (AuthenticationResult.SUCCESSFULL == result && context.getPerson() != null) {
					// Log the person in.
					Person person = context.getPerson();
					
					session.put("personId", person.getId());
					session.put("firstName", person.getFirstName());
					session.put("lastName", person.getLastName());
					session.put("displayName", person.getDisplayName());
					
					// Where to go next? If there were trying to go somethere go
					// back there, otherwise go to the root.
					if (flash.get("url") != null) {
						String url = flash.get("url");
						flash.remove("url");
						redirect(url);
					} else {
						redirect("Application.index");
					}
				} 
				validation.addError("login", "The username and/or password does not exist. Please try again.");
			
				render(method,username,password);
			}
			render(method);
		} 
		
		// Neither implicit or explicit
		error("Authentication method '"+methodName+"' is invalid because it is not implicit or explicit.");
	}
	
	/**
	 * This handles returning from external authentication methods which are
	 * implicit (such as shibboleth or cas). If the credentials are valid the
	 * user is logged in, otherwise they are shuttled to another page.
	 * 
	 * @param methodName
	 *            The spring bean name of the implicit authentication method
	 */
	public static void loginReturn(String methodName) {
		flash.keep("url");	
		notFoundIfNull(methodName);
		
		AuthenticationMethod.Implicit method = null;
		try {
			method = (AuthenticationMethod.Implicit) Spring.getBean(methodName);
		} catch (Throwable t) {
			notFound();
		}
		if (!method.isEnabled())
			error("Authentication method '"+methodName+"' is not enabled.");
		
		
		AuthenticationResult result = method.authenticate(request);
		
		if (AuthenticationResult.SUCCESSFULL == result && context.getPerson() != null) {
			// Log the person in.
			Person person = context.getPerson();
			
			session.put("personId", person.getId());
			session.put("firstName", person.getFirstName());
			session.put("lastName", person.getLastName());
			session.put("displayName", person.getDisplayName());
			
			// Where to go next? If there were trying to go somethere go
			// back there, otherwise go to the root.
			if (flash.get("url") != null) {
				String url = flash.get("url");
				flash.remove("url");
				redirect(url);
			} else {
				redirect("Application.index");
			}
		} 
		
		AuthenticationResult missing = AuthenticationResult.MISSING_CREDENTIALS;
		AuthenticationResult bad = AuthenticationResult.BAD_CREDENTIALS;
		AuthenticationResult unknown = AuthenticationResult.UNKNOWN_FAILURE;
		
		render(method, result, missing, bad, unknown);
	}
	
	/**
	 * Handle new account registration. This action has several different parts
	 * to complete the registration process. By design it is very similar to
	 * DSpace's registration process.
	 * 
	 * 1) Display the initial form where a user supplies their email address. If
	 * it's valid and not already used we send an email token to it.
	 * 
	 * 2) Display an email sent message after sending the verification email.
	 * 
	 * 3) Upon return from a link in email ask the user for the required
	 * information: first and last names along with a password. Once validated
	 * create the account.
	 * 
	 * 4) If the return link is not valid, then display a pretty error page
	 * explaining common problems.
	 */
	public static void register() {
		// Bail if we don't allow registration
		if (!isRegistrationEnabled())
			notFound();
		
		// Bail if someone is already logged in.
		if (context.getPerson() != null)
			Authentication.profile();
		
		// Bail if they've clicked cancel.
		if (params.get("submit_cancel") != null)
			Application.index();
		
		// Handle returning after email verification.
		if (params.get("token") != null) {
			String token = params.get("token");
			
			String email = validateToken(token, "reg");
			if (email == null)
				renderTemplate("Authentication/invalidToken.html");
			
			String firstName = params.get("firstname");
			String lastName = params.get("lastname");
			String password1 = params.get("password1");
			String password2 = params.get("password2");
			
			if (params.get("submit_register") != null) {
				
				if (firstName == null || firstName.trim().length() == 0)
					validation.addError("firstname", "First name is required.");
				
				if (lastName == null || lastName.trim().length() == 0)
					validation.addError("lastname", "Last name is required.");
				
				if (password1 == null || password1.trim().length() == 0)
					validation.addError("password1", "Please pick a password.");
				
				if (password1 != null && !password1.equals(password2)) 
					validation.addError("password2", "The passwords do not match.");
				
				if (password1 != null && password1.trim().length() < 6)
					validation.addError("password1", "Please pick a password longer than 6 characters.");
				
				if (!validation.hasErrors()) {
					
					// Create the account.
					context.turnOffAuthorization();
					Person person = personRepo.createPerson(null, email, firstName, lastName, RoleType.STUDENT);
					person.setPassword(password1);
					person.save();
					context.turnOffAuthorization();
					context.login(person);
					
					// Notify all the authentication methods of the new user.
					List<AuthenticationMethod> methods = getEnabledAuthenticationMethods();
					for (AuthenticationMethod method : methods) 
						method.personCreated(request, person);
					
					// Log the person in
					session.put("personId", person.getId());
					session.put("firstName", person.getFirstName());
					session.put("lastName", person.getLastName());
					session.put("displayName", person.getDisplayName());
							
					// Go to the index page.
					Application.index();
				}
			}
			
			renderTemplate("Authentication/registerReturn.html", token, email, firstName, lastName, password1, password2);
		}
		
		// Otherwise handle sending the email verification.
		if (params.get("submit_register") != null) {
			
			String email = params.get("email");
			
			if (email == null || email.trim().length() == 0)
				validation.addError("email", "An email address is required.");
			
			// Check if the address is valid.
			try {
				if (email != null) {
					InternetAddress ia = new InternetAddress(email);
					ia.validate();
				}
			} catch (AddressException ae) {
				validation.addError("email", ae.getMessage());
			}
			
			// Check if the account allready exists
			Person person = personRepo.findPersonByEmail(email);
			if (person != null)
				validation.addError("email", "An account with this email address allready exists.");
			
			if (!validation.hasErrors()) {
				// We're good let's send this off.
				systemEmailService.generateAllSystemEmailTemplates();
				EmailTemplate template = settingRepo.findEmailTemplateByName(REGISTRATION_TEMPLATE);
				
				ActionDefinition action = Router.reverse("Authentication.register");
				action.absolute();
				String token = generateToken(email, "reg");
				
				TemplateParameters params = new TemplateParameters();
				params.REGISTRATION_URL = action.url + "?token="+token;
				
				List<String> recipients = new ArrayList<String>();
				recipients.add(email);
				
				emailService.sendEmail(template, params, recipients, null);
				
				renderTemplate("Authentication/registerSent.html",email);
			}
			
			renderTemplate("Authentication/registerStart.html",email);
		}
		renderTemplate("Authentication/registerStart.html");
	}
	
	/**
	 * Handle recovery of lost accounts. When the user forgets their password
	 * they can use this facility to reset it after verifying their email
	 * address. This action has several different parts to complete the recovery
	 * process. By design it is very similar to DSpace's forgot password
	 * process.
	 * 
	 * 1) Display the initial form where a user supplies their email address. If
	 * there is an account associated with the email address we send an email to
	 * the address containing a special verification token.
	 * 
	 * 2) Display an email sent message after sending the verification email.
	 * 
	 * 3) Upon return from a link in email ask the user to pick a password. for
	 * the required
	 * 
	 * 4) If the return link is not valid, then display a pretty error page
	 * explaining common problems.
	 */
	public static void recover() {
		// Bail if we don't allow registration
		if (!isPasswordRecoveryEnabled())
			notFound();
		
		// Bail if someone is already logged in.
		if (context.getPerson() != null)
			Authentication.profile();
		
		// Bail if they've clicked cancel.
		if (params.get("submit_cancel") != null)
			Application.index();
		
		// Handle returning after email verification.
		if (params.get("token") != null) {
			String token = params.get("token");
			
			String email = validateToken(token, "rec");
			if (email == null)
				renderTemplate("Authentication/invalidToken.html");
			Person person = personRepo.findPersonByEmail(email);
			if (person == null)
				error("Unable to locate person object for email: "+email);
			
			String password1 = params.get("password1");
			String password2 = params.get("password2");
			
			if (params.get("submit_recover") != null) {
				
				if (password1 == null || password1.trim().length() == 0)
					validation.addError("password1", "Please pick a password.");
				
				if (password1 != null && !password1.equals(password2)) 
					validation.addError("password2", "The passwords do not match.");
				
				if (password1 != null && password1.trim().length() < 6)
					validation.addError("password1", "Please pick a password longer than 6 characters.");
				
				if (!validation.hasErrors()) {
					
					// Update the password
					context.login(person);
					person.setPassword(password1);
					person.save();
					
					// Notify all the authentication methods of the change.
					List<AuthenticationMethod> methods = getEnabledAuthenticationMethods();
					for (AuthenticationMethod method : methods) 
						method.personUpdated(request, person);
					
					// Log the person in
					session.put("personId", person.getId());
					session.put("firstName", person.getFirstName());
					session.put("lastName", person.getLastName());
					session.put("displayName", person.getDisplayName());
							
					// Go to the index page.
					Application.index();
				}
			}
			
			renderTemplate("Authentication/recoverReturn.html", token, email, password1, password2);
		}
		
		// Otherwise handle sending the email verification.
		if (params.get("submit_recover") != null) {
			
			String email = params.get("email");
			
			if (email == null || email.trim().length() == 0)
				validation.addError("email", "An email address is required.");
			
			// Check if the address is valid.
			try {
				if (email != null) {
					InternetAddress ia = new InternetAddress(email);
					ia.validate();
				}
			} catch (AddressException ae) {
				validation.addError("email", ae.getMessage());
			}
			
			// Check if the account allready exists
			Person person = personRepo.findPersonByEmail(email);
			if (person == null)
				validation.addError("email", "No account exists with this email address.");
			
			if (!validation.hasErrors()) {
				// We're good let's send this off.
				systemEmailService.generateAllSystemEmailTemplates();
				EmailTemplate template = settingRepo.findEmailTemplateByName(RECOVER_TEMPLATE);
				
				ActionDefinition action = Router.reverse("Authentication.recover");
				action.absolute();
				String token = generateToken(email, "rec");
				
				TemplateParameters params = new TemplateParameters();
				params.REGISTRATION_URL = action.url + "?token="+token;
				
				List<String> recipients = new ArrayList<String>();
				recipients.add(email);
				
				emailService.sendEmail(template, params, recipients, null);
				
				renderTemplate("Authentication/recoverSent.html",email);
			}
			
			renderTemplate("Authentication/recoverStart.html",email);
		}
		renderTemplate("Authentication/recoverStart.html");
	}
	
	/**
	 * View the current user's profile. This is purly informational. However if
	 * configured this person will be able to follow links from this page to
	 * update their profile or their password.
	 */
	@Security(RoleType.NONE)
	public static void profile() {
		
		// Who's logged in.
		Person person = context.getPerson();
		
		// Person profile information.
		String fullName = person.getFullName();
		String email = person.getEmail();
		String firstName = person.getFirstName();
		String lastName = person.getLastName();
		String middleName = person.getMiddleName();
		
		String birthYear = "";
		if (person.getBirthYear() != null)
			birthYear = String.valueOf(person.getBirthYear());
		String currentPhoneNumber = person.getCurrentPhoneNumber();
		String currentPostalAddress = person.getCurrentPostalAddress();
		String permanentPhoneNumber = person.getPermanentPhoneNumber();
		String permanentPostalAddress = person.getPermanentPostalAddress();
		String permanentEmailAddress = person.getPermanentEmailAddress();
		
		// Update Controls
		boolean updateProfile = isUpdateProfileEnabled(person);
		boolean updatePassword = isUpdatePasswordEnabled(person);
		
		
		renderTemplate("Authentication/profile.html", updateProfile, updatePassword, fullName, firstName, email, lastName, middleName, 
				birthYear, currentPhoneNumber, currentPostalAddress, permanentPhoneNumber,
				permanentPostalAddress, permanentEmailAddress);
	}
	
	/**
	 * Update the current user's profile information. Basically all the
	 * information other than their password: First, middle, last names,
	 * addresses, phone numbers, etc.
	 */
	@Security(RoleType.NONE)
	public static void updateProfile() {
		
		Person person = context.getPerson();
		
		if (!isUpdateProfileEnabled(person))
			todo(); // Need to display static form.
		
		if (params.get("submit_cancel") != null)
			Authentication.profile();
		
		// Static content
		String fullName = person.getFullName();
		String email = person.getEmail();
		
		// Dynamic fields
		String firstName = params.get("firstName");
		String lastName = params.get("lastName");
		String middleName = params.get("middleName");
		String birthYear = params.get("birthYear");
		String currentPhoneNumber = params.get("currentPhoneNumber");
		String currentPostalAddress = params.get("currentPostalAddress");
		String permanentPhoneNumber = params.get("permanentPhoneNumber");
		String permanentPostalAddress = params.get("permanentPostalAddress");
		String permanentEmailAddress = params.get("permanentEmailAddress");
		
		if (params.get("submit_update") != null) {
			
			if (firstName == null || firstName.trim().length() == 0)
				validation.addError("firstName", "First name is required.");

			if (lastName == null || lastName.trim().length() == 0)
				validation.addError("firstName", "Last name is required.");
			
			Integer birthYearInt = null;
			if (birthYear != null && birthYear.trim().length() > 0) {
				try {
					birthYearInt = Integer.valueOf(birthYear);
					if (birthYearInt < 1900 || birthYearInt > (new Date().getYear() + 1900))
						validation.addError("birthYear", "Invalid birth year, please use four digits");
				} catch (NumberFormatException nfe) {
					validation.addError("birthYear", "Invalid birth year.");
			}
			}
			
			if (permanentEmailAddress != null && permanentEmailAddress.trim().length() > 0) {
				try {
					new InternetAddress(permanentEmailAddress).validate();
				} catch (AddressException ae) {
					validation.addError("permanentEmailAddress","Invalid permanent email address.");
				}
			}
			
			if (!validation.hasErrors()) {
				person.setFirstName(firstName);
				person.setLastName(lastName);
				person.setMiddleName(middleName);
				person.setBirthYear(birthYearInt);
				person.setCurrentPhoneNumber(currentPhoneNumber);
				person.setCurrentPostalAddress(currentPostalAddress);
				person.setPermanentPhoneNumber(permanentPhoneNumber);
				person.setPermanentPostalAddress(permanentPostalAddress);
				person.setPermanentEmailAddress(permanentEmailAddress);
				person.save();
				
				Authentication.profile();
			}
		} else {
			// First time viewing the form fill out information from the person object.
			firstName = person.getFirstName();
			lastName = person.getLastName();
			middleName = person.getMiddleName();
			if (person.getBirthYear() == null)
				birthYear = "";
			else
				birthYear = String.valueOf(person.getBirthYear());
			currentPhoneNumber = person.getCurrentPhoneNumber();
			currentPostalAddress = person.getCurrentPostalAddress();
			permanentPhoneNumber = person.getPermanentPhoneNumber();
			permanentPostalAddress = person.getPermanentPostalAddress();
			permanentEmailAddress = person.getPermanentEmailAddress();
		}
		
		renderTemplate("Authentication/updateProfile.html", fullName, firstName, email, lastName, middleName, 
				birthYear, currentPhoneNumber, currentPostalAddress, permanentPhoneNumber,
				permanentPostalAddress, permanentEmailAddress);
	}
	
	/**
	 * Update a user's password. This assumes they know their current password,
	 * if they don't then they wouldn't be logged in and could use the forgot
	 * password mechanism.
	 */
	@Security(RoleType.NONE)
	public static void updatePassword() {
		Person person = context.getPerson();
		
		if (!isUpdatePasswordEnabled(person))
			Authentication.profile();
		
		if (params.get("submit_cancel") != null)
			Authentication.profile();
		
		// Static content
		String fullName = person.getFullName();
		
		// Dynamic fields
		String current = params.get("current");
		String password1 = params.get("password1");
		String password2 = params.get("password2");
		
		if (params.get("submit_update") != null) {
			
			if (current == null || current.trim().length() == 0)
				validation.addError("current", "Please provide your current password.");
			else if (!person.validatePassword(current))
				validation.addError("current", "Your current password is not correct.");
			
			
			if (password1 == null || password1.trim().length() == 0)
				validation.addError("password1", "Please pick a new password.");
			
			if (password1 != null && !password1.equals(password2)) 
				validation.addError("password1", "The passwords do not match.");
			
			if (password1 != null && password1.trim().length() < 6)
				validation.addError("password1", "Please pick a new password longer than 6 characters.");
			
			
			if (!validation.hasErrors()) {
				
				person.setPassword(password1);
				person.save();
				
				Authentication.profile();
			}
		}
		
		renderTemplate("Authentication/updatePassword.html", fullName, password1, password2);
	}
	
	/**
	 * Logged the user out by clearing their session and sending them to the
	 * application's index page.
	 */
	public static void logout() {
		session.clear();
		Application.index();
	}
	
	
	
	
	
	/**
	 * Generate a secure token of an email address. The email address will be
	 * encrypted in a specific format to be decrypted by the validateToken
	 * below. When validated the email address will be returned this allows for
	 * the UI to validate a particular email address.
	 * 
	 * @param emailAddress
	 *            The email address to be validated. It can contain any
	 *            character other than the ":".
	 * @param type
	 *            The purpose of for this token. This same string is needed for
	 *            validating.
	 * @return A base64 encoded token.
	 */
	private static String generateToken(String emailAddress, String type) {
		
		if (emailAddress.contains(":"))
			throw new IllegalArgumentException("Email Addresses may not contain a colon: "+emailAddress);
		
		Date now = new Date();
		int date = now.getYear() + now.getMonth() + now.getDate();
		String rawToken = date+":"+emailAddress+":"+type;
		
		
		// Encrypt the raw token address & base64 encode the result.
		try {
			String privateKey = Play.configuration.getProperty("application.secret").substring(0, 16);
			byte[] raw = privateKey.getBytes();
	        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
	        Cipher cipher = Cipher.getInstance("AES");
	        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
	        return Base64.encodeBase64URLSafeString(cipher.doFinal(rawToken.getBytes()));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
	}
	
	/**
	 * Decrypt and validate a token. The embedded email address encoded in the
	 * token will be returned. The same type string that was used when producing
	 * the token must be used, typically either "register" or "forgot".
	 * 
	 * @param token
	 *            The base64 encoded token.
	 * @param type
	 *            The purpose of this token. The same string is needed that
	 *            generated the token.
	 * @return The embedded email address.
	 */
	private static String validateToken(String token, String type) {

		String rawToken = null;
		try {
			String privateKey = Play.configuration.getProperty("application.secret").substring(0, 16);
	        byte[] raw = privateKey.getBytes();
	        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
	        Cipher cipher = Cipher.getInstance("AES");
	        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
	        rawToken = new String(cipher.doFinal(Base64.decodeBase64(token)));
		} catch (Exception e) {
			// Any error should just return null. 
			return null;
		}
	    
		// Check that we were able to decrypt something.
		if (rawToken == null)
			return null;
		
		// Check that we have the three parts of the rawToken.
        String[] parts = rawToken.split(":");
        if (parts.length != 3)
        	return null;
        
        // Check that the token was generated today or yesterday.
		Date now = new Date();
		int date = now.getYear() + now.getMonth() + now.getDate();
		if (!(String.valueOf(date).equals(parts[0]) ||
			String.valueOf(date-1).equals(parts[0])))
			return null;
		
		// Check that the token is of the expected type.
		if (!type.equals(parts[2]))
			return null;
		
		// All checks passed, return the email address.
		return parts[1];
	}
	
	/**
	 * @return The list of all enabled authenticationMethods.
	 */
	private static List<AuthenticationMethod> getEnabledAuthenticationMethods() {
		Map<String,AuthenticationMethod> methodMap = Spring.getBeansOfType(AuthenticationMethod.class);
		List<AuthenticationMethod> enabledMethods = new ArrayList<AuthenticationMethod>();
		for(AuthenticationMethod method : methodMap.values()) {
			if (method.isEnabled())
				enabledMethods.add(method);
		}
		return enabledMethods;
	}
	
	/**
	 * @return If the registration is enabled by any authentication method.
	 */
	private static boolean isRegistrationEnabled() {
		
		List<AuthenticationMethod> enabledMethods = getEnabledAuthenticationMethods();
		for (AuthenticationMethod method : enabledMethods) {
			if (method.getAllowNewRegistration())
				return true;
		}
		return false;
	}
	
	/**
	 * @return If the password recovery is enabled by any authentication method.
	 */
	private static boolean isPasswordRecoveryEnabled() {
		
		List<AuthenticationMethod> enabledMethods = getEnabledAuthenticationMethods();
		for (AuthenticationMethod method : enabledMethods) {
			if (method.getAllowPasswordRecovery())
				return true;
		}
		return false;
	}
	
	/**
	 * @param person
	 *            The person to update their profile.
	 * 
	 * @return If this person may update their profile information.
	 */
	private static boolean isUpdateProfileEnabled(Person person) {
	
		List<AuthenticationMethod> enabledMethods = getEnabledAuthenticationMethods();
		for (AuthenticationMethod method : enabledMethods) {
			if (method.getAllowUpdateProfile(request, person))
				return true;
		}
		return false;
	}
	
	/**
	 * @param person
	 *            The person to update their password.
	 * @return If this person may update their password.
	 */
	private static boolean isUpdatePasswordEnabled(Person person) {
		
		List<AuthenticationMethod> enabledMethods = getEnabledAuthenticationMethods();
		for (AuthenticationMethod method : enabledMethods) {
			if (method.getAllowUpdatePassword(request, person))
				return true;
		}
		return false;
	}
	
}
