package org.tdl.vireo.export.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jdom.JDOMException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tdl.vireo.export.ExportPackage;
import org.tdl.vireo.model.AttachmentType;
import org.tdl.vireo.model.DegreeLevel;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.RoleType;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.jpa.JpaPersonRepositoryImpl;
import org.tdl.vireo.model.jpa.JpaSettingsRepositoryImpl;
import org.tdl.vireo.model.jpa.JpaSubmissionRepositoryImpl;
import org.tdl.vireo.proquest.ProquestVocabularyRepository;
import org.tdl.vireo.security.SecurityContext;

import play.Logger;
import play.modules.spring.Spring;
import play.test.UnitTest;

/**
 * Test the multiple template packager.
 * 
 * Since there is expected to be lots of various formats, this test just runs
 * through the basics. It checks that the files were created and not much more.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 * 
 */
public class MultipleTemplatePackagerImplTest extends UnitTest {

	// All the repositories
	public static SecurityContext context = Spring.getBeanOfType(SecurityContext.class);
	public static JpaPersonRepositoryImpl personRepo = Spring.getBeanOfType(JpaPersonRepositoryImpl.class);
	public static JpaSubmissionRepositoryImpl subRepo = Spring.getBeanOfType(JpaSubmissionRepositoryImpl.class);
	public static JpaSettingsRepositoryImpl settingRepo = Spring.getBeanOfType(JpaSettingsRepositoryImpl.class);
	public static ProquestVocabularyRepository proquestRepo = Spring.getBeanOfType(ProquestVocabularyRepository.class);

	
	
	public Person person;
	public Submission sub;
	
	/**
	 * Set up a submission so we can test packaging it up.
	 */
	@Before
	public void setup() throws IOException {
		context.turnOffAuthorization();
		person = personRepo.createPerson("netid", "email@email.com", "first", "last", RoleType.NONE).save();
		sub = subRepo.createSubmission(person);
		
		// Okay, we should start generating action log messages now.
		sub.setStudentFirstName("first name");
		sub.setStudentLastName("last name");
		sub.setStudentMiddleName("middle name");
		sub.setStudentBirthYear(2002);
		sub.setDocumentTitle("document title");
		sub.setDocumentAbstract("document abstract");
		sub.setDocumentKeywords("document keywords");
		sub.addDocumentSubject(proquestRepo.findAllSubjects().get(0).getDescription());
		sub.addDocumentSubject(proquestRepo.findAllSubjects().get(1).getDescription());
		sub.addDocumentSubject(proquestRepo.findAllSubjects().get(2).getDescription());
		sub.setDegree("selected degree");
		sub.setDegreeLevel(DegreeLevel.UNDERGRADUATE);
		sub.setDepartment("selected department");
		sub.setCollege("selected college");
		sub.setMajor("selected major");
		sub.setDocumentType("selected document type");
		sub.setGraduationMonth(0);
		sub.setGraduationYear(2002);
		sub.setDepositId("depositId");
		
		// Create some attachments
		File tmpDir = createNewTempDir();
		File bottle_pdf = createAndWriteFile(tmpDir, "bottle.pdf", "bottle.pdf: This is not really a pdf file.");
		File fluff_jpg = createAndWriteFile(tmpDir, "fluff.jpg", "fluff.jpg: This is not really a jpg file.");
		
		sub.addAttachment(bottle_pdf, AttachmentType.PRIMARY);
		sub.addAttachment(fluff_jpg, AttachmentType.SUPPLEMENTAL);
		
		sub.save();
		
		bottle_pdf.delete();
		fluff_jpg.delete();
		tmpDir.delete();
		
	}
	
	/**
	 * Clean up our submission.
	 */
	@After
	public void cleanup() {
		try {
		sub.delete();
		person.delete();
		context.restoreAuthorization();
		} catch (RuntimeException re) {
			re.printStackTrace();
		}
	}
	
	/**
	 * Test each packager handling of the submission.
	 */
	@Test
	public void testPackager() throws IOException, JDOMException {

		// Test all the template packagers
		Map<String,MultipleTemplatePackagerImpl> packagers = Spring.getBeansOfType(MultipleTemplatePackagerImpl.class);
		
		for (MultipleTemplatePackagerImpl packager : packagers.values()) {
			
			ExportPackage pkg = packager.generatePackage(sub);
			
			assertNotNull(pkg);
			assertEquals(packager.format,pkg.getFormat());
			
			
			
			
			File exportFile = pkg.getFile();
			assertNotNull(exportFile);
			assertTrue("Package file does not exist", exportFile.exists());
			assertTrue("Package file is not readable", exportFile.canRead());
			
			Map<String, File> fileMap = getFileMap(exportFile);
			
			// There should be three files
			assertTrue(fileMap.containsKey("LASTNAME-SELECTEDDOCUMENTTYPE-2002.pdf"));
			assertTrue(fileMap.containsKey("fluff.jpg"));
			
			// Check that each of the templates exist
			for (String name : packager.templates.keySet()) {
				assertTrue(fileMap.containsKey(name));
			}
			
			// Cleanup
			pkg.delete();
			assertFalse(exportFile.exists());
		}
	}
	
	
	/**
	 * Create a temporary working directory
	 * 
	 * @return the File object pointing to the created directory
	 */
	public File createNewTempDir() throws IOException {
		File tempDir = File.createTempFile("packager-tester", ".dat");
		tempDir.delete();
		tempDir.mkdir();

		assertTrue(tempDir.exists());
		assertTrue(tempDir.isDirectory());

		return tempDir;
	}

	/**
	 * Creates a Hashmap of file names to file pointers in a given directory
	 * 
	 * @param targetDir
	 *            the source directory be parsed
	 * @return a map of file names to file pointers
	 */
	public Map<String, File> getFileMap(File targetDir) 
	{
		File[] contents = targetDir.listFiles();
		Map<String, File> fileMap = new HashMap<String, File>();
		for (File file : contents) {
			fileMap.put(file.getName(), file);
		}
		return fileMap;
	}
	
	/**
	 * Create a new file within the parent directory and fill it with some data.
	 * 
	 * @param directory
	 *            The parent directory
	 * @param fileName
	 *            The name of the file to create.
	 * @param data
	 *            The data to put into the file, or null for no data.
	 * @return The file pointer of the newly created file.
	 */
	public static File createAndWriteFile(File directory, String fileName, String data) throws IOException {
		
		File file = new File(directory.getCanonicalPath()+File.separator+fileName);
		file.createNewFile();
		
		// Write some some data so the file is not empty.
		if (data != null) {
			FileWriter fw = new FileWriter(file);
			fw.write(data);
			fw.close();
		}
		
		return file;
		
	}

	/**
	 * Read a file and return it's contents as a string.
	 * 
	 * @param file
	 *            The file to be read.
	 * @return The contents of the file.
	 */
	public static String readFile(File file) throws IOException {
		StringBuffer fileData = new StringBuffer(1000);
		BufferedReader reader = new BufferedReader(new FileReader(file));
		char[] buf = new char[1024];
		int numRead = 0;
		
		while ((numRead = reader.read(buf)) != -1) {
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}
		reader.close();
		return fileData.toString();
	}
	
}
