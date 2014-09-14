package org.tdl.vireo.model;

import java.util.HashMap;

/**
 * This class represents the departments which may award degree status by Vireo.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public interface Department extends AbstractOrderedModel {
	
	/**
	 * @return The name of the department
	 */
	public String getName();
	
	/**
	 * @param name The new name of the department
	 */
	public void setName(String name);
	
	/**
	 * @return The email addresses of the department
	 */
	public HashMap<Integer, String> getEmails();

	/**
	 * @param emails
	 *            Set the new email of this department.
	 */
	public void setEmails(HashMap emails);

	/**
	 * @param email
	 *            Add a new email to this department.
	 */
	public void addEmail(String email);
	
	/**
	 * @param email
	 *            Remove a email to this department.
	 */
	public void removeEmail(int index);

}
