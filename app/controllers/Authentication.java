package controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.PersonRepository;
import org.tdl.vireo.model.RoleType;
import org.tdl.vireo.security.AuthenticationMethod;
import org.tdl.vireo.security.AuthenticationResult;
import org.tdl.vireo.security.SecurityContext;

import play.Logger;
import play.Play;
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
			PersonRepository personRepo = Spring.getBeanOfType(PersonRepository.class);
			person = personRepo.findPerson(personId);
			
			SecurityContext context = Spring.getBeanOfType(SecurityContext.class);
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
		SecurityContext context = Spring.getBeanOfType(SecurityContext.class);
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
		Map<String,AuthenticationMethod> methodMap = Spring.getBeansOfType(AuthenticationMethod.class);
		List<AuthenticationMethod> enabledMethods = new ArrayList<AuthenticationMethod>();
		for(AuthenticationMethod method : methodMap.values()) {
			if (method.isEnabled())
				enabledMethods.add(method);
		}
		
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
			redirect("Application.index");
		
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

				SecurityContext context = Spring.getBeanOfType(SecurityContext.class);
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
		
		SecurityContext context = Spring.getBeanOfType(SecurityContext.class);
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
	
	public static void register() {
		todo();
	}
	
	public static void forgot() {
		todo();
	}
	
	@Security(RoleType.NONE)
	public static void profile() {
		render();
	}
	
	/**
	 * Logged the user out by clearing their session and sending them to the
	 * application's index page.
	 */
	public static void logout() {
		session.clear();
		Application.index();
	}
	
	
	
	
}
