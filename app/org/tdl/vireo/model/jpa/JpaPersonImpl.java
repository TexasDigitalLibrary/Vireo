package org.tdl.vireo.model.jpa;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.tdl.vireo.model.AbstractModel;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.Preference;
import org.tdl.vireo.model.RoleType;

import play.db.jpa.Model;

/**
 * Jpa specific implementation of Vireo's Person interface.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
@Entity
@Table(name = "Person")
public class JpaPersonImpl extends Model implements Person {

	@Column(nullable = false, unique = true)
	public String netid;

	@Column(nullable = false, unique = true)
	public String email;

	@Column(nullable = false)
	public String firstName;

	@Column(nullable = false)
	public String lastName;
	public String middleInitial;
	public String displayName;

	public Integer birthYear;

	public String currentPhoneNumber;
	public String currentPostalAddress;
	public String currentEmailAddress;

	public String permanentPhoneNumber;
	public String permanentPostalAddress;
	public String permanentEmailAddress;

	public String currentDepartment;
	public String currentCollege;
	public String currentMajor;
	public Integer currentGraduationYear;
	public Integer currentGraduationMonth;

	@OneToMany(targetEntity=JpaPreferenceImpl.class, mappedBy="person", cascade=CascadeType.ALL)
	public Set<Preference> preferences;
	
	@Column(nullable = false)
	public RoleType role;

	/**
	 * Create a new JpaPersonImpl
	 * 
	 * @param netid
	 *            The netid of the new person.
	 * @param email
	 *            The email of the new person.
	 * @param firstName
	 *            The first name of the new person.
	 * @param lastName
	 *            The last name of the new person.
	 * @param role
	 *            The role for the new person.
	 */
	protected JpaPersonImpl(String netid, String email, String firstName,
			String lastName, RoleType role) {

		if (netid == null || netid.length() == 0)
			throw new IllegalArgumentException("Netid is required");
		
		if (email == null || email.length() == 0)
			throw new IllegalArgumentException("Email is required");
		
		if (firstName == null || firstName.length() == 0)
			throw new IllegalArgumentException("FirstName is required");
		
		if (lastName == null || lastName.length() == 0)
			throw new IllegalArgumentException("lastName is required");
		
		if (role == null )
			throw new IllegalArgumentException("Role is required");

		this.netid = netid;
		this.email = email;
		this.firstName = firstName;
		this.lastName = lastName;
		this.preferences = new HashSet<Preference>();
		this.role = role;
	}

	@Override
	public JpaPersonImpl save() {
		return super.save();
	}

	@Override
	public JpaPersonImpl delete() {
		return super.delete();
	}

	@Override
	public JpaPersonImpl refresh() {
		return super.refresh();
	}

	@Override
	public JpaPersonImpl merge() {
		return super.merge();
	}

	@Override
	public String getNetId() {
		return netid;
	}

	@Override
	public void setNetId(String netid) {
		if (netid == null || netid.length() == 0)
			throw new IllegalArgumentException("Netid is required");
		
		this.netid = netid;
	}

	@Override
	public String getEmail() {
		return email;
	}

	@Override
	public void setEmail(String email) {
		if (email == null || email.length() == 0)
			throw new IllegalArgumentException("Email is required");
		this.email = email;
	}

	@Override
	public String getFirstName() {
		return firstName;
	}

	@Override
	public void setFirstName(String firstName) {
		
		if (firstName == null || firstName.length() == 0)
			throw new IllegalArgumentException("firstName is required");
		this.firstName = firstName;
	}

	@Override
	public String getMiddleInitial() {
		return middleInitial;
	}

	@Override
	public void setMiddleInitial(String middleInitial) {
		this.middleInitial = middleInitial;
	}

	@Override
	public String getLastName() {
		return lastName;
	}

	@Override
	public void setLastName(String lastName) {
		
		if (lastName == null || lastName.length() == 0)
			throw new IllegalArgumentException("lastName is required");
		
		this.lastName = lastName;
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}

	@Override
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	@Override
	public Integer getBirthYear() {
		return birthYear;
	}

	@Override
	public void setBirthYear(Integer year) {
		this.birthYear = year;
	}

	@Override
	public String getCurrentPhoneNumber() {
		return currentPhoneNumber;
	}

	@Override
	public void setCurrentPhoneNumber(String phoneNumber) {
		this.currentPhoneNumber = phoneNumber;
	}

	@Override
	public String getCurrentPostalAddress() {
		return currentPostalAddress;
	}

	@Override
	public void setCurrentPostalAddress(String postalAddress) {
		this.currentPostalAddress = postalAddress;
	}

	@Override
	public String getCurrentEmailAddress() {
		return currentEmailAddress;
	}

	@Override
	public void setCurrentEmailAddress(String email) {
		this.currentEmailAddress = email;
	}

	@Override
	public String getPermanentPhoneNumber() {
		return permanentPhoneNumber;
	}

	@Override
	public void setPermanentPhoneNumber(String phoneNumber) {
		this.permanentPhoneNumber = phoneNumber;
	}

	@Override
	public String getPermanentPostalAddress() {
		return permanentPostalAddress;
	}

	@Override
	public void setPermanentPostalAddress(String postalAddress) {
		this.permanentPostalAddress = postalAddress;
	}

	@Override
	public String getPermanentEmailAddress() {
		return permanentEmailAddress;
	}

	@Override
	public void setPermanentEmailAddress(String email) {
		this.permanentEmailAddress = email;
	}

	@Override
	public String getCurrentDepartment() {
		return currentDepartment;
	}

	@Override
	public void setCurrentDepartment(String department) {
		this.currentDepartment = department;
	}

	@Override
	public String getCurrentCollege() {
		return currentCollege;
	}

	@Override
	public void setCurrentCollege(String college) {
		this.currentCollege = college;
	}

	@Override
	public String getCurrentMajor() {
		return currentMajor;
	}

	@Override
	public void setCurrentMajor(String major) {
		this.currentMajor = major;
	}

	@Override
	public int getCurrentGraduationYear() {
		return currentGraduationYear;
	}

	@Override
	public void setCurrentGraduationYear(int year) {
		this.currentGraduationYear = year;
	}

	@Override
	public Integer getCurrentGraduationMonth() {
		return currentGraduationMonth;
	}

	@Override
	public void setCurrentGraduationMonth(Integer month) {
		if (month != null && ( month > 11 || month < 0)) {
			throw new IllegalArgumentException("Graduation month is out of bounds.");
		}
		
		this.currentGraduationMonth = month;
	}

	@Override
	public Set<Preference> getPreferences() {
		return preferences;
	}

	@Override
	public Preference addPreference(String name, String value) {
		Preference preference = new JpaPreferenceImpl(this, name, value);
		this.preferences.add(preference);
		return preference;
	}
	
	@Override
	public RoleType getRole() {
		return role;
	}

	@Override
	public void setRole(RoleType role) {
		if (role == null )
			throw new IllegalArgumentException("Role is required");
		this.role = role;
	}

}
