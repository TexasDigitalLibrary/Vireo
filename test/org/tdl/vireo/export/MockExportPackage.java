package org.tdl.vireo.export;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.tdl.vireo.model.Submission;

/**
 * Mock implementation of the export package interface.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class MockExportPackage implements ExportPackage {

	public Submission submission;
	public String mimeType = "application/zip";
	public String format = "http://something/";
	public File file;
	public String entryName;

	public MockExportPackage() {
		try {
			file = File.createTempFile("mock-package-", ".xml");
			FileUtils.writeStringToFile(file, "Mock-Package");
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
	}

	@Override
	public Submission getSubmission() {
		return submission;
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
		if (file != null)
			file.delete();
	}

	@Override
	public String getEntryName() {
		return entryName;
	}

}
