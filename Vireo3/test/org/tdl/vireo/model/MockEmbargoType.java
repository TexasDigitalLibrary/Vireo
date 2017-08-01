package org.tdl.vireo.model;

import java.util.List;


/**
 * This is a simple mock embargo type class that may be useful for testing. Feel
 * free to extend this to add in extra parameters that you feel appropriate.
 * 
 * The basic concept is all properties are public so you can create the mock
 * object and then set whatever relevant properties are needed for your
 * particular test.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class MockEmbargoType extends AbstractMock implements EmbargoType {

	/* Embargo Type Properties */
	public int displayOrder;
	public String name;
	public String description;
	public Integer duration;
	public boolean active;
	public boolean systemRequired;
	public EmbargoGuarantor guarantor = EmbargoGuarantor.DEFAULT;
	public List<Submission> submissions;

	@Override
	public MockEmbargoType save() {
		return this;
	}

	@Override
	public MockEmbargoType delete() {
		return this;
	}

	@Override
	public MockEmbargoType refresh() {
		return this;
	}

	@Override
	public MockEmbargoType merge() {
		return this;
	}
	
	@Override
	public MockEmbargoType detach() {
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
	public String getDescription() {
		return description;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public Integer getDuration() {
		return duration;
	}

	@Override
	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	@Override
	public boolean isActive() {
		return active;
	}

	@Override
	public void setActive(boolean active) {
		this.active = active;
	}
	
	@Override
    public boolean isSystemRequired() {
		return systemRequired;
    }

	@Override
    public void setSystemRequired(boolean required) {
	    this.systemRequired = required;
    }
	
	@Override
	public EmbargoGuarantor getGuarantor() {
		return guarantor;
	}
	
	@Override
	public void setGuarantor(EmbargoGuarantor guarantor) {
		this.guarantor = guarantor;
	}
	
	@Override
	public List<Submission> getSubmissions() {
		return submissions;
	}
}
