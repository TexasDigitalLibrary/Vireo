package org.tdl.vireo.proquest.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.Resource;
import org.tdl.vireo.proquest.ProquestLanguage;
import org.tdl.vireo.proquest.ProquestSubject;
import org.tdl.vireo.proquest.ProquestVocabularyRepository;

import play.modules.spring.Spring;
import play.test.UnitTest;

/**
 * Test the Proquest Excel Controlled Vocabulary Repository. This class tests
 * both excel formats the binary 97 format (aka hssf) and the newer XML based
 * format (aka xssf).
 * 
 * There is a memory bug in Apache-POI's handling of excel's newer formats. It
 * causes big memory problems so I've comment those tests out. I am hoping that
 * upgrading to a newer version of POI in the future will resolve the problem.
 * At the time these tests were written we were using 3.8
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class ProquestExcelVocabularyRepositoryImplTests extends UnitTest {

	// Test file in old Excel 97 binary format
	public static final File hssfTestFile = new File("test/org/tdl/vireo/proquest/impl/test_vocabulary.xls");
	
//	// Test file in newer Excel XML format
//	public static final File xssfTestFile = new File("test/org/tdl/vireo/proquest/impl/test_vocabulary.xlsx");

	// The repository to test
	public static final ProquestExcelVocabularyRepositoryImpl proquestRepo = Spring.getBeanOfType(ProquestExcelVocabularyRepositoryImpl.class);
	
	// Store the current state
	public List<ProquestSubject> originalSubjects = new ArrayList<ProquestSubject>();
	public List<ProquestLanguage> originalLanguages = new ArrayList<ProquestLanguage>();
	
	
	/**
	 * Save the current subject and language sets because our tests will blow them away.
	 */
	@Before
	public void setup() {
		originalSubjects = new ArrayList<ProquestSubject>(proquestRepo.subjects);
		originalLanguages = new ArrayList<ProquestLanguage>(proquestRepo.languages);
	}
	
	/**
	 * Restore the original subject and language sets.
	 */
	@After
	public void cleanup() {
		proquestRepo.subjects = originalSubjects;
		proquestRepo.languages = originalLanguages;
	}
	
	/**
	 * Test reading subjects from the older office 97 binary format.
	 * @throws InterruptedException 
	 */
	@Test
	public void testExcel97Subjects() throws IOException, InterruptedException {
		
		proquestRepo.setSubjects(new MockFileResource(hssfTestFile));
		
		assertEquals(3,proquestRepo.findAllSubjects().size());
		assertEquals("description 2",proquestRepo.findSubjectByCode("code 2").getDescription());
		assertEquals("code 2",proquestRepo.findSubjectByDescription("description 2").getCode());
		assertEquals("code 1",proquestRepo.findAllSubjects().get(0).getCode());
		assertEquals("code 3",proquestRepo.findAllSubjects().get(2).getCode());
	}
	
	/**
	 * Test reading languages from the older office 97 binary format.
	 * @throws InterruptedException 
	 */
	@Test
	public void testExcel97Languages() throws IOException, InterruptedException {
		
		proquestRepo.setLanguages(new MockFileResource(hssfTestFile));
		
		assertEquals(3,proquestRepo.findAllLanguages().size());
		assertEquals("description 2",proquestRepo.findLanguageByCode("code 2").getDescription());
		assertEquals("code 2",proquestRepo.findLanguageByDescription("description 2").getCode());
		assertEquals("code 1",proquestRepo.findAllLanguages().get(0).getCode());
		assertEquals("code 3",proquestRepo.findAllLanguages().get(2).getCode());
	}
	
	/**
	 * Test reading degrees from the older office 97 binary format.
	 * @throws InterruptedException 
	 */
	@Test
	public void testExcel97Degrees() throws IOException, InterruptedException {
		
		proquestRepo.setDegrees(new MockFileResource(hssfTestFile));
		
		assertEquals(3,proquestRepo.findAllDegrees().size());
		assertEquals("description 2",proquestRepo.findDegreeByCode("code 2").getDescription());
		assertEquals("code 2",proquestRepo.findDegreeByDescription("description 2").getCode());
		assertEquals("code 1",proquestRepo.findAllDegrees().get(0).getCode());
		assertEquals("code 3",proquestRepo.findAllDegrees().get(2).getCode());
	}
	
//	/**
//	 * Test reading subjects from the new XML excel format.
//	 * @throws InterruptedException 
//	 */
//	@Test
//	public void testExcelXMLSubjects() throws IOException, InterruptedException {
//		
//		proquestRepo.setSubjects(new MockFileResource(xssfTestFile));
//		
//		assertEquals(3,proquestRepo.findAllSubjects().size());
//		assertEquals("description 2",proquestRepo.findSubjectByCode("code 2").getDescription());
//		assertEquals("code 2",proquestRepo.findSubjectByDescription("description 2").getCode());
//		assertEquals("code 1",proquestRepo.findAllSubjects().get(0).getCode());
//		assertEquals("code 3",proquestRepo.findAllSubjects().get(2).getCode());
//	}
//	
//	
//	/**
//	 * Test reading languages from the new XML excel format.
//	 * @throws InterruptedException 
//	 */
//	@Test
//	public void testExcelXMLLanguages() throws IOException, InterruptedException {
//		
//		proquestRepo.setLanguages(new MockFileResource(xssfTestFile));
//		
//		assertEquals(3,proquestRepo.findAllLanguages().size());
//		assertEquals("description 2",proquestRepo.findLanguageByCode("code 2").getDescription());
//		assertEquals("code 2",proquestRepo.findLanguageByDescription("description 2").getCode());
//		assertEquals("code 1",proquestRepo.findAllLanguages().get(0).getCode());
//		assertEquals("code 3",proquestRepo.findAllLanguages().get(2).getCode());
//	}
//	
	
	
	
	
	/**
	 * Mock spring resource class. This is how files can be injected into a
	 * spring bean using the nice syntax that spring provides.
	 */
	public static class MockFileResource implements Resource{

		public final File file;
		
		public MockFileResource(File file) {
			this.file = file;
		}
		
		@Override
		public InputStream getInputStream() throws IOException {
			return null;
		}

		@Override
		public boolean exists() {
			return false;
		}

		@Override
		public boolean isReadable() {
			return false;
		}

		@Override
		public boolean isOpen() {
			return false;
		}

		@Override
		public URL getURL() throws IOException {
			return null;
		}

		@Override
		public URI getURI() throws IOException {
			return null;
		}

		@Override
		public File getFile() throws IOException {
			return file;
		}

		@Override
		public long contentLength() throws IOException {
			return 0;
		}

		@Override
		public long lastModified() throws IOException {
			return 0;
		}

		@Override
		public Resource createRelative(String relativePath) throws IOException {
			return null;
		}

		@Override
		public String getFilename() {
			return null;
		}

		@Override
		public String getDescription() {
			return null;
		}
		
	}
	
}
