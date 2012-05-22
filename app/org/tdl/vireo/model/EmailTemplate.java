package org.tdl.vireo.model;

/**
 * An email templates available for common emails that Vireo staff send. The
 * message and subject values will have a set of standard template variable
 * replacements established like FULL_NAME, DOCUMENT_TITLE, etc..
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public interface EmailTemplate extends AbstractOrderedModel {

	/**
	 * @return The subject of this email template.
	 */
	public String getSubject();

	/**
	 * @param subject
	 *            The new subject of this email template.
	 */
	public void setSubject(String subject);

	/**
	 * @return The message of this email template.
	 */
	public String getMessage();

	/**
	 * @param message
	 *            The new message of this email template.
	 */
	public void setMessage(String message);
}
