package org.tdl.vireo.security.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.PersonRepository;
import org.tdl.vireo.model.RoleType;
import org.tdl.vireo.security.AuthenticationResult;
import org.tdl.vireo.security.SecurityContext;
import org.tdl.vireo.security.impl.ShibbolethAuthenticationMethodImpl;

import play.db.jpa.JPA;
import play.modules.spring.Spring;
import play.mvc.Http.Header;
import play.mvc.Http.Request;
import play.test.UnitTest;

/**
 * Test the shibboleth authentication method. These tests require the method to
 * be configured in certain ways so that we know exactly what input to give it.
 * To make these tests work with a variety of configurations before each test
 * we save the current state of, run our tests, then restore the state after the
 * test has run. This means that if a tests fails we may leave the method in a
 * changed state, but other than that it should remain as original configured.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class ShibbolethAuthenticationMethodImplTest extends UnitTest {

	/* Dependencies */
	public static SecurityContext context = Spring.getBeanOfType(SecurityContext.class);
	public static PersonRepository personRepo = Spring.getBeanOfType(PersonRepository.class);
	public static ShibbolethAuthenticationMethodImpl method = Spring.getBeanOfType(ShibbolethAuthenticationMethodImpl.class);

	// predefined persons to test with.
	public Person person1;
	public Person person2;
	
	// Original Shibboleth State
	public boolean originalMock;
	public boolean originalUseNetIdAsIdentifier;
	public String originalHeaderNetId;
	public String originalHeaderEmail;
	public String originalHeaderInstitutionalIdentifier;
	public String originalHeaderFirstName;
	public String originalHeaderMiddleName;
	public String originalHeaderLastName;
	public String originalHeaderDisplayName;
	public String originalHeaderBirthYear;
	public String originalHeaderCurrentPhoneNumber;
	public String originalHeaderCurrentPostalAddress;
	public String originalHeaderCurrentEmailAddress;
	public String originalHeaderPermanentPhoneNumber;
	public String originalHeaderPermanentPostalAddress;
	public String originalHeaderPermanentEmailAddress;
	public String originalHeaderCurrentDegree;
	public String originalHeaderCurrentDepartment;
	public String originalHeaderCurrentCollege;
	public String originalHeaderCurrentMajor;
	public String originalHeaderCurrentGraduationYear;
	public String originalHeaderCurrentGraduationMonth;
	public Map<String,String> originalMockAttributes;
	
	/**
	 * Setup for a test by doing three things:
	 * 
	 * 1) Create two users for us to test with.
	 * 
	 * 2) Save the current state of the shibboleth authentication method.
	 * 
	 * 3) Establish our expected state (i.e. what all the headers are named);
	 */
	@Before
	public void setup() {
		// Setup two user accounts;
		context.turnOffAuthorization();
		person1 = personRepo.createPerson("netid", "email@email.com", "first", "last", RoleType.NONE).save();
		person2 = personRepo.createPerson("other", "other@email.com", "other", "another", RoleType.NONE).save();
		context.restoreAuthorization();
		
		// Save the method's current state.
		originalMock = method.mock;
		originalUseNetIdAsIdentifier = method.useNetIdAsIdentifier;
		originalHeaderNetId = method.headerNetId;
		originalHeaderEmail = method.headerEmail;
		originalHeaderInstitutionalIdentifier = method.headerInstitutionalIdentifier;
		originalHeaderFirstName = method.headerFirstName;
		originalHeaderMiddleName = method.headerMiddleName;
		originalHeaderLastName = method.headerLastName;
		originalHeaderDisplayName = method.headerDisplayName;
		originalHeaderBirthYear = method.headerBirthYear;
		originalHeaderCurrentPhoneNumber = method.headerCurrentPhoneNumber;
		originalHeaderCurrentPostalAddress = method.headerCurrentPostalAddress;
		originalHeaderCurrentEmailAddress = method.headerCurrentEmailAddress;
		originalHeaderPermanentPhoneNumber = method.headerPermanentPhoneNumber;
		originalHeaderPermanentPostalAddress = method.headerPermanentPostalAddress;
		originalHeaderPermanentEmailAddress = method.headerPermanentEmailAddress;
		originalHeaderCurrentDegree = method.headerCurrentDegree;
		originalHeaderCurrentDepartment = method.headerCurrentDepartment;
		originalHeaderCurrentCollege = method.headerCurrentCollege;
		originalHeaderCurrentMajor = method.headerCurrentMajor;
		originalHeaderCurrentGraduationYear = method.headerCurrentGraduationYear;
		originalHeaderCurrentGraduationMonth = method.headerCurrentGraduationMonth;
		originalMockAttributes = method.mockAttributes;
		
		// Set the method's state to what the test expect.
		method.headerNetId = "SHIB_netid";
		method.headerEmail = "SHIB_mail";
		method.headerInstitutionalIdentifier = "SHIB_uin";
		method.headerFirstName = "SHIB_givenName";
		method.headerMiddleName = "SHIB_initials"; 
		method.headerLastName = "SHIB_sn"; 
		method.headerDisplayName = "SHIB_cn"; 
		method.headerBirthYear = "SHIB_birthYear"; 
		method.headerCurrentPhoneNumber = "SHIB_phone";
		method.headerCurrentPostalAddress = "SHIB_postal";
		method.headerCurrentEmailAddress = "SHIB_mail";
		method.headerPermanentPhoneNumber = "SHIB_permanentPhone";
		method.headerPermanentPostalAddress = "SHIB_permanentPostal";
		method.headerPermanentEmailAddress = "SHIB_permanentMail";
		method.headerCurrentDegree = "SHIB_degree";
		method.headerCurrentDepartment = "SHIB_department";
		method.headerCurrentCollege = "SHIB_college";
		method.headerCurrentMajor = "SHIB_major";
		method.headerCurrentGraduationYear = "SHIB_gradYear";
		method.headerCurrentGraduationMonth = "SHIB_gradMonth";
		
	}
	
	/**
	 * Clean up after a test by doing three things:
	 * 
	 * 1) Restore the original state of the shibboleth authentication method
	 * 
	 * 2) Cleanup test person's created.
	 * 
	 * 3) Rollback the database transaction.
	 */
	@After
	public void cleanup() {
		
		// Restore the method's state.
		method.mock = originalMock;
		method.useNetIdAsIdentifier = originalUseNetIdAsIdentifier;
		method.headerNetId = originalHeaderNetId;
		method.headerEmail = originalHeaderEmail;
		method.headerInstitutionalIdentifier = originalHeaderInstitutionalIdentifier;
		method.headerFirstName = originalHeaderFirstName;
		method.headerMiddleName = originalHeaderMiddleName;
		method.headerLastName = originalHeaderLastName;
		method.headerDisplayName = originalHeaderDisplayName;
		method.headerBirthYear = originalHeaderBirthYear;
		method.headerCurrentPhoneNumber = originalHeaderCurrentPhoneNumber;
		method.headerCurrentPostalAddress = originalHeaderCurrentPostalAddress;
		method.headerCurrentEmailAddress = originalHeaderCurrentEmailAddress;
		method.headerPermanentPhoneNumber = originalHeaderPermanentPhoneNumber;
		method.headerPermanentPostalAddress = originalHeaderPermanentPostalAddress;
		method.headerPermanentEmailAddress = originalHeaderPermanentEmailAddress;
		method.headerCurrentDegree = originalHeaderCurrentDegree;
		method.headerCurrentDepartment = originalHeaderCurrentDepartment;
		method.headerCurrentCollege = originalHeaderCurrentCollege;
		method.headerCurrentMajor = originalHeaderCurrentMajor;
		method.headerCurrentGraduationYear = originalHeaderCurrentGraduationYear;
		method.headerCurrentGraduationMonth = originalHeaderCurrentGraduationMonth;
		method.mockAttributes = originalMockAttributes;

		context.turnOffAuthorization();
		personRepo.findPerson(person1.getId()).delete();
		personRepo.findPerson(person2.getId()).delete();

		context.restoreAuthorization();
		context.logout();
		JPA.em().getTransaction().rollback();
		JPA.em().getTransaction().begin();
	}

	/**
	 * Test that a user presenting valid credentials can authenticate. We also
	 * test that all the parameters are used, and put into their appropriate
	 * place.
	 */
	@Test
	public void testPositiveAuthentication() {
		// Turn off mocking in the method.
		method.mock = false;
		method.useNetIdAsIdentifier = true;

		Map<String,String> headers = new HashMap<String,String>();
		headers.put("SHIB_netid","netid");
		headers.put("SHIB_mail", "updatedemail@email.com");
		headers.put("SHIB_uin","123456789");
		headers.put("SHIB_givenName", "updatedfirst");
		headers.put("SHIB_sn","updatedlast");
		headers.put("SHIB_initials","initials");
		headers.put("SHIB_cn","cn");
		headers.put("SHIB_birthYear","1950");
		headers.put("SHIB_phone","phone");
		headers.put("SHIB_postal","postal");
		headers.put("SHIB_permanentPhone","permanentPhone");
		headers.put("SHIB_permanentPostal","permanentPostal");
		headers.put("SHIB_permanentMail","permanentEmail");
		headers.put("SHIB_degree","degree");
		headers.put("SHIB_department","department");
		headers.put("SHIB_college","college");
		headers.put("SHIB_major","major");
		headers.put("SHIB_gradYear","2012");
		headers.put("SHIB_gradMonth","05");

		Request request = buildRequest(headers);
		
		AuthenticationResult result = method.authenticate(request);

		assertEquals(AuthenticationResult.SUCCESSFULL, result);
		assertEquals(person1,context.getPerson());
		
		// Assert that all the header were matched over and setup correctly.
		assertEquals("updatedemail@email.com",person1.getEmail());
		assertEquals("123456789",person1.getInstitutionalIdentifier());
		assertEquals("updatedfirst",person1.getFirstName());
		assertEquals("updatedlast",person1.getLastName());
		assertEquals("initials",person1.getMiddleName());
		assertEquals("cn",person1.getDisplayName());
		assertEquals(Integer.valueOf(1950),person1.getBirthYear());
		assertEquals("phone",person1.getCurrentPhoneNumber());
		assertEquals("postal",person1.getCurrentPostalAddress());
		assertEquals("permanentPhone",person1.getPermanentPhoneNumber());
		assertEquals("permanentPostal",person1.getPermanentPostalAddress());
		assertEquals("permanentEmail",person1.getPermanentEmailAddress());
		assertEquals("degree",person1.getCurrentDegree());
		assertEquals("college",person1.getCurrentCollege());
		assertEquals("department",person1.getCurrentDepartment());
		assertEquals("major",person1.getCurrentMajor());
		assertEquals(Integer.valueOf(2012),person1.getCurrentGraduationYear());
		assertEquals(Integer.valueOf(05), person1.getCurrentGraduationMonth());
	}
	
	/**
	 * Test that the mock feature is able to succesfully authenticate a person.
	 */
	@Test
	public void testPositiveMockAuthentication() {
		// Turn on mocking in the method
		method.mock = true;
		method.useNetIdAsIdentifier = true;
		
		Map<String,String> mockAttributes = new HashMap<String,String>();
		mockAttributes.put("SHIB_netid","netid");
		mockAttributes.put("SHIB_mail", "updatedemail@email.com");
		mockAttributes.put("SHIB_uin","123456789");
		mockAttributes.put("SHIB_givenName", "updatedfirst");
		mockAttributes.put("SHIB_sn","updatedlast");
		mockAttributes.put("SHIB_initials","initials");
		mockAttributes.put("SHIB_cn","cn");
		mockAttributes.put("SHIB_birthYear","1950");
		mockAttributes.put("SHIB_phone","phone");
		mockAttributes.put("SHIB_postal","postal");
		mockAttributes.put("SHIB_permanentPhone","permanentPhone");
		mockAttributes.put("SHIB_permanentPostal","permanentPostal");
		mockAttributes.put("SHIB_permanentMail","permanentEmail");
		mockAttributes.put("SHIB_degree","degree");
		mockAttributes.put("SHIB_department","department");
		mockAttributes.put("SHIB_college","college");
		mockAttributes.put("SHIB_major","major");
		mockAttributes.put("SHIB_gradYear","2012");
		mockAttributes.put("SHIB_gradMonth","05");
		method.setMockAttributes(mockAttributes);
		
		Request request = buildRequest(new HashMap<String,String>());
		
		// Now the attributes are only present on the mock Attributes the header is basicly blank.
		AuthenticationResult result = method.authenticate(request);

		assertEquals(AuthenticationResult.SUCCESSFULL, result);
		assertEquals(person1,context.getPerson());
		
		// Assert that all the header were matched over and setup correctly.
		assertEquals("updatedemail@email.com",person1.getEmail());
		assertEquals("123456789",person1.getInstitutionalIdentifier());
		assertEquals("updatedfirst",person1.getFirstName());
		assertEquals("updatedlast",person1.getLastName());
		assertEquals("initials",person1.getMiddleName());
		assertEquals("cn",person1.getDisplayName());
		assertEquals(Integer.valueOf(1950),person1.getBirthYear());
		assertEquals("phone",person1.getCurrentPhoneNumber());
		assertEquals("postal",person1.getCurrentPostalAddress());
		assertEquals("permanentPhone",person1.getPermanentPhoneNumber());
		assertEquals("permanentPostal",person1.getPermanentPostalAddress());
		assertEquals("permanentEmail",person1.getPermanentEmailAddress());
		assertEquals("degree",person1.getCurrentDegree());
		assertEquals("college",person1.getCurrentCollege());
		assertEquals("department",person1.getCurrentDepartment());
		assertEquals("major",person1.getCurrentMajor());
		assertEquals(Integer.valueOf(2012),person1.getCurrentGraduationYear());
		assertEquals(Integer.valueOf(05), person1.getCurrentGraduationMonth());
	}
	
	/**
	 * Test that people who present incomplete credentials can not authenticate.
	 */
	@Test
	public void testNegativeAuthentication() {
		// Turn off mocking in the method.
		method.mock = false;
		method.useNetIdAsIdentifier = true;

		Map<String,String> headers = new HashMap<String,String>();
		headers.put("SHIB_netid","netid");
		headers.put("SHIB_mail", "email@email.com");
		headers.put("SHIB_givenName", "updatedfirst");
		headers.put("SHIB_sn","updatelast");


		Map<String,String> headersWithoutNetid = new HashMap<String,String>(headers);
		headersWithoutNetid.remove("SHIB_netid");
		Map<String,String> headersWithoutMail = new HashMap<String,String>(headers);
		headersWithoutMail.remove("SHIB_mail");
		Map<String,String> headersWithoutName = new HashMap<String,String>(headers);
		headersWithoutName.remove("SHIB_givenName");
		headersWithoutName.remove("SHIB_sn");
		
		Request requestWithoutNetid = buildRequest(headersWithoutNetid);
		Request requestWithoutMail = buildRequest(headersWithoutMail);
		Request requestWithoutName = buildRequest(headersWithoutName);
		
		// Try each time with one value missing
		AuthenticationResult result = method.authenticate(requestWithoutNetid);
		assertEquals(AuthenticationResult.MISSING_CREDENTIALS, result);
		assertNull(context.getPerson());
		
		result = method.authenticate(requestWithoutMail);
		assertEquals(AuthenticationResult.MISSING_CREDENTIALS, result);
		assertNull(context.getPerson());
		
		result = method.authenticate(requestWithoutName);
		assertEquals(AuthenticationResult.MISSING_CREDENTIALS, result);
		assertNull(context.getPerson());
		
		
		// Now try where netid is not required
		method.useNetIdAsIdentifier = false;
		
		result = method.authenticate(requestWithoutNetid);
		assertEquals(AuthenticationResult.SUCCESSFULL, result);
		assertEquals(person1,context.getPerson());
	}
	
	/**
	 * Test that given new credentials a person is created.
	 */
	@Test
	public void testCreatingPerson() {
		
		method.mock = false;
		method.useNetIdAsIdentifier = true;
		
		// Test using netid
		{
			Map<String,String> headers = new HashMap<String,String>();
			headers.put("SHIB_netid","newPerson");
			headers.put("SHIB_mail", "newPerson@email.com");
			headers.put("SHIB_givenName", "New");
			headers.put("SHIB_sn","Person");
			
			Request request = buildRequest(headers);
	
			
			AuthenticationResult result = method.authenticate(request);
			
			assertEquals(AuthenticationResult.SUCCESSFULL, result);
			Person created = context.getPerson();
			assertNotNull(created);
			assertEquals("newPerson",created.getNetId());
			assertEquals("newPerson@email.com",created.getEmail());
			assertEquals("New",created.getFirstName());
			assertEquals("Person",created.getLastName());
			assertEquals(RoleType.STUDENT,created.getRole());
			
			created.delete();
		}
		
		// Test using email
		method.useNetIdAsIdentifier = false;
		{
			Map<String,String> headers = new HashMap<String,String>();
			headers.put("SHIB_mail", "newPerson@email.com");
			headers.put("SHIB_givenName", "New");
			headers.put("SHIB_sn","Person");
			
			Request request = buildRequest(headers);

			
			AuthenticationResult result = method.authenticate(request);
			
			assertEquals(AuthenticationResult.SUCCESSFULL, result);
			Person created = context.getPerson();
			assertNotNull(created);
			assertEquals("newPerson@email.com",created.getEmail());
			assertEquals("New",created.getFirstName());
			assertEquals("Person",created.getLastName());
			assertEquals(RoleType.STUDENT,created.getRole());
			
			created.delete();
		}
	}
	
	/**
	 * Test that the shibboleth initiation url looks correct.
	 */
	@Test
	public void testShibbolethInitiation() {
		
		method.mock = false;
		
		String redirect = method.startAuthentication(null, "return");
		assertNotNull(redirect);
		assertTrue(redirect.contains("return"));
		
		method.mock = true;
		
		assertNull(method.startAuthentication(null, "return"));
	}
	
	/**
	 * Test that attributes are read into multiple values correctly.
	 */
	@Test
	public void testGetAttributes() {
		
		
		method.mock = false;
		
		Map<String,String> headers = new HashMap<String,String>();
		headers.put("test1", "easy");
		headers.put("test2", "one;two");
		headers.put("test3", "one;two;three");
		headers.put("test4", "double;;semicolin");
		headers.put("test5", ";starts;with;one");
		headers.put("test6", "ends;with;one;");
		headers.put("test7", "escaping\\;;whoa");
		headers.put("test8", "tricky;\\;\\;\\;;?");
		headers.put("test9", ";");
		
		Request request = buildRequest(headers);
		
		{
			List<String> test1 = method.getAttributes(request, "test1");
			assertNotNull(test1);
			assertEquals("easy",test1.get(0));
			assertEquals(1,test1.size());
		}

		{
			List<String> test2 = method.getAttributes(request, "test2");
			assertNotNull(test2);
			assertEquals("one",test2.get(0));
			assertEquals("two",test2.get(1));
			assertEquals(2,test2.size());
		}

		{
			List<String> test3 = method.getAttributes(request, "test3");
			assertNotNull(test3);
			assertEquals("one",test3.get(0));
			assertEquals("two",test3.get(1));
			assertEquals("three",test3.get(2));
			assertEquals(3,test3.size());
		}

		{
			List<String> test4 = method.getAttributes(request, "test4");
			assertNotNull(test4);
			assertEquals("double",test4.get(0));
			assertEquals("semicolin",test4.get(1));
			assertEquals(2,test4.size());
		}

		{
			List<String> test5 = method.getAttributes(request, "test5");
			assertNotNull(test5);
			assertEquals("starts",test5.get(0));
			assertEquals("with",test5.get(1));
			assertEquals("one",test5.get(2));
			assertEquals(3,test5.size());
		}

		{
			List<String> test6 = method.getAttributes(request, "test6");
			assertNotNull(test6);
			assertEquals("ends",test6.get(0));
			assertEquals("with",test6.get(1));
			assertEquals("one",test6.get(2));
			assertEquals(3,test6.size());
		}

		{
			List<String> test7 = method.getAttributes(request, "test7");
			assertNotNull(test7);
			assertEquals("escaping;",test7.get(0));
			assertEquals("whoa",test7.get(1));
			assertEquals(2,test7.size());
		}

		{
			List<String> test8 = method.getAttributes(request, "test8");
			assertNotNull(test8);
			assertEquals("tricky",test8.get(0));
			assertEquals(";;;",test8.get(1));
			assertEquals("?",test8.get(2));
			assertEquals(3,test8.size());
		}

		{
			List<String> test9 = method.getAttributes(request, "test9");
			assertNotNull(test9);
			assertEquals(0,test9.size());
		}
	}
	
	/**
	 * Internal method to generate a Play Request object.
	 * 
	 * @param headers
	 *            The headers to include on the request.
	 * @return A new request object.
	 */
	private static Request buildRequest(Map<String, String> headers) {

		Request request = Request.createRequest(null, null, null, null, null,
				null, null, null, false, 80, null, true, null, null);

		for (String name : headers.keySet()) {
			request.headers.put(name,new Header(name,headers.get(name)));
		}
		
		return request;
		
	}
	
	
}
