package org.tdl.vireo.model;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.CascadeType.REFRESH;
import static javax.persistence.FetchType.EAGER;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdl.vireo.AppContextInitializedHandler;
import org.tdl.vireo.model.validation.SubmissionValidator;

import edu.tamu.framework.model.BaseEntity;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "submitter_id", "organization_id" }))
public class Submission extends BaseEntity {

    final static Logger logger = LoggerFactory.getLogger(AppContextInitializedHandler.class);

    @ManyToOne(optional = false)
    private User submitter;

    @ManyToOne(cascade = { REFRESH }, fetch = EAGER, optional = true)
    private User assignee;

    @ManyToOne(cascade = { REFRESH })
    private SubmissionState submissionState;

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
    private Calendar submissionDate;

    @ManyToMany(cascade = { REFRESH }, fetch = EAGER)
    private Set<Embargo> embargoTypes;

    @OneToMany(cascade = ALL, fetch = EAGER, orphanRemoval = true)
    private List<CustomActionValue> customActionValues;

    @JoinColumn
    @OneToMany(cascade = ALL, fetch = EAGER, orphanRemoval = true)
    private List<ActionLog> actionLogs;

    @Lob
    private String reviewerNotes;

    @Column(nullable = true)
    private String advisorAccessHash;

    public Submission() {
        setModelValidator(new SubmissionValidator());
        setFieldValues(new HashSet<FieldValue>());
        setSubmissionWorkflowSteps(new ArrayList<SubmissionWorkflowStep>());
        setActionLogs(new ArrayList<ActionLog>());
        setEmbargoTypes(new HashSet<Embargo>());
        setCustomActionValues(new ArrayList<CustomActionValue>());
    }

    /**
     * @param submitter
     * @param submissionState
     */
    public Submission(User submitter, Organization organization) {
        this();
        setSubmitter(submitter);
        setOrganization(organization);
        generateAdvisorAccessHash();
    }

    /**
     * @param submitter
     * @param submissionState
     */
    public Submission(User submitter, Organization organization, SubmissionState submissionState) {
        this(submitter, organization);
        setSubmissionState(submissionState);
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

        if (submissionState.getName().equals("Submitted")) {
            setSubmissionDate(getTime());
        }

        if (this.submissionState != null) {
            logger.info("Changing status from " + this.submissionState.getName() + " to " + submissionState.getName());
        } else {
            logger.info("Changing status to " + submissionState.getName());
        }

        this.submissionState = submissionState;

    }

    private Calendar getTime() {
        Calendar time = Calendar.getInstance();
        time.clear(Calendar.HOUR);
        time.clear(Calendar.MINUTE);
        time.clear(Calendar.SECOND);
        return time;
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

    public List<FieldValue> getFieldValuesByPredicate(FieldPredicate predicate) {

        List<FieldValue> fielsValues = new ArrayList<FieldValue>();

        this.getFieldValues().forEach(fv -> {
            if (predicate.equals(fv.getFieldPredicate())) {
                fielsValues.add(fv);
            }
        });

        return fielsValues;
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

    public List<FieldValue> getFieldValueByPredicate(FieldPredicate fieldPredicate) {

        List<FieldValue> foundFieldValues = new ArrayList<FieldValue>();

        for (FieldValue fieldValue : getFieldValues()) {
            if (fieldValue.getFieldPredicate().equals(fieldPredicate)) {
                foundFieldValues.add(fieldValue);
            }
        }

        return foundFieldValues;

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
     * @return the submissionDate
     */
    public Calendar getSubmissionDate() {
        return submissionDate;
    }

    /**
     * @param submissionDate
     *            the submissionDate to set
     */
    public void setSubmissionDate(Calendar submissionDate) {
        this.submissionDate = submissionDate;
    }

    /**
     * @return the actionLog
     */
    public List<ActionLog> getActionLogs() {
        return actionLogs;
    }

    /**
     * @param actionLog
     *            the actionLog to set
     */
    public void setActionLogs(List<ActionLog> actionLogs) {
        this.actionLogs = actionLogs;
    }

    /**
     *
     * @param actionLog
     */
    public void addActionLog(ActionLog actionLog) {
        getActionLogs().add(actionLog);
    }

    /**
     *
     * @param actionLog
     */
    public void removeActionLog(ActionLog actionLog) {
        getActionLogs().remove(actionLog);
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
     *
     * @return
     */
    public String getReviewerNotes() {
        return reviewerNotes;
    }

    /**
     *
     * @param reviewerNotes
     */
    public void setReviewerNotes(String reviewerNotes) {
        this.reviewerNotes = reviewerNotes;
    }

    private void generateAdvisorAccessHash() {
        setAdvisorAccessHash(UUID.randomUUID().toString().replace("-", ""));
    }

    public void setAdvisorAccessHash(String string) {
        advisorAccessHash = string;
    }

    public String getAdvisorAccessHash() {
        return advisorAccessHash;
    }

    /**
     * @return the customActionValues
     */
    public List<CustomActionValue> getCustomActionValues() {
        return customActionValues;
    }

    /**
     * @param customActionValues
     *            the customActionValues to set
     */
    public void setCustomActionValues(List<CustomActionValue> customActionValues) {
        this.customActionValues = customActionValues;
    }

    public void addCustomActionValue(CustomActionValue customActionValue) {
        this.customActionValues.add(customActionValue);
    }

    /**
     *
     * @param customActionValue
     * @return
     */
    public CustomActionValue editCustomActionValue(CustomActionValue customActionValue) {
        for (CustomActionValue cav : this.customActionValues) {
            if (cav.getId().equals(customActionValue.getId())) {
                cav.setDefinition(customActionValue.getDefinition());
                cav.setValue(customActionValue.getValue());
                return cav;
            }
        }
        this.customActionValues.add(customActionValue);
        return customActionValue;
    }

    public List<FieldValue> getFieldValuesByInputType(InputType inputType) {

        List<FieldValue> fieldValues = new ArrayList<FieldValue>();

        this.submissionWorkflowSteps.forEach(submissionWorkflowSteps -> {
            submissionWorkflowSteps.getAggregateFieldProfiles().forEach(afp -> {
                if (afp.getInputType().equals(inputType)) {
                    List<FieldValue> foundFieldValues = this.getFieldValuesByPredicate(afp.getFieldPredicate());
                    fieldValues.addAll(foundFieldValues);
                }
            });
        });

        return fieldValues;
    }
}
