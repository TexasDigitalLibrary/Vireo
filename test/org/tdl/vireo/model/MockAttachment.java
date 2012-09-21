package org.tdl.vireo.model;

import java.io.File;
import java.util.Date;

/**
 * This is a simple mock attachment class that may be useful for testing. Feel
 * free to extend this to add in extra parameters that you feel appropriate.
 * 
 * The basic concept is all properties are public so you can create the mock
 * object and then set whatever relevant properties are needed for your
 * particular test.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class MockAttachment extends AbstractMock implements Attachment {

	/* Attachment Properties */
	public Submission submission;
	public String name;
	public AttachmentType type;
	public Person person;
	public Date date;
	public String mimeType;
	public File file;
	public long fileSize;

	@Override
	public MockAttachment save() {
		return this;
	}

	@Override
	public MockAttachment delete() {
		return this;
	}

	@Override
	public MockAttachment refresh() {
		return this;
	}

	@Override
	public MockAttachment merge() {
		return this;
	}
	
	@Override
	public MockAttachment detach() {
		return this;
	}
	
	@Override
	public void archive() {
		type = AttachmentType.ARCHIVED;
	}
	
	@Override
	public Submission getSubmission() {
		return submission;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public AttachmentType getType() {
		return type;
	}

	@Override
	public void setType(AttachmentType type) {
		this.type = type;
	}

	@Override
	public String getMimeType() {
		return mimeType;
	}

	@Override
	public long getSize() {
		return fileSize;
	}

	@Override
	public String getDisplaySize() {
		return "Unknown";
	}

	@Override
	public File getFile() {
		return file;
	}

	@Override
	public Person getPerson() {
		return person;
	}

	@Override
	public Date getDate() {
		return date;
	}

}
