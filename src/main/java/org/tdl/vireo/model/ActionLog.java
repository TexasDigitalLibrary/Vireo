package org.tdl.vireo.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.tdl.vireo.model.ActionLog;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.Submission;

/**
 * 
 */
@Entity
@Table(name = "actionlog")
public class ActionLog extends BaseEntity {

	@ManyToOne(optional = false)
	private Submission submission;

	@Column(nullable = false)
	private String submissionState;

	@ManyToOne
	private User user;

	@Temporal(TemporalType.TIMESTAMP)
	private Date actionDate;

	@ManyToOne
	private Attachment attachment;

	@Column(nullable = false, length=32768) // 2^15
	private String entry;

	@Column(nullable = false)
	private boolean privateFlag;
	
	public ActionLog(){
		
	}

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
	public ActionLog(Submission submission, SubmissionState submissionState,
			User user, Date actionDate, Attachment attachment, String entry,
			boolean privateFlag) {

		// TODO: Check that all the parameters are not null, good, etc...
		assertReviewerOrOwner(submission.getSubmitter());
		
		// If the person operating is not a persistant person, a mock or
		// otherwise then don't record the link. The person's name might be in
		// the entry text.
		if (user != null && !user.getClass().isAnnotationPresent(Entity.class))
			user = null;
		
		
		this.submission = submission;
		this.submissionState = submissionState.getBeanName();
		this.user = user;
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
