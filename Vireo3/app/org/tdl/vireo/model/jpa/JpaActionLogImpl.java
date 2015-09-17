package org.tdl.vireo.model.jpa;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.tdl.vireo.model.ActionLog;
import org.tdl.vireo.model.Attachment;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.state.State;
import org.tdl.vireo.state.StateManager;

import play.modules.spring.Spring;

/**
 * JPA specific implementation of Vireo's Action log.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
@Entity
@Table(name = "actionlog")
public class JpaActionLogImpl extends JpaAbstractModel<JpaActionLogImpl> implements ActionLog {

	@ManyToOne(targetEntity=JpaSubmissionImpl.class, optional = false)
	public Submission submission;

	@Column(nullable = false)
	public String submissionState;

	@ManyToOne(targetEntity=JpaPersonImpl.class)
	public Person person;

	@Temporal(TemporalType.TIMESTAMP)
	public Date actionDate;

	@ManyToOne(targetEntity=JpaAttachmentImpl.class)
	public Attachment attachment;

	@Column(nullable = false, length=32768) // 2^15
	public String entry;

	@Column(nullable = false)
	public boolean privateFlag;

	/**
	 * Create a new JpaActionLogImpl.
	 * 
	 * @param submission
	 *            The submission this action log affects.
	 * @param submissionState
	 *            The state of the submission after the action log.
	 * @param person
	 *            The person who made the change.
	 * @param actionDate
	 *            Date the action occurred.
	 * @param attachment
	 *            The attachment affected, may be null.
	 * @param entry
	 *            An English description of the action.
	 * @param privateFlag
	 *            Weather the action should be published publicly viewable.
	 */
	protected JpaActionLogImpl(Submission submission, State submissionState,
			Person person, Date actionDate, Attachment attachment, String entry,
			boolean privateFlag) {

		// TODO: Check that all the parameters are not null, good, etc...
		assertReviewerOrOwner(submission.getSubmitter());
		
		// If the person operating is not a persistant person, a mock or
		// otherwise then don't record the link. The person's name might be in
		// the entry text.
		if (person != null && !person.getClass().isAnnotationPresent(Entity.class))
			person = null;
		
		
		this.submission = submission;
		this.submissionState = submissionState.getBeanName();
		this.person = person;
		this.actionDate = actionDate;
		this.attachment = attachment;
		this.entry = entry;
		this.privateFlag = privateFlag;
	}

	@Override
	public JpaActionLogImpl save() {

		assertReviewerOrOwner(submission.getSubmitter());
		
		return super.save();
	}

	@Override
	public JpaActionLogImpl delete() {
		// You can not delete action logs... at least not right now.
		throw new IllegalStateException("Action Logs may not be deleted.");
		// return super.delete();
	}
	
	@Override
	public JpaActionLogImpl detach() {
		
		submission.detach();
		if (person != null)
			person.detach();
		if (attachment != null)
			attachment.detach();
		return super.detach();
	}

	@Override
	public Submission getSubmission() {
		return submission;
	}

	@Override
	public State getSubmissionState() {
		return Spring.getBeanOfType(StateManager.class).getState(submissionState);
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
		
		assertReviewerOrOwner(submission.getSubmitter());
		this.privateFlag = privateFlag;
	}
}
