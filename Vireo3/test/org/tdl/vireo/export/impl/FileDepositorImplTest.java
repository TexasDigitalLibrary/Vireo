package org.tdl.vireo.export.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.tdl.vireo.export.MockExportPackage;
import org.tdl.vireo.model.MockDepositLocation;
import org.tdl.vireo.model.MockSubmission;

import play.Play;
import play.modules.spring.Spring;
import play.test.UnitTest;

/**
 * Test the simple file depositor
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class FileDepositorImplTest extends UnitTest {

	public static FileDepositorImpl depositor = Spring.getBeanOfType(FileDepositorImpl.class);

	
	/**
	 * Test that the bean name for this depositor is accurate.
	 */
	@Test
	public void testGetBeanName() {

		String beanName = depositor.getBeanName();
		assertNotNull(beanName);
		Object bean = Spring.getBean(beanName);
		if (!(bean instanceof FileDepositorImpl))
			fail("Bean returned by name '" + beanName + "' is not a FileDepositorImpl.");
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
	 * Test that directories are returned as collections.
	 */
	@Test
	public void testGetCollection() throws IOException  {

		// Create some sub-directories to be collections.
		File baseDir = depositor.baseDir;
		FileUtils.forceMkdir(new File(baseDir.getPath()+File.separator+"Collection_A"));
		FileUtils.forceMkdir(new File(baseDir.getPath()+File.separator+"Collection_B"));
		FileUtils.forceMkdir(new File(baseDir.getPath()+File.separator+"Collection_C"));

		MockDepositLocation location = getDepositLocation();

		Map<String, String> collections = depositor.getCollections(location);

		assertNotNull(collections);
		assertTrue(collections.size() >= 4); // There could all ready be other collections.
		
		assertNotNull(collections.get("Collection_A"));
		assertNotNull(collections.get("Collection_B"));
		assertNotNull(collections.get("Collection_C"));
		
		new File(baseDir.getPath()+File.separator+"Collection_A").delete();
		new File(baseDir.getPath()+File.separator+"Collection_B").delete();
		new File(baseDir.getPath()+File.separator+"Collection_C").delete();

	}
	
	/**
	 * Test that the depositor returns the correct names for the
	 * hard-coded collections A, B, and C.
	 */
	@Test
	public void testGetCollectionName() throws IOException {
		
		// Create some sub-directories to be collections.
		File baseDir = depositor.baseDir;
		FileUtils.forceMkdir(new File(baseDir.getPath()+File.separator+"Collection_A"));
		FileUtils.forceMkdir(new File(baseDir.getPath()+File.separator+"Collection_B"));
		FileUtils.forceMkdir(new File(baseDir.getPath()+File.separator+"Collection_C"));
		
		MockDepositLocation location = getDepositLocation();

		// resolve collection A
		String name = depositor.getCollectionName(location, baseDir.getCanonicalPath()+File.separator+"Collection_A");
		assertEquals("Collection_A", name);

		// resolve collection B
		name = depositor.getCollectionName(location, baseDir.getCanonicalPath()+File.separator+"Collection_B");
		assertEquals("Collection_B", name);
		
		// resolve collection C
		name = depositor.getCollectionName(location, baseDir.getCanonicalPath()+File.separator+"Collection_C");
		assertEquals("Collection_C", name);

		new File(baseDir.getPath()+File.separator+"Collection_A").delete();
		new File(baseDir.getPath()+File.separator+"Collection_B").delete();
		new File(baseDir.getPath()+File.separator+"Collection_C").delete();
	}

	
	/**
	 * Test that the depositor returns null when queried for the
	 * name of a collection that does not exist.
	 */
	@Test
	public void testGetCollectionNameBad() throws MalformedURLException {
		MockDepositLocation location = getDepositLocation();

		assertNull(depositor.getCollectionName(location, "thisdoesnotexist"));
	}

	
	/**
	 * Test that the depositor reports success for the valid deposit package.
	 */
	@Test
	public void testDeposit() throws IOException {
		MockDepositLocation location = getDepositLocation();
		
		MockExportPackage pkg = new MockExportPackage();
		pkg.file = getResourceFile("org/tdl/vireo/export/impl/Sword1_ValidDeposit.zip");
		pkg.mimeType = "application/zip";
		pkg.format = "http://purl.org/net/sword-types/METSDSpaceSIP";
		pkg.submission = new MockSubmission();

		String depositID = depositor.deposit(location, pkg);
		
		assertNull(depositID);
		
		File depositFile = new File(depositor.baseDir.getPath()+File.separator+"package_"+pkg.submission.getId()+".zip");
		assertTrue(depositFile.exists());
		
		depositFile.delete();
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

	
	/**
	 * @return A basic deposit location.
	 */
	protected static MockDepositLocation getDepositLocation() {
		
		MockDepositLocation location = new MockDepositLocation();
		location.repository = Play.applicationPath + File.separator + "data/deposits";
		location.collection = Play.applicationPath + File.separator + "data/deposits";
		location.username = "";
		location.password = "";
		return location;
	}
}
