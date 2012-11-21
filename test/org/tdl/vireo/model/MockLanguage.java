package org.tdl.vireo.model;

/**
 * This is a simple mock language class that may be useful for testing. Feel free
 * to extend this to add in extra parameters that you feel appropriate.
 * 
 * The basic concept is all properties are public so you can create the mock
 * object and then set whatever relevant properties are needed for your
 * particular test.
 * 
 * @author Micah Cooper
 */
public class MockLanguage extends AbstractMock implements Language {

	/* College Properties */
	public Long id;
	public int displayOrder;
	public String name;

	@Override
	public MockLanguage save() {
		return this;
	}

	@Override
	public MockLanguage delete() {
		return this;
	}

	@Override
	public MockLanguage refresh() {
		return this;
	}

	@Override
	public MockLanguage merge() {
		return this;
	}
	
	@Override
	public MockLanguage detach() {
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
