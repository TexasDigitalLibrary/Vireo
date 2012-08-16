package org.tdl.vireo.model.jpa;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.io.FileUtils;
import org.tdl.vireo.model.AbstractModel;
import org.tdl.vireo.model.ActionLog;
import org.tdl.vireo.model.Attachment;
import org.tdl.vireo.model.AttachmentType;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.security.SecurityContext;

import play.data.validation.Required;
import play.db.jpa.Blob;
import play.db.jpa.Model;
import play.libs.MimeTypes;
import play.modules.spring.Spring;

/**
 * Jpa specific implementation of Vireo's Attachment interface
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
@Entity
@Table(name = "attachment",
	uniqueConstraints = { @UniqueConstraint( columnNames = { "submission_id", "name" } ) } )
public class JpaAttachmentImpl extends JpaAbstractModel<JpaAttachmentImpl> implements Attachment {

	@ManyToOne(targetEntity=JpaSubmissionImpl.class, optional=false)
	public Submission submission;

	@Column(nullable = false, length=255)
	public String name;

	@Column(nullable = false)
	public AttachmentType type;
	
	@ManyToOne(targetEntity=JpaPersonImpl.class, optional=true)
	public Person person;
	
	@Column(nullable = false)
	public Date date;

	public Blob data;

	/**
	 * Private constructor to share code between the file and bytearray constructors.
	 * 
	 * @param submission The submission this attachment will be associated with.
	 * @param type The type of the submission.
	 */
	private JpaAttachmentImpl(Submission submission, AttachmentType type) {
		if (submission == null)
			throw new IllegalArgumentException("Submission is required");
		
		if (type == null)
			throw new IllegalArgumentException("Attachment type is required");
		
		if (AttachmentType.PRIMARY == type) {
			// Check that there is not allready a primary document.	
			if (submission.getPrimaryDocument() != null)
				throw new IllegalArgumentException("There can only be one primary document associated with a submission. You must remove the current primary document before adding another.");
		}

		assertReviewerOrOwner(submission.getSubmitter());

		// If the person operating is not a persistant person, a mock or
		// otherwise then don't record the link.
		SecurityContext context = Spring.getBeanOfType(SecurityContext.class);		
		Person person = context.getPerson();
		if (person != null && !person.getClass().isAnnotationPresent(Entity.class))
			person = null;
		
		this.submission = submission;
		this.type = type;
		this.person = person;
		this.date = new Date();
		
	}
	
	
	/**
	 * Create a new JpaAttachmentImpl from a file.
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

		this(submission,type);
		
		if (file == null)
			throw new IllegalArgumentException("File is required");
		
		if (!file.exists())
			throw new IllegalArgumentException("File does not exist");
		
		if (!file.canRead())
			throw new IllegalArgumentException("File is not readable");
		
		setName(file.getName());
		this.data = new Blob();
		this.data.set(new FileInputStream(file), MimeTypes.getContentType(name));
	}
	

	/**
	 * Create a new JpaAttachmentIpml from a byte array.
	 * 
	 * @param submission
	 *            The submission this attachment belongs too.
	 * @param type
	 *            The type of the attachment.
	 * @param filename
	 *            The filename of the attachment.
	 * @param content
	 *            The contents of the attachment.
	 */
	protected JpaAttachmentImpl(Submission submission, AttachmentType type,
			String filename, byte[] content) throws IOException {
	
		this(submission,type);
		
		if (filename == null || filename.trim().length() == 0)
			throw new IllegalArgumentException("A filename is required for an attachment.");
		
		if (content == null || content.length == 0)
			throw new IllegalArgumentException("The contents of an attachment may not be blank.");
		
		setName(filename);
		this.data = new Blob();
		this.data.set(
				new ByteArrayInputStream(content),
				MimeTypes.getContentType(filename)
				);
		
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
		
		// Check if this filename has allready exists
		if (submission.getId() != null && find("submission = ?1 AND name = ?2", submission, name).first() != null)
			throw new IllegalArgumentException("An attachment with the name '"+name+"' allready exists for this submission.");
		
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
			// Check that there is not already a primary document.	
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

	@Override
	public Person getPerson() {
		return this.person;
	}

	@Override
	public Date getDate() {
		return this.date;
	}

}
