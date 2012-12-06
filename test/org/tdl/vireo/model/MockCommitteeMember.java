package org.tdl.vireo.model;

import java.util.ArrayList;
import java.util.List;


/**
 * This is a simple mock committee member class that may be useful for testing.
 * Feel free to extend this to add in extra parameters that you feel
 * appropriate.
 * 
 * The basic concept is all properties are public so you can create the mock
 * object and then set whatever relevant properties are needed for your
 * particular test.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class MockCommitteeMember extends AbstractMock implements CommitteeMember {

	/* Committee Member Properties */
	public int displayOrder;
	public Submission submission;
	public String firstName;
	public String lastName;
	public String middleName;
	public List<String> roles = new ArrayList<String>();
	
	@Deprecated
	public boolean chair;

	@Override
	public MockCommitteeMember save() {
		return this;
	}

	@Override
	public MockCommitteeMember delete() {
		return this;
	}

	@Override
	public MockCommitteeMember refresh() {
		return this;
	}

	@Override
	public MockCommitteeMember merge() {
		return this;
	}
	
	@Override
	public MockCommitteeMember detach() {
		return this;
	}

	@Override
	public int getDisplayOrder() {
		return displayOrder;
	}

	@Override
	public void setDisplayOrder(int displayOrder) {
		this.displayOrder = displayOrder;
	}

	@Override
	public Submission getSubmission() {
		return submission;
	}

	@Override
	public String getFirstName() {
		return firstName;
	}

	@Override
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@Override
	public String getLastName() {
		return lastName;
	}

	@Override
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Override
	public String getMiddleName() {
		return middleName;
	}

	@Override
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}
	
	@Override
	public List<String> getRoles() {
		return roles;
	}

	@Override
	public void addRole(String role) {
		this.roles.add(role);
	}

	@Override
	public void removeRole(String role) {
		this.roles.remove(role);
	}
	
	@Override
	public boolean hasRole(String ... roles) {
		
		for (String role : roles) {
			if (this.roles.contains(role))
				return true;
		}
		return false;
	}
	
	@Override
	public boolean hasNoRole() {
		return this.roles.size() == 0;
	}
	
	@Override
	public String getFormattedRoles() {
		String result = "";
		for (String role : this.roles) {
			if (result.length() != 0)
				result += ", ";
			result += role;
		}
		return result;
	}
	
	@Override
	public String getFormattedName(NameFormat format) {
		return NameFormat.format(format, firstName, middleName, lastName, null);
	}

}
