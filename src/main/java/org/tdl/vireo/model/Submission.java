package org.tdl.vireo.model;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.CascadeType.REFRESH;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.FetchType.LAZY;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.tdl.vireo.model.validation.SubmissionValidator;

import edu.tamu.framework.model.BaseEntity;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "submitter_id", "organization_id" }))
public class Submission extends BaseEntity {

    @ManyToOne(optional = false)
    private User submitter;

    @ManyToOne(cascade = { REFRESH }, fetch = EAGER, optional = true)
    private User assignee;

    @ManyToOne(cascade = { REFRESH })
    private SubmissionState state;

    @ManyToOne(cascade = { REFRESH }, fetch = EAGER, optional = false)
    private Organization organization;

    @OneToMany(cascade = ALL, fetch = EAGER, orphanRemoval = true)
    private Set<FieldValue> fieldValues;

    @ManyToMany(cascade = { REFRESH }, fetch = EAGER)
    @CollectionTable(uniqueConstraints = @UniqueConstraint(columnNames = { "submission_id", "submission_workflow_steps_id", "submissionWorkflowSteps_order" }))
    @OrderColumn
    private List<SubmissionWorkflowStep> submissionWorkflowSteps;

    @Column(nullable = true)
    @Temporal(TemporalType.DATE)
    private Calendar dateOfGraduation;

    @OneToMany(cascade = ALL, fetch = LAZY, orphanRemoval = true)
    private Set<ActionLog> actionLog;

    @ManyToMany(cascade = { REFRESH }, fetch = LAZY)
    private Set<Embargo> embargoTypes;

    @OneToMany(cascade = ALL, fetch = LAZY, orphanRemoval = true)
    private Set<Attachment> attachments;

    public Submission() {
        setModelValidator(new SubmissionValidator());
        setFieldValues(new HashSet<FieldValue>());
        setSubmissionWorkflowSteps(new ArrayList<SubmissionWorkflowStep>());
        setActionLog(new HashSet<ActionLog>());
        setEmbargoTypes(new HashSet<Embargo>());
        setAttachments(new HashSet<Attachment>());

    }

    /**
     * @param submitter
     * @param state
     */
    public Submission(User submitter, Organization organization) {
        this();
        setSubmitter(submitter);
        setOrganization(organization);
    }

    /**
     * @param submitter
     * @param state
     */
    public Submission(User submitter, Organization organization, SubmissionState state) {
        this(submitter, organization);
        setState(state);
    }

    /**
     * 
     * @return the submitter
     */
    public User getSubmitter() {
        return submitter;
    }

    /**
     * 
     * @param submitter
     */
    public void setSubmitter(User submitter) {
        this.submitter = submitter;
    }

    /**
     * 
     * @return
     */
    public User getAssignee() {
        return assignee;
    }

    /**
     * @param assignee
     *            the assignee to set
     */
    public void setAssignee(User assignee) {
        this.assignee = assignee;
    }

    /**
     * @return the state
     */
    public SubmissionState getState() {
        return state;
    }

    /**
     * @param state
     *            the state to set
     */
    public void setState(SubmissionState state) {
        this.state = state;
    }

    /**
     * @return the organization
     */
    public Organization getOrganization() {
        return organization;
    }

    /**
     * @param organization
     *            the organization to set
     */
    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    /**
     * @return the fieldvalues
     */
    public Set<FieldValue> getFieldValues() {
        return fieldValues;
    }

    /**
     * @param fieldvalues
     *            the fieldvalues to set
     */
    public void setFieldValues(Set<FieldValue> fieldvalues) {
        this.fieldValues = fieldvalues;
    }

    /**
     * 
     * @param fieldValue
     */
    public void addFieldValue(FieldValue fieldValue) {
        getFieldValues().add(fieldValue);
    }

    /**
     * 
     * @param fieldValue
     */
    public FieldValue getFieldValueByValueAndPredicate(String value, FieldPredicate fieldPredicate) {

        FieldValue foundFieldValue = null;

        for (FieldValue fieldValue : getFieldValues()) {
            if (fieldValue.getValue().equals(value) && fieldValue.getFieldPredicate().equals(fieldPredicate)) {
                foundFieldValue = fieldValue;
                break;
            }
        }

        return foundFieldValue;
    }

    /**
     * 
     * @param fieldValue
     */
    public void removeFieldValue(FieldValue fieldValue) {
        getFieldValues().remove(fieldValue);
    }

    /**
     * @return the submissionWorkflowSteps
     */
    public List<SubmissionWorkflowStep> getSubmissionWorkflowSteps() {
        return submissionWorkflowSteps;
    }

    /**
     * @param list
     *            the submissionWorkflowSteps to set
     */
    public void setSubmissionWorkflowSteps(List<SubmissionWorkflowStep> list) {
        this.submissionWorkflowSteps = list;
    }

    /**
     * 
     * @param submissionWorkflowStep
     */
    public void addSubmissionWorkflowStep(SubmissionWorkflowStep submissionWorkflowStep) {
        getSubmissionWorkflowSteps().add(submissionWorkflowStep);
    }

    /**
     * 
     * @param submissionWorkflowStep
     */
    public void removeSubmissionWorkflowStep(SubmissionWorkflowStep submissionWorkflowStep) {
        getSubmissionWorkflowSteps().remove(submissionWorkflowStep);
    }

    /**
     * @return the dateOfGraduation
     */
    public Calendar getDateOfGraduation() {
        return dateOfGraduation;
    }

    /**
     * @param dateOfGraduation
     *            the dateOfGraduation to set
     */
    public void setDateOfGraduation(Calendar dateOfGraduation) {
        this.dateOfGraduation = dateOfGraduation;
    }

    /**
     * @return the actionLog
     */
    public Set<ActionLog> getActionLog() {
        return actionLog;
    }

    /**
     * @param actionLog
     *            the actionLog to set
     */
    public void setActionLog(Set<ActionLog> actionLog) {
        this.actionLog = actionLog;
    }

    /**
     * 
     * @param actionLog
     */
    public void addActionLog(ActionLog actionLog) {
        getActionLog().add(actionLog);
    }

    /**
     * 
     * @param actionLog
     */
    public void removeActionLog(ActionLog actionLog) {
        getActionLog().remove(actionLog);
    }

    /**
     * @return the embargoTypes
     */
    public Set<Embargo> getEmbargoTypes() {
        return embargoTypes;
    }

    /**
     * @param embargoTypes
     *            the embargoTypes to set
     */
    public void setEmbargoTypes(Set<Embargo> embargoType) {
        this.embargoTypes = embargoType;
    }

    /**
     * 
     * @param emabargoType
     */
    public void addEmbargoType(Embargo embargoType) {
        getEmbargoTypes().add(embargoType);
    }

    /**
     * 
     * @param embargoType
     */
    public void removeEmbargoType(Embargo embargoType) {
        getEmbargoTypes().remove(embargoType);
    }

    /**
     * @return the attachments
     */
    public Set<Attachment> getAttachments() {
        return attachments;
    }

    /**
     * @param attachments
     *            the attachments to set
     */
    public void setAttachments(Set<Attachment> attachments) {
        this.attachments = attachments;
    }

    /**
     * 
     * @param attachment
     */
    public void addAttachment(Attachment attachment) {
        getAttachments().add(attachment);
    }

    /**
     * 
     * @param actionLog
     */
    public void removeAttachment(Attachment attachment) {
        getAttachments().remove(attachment);
    }

}
