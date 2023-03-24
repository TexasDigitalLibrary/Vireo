package org.tdl.vireo.model.simple;

import java.io.Serializable;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import org.hibernate.annotations.Immutable;
import org.tdl.vireo.model.ActionLog;
import org.tdl.vireo.model.CustomActionValue;
import org.tdl.vireo.model.FieldValue;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.SubmissionWorkflowStep;

@Entity
@Immutable
@Table(name = "submission")
public class SimpleSubmission implements Serializable {

    @Transient
    private static final long serialVersionUID = 9085465328046307313L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", insertable = false, updatable = false, nullable = false)
    private Long id;

    @Immutable
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private SimpleUser submitter;

    @Immutable
    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    private SimpleUser assignee;

    @Immutable
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private SimpleSubmissionStatus submissionStatus;

    @Immutable
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private SimpleOrganization organization;

    @Transient
    private Set<SimpleFieldValue> fieldValues;

    @Transient
    private List<SubmissionWorkflowStep> submissionWorkflowSteps;

    @Column(insertable = false, updatable = false, nullable = true)
    @Temporal(TemporalType.DATE)
    private Calendar approveEmbargoDate;

    @Column(insertable = false, updatable = false, nullable = true)
    @Temporal(TemporalType.DATE)
    private Calendar approveApplicationDate;

    @Column(insertable = false, updatable = false, nullable = true)
    @Temporal(TemporalType.DATE)
    private Calendar submissionDate;

    @Column(insertable = false, updatable = false, nullable = true)
    @Temporal(TemporalType.DATE)
    private Calendar approveAdvisorDate;

    @Column(insertable = false, updatable = false, nullable = true)
    private boolean approveEmbargo;

    @Column(insertable = false, updatable = false, nullable = true)
    private boolean approveApplication;

    @Column(insertable = false, updatable = false, nullable = true)
    private boolean approveAdvisor;

    @Transient
    private Set<CustomActionValue> customActionValues;

    @Transient
    private Set<ActionLog> actionLogs;

    @Column(insertable = false, updatable = false, columnDefinition = "TEXT")
    private String reviewerNotes;

    @Column(insertable = false, updatable = false, nullable = true)
    private String advisorAccessHash;

    @Column(insertable = false, updatable = false, nullable = true)
    private String advisorReviewURL;

    @Column(insertable = false, updatable = false, nullable = true)
    private String depositURL;

    @Transient
    private ActionLog lastAction;

    public static Submission toSubmission(SimpleSubmission simpleSubmission) {
        if (simpleSubmission == null) {
            return null;
        }

        Submission submission = new Submission();

        submission.setId(simpleSubmission.getId());
        submission.setSubmitter(SimpleUser.toUser(simpleSubmission.getSubmitter()));
        submission.setAssignee(SimpleUser.toUser(simpleSubmission.getAssignee()));
        submission.setSubmissionStatus(SimpleSubmissionStatus.toSubmissionStatus(simpleSubmission.getSubmissionStatus()));
        submission.setOrganization(SimpleOrganization.toOrganization(simpleSubmission.getOrganization()));
        submission.setSubmissionWorkflowSteps(simpleSubmission.getSubmissionWorkflowSteps());
        submission.setApproveEmbargoDate(simpleSubmission.getApproveEmbargoDate());
        submission.setApproveApplicationDate(simpleSubmission.getApproveApplicationDate());
        submission.setSubmissionDate(simpleSubmission.getSubmissionDate());
        submission.setApproveAdvisorDate(simpleSubmission.getApproveAdvisorDate());
        submission.setApproveEmbargo(simpleSubmission.isApproveEmbargo());
        submission.setApproveApplication(simpleSubmission.isApproveApplication());
        submission.setApproveAdvisor(simpleSubmission.isApproveAdvisor());
        submission.setCustomActionValues(simpleSubmission.getCustomActionValues());
        submission.setActionLogs(simpleSubmission.getActionLogs());
        submission.setReviewerNotes(simpleSubmission.getReviewerNotes());
        submission.setAdvisorAccessHash(simpleSubmission.getAdvisorAccessHash());
        submission.setAdvisorReviewURL(simpleSubmission.getAdvisorReviewURL());
        submission.setDepositURL(simpleSubmission.getDepositURL());
        submission.setLastAction(simpleSubmission.getLastAction());

        if (simpleSubmission.getFieldValues() == null) {
            submission.setFieldValues(null);
        } else {
            Set<FieldValue> fv = new HashSet<>();

            simpleSubmission.getFieldValues().forEach(fieldValue -> {
                fv.add(SimpleFieldValue.toFieldValue(fieldValue));
            });

            submission.setFieldValues(fv);
        }

        return submission;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SimpleUser getSubmitter() {
        return submitter;
    }

    public void setSubmitter(SimpleUser submitter) {
        this.submitter = submitter;
    }

    public SimpleUser getAssignee() {
        return assignee;
    }

    public void setAssignee(SimpleUser assignee) {
        this.assignee = assignee;
    }

    public SimpleSubmissionStatus getSubmissionStatus() {
        return submissionStatus;
    }

    public void setSubmissionStatus(SimpleSubmissionStatus submissionStatus) {
        this.submissionStatus = submissionStatus;
    }

    public SimpleOrganization getOrganization() {
        return organization;
    }

    public void setOrganization(SimpleOrganization organization) {
        this.organization = organization;
    }

    public Set<SimpleFieldValue> getFieldValues() {
        return fieldValues;
    }

    public void setFieldValues(Set<SimpleFieldValue> fieldValues) {
        this.fieldValues = fieldValues;
    }

    public List<SubmissionWorkflowStep> getSubmissionWorkflowSteps() {
        return submissionWorkflowSteps;
    }

    public void setSubmissionWorkflowSteps(List<SubmissionWorkflowStep> submissionWorkflowSteps) {
        this.submissionWorkflowSteps = submissionWorkflowSteps;
    }

    public Calendar getApproveEmbargoDate() {
        return approveEmbargoDate;
    }

    public void setApproveEmbargoDate(Calendar approveEmbargoDate) {
        this.approveEmbargoDate = approveEmbargoDate;
    }

    public Calendar getApproveApplicationDate() {
        return approveApplicationDate;
    }

    public void setApproveApplicationDate(Calendar approveApplicationDate) {
        this.approveApplicationDate = approveApplicationDate;
    }

    public Calendar getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(Calendar submissionDate) {
        this.submissionDate = submissionDate;
    }

    public Calendar getApproveAdvisorDate() {
        return approveAdvisorDate;
    }

    public void setApproveAdvisorDate(Calendar approveAdvisorDate) {
        this.approveAdvisorDate = approveAdvisorDate;
    }

    public boolean isApproveEmbargo() {
        return approveEmbargo;
    }

    public void setApproveEmbargo(boolean approveEmbargo) {
        this.approveEmbargo = approveEmbargo;
    }

    public boolean isApproveApplication() {
        return approveApplication;
    }

    public void setApproveApplication(boolean approveApplication) {
        this.approveApplication = approveApplication;
    }

    public boolean isApproveAdvisor() {
        return approveAdvisor;
    }

    public void setApproveAdvisor(boolean approveAdvisor) {
        this.approveAdvisor = approveAdvisor;
    }

    public Set<CustomActionValue> getCustomActionValues() {
        return customActionValues;
    }

    public void setCustomActionValues(Set<CustomActionValue> customActionValues) {
        this.customActionValues = customActionValues;
    }

    public Set<ActionLog> getActionLogs() {
        return actionLogs;
    }

    public void setActionLogs(Set<ActionLog> actionLogs) {
        this.actionLogs = actionLogs;
    }

    /**
     * Given a list, set (or unset) both the logs and the last action.
     * 
     * The last action is populated from the first index under the assumption
     * that the order is descending.
     *
     * @param actionLogs The array of action logs.
     */
    public void setActionLogs(List<ActionLog> actionLogs) {
        if (actionLogs == null) {
            this.actionLogs = null;
            this.lastAction = null;
        } else {
            this.actionLogs = new HashSet<>(actionLogs);
            this.lastAction = actionLogs.get(0);
        }
    }

    public String getReviewerNotes() {
        return reviewerNotes;
    }

    public void setReviewerNotes(String reviewerNotes) {
        this.reviewerNotes = reviewerNotes;
    }

    public String getAdvisorAccessHash() {
        return advisorAccessHash;
    }

    public void setAdvisorAccessHash(String advisorAccessHash) {
        this.advisorAccessHash = advisorAccessHash;
    }

    public String getAdvisorReviewURL() {
        return advisorReviewURL;
    }

    public void setAdvisorReviewURL(String advisorReviewURL) {
        this.advisorReviewURL = advisorReviewURL;
    }

    public String getDepositURL() {
        return depositURL;
    }

    public void setDepositURL(String depositURL) {
        this.depositURL = depositURL;
    }

    public ActionLog getLastAction() {
        return lastAction;
    }

    public void setLastAction(ActionLog lastAction) {
        this.lastAction = lastAction;
    }

}
