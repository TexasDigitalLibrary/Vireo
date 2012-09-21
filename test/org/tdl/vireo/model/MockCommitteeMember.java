package org.tdl.vireo.model;


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
	public String getFormattedName(NameFormat format) {
		return NameFormat.format(format, firstName, middleName, lastName, null);
	}

	@Override
	public boolean isCommitteeChair() {
		return chair;
	}

	@Override
	public void setCommitteeChair(boolean chair) {
		this.chair = chair;
	}

}
