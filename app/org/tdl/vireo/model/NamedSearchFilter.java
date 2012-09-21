package org.tdl.vireo.model;

import org.tdl.vireo.search.SearchFilter;

/**
 * A named filter search is a set of parameters to search for a set of Vireo
 * submission. The object is used by the SubmissionRepository to filter the set
 * of all submissions by particular criteria. This interface builds upon the
 * basic non persistable SearchFilter by adding a name, a creator, and a public
 * flag. Named filters are persisted in the database while other implementation
 * may only reside in memory.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public interface NamedSearchFilter extends AbstractModel, SearchFilter {

	/**
	 * @return The person who created this filter search
	 */
	public Person getCreator();

	/**
	 * @return The user assigned name of this filter search.
	 */
	public String getName();

	/**
	 * @param name
	 *            The new name of this filter search.
	 */
	public void setName(String name);

	/**
	 * @return Weather this filter search is publicly viewable by all vireo
	 *         reviewers.
	 */
	public boolean isPublic();

	/**
	 * @param publicFlag
	 *            Set this filter search as public or not.
	 */
	public void setPublic(boolean publicFlag);

}
