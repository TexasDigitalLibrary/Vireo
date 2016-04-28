package org.tdl.vireo.model;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.REFRESH;
import static javax.persistence.FetchType.EAGER;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import edu.tamu.framework.model.BaseEntity;

/**
 * 
 */
@Entity
public class Attachment extends BaseEntity {
    
    @Transient
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	@Column(nullable = false, length=255)
	private String name;
	
	// allows normalization as relation. 
	// may be over encapsulation however. 
	// join column instead of column of strings.
	@ManyToOne(cascade = { DETACH, REFRESH, MERGE }, optional = false)
	private AttachmentType type;
	
	@Column(nullable = false)
	private Calendar date;
	
	@Column(unique = true)
	private UUID uuid;
	
	//TODO:  do we want to make an action log not optional on the attachment?
	@OneToMany(cascade=ALL, fetch = EAGER, orphanRemoval = true)
	private Set<ActionLog> actionLogs;
	
	public Attachment() {
	    setDate(Calendar.getInstance());
	}
	
	public Attachment(String name, UUID uuid, AttachmentType attachmentType) {
	 	this();
		setName(name);
		setType(attachmentType);
		setUuid(uuid);
		actionLogs = new TreeSet<ActionLog>();
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * 
	 * @return
	 */
	public AttachmentType getType() {
        return type;
    }

	/**
	 * 
	 * @param type
	 */
    public void setType(AttachmentType type) {
        this.type = type;
    }

    /**
	 * @return the date
	 */
	public Calendar getDate() {
		return date;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(Calendar date) {
		this.date = date;
	}

	/**
	 * @return the uuid
	 */
	public UUID getUuid() {
		return uuid;
	}

	/**
	 * @param uuid the uuid to set
	 */
	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}
	
	/**
	 * 
	 * @return
	 */
	public static SimpleDateFormat getDateformat() {
        return dateFormat;
    }

	/**
	 * 
	 * @param actionLogs
	 */
    public void setActionLogs(Set<ActionLog> actionLogs) {
        this.actionLogs = actionLogs;
    }
	
    /**
     * 
     * @return
     */
	public Set<ActionLog> getActionLogs() {
        return actionLogs;
    }

	/**
	 * 
	 * @param actionLog
	 */
    public void addActionLog(ActionLog actionLog) {
        this.actionLogs.add(actionLog);
    }
    
    /**
     * 
     * @param actionLog
     */
    public void removeActionLog(ActionLog actionLog) {
        this.actionLogs.remove(actionLog);
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
	/*
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
	*/
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
	/*
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
	
	*/
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
	 *//*
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
*/
}
