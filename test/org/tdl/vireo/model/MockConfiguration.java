package org.tdl.vireo.model;

/**
 * This is a simple mock configuration class that may be useful for testing.
 * Feel free to extend this to add in extra parameters that you feel
 * appropriate.
 * 
 * The basic concept is all properties are public so you can create the mock
 * object and then set whatever relevant properties are needed for your
 * particular test.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class MockConfiguration extends AbstractMock implements Configuration {

	/* Configuration Properties */
	public String name;
	public String value;

	@Override
	public MockConfiguration save() {
		return this;
	}

	@Override
	public MockConfiguration delete() {
		return this;
	}

	@Override
	public MockConfiguration refresh() {
		return this;
	}

	@Override
	public MockConfiguration merge() {
		return this;
	}
	
	@Override
	public MockConfiguration detach() {
		return this;
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
	public String getValue() {
		return value;
	}

	@Override
	public void setValue(String value) {
		this.value = value;
	}

}
