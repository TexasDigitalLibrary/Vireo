package org.tdl.vireo.model.jpa;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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

import org.tdl.vireo.model.AbstractModel;
import org.tdl.vireo.model.EmbargoType;
import org.tdl.vireo.model.GraduationMonth;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.NamedSearchFilter;
import org.tdl.vireo.search.GraduationSemester;

/**
 * Jpa specific implementation of Vireo's Named Search Filter interface.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
@Entity
@Table(name = "search_filter",
uniqueConstraints = { @UniqueConstraint( columnNames = { "creator_id", "name" } ) } )
public class JpaNamedSearchFilterImpl extends JpaAbstractModel<JpaNamedSearchFilterImpl> implements NamedSearchFilter{
	
	@ManyToOne(targetEntity=JpaPersonImpl.class, optional=false)
	public Person creator;
	
	@Column(nullable = false)
	public String name;
	
	public boolean publicFlag;
	
	@ElementCollection
	public List<String> searchText;
	
	@ElementCollection
	public List<String> states;
	
	@OneToMany(targetEntity=JpaPersonImpl.class)
	public List<Person> assignees;

	@OneToMany(targetEntity=JpaEmbargoTypeImpl.class)
	public List<EmbargoType> embargos;
	
	@ElementCollection
	public List<String> semesters;
	
	@Transient
	public List<GraduationSemester> cachedSemesters;
	
	@ElementCollection
	public List<String> degrees;
	
	@ElementCollection
	public List<String> departments;
	
	@ElementCollection
	public List<String> colleges;
	
	@ElementCollection
	public List<String> majors;
	
	@ElementCollection
	public List<String> documentTypes;
	
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
		this.searchText = new ArrayList<String>();
		this.states = new ArrayList<String>();
		this.assignees = new ArrayList<Person>();
		this.embargos = new ArrayList<EmbargoType>();
		this.semesters = new ArrayList<String>();
		this.cachedSemesters = new ArrayList<GraduationSemester>();
		this.degrees = new ArrayList<String>();
		this.departments = new ArrayList<String>();
		this.colleges = new ArrayList<String>();
		this.majors = new ArrayList<String>();
		this.documentTypes = new ArrayList<String>();
	}

	/**
	 * Just before being written to the database update the semester's data
	 * structure with the current state of the cachedSemester's data structure.
	 * Since while the object is live that is the structure that is manipulated.
	 */
	@PrePersist
	@PreUpdate
	@PreRemove
	public void onSave() {
		
		semesters.clear();
		for(GraduationSemester semester : cachedSemesters) {
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
	 * semester data structure.
	 */
	@PostPersist
	@PostLoad
	@PostUpdate
	public void onLoad() {
		
		cachedSemesters = new ArrayList<GraduationSemester>();
		for(String semesterString : semesters) {
			
			String[] split = semesterString.split("/");
			
			GraduationSemester semester = new GraduationSemester();
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
		
		return assignees;
	}

	@Override
	public void addAssignee(Person assignee) {
		assertManagerOrOwner(creator);
		assignees.add(assignee);
	}

	@Override
	public void removeAssignee(Person assignee) {
		assertManagerOrOwner(creator);
		assignees.remove(assignee);
	}
	
	@Override
	public List<EmbargoType> getEmbargoTypes() {
		return embargos;
	}
	
	@Override
	public void addEmbargoType(EmbargoType type) {
		assertManagerOrOwner(creator);
		embargos.add(type);
	}
	
	@Override
	public void removeEmbargoType(EmbargoType type) {
		assertManagerOrOwner(creator);
		embargos.remove(type);
	}

	@Override
	public List<GraduationSemester> getGraduationSemesters() {
		
		return cachedSemesters;
	}

	@Override
	public void addGraduationSemester(GraduationSemester semester) {
		assertManagerOrOwner(creator);
		cachedSemesters.add(semester);
	}
	
	@Override
	public void removeGraduationSemester(GraduationSemester semester) {
		assertManagerOrOwner(creator);
		cachedSemesters.remove(semester);
	}
	
	@Override
	public void addGraduationSemester(Integer year, Integer month) {
		addGraduationSemester(new GraduationSemester(year,month));
	}
	
	@Override
	public void removeGraduationSemester(Integer year, Integer month) {
		removeGraduationSemester(new GraduationSemester(year,month));
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
	public Date getDateRangeEnd() {
		return rangeEnd;
	}

	@Override
	public void setDateRange(Date start, Date end) {
		assertManagerOrOwner(creator);
		
		this.rangeStart = start;
		this.rangeEnd = end;
	}

}
