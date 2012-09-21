package org.tdl.vireo.model.jpa;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tdl.vireo.model.ActionLog;
import org.tdl.vireo.model.Attachment;
import org.tdl.vireo.model.AttachmentType;
import org.tdl.vireo.model.MockPerson;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.RoleType;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.security.SecurityContext;
import org.tdl.vireo.state.State;
import org.tdl.vireo.state.StateManager;

import play.db.jpa.JPA;
import play.modules.spring.Spring;
import play.test.UnitTest;


/**
 * Test the Jpa specefic implementation of the Attachment interface
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class JpaAttachmentImplTest extends UnitTest {

	// Persistence repositories
	public static SecurityContext context = Spring.getBeanOfType(SecurityContext.class);
	public static StateManager stateManager = Spring.getBeanOfType(StateManager.class);
	public static JpaPersonRepositoryImpl personRepo = Spring.getBeanOfType(JpaPersonRepositoryImpl.class);
	public static JpaSubmissionRepositoryImpl subRepo = Spring.getBeanOfType(JpaSubmissionRepositoryImpl.class);
	
	// Share the same person & submission
	public static Person person;
	public static Submission sub;
	
	/**
	 * Create a new person & submission for each test.
	 */
	@Before
	public void setup() {
		context.login(MockPerson.getAdministrator());
		person = personRepo.createPerson("netid", "email@email.com", "first", "last", RoleType.NONE).save();
		sub = subRepo.createSubmission(person).save();
	}
	
	/**
	 * Cleanup the person & submission after each test.
	 */
	@After
	public void cleanup() {
		try {
		JPA.em().clear();
		if (sub != null)
			subRepo.findSubmission(sub.getId()).delete();
		
		if (person != null)
			personRepo.findPerson(person.getId()).delete();
		context.logout();
		
		JPA.em().getTransaction().rollback();
		JPA.em().getTransaction().begin();
		} catch (RuntimeException re) {
			
		}
	}
	
	/**
	 * Test creating an attachment.
	 */
	@Test
	public void testCreateAttachment() throws IOException {
		
		File file = createRandomFile(10L);
		
		Attachment attachment = sub.addAttachment(file, AttachmentType.SUPPLEMENTAL).save();
		
		assertNotNull(attachment);
		assertEquals(file.getName(),attachment.getName());
		assertEquals(AttachmentType.SUPPLEMENTAL,attachment.getType());
	
		attachment.delete();
		file.delete();
	}
	
	/**
	 * Test creating a duplicate attachments.
	 */
	@Test
	public void testCreatingDuplicateAttachments() throws IOException {
		
		File file = createRandomFile(10L);
		
		Attachment attachment1 = sub.addAttachment(file, AttachmentType.SUPPLEMENTAL).save();
		Attachment attachment2 = sub.addAttachment(file, AttachmentType.SUPPLEMENTAL).save();
		
		assertFalse(attachment1.getName().equals(attachment2.getName()));
		file.delete();
		
		attachment1.delete();
		attachment2.delete();
	}
	
	/**
	 * Test renaming when adding a primary document.
	 */
	@Test
	public void testCreatingPrimaryDocumentWithAllInfo() throws IOException {
		File file = createRandomFile(10L);
		
		sub.setStudentLastName("last");
		sub.setDocumentType("Thesis");
		sub.setGraduationYear(2010);
		sub.save();
		
		Attachment attachment = sub.addAttachment(file, AttachmentType.PRIMARY).save();
		
		assertNotNull(attachment);
		assertEquals("LAST-THESIS-2010.dat",attachment.getName());
		assertEquals(AttachmentType.PRIMARY,attachment.getType());
	
		attachment.delete();
		file.delete();
	}
	
	/**
	 * Test renaming when adding a primary document without a name
	 */
	@Test
	public void testCreatingPrimaryDocumentWithOutName() throws IOException {
		File file = createRandomFile(10L);
		
		sub.setDocumentType("Thesis");
		sub.setGraduationYear(2010);
		sub.save();
		
		Attachment attachment = sub.addAttachment(file, AttachmentType.PRIMARY).save();
		
		assertNotNull(attachment);
		assertEquals("PRIMARY-THESIS-2010.dat",attachment.getName());
		assertEquals(AttachmentType.PRIMARY,attachment.getType());
	
		attachment.delete();
		file.delete();
	}
	
	/**
	 * Test renaming when adding a primary document without a document type
	 */
	@Test
	public void testCreatingPrimaryDocumentWithOutDocType() throws IOException {
		File file = createRandomFile(10L);
		
		sub.setStudentLastName("last");
		sub.setGraduationYear(2010);
		sub.save();
		
		Attachment attachment = sub.addAttachment(file, AttachmentType.PRIMARY).save();
		
		assertNotNull(attachment);
		assertEquals("LAST-DOCUMENT-2010.dat",attachment.getName());
		assertEquals(AttachmentType.PRIMARY,attachment.getType());
	
		attachment.delete();
		file.delete();
	}
	
	/**
	 * Test renaming when adding a primary document without grad year
	 */
	@Test
	public void testCreatingPrimaryDocumentWithoutGradYear() throws IOException {
		File file = createRandomFile(10L);
		
		sub.setStudentLastName("last");
		sub.setDocumentType("Thesis");
		sub.save();
		
		Attachment attachment = sub.addAttachment(file, AttachmentType.PRIMARY).save();
		
		assertNotNull(attachment);
		assertEquals("LAST-THESIS.dat",attachment.getName());
		assertEquals(AttachmentType.PRIMARY,attachment.getType());
	
		attachment.delete();
		file.delete();
	}
	
	
	
	/**
	 * Test creating an attachment from a byte array.
	 */
	@Test
	public void testCreateFromByteArray() throws IOException {
		
		byte[] content = "This is test content".getBytes();
		
		Attachment attachment = sub.addAttachment(content, "test.txt",AttachmentType.LICENSE).save();
		
		assertNotNull(attachment);
		assertEquals("test.txt",attachment.getName());
		assertEquals(AttachmentType.LICENSE,attachment.getType());
		
		File file = attachment.getFile();
		String retrieved = FileUtils.readFileToString(file);
		assertEquals("This is test content",retrieved);
	
		attachment.delete();
		
	}
	
	/**
	 * Test creating an attachment with an owner.
	 */
	@Test
	public void testCreateAttachmentWithOwner() throws IOException {
		
		Person person = personRepo.createPerson("uploader", "uploader@email.com", "firstName", "lastName", RoleType.ADMINISTRATOR).save();		
		context.logout();
		context.login(person);
		
		File file = createRandomFile(10L);
		
		Attachment attachment = sub.addAttachment(file, AttachmentType.PRIMARY).save();
		
		assertEquals(person,attachment.getPerson());
		assertNotNull(attachment.getDate());
		
		attachment.delete();
		file.delete();
		sub.delete();
		sub = null;
		person.delete();
	}
	
	/**
	 * Tests creating a bad attachment.
	 */
	@Test
	public void testBadCreateAttachment() throws IOException {
		
		File file = createRandomFile(10L);

		try {
			sub.addAttachment(file, null);
			fail("Able to create attachment with null type");
		} catch (IllegalArgumentException iae) {
			/* yay */
		}

		try {
			sub.addAttachment(null, AttachmentType.PRIMARY);
			fail("Able to create attachment with null file");
		} catch (IllegalArgumentException iae) {
			/* yay */
		}
		
		file.delete();
	}
	
	/**
	 * Test creating a second primary document.
	 */
	@Test
	public void testCreateDuplicatePrimary() throws IOException {
		
		File file = createRandomFile(10L);
		
		Attachment attachment = sub.addAttachment(file, AttachmentType.PRIMARY).save();
		
		assertNotNull(attachment);
		assertEquals(AttachmentType.PRIMARY,attachment.getType());
		
		
		// Create duplicate
		try {
			sub.addAttachment(file, AttachmentType.PRIMARY).save();
			fail("Able to create duplicate primary document.");
		} catch (RuntimeException re) {
			/* yay */
		}
		
		file.delete();
	}
	
	/**
	 * Test that attachments have ids
	 */
	@Test
	public void testId() throws IOException {
		
		File file = createRandomFile(10L);
		
		Attachment attachment = sub.addAttachment(file, AttachmentType.PRIMARY).save();
		
		assertNotNull(attachment);
		assertNotNull(attachment.getId());
		
		file.delete();
	}
	
	/**
	 * Test retrieving attachment via Id.
	 */
	@Test
	public void testFindById() throws IOException {
		
		File file = createRandomFile(10L);
		
		Attachment attachment = sub.addAttachment(file, AttachmentType.PRIMARY).save();
		
		Attachment retrieved = subRepo.findAttachment(attachment.getId());
		
		assertEquals(attachment.getId(),retrieved.getId());
		assertEquals(attachment.getName(),retrieved.getName());
		
		String originalContent = FileUtils.readFileToString(file);
		String retrievedContent = FileUtils.readFileToString(retrieved.getFile());
		
		assertEquals(originalContent,retrievedContent);
		
		file.delete();
		
	}
	
	/**
	 * Test retrieving attachment via name.
	 */
	@Test
	public void testFindByIdAndName() throws IOException {
		
		File file = createRandomFile(10L);
		
		Attachment attachment = sub.addAttachment(file, AttachmentType.PRIMARY).save();
		
		Attachment retrieved = sub.findAttachmentById(attachment.getId());
		assertEquals(attachment,retrieved);
		
		retrieved = sub.findAttachmentByName(attachment.getName());
		assertEquals(attachment,retrieved);
		
		file.delete();
		
	}
	
	/**
	 * Test retrieving the primary document.
	 */
	@Test 
	public void testFindByPrimary() throws IOException {
		
		File file1 = createRandomFile(10L);
		File file2 = createRandomFile(10L);
		File file3 = createRandomFile(10L);

		
		Attachment a1 = sub.addAttachment(file1, AttachmentType.PRIMARY).save();
		Attachment a2 = sub.addAttachment(file2, AttachmentType.SUPPLEMENTAL).save();
		Attachment a3 = sub.addAttachment(file3, AttachmentType.SUPPLEMENTAL).save();

		
		Attachment primary = sub.getPrimaryDocument();
		assertEquals(a1.getId(),primary.getId());
		
		file1.delete();
		file2.delete();
		file3.delete();
	}
	
	/**
	 * Test retrieving the supplemental documents.
	 */
	@Test
	public void testFindSupplemental() throws IOException {
		
		File file1 = createRandomFile(10L);
		File file2 = createRandomFile(10L);
		File file3 = createRandomFile(10L);

		
		Attachment a1 = sub.addAttachment(file1, AttachmentType.PRIMARY).save();
		Attachment a2 = sub.addAttachment(file2, AttachmentType.SUPPLEMENTAL).save();
		Attachment a3 = sub.addAttachment(file3, AttachmentType.SUPPLEMENTAL).save();
		
		List<Attachment> supplemental = sub.getSupplementalDocuments();
		
		boolean foundA2 = false;
		boolean foundA3 = false;
		
		for (Attachment a : supplemental) {
			if (a.getId() == a2.getId())
				foundA2 = true;
			if (a.getId() == a3.getId())
				foundA3 = true;
		}
		
		assertTrue(foundA2);
		assertTrue(foundA3);
		assertEquals(2,supplemental.size());
		
		file1.delete();
		file2.delete();
		file3.delete();
	}
	
	/**
	 * Test retrieving attchments by type
	 */
	@Test
	public void testFindByType() throws IOException {
		
		File file1 = createRandomFile(10L);
		File file2 = createRandomFile(10L);
		File file3 = createRandomFile(10L);

		
		Attachment a1 = sub.addAttachment(file1, AttachmentType.PRIMARY).save();
		Attachment a2 = sub.addAttachment(file2, AttachmentType.SUPPLEMENTAL).save();
		Attachment a3 = sub.addAttachment(file3, AttachmentType.SUPPLEMENTAL).save();
		
		List<Attachment> supplemental = sub.getAttachmentsByType(AttachmentType.SUPPLEMENTAL);
		
		boolean foundA2 = false;
		boolean foundA3 = false;
		
		for (Attachment a : supplemental) {
			if (a.getId() == a2.getId())
				foundA2 = true;
			if (a.getId() == a3.getId())
				foundA3 = true;
		}
		
		assertTrue(foundA2);
		assertTrue(foundA3);
		assertEquals(2,supplemental.size());
		
		file1.delete();
		file2.delete();
		file3.delete();
	}
	
	/**
	 * Test retrieving all attachments
	 */
	@Test
	public void testGetAttachments() throws IOException {
		
		File file1 = createRandomFile(10L);
		File file2 = createRandomFile(10L);
		File file3 = createRandomFile(10L);

		
		Attachment a1 = sub.addAttachment(file1, AttachmentType.PRIMARY).save();
		Attachment a2 = sub.addAttachment(file2, AttachmentType.SUPPLEMENTAL).save();
		Attachment a3 = sub.addAttachment(file3, AttachmentType.SUPPLEMENTAL).save();
		
		List<Attachment> attachments = sub.getAttachments();
		
		boolean foundA1 = false;
		boolean foundA2 = false;
		boolean foundA3 = false;
		
		for (Attachment a : attachments) {
			if (a.getId() == a1.getId())
				foundA1 = true;
			if (a.getId() == a2.getId())
				foundA2 = true;
			if (a.getId() == a3.getId())
				foundA3 = true;
		}
		
		assertTrue(foundA1);
		assertTrue(foundA2);
		assertTrue(foundA3);
		assertEquals(3,attachments.size());
		
		file1.delete();
		file2.delete();
		file3.delete();
	}
	
	/**
	 * Test updating the primary document name after changes to the submission.
	 */
	@Test
	public void testUpdatingPrimaryDocumentName() throws IOException {
		File file = createRandomFile(10L);
		
		Attachment attachment = sub.addAttachment(file, AttachmentType.PRIMARY).save();
		
		assertNotNull(attachment);
		assertEquals("PRIMARY-DOCUMENT.dat",attachment.getName());
		assertEquals(AttachmentType.PRIMARY,attachment.getType());
		
		
		sub.setStudentLastName("last");
		sub.setDocumentType("Thesis");
		assertEquals("LAST-THESIS.dat",attachment.getName());
		
		sub.setGraduationYear(2010);
		assertEquals("LAST-THESIS-2010.dat",attachment.getName());
		
		sub.setStudentLastName(null);
		assertEquals("PRIMARY-THESIS-2010.dat",attachment.getName());
		
		sub.save();
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		sub = subRepo.findSubmission(sub.getId());
	
		assertEquals("PRIMARY-THESIS-2010.dat",attachment.getName());		
	
		
		// clean everything up
		subRepo.findAttachment(attachment.getId()).delete();
		subRepo.findSubmission(sub.getId()).delete();
		personRepo.findPerson(person.getId()).delete();
		
		sub = null;
		person = null;
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		file.delete();
	}
	
	/**
	 * Test updating the primary document name after changes to the submission.
	 */
	@Test
	public void testArchiving() throws IOException {
		File file = createRandomFile(10L);
		
		Attachment attachment = sub.addAttachment(file, AttachmentType.PRIMARY).save();
		
		assertNotNull(attachment);
		assertEquals("PRIMARY-DOCUMENT.dat",attachment.getName());
		assertEquals(AttachmentType.PRIMARY,attachment.getType());
		
		attachment.archive();
		
		assertEquals(AttachmentType.ARCHIVED,attachment.getType());
		assertEquals("PRIMARY-DOCUMENT-archived-on-"+JpaAttachmentImpl.dateFormat.format(new Date())+".dat",attachment.getName());
		
		attachment.delete();
		file.delete();
	}
	
	/**
	 * Test property validation
	 */
	@Test
	public void testPropertyValidation() throws IOException {		
		File file = createRandomFile(10L);

		Attachment attachment = sub.addAttachment(file, AttachmentType.PRIMARY).save();
		
		try {
			attachment.setName(null);
			fail("able to set attachment name to null.");
		} catch (IllegalArgumentException iae) {
			/* yay */
		}
		
		try {
			attachment.setName("");
			fail("able to set attachment name to blank.");
		} catch (IllegalArgumentException iae) {
			/* yay */
		}
		
		try {
			attachment.setType(null);
			fail("able to set attachment type to null.");
		} catch (IllegalArgumentException iae) {
			/* yay */
		}
		
		file.delete();
	}
	
	/**
	 * Test creating a duplicate primary document by modification.
	 */
	@Test
	public void testModifyToDuplicatePrimary() throws IOException {
		File file1 = createRandomFile(10L);
		File file2 = createRandomFile(10L);

		
		Attachment a1 = sub.addAttachment(file1, AttachmentType.PRIMARY).save();
		Attachment a2 = sub.addAttachment(file2, AttachmentType.SUPPLEMENTAL).save();
		

		try {
			a2.setType(AttachmentType.PRIMARY);
			a2.save();
			fail("Able to create duplicate primary document.");
		} catch (RuntimeException re) {
			/* yay */
		}
		
		file1.delete();
		file2.delete();
	}
	
	/**
	 * Test determining the mimetype of a file.
	 */
	@Test
	public void testMimeType() throws IOException {
		
		File file = createRandomFile(10L);

		
		Attachment attachment = sub.addAttachment(file, AttachmentType.PRIMARY).save();
		assertEquals("application/octet-stream",attachment.getMimeType());
		
		file.delete();
	}
	
	/**
	 * Test determining the size of a file, both in bytes and in english.
	 */
	@Test
	public void testSize() throws IOException {
	
		// Test both display size and regular size.
		File file1 = createRandomFile(10L);
		File file2 = createRandomFile(1024L);
		File file3 = createRandomFile(1024L * 1024L);
		File file4 = createRandomFile(1024L * 1024L * 5L);

		
		Attachment a1 = sub.addAttachment(file1, AttachmentType.PRIMARY).save();
		Attachment a2 = sub.addAttachment(file2, AttachmentType.SUPPLEMENTAL).save();
		Attachment a3 = sub.addAttachment(file3, AttachmentType.SUPPLEMENTAL).save();
		Attachment a4 = sub.addAttachment(file4, AttachmentType.SUPPLEMENTAL).save();

		
		assertEquals(10L,a1.getSize());
		assertEquals(1024L,a2.getSize());
		assertEquals(1024L * 1024L,a3.getSize());
		assertEquals(1024L * 1024L * 5L,a4.getSize());

		
		assertEquals("10 bytes",a1.getDisplaySize());
		assertEquals("1 KB",a2.getDisplaySize());
		assertEquals("1 MB",a3.getDisplaySize());
		assertEquals("5 MB",a4.getDisplaySize());
		
		file1.delete();
		file2.delete();
		file3.delete();
		file4.delete();
	}
	
	/**
	 * Test the handling of files.
	 */
	@Test
	public void testFile() throws IOException {
		
		File file1 = createRandomFile(10L);

		Attachment a1 = sub.addAttachment(file1, AttachmentType.SUPPLEMENTAL).save();

		assertTrue(a1.getFile().exists());
		assertFalse(file1.getAbsolutePath().equals(a1.getFile().getAbsolutePath()));
		
		String originalContent = FileUtils.readFileToString(file1);
		String retrievedContent = FileUtils.readFileToString(a1.getFile());
		assertEquals(originalContent,retrievedContent);
		
		file1.delete();
	}
	
	/**
	 * Test that action logs are generated appropriately.
	 */
	@Test
	public void testActionLogGeneration() throws IOException {
		File file = createRandomFile(10L);

		State initialState = stateManager.getInitialState();
		State nextState = initialState.getTransitions(sub).get(0);
		sub.setState(nextState);
		sub.save();
		
		Attachment attachment = sub.addAttachment(file, AttachmentType.FEEDBACK);
		attachment.save();
		attachment.setName("newPDF.pdf");
		attachment.save();
		attachment.setType(AttachmentType.SUPPLEMENTAL);
		attachment.save();
		attachment.delete();
		
		List<ActionLog> logs = subRepo.findActionLog(sub);
		Iterator<ActionLog> logItr = logs.iterator();
		
		sub.delete();
		sub = null;
		
		assertEquals("SUPPLEMENTAL file 'newPDF.pdf' (10 bytes) removed", logItr.next().getEntry());
		assertEquals("SUPPLEMENTAL file 'newPDF.pdf' modified", logItr.next().getEntry());
		assertEquals("FEEDBACK file 'newPDF.pdf' modified", logItr.next().getEntry());
		assertEquals("FEEDBACK file '"+file.getName()+"' (10 bytes) uploaded", logItr.next().getEntry());
		assertEquals("Submission status changed to 'Submitted'",logItr.next().getEntry());
		assertEquals("Submission created",logItr.next().getEntry());
		
		assertFalse(logItr.hasNext());
		
		file.delete();
	}
	
	
	/**
	 * Test that attachments are persistent.
	 */
	@Test
	public void testPersistance() throws IOException {
		
		File file1 = createRandomFile(10L);
		Attachment attachment = sub.addAttachment(file1, AttachmentType.SUPPLEMENTAL).save();
		
		// Commit and reopen a new transaction.
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		attachment = subRepo.findAttachment(attachment.getId());
		assertEquals(attachment.getName(),file1.getName());
		
		String originalContent = FileUtils.readFileToString(file1);
		String retrievedContent = FileUtils.readFileToString(attachment.getFile());
		assertEquals(originalContent,retrievedContent);


		attachment.delete();
		subRepo.findSubmission(sub.getId()).delete();
		personRepo.findPerson(person.getId()).delete();
		
		sub = null;
		person = null;
		
		// Commit and reopen a new transaction.
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
	}
	
	/**
	 * Test that attachments delete correctly. Specifically that the parent
	 * submission is updated and the actual file is deleted.
	 */
	@Test
	public void testDelete() throws IOException {
		File file1 = createRandomFile(10L);
		String originalContent = FileUtils.readFileToString(file1);

		
		Attachment a1 = sub.addAttachment(file1, AttachmentType.SUPPLEMENTAL).save();
		sub.refresh();
		
		file1.delete();
		File attachmentFile = a1.getFile();
		assertTrue(attachmentFile.exists());
		
		String retrievedContent = FileUtils.readFileToString(attachmentFile);
		assertEquals(originalContent,retrievedContent);
		
		a1.delete();

		// Check that the file is deleted.
		assertFalse(attachmentFile.exists());
		
		// Check the list of attachments
		List<Attachment> attachments = sub.getAttachments();
		assertEquals(0,attachments.size());
		
		file1.delete();
	}
	
	/**
	 * Test who has access to add/modify/delete attachments.
	 */
	@Test
	public void testAccess() throws IOException {
		File file1 = createRandomFile(10L);
		File file2 = createRandomFile(10L);
		File file3 = createRandomFile(10L);

		
		
		// Test that the owner can add an attachment

		context.login(person);
		Attachment a1 = sub.addAttachment(file1, AttachmentType.SUPPLEMENTAL).save();
		a1.setName("changed1");
		a1.save();
		
		// Test that a reviewer can add an attachment
		context.login(MockPerson.getReviewer());
		Attachment a2 = sub.addAttachment(file2, AttachmentType.SUPPLEMENTAL).save();
		a2.setName("changed2");
		a2.save();

		// Test that a someone else can not add an attachment.
		context.login(MockPerson.getStudent());
		try {
			sub.addAttachment(file3, AttachmentType.SUPPLEMENTAL).save();
			fail("Someone else was able to add an attachment to a submission.");
		} catch (SecurityException se) {
			/* yay */
		}
		
		context.login(MockPerson.getAdministrator());

		file1.delete();
		file2.delete();
		file3.delete();
	}
	
	/**
	 * Private method to create a new file with the given content.
	 * 
	 * @param data
	 *            The file's content.
	 * @return The new file
	 */
	private File createFile(String data) {
		try {
			File file = File.createTempFile("attachment-test", ".dat");

			FileUtils.writeStringToFile(file, data);

			return file;
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
	}

	/**
	 * Create a new file with random content equal to the size passed in.
	 * 
	 * @param size
	 *            The size of the new file in bytes.
	 * @return The new random file.
	 */
	private File createRandomFile(long size) {
		return createFile(createRandomData(size));
	}

	/**
	 * Create a random data string of given bytes.
	 * 
	 * @param size
	 *            How many bytes in the new string.
	 * @return Random data.
	 */
	private String createRandomData(long size) {

		StringBuffer data = new StringBuffer();
		Random random = new Random();

		for (int i = 0; i < size; i++) {
			data.append(random.nextInt(9));
		}

		return data.toString();
	}
	
}
