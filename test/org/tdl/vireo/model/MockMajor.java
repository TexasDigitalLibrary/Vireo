package org.tdl.vireo.model;

/**
 * This is a simple mock major class that may be useful for testing. Feel free
 * to extend this to add in extra parameters that you feel appropriate.
 * 
 * The basic concept is all properties are public so you can create the mock
 * object and then set whatever relevant properties are needed for your
 * particular test.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class MockMajor extends AbstractMock implements Major {

	/* Major Properties */
	public int displayOrder;
	public String name;

	@Override
	public MockMajor save() {
		return this;
	}

	@Override
	public MockMajor delete() {
		return this;
	}

	@Override
	public MockMajor refresh() {
		return this;
	}

	@Override
	public MockMajor merge() {
		return this;
	}
	
	@Override
	public MockMajor detach() {
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

}
