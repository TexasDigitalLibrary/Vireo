package org.tdl.vireo.email.impl;

import java.util.Map;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.junit.Test;
import org.tdl.vireo.constant.AppPref;
import org.tdl.vireo.email.VireoEmail;
import org.tdl.vireo.model.MockPerson;
import org.tdl.vireo.model.MockPreference;
import org.tdl.vireo.model.MockSubmission;
import org.tdl.vireo.model.Preference;
import org.tdl.vireo.security.SecurityContext;
import org.tdl.vireo.state.MockState;

import play.modules.spring.Spring;
import play.test.UnitTest;

/**
 * Test the vireo email object.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class VireoEmailImplTest extends UnitTest {

	// Spring dependencies
	public static SecurityContext context = Spring.getBeanOfType(SecurityContext.class);

	/**
	 * Test creating a vireo email object with it's initial default values
	 */
	@Test
	public void testCreation() {
		context.logout();

		try {
			VireoEmail email = Spring.getBeanOfType(VireoEmailImpl.class);

			assertTrue(email.getFrom() != null);
			assertTrue(email.getReplyTo() != null);
			assertTrue(email.getCc().size() == 0);

			// Now try with someone logged in.
			MockPreference pref = new MockPreference();
			pref.name = AppPref.CC_EMAILS;

			MockPerson person = new MockPerson();
			person.currentEmailAddress = "current@email.com";
			person.firstName = "First";
			person.lastName = "Last";
			person.preferences.add(pref);
			context.login(person);

			email = Spring.getBeanOfType(VireoEmailImpl.class);
			assertTrue(email.getCc().size() == 1);
			assertEquals("current@email.com",email.getCc().get(0).getAddress());

		} finally {
			context.logout();
		}
	}

	/**
	 * Test all the lists of addresses.
	 */
	@Test
	public void testLists() throws AddressException {

		MockPerson person = new MockPerson();
		person.firstName = "First";
		person.lastName = "Last";
		person.currentEmailAddress = "four@email.com";
		
		VireoEmail email = Spring.getBeanOfType(VireoEmailImpl.class);

		// To fields
		email.addTo("one@email.com");
		email.addTo("two@email.com","Two");
		email.addTo(new InternetAddress("three@email.com"));
		email.addTo(person);

		assertEquals("one@email.com",email.getTo().get(0).getAddress());
		assertEquals("two@email.com",email.getTo().get(1).getAddress());
		assertEquals("Two",email.getTo().get(1).getPersonal());
		assertEquals("three@email.com",email.getTo().get(2).getAddress());
		assertEquals("four@email.com",email.getTo().get(3).getAddress());
		assertEquals("First Last",email.getTo().get(3).getPersonal());
		
		// CC fields
		email.addCc("one@email.com");
		email.addCc("two@email.com","Two");
		email.addCc(new InternetAddress("three@email.com"));
		email.addCc(person);

		assertEquals("one@email.com",email.getCc().get(0).getAddress());
		assertEquals("two@email.com",email.getCc().get(1).getAddress());
		assertEquals("Two",email.getCc().get(1).getPersonal());
		assertEquals("three@email.com",email.getCc().get(2).getAddress());
		assertEquals("four@email.com",email.getCc().get(3).getAddress());
		assertEquals("First Last",email.getCc().get(3).getPersonal());

		// Bcc fields
		email.addBcc("one@email.com");
		email.addBcc("two@email.com","Two");
		email.addBcc(new InternetAddress("three@email.com"));
		email.addBcc(person);

		assertEquals("one@email.com",email.getBcc().get(0).getAddress());
		assertEquals("two@email.com",email.getBcc().get(1).getAddress());
		assertEquals("Two",email.getBcc().get(1).getPersonal());
		assertEquals("three@email.com",email.getBcc().get(2).getAddress());
		assertEquals("four@email.com",email.getBcc().get(3).getAddress());
		assertEquals("First Last",email.getBcc().get(3).getPersonal());
		
//		// From field - no longer used as it causes bounced emails
//		email.setFrom("from1@email.com");
//		assertEquals("from1@email.com",email.getFrom().getAddress());
//
//		email.setFrom("from2@email.com","From");
//		assertEquals("from2@email.com",email.getFrom().getAddress());
//		assertEquals("From",email.getFrom().getPersonal());
//
//		email.setFrom(new InternetAddress("from3@email.com"));
//		assertEquals("from3@email.com",email.getFrom().getAddress());
//		
//		email.setFrom(person);
//		assertEquals("four@email.com",email.getFrom().getAddress());
//		assertEquals("First Last",email.getFrom().getPersonal());

		// ReplyTo field
		email.setReplyTo("reply1@email.com");
		assertEquals("reply1@email.com",email.getReplyTo().getAddress());

		email.setReplyTo("reply2@email.com","Reply");
		assertEquals("reply2@email.com",email.getReplyTo().getAddress());
		assertEquals("Reply",email.getReplyTo().getPersonal());

		email.setReplyTo(new InternetAddress("reply3@email.com"));
		assertEquals("reply3@email.com",email.getReplyTo().getAddress());
		
		email.setReplyTo(person);
		assertEquals("four@email.com",email.getReplyTo().getAddress());
		assertEquals("First Last",email.getReplyTo().getPersonal());
	}

	/**
	 * Test replacing paramaters
	 */
	@Test
	public void testParameters() {

		VireoEmail email = Spring.getBeanOfType(VireoEmailImpl.class);

		email.addParameter("ONE", "111");
		email.addParameter("TWO", "222");

		email.setSubject("The Subject is {ONE},{TWO},{THREE}");
		email.setMessage("The Message is {ONE},{TWO},{THREE}");

		email.applyParameterSubstitution();

		assertEquals("The Subject is 111,222,{THREE}",email.getSubject());
		assertEquals("The Message is 111,222,{THREE}",email.getMessage());
	}

	/**
	 * Test extrating parameters from a submission.
	 */
	@Test
	public void testParametersFromSubmission() {

		MockState state = new MockState();
		state.displayName = "Mock State";

		MockPerson assignee = new MockPerson();
		assignee.firstName = "Assigned";
		assignee.lastName = "Reviewer";

		MockSubmission sub = new MockSubmission();
		sub.studentFirstName = "First";
		sub.studentLastName = "Last";
		sub.documentTitle = "Document Title";
		sub.graduationYear = 2010;
		sub.graduationMonth = 4;
		sub.state = state;
		sub.assignee = assignee;

		VireoEmail email = Spring.getBeanOfType(VireoEmailImpl.class);

		email.addParameters(sub);

		Map<String,String> params = email.getParameters();
		assertEquals("First Last",params.get("FULL_NAME"));
		assertEquals("First",params.get("FIRST_NAME"));
		assertEquals("Last",params.get("LAST_NAME"));
		assertEquals("Document Title",params.get("DOCUMENT_TITLE"));
		assertEquals("May, 2010",params.get("GRAD_SEMESTER"));
		assertEquals("Mock State",params.get("SUBMISSION_STATUS"));
		assertEquals("Assigned Reviewer",params.get("SUBMISSION_ASSIGNED_TO"));		
	}

	/**
	 * Test the log handling.
	 */
	@Test
	public void testLogging() {

		MockPerson reviewer = new MockPerson();
		reviewer.firstName = "Assigned";
		reviewer.lastName = "Reviewer";

		MockSubmission sub = new MockSubmission();
		sub.studentFirstName = "First";
		sub.studentLastName = "Last";
		sub.documentTitle = "Document Title";
		sub.graduationYear = 2010;
		sub.graduationMonth = 4;


		VireoEmail email = Spring.getBeanOfType(VireoEmailImpl.class);

		email.setLogOnCompletion(reviewer, sub);

		// We can't really test retrieving them with out using real objects...

		email.setSuccessLogMessage("This is a success");
		email.setFailureLogMessage("This is a failure");

		assertEquals("This is a success",email.getSuccessLogMessage());
		assertEquals("This is a failure",email.getFailureLogMessage("reason"));
	}
}
