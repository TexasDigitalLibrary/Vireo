package org.tdl.vireo.export.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.Enumeration;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.junit.Test;
import org.tdl.vireo.export.ExportPackage;
import org.tdl.vireo.model.MockDepositLocation;
import org.tdl.vireo.model.Submission;

import play.Play;
import play.modules.spring.Spring;
import play.test.UnitTest;

/**
 * Test the sword 1 depositor.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class Sword1DepositorImplTest extends UnitTest {

	public static Sword1DepositorImpl depositor = Spring.getBeanOfType(Sword1DepositorImpl.class);

	
	public static final int PORT = 8082;
	public static final String repositoryURL = "http://localhost:"+PORT+"/servicedocument";
	public static final String collectionA = "http://localhost:"+PORT+"/deposit/a";
	public static final String collectionB = "http://localhost:"+PORT+"/deposit/b";
	public static final String collectionC = "http://localhost:"+PORT+"/deposit/c";
	
	/**
	 * Test that the bean name for this depositor is accurate.
	 */
	@Test
	public void testGetBeanName() {

		String beanName = depositor.getBeanName();
		assertNotNull(beanName);
		Object bean = Spring.getBean(beanName);
		if (!(bean instanceof Sword1DepositorImpl))
			fail("Bean returned by name '" + beanName + "' is not a Sword1DepositorImpl.");
	}

	
	/**
	 * Test that the display name returns a readable string name
	 * 
	 */
	@Test
	public void testGetServiceName() {
		assertNotNull(depositor.getDisplayName());
	}

	
	/**
	 * That when when given a basic set of credentials the depositor can get a
	 * list of collections. The mock sever should return the three hard coded
	 * collections A, B, and C.
	 */
	@Test
	public void testGetCollection() throws MalformedURLException {

		MockDepositLocation location = getDepositLocation();

		Map<String, String> collections = depositor.getCollections(location);

		assertNotNull(collections);
		assertEquals(3, collections.size());
		assertNotNull(collections.get("Collection A"));
		assertNotNull(collections.get("Collection B"));
		assertNotNull(collections.get("Collection C"));
		assertEquals("http://localhost:"+PORT+"/deposit/a",collections.get("Collection A"));
	}

	
	/**
	 * Test that when queried with onbehalfof credentials the
	 * depositor return the collections A, B, and C that are
	 * hard-coded in the MockSwordServer.
	 */
	@Test
	public void testGetCollectionOnBehalf() throws MalformedURLException {

		MockDepositLocation location = getDepositLocation();
		location.onBehalfOf = "someoneelse";

		Map<String, String> collections = depositor.getCollections(location);

		assertNotNull(collections);
		assertEquals(3, collections.size());
		assertNotNull(collections.get("Collection A"));
		assertNotNull(collections.get("Collection B"));
		assertNotNull(collections.get("Collection C"));
		assertEquals("http://localhost:"+PORT+"/deposit/a",collections.get("Collection A"));

	}
	

	/**
	 * Test that when queried with (1) a non-SWORD-server URL (2) a bad
	 * username/password pair (3) a bad onbehalf of username the
	 * depositor throws appropriate exceptions.
	 */
	@Test
	public void testGetCollectionBad() throws MalformedURLException {

		// (1) Test with just a bad url
		try {
			MockDepositLocation location = getDepositLocation();
			location.repository = "http://localhost:"+PORT+"/thisdoesnotexist";
			
			depositor.getCollections(location);
			fail("getCollections() did not throw an exception with an invalid repository URL.");
		} catch (RuntimeException re) {
			// yay
		}

		// (2) Test with just a bad username/password
		try {
			MockDepositLocation location = getDepositLocation();
			location.username = "invalid";
			location.password = "bad";

			depositor.getCollections(location);
			fail("getCollections() did not throw an exception with an invalid username/password pair.");
		} catch (RuntimeException re) {
			// yay
		}

		// (3) Test with onbehalfof = error
		try {
			MockDepositLocation location = getDepositLocation();
			location.onBehalfOf = "error";

			depositor.getCollections(location);
			fail("getCollections() did not throw an exception with onBehalfOf as error.");
		} catch (RuntimeException re) {
			// yay
		}
	}

	
	/**
	 * Test that the depositor returns the correct names for the
	 * hard-coded collections A, B, and C.
	 * 
	 * @throws MalformedURLException
	 */
	@Test
	public void testGetCollectionName() throws MalformedURLException {
		MockDepositLocation location = getDepositLocation();

		// resolve collection A
		String name = depositor.getCollectionName(location, collectionA);
		assertEquals("Collection A", name);

		// resolve collection B
		name = depositor.getCollectionName(location, collectionB);
		assertEquals("Collection B", name);
		
		// resolve collection C
		name = depositor.getCollectionName(location, collectionC);
		assertEquals("Collection C", name);

	}

	
	/**
	 * Test that the depositor returns null when queried for the
	 * name of a collection that does not exist.
	 */
	@Test
	public void testGetCollectionNameBad() throws MalformedURLException {
		MockDepositLocation location = getDepositLocation();

		assertNull(depositor.getCollectionName(location, "http://localhost:"+PORT+"/deposit/thisdoesnotexist"));
	}

	
	/**
	 * Test that the depositor reports success for the valid deposit package.
	 */
	@Test
	public void testDeposit() throws IOException {
		MockDepositLocation location = getDepositLocation();
		
		File file = getResourceFile("org/tdl/vireo/export/impl/Sword1_ValidDeposit.zip");
		String mimeType = "application/zip";
		String format = "http://purl.org/net/sword-types/METSDSpaceSIP";
		MockPackage pkg = new MockPackage(mimeType, format, file);

		String depositID = depositor.deposit(location, pkg);
		
		assertNotNull(depositID);
	}
	
	/**
	 * Test that the depositor reports success for the valid deposit package that is uncompressed as a zip.
	 */
	@Test
	public void testUncompressedDeposit() throws IOException {
		MockDepositLocation location = getDepositLocation();
		
		File file = getResourceFile("org/tdl/vireo/export/impl/Sword1_ValidDeposit.zip");
		File dir = File.createTempFile("tmp-", ".dir");
		dir.delete();
		dir.mkdir();
		unzip(file, dir);
		
		String mimeType = null;
		String format = "http://purl.org/net/sword-types/METSDSpaceSIP";
		MockPackage pkg = new MockPackage(mimeType, format, dir);

		String depositID = depositor.deposit(location, pkg);
		
		assertNotNull(depositID);
	}

	
	/**
	 * Test that the deposit reports an error for deposit of an
	 * invalid package
	 */
	@Test
	public void testDepositWithBadPackage() throws IOException {
		MockDepositLocation location = getDepositLocation();
		
		File file = getResourceFile("org/tdl/vireo/export/impl/Sword1_InvalidDeposit.zip");
		String mimeType = "application/zip";
		String format = "http://purl.org/net/sword-types/METSDSpaceSIP";
		MockPackage pkg = new MockPackage(mimeType, format, file);

		try {
			depositor.deposit(location, pkg);
			fail("The depositor should have thrown an error when depositing a bad package.");
		} catch (RuntimeException re) {
			// yay
		} 
	}

	
	/**
	 * Test that the deposit throws the appropriate exception
	 * when a deposit is attempted with (1) an invalid username/password pair
	 * (2) an invalid onbehalfof username
	 * 
	 */
	@Test
	public void testDepositWithBadAuth() throws IOException {

		MockDepositLocation location = getDepositLocation();
		location.username = "invalid";
		location.password = "invalid";

		File file = getResourceFile("org/tdl/vireo/export/impl/Sword1_ValidDeposit.zip");
		String mimeType = "application/zip";
		String format = "http://purl.org/net/sword-types/METSDSpaceSIP";
		MockPackage pkg = new MockPackage(mimeType, format, file);
		
		
		try {
			depositor.deposit(location, pkg);
			fail("The depositor should have thrown an error when depositing a valid package with invalid authentication.");
		} catch (RuntimeException re) {
			// yay
		} 
		
		location = getDepositLocation();
		location.onBehalfOf = "error";

		try {
			depositor.deposit(location, pkg);
			fail("The depositor should have thrown an error when depositing a valid package with invalid onBehalfOf.");
		} catch (RuntimeException re) {
			// yay
		} 
	}

	
	/**
	 * Extract the file from the jar and place it in a temporary location for
	 * the test to operate from.
	 * 
	 * @param filePath
	 *            The path, relative to the classpath, of the file to reference.
	 * @return A Java File object reference.
	 */
	protected static File getResourceFile(String filePath) throws IOException {

		File file = File.createTempFile("sword-deposit", ".zip");

		// While we're packaged by play we have to ask Play for the inputstream
		// instead of the classloader.
		// InputStream is = DSpaceCSVIngestServiceImplTests.class
		// .getResourceAsStream(filePath);
		InputStream is = Play.classloader.getResourceAsStream(filePath);		
		OutputStream os = new FileOutputStream(file);

		// Copy the file out of the jar into a temporary space.
		byte[] buffer = new byte[1024];
		int len;
		while ((len = is.read(buffer)) > 0) {
			os.write(buffer, 0, len);
		}
		is.close();
		os.close();

		return file;
	}

	
	public void unzip(File zipFile, File destDir) throws IOException {
		int BUFFER = 2048;

		ZipFile zip = new ZipFile(zipFile);
		
		Enumeration zipFileEntries = zip.entries();

		// Process each entry
		while (zipFileEntries.hasMoreElements()) {
			// grab a zip file entry
			ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();

			String currentEntry = entry.getName();

			File destFile = new File(destDir, currentEntry);

			// create the parent directory structure if needed
			if (!entry.isDirectory()) {
				BufferedInputStream is = new BufferedInputStream(zip.getInputStream(entry));
				int currentByte;
				// establish buffer for writing file
				byte data[] = new byte[BUFFER];

				// write the current file to disk
				FileOutputStream fos = new FileOutputStream(destFile);
				BufferedOutputStream bos = new BufferedOutputStream(fos,BUFFER);

				// read and write until last byte is encountered
				while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
					bos.write(data, 0, currentByte);
				}
				bos.flush();
				bos.close();
				is.close();
			}
		}
	}

	
	/**
	 * @return A basic deposit location.
	 */
	protected static MockDepositLocation getDepositLocation() throws MalformedURLException {
		
		MockDepositLocation location = new MockDepositLocation();
		location.repository = repositoryURL;
		location.collection = collectionA;
		location.username = "testUser";
		location.password = "testPassword";
		return location;
	}
	
	/**
	 * Mock deposit package.
	 */
	public static class MockPackage implements ExportPackage {

		public Submission submission;
		public String mimeType;
		public String format;
		public File file;
		public String entryName;
		
		public MockPackage(String mimeType, String format, File file) {
			this.mimeType = mimeType;
			this.format = format;
			this.file = file;
		}
		
		@Override
		public Submission getSubmission() {
			// Sword 1 dosen't need the submission
			return null;
		}
		
		@Override
		public String getMimeType() {
			return mimeType;
		}

		@Override
		public String getFormat() {
			return format;
		}

		@Override
		public File getFile() {
			return file;
		}

		@Override
		public void delete() {
		}

		@Override
		public String getEntryName() {
			return entryName;
		}
	}
}
