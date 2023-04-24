package org.tdl.vireo.model;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.LAZY;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.NamedSubgraph;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.tdl.vireo.model.response.Views;
import org.tdl.vireo.model.validation.SubmissionValidator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;

import edu.tamu.weaver.validation.model.ValidatingBaseEntity;

@Entity
@JsonIgnoreProperties(value = { "organization" }, allowGetters = true)
@Table(
    indexes = {
        @Index(columnList = "submitter_id", name = "submission_submitter_id_idx"),
        @Index(columnList = "submitter_id, organization_id", name = "submission_organization_idx"),
    }
)
@NamedEntityGraphs({
    @NamedEntityGraph(
        name = "graph.Submission.List",
        attributeNodes = {
            @NamedAttributeNode(value = "assignee", subgraph = "subgraph.user"),
            @NamedAttributeNode(value = "submissionStatus", subgraph = "subgraph.submissionStatus"),
            @NamedAttributeNode(value = "organization", subgraph = "subgraph.organization"),
            // @NamedAttributeNode(value = "fieldValues", subgraph = "subgraph.fieldValues"),
            // @NamedAttributeNode(value = "customActionValues", subgraph = "subgraph.customActionValues"),
        },
        subgraphs = {
            @NamedSubgraph(
                name = "subgraph.user",
                attributeNodes = {}
            ),
            @NamedSubgraph(
                name = "subgraph.submissionStatus",
                attributeNodes = {}
            ),
            @NamedSubgraph(
                name = "subgraph.organization",
                attributeNodes = {
                    @NamedAttributeNode(value = "category"),
                }
            ),
            // @NamedSubgraph(
            //     name = "subgraph.fieldValues",
            //     attributeNodes = {
            //         @NamedAttributeNode(value = "fieldPredicate", subgraph = "subgraph.fieldPredicate"),
            //     }
            // ),
            // @NamedSubgraph(
            //     name = "subgraph.fieldPredicate",
            //     attributeNodes = {}
            // ),
            // @NamedSubgraph(
            //     name = "subgraph.customActionValues",
            //     attributeNodes = {
            //         @NamedAttributeNode(value = "definition"),
            //     }
            // ),
        }
    ),
    @NamedEntityGraph(
        name = "graph.Submission.Individual",
        attributeNodes = {
            @NamedAttributeNode(value = "submitter", subgraph = "subgraph.user"),
            @NamedAttributeNode(value = "assignee", subgraph = "subgraph.user"),
            @NamedAttributeNode(value = "submissionStatus", subgraph = "subgraph.submissionStatus"),
            @NamedAttributeNode(value = "organization", subgraph = "subgraph.organization"),
            // @NamedAttributeNode(value = "fieldValues", subgraph = "subgraph.fieldValues"),
            // @NamedAttributeNode(value = "submissionWorkflowSteps", subgraph = "subgraph.submissionWorkflowSteps"),
            // @NamedAttributeNode(value = "customActionValues", subgraph = "subgraph.customActionValues"),
        },
        subgraphs = {
            @NamedSubgraph(
                name = "subgraph.user",
                attributeNodes = {
                    @NamedAttributeNode(value = "currentContactInfo"),
                    @NamedAttributeNode(value = "permanentContactInfo"),
                }
            ),
            @NamedSubgraph(
                name = "subgraph.submissionStatus",
                attributeNodes = {}
            ),
            @NamedSubgraph(
                name = "subgraph.organization",
                attributeNodes = {
                    @NamedAttributeNode(value = "category"),
                }
            ),
            // @NamedSubgraph(
            //     name = "subgraph.fieldValues",
            //     attributeNodes = {
            //         @NamedAttributeNode(value = "contacts"),
            //         @NamedAttributeNode(value = "fieldPredicate", subgraph = "subgraph.fieldPredicate"),
            //     }
            // ),
            // @NamedSubgraph(
            //     name = "subgraph.fieldPredicate",
            //     attributeNodes = {}
            // ),
            // @NamedSubgraph(
            //     name = "subgraph.submissionWorkflowSteps",
            //     attributeNodes = {
            //         @NamedAttributeNode(value = "aggregateFieldProfiles"),
            //         @NamedAttributeNode(value = "aggregateNotes"),
            //     }
            // ),
            // @NamedSubgraph(
            //     name = "subgraph.customActionValues",
            //     attributeNodes = {
            //         @NamedAttributeNode(value = "definition"),
            //     }
            // ),
        }
    )
})
public class Submission extends ValidatingBaseEntity {

    @JsonView(Views.Partial.class)
    @ManyToOne(fetch = LAZY, optional = false)
    private User submitter;

    @JsonView(Views.SubmissionList.class)
    @ManyToOne(fetch = LAZY, optional = true)
    private User assignee;

    @JsonView(Views.SubmissionList.class)
    @ManyToOne(fetch = LAZY, optional = false)
    private SubmissionStatus submissionStatus;

    @JsonView(Views.SubmissionList.class)
    @ManyToOne(fetch = LAZY, optional = false)
    private Organization organization;

    @JsonView(Views.SubmissionList.class)
    @OneToMany(cascade = ALL, fetch = LAZY, orphanRemoval = true)
    private Set<FieldValue> fieldValues;

    @JsonView(Views.Partial.class)
    @ManyToMany(fetch = LAZY)
    @Fetch(FetchMode.SELECT)
    @CollectionTable(uniqueConstraints = @UniqueConstraint(columnNames = { "submission_id", "submission_workflow_steps_id", "submissionWorkflowSteps_order" }))
    @OrderColumn
    private List<SubmissionWorkflowStep> submissionWorkflowSteps;

    @JsonView(Views.SubmissionList.class)
    @Column(nullable = true)
    @Temporal(TemporalType.DATE)
    private Calendar approveEmbargoDate;

    @JsonView(Views.SubmissionList.class)
    @Column(nullable = true)
    @Temporal(TemporalType.DATE)
    private Calendar approveApplicationDate;

    @JsonView(Views.SubmissionList.class)
    @Column(nullable = true)
    @Temporal(TemporalType.DATE)
    private Calendar submissionDate;

    @JsonView(Views.SubmissionList.class)
    @Column(nullable = true)
    @Temporal(TemporalType.DATE)
    private Calendar approveAdvisorDate;

    @JsonView(Views.SubmissionList.class)
    @Column(nullable = true)
    private boolean approveEmbargo;

    @JsonView(Views.SubmissionList.class)
    @Column(nullable = true)
    private boolean approveApplication;

    @JsonView(Views.SubmissionList.class)
    @Column(nullable = true)
    private boolean approveAdvisor;

    @JsonView(Views.SubmissionList.class)
    @OneToMany(cascade = ALL, fetch = LAZY, orphanRemoval = true)
    @Fetch(FetchMode.SELECT)
    private Set<CustomActionValue> customActionValues;

    @JsonView(Views.SubmissionIndividualActionLogs.class)
    @OneToMany(cascade = ALL, fetch = LAZY, orphanRemoval = true)
    @Fetch(FetchMode.SELECT)
    @JoinColumn
    private Set<ActionLog> actionLogs;

    @JsonView(Views.SubmissionList.class)
    @Column(columnDefinition = "TEXT")
    private String reviewerNotes;

    @JsonView(Views.SubmissionIndividual.class)
    @Column(nullable = true)
    private String advisorAccessHash;

    @JsonView(Views.SubmissionIndividual.class)
    @Column(nullable = true)
    private String advisorReviewURL;

    @JsonView(Views.SubmissionIndividual.class)
    @Column(nullable = true)
    private String depositURL;

    public Submission() {
        setModelValidator(new SubmissionValidator());
        setFieldValues(new HashSet<FieldValue>());
        setSubmissionWorkflowSteps(new ArrayList<SubmissionWorkflowStep>());
        setActionLogs(new HashSet<ActionLog>());
        setApproveApplication(false);
        setApproveEmbargo(false);
        setCustomActionValues(new HashSet<CustomActionValue>());
    }

    /**
     * @param submitter
     * @param organization
     */
    public Submission(User submitter, Organization organization) {
        this();
        setSubmitter(submitter);
        setOrganization(organization);
        generateAdvisorAccessHash();
    }

    /**
     * @param submitter
     * @param organization
     * @param submissionStatus
     */
    public Submission(User submitter, Organization organization, SubmissionStatus submissionStatus) {
        this(submitter, organization);
        setSubmissionStatus(submissionStatus);
    }

    /**
     * @return the submitter
     */
    public User getSubmitter() {
        return submitter;
    }

    /**
     * @param submitter the submitter to set
     */
    public void setSubmitter(User submitter) {
        this.submitter = submitter;
    }

    /**
     * @return the assignee
     */
    public User getAssignee() {
        return assignee;
    }

    /**
     * @param assignee the assignee to set
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
     * @param submissionStatus the submissionStatus to set
     */
    public void setSubmissionStatus(SubmissionStatus submissionStatus) {
        this.submissionStatus = submissionStatus;
    }

    /**
     * @return the organization
     */
    public Organization getOrganization() {
        return organization;
    }

    /**
     * @param organization the organization to set
     */
    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    /**
     * @return the fieldValues
     */
    public Set<FieldValue> getFieldValues() {
        return fieldValues;
    }

    /**
     * @param fieldValues the fieldValues to set
     */
    public void setFieldValues(Set<FieldValue> fieldValues) {
        this.fieldValues = fieldValues;
    }

    /**
     * @param fieldValue
     */
    public void addFieldValue(FieldValue fieldValue) {
        getFieldValues().add(fieldValue);
    }

    /**
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
     * @param submissionWorkflowSteps the submissionWorkflowSteps to set
     */
    public void setSubmissionWorkflowSteps(List<SubmissionWorkflowStep> submissionWorkflowSteps) {
        this.submissionWorkflowSteps = submissionWorkflowSteps;
    }

    /**
     * @param submissionWorkflowStep the submissionWorkflowStep to add.
     */
    public void addSubmissionWorkflowStep(SubmissionWorkflowStep submissionWorkflowStep) {
        getSubmissionWorkflowSteps().add(submissionWorkflowStep);
    }

    /**
     * @param submissionWorkflowStep the submissionWorkflowStep to remove.
     */
    public void removeSubmissionWorkflowStep(SubmissionWorkflowStep submissionWorkflowStep) {
        getSubmissionWorkflowSteps().remove(submissionWorkflowStep);
    }

    /**
     * @return the approveEmbargoDate
     */
    public Calendar getApproveEmbargoDate() {
        return approveEmbargoDate;
    }

    /**
     * @param approveEmbargoDate the approveEmbargoDate to set
     */
    public void setApproveEmbargoDate(Calendar approveEmbargoDate) {
        this.approveEmbargoDate = approveEmbargoDate;
    }

    /**
     * @return the approveApplicationDate
     */
    public Calendar getApproveApplicationDate() {
        return approveApplicationDate;
    }

    /**
     * @param approveApplicationDate the approveApplicationDate to set
     */
    public void setApproveApplicationDate(Calendar approveApplicationDate) {
        this.approveApplicationDate = approveApplicationDate;
    }

    /**
     * @return the submissionDate
     */
    public Calendar getSubmissionDate() {
        return submissionDate;
    }

    /**
     * @param submissionDate the submissionDate to set
     */
    public void setSubmissionDate(Calendar submissionDate) {
        this.submissionDate = submissionDate;
    }

    /**
     * @return the approveAdvisorDate
     */
    public Calendar getApproveAdvisorDate() {
        return approveAdvisorDate;
    }

    /**
     * @param approveAdvisorDate the approveAdvisorDate to set
     */
    public void setApproveAdvisorDate(Calendar approveAdvisorDate) {
        this.approveAdvisorDate = approveAdvisorDate;
    }

    /**
     * @return the approveEmbargo
     */
    public boolean getApproveEmbargo() {
        return approveEmbargo;
    }

    /**
     * @param approveEmbargo the approveEmbargo to set
     */
    public void setApproveEmbargo(boolean approveEmbargo) {
        this.approveEmbargo = approveEmbargo;
    }

    /**
     * Clear the embargo and the embargo date.
     */
    public void clearApproveEmbargo() {
        this.approveEmbargoDate = null;
        this.approveEmbargo = false;
    }

    /**
     * @return the approveApplication
     */
    public boolean isApproveApplication() {
        return approveApplication;
    }

    /**
     * @param approveApplication the approveApplication to set
     */
    public void setApproveApplication(boolean approveApplication) {
        this.approveApplication = approveApplication;
    }

    /**
     * Clear the approve application and the approve application date.
     */
    public void clearApproveApplication() {
        this.approveApplicationDate = null;
        this.approveApplication = false;
    }

    /**
     * @return the approveAdvisor
     */
    public boolean getApproveAdvisor() {
        return approveAdvisor;
    }

    /**
     * @param approveAdvisor the approveAdvisor to set
     */
    public void setApproveAdvisor(boolean approveAdvisor) {
        this.approveAdvisor = approveAdvisor;
    }

    /**
     * Clear the approve advisor and the approve advisor date.
     */
    public void clearApproveAdvisor() {
        this.approveAdvisorDate = null;
        this.approveAdvisor = false;
    }

    /**
     * @return the actionLogs
     */
    public Set<ActionLog> getActionLogs() {
        return actionLogs;
    }

    /**
     * @param actionLogs the actionLogs to set
     */
    public void setActionLogs(Set<ActionLog> actionLogs) {
        this.actionLogs = actionLogs;
    }

    /**
     * @param actionLog
     */
    public void addActionLog(ActionLog actionLog) {
        getActionLogs().add(actionLog);
    }

    /**
     * @param actionLog
     */
    public void removeActionLog(ActionLog actionLog) {
        getActionLogs().remove(actionLog);
    }

    /**
     *
     */
    @JsonView(Views.Partial.class)
    public String getLastEvent() {
        Optional<ActionLog> actionLog = getActionLogs()
            .stream()
            .max(Comparator.comparing(al -> al.getActionDate()));
        String lastEvent = null;

        if (actionLog.isPresent()) {
            lastEvent = actionLog.get().getEntry();
        }

        return lastEvent;
    }

    /**
     * @return The reviewer notes.
     */
    public String getReviewerNotes() {
        return reviewerNotes;
    }

    /**
     * @param reviewerNotes
     */
    public void setReviewerNotes(String reviewerNotes) {
        this.reviewerNotes = reviewerNotes;
    }

    /**
     * Generate the advisor access hash.
     */
    private void generateAdvisorAccessHash() {
        setAdvisorAccessHash(UUID.randomUUID().toString().replace("-", ""));
    }

    /**
     * @return the advisorAccessHash
     */
    public String getAdvisorAccessHash() {
        return advisorAccessHash;
    }

    /**
     * @param advisorAccessHash the advisorAccessHash to set
     */
    public void setAdvisorAccessHash(String advisorAccessHash) {
        this.advisorAccessHash = advisorAccessHash;
    }

    /**
     * @return The committee contact e-mail.
     */
    @JsonView(Views.SubmissionList.class)
    public String getCommitteeContactEmail() {
        Optional<FieldValue> optFv = this.getFieldValuesByPredicateValue("dc.contributor.advisor")
            .stream()
            .findFirst();
        String email = null;
        if (optFv.isPresent()) {
            Optional<String> optEmail = optFv.get()
                .getContacts()
                .stream()
                .findFirst();
            if (optEmail.isPresent()) {
                email = optEmail.get();
            }
        }
        return email;
    }

    /**
     * @return the customActionValues
     */
    public Set<CustomActionValue> getCustomActionValues() {
        return customActionValues;
    }

    /**
     * @param customActionValues the customActionValues to set
     */
    public void setCustomActionValues(Set<CustomActionValue> customActionValues) {
        this.customActionValues = customActionValues;
    }

    /**
     * @return the depositURL
     */
    public String getDepositURL() {
        return depositURL;
    }

    /**
     * @param depositURL the depositURL to set
     */
    public void setDepositURL(String depositURL) {
        this.depositURL = depositURL;
    }

    /**
     * @param customActionValue
     */
    public void addCustomActionValue(CustomActionValue customActionValue) {
        this.customActionValues.add(customActionValue);
    }

    /**
     * @param customActionValue
     */
    public void removeCustomActionValue(CustomActionValue customActionValue) {
        this.customActionValues.remove(customActionValue);
    }

    /**
     * @param customActionValue
     * @return The edited custom action value.
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

    /**
     * @param customActionValue
     * @return The custom action value.
     */
    public CustomActionValue getCustomActionValue(CustomActionValue customActionValue) {
        for (CustomActionValue cav : this.customActionValues) {
            if (cav.getDefinition().getLabel().equals(customActionValue.getDefinition().getLabel())) {
                return cav;
            }
        }
        return null;
    }

    @JsonIgnore
    public List<FieldValue> getFieldValuesByPredicate(FieldPredicate fieldPredicate) {
        List<FieldValue> fielsValues = new ArrayList<FieldValue>();
        getFieldValues().forEach(fieldValue -> {
            if (fieldValue.getFieldPredicate().equals(fieldPredicate)) {
                fielsValues.add(fieldValue);
            }
        });
        return fielsValues;
    }

    @JsonIgnore
    public List<FieldValue> getFieldValuesByPredicateValue(String predicateValue) {
        List<FieldValue> fielsValues = new ArrayList<FieldValue>();
        getFieldValues().forEach(fieldValue -> {
            if (fieldValue.getFieldPredicate().getValue().equals(predicateValue)) {
                fielsValues.add(fieldValue);
            }
        });
        return fielsValues;
    }

    @JsonIgnore
    public List<FieldValue> getFieldValuesByPredicateValueStartsWith(String predicateValue) {
        List<FieldValue> fieldValues = new ArrayList<FieldValue>();
        getFieldValues().forEach(fieldValue -> {
            if (fieldValue.getFieldPredicate().getValue().startsWith(predicateValue)) {
                fieldValues.add(fieldValue);
            }
        });
        return fieldValues;
    }   


    @JsonIgnore
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

    @JsonIgnore
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

    @JsonIgnore
    public List<FieldValue> getAllDocumentFieldValues() {
        List<FieldValue> fielsValues = new ArrayList<FieldValue>();
        for (FieldValue fieldValue : getFieldValues()) {
            if (fieldValue.getFieldPredicate().getDocumentTypePredicate()) {
                fielsValues.add(fieldValue);
            }
        }
        return fielsValues;
    }

    @JsonIgnore
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

    @JsonIgnore
    public List<FieldValue> getLicenseDocumentFieldValues() {
        List<FieldValue> fielsValues = new ArrayList<FieldValue>();
        for (FieldValue fieldValue : getFieldValues()) {
            if (fieldValue.getFieldPredicate().getValue().equals("_doctype_license")) {
                fielsValues.add(fieldValue);
            }
        }
        return fielsValues;
    }

    @JsonIgnore
    public List<FieldValue> getSupplementalAndSourceDocumentFieldValues() {
        List<FieldValue> fielsValues = new ArrayList<FieldValue>();
        for (FieldValue fieldValue : getFieldValues()) {
            if (fieldValue.getFieldPredicate().getValue().equals("_doctype_supplemental") || fieldValue.getFieldPredicate().getValue().equals("_doctype_source")) {
                fielsValues.add(fieldValue);
            }
        }
        return fielsValues;
    }

    @JsonIgnore
    public List<FieldValue> getSupplementalDocumentFieldValues() {
        List<FieldValue> fielsValues = new ArrayList<FieldValue>();
        for (FieldValue fieldValue : getFieldValues()) {
            if (fieldValue.getFieldPredicate().getValue().equals("_doctype_supplemental")) {
                fielsValues.add(fieldValue);
            }
        }
        return fielsValues;
    }

    @JsonIgnore
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

    public void setAdvisorReviewURL(String advisorReviewURL) {
        this.advisorReviewURL = advisorReviewURL;
    }

    public String getAdvisorReviewURL() {
        return advisorReviewURL;
    }

}
