package org.tdl.vireo.model;

/**
 * This is a simple mock college class that may be useful for testing. Feel free
 * to extend this to add in extra parameters that you feel appropriate.
 * 
 * The basic concept is all properties are public so you can create the mock
 * object and then set whatever relevant properties are needed for your
 * particular test.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class MockCollege extends AbstractMock implements College {

	/* College Properties */
	public Long id;
	public int displayOrder;
	public String name;

	@Override
	public MockCollege save() {
		return this;
	}

	@Override
	public MockCollege delete() {
		return this;
	}

	@Override
	public MockCollege refresh() {
		return this;
	}

	@Override
	public MockCollege merge() {
		return this;
	}
	
	@Override
	public MockCollege detach() {
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
