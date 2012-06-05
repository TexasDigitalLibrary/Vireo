package org.tdl.vireo.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.tdl.vireo.model.AttachmentType;
import org.tdl.vireo.model.MockAttachment;
import org.tdl.vireo.model.MockEmailTemplate;
import org.tdl.vireo.model.MockPerson;
import org.tdl.vireo.model.MockSubmission;
import org.tdl.vireo.services.EmailService;
import org.tdl.vireo.services.EmailService.TemplateParameters;
import org.tdl.vireo.state.StateManager;

import play.Play;
import play.libs.Mail;
import play.modules.spring.Spring;
import play.test.UnitTest;

/**
 * Test the email service using Play's mock email.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 * @author Joe DeVries
 */
public class EmailServiceImplTest extends UnitTest {
	
	// Spring injection
	public StateManager stateManager = Spring.getBeanOfType(StateManager.class);
	public EmailService emailService = Spring.getBeanOfType(EmailServiceImpl.class);
	
	/**
	 * Test that an email is sent and all the parameters are replaced with what
	 * they should be.
	 */
	@Test
	public void testSendingEmail() throws InterruptedException {
		Mail.Mock.reset();
		
		// Create mock template with all parameters
		MockEmailTemplate template = new MockEmailTemplate();
		template.name = "Template Name";
		template.subject = "Template Subject";
		template.message = "\n\nThis is the email message body: \n"; 
		template.message += "Full Name: {FULL_NAME} \n"; 
		template.message += "First Name: {FIRST_NAME} \n"; 
		template.message += "Last Name: {LAST_NAME} \n"; 
		template.message += "Document Title: {DOCUMENT_TITLE} \n"; 
		template.message += "Document Type: {DOCUMENT_TYPE} \n"; 
		template.message += "Graduation Semester: {GRAD_SEMESTER} \n"; 
		template.message += "Student URL: {STUDENT_URL} \n"; 
		template.message += "Advisor URL: {ADVISOR_URL} \n"; 
		template.message += "Registration URL: {REGISTRATION_URL} \n"; 
		template.message += "Submission Status: {SUBMISSION_STATUS} \n"; 
		template.message += "Assigned To: {SUBMISSION_ASSIGNED_TO} \n";
		
		// Create mock data to fill into as parameter values.
		MockPerson submitter = new MockPerson();
		MockSubmission submission = new MockSubmission();
		MockPerson assignee = new MockPerson();
		MockAttachment primaryDocument = new MockAttachment();
		
		// Example properties
		submitter.firstName = "Student";
		submitter.lastName = "Submitter";
		assignee.firstName = "Staff";
		assignee.lastName = "Reviewer";
		primaryDocument.name = "MyThesis.pdf";
		primaryDocument.type = AttachmentType.PRIMARY;
		submission.documentType = "Thesis";
		submission.state = stateManager.getInitialState();
		submission.graduationYear = 2012;
		submission.graduationMonth = 4;
		
		// Link everything together
		submission.submitter = submitter;
		submission.assignee = assignee;
		submission.attachments.add(primaryDocument);
		
		List<String> recipients = new ArrayList<String>();
		recipients.add("email@email.com");
		
		// Send the email:
		TemplateParameters params = new TemplateParameters(submission);
		params.STUDENT_URL = "http://studenturl/";
		params.ADVISOR_URL = "http://advisorurl/";
		params.REGISTRATION_URL = "http://registerurl/";
		emailService.sendEmail(template, params, recipients, "noreply@email.com");
		
		// Wait for the email thread to send the email.
		String recieved = null;
		for (int i = 0; i < 1000; i++) {
			Thread.yield();
			Thread.sleep(100);
			recieved = Mail.Mock.getLastMessageReceivedBy("email@email.com");
			if (recieved != null)
				break;
		}
		assertNotNull(recieved);
		
		// Check the email received for completeness
		assertTrue(recieved.contains("From: "+Play.configuration.getProperty("mail.from")));
		assertTrue(recieved.contains("ReplyTo: noreply@email.com"));
		assertTrue(recieved.contains("To: \"email@email.com\" <email@email.com>"));
		assertTrue(recieved.contains("Subject: Template Subject"));

		assertTrue(recieved.contains("Full Name: Student Submitter \n"));
		assertTrue(recieved.contains("First Name: Student \n"));
		assertTrue(recieved.contains("Last Name: Submitter \n"));
		assertTrue(recieved.contains("Document Title: MyThesis.pdf \n"));
		assertTrue(recieved.contains("Document Type: Thesis \n"));
		assertTrue(recieved.contains("Graduation Semester: May, 2012 \n"));
		assertTrue(recieved.contains("Student URL: http://studenturl/ \n"));
		assertTrue(recieved.contains("Advisor URL: http://advisorurl/ \n"));
		assertTrue(recieved.contains("Registration URL: http://registerurl/ \n"));
		assertTrue(recieved.contains("Submission Status: "+stateManager.getInitialState().getDisplayName()));
		assertTrue(recieved.contains("Assigned To: Staff Reviewer \n"));
	}

}
