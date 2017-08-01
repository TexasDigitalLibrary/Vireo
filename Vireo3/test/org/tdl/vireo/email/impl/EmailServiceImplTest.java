package org.tdl.vireo.email.impl;

import java.util.List;

import javax.mail.internet.AddressException;

import org.junit.Test;
import org.tdl.vireo.email.EmailService;
import org.tdl.vireo.email.VireoEmail;
import org.tdl.vireo.model.ActionLog;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.RoleType;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.jpa.JpaPersonRepositoryImpl;
import org.tdl.vireo.model.jpa.JpaSubmissionRepositoryImpl;
import org.tdl.vireo.security.SecurityContext;

import play.libs.Mail;
import play.modules.spring.Spring;
import play.test.UnitTest;

/**
 * Test sending vireo emails.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class EmailServiceImplTest extends UnitTest {

	public EmailService emailService = Spring.getBeanOfType(EmailServiceImpl.class);
	public static SecurityContext context = Spring.getBeanOfType(SecurityContext.class);
	public static JpaPersonRepositoryImpl personRepo = Spring.getBeanOfType(JpaPersonRepositoryImpl.class);
	public static JpaSubmissionRepositoryImpl subRepo = Spring.getBeanOfType(JpaSubmissionRepositoryImpl.class);

	/**
	 * Test sending a very basic email without any logging or other statefull objects.
	 */
	@Test
	public void testSendingEmail() throws AddressException, InterruptedException {
		Mail.Mock.reset();
		
		VireoEmail email = Spring.getBeanOfType(VireoEmailImpl.class);

		email.addTo("email@email.com");
		email.setSubject("This is a really important email");
		email.setMessage("Here's the meat of what is really cool.");
		
		emailService.sendEmail(email, true);
				
		String recieved = Mail.Mock.getLastMessageReceivedBy("email@email.com");
		
		
		assertNotNull(recieved);
		assertTrue(recieved.contains("Subject: This is a really important email"));
		assertTrue(recieved.contains("Here's the meat of what is really cool."));
	}
	
	/**
	 * Test sending an email and logging the successful result.
	 */
	@Test
	public void testSendingEmailAndLoggingSuccess() throws AddressException {
		Mail.Mock.reset();

		context.turnOffAuthorization();
		Person person = personRepo.createPerson("netid", "email@email.com", "first", "last", RoleType.NONE).save();
		Submission sub = subRepo.createSubmission(person).save();
		
		VireoEmail email = Spring.getBeanOfType(VireoEmailImpl.class);
		email.addTo("email@email.com");
		email.setSubject("This is a really important email");
		email.setMessage("Here's the meat of what is really cool.");
		email.setLogOnCompletion(person, sub);
		
		emailService.sendEmail(email, true);

		// Verify the email
		String recieved = Mail.Mock.getLastMessageReceivedBy("email@email.com");
		assertNotNull(recieved);
		assertTrue(recieved.contains("Subject: This is a really important email"));
		assertTrue(recieved.contains("Here's the meat of what is really cool."));
		
		// Verify the log message
		List<ActionLog> logs = subRepo.findActionLog(sub);
		assertEquals("Email sent to: [ email@email.com ]; This is a really important email: 'Here's the meat of what is really cool.'",logs.get(0).getEntry());
		assertEquals(person,logs.get(0).getPerson());
		
		context.turnOffAuthorization();
		sub.delete();
		person.delete();
		context.restoreAuthorization();
	}
	
	/**
	 * Test sending an email and logging the failure.
	 */
	@Test
	public void testSendingEmailAndLoggingFailure() {
		Mail.Mock.reset();

		context.turnOffAuthorization();
		Person person = personRepo.createPerson("netid", "email@email.com", "first", "last", RoleType.NONE).save();
		Submission sub = subRepo.createSubmission(person).save();
		
		VireoEmail email = Spring.getBeanOfType(VireoEmailImpl.class);
		email.setSubject("This is a really important email");
		email.setMessage("Here's the meat of what is really cool.");
		email.setLogOnCompletion(person, sub);
		
		try {
			emailService.sendEmail(email, true);
			fail("Email service failed to throw an error when no recipients were defined.");
		} catch (RuntimeException re) {
			// yay, it should happen.
		}

		// Verify the non-email
		String recieved = Mail.Mock.getLastMessageReceivedBy("email@email.com");
		assertNull(recieved);
		
		// Verify the log message
		List<ActionLog> logs = subRepo.findActionLog(sub);
		assertEquals("Failed to send email to  ; This is a really important email: 'Here's the meat of what is really cool.' because 'Please define a recipient email address'",logs.get(0).getEntry());
		assertEquals(person,logs.get(0).getPerson());
		
		context.turnOffAuthorization();
		sub.delete();
		person.delete();
		context.restoreAuthorization();
	}
	
	
}
