package org.tdl.vireo.model.jpa;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.commons.io.FileUtils;
import org.tdl.vireo.model.AbstractModel;
import org.tdl.vireo.model.Attachment;
import org.tdl.vireo.model.AttachmentType;
import org.tdl.vireo.model.Submission;

import play.data.validation.Required;
import play.db.jpa.Blob;
import play.db.jpa.Model;
import play.libs.MimeTypes;

/**
 * Jpa specific implementation of Vireo's Attachment interface
 * 
 * TODO: Create actionLog items when the submission is changed.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
@Entity
@Table(name = "Attachment")
public class JpaAttachmentImpl extends Model implements Attachment {

	@ManyToOne(targetEntity=JpaSubmissionImpl.class, optional=false)
	public Submission submission;

	@Column(nullable = false)
	public String name;

	@Column(nullable = false)
	public AttachmentType type;

	@Column(nullable = false)
	public Blob data;

	/**
	 * Create a new JpaAttachmentImpl
	 * 
	 * @param submission
	 *            The submission this attachment belongs too.
	 * @param type
	 *            The type of the attachment.
	 * @param file
	 *            The file.
	 */
	protected JpaAttachmentImpl(Submission submission, AttachmentType type,
			File file) throws IOException {

		if (submission == null)
			throw new IllegalArgumentException("Submission is required");
		
		if (type == null)
			throw new IllegalArgumentException("Attachment type is required");
		
		if (AttachmentType.PRIMARY == type) {
			// Check that there is not allready a primary document.	
			if (submission.getPrimaryDocument() != null)
				throw new IllegalArgumentException("There can only be one primary document associated with a submission. You must remove the current primary document before adding another.");
		}

		if (file == null)
			throw new IllegalArgumentException("File is required");
		
		if (!file.exists())
			throw new IllegalArgumentException("File does not exist");
		
		if (!file.canRead())
			throw new IllegalArgumentException("File is not readable");
		
		this.submission = submission;
		this.name = file.getName();
		this.type = type;
		this.data = new Blob();
		this.data.set(new FileInputStream(file), MimeTypes.getContentType(name));
	}

	@Override
	public JpaAttachmentImpl save() {
		return super.save();
	}

	@Override
	public JpaAttachmentImpl delete() {

		((JpaSubmissionImpl) submission).removeAttachment(this);
		
		if (this.data.exists())
			this.data.getFile().delete();

		return super.delete();
	}

	@Override
	public JpaAttachmentImpl refresh() {
		return super.refresh();
	}

	@Override
	public JpaAttachmentImpl merge() {
		return super.merge();
	}

	@Override
	public Submission getSubmission() {
		return this.submission;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setName(String name) {
		
		if (name == null || name.length() == 0)
			throw new IllegalArgumentException("Attachment name may not be blank or null.");

		this.name = name;
	}

	@Override
	public AttachmentType getType() {
		return this.type;
	}

	@Override
	public void setType(AttachmentType type) {

		if (type == null)
			throw new IllegalArgumentException("Attachment type is required");
		
		if (AttachmentType.PRIMARY == type) {
			// Check that there is not allready a primary document.	
			if (submission.getPrimaryDocument() != null)
				throw new IllegalArgumentException("There can only be one primary document associated with a submission. You must remove the current primary document before adding another.");
		}

		this.type = type;
	}

	@Override
	public String getMimeType() {
		return this.data.type();
	}

	@Override
	public long getSize() {
		return this.data.getFile().length();
	}

	@Override
	public String getDisplaySize() {

		return FileUtils.byteCountToDisplaySize(this.data.getFile().length());
	}

	@Override
	public File getFile() {
		return this.data.getFile();
	}

}
