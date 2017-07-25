package org.tdl.vireo.model;

/**
 * This is a simple mock custom action definition class that may be useful for testing. Feel free to extend this to add in extra parameters that you feel appropriate.
 * 
 * The basic concept is all properties are public so you can create the mock object and then set whatever relevant properties are needed for your particular test.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class MockCustomActionDefinition extends AbstractMock implements CustomActionDefinition {

	/* Custom Action Definition Properties */
	public int displayOrder;
	public String label;
	private Boolean isStudentVisible;

	@Override
	public MockCustomActionDefinition save() {
		return this;
	}

	@Override
	public MockCustomActionDefinition delete() {
		return this;
	}

	@Override
	public MockCustomActionDefinition refresh() {
		return this;
	}

	@Override
	public MockCustomActionDefinition merge() {
		return this;
	}

	@Override
	public MockCustomActionDefinition detach() {
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
	public String getLabel() {
		return label;
	}

	@Override
	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public Boolean isStudentVisible() {
		return isStudentVisible;
	}

	@Override
	public void setIsStudentVisible(Boolean isStudentVisible) {
		this.isStudentVisible = isStudentVisible;
	}

}
