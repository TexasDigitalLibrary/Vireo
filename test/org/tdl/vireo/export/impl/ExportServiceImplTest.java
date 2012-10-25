package org.tdl.vireo.export.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.concurrent.ExecutionException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.tdl.vireo.export.ChunkStream;
import org.tdl.vireo.export.MockPackager;
import org.tdl.vireo.model.MockSubmission;
import org.tdl.vireo.model.SubmissionRepository;
import org.tdl.vireo.search.MockSearchFilter;
import org.tdl.vireo.search.MockSearcher;
import org.tdl.vireo.search.Searcher;

import play.libs.F.Promise;
import play.modules.spring.Spring;
import play.test.UnitTest;

/**
 * Test the export service
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class ExportServiceImplTest extends UnitTest {

	// The export service to test.
	public static ExportServiceImpl service = Spring.getBeanOfType(ExportServiceImpl.class);

	/**
	 * Test generating an export, and confirm it looks good.
	 */
	@Test
	public void testExport() throws IOException, InterruptedException, ExecutionException {
		
		Searcher originalSearcher = service.searcher;
		SubmissionRepository originalSubRepo = service.subRepo;

		try {
			MockPackager packager = new MockPackager();
			MockSearchFilter filter = new MockSearchFilter();
			MockSearcher searcher = new MockSearcher();
			for (int i=0; i<50; i++)
				searcher.submissions.add(new MockSubmission());
			
			service.searcher = searcher;
			service.subRepo = searcher.subRepo;
			ChunkStream stream = service.export(packager, filter);
			
			// Write out the export to a single file.
			File exportFile = File.createTempFile("export-file-", ".zip");
			FileOutputStream fos = new FileOutputStream(exportFile);
			while(stream.hasNextChunk()) {
				Promise<byte[]> nextChunk = stream.nextChunk();
				while (!nextChunk.isDone()) {Thread.yield();}
				assertTrue(nextChunk.isDone());
				
				fos.write(nextChunk.get());
			}
			fos.close();
			
			// Uncompress the export file.
			File exportDir = File.createTempFile("export-dir-", ".dir");
			exportDir.delete();
			exportDir.mkdir();
			unzip(exportFile,exportDir);
			
			assertEquals(exportDir.list().length, 1);
			assertEquals(exportDir.listFiles()[0].list().length,50);
			String read = FileUtils.readFileToString(exportDir.listFiles()[0].listFiles()[0]);
			assertEquals("Mock-Package",read);
			
			
			FileUtils.deleteDirectory(exportDir);
			exportFile.delete();
		} finally {
			service.searcher = originalSearcher;
			service.subRepo = originalSubRepo;
		}
	}
	
	
	
	/**
	 * Unzip the zip file into the destination directory.
	 * 
	 * @param zipFile The zip file.
	 * @param destDir The destination directory.
	 */
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
			destFile.getParentFile().mkdirs();

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
	
}
