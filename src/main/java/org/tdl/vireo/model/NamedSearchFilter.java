package org.tdl.vireo.model;

import static javax.persistence.FetchType.LAZY;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
import org.tdl.vireo.model.CustomActionDefinition;
import org.tdl.vireo.model.EmbargoType;
import org.tdl.vireo.model.Submission;
//import org.tdl.vireo.search.SearchOrder;
//import org.tdl.vireo.search.Semester;

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
@Entity
@Table(uniqueConstraints = { @UniqueConstraint( columnNames = { "creator_id", "name" } ) } )
public class NamedSearchFilter extends BaseEntity{
	
	@Column(nullable = false)
	private String name;
	
	private Boolean isPublicFlag;
	
	private Boolean isUmiRelease;
	
	@ManyToOne(optional=false)
	private User creator;
	
	@Temporal(TemporalType.DATE)
	private Calendar rangeStart;
	
	@Temporal(TemporalType.DATE)
	private Calendar rangeEnd;
	
	private Set<Submission> includedSubmissions;
	
	private Set<Submission> excludedSubmissions;
	
	private Set<ActionLog> includedActionLogs;

	private Set<ActionLog> excludedActionLogIds;
	
	//TODO
	/*@ElementCollection
	@CollectionTable(name="search_filter_text", joinColumns=@JoinColumn(name="search_filter_id"))
	private Set<String> searchText;*/
	
	private Set<SubmissionState> submissionStates;
	
	private Set<User> assignees;
	
	private Set<EmbargoType> embargoTypes;
	
	private Set<Calendar> semesters;
	
	//TODO
	/*@Transient
	private Set<Semester> cachedSemesters;*/
	
	private Set<Organization> organizations;
		
	//TODO: can we use a predicate with specific degree level values here instead of a string?
	@ElementCollection
	@CollectionTable(name="search_filter_documenttypes",joinColumns=@JoinColumn(name="search_filter_id"))
	private Set<String> documentTypes;
	
	//TODO:  this is just subsumed by the order-by functionality of a table, although we 
	//@ElementCollection
	//@CollectionTable(name="search_filter_columns",joinColumns=@JoinColumn(name="search_filter_id"))
	//TODO private Set<SearchOrder> columns;
	
	

	//@ElementCollection
	//@CollectionTable(name="search_filter_customactions",joinColumns=@JoinColumn(name="search_filter_id"))
	//private Set<Long> customActionIds;
	private Set<CustomActionValue> customActionValues;
	
	
	public NamedSearchFilter() {
		isPublicFlag(false);
		
		
		setName(new String());
		
		
		isUmiRelease(false);
		
		setCreator(null);
		
		setRangeStart(Calendar.getInstance());
		
		setRangeEnd(Calendar.getInstance());
		
		setIncludedSubmissions(new TreeSet<Submission>());
		
		setExcludedSubmissions(new TreeSet<Submission>());
		
		setIncludedActionLogs(new TreeSet<ActionLog>());

		setExcludedActionLogIds(new TreeSet<ActionLog>());
		
		setSubmissionStates(new TreeSet<SubmissionState>());
		
		setAssignees(new TreeSet<User>());
		
		setEmbargoTypes(new TreeSet<EmbargoType>());
		
		setSemesters(new TreeSet<Calendar>());
		
		setOrganizations(new TreeSet<Organization>());
		
		//TODO:  can this be handled better than just a set of Strings?
		setDocumentTypes(new TreeSet<String>());
		
		setCustomActionValues(new TreeSet<CustomActionValue>());
		
		
		
	}
	
	/**
	 * Construct a new Named Search Filter
	 * 
	 * @param creator
	 *            The original creator of this filter.
	 * @param name
	 *            The unique name (amongst all other filters of this creator)
	 */
	protected NamedSearchFilter(User creator, String name) {
		this();
		this.creator = creator;
		this.name = name;
		/*this.includedSubmissionIds = new ArrayList<Long>();
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
		this.columns = new ArrayList<SearchOrder>();
		this.customActionIds = new ArrayList<Long>();*/
	}

	/**
	 * @return the isPublicFlag
	 */
	public Boolean isPublicFlag() {
		return isPublicFlag;
	}

	/**
	 * @param isPublicFlag the isPublicFlag to set
	 */
	public void isPublicFlag(Boolean isPublicFlag) {
		this.isPublicFlag = isPublicFlag;
	}

	/**
	 * @return the isUmiRelease
	 */
	public Boolean isUmiRelease() {
		return isUmiRelease;
	}

	/**
	 * @param isUmiRelease the isUmiRelease to set
	 */
	public void isUmiRelease(Boolean isUmiRelease) {
		this.isUmiRelease = isUmiRelease;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the creator
	 */
	public User getCreator() {
		return creator;
	}

	/**
	 * @param creator the creator to set
	 */
	public void setCreator(User creator) {
		this.creator = creator;
	}

	/**
	 * @return the rangeStart
	 */
	public Calendar getRangeStart() {
		return rangeStart;
	}

	/**
	 * @param rangeStart the rangeStart to set
	 */
	public void setRangeStart(Calendar rangeStart) {
		this.rangeStart = rangeStart;
	}

	/**
	 * @return the rangeEnd
	 */
	public Calendar getRangeEnd() {
		return rangeEnd;
	}

	/**
	 * @param rangeEnd the rangeEnd to set
	 */
	public void setRangeEnd(Calendar rangeEnd) {
		this.rangeEnd = rangeEnd;
	}

	public Set<Submission> getIncludedSubmissions() {
		return includedSubmissions;
	}

	public void setIncludedSubmissions(Set<Submission> includedSubmissions) {
		this.includedSubmissions = includedSubmissions;
	}

	public Set<Submission> getExcludedSubmissions() {
		return excludedSubmissions;
	}

	public void setExcludedSubmissions(Set<Submission> excludedSubmissions) {
		this.excludedSubmissions = excludedSubmissions;
	}

	public Set<ActionLog> getIncludedActionLogs() {
		return includedActionLogs;
	}

	public void setIncludedActionLogs(Set<ActionLog> includedActionLogs) {
		this.includedActionLogs = includedActionLogs;
	}

	public Set<ActionLog> getExcludedActionLogIds() {
		return excludedActionLogIds;
	}

	public void setExcludedActionLogIds(Set<ActionLog> excludedActionLogIds) {
		this.excludedActionLogIds = excludedActionLogIds;
	}

	public Set<SubmissionState> getSubmissionStates() {
		return submissionStates;
	}

	public void setSubmissionStates(Set<SubmissionState> submissionStates) {
		this.submissionStates = submissionStates;
	}

	public Set<User> getAssignees() {
		return assignees;
	}

	public void setAssignees(Set<User> assignees) {
		this.assignees = assignees;
	}

	public Set<EmbargoType> getEmbargoTypes() {
		return embargoTypes;
	}

	public void setEmbargoTypes(Set<EmbargoType> embargoTypes) {
		this.embargoTypes = embargoTypes;
	}

	public Set<Calendar> getSemesters() {
		return semesters;
	}

	public void setSemesters(Set<Calendar> semesters) {
		this.semesters = semesters;
	}

	public Set<Organization> getOrganizations() {
		return organizations;
	}

	public void setOrganizations(Set<Organization> organizations) {
		this.organizations = organizations;
	}

	public Set<CustomActionValue> getCustomActionValues() {
		return customActionValues;
	}

	public void setCustomActionValues(Set<CustomActionValue> customActionValues) {
		this.customActionValues = customActionValues;
	}
	
	
	//TODO:  can the document types be represented more elegantly than with String?
	public Set<String> getDocumentTypes() {
		return documentTypes;
	}

	public void setDocumentTypes(Set<String> documentTypes) {
		this.documentTypes = documentTypes;
	}

	
	
	
	
	//TODO -to be considered
	

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
	 *//*
	 
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
	
	*//**
	 * After being loaded from the database update our cached copy of the
	 * semester data structure and unassigned assignees.
	 *//*
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
		return (this.columns.size() > 0);
	}

	@Override
	public List<Submission> getIncludedSubmissions() {
		
		List<Submission> result = new ArrayList<Submission>();
		SubmissionRepository subRepo = Spring.getBeanOfType(SubmissionRepository.class);
		for (Long id : includedSubmissionIds) {
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
		SubmissionRepository subRepo = Spring.getBeanOfType(SubmissionRepository.class);
		for (Long id : excludedSubmissionIds) {
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

	@Override
    public List<CustomActionDefinition> getCustomActions() {
		List<CustomActionDefinition> result = new ArrayList<CustomActionDefinition>();
		
		SettingsRepository settingRepo = Spring.getBeanOfType(SettingsRepository.class);
		for (Long id : customActionIds) {
			CustomActionDefinition customActionDefinition = settingRepo.findCustomActionDefinition(id);
			if (customActionDefinition != null)
				result.add(customActionDefinition);
		}
	    return result;
    }

	@Override
    public void addCustomAction(CustomActionDefinition customAction) {
		this.customActionIds.add(customAction.getId());
    }

	@Override
    public void removeCustomAction(CustomActionDefinition customAction) {
		this.customActionIds.remove(customAction.getId());
    }*/
}
