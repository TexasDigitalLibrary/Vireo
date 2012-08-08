package org.tdl.vireo.deposit;

import java.io.File;
import java.io.IOException;

import org.tdl.vireo.model.Submission;

/**
 * Mock implementation of the deposit package interface.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class MockDepositPackage implements DepositPackage {

	public Submission submission;
	public String depositId;
	public String mimeType = "application/zip";
	public String format = "http://something/";
	public File file;

	public MockDepositPackage() {
		try {
			file = File.createTempFile("mock-deposit-package-", ".zip");
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
	}

	@Override
	public Submission getSubmission() {
		return submission;
	}
	
	@Override
	public String getDepositId() {
		return depositId;
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

}
