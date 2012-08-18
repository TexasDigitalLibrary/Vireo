package org.tdl.vireo.state.impl;

import org.junit.Test;
import org.tdl.vireo.model.AttachmentType;
import org.tdl.vireo.model.MockAttachment;
import org.tdl.vireo.model.MockPerson;
import org.tdl.vireo.model.MockSubmission;
import org.tdl.vireo.state.MockState;

import play.libs.Mail;
import play.modules.spring.Spring;
import play.test.UnitTest;

public class StudentSubmissionCompletedListenerImplTest extends UnitTest {

	public static StudentSubmissionCompletedListenerImpl listener = Spring.getBeanOfType(StudentSubmissionCompletedListenerImpl.class);
	
	@Test
	public void testListener() throws InterruptedException {
		Mail.Mock.reset();
		
		// Setup a mock submission
		MockPerson submitter = new MockPerson();
		submitter.email = "student@noreply.org";
		
		MockAttachment primary = new MockAttachment();
		primary.setName("original.pdf");
		primary.type = AttachmentType.PRIMARY;
		
		MockSubmission sub = new MockSubmission();
		sub.submitter = submitter;
		sub.committeeContactEmail = "committee@noreply.org";
		sub.studentFirstName = "first";
		sub.studentLastName = "last";
		sub.documentType = "Thesis";
		sub.documentTitle = "My Test Thesis";
		sub.graduationMonth = 06;
		sub.graduationYear = 2012;
		sub.attachments.add(primary);
		
		MockState previous = new MockState();
		previous.inProgress = true;
		
		MockState current = new MockState();
		current.inProgress = false;
		
		sub.state = current;
		
		// Run the service
		listener.transition(sub, previous);
		
		// Wait for the emails to be recieved.
		String studentEmail = null;
		for (int i = 0; i < 1000; i++) {
			Thread.yield();
			Thread.sleep(100);
			studentEmail = Mail.Mock.getLastMessageReceivedBy("student@noreply.org");
			if (studentEmail != null)
				break;
		}
		assertNotNull(studentEmail);
		
		String advisorEmail = null;
		for (int i = 0; i < 1000; i++) {
			Thread.yield();
			Thread.sleep(100);
			advisorEmail = Mail.Mock.getLastMessageReceivedBy("committee@noreply.org");
			if (advisorEmail != null)
				break;
		}
		assertNotNull(advisorEmail);
		
		
		// Verify state.
		assertNotNull(sub.getCommitteeEmailHash());
		assertTrue(advisorEmail.contains(sub.getCommitteeEmailHash()));
		assertEquals("LAST-THESIS.pdf",sub.getPrimaryDocument().getName());
		
		
	}
	
	
}
