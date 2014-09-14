package org.tdl.vireo.model;

import java.util.HashMap;

/**
 * This class represents programs.
 * 
 * @author Micah Cooper
 */
public interface Program extends AbstractOrderedModel {

	/**
	 * @return The name of the program
	 */
	public String getName();

	/**
	 * @param name
	 *            Set the new name of this program.
	 */
	public void setName(String name);
	
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
