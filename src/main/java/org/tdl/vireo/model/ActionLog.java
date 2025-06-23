package org.tdl.vireo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import java.util.Calendar;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import org.tdl.vireo.model.response.Views;
import org.tdl.vireo.model.validation.ActionLogValidator;

import edu.tamu.weaver.validation.model.ValidatingBaseEntity;

/**
 *
 */
@Entity
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@Table(uniqueConstraints = @UniqueConstraint(
    name = "uk_action_log_unique_columns",
    columnNames = {
        "actionDate",
        "action",
        "entry",
        "privateFlag",
        "submission_status_id",
        "user_id",
        "action_logs_id"
}))
public class ActionLog extends ValidatingBaseEntity {

    @ManyToOne(optional = false)
    private SubmissionStatus submissionStatus;

    @JsonView(Views.SubmissionIndividual.class)
    @ManyToOne(optional = true)
    private User user;

    @JsonView(Views.SubmissionList.class)
    @Column(nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar actionDate;

    @JsonView(Views.SubmissionList.class)
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Action action;

    @JsonView(Views.SubmissionList.class)
    @Column(nullable = false, columnDefinition = "text")
    private String entry;

    @JsonView(Views.SubmissionIndividual.class)
    @Column(nullable = false)
    private boolean privateFlag;

    public ActionLog() {
        setModelValidator(new ActionLogValidator());
    }

    public ActionLog(Action action) {
        this();
        setAction(action);
    }

    public ActionLog(Action action, SubmissionStatus submissionStatus, Calendar actionDate, String entry, boolean privateFlag) {
        this(action);
        setSubmissionStatus(submissionStatus);
        setActionDate(actionDate);
        setEntry(entry);
        setPrivateFlag(privateFlag);
    }

    public ActionLog(Action action, SubmissionStatus submissionStatus, User user, Calendar actionDate, String entry, boolean privateFlag) {
        this(action, submissionStatus, actionDate, entry, privateFlag);
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
     * @return the action
     */
    public Action getAction() {
        return action;
    }

    /**
     * @param action
     *            the action to set
     */
    public void setAction(Action action) {
        this.action = action;
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
