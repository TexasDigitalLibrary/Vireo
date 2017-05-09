package org.tdl.vireo.model;

import java.util.HashMap;

/**
 * This is a simple mock department class that may be useful for testing. Feel
 * free to extend this to add in extra parameters that you feel appropriate.
 * 
 * The basic concept is all properties are public so you can create the mock
 * object and then set whatever relevant properties are needed for your
 * particular test.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class MockDepartment extends AbstractMock implements Department {

	/* Department Properties */
	public int displayOrder;
	public String name;
	public HashMap<Integer, String> emails = new HashMap<Integer, String>();

	@Override
	public MockDepartment save() {
		return this;
	}

	@Override
	public MockDepartment delete() {
		return this;
	}

	@Override
	public MockDepartment refresh() {
		return this;
	}

	@Override
	public MockDepartment merge() {
		return this;
	}
	
	@Override
	public MockDepartment detach() {
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
	public HashMap<Integer, String> getEmails() {
		return this.emails;
	}

	@Override
	public void setEmails(HashMap emails) {
		this.emails = emails;
	}

	@Override
	public void addEmail(String email) {
		int i = this.emails.size();
		this.emails.put(i, email);
	}

	@Override
	public void removeEmail(int index) {
		this.emails.remove(index);
	}

}
