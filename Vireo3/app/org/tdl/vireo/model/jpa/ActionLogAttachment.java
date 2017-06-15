/**
 * 
 */
package org.tdl.vireo.model.jpa;

import java.io.File;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.tdl.vireo.model.AbstractModel;
import org.tdl.vireo.model.Attachment;
import org.tdl.vireo.model.AttachmentType;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.Submission;

import play.libs.MimeTypes;

/**
 * This attachment type implements a JPA object but is not intended to be saved into DB as an @Entity
 * 
 * Also not intended to be indexed or stored in the Vireo data/attachments directory
 * 
 * @author <a href=mailto:gad.krumholz@austin.utexas.edu>Gad Krumholz</a>
 */
public class ActionLogAttachment extends JpaAbstractModel<ActionLogAttachment> implements Attachment {
	private File file;
	private Submission submission;
	private String name, mimeType;
	private Date date;

	public ActionLogAttachment(JpaSubmissionImpl submission, File file) {
		if (file == null)
			throw new IllegalArgumentException("File is required");

		if (!file.exists())
			throw new IllegalArgumentException("File does not exist");

		if (!file.canRead())
			throw new IllegalArgumentException("File is not readable");

		setName(file.getName());
		try {
			mimeType = MimeTypes.getContentType(name);
		} catch (RuntimeException re) {
			mimeType = "application/octet-stream";
		}
		this.submission = submission;
		this.file = file;
		this.date = new Date();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tdl.vireo.model.AbstractModel#getId()
	 */
	@Override
	public Long getId() {
		return id;
	}

	@Override
	public ActionLogAttachment save() {
		return this;
	}

	@Override
	public ActionLogAttachment delete() {
		return this;
	}

	@Override
	public ActionLogAttachment detach() {
		return this;
	}

	@Override
	public ActionLogAttachment merge() {
		return this;
	}

	@Override
	public ActionLogAttachment refresh() {
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tdl.vireo.model.Attachment#archive()
	 */
	@Override
	public void archive() {
		// DO NOTHING
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tdl.vireo.model.Attachment#getSubmission()
	 */
	@Override
	public Submission getSubmission() {
		return submission;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tdl.vireo.model.Attachment#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tdl.vireo.model.Attachment#setName(java.lang.String)
	 */
	@Override
	public void setName(String name) {
		this.name = name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tdl.vireo.model.Attachment#getType()
	 */
	@Override
	public AttachmentType getType() {
		return AttachmentType.ACTIONLOG;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tdl.vireo.model.Attachment#setType(org.tdl.vireo.model.AttachmentType)
	 */
	@Override
	public void setType(AttachmentType type) {
		// DO NOTHING
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tdl.vireo.model.Attachment#getMimeType()
	 */
	@Override
	public String getMimeType() {
		return mimeType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tdl.vireo.model.Attachment#getSize()
	 */
	@Override
	public long getSize() {
		return file.length();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tdl.vireo.model.Attachment#getDisplaySize()
	 */
	@Override
	public String getDisplaySize() {
		return FileUtils.byteCountToDisplaySize(getSize());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tdl.vireo.model.Attachment#getFile()
	 */
	@Override
	public File getFile() {
		return file;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tdl.vireo.model.Attachment#getPerson()
	 */
	@Override
	public Person getPerson() {
		return submission.getSubmitter();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tdl.vireo.model.Attachment#getDate()
	 */
	@Override
	public Date getDate() {
		return date;
	}

}
