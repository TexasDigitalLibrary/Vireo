package org.tdl.vireo.model;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.tdl.vireo.model.validation.ActionLogValidator;

import edu.tamu.weaver.validation.model.ValidatingBaseEntity;

/**
 *
 */
@Entity
public class ActionLog extends ValidatingBaseEntity {

    @ManyToOne(optional = false)
    private SubmissionStatus submissionStatus;

    @ManyToOne(optional = true)
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

    public ActionLog(SubmissionStatus submissionStatus, Calendar actionDate, String entry, boolean privateFlag) {
        this();
        setSubmissionStatus(submissionStatus);
        setActionDate(actionDate);
        setEntry(entry);
        setPrivateFlag(privateFlag);
    }

    public ActionLog(SubmissionStatus submissionStatus, User user, Calendar actionDate, String entry, boolean privateFlag) {
        this(submissionStatus, actionDate, entry, privateFlag);
        setUser(user);
    }

    /**
     * @return the submissionStatus
     */
    public SubmissionStatus getSubmissionStatus() {
        return submissionStatus;
    }

    /**
     * @param submissionStatus
     *            the submissionStatus to set
     */
    public void setSubmissionStatus(SubmissionStatus submissionStatus) {
        this.submissionStatus = submissionStatus;
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
