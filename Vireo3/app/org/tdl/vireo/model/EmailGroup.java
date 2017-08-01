package org.tdl.vireo.model;

import java.util.HashMap;

/**
 * This abstract parent interface extends the base AbstractOrderedModel and adds the
 * ability for the objects to be sorted based upon a relative display order value placed
 * on each object.
 * 
 * @author <a href="mailto:huff@library.tamu.edu">Jeremy Huff</a>
 */
public interface EmailGroup extends AbstractOrderedModel {

	/**
	 * @return The email addresses of the program
	 */
	public HashMap<Integer, String> getEmails();

	/**
	 * @param emails
	 *            Set the new email of this program.
	 */
	public void setEmails(HashMap emails);

	/**
	 * @param email
	 *            Add a new email to this program.
	 */
	public void addEmail(String email);
	
	/**
	 * @param email
	 *            Remove a email to this program.
	 */
	public void removeEmail(int index);
}
