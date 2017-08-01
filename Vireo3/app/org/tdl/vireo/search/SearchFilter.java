package org.tdl.vireo.search;

import java.util.Date;
import java.util.List;

import org.tdl.vireo.model.ActionLog;
import org.tdl.vireo.model.CustomActionDefinition;
import org.tdl.vireo.model.EmbargoType;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.Submission;

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
	 * @return A list of individual submissions this filter is restricted too.
	 */
	public List<Submission> getIncludedSubmissions();

	/**
	 * @param sub
	 *            Add an individual submission this filter is limmitted too.
	 */
	public void addIncludedSubmission(Submission sub);

	/**
	 * @param sub
	 *            Remove an individual submission from this filter.
	 */
	public void removeIncludedSubmission(Submission sub);

	/**
	 * @return A list of individual logs this filter will exclude.
	 */
	public List<ActionLog> getExcludedActionLogs();

	/**
	 * @param log
	 *            Add an individual logs this filter will exclude.
	 */
	public void addExcludedActionLog(ActionLog log);

	/**
	 * @param log
	 *            Remove an individual logs from this filter this filter will no longer exclude.
	 */
	public void removeExcludedActionLog(ActionLog log);
	
	/**
	 * @return A list of individual logs this filter is restricted too.
	 */
	public List<ActionLog> getIncludedActionLogs();

	/**
	 * @param log
	 *            Add an individual log this filter is limmitted too.
	 */
	public void addIncludedActionLog(ActionLog log);

	/**
	 * @param log
	 *            Remove an individual log from this filter.
	 */
	public void removeIncludedActionLog(ActionLog log);
	
	/**
	 * @return A list of individual submissions this filter will exclude.
	 */
	public List<Submission> getExcludedSubmissions();

	/**
	 * @param sub
	 *            Add an individual submission this filter will exclude.
	 */
	public void addExcludedSubmission(Submission sub);

	/**
	 * @param sub
	 *            Remove an individual submission from this filter this filter will no longer exclude.
	 */
	public void removeExcludedSubmission(Submission sub);
	
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
	 *         Spring Bean Names should be included in this list.
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
	 * @return The list of embago types to filter for.
	 */
	public List<EmbargoType> getEmbargoTypes();

	/**
	 * @param type
	 *            The embargo type to add to the filter.
	 */
	public void addEmbargoType(EmbargoType type);

	/**
	 * @param type
	 *            The embargo type to remove from the filter.
	 */
	public void removeEmbargoType(EmbargoType type);

	/**
	 * @return The graduation semesters
	 */
	public List<Semester> getGraduationSemesters();

	/**
	 * @param semester
	 *            The graduation semester to add to the filter.
	 */
	public void addGraduationSemester(Semester semester);

	/**
	 * @param semester
	 *            The graduation semester to remove from the filter.
	 */
	public void removeGraduationSemester(Semester semester);

	/**
	 * Add a graduation semester with the provided year and month.
	 * 
	 * @param year
	 *            The graduation year.
	 * @param month
	 *            The graduation month.
	 */
	public void addGraduationSemester(Integer year, Integer month);

	/**
	 * Remove a graduation semester with the provided year and month.
	 * 
	 * @param year
	 *            The graduation year.
	 * @param month
	 *            The graduation month.
	 */
	public void removeGraduationSemester(Integer year, Integer month);

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
	 * @return The list of programs
	 */
	public List<String> getPrograms();
	
	/**
	 * @param program
	 * 			Add a new program to the filter.
	 */
	public void addProgram(String program);
	
	/**
	 * @param program
	 * 			Remove a program from the filter.
	 */
	public void removeProgram(String program);
	
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
	 * @param start
	 *            The new start of the date range, may be null for infinite.
	 */
	public void setDateRangeStart(Date start);

	/**
	 * @return The end of the current date range search.
	 */
	public Date getDateRangeEnd();

	/**
	 * @param end
	 *            The new end of the date range, may be null for infinite
	 */
	public void setDateRangeEnd(Date end);
	
	/**
	 * @return The custom actions
	 */
	public List<CustomActionDefinition> getCustomActions();
	
	/**
	 * @param customAction
	 *            The custom action to add to the filter.
	 */
	public void addCustomAction(CustomActionDefinition customAction);

	/**
	 * @param customAction
	 *            The custom action to remove from the filter.
	 */
	public void removeCustomAction(CustomActionDefinition customAction);
	
	/**
	 * @return The list of columns associated with this SearchFilter
	 */
	public List<SearchOrder> getColumns();
	
	/**
	 * @param columns - The list of columns associated with this SearchFilter
	 */
	public void setColumns(List<SearchOrder> columns);
}
