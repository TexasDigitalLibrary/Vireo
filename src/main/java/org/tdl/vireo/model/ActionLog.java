package org.tdl.vireo.model;

import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.REFRESH;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * 
 */
@Entity
@Table(name = "actionlog")
public class ActionLog extends BaseEntity {

	@ManyToOne(cascade = { DETACH, REFRESH, MERGE }, optional = false)
	private Submission submission;

	@ManyToOne(cascade = { DETACH, REFRESH, MERGE }, optional = false)
	private SubmissionState submissionState;

	@ManyToOne(cascade = { DETACH, REFRESH, MERGE }, optional = false)
	private User user;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Calendar actionDate;

	//@ManyToOne(cascade = { DETACH, REFRESH, MERGE }, optional = false)
	//private Attachment attachment;

	@Column(nullable = false, length=32768) // 2^15
	private String entry;

	@Column(nullable = false)
	private boolean privateFlag;
	
	public ActionLog(){
		setSubmission(submission);
		
		setUser(user);
		//setAttachment(attachment);
		setActionDate(actionDate);
		
	}
	//TODO - revisit this constructor, Attachment
	
	public ActionLog(Submission submission, SubmissionState submissionState, User user, Calendar actionDate, String entry,boolean privateFlag) {

		// TODO: Check that all the parameters are not null, good, etc...
		//assertReviewerOrOwner(submission.getSubmitter());
		
		// If the person operating is not a persistant person, a mock or
		// otherwise then don't record the link. The person's name might be in
		// the entry text.
		//if (user != null && !user.getClass().isAnnotationPresent(Entity.class))
			//user = null;		
		this.submission = submission;
		this.submissionState = submissionState;
		this.user = user;
		this.actionDate = actionDate;
		//this.attachment = attachment;
		this.entry = entry;
		this.privateFlag = privateFlag;
	}

	/**
	 * @return the submission
	 */
	public Submission getSubmission() {
		return submission;
	}

	/**
	 * @param submission the submission to set
	 */
	public void setSubmission(Submission submission) {
		this.submission = submission;
	}

	/**
	 * @return the submissionState
	 */
	public SubmissionState getSubmissionState() {
		return submissionState;
	}
	/**
	 * @param submissionState the submissionState to set
	 */
	public void setSubmissionState(SubmissionState submissionState) {
		this.submissionState = submissionState;
	}
	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * @return the actionDate
	 */
	public Calendar getActionDate() {
		return actionDate;
	}
	/**
	 * @param actionDate the actionDate to set
	 */
	public void setActionDate(Calendar actionDate) {
		this.actionDate = actionDate;
	}
	/**
	 * @return the entry
	 */
	public String getEntry() {
		return entry;
	}

	/**
	 * @param entry the entry to set
	 */
	public void setEntry(String entry) {
		this.entry = entry;
	}

	/**
	 * @return the privateFlag
	 */
	public boolean isPrivateFlag() {
		return privateFlag;
	}

	/**
	 * @param privateFlag the privateFlag to set
	 */
	public void setPrivateFlag(boolean privateFlag) {
		this.privateFlag = privateFlag;
	}
/*	
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
	public boolean isPrivate() {
		return privateFlag;
	}

	@Override
	public void setPrivate(boolean privateFlag) {
		
		assertReviewerOrOwner(submission.getSubmitter());
		this.privateFlag = privateFlag;
	}*/
}
