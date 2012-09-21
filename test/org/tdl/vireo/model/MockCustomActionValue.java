package org.tdl.vireo.model;


/**
 * This is a simple mock custom action value class that may be useful for
 * testing. Feel free to extend this to add in extra parameters that you feel
 * appropriate.
 * 
 * The basic concept is all properties are public so you can create the mock
 * object and then set whatever relevant properties are needed for your
 * particular test.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class MockCustomActionValue extends AbstractMock implements CustomActionValue {

	/* Custom Action Value Properties */
	public Submission submission;
	public CustomActionDefinition definition;
	public boolean value;

	@Override
	public MockCustomActionValue save() {
		return this;
	}

	@Override
	public MockCustomActionValue delete() {
		return this;
	}

	@Override
	public MockCustomActionValue refresh() {
		return this;
	}

	@Override
	public MockCustomActionValue merge() {
		return this;
	}
	
	@Override
	public MockCustomActionValue detach() {
		return this;
	}

	@Override
	public Submission getSubmission() {
		return submission;
	}

	@Override
	public CustomActionDefinition getDefinition() {
		return definition;
	}

	@Override
	public boolean getValue() {
		return value;
	}

	@Override
	public void setValue(boolean value) {
		this.value = value;
	}

}
