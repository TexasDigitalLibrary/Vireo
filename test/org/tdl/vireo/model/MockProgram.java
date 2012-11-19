package org.tdl.vireo.model;

/**
 * This is a simple mock program class that may be useful for testing. Feel free
 * to extend this to add in extra parameters that you feel appropriate.
 * 
 * The basic concept is all properties are public so you can create the mock
 * object and then set whatever relevant properties are needed for your
 * particular test.
 * 
 * @author Micah Cooper
 */
public class MockProgram extends AbstractMock implements Program {

	/* Program Properties */
	public Long id;
	public int displayOrder;
	public String name;

	@Override
	public MockProgram save() {
		return this;
	}

	@Override
	public MockProgram delete() {
		return this;
	}

	@Override
	public MockProgram refresh() {
		return this;
	}

	@Override
	public MockProgram merge() {
		return this;
	}
	
	@Override
	public MockProgram detach() {
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
