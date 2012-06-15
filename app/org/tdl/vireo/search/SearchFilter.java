package org.tdl.vireo.search;

import java.util.Date;
import java.util.List;

import org.tdl.vireo.model.Person;
import org.tdl.vireo.state.State;

/**
 * A filter search is a set of parameters to search for a set of Vireo
 * submission. The object is used by the SubmissionRepository to filter the set
 * of all submissions by particular criteria.
 * 
 * Implementers should pick on of the child interfaces instead of implementing
 * this interface directly. The two child interfaces are NamedSearchfilter, and
 * ActiveSearchFilter. The named version is designed to be persisted in the
 * database, while the active search filter is designed to be serialized to a
 * cookie value. Both types may be feed to the filter search to produce search
 * results.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public interface SearchFilter {

	/**
	 * @return The list of all free-form search parameters
	 */
	public List<String> getSearchText();

	/**
	 * @param text
	 *            The new text search to add.
	 */
	public void addSearchText(String text);

	/**
	 * @param text
	 *            The text search to remove
	 */
	public void removeSearchText(String text);

	/**
	 * @return The list of all submission states to search for. Only valid 
	 * Spring Bean Names should be included in this list.
	 */
	public List<String> getStates();

	/**
	 * @param status
	 *            The new state to add to the filter.
	 */
	public void addState(String state);

	/**
	 * @param status
	 *            Remove the state from the filter.
	 */
	public void removeState(String state);

	/**
	 * @return The list of assignees to filter for.
	 */
	public List<Person> getAssignees();

	/**
	 * @param assignee
	 *            A new assignee to add to the filter.
	 */
	public void addAssignee(Person assignee);

	/**
	 * @param assignee
	 *            The assignee to remove from the filter.
	 */
	public void removeAssignee(Person assignee);

	/**
	 * @return The list of graduation years
	 */
	public List<Integer> getGraduationYears();

	/**
	 * @param year
	 *            Add a year to the filter.
	 */
	public void addGraduationYear(Integer year);

	/**
	 * @param year
	 *            Remove a year from the filter.
	 */
	public void removeGraduationYear(Integer year);

	/**
	 * @return The list of graduation months
	 */
	public List<Integer> getGraduationMonths();

	/**
	 * @param month
	 *            Add a new graduation month to the filter
	 */
	public void addGraduationMonth(Integer month);

	/**
	 * @param month
	 *            Remove a graduation month from the filter.
	 */
	public void removeGraduationMonth(Integer month);

	/**
	 * @return The list of degrees
	 */
	public List<String> getDegrees();

	/**
	 * @param degree
	 *            A a new degree to the filter.
	 */
	public void addDegree(String degree);

	/**
	 * @param degree
	 *            Remove a degree from the filter.
	 */
	public void removeDegree(String degree);

	/**
	 * @return The list of departments.
	 */
	public List<String> getDepartments();

	/**
	 * @param department
	 *            Add a new department to the filter.
	 */
	public void addDepartment(String department);

	/**
	 * @param department
	 *            remove a department from the filter.
	 */
	public void removeDepartment(String department);

	/**
	 * @return The list of colleges
	 */
	public List<String> getColleges();

	/**
	 * 
	 * @param college
	 *            Add a new college to the filter.
	 */
	public void addCollege(String college);

	/**
	 * 
	 * @param college
	 *            Remove a college from the filter.
	 */
	public void removeCollege(String college);

	/**
	 * 
	 * @return The list of majors
	 */
	public List<String> getMajors();

	/**
	 * @param major
	 *            Add a new major to the filter.
	 */
	public void addMajor(String major);

	/**
	 * @param major
	 *            Remove a major from the filter.
	 */
	public void removeMajor(String major);

	/**
	 * @return The list of document types
	 */
	public List<String> getDocumentTypes();

	/**
	 * 
	 * @param documentType
	 *            add a new documentType to the filter.
	 */
	public void addDocumentType(String documentType);

	/**
	 * @param documentType
	 *            Remove a document type from the filter.
	 */
	public void removeDocumentType(String documentType);

	/**
	 * True -> applications are set to be released to UMI 
	 * 
	 * False -> applications are not set to be released. 
	 * 
	 * Null -> Either released or not.
	 * 
	 * @return How to filter for UMI release
	 */
	public Boolean getUMIRelease();

	/**
	 * @param value
	 *            Set the UMI release filter.
	 */
	public void setUMIRelease(Boolean value);

	/**
	 * @return The start of the current date range search.
	 */
	public Date getDateRangeStart();

	/**
	 * @return The end of the current date range search.
	 */
	public Date getDateRangeEnd();

	/**
	 * Set a new start and end date for a date range search.
	 * 
	 * @param start
	 *            The start date, inclusive
	 * @param end
	 *            The end date, inclusive.
	 */
	public void setDateRange(Date start, Date end);

}
