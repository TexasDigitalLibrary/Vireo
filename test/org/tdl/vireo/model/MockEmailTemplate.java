package org.tdl.vireo.model;

/**
 * This is a simple mock email template class that may be useful for testing.
 * Feel free to extend this to add in extra parameters that you feel
 * appropriate.
 * 
 * The basic concept is all properties are public so you can create the mock
 * object and then set whatever relevant properties are needed for your
 * particular test.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class MockEmailTemplate extends AbstractMock implements EmailTemplate {

	/* Email Template Properties */
	public int displayOrder;
	public String name;
	public String subject;
	public String message;
	public boolean required;

	@Override
	public MockEmailTemplate save() {
		return this;
	}

	@Override
	public MockEmailTemplate delete() {
		return this;
	}

	@Override
	public MockEmailTemplate refresh() {
		return this;
	}

	@Override
	public MockEmailTemplate merge() {
		return this;
	}
	
	@Override
	public MockEmailTemplate detach() {
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
	public String getSubject() {
		return subject;
	}

	@Override
	public void setSubject(String subject) {
		this.subject = subject;
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public void setMessage(String message) {
		this.message = message;
	}
	
	@Override
	public boolean isSystemRequired() {
		return required;
	}
	
	@Override
	public void setSystemRequired(boolean required) {
		this.required = required;
	}

}
