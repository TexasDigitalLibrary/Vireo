package org.tdl.vireo.model;

import java.util.HashMap;

/**
 * This class represents colleges which may award degree status by Vireo.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public interface College extends AbstractOrderedModel {

	/**
	 * @return The name of the college
	 */
	public String getName();

	/**
	 * @param name
	 *            Set the new name of this college.
	 */
	public void setName(String name);
	
	/**
	 * @return The email addresses of the college
	 */
	public HashMap<Integer, String> getEmails();

	/**
	 * @param emails
	 *            Set the new email of this college.
	 */
	public void setEmails(HashMap emails);

	/**
	 * @param email
	 *            Add a new email to this college.
	 */
	public void addEmail(String email);
	
	/**
	 * @param email
	 *            Remove a email to this college.
	 */
	public void removeEmail(int index);

}
