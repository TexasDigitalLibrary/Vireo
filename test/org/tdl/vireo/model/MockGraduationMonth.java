package org.tdl.vireo.model;

/**
 * This is a simple mock graduation month class that may be useful for testing.
 * Feel free to extend this to add in extra parameters that you feel
 * appropriate.
 * 
 * The basic concept is all properties are public so you can create the mock
 * object and then set whatever relevant properties are needed for your
 * particular test.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class MockGraduationMonth extends AbstractMock implements GraduationMonth {

	/* Graduation Month Properties */
	public int displayOrder;
	public Integer month;
	public String monthName;

	@Override
	public MockGraduationMonth save() {
		return this;
	}

	@Override
	public MockGraduationMonth delete() {
		return this;
	}

	@Override
	public MockGraduationMonth refresh() {
		return this;
	}

	@Override
	public MockGraduationMonth merge() {
		return this;
	}
	
	@Override
	public MockGraduationMonth detach() {
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
	public int getMonth() {
		return month;
	}

	@Override
	public void setMonth(int month) {
		this.month = month;
	}

	@Override
	public String getMonthName() {
		return monthName;
	}

}
