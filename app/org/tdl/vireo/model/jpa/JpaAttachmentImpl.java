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
import org.tdl.vireo.model.ActionLog;
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
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
@Entity
@Table(name = "attachment")
public class JpaAttachmentImpl extends JpaAbstractModel<JpaAttachmentImpl> implements Attachment {

	@ManyToOne(targetEntity=JpaSubmissionImpl.class, optional=false)
	public Submission submission;

	@Column(nullable = false)
	public String name;

	@Column(nullable = false)
	public AttachmentType type;

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
		
		assertReviewerOrOwner(submission.getSubmitter());
		
		this.submission = submission;
		this.name = file.getName();
		this.type = type;
		this.data = new Blob();
		this.data.set(new FileInputStream(file), MimeTypes.getContentType(name));
	}

	@Override
	public JpaAttachmentImpl save() {

		assertReviewerOrOwner(submission.getSubmitter());

		boolean newObject = false;
		if (id == null)
			newObject = true;

		super.save();

		if (newObject) {
			
			// We're a new object so log the addition.
			String entry = String.format(
					"%s file '%s' (%s) uploaded", 
					this.getType().name(), 
					this.getName(), 
					this.getDisplaySize()
			);
			((JpaSubmissionImpl) submission).logAction(entry,this).save();

		} else {
			
			// We've been updated so log the change.
			String entry = String.format(
					"%s file '%s' modified",
					this.getType().name(),
					this.getName()
			);
			((JpaSubmissionImpl) submission).logAction(entry,this).save();
		}

		return this;
	}
	
	@Override
	public JpaAttachmentImpl delete() {

		assertReviewerOrOwner(submission.getSubmitter());
		
		((JpaSubmissionImpl) submission).removeAttachment(this);
		
		
		String displaySize = this.getDisplaySize();
		if (this.data.exists())
			this.data.getFile().delete();
		

		// Scrub the actionlog of references to this file. The entries will
		// still exist, but it won't have a foriegn key reference.
		em().createQuery(
				"UPDATE JpaActionLogImpl " +
				"SET Attachment_Id = null " +
				"WHERE Attachment_Id = ? " 
				).setParameter(1, this.getId())
				.executeUpdate();
		
		super.delete();
		
		String entry = String.format(
				"%s file '%s' (%s) removed",
				this.getType().name(),
				this.getName(),
				displaySize);
		((JpaSubmissionImpl) submission).logAction(entry).save();
		
		return this;
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

		assertReviewerOrOwner(submission.getSubmitter());
		
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
		
		assertReviewerOrOwner(submission.getSubmitter());

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
