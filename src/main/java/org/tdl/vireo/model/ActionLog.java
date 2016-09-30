package org.tdl.vireo.model;

import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.REFRESH;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.tdl.vireo.model.validation.ActionLogValidator;

import edu.tamu.framework.model.BaseEntity;

/**
 * 
 */
@Entity
public class ActionLog extends BaseEntity {

    @ManyToOne(cascade = { REFRESH, MERGE }, optional = false)
    private Submission submission;

    @ManyToOne(cascade = { REFRESH, MERGE }, optional = false)
    private SubmissionState submissionState;

    @ManyToOne(cascade = { REFRESH, MERGE }, optional = false)
    private User user;

    @Temporal(TemporalType.TIMESTAMP)
    private Calendar actionDate;

    @ManyToOne(cascade = { REFRESH, MERGE }, optional = true)
    private Attachment attachment;

    @Column(nullable = false, columnDefinition = "text")
    private String entry;

    @Column(nullable = false)
    private boolean privateFlag;

    public ActionLog() {
        setModelValidator(new ActionLogValidator());
    }

    public ActionLog(Submission submission, SubmissionState submissionState, User user, Calendar actionDate, Attachment attachment, String entry, boolean privateFlag) {
        this();
        this.submission = submission;
        this.submissionState = submissionState;
        this.user = user;
        this.actionDate = actionDate;
        this.attachment = attachment;
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
     * @param submission
     *            the submission to set
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
     * @param submissionState
     *            the submissionState to set
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
     * @param user
     *            the user to set
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
     * @param actionDate
     *            the actionDate to set
     */
    public void setActionDate(Calendar actionDate) {
        this.actionDate = actionDate;
    }

    /**
     * @return the attachment
     */
    public Attachment getAttachment() {
        return attachment;
    }

    /**
     * @param attachment
     *            the attachment to set
     */
    public void setAttachment(Attachment attachment) {
        this.attachment = attachment;
    }

    /**
     * @return the entry
     */
    public String getEntry() {
        return entry;
    }

    /**
     * @param entry
     *            the entry to set
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
     * @param privateFlag
     *            the privateFlag to set
     */
    public void setPrivateFlag(boolean privateFlag) {
        this.privateFlag = privateFlag;
    }

}
