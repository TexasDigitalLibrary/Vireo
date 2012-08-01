package org.tdl.vireo.model;

/**
 * This is a simple mock document type class that may be useful for testing.
 * Feel free to extend this to add in extra parameters that you feel
 * appropriate.
 * 
 * The basic concept is all properties are public so you can create the mock
 * object and then set whatever relevant properties are needed for your
 * particular test.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class MockDocumentType extends AbstractMock implements DocumentType {

	/* Document Type Properties */
	public int displayOrder;
	public String name;
	public DegreeLevel level;

	@Override
	public MockDocumentType save() {
		return this;
	}

	@Override
	public MockDocumentType delete() {
		return this;
	}

	@Override
	public MockDocumentType refresh() {
		return this;
	}

	@Override
	public MockDocumentType merge() {
		return this;
	}
	
	@Override
	public MockDocumentType detach() {
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
