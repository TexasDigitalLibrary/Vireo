package org.tdl.vireo.model;

/**
 * This is a simple mock committee member role type class that may be usefull
 * for testing. Feel free to extend this to add in extra parameters that you
 * feel appropriate.
 * 
 * The basic concept is all properties are public so you can create the mock
 * object and then set whatever relevant properties are needed for your
 * particular test.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class MockCommitteeMemberRoleType extends AbstractMock implements CommitteeMemberRoleType {

	/* Role Type Properties */
	public int displayOrder;
	public String name;
	public DegreeLevel level;

	@Override
	public MockCommitteeMemberRoleType save() {
		return this;
	}

	@Override
	public MockCommitteeMemberRoleType delete() {
		return this;
	}

	@Override
	public MockCommitteeMemberRoleType refresh() {
		return this;
	}

	@Override
	public MockCommitteeMemberRoleType merge() {
		return this;
	}
	
	@Override
	public MockCommitteeMemberRoleType detach() {
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
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public DegreeLevel getLevel() {
		return level;
	}

	@Override
	public void setLevel(DegreeLevel level) {
		this.level = level;
	}

}
