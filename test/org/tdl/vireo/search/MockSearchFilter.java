package org.tdl.vireo.search;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.tdl.vireo.model.ActionLog;
import org.tdl.vireo.model.EmbargoType;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.Submission;

/**
 * Mock implementation of the generic search filter interface.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class MockSearchFilter implements SearchFilter {

	public List<Submission> includedSubmissions = new ArrayList<Submission>();
	public List<Submission> excludedSubmissions = new ArrayList<Submission>();
	public List<ActionLog> includedActionLogs = new ArrayList<ActionLog>();
	public List<ActionLog> excludedActionLogs = new ArrayList<ActionLog>();
	public List<String> searchText = new ArrayList<String>();
	public List<String> states = new ArrayList<String>();
	public List<Person> assignees = new ArrayList<Person>();
	public List<EmbargoType> embargos = new ArrayList<EmbargoType>();
	public List<Semester> semesters = new ArrayList<Semester>();
	public List<String> degrees = new ArrayList<String>();
	public List<String> departments = new ArrayList<String>();
	public List<String> colleges = new ArrayList<String>();
	public List<String> majors = new ArrayList<String>();
	public List<String> documentTypes = new ArrayList<String>();
	public Boolean umiRelease = null;
	public Date rangeStart = null;
	public Date rangeEnd = null;
	
	@Override
	public List<Submission> getIncludedSubmissions() {
		return includedSubmissions;
	}

	@Override
	public void addIncludedSubmission(Submission sub) {
		includedSubmissions.add(sub);
	}

	@Override
	public void removeIncludedSubmission(Submission sub) {
		includedSubmissions.remove(sub);
	}
	
	@Override
	public List<Submission> getExcludedSubmissions() {
		return excludedSubmissions;
	}

	@Override
	public void addExcludedSubmission(Submission sub) {
		excludedSubmissions.add(sub);
	}

	@Override
	public void removeExcludedSubmission(Submission sub) {
		excludedSubmissions.remove(sub);
	}
	
	@Override
	public List<ActionLog> getIncludedActionLogs() {
		return includedActionLogs;
	}

	@Override
	public void addIncludedActionLog(ActionLog log) {
		includedActionLogs.add(log);
	}

	@Override
	public void removeIncludedActionLog(ActionLog log) {
		includedActionLogs.remove(log);
	}
	
	@Override
	public List<ActionLog> getExcludedActionLogs() {
		return excludedActionLogs;
	}

	@Override
	public void addExcludedActionLog(ActionLog log) {
		excludedActionLogs.add(log);
	}

	@Override
	public void removeExcludedActionLog(ActionLog log) {
		excludedActionLogs.remove(log);
	}

	@Override
	public List<String> getSearchText() {
		return searchText;
	}

	@Override
	public void addSearchText(String text) {
		searchText.add(text);
	}

	@Override
	public void removeSearchText(String text) {
		searchText.remove(text);
	}

	@Override
	public List<String> getStates() {
		return states;
	}

	@Override
	public void addState(String state) {
		states.add(state);
	}

	@Override
	public void removeState(String state) {
		states.remove(state);
	}

	@Override
	public List<Person> getAssignees() {
		return assignees;
	}

	@Override
	public void addAssignee(Person assignee) {
		assignees.add(assignee);
	}

	@Override
	public void removeAssignee(Person assignee) {
		assignees.remove(assignee);
	}

	@Override
	public List<EmbargoType> getEmbargoTypes() {
		return embargos;
	}

	@Override
	public void addEmbargoType(EmbargoType type) {
		embargos.add(type);
	}

	@Override
	public void removeEmbargoType(EmbargoType type) {
		embargos.remove(type);
	}

	@Override
	public List<Semester> getGraduationSemesters() {
		return semesters;
	}

	@Override
	public void addGraduationSemester(Semester semester) {
		semesters.add(semester);
	}

	@Override
	public void removeGraduationSemester(Semester semester) {
		semesters.remove(semester);
	}

	@Override
	public void addGraduationSemester(Integer year, Integer month) {
		addGraduationSemester(new Semester(year,month));
	}

	@Override
	public void removeGraduationSemester(Integer year, Integer month) {
		removeGraduationSemester(new Semester(year,month));
	}

	@Override
	public List<String> getDegrees() {
		return degrees;
	}

	@Override
	public void addDegree(String degree) {
		degrees.add(degree);
	}

	@Override
	public void removeDegree(String degree) {
		degrees.remove(degree);
	}

	@Override
	public List<String> getDepartments() {
		return departments;
	}

	@Override
	public void addDepartment(String department) {
		departments.add(department);
	}

	@Override
	public void removeDepartment(String department) {
		departments.remove(department);
	}

	@Override
	public List<String> getColleges() {
		return colleges;
	}

	@Override
	public void addCollege(String college) {
		colleges.add(college);
	}

	@Override
	public void removeCollege(String college) {
		colleges.remove(college);
	}

	@Override
	public List<String> getMajors() {
		return majors;
	}

	@Override
	public void addMajor(String major) {
		majors.add(major);
	}

	@Override
	public void removeMajor(String major) {
		majors.remove(major);
	}

	@Override
	public List<String> getDocumentTypes() {
		return documentTypes;
	}

	@Override
	public void addDocumentType(String documentType) {
		documentTypes.add(documentType);
	}

	@Override
	public void removeDocumentType(String documentType) {
		documentTypes.remove(documentType);
	}

	@Override
	public Boolean getUMIRelease() {
		return umiRelease;
	}

	@Override
	public void setUMIRelease(Boolean value) {
		umiRelease = value;
	}

	@Override
	public Date getDateRangeStart() {
		return rangeStart;
	}

	@Override
	public void setDateRangeStart(Date start) {
		rangeStart = start;
	}

	@Override
	public Date getDateRangeEnd() {
		return rangeEnd;
	}

	@Override
	public void setDateRangeEnd(Date end) {
		rangeEnd = end;
	}

}
