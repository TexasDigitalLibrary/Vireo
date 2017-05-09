package org.tdl.vireo.model;

import java.io.File;
import java.util.Date;

/**
 * A file attachment associated with a particular submission such as the primary
 * document, supplemental content, or feedback. The metadata about an
 * attachment, it's name, or mimetype, may be changed but the contents of the
 * file is immutable. If you need to modify the attachments contents then just
 * delete the attachment and create a new document for the updated content.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public interface Attachment extends AbstractModel {

	/**
	 * Archive this attachment.
	 * 
	 * This means changing the type to be ARCHIVED, and modifying the name to
	 * denote that it is an archive file (so that the name can be re-used)
	 * 
	 * This will fail if the attachment is already an archive.
	 */
	public void archive();
	
	/**
	 * @return The submission this attachment belongs too.
	 */
	public Submission getSubmission();
	
	/**
	 * The name of the file will be how the file is named on disk with an
	 * extension following the normal conventions of file names. Some examples
	 * are: "myresearch.pdf", "survey_data.xsls", etc...
	 * 
	 * @return The technical name of the file.
	 */
	public String getName();

	/**
	 * @param name
	 *            The new technical name of the file.
	 */
	public void setName(String name);

	/**
	 * @return The attachment types.
	 */
	public AttachmentType getType();

	/**
	 * @param type
	 *            The new type of this attachment.
	 */
	public void setType(AttachmentType type);

	/**
	 * @return The mimetype of the file.
	 */
	public String getMimeType();

	/**
	 * @return The file size measured in bytes.
	 */
	public long getSize();

	/**
	 * @return The displayable size of this file, such as "12 MB".
	 */
	public String getDisplaySize();

	/**
	 * @return The file.
	 */
	public File getFile();
	
	/**
	 * @return The person that uploaded the attachment.
	 */
	public Person getPerson();
	
	/**
	 * @return The date the attachment was uploaded.
	 */
	public Date getDate();

}
