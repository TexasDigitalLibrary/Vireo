package org.tdl.vireo.model;

import static javax.persistence.CascadeType.DETACH;
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

    @ManyToOne(cascade = { DETACH, REFRESH, MERGE }, optional = false)
    private SubmissionStatus submissionState;

    @ManyToOne(cascade = { DETACH, REFRESH, MERGE }, optional = true)
    private User user;

    @Column(nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar actionDate;

    @Column(nullable = false, columnDefinition = "text")
    private String entry;

    @Column(nullable = false)
    private boolean privateFlag;

    public ActionLog() {
        setModelValidator(new ActionLogValidator());
    }

    public ActionLog(SubmissionStatus submissionState, User user, Calendar actionDate, String entry, boolean privateFlag) {
        this();
        this.submissionState = submissionState;
        this.user = user;
        this.actionDate = actionDate;
        this.entry = entry;
        this.privateFlag = privateFlag;
    }

    public ActionLog(SubmissionStatus submissionState, Calendar actionDate, String entry, boolean privateFlag) {
        this();
        this.submissionState = submissionState;
        this.actionDate = actionDate;
        this.entry = entry;
        this.privateFlag = privateFlag;
    }

    /**
     * @return the submissionState
     */
    public SubmissionStatus getSubmissionState() {
        return submissionState;
    }

    /**
     * @param submissionState
     *            the submissionState to set
     */
    public void setSubmissionState(SubmissionStatus submissionState) {
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
