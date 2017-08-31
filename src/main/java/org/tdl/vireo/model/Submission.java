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
import java.util.UUID;

import javax.annotation.PostConstruct;
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
import org.springframework.beans.factory.annotation.Value;
import org.tdl.vireo.AppContextInitializedHandler;
import org.tdl.vireo.enums.SubmissionState;
import org.tdl.vireo.model.validation.SubmissionValidator;

import edu.tamu.framework.model.BaseEntity;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "submitter_id", "organization_id" }))
public class Submission extends BaseEntity {
    
    @Value("${app.url}")
    private String url;

    final static Logger logger = LoggerFactory.getLogger(AppContextInitializedHandler.class);

    @ManyToOne(optional = false)
    private User submitter;

    @ManyToOne(cascade = { REFRESH }, fetch = EAGER, optional = true)
    private User assignee;

    @ManyToOne(cascade = { REFRESH })
    private SubmissionStatus submissionStatus;

    @ManyToOne(cascade = { REFRESH }, fetch = EAGER, optional = false)
    private Organization organization;

    @OneToMany(cascade = ALL, fetch = LAZY, orphanRemoval = true)
    private Set<FieldValue> fieldValues;

    @ManyToMany(cascade = { REFRESH }, fetch = EAGER)
    @CollectionTable(uniqueConstraints = @UniqueConstraint(columnNames = { "submission_id", "submission_workflow_steps_id", "submissionWorkflowSteps_order" }))
    @OrderColumn
    private List<SubmissionWorkflowStep> submissionWorkflowSteps;

    @Column(nullable = true)
    @Temporal(TemporalType.DATE)
    private Calendar approveEmbargoDate;

    @Column(nullable = true)
    @Temporal(TemporalType.DATE)
    private Calendar submissionDate;

    @Column(nullable = true)
    @Temporal(TemporalType.DATE)
    private Calendar approveApplicationDate;

    @Column(nullable = true)
    private boolean approveEmbargo;

    @Column(nullable = true)
    private boolean approveApplication;

    @ManyToMany(cascade = { REFRESH }, fetch = LAZY)
    private Set<Embargo> embargoTypes;

    @OneToMany(cascade = ALL, fetch = LAZY, orphanRemoval = true)
    private List<CustomActionValue> customActionValues;

    @JoinColumn
    @OneToMany(cascade = ALL, fetch = LAZY, orphanRemoval = true)
    private List<ActionLog> actionLogs;

    @Lob
    private String reviewerNotes;

    @Column(nullable = true)
    private String advisorAccessHash;
    
    @Column(nullable = true)
    private String advisorReviewURL;
    
    @Column(nullable = true)
    private String depositUri;

    public Submission() {
        setModelValidator(new SubmissionValidator());
        setFieldValues(new HashSet<FieldValue>());
        setSubmissionWorkflowSteps(new ArrayList<SubmissionWorkflowStep>());
        setActionLogs(new ArrayList<ActionLog>());
        setEmbargoTypes(new HashSet<Embargo>());
        setApproveApplication(false);
        setApproveEmbargo(false);
        setCustomActionValues(new ArrayList<CustomActionValue>());
    }


    /**
     * @param submitter
     * @param submissionStatus
     */
    public Submission(User submitter, Organization organization) {
        this();
        setSubmitter(submitter);
        setOrganization(organization);
        generateAdvisorAccessHash();
    }

    /**
     * @param submitter
     * @param submissionStatus
     */
    public Submission(User submitter, Organization organization, SubmissionStatus submissionStatus) {
        this(submitter, organization);
        setSubmissionStatus(submissionStatus);
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

        if (submissionStatus.getSubmissionState() == SubmissionState.SUBMITTED) {
            setSubmissionDate(getDay());
        }

        if (this.submissionStatus != null) {
            logger.debug("Changing status of submission " + getId() + " from " + this.submissionStatus.getName() + " to " + submissionStatus.getName());
        } else {
            logger.debug("Changing status of submission "  + getId() + "to " + submissionStatus.getName());
        }

        this.submissionStatus = submissionStatus;

    }

    private Calendar getTime() {
        return Calendar.getInstance();
    }

    private Calendar getDay() {
        Calendar day = getTime();
// TODO: Confirm that this is not needed.
//        day.clear(Calendar.HOUR);
//        day.clear(Calendar.MINUTE);
//        day.clear(Calendar.SECOND);
        return day;
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

    public List<FieldValue> getFieldValuesByPredicate(FieldPredicate fieldPredicate) {
        List<FieldValue> fielsValues = new ArrayList<FieldValue>();
        getFieldValues().forEach(fieldValue -> {
            if (fieldValue.getFieldPredicate().equals(fieldPredicate)) {
                fielsValues.add(fieldValue);
            }
        });
        return fielsValues;
    }

    public List<FieldValue> getFieldValuesByPredicateValue(String predicateValue) {
        List<FieldValue> fielsValues = new ArrayList<FieldValue>();
        getFieldValues().forEach(fieldValue -> {
            if (fieldValue.getFieldPredicate().getValue().equals(predicateValue)) {
                fielsValues.add(fieldValue);
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

    public void setApproveEmbargoDate(Calendar approveEmbargoDate) {
        this.approveEmbargoDate = approveEmbargoDate;
    }

    public Calendar getApproveEmbargoDate() {
        return approveEmbargoDate;
    }

    public void setApproveApplicationDate(Calendar approveApplicationDate) {
        this.approveApplicationDate = approveApplicationDate;
    }

    public Calendar getApproveApplicationDate() {
        return approveApplicationDate;
    }

    public boolean getApproveEmbargo() {
        return approveEmbargo;
    }

    public void setApproveEmbargo(boolean approveEmbargo) {
        if (approveEmbargo) {
            this.approveEmbargoDate = getTime();
        } else {
            this.approveEmbargoDate = null;
        }
        this.approveEmbargo = approveEmbargo;
    }

    public void clearApproveEmbargo() {
        this.approveEmbargoDate = null;
        this.approveEmbargo = false;
    }

    public boolean getApproveApplication() {
        return approveApplication;
    }

    public void setApproveApplication(boolean approveApplication) {
        if (approveApplication) {
            this.approveApplicationDate = getTime();
        } else {
            this.approveApplicationDate = null;
        }
        this.approveApplication = approveApplication;
    }

    public void clearApproveApplication() {
        this.approveApplicationDate = null;
        this.approveApplication = false;
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

    public String getDepositUri() {
		return depositUri;
	}

	public void setDepositUri(String depositUri) {
		this.depositUri = depositUri;
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

        getSubmissionWorkflowSteps().forEach(submissionWorkflowSteps -> {
            submissionWorkflowSteps.getAggregateFieldProfiles().forEach(afp -> {
                if (afp.getInputType().equals(inputType)) {
                    List<FieldValue> foundFieldValues = getFieldValuesByPredicate(afp.getFieldPredicate());
                    fieldValues.addAll(foundFieldValues);
                }
            });
        });

        return fieldValues;
    }

    public List<FieldValue> getAllDocumentFieldValues() {
        List<FieldValue> fielsValues = new ArrayList<FieldValue>();
        for (FieldValue fieldValue : getFieldValues()) {
            if (fieldValue.getFieldPredicate().getDocumentTypePredicate()) {
                fielsValues.add(fieldValue);
            }
        }
        return fielsValues;
    }

    public FieldValue getPrimaryDocumentFieldValue() {
        FieldValue primaryDocumentFieldValue = null;
        for (FieldValue fieldValue : getFieldValues()) {
            if (fieldValue.getFieldPredicate().getValue().equals("_doctype_primary")) {
                primaryDocumentFieldValue = fieldValue;
                break;
            }
        }
        return primaryDocumentFieldValue;
    }

    public List<FieldValue> getSupplementalAndSourceDocumentFieldValues() {
        List<FieldValue> fielsValues = new ArrayList<FieldValue>();
        for (FieldValue fieldValue : getFieldValues()) {
            if (fieldValue.getFieldPredicate().getValue().equals("_doctype_supplemental") || fieldValue.getFieldPredicate().getValue().equals("_doctype_source")) {
                fielsValues.add(fieldValue);
            }
        }
        return fielsValues;
    }

    public List<FieldValue> getLicenseAgreementFieldValues() {
        List<FieldValue> fielsValues = new ArrayList<FieldValue>();
        for (FieldValue fieldValue : getFieldValues()) {
            if (fieldValue.getFieldPredicate().getValue().equals("license_agreement")) {
                fielsValues.add(fieldValue);
            }
        }
        return fielsValues;
    }
    
    public List<SubmissionFieldProfile> getSubmissionFieldProfilesByInputTypeName(String inputType) {

        List<SubmissionFieldProfile> submissionFieldProfiles = new ArrayList<SubmissionFieldProfile>();

        getSubmissionWorkflowSteps().forEach(submissionWorkflowSteps -> {
            submissionWorkflowSteps.getAggregateFieldProfiles().forEach(afp -> {
                if (afp.getInputType().getName().equals(inputType)) {
                    submissionFieldProfiles.add(afp);
                }
            });
        });

        return submissionFieldProfiles;
    }

    public void generateAdvisorReviewUrl(String baseUrl) {
        this.advisorReviewURL = baseUrl + "/review/" + this.getAdvisorAccessHash();
    }
    
    public String getAdvisorReviewURL() {
        return advisorReviewURL;
    }
}
