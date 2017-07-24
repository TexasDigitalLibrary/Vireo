package org.tdl.vireo.model;

import java.util.Date;

import org.tdl.vireo.state.State;

/**
 * This is a simple mock action log class that may be useful for testing. Feel
 * free to extend this to add in extra parameters that you feel appropriate.
 * 
 * The basic concept is all properties are public so you can create the mock
 * object and then set whatever relevant properties are needed for your
 * particular test.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class MockActionLog extends AbstractMock implements ActionLog {

	/* Action Log Parameters */
	public Submission submission;
	public State submissionState;
	public Person person;
	public Date actionDate;
	public Attachment attachment;
	public String entry;
	public boolean privateFlag;

	@Override
	public MockActionLog save() {
		return this;
	}

	@Override
	public MockActionLog delete() {
		return this;
	}

	@Override
	public MockActionLog refresh() {
		return this;
	}

	@Override
	public MockActionLog merge() {
		return this;
	}
	
	@Override
	public MockActionLog detach() {
		return this;
	}

	@Override
	public Submission getSubmission() {
		return submission;
	}

	@Override
	public State getSubmissionState() {
		return submissionState;
	}

	@Override
	public Person getPerson() {
		return person;
	}

	@Override
	public Date getActionDate() {
		return actionDate;
	}

	@Override
	public Attachment getAttachment() {
		return attachment;
	}

	@Override
	public String getEntry() {
		return entry;
	}

	@Override
	public boolean isPrivate() {
		return privateFlag;
	}

	@Override
	public void setPrivate(boolean privateFlag) {
		this.privateFlag = privateFlag;
	}

}
