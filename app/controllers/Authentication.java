package controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.tdl.vireo.security.AuthenticationMethod;
import org.tdl.vireo.security.AuthenticationResult;

import play.modules.spring.Spring;
import play.mvc.Controller;
import play.mvc.Router;

public class Authentication extends Controller {

	public static void loginList() {
		
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
	
	public static void loginMethod(String methodName) {
		
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
			routeArgs.put("method",methodName);
			String returnURL = Router.getFullUrl("Authentication.loginReturn", routeArgs);
			
			String initiationRedirect = implicitMethod.startAuthentication(request, returnURL);
			
			if (initiationRedirect == null) {
				redirect("Authentication.loginReturn",routeArgs);
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
				
				if (AuthenticationResult.SUCCESSFULL == result) {
					// Yay log the person in.
					todo();
				} 
				validation.addError("login", "The username and/or password does not exist. Please try again.");
			
				render(method,username,password);
			}
			render(method);
		} 
		
		// Neither implicit or explicit
		error("Authentication method '"+methodName+"' is invalid because it is not implicit or explicit.");
	}
	
	public static void loginReturn(String methodName) {
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
		
		if (AuthenticationResult.SUCCESSFULL == result) {
			// Yay log the person in.
			todo();
		} 
		
		render(result);
	}
	
	public static void register() {
		todo();
	}
	
	public static void profile() {
		todo();
	}
	
	public static void logout() {
		todo();
	}
	
	
	
	
}
