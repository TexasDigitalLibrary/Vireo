package notifiers;

import static org.junit.Assert.*;

import notifiers.Notifier;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tdl.vireo.model.MockPerson;
import org.tdl.vireo.model.Person;

import play.libs.Mail;
import play.test.UnitTest;

public class NotifierTest extends UnitTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

    @Test
    public void testReview() throws Exception {
        Person reviewer = MockPerson.getReviewer();
        Notifier.review(reviewer);
        String email = Mail.Mock.getLastMessageReceivedBy(reviewer.getEmail());
        assertTrue("Expected subject", email.contains("Subject: Welcome "+ reviewer.getFullName()));
        assertTrue("Expected email address " + reviewer.getEmail() + " but got" +email, email.contains(reviewer.getEmail()));
    }

//	@Test
//	public void testLostPassword() {
//		fail("Not yet implemented");
//	}

//	@Test
//	public void testMailer() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testSetSubject() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testAddRecipient() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testAddBcc() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testAddCc() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testAddAttachment() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testSetContentType() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testSetFrom() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testSetReplyTo() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testSetCharset() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testAddHeader() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testSend() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testSendAndWait() {
//		fail("Not yet implemented");
//	}

}
