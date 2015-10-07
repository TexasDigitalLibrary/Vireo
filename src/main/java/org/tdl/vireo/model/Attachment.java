package org.tdl.vireo.model.jpa;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.tdl.vireo.model.Attachment;
import org.tdl.vireo.model.AttachmentType;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.security.SecurityContext;

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

	public HashedBlob data;

	public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
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
			// Check that there is not already a primary document.	
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
		
		String mimeType;
		try {
			mimeType = MimeTypes.getContentType(name);
		} catch (RuntimeException re) {
			mimeType = "application/octet-stream";
		}
		
		this.data = new HashedBlob();
		this.data.set(new FileInputStream(file), mimeType);
		
		if (AttachmentType.PRIMARY == type)
			renamePrimaryDocument();
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
		
		String mimeType;
		try {
			mimeType = MimeTypes.getContentType(filename);
		} catch (RuntimeException re) {
			mimeType = "application/octet-stream";
		}
		
		this.data = new HashedBlob();
		this.data.set(
				new ByteArrayInputStream(content),
				mimeType
				);
		
		if (AttachmentType.PRIMARY == type)
			renamePrimaryDocument();
	}

	@Override
	public JpaAttachmentImpl save() {

		assertReviewerOrOwner(submission.getSubmitter());

		if (AttachmentType.PRIMARY == type)
			renamePrimaryDocument();
		
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
			((JpaSubmissionImpl) submission).logAction(entry,this);

		} else {
			
			// We've been updated so log the change.
			String entry = String.format(
					"%s file '%s' modified",
					this.getType().name(),
					this.getName()
			);
			((JpaSubmissionImpl) submission).logAction(entry,this);
		}

		submission.save();
		
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
		((JpaSubmissionImpl) submission).logAction(entry);
		
		submission.save();
		
		return this;
	}
	
	@Override
	public void archive() {
		
		if (type == AttachmentType.ARCHIVED)
			throw new IllegalStateException("Unable to archive an already archived attachment.");
		
		type = AttachmentType.ARCHIVED;
		
		// Rename the file: [filename]-2012-08-14[#].extension
		
		String basename = FilenameUtils.getBaseName(name);
		String date = dateFormat.format(new Date());
		String extension = FilenameUtils.getExtension(name);
		
		String newName = rename(basename+"-archived-on-"+date,extension);
		
		this.setName(newName);
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
		
		// Check if this filename has already exists
		if (submission.getId() != null && submission.findAttachmentByName(name) != null) {
			// If so, we rename this attachment
			String basename = FilenameUtils.getBaseName(name);
			String extension = FilenameUtils.getExtension(name);
			name = rename(basename,extension);
		}
		
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
	
	/**
	 * Search for an available attachment name based upon the basename and
	 * extension provided. If the name is already taken then a _1, _2, _3 will
	 * be appended until a unique name can be reached. Once a unique name has
	 * been found it is returned.
	 * 
	 * @param basename
	 *            The base filename.
	 * @param extension
	 *            The file's extension (may be null or blank)
	 * @return An unused filename in the form [basename][_1][.extension]
	 */
	protected String rename(String basename, String extension) {
		
		// Check if the new filename exsits, if so add a copy number. Repeate until found.
		int i = 0;
		while (true) {
			
			// Figure out the new filename
			String filename = basename+"_"+i;
			if (i == 0 )
				filename = basename;
			
			if (extension != null && extension.length() > 0)
				filename += "."+extension;
			
			// Check if it exists
			if (submission.findAttachmentByName(filename) == null)
				// The file name is unique!
				return filename;
			
			// keep searching
			i++;
		}
	}
	
	/**
	 * Rename the primary document to follow the convention: [LAST]-[DOCUMENT
	 * TYPE]-[YEAR].pdf. If those parameters are not available on the submission
	 * then fall backs are used all the way back to "PRIMARY-DOCUMENT.pdf" is
	 * nothing is available.
	 * 
	 * If an attachment already exists with that name then then the existing
	 * attachment is renamed. The primary document's name will always take
	 * precedence.
	 * 
	 * @return The attachment that was renamed, if any.
	 */
	protected Attachment renamePrimaryDocument() {
		
		// We only enforce the nameing convention on primary documents.
		if (this.type != AttachmentType.PRIMARY)
			throw new IllegalStateException("Unable to rename the primary document on an attachment which is not of type = PRIMARY.");
		
		// Step 1) Figure out what the primary document should be named.
		String namePart = null;
		if (submission.getStudentLastName() != null && submission.getStudentLastName().trim().length() > 0)
			// If available use the last name.
			namePart = submission.getStudentLastName();
		if (namePart == null && submission.getStudentLastName() != null && submission.getStudentLastName().trim().length() > 0)
			// If the last name is unavailable, use the first name.
			namePart = submission.getStudentFirstName();
		if (namePart == null)
			// Lastly fall back to the word primary if we don't have a student name;
			namePart = "primary";
		
		String docPart = null;
		if (submission.getDocumentType() != null && submission.getDocumentType().trim().length() > 0)
			// Use the document type
			docPart = submission.getDocumentType();
		if (docPart == null)
			// If no document type then, just use the word "document"
			docPart = "document";
		
		String yearPart = null;
		if (submission.getGraduationYear() != null)
			// Add the year if helpfull
			yearPart = String.valueOf(submission.getGraduationYear());
		
		// Put it all together
		String filename = namePart + "-" + docPart;
		if (yearPart != null)
			filename += "-" + yearPart;
		
		// Sanitize the filename for any random characters
		filename = Normalizer.normalize(filename, Normalizer.Form.NFD);
		filename = filename.replaceAll("[^a-zA-Z0-9\\-_]", "");
		filename = filename.toUpperCase();
		
		String extension = FilenameUtils.getExtension(this.name);
		if (extension != null && extension.trim().length() > 0)
			filename += "." + extension;
		
		// Step 2) Check if that name already exists, and if so move it out of the way.
		Attachment exists = submission.findAttachmentByName(filename);
		if (exists != null && exists != this) {
			String existsBase = FilenameUtils.getBaseName(exists.getName());
			String existsExt = FilenameUtils.getExtension(exists.getName());
			String newName = ((JpaAttachmentImpl) (exists)).rename(existsBase,existsExt);
			exists.setName(newName);
			exists.save();
		}
		
		// Step 3) rename the file
		if (!filename.equals(this.getName()))
			this.setName(filename);
		
		
		return exists;
	}

}
