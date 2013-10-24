package org.tdl.vireo.security.impl;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.RoleType;
import org.tdl.vireo.security.AuthenticationMethod;
import org.tdl.vireo.security.AuthenticationResult;

import play.Logger;
import play.Play;
import play.Play.Mode;
import play.mvc.Http.Header;
import play.mvc.Http.Request;
import play.mvc.Router.ActionDefinition;

/**
 * Shibboleth Authentication
 * 
 * TODO: Lots more documentation about how to configure this thing.
 * 
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class ShibbolethAuthenticationMethodImpl extends
	AbstractAuthenticationMethodImpl.AbstractImplicitAuthenticationMethod implements AuthenticationMethod.Implicit {

	/* Injected configuration */
	
	// Weather shibboleth will be mocked or real.
	public boolean mock = (Play.mode == Mode.DEV);
	
	// The location to start a shibboleth session.
	public String loginURL = "/Shibboleth.sso/Login?target=%1s";
	public String logoutURL = "/Shibboleth.sso/Logout?return=%1s";
	
	// Force the login URL to use SSL
	public boolean loginForceSSL = false;
	
	// Use netid or email adress as primary account identifier.
	public boolean useNetIdAsIdentifier = true;
	
	// Weather logout is enabled.
	public boolean logoutEnabled = false;
	
	// All the header names.
	public String headerNetId = "SHIB_netid";
	public String headerEmail = "SHIB_mail";
	public String headerInstitutionalIdentifier = "SHIB_uin";
	public String headerFirstName = "SHIB_givenname";
	public String headerMiddleName = "SHIB_initials"; 
	public String headerLastName = "SHIB_sn"; 
	public String headerBirthYear = "SHIB_dateOfBirth"; 
	public String headerAffiliations = "SHIB_eduPersonAffilation";
	public String headerCurrentPhoneNumber = "SHIB_phone";
	public String headerCurrentPostalAddress = "SHIB_postal";
	public String headerCurrentEmailAddress = "SHIB_mail";
	public String headerPermanentPhoneNumber = "SHIB_permanentPhone";
	public String headerPermanentPostalAddress = "SHIB_permanentPostal";
	public String headerPermanentEmailAddress = "SHIB_permanentMail";
	public String headerCurrentDegree = "SHIB_degree";
	public String headerCurrentDepartment = "SHIB_department";
	public String headerCurrentCollege = "SHIB_college";
	public String headerCurrentMajor = "SHIB_major";
	public String headerCurrentGraduationYear = "SHIB_gradYear";
	public String headerCurrentGraduationMonth = "SHIB_gradMonth";
	public String headerOrcid = "SHIB_orcid";
	
	// Map of mock shibboleth attributes
	public Map<String,String> mockAttributes = new HashMap<String,String>();
	
	
	/**
	 * Set whether the current protocol should be overridden for any login
	 * request to force it to SSL. If this is turned off then login requests
	 * will be handled using the protocol under which they were received.
	 * 
	 * @param loginForceSSL
	 *            Whether to force SSL for login.
	 */
	public void setLoginForceSSL(boolean loginForceSSL) {
		this.loginForceSSL = loginForceSSL;
	}
	
	/**
	 * Set the shibboleth login initiation url. This is a standard java format
	 * string, where the first parameter is a string representation of the
	 * return url. The return url is where shibboleth should send the user to
	 * after successfully authenticating via shibboleth. The application will
	 * intercept this url, and send the request to be authenticated by this
	 * plugin.
	 * 
	 * @param loginURL The shibboleth login initiator
	 */
	public void setLoginURL(String loginURL) {
		this.loginURL = loginURL;
	}
	
	/**
	 * Set the shibboleth logout initaitor url. This is a standard java format
	 * string, where the first paramater is a string representation of the
	 * return url. The url is where shibboleth should send the user after
	 * succesfully logging out.
	 * 
	 * Note: for this value to take effect you also need to enable the logout
	 * feature: setLogoutEnabled(true);
	 * 
	 * @param logoutURL The shibboleth logout initiator
	 */
	public void setLogoutURL(String logoutURL) {
		this.logoutURL = logoutURL;
	}
	
	/**
	 * @param mock
	 *            True if shibboleth authentication should be mocked with test
	 *            attributes. This allows you to use the application without it
	 *            actualy running behind a shibboleth.
	 */
	public void setMock(boolean mock) {
		this.mock = mock;
	}
	
	/**
	 * If this authentication method is configured to Mock a shibboleth
	 * connection then these are the shibboleth attributes that will be assumed
	 * when authenticating.
	 * 
	 * The map should contain the configured Shibboleth HTTP Header names as
	 * keys to the map, while the value should be the actual value (or values)
	 * of the shibboleth attribute.
	 * 
	 * @param mockAttributes
	 *            A map of mock shibboleth attributes.
	 */
	public void setMockAttributes(Map<String,String> mockAttributes) {
		this.mockAttributes = mockAttributes;
	}
	
	/**
	 * Set the primary account identifier, the only valid responses are "netid",
	 * or "email". The field set will be used to uniquely identify person
	 * objects.
	 * 
	 * @param primaryIdentifier
	 *            Either "netid" or "email".
	 */
	public void setPrimaryIdentifier(String primaryIdentifier) {
		if ("netid".equals(primaryIdentifier)) {
			useNetIdAsIdentifier = true;
		} else if ("email".equals(primaryIdentifier)) {
			useNetIdAsIdentifier = false;
		} else {
			throw new IllegalArgumentException("Invalid primary identifier: "+primaryIdentifier+", the only valid options are 'netid' or 'email'.");
		}
	}

	/**
	 * 
	 * @param enabled
	 *            True if shibboleth logout should be enabled by this
	 *            application. If this is enabled make sure to set the logoutURL
	 *            initator as well.
	 */
	public void setLogoutEnabled(boolean enabled) {
		this.logoutEnabled = enabled;
	}
	
	/**
	 * Set the shibboleth header mapping to Vireo attributes. The keys of the
	 * map must equal one of the keys defined bellow. Then the value of that key
	 * must be the expected header name for that particular attribute. For a
	 * definition of what each attribute is, see the Person model.
	 * 
	 * Required Mappings: netId*, email, firstName, lastName.
	 * 
	 * Optional Mappings: middleName, displayName, birthYear,
	 * currentPhoneNumber, currentPostalAddress, currentEmailAddress,
	 * permanentPhoneNumber, permanentPostalAddress, permanentEmailAddress,
	 * currentDepartment, currentCollege, currentMajor, currentGraduationYear,
	 * currentGraduationMonth.
	 * 
	 * netId is only required if useNetIdAsIdentifier is turned on.
	 * 
	 * @param attributeMap
	 */
	public void setAttributeMap(Map<String,String> attributeMap) {
		
		// Check for required mappings
		headerNetId = attributeMap.get("netId");
		headerEmail = attributeMap.get("email");
		headerFirstName = attributeMap.get("firstName");
		headerLastName = attributeMap.get("lastName");
		
		if (headerEmail == null || headerFirstName == null || headerLastName == null) {
			throw new IllegalArgumentException("Missing required attributes in the provided attributeMap.");
		}
		
		// Store all the optional attributes.
		headerInstitutionalIdentifier = attributeMap.get("institutionalIdentifier");
		headerMiddleName = attributeMap.get("middleName");
		headerBirthYear = attributeMap.get("birthYear");
		headerAffiliations = attributeMap.get("affiliations");
		headerCurrentPhoneNumber = attributeMap.get("currentPhoneNumber");
		headerCurrentPostalAddress = attributeMap.get("currentPostalAddress");
		headerCurrentEmailAddress = attributeMap.get("currentEmailAddress");
		headerPermanentPhoneNumber = attributeMap.get("permanentPhoneNumber");
		headerPermanentPostalAddress = attributeMap.get("permanentPostalAddress");
		headerPermanentEmailAddress = attributeMap.get("permanentEmailAddress");
		headerCurrentDegree = attributeMap.get("currentDegree");
		headerCurrentDepartment = attributeMap.get("currentDepartment");
		headerCurrentCollege = attributeMap.get("currentCollege");
		headerCurrentMajor = attributeMap.get("currentMajor");
		headerCurrentGraduationYear = attributeMap.get("currentGraduationYear");
		headerCurrentGraduationMonth = attributeMap.get("currentGraduationMonth");
		headerOrcid = attributeMap.get("orcid");
	}

	
	@Override
	public String startAuthentication(Request request, ActionDefinition returnAction) {
		
		// If we are mocking a shibboleth connection then we can proceed straight to authentication.
		if (mock)
			return null;
		
		String returnURL = returnAction.url;
		if (loginForceSSL) {
			returnAction.absolute();
			returnURL = returnAction.url;
			if (returnURL.startsWith("http://")) 
				returnURL = "https://"+returnURL.substring(7);
		}
		
		// Generate the URL to initiate a shibboleth session
		String encodedReturnURL = URLEncoder.encode(returnURL);
		String completeLoginURL = String.format(loginURL,encodedReturnURL);
		
		return completeLoginURL;
	}

	@Override
	public AuthenticationResult authenticate(Request request) {
		
		// 1. Log all headers received, if tracing (it fills up the logs fast!)
		if (Logger.isTraceEnabled()) {
			String log = "Shib: Recieved the following headers: \n";
			for (String name : request.headers.keySet()) {
				for (String value : request.headers.get(name).values) {
					log += "    '" + name + "' = '" + value + "'\n";
				}
			}
			Logger.trace(log);
		}

		// 2. Get required attributes.
		String netid = getSingleAttribute(request, headerNetId);
		String email = getSingleAttribute(request, headerEmail);
		String firstName = getSingleAttribute(request, headerFirstName);
		String lastName = getSingleAttribute(request, headerLastName);
		
		if (useNetIdAsIdentifier && netid == null) {
			Logger.error("Shib: Missing required NetId attributes because netid is the primary account identifier. Netid attribute header = %1s.", headerNetId);
			return AuthenticationResult.MISSING_CREDENTIALS;
		}
		if (email == null ) {
			Logger.error("Shib: Missing required email address attributes. Email address attribute header = %1s.", headerEmail);
			return AuthenticationResult.MISSING_CREDENTIALS;
		}
		if (firstName == null && lastName == null) {
			Logger.error("Shib: Missing required first and last name attributes. First Name attribute header = %1s, Last Name attribute header = %2s", headerFirstName, headerLastName);
			return AuthenticationResult.MISSING_CREDENTIALS;
		}
				
		// 3. Lookup the person based upon the primary identifier
		Person person;
		try {
			context.turnOffAuthorization();
			if (useNetIdAsIdentifier) {
				person = personRepo.findPersonByNetId(netid);
			} else {
				person = personRepo.findPersonByEmail(email);
			}
			if (person == null) {
				// Create the new person
				try {
					person = personRepo.createPerson(netid, email, firstName, lastName, RoleType.STUDENT).save();
				} catch (RuntimeException re) {
					// Unable to create new person, probably because the email or netid already exist.
					Logger.error(re,"Shib: Unable to create new eperson.");
					return AuthenticationResult.BAD_CREDENTIALS;
				}
			} else {
				// Update required fields.
				if (netid != null)
					person.setNetId(netid);
				person.setEmail(email);
				person.setFirstName(firstName);
				person.setLastName(lastName);
			}

			// 4. Update Optional attributes:
			if (headerInstitutionalIdentifier != null) {
				String identifier = getSingleAttribute(request, headerInstitutionalIdentifier);
				if (!isEmpty(identifier))
					person.setInstitutionalIdentifier(identifier);
			}
			if (headerMiddleName != null) {
				String middleName = getSingleAttribute(request, headerMiddleName);
				if (!isEmpty(middleName) && person.getMiddleName() == null)
					person.setMiddleName(middleName);
			}
			if (headerBirthYear != null) {
				String birthYearString = getSingleAttribute(request, headerBirthYear);
				if (!isEmpty(birthYearString) && person.getBirthYear() == null) {
					try {
						Integer birthYear = Integer.valueOf(birthYearString);
						person.setBirthYear(birthYear);
					} catch (NumberFormatException nfe) {
						Logger.warn("Shib: Unable to interpret birth year attribute '"+headerBirthYear+"'='"+birthYearString+"' as an integer.");
					}
				}
			}
			if (headerAffiliations != null) {
				List<String> affiliations = getAttributes(request, headerAffiliations);
				if (affiliations != null && affiliations.size() > 0) {
					person.getAffiliations().clear();
					if (affiliations != null)
						person.getAffiliations().addAll(affiliations);
				}
			}
			if (headerCurrentPhoneNumber != null) {
				String currentPhoneNumber = getSingleAttribute(request, headerCurrentPhoneNumber);
				if (!isEmpty(currentPhoneNumber) && person.getCurrentPhoneNumber() == null)
					person.setCurrentPhoneNumber(currentPhoneNumber);
			}
			if (headerCurrentPostalAddress != null) {
				String currentPostalAddress = getSingleAttribute(request, headerCurrentPostalAddress);
				if (!isEmpty(currentPostalAddress) && person.getCurrentPostalAddress() == null) 
					person.setCurrentPostalAddress(currentPostalAddress);
			}
			if (headerCurrentEmailAddress != null) {
				String currentEmailAddress = getSingleAttribute(request, headerCurrentEmailAddress);
				if (!isEmpty(currentEmailAddress) && person.getCurrentEmailAddress() == null)
					person.setCurrentEmailAddress(currentEmailAddress);
			}
			if (headerPermanentPhoneNumber != null) {
				String permanentPhoneNumber = getSingleAttribute(request, headerPermanentPhoneNumber);
				if (!isEmpty(permanentPhoneNumber) && person.getPermanentPhoneNumber() == null)
					person.setPermanentPhoneNumber(permanentPhoneNumber);
			}
			if (headerPermanentPostalAddress != null) {
				String permanentPostalAddress = getSingleAttribute(request, headerPermanentPostalAddress);
				if (!isEmpty(permanentPostalAddress) && person.getPermanentPostalAddress() == null)
					person.setPermanentPostalAddress(permanentPostalAddress);
			}
			if (headerPermanentEmailAddress != null) {
				String permanentEmailAddress = getSingleAttribute(request, headerPermanentEmailAddress);
				if (!isEmpty(permanentEmailAddress) && person.getPermanentEmailAddress() == null)
					person.setPermanentEmailAddress(permanentEmailAddress);
			}
			if (headerCurrentDegree != null) {
				String currentDegree = getSingleAttribute(request, headerCurrentDegree);
				if (!isEmpty(currentDegree) && person.getCurrentDegree() == null)
					person.setCurrentDegree(currentDegree);
			}
			if (headerCurrentDepartment != null) {
				String currentDepartment = getSingleAttribute(request, headerCurrentDepartment);
				if (!isEmpty(currentDepartment) && person.getCurrentDepartment() == null)
					person.setCurrentDepartment(currentDepartment);
			}
			if (headerCurrentCollege != null) {
				String currentCollege = getSingleAttribute(request, headerCurrentCollege);
				if (!isEmpty(currentCollege) && person.getCurrentCollege() == null)
					person.setCurrentCollege(currentCollege);
			}
			if (headerCurrentMajor != null) {
				String currentMajor = getSingleAttribute(request, headerCurrentMajor);
				if (!isEmpty(currentMajor) && person.getCurrentMajor() == null)
					person.setCurrentMajor(currentMajor);
			}
			if (headerCurrentGraduationYear != null) {
				String currentGraduationYearString = getSingleAttribute(request, headerCurrentGraduationYear);
				if (!isEmpty(currentGraduationYearString) && person.getCurrentGraduationYear() == null) {
					try {
						Integer currentGraduationYear = Integer.valueOf(currentGraduationYearString);
						person.setCurrentGraduationYear(currentGraduationYear);
					} catch (NumberFormatException nfe) {
						Logger.warn("Shib: Unable to interpret current graduation year attribute '"+headerCurrentGraduationYear+"'='"+currentGraduationYearString+"' as an integer.");
					}
				}
			}
			if (headerCurrentGraduationMonth != null) {
				String currentGraduationMonthString = getSingleAttribute(request, headerCurrentGraduationMonth);
				if (!isEmpty(currentGraduationMonthString) && person.getCurrentGraduationMonth() == null) {
					try {
						Integer currentGraduationMonth = Integer.valueOf(currentGraduationMonthString);
						person.setCurrentGraduationMonth(currentGraduationMonth);
					} catch (NumberFormatException nfe) {
						Logger.warn("Shib: Unable to interpret current graduation month attribute '"+headerCurrentGraduationMonth+"'='"+currentGraduationMonthString+"' as an integer.");
					} catch (IllegalArgumentException iae) {
						Logger.warn("Shib: Illegal value for current graduation month attribute '"+headerCurrentGraduationMonth+"'='"+currentGraduationMonthString+"', 0=January, 11=Dember. Any values outside this range are illegal.");
					}
				}
			}
			if (headerOrcid != null) {
				String orcidString = getSingleAttribute(request, headerOrcid);
				if (!isEmpty(orcidString) && person.getOrcid() == null)
					person.setOrcid(orcidString);
			}
			
			person.save();

		} finally {
			context.restoreAuthorization();
		}
		
		context.login(person);
		return AuthenticationResult.SUCCESSFULL;
	}
	
	
	
	@Override
	public String getFailureMessage(Request request, AuthenticationResult result) {
		
		StringBuilder msg = new StringBuilder();
		
		if (AuthenticationResult.BAD_CREDENTIALS == result ) {
			msg.append("<p>Shibboleth authentication recieved the required attributes but was unable to create a new account. The most likely cause is because an account with the same netid or email already exists in the system.</p>");
		} else {
			msg.append("<p>Shibboleth authentication failed because it did not recieve enough information from your Shibboleth Identity Provider. Vireo requires four attributes Netid, Email, and your First and Last name. If any of these are missing then authentication will fail. Below is a listing of the attributes we did recieve:</p>");
		}
		msg.append("<dl class=\"dl-horizontal\">");

		msg.append("<dt>NetId:</dt>");
		String netid = getSingleAttribute(request, headerNetId);
		if (netid != null)
			msg.append("<dd><code>'"+headerNetId+"' = '"+netid+"'</code></dd>");
		else 
			msg.append("<dd><code>'"+headerNetId+"' = null</code></dd>");
		msg.append("<br/>");
		
		msg.append("<dt>Email Address:</dt>");
		String email = getSingleAttribute(request, headerEmail);
		if (email != null)
			msg.append("<dd><code>'"+headerEmail+"' = '"+email+"'</code></dd>");
		else 
			msg.append("<dd><code>'"+headerEmail+"' = null</code></dd>");
		msg.append("<br/>");
		
		msg.append("<dt>First Name:</dt>");
		String firstName = getSingleAttribute(request, headerFirstName);
		if (firstName != null)
			msg.append("<dd><code>'"+headerFirstName+"' = '"+firstName+"'</code></dd>");
		else 
			msg.append("<dd><code>'"+headerFirstName+"' = null</code></dd>");
		msg.append("<br/>");
		
		msg.append("<dt>Last Name:</dt>");
		String lastName = getSingleAttribute(request, headerLastName);
		if (lastName != null)
			msg.append("<dd><code>'"+headerLastName+"' = '"+lastName+"'</code></dd>");
		else 
			msg.append("<dd><code>'"+headerLastName+"' = null</code></dd>");
		
		msg.append("</dl>");
		
		msg.append("<p>Please contact your university Information Technology support at your local campus for trouble shooting this problem.</p>");
		
		return msg.toString();
	}
	
	@Override
	public String logout(Request request, String returnURL) {
		
		// If we are mocking a shibboleth connection we don't do a full shib logout.
		if (mock)
			return null;
		
		// Is logout enabled?
		if (!logoutEnabled)
			return null;
		
		// Generate the URL to initiate a shibboleth session
		String encodedReturnURL = URLEncoder.encode(returnURL);
		String completeLoginoutURL = String.format(logoutURL,encodedReturnURL);
		
		return completeLoginoutURL;
	}
	
	/**
	 * Internal method for retrieving a single shibboleth attribute value. If
	 * there are multiple values encoded in the header then only the first one
	 * is returned and all others are ignored.
	 * 
	 * @param request
	 *            The HTTP Request object
	 * @param attribute
	 *            The name of the attribute header.
	 * @return The attribute value, or null if not found.
	 */
	protected String getSingleAttribute(Request request, String attribute) {
		
		List<String> attributes = getAttributes(request, attribute);
		
		if (attributes == null || attributes.size() == 0)
			return null;
		
		return attributes.get(0);
	}
	
	/**
	 * Internal method for retrieving a list of shibboleth attribute values.
	 * Shibboleth attributes may contain multiple values separated by a
	 * semicolon and semicolons are escaped with a backslash. This method will
	 * split all the attributes into a list and unescape semicolons.
	 * 
	 * 
	 * @param request
	 *            The HTTP request object
	 * @param attribute
	 *            The name of the attribute header.
	 * @return A list of attributes, or null if not found.
	 */
	protected List<String> getAttributes(Request request, String attribute) {
		
		// Get the attribute header
		String valueString = null;
		if (mock) {
			valueString = mockAttributes.get(attribute);
		} else {
			Header header = request.headers.get(attribute);
			if (header == null || header.values == null || header.values.size() == 0)
				return null;
			if (header.values.size() > 1) 
				Logger.warn("Shib: Recieved multiple shibboleth attributes for '" + attribute
						+ "'. Using the first one and ignoring the rest.");
			valueString = header.value();
		} 
		if (valueString == null) {
			Logger.warn("Shib: Recieved shibboleth attribute for '" + attribute
					+ "' but the value was null.");
			return null;
		}
	
		// Split the attribute into it's individual components. 
		List<String> valueList = new ArrayList<String>();
		int idx = 0;
		do {
			idx = valueString.indexOf(';', idx);

			if (idx == 0) {
				// if the string starts with a semicolon just remove it. This
				// will
				// prevent an endless loop in an error condition.
				valueString = valueString.substring(1, valueString.length());
				continue;
			} 
			if (idx > 0 && valueString.charAt(idx - 1) == '\\') {
				idx++;
				continue;
			} 
			if (idx > 0 ) {
				// First extract the value and store it on the list.
				String value = valueString.substring(0, idx);
				value = value.replaceAll("\\\\;", ";");
				valueList.add(value);

				// Next, remove the value from the string and continue to scan.
				valueString = valueString.substring(idx + 1, valueString.length());
				idx = 0;
			} 
		} while (idx >= 0);

		// The last attribute will still be left on the values string, put it
		// into the list.
		if (valueString.length() > 0) {
			valueString = valueString.replaceAll("\\\\;", ";");
			valueList.add(valueString);
		}
		
		return valueList;
	}
	
	/**
	 * @param value
	 *            The value to be tested.
	 * @return true if the value is either null, or only consists of whitespace.
	 *         Otherwise return false.
	 */
	private static boolean isEmpty(String value) {
		if (value == null)
			return true;

		if (value.trim().length() == 0)
			return true;

		return false;
	}

}
