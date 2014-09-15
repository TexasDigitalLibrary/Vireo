package org.tdl.vireo.model.jpa;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.tdl.vireo.model.ActionLog;
import org.tdl.vireo.model.EmbargoType;
import org.tdl.vireo.model.NamedSearchFilter;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.PersonRepository;
import org.tdl.vireo.model.SettingsRepository;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.SubmissionRepository;
import org.tdl.vireo.search.SearchOrder;
import org.tdl.vireo.search.Semester;

import play.modules.spring.Spring;

/**
 * Jpa specific implementation of Vireo's Named Search Filter interface.
 * 
 * Importantly, shortly after Vireo 2.0 this class was modified to remove all
 * external dependencies on other JPA objects. It was proved problematic to
 * preserve the foreign key constraints with @ElementCollections because they
 * can not be referenced by a JPA query so there was no good way to handle
 * deleting those elements.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
@Entity
@Table(name = "search_filter",
uniqueConstraints = { @UniqueConstraint( columnNames = { "creator_id", "name" } ) } )
public class JpaNamedSearchFilterImpl extends JpaAbstractModel<JpaNamedSearchFilterImpl> implements NamedSearchFilter{
	
	@ManyToOne(targetEntity=JpaPersonImpl.class, optional=false)
	public Person creator;
	
	@Column(nullable = false, length=255)
	public String name;
	
	public boolean publicFlag;
	
	public boolean hasColumnsFlag;

	// These @ElementCollections have "_" so their names don't 
	// conflict with getIncludedSubmissions() and play's enhances.
	@ElementCollection
	@CollectionTable(
			name="search_filter_included_submissions",
			joinColumns=@JoinColumn(name="search_filter_id"))
	public List<Long> includedSubmissionIds;
	
	@ElementCollection
	@CollectionTable(
			name="search_filter_excluded_submissions",
			joinColumns=@JoinColumn(name="search_filter_id"))
	public List<Long> excludedSubmissionIds;
	
	@ElementCollection
	@CollectionTable(
			name="search_filter_included_actionlogs",
			joinColumns=@JoinColumn(name="search_filter_id"))
	public List<Long> includedActionLogIds;

	@ElementCollection
	@CollectionTable(
			name="search_filter_excluded_actionlogs",
			joinColumns=@JoinColumn(name="search_filter_id"))
	public List<Long> excludedActionLogIds;
	
	@ElementCollection
	@CollectionTable(
			name="search_filter_text",
			joinColumns=@JoinColumn(name="search_filter_id"))
	public List<String> searchText;
	
	@ElementCollection
	@CollectionTable(
			name="search_filter_states",
			joinColumns=@JoinColumn(name="search_filter_id"))
	public List<String> states;
	
	// Note: -1 means unassigned
	@ElementCollection
	@CollectionTable(
			name="search_filter_assignees",
			joinColumns=@JoinColumn(name="search_filter_id"))
	public List<Long> assigneeIds;

	@ElementCollection
	@CollectionTable(
			name="search_filter_embargos",
			joinColumns=@JoinColumn(name="search_filter_id"))
	public List<Long> embargoIds;
	
	@ElementCollection
	@CollectionTable(
			name="search_filter_semesters",
			joinColumns=@JoinColumn(name="search_filter_id"))
	public List<String> semesters;
	
	@Transient
	public List<Semester> cachedSemesters;
	
	@ElementCollection
	@CollectionTable(
			name="search_filter_degrees",
			joinColumns=@JoinColumn(name="search_filter_id"))
	public List<String> degrees;
	
	@ElementCollection
	@CollectionTable(
			name="search_filter_departments",
			joinColumns=@JoinColumn(name="search_filter_id"))
	public List<String> departments;

	@ElementCollection
	@CollectionTable(
			name="search_filter_programs",
			joinColumns=@JoinColumn(name="search_filter_id"))
	public List<String> programs;
	
	@ElementCollection
	@CollectionTable(
			name="search_filter_colleges",
			joinColumns=@JoinColumn(name="search_filter_id"))
	public List<String> colleges;
		
	@ElementCollection
	@CollectionTable(
			name="search_filter_majors",
			joinColumns=@JoinColumn(name="search_filter_id"))
	public List<String> majors;
	
	@ElementCollection
	@CollectionTable(
			name="search_filter_documenttypes",
			joinColumns=@JoinColumn(name="search_filter_id"))
	public List<String> documentTypes;
	
	@ElementCollection
	@CollectionTable(
			name="search_filter_columns",
			joinColumns=@JoinColumn(name="search_filter_id"))
	public List<SearchOrder> columns;

	public Boolean umiRelease;

	@Temporal(TemporalType.DATE)
	public Date rangeStart;
	
	@Temporal(TemporalType.DATE)
	public Date rangeEnd;
	
	/**
	 * Construct a new Named Search Filter
	 * 
	 * @param creator
	 *            The original creator of this filter.
	 * @param name
	 *            The unique name (amongst all other filters of this creator)
	 */
	protected JpaNamedSearchFilterImpl(Person creator, String name) {
		
		if (creator == null)
			throw new IllegalArgumentException("Creator is required");
		
		if (name == null || name.length() == 0)
			throw new IllegalArgumentException("Name is required");
		
		this.creator = creator;
		this.name = name;
		this.publicFlag = false;
		this.includedSubmissionIds = new ArrayList<Long>();
		this.excludedSubmissionIds = new ArrayList<Long>();
		this.includedActionLogIds = new ArrayList<Long>();
		this.excludedActionLogIds = new ArrayList<Long>();
		this.searchText = new ArrayList<String>();
		this.states = new ArrayList<String>();
		this.assigneeIds = new ArrayList<Long>();
		this.embargoIds = new ArrayList<Long>();
		this.semesters = new ArrayList<String>();
		this.cachedSemesters = new ArrayList<Semester>();
		this.degrees = new ArrayList<String>();
		this.departments = new ArrayList<String>();
		this.programs = new ArrayList<String>();
		this.colleges = new ArrayList<String>();
		this.majors = new ArrayList<String>();
		this.documentTypes = new ArrayList<String>();
	}

	/**
	 * Just before data is written to the database update some fields for
	 * saving.
	 * 
	 * 1) the semester's data structure with the current state of the
	 * cachedSemester's data structure. Since while the object is live that is
	 * the structure that is manipulated.
	 * 
	 * 2) Check if the unassigned user has been added to the list, aka (null).
	 * If so remove it from the list and set the unassigned flag.
	 */
	@PrePersist
	@PreUpdate
	@PreRemove
	public void onSave() {
		// 1) Semester
		semesters.clear();
		for(Semester semester : cachedSemesters) {
			// Format: year/month
			
			String value;
			if (semester.year == null)
				value = "null";
			else
				value = String.valueOf(semester.year);
			
			value += "/";
			
			if (semester.month == null)
				value += "null";
			else
				value += String.valueOf(semester.month);
			
			semesters.add(value);
		}
	}

	/**
	 * After being loaded from the database update our cached copy of the
	 * semester data structure and unassigned assignees.
	 */
	@PostPersist
	@PostLoad
	@PostUpdate
	public void onLoad() {
		// 1) Semesters
		cachedSemesters = new ArrayList<Semester>();
		for(String semesterString : semesters) {
			
			String[] split = semesterString.split("/");
			
			Semester semester = new Semester();
			if (!"null".equals(split[0]))
				semester.year = Integer.valueOf(split[0]);
			if (!"null".equals(split[1]))
				semester.month = Integer.valueOf(split[1]);
			
			cachedSemesters.add(semester);
		}
	}
	
	@Override
	public Person getCreator() {
		return creator;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		if (name == null || name.length() == 0)
			throw new IllegalArgumentException("Name is required");
		
		assertManagerOrOwner(creator);
		
		this.name = name;
		
	}

	@Override
	public boolean isPublic() {
		return publicFlag;
	}

	@Override
	public void setPublic(boolean publicFlag) {

		if (publicFlag) {
			// We're trying to make this public, only managers can do that.
			assertManager();
			
			this.publicFlag = true;
		} else {
			// We're making it private, the owner or manager can do that.
			assertManagerOrOwner(creator);
			
			this.publicFlag = false;
		}
	}

	@Override
	public boolean hasColumns() {
		return hasColumnsFlag;
	}

	@Override
	public void setHasColumns(boolean hasColumnsFlag) {
		this.hasColumnsFlag = hasColumnsFlag;
	}

	@Override
	public List<Submission> getIncludedSubmissions() {
		
		List<Submission> result = new ArrayList<Submission>();
		for (Long id : includedSubmissionIds) {
			SubmissionRepository subRepo = Spring.getBeanOfType(SubmissionRepository.class);
			Submission sub = subRepo.findSubmission(id);
			if (sub != null)
				result.add(sub);
		}
		
		return result;
	}

	@Override
	public void addIncludedSubmission(Submission sub) {
		
		includedSubmissionIds.add(sub.getId());
	}

	@Override
	public void removeIncludedSubmission(Submission sub) {
		includedSubmissionIds.remove(sub.getId());
	}
	
	@Override
	public List<Submission> getExcludedSubmissions() {
		
		List<Submission> result = new ArrayList<Submission>();
		for (Long id : excludedSubmissionIds) {
			SubmissionRepository subRepo = Spring.getBeanOfType(SubmissionRepository.class);
			Submission sub = subRepo.findSubmission(id);
			if (sub != null)
				result.add(sub);
		}
		
		return result;
	}

	@Override
	public void addExcludedSubmission(Submission sub) {
		excludedSubmissionIds.add(sub.getId());
	}

	@Override
	public void removeExcludedSubmission(Submission sub) {
		excludedSubmissionIds.remove(sub.getId());
	}
	
	@Override
	public List<ActionLog> getIncludedActionLogs() {
		
		List<ActionLog> result = new ArrayList<ActionLog>();
		for (Long id : includedActionLogIds) {
			SubmissionRepository subRepo = Spring.getBeanOfType(SubmissionRepository.class);
			ActionLog log = subRepo.findActionLog(id);
			if (log != null)
				result.add(log);
		}
		
		return result;
	}

	@Override
	public void addIncludedActionLog(ActionLog log) {
		
		List<Long> list = includedActionLogIds;
		list.add(log.getId());
		
		includedActionLogIds.add(log.getId());
	}

	@Override
	public void removeIncludedActionLog(ActionLog log) {
		includedActionLogIds.remove(log.getId());
	}
	
	@Override
	public List<ActionLog> getExcludedActionLogs() {
		
		List<ActionLog> result = new ArrayList<ActionLog>();
		for (Long id : excludedActionLogIds) {
			SubmissionRepository subRepo = Spring.getBeanOfType(SubmissionRepository.class);
			ActionLog log = subRepo.findActionLog(id);
			if (log != null)
				result.add(log);
		}
		
		return result;
	}

	@Override
	public void addExcludedActionLog(ActionLog log) {
		excludedActionLogIds.add(log.getId());
	}

	@Override
	public void removeExcludedActionLog(ActionLog log) {
		excludedActionLogIds.remove(log.getId());
	}
	
	@Override
	public List<String> getSearchText() {
		return searchText;
	}

	@Override
	public void addSearchText(String text) {
		assertManagerOrOwner(creator);		
		searchText.add(text);
	}

	@Override
	public void removeSearchText(String text) {
		assertManagerOrOwner(creator);
		searchText.remove(text);
	}

	@Override
	public List<String> getStates() {
		return states;
	}

	@Override
	public void addState(String state) {
		
		assertManagerOrOwner(creator);
		states.add(state);
	}

	@Override
	public void removeState(String state) {
		assertManagerOrOwner(creator);
		states.remove(state);
	}

	@Override
	public List<Person> getAssignees() {		
		List<Person> result = new ArrayList<Person>();
		for (Long id : assigneeIds) {
			PersonRepository personRepo = Spring.getBeanOfType(PersonRepository.class);
			if (id != -1) {
				Person person = personRepo.findPerson(id);
				if (person != null)
					result.add(person);
			} else {
				result.add(null);
			}
		}
		return result;
	}

	@Override
	public void addAssignee(Person assignee) {
		assertManagerOrOwner(creator);
		
		if (assignee == null)
			assigneeIds.add(-1L);
		else
			assigneeIds.add(assignee.getId());
		
	}

	@Override
	public void removeAssignee(Person assignee) {
		assertManagerOrOwner(creator);
		
		if (assignee == null)
			assigneeIds.remove(-1L);
		else
			assigneeIds.remove(assignee);
	}	
	
	@Override
	public List<EmbargoType> getEmbargoTypes() {
		
		List<EmbargoType> result = new ArrayList<EmbargoType>();
		for (Long id : embargoIds) {
			SettingsRepository settingRepo = Spring.getBeanOfType(SettingsRepository.class);
			EmbargoType type = settingRepo.findEmbargoType(id);
			if (type != null)
				result.add(type);
		}
		
		return result;

	}
	
	@Override
	public void addEmbargoType(EmbargoType type) {
		assertManagerOrOwner(creator);
		embargoIds.add(type.getId());
	}
	
	@Override
	public void removeEmbargoType(EmbargoType type) {
		assertManagerOrOwner(creator);
		embargoIds.remove(type.getId());
	}

	@Override
	public List<Semester> getGraduationSemesters() {
		
		return cachedSemesters;
	}

	@Override
	public void addGraduationSemester(Semester semester) {
		assertManagerOrOwner(creator);
		cachedSemesters.add(semester);
	}
	
	@Override
	public void removeGraduationSemester(Semester semester) {
		assertManagerOrOwner(creator);
		cachedSemesters.remove(semester);
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
		assertManagerOrOwner(creator);
		degrees.add(degree);
	}

	@Override
	public void removeDegree(String degree) {
		assertManagerOrOwner(creator);
		degrees.remove(degree);
	}

	@Override
	public List<String> getDepartments() {
		return departments;
	}

	@Override
	public void addDepartment(String department) {
		assertManagerOrOwner(creator);
		departments.add(department);
	}

	@Override
	public void removeDepartment(String department) {
		assertManagerOrOwner(creator);
		departments.remove(department);
	}

	@Override
	public List<String> getPrograms() {
		return programs;
	}
	
	@Override
	public void addProgram(String program) {
		assertManagerOrOwner(creator);
		programs.add(program);
	}
	
	@Override
	public void removeProgram(String program) {
		assertManagerOrOwner(creator);
		programs.remove(program);
	}
	
	@Override
	public List<String> getColleges() {
		return colleges;
	}

	@Override
	public void addCollege(String college) {
		assertManagerOrOwner(creator);
		colleges.add(college);
	}

	@Override
	public void removeCollege(String college) {
		assertManagerOrOwner(creator);
		colleges.remove(college);
	}	

	@Override
	public List<String> getMajors() {
		return majors;
	}

	@Override
	public void addMajor(String major) {
		assertManagerOrOwner(creator);
		majors.add(major);
	}

	@Override
	public void removeMajor(String major) {
		assertManagerOrOwner(creator);
		majors.remove(major);
	}

	@Override
	public List<String> getDocumentTypes() {
		return documentTypes;
	}

	@Override
	public void addDocumentType(String documentType) {
		assertManagerOrOwner(creator);
		documentTypes.add(documentType);
	}

	@Override
	public void removeDocumentType(String documentType) {
		assertManagerOrOwner(creator);
		documentTypes.remove(documentType);
	}

	@Override
	public Boolean getUMIRelease() {
		return umiRelease;
	}

	@Override
	public void setUMIRelease(Boolean value) {
		assertManagerOrOwner(creator);
		
		// Note: null is valid!
		this.umiRelease = value;
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

	@Override
	public List<SearchOrder> getColumns() {
		return columns;
	}

	@Override
	public void setColumns(List<SearchOrder> columns) {
		this.columns = columns;
	}
}
