package org.tdl.vireo.model.jpa;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tdl.vireo.model.Attachment;
import org.tdl.vireo.model.AttachmentType;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.RoleType;
import org.tdl.vireo.model.Submission;

import play.db.jpa.JPA;
import play.modules.spring.Spring;
import play.test.UnitTest;

public class JpaAttachmentImplTest extends UnitTest {

	// Persistence repositories
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
		person = personRepo.createPerson("netid", "email@email.com", "first", "last", RoleType.NONE).save();
		sub = subRepo.createSubmission(person).save();
	}
	
	/**
	 * Cleanup the person & submission after each test.
	 */
	@After
	public void cleanup() {
		try {
		if (sub != null)
			subRepo.findSubmission(sub.getId()).delete();
		
		if (person != null)
			personRepo.findPerson(person.getId()).delete();
		} catch (RuntimeException re) {
			
		}
	}
	
	@Test
	public void testCreateAttachment() throws IOException {
		
		File file = createRandomFile(10L);
		
		Attachment attachment = sub.addAttachment(file, AttachmentType.PRIMARY);
		
		assertNotNull(attachment);
		assertEquals(file.getName(),attachment.getName());
		assertEquals(AttachmentType.PRIMARY,attachment.getType());
	
		file.delete();
	}
	
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
	
	@Test
	public void testCreateDuplicatePrimary() throws IOException {
		
		File file = createRandomFile(10L);
		
		Attachment attachment = sub.addAttachment(file, AttachmentType.PRIMARY).save();
		
		assertNotNull(attachment);
		assertEquals(file.getName(),attachment.getName());
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
	
	@Test
	public void testId() throws IOException {
		
		File file = createRandomFile(10L);
		
		Attachment attachment = sub.addAttachment(file, AttachmentType.PRIMARY).save();
		
		assertNotNull(attachment);
		assertNotNull(attachment.getId());
		
	}
	
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
		
	}
	
	@Test 
	public void testFindByPrimary() throws IOException {
		
		File file1 = createRandomFile(10L);
		File file2 = createRandomFile(10L);
		File file3 = createRandomFile(10L);

		
		Attachment a1 = sub.addAttachment(file1, AttachmentType.PRIMARY).save();
		Attachment a2 = sub.addAttachment(file1, AttachmentType.SUPPLEMENTAL).save();
		Attachment a3 = sub.addAttachment(file1, AttachmentType.SUPPLEMENTAL).save();

		
		Attachment primary = sub.getPrimaryDocument();
		assertEquals(a1.getId(),primary.getId());
		
		file1.delete();
		file2.delete();
		file3.delete();
	}
	
	@Test
	public void testFindSupplemental() throws IOException {
		
		File file1 = createRandomFile(10L);
		File file2 = createRandomFile(10L);
		File file3 = createRandomFile(10L);

		
		Attachment a1 = sub.addAttachment(file1, AttachmentType.PRIMARY).save();
		Attachment a2 = sub.addAttachment(file2, AttachmentType.SUPPLEMENTAL).save();
		Attachment a3 = sub.addAttachment(file3, AttachmentType.SUPPLEMENTAL).save();
		
		Set<Attachment> supplemental = sub.getSupplementalDocuments();
		
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
	
	@Test
	public void testGetAttachments() throws IOException {
		
		File file1 = createRandomFile(10L);
		File file2 = createRandomFile(10L);
		File file3 = createRandomFile(10L);

		
		Attachment a1 = sub.addAttachment(file1, AttachmentType.PRIMARY).save();
		Attachment a2 = sub.addAttachment(file1, AttachmentType.SUPPLEMENTAL).save();
		Attachment a3 = sub.addAttachment(file1, AttachmentType.SUPPLEMENTAL).save();
		
		Set<Attachment> attachments = sub.getAttachments();
		
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
	}
	
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
	
	
	@Test
	public void testFile() throws IOException {
		
		File file1 = createRandomFile(10L);

		Attachment a1 = sub.addAttachment(file1, AttachmentType.PRIMARY).save();

		assertTrue(a1.getFile().exists());
		assertFalse(file1.getAbsolutePath().equals(a1.getFile().getAbsolutePath()));
		
		String originalContent = FileUtils.readFileToString(file1);
		String retrievedContent = FileUtils.readFileToString(a1.getFile());
		assertEquals(originalContent,retrievedContent);
		
		file1.delete();
	}
	
	@Test
	public void testPersistance() throws IOException {
		// Commit and reopen a new transaction because some of the other tests
		// may have caused exceptions which set the transaction to be rolled
		// back.
		if (JPA.em().getTransaction().getRollbackOnly())
			JPA.em().getTransaction().rollback();
		else
			JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		
		File file1 = createRandomFile(10L);
		Attachment attachment = sub.addAttachment(file1, AttachmentType.PRIMARY).save();
		
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
	
	@Test
	public void testDelete() throws IOException {
		File file1 = createRandomFile(10L);
		String originalContent = FileUtils.readFileToString(file1);

		
		Attachment a1 = sub.addAttachment(file1, AttachmentType.PRIMARY).save();
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
		Set<Attachment> attachments = sub.getAttachments();
		assertEquals(0,attachments.size());
		
		
		
		
		
	}
	
	
	
	
	private File createFile(String data) {
		try {
			File file = File.createTempFile("attachment-test", ".dat");

			FileUtils.writeStringToFile(file, data);

			return file;
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
	}

	private File createRandomFile(long size) {
		return createFile(createRandomData(size));
	}

	private String createRandomData(long size) {

		StringBuffer data = new StringBuffer();
		Random random = new Random();

		for (int i = 0; i < size; i++) {
			data.append(random.nextInt(9));
		}

		return data.toString();
	}
	
}
