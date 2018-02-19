package org.tdl.vireo.model;

import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.REFRESH;
import static javax.persistence.FetchType.EAGER;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;

import org.tdl.vireo.model.validation.SubmissionStatusValidator;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import edu.tamu.weaver.response.ApiView;
import edu.tamu.weaver.validation.model.ValidatingBaseEntity;

@Entity
public class SubmissionStatus extends ValidatingBaseEntity {

    @JsonView(ApiView.Partial.class)
    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    @JsonProperty("isArchived")
    private Boolean isArchived;

    @Column(nullable = false)
    @JsonProperty("isPublishable")
    private Boolean isPublishable;

    @Column(nullable = false)
    @JsonProperty("isDeletable")
    private Boolean isDeletable;

    @Column(nullable = false)
    @JsonProperty("isEditableByReviewer")
    private Boolean isEditableByReviewer;

    @Column(nullable = false)
    @JsonProperty("isEditableByStudent")
    private Boolean isEditableByStudent;

    @Column(nullable = true)
    @JsonProperty("isActive")
    private Boolean isActive;

    @JsonView(ApiView.Partial.class)
    @Column(nullable = false)
    private SubmissionState submissionState;

    @ManyToMany(cascade = { DETACH, REFRESH, MERGE }, fetch = EAGER)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = SubmissionStatus.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private List<SubmissionStatus> transitionSubmissionStatuses;

    public SubmissionStatus() {
        setModelValidator(new SubmissionStatusValidator());
        setTransitionSubmissionStatuses(new ArrayList<SubmissionStatus>());
    }

    /**
     * @param name
     * @param isArchived
     * @param isPublishable
     * @param isDeletable
     * @param isEditableByReviewer
     * @param isEditableByStudent
     * @param isActive
     */
    public SubmissionStatus(String name, Boolean isArchived, Boolean isPublishable, Boolean isDeletable, Boolean isEditableByReviewer, Boolean isEditableByStudent, Boolean isActive, SubmissionState submissionState) {
        this();
        setName(name);
        isArchived(isArchived);
        isPublishable(isPublishable);
        isDeletable(isDeletable);
        isEditableByReviewer(isEditableByReviewer);
        isEditableByStudent(isEditableByStudent);
        isActive(isActive);
        setSubmissionState(submissionState);
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return isArchived
     */
    public Boolean isArchived() {
        return isArchived;
    }

    /**
     * @param isArchived
     *            the archived to set
     */
    public void isArchived(Boolean isArchived) {
        this.isArchived = isArchived;
    }

    /**
     * @return isPublishable
     */
    public Boolean isPublishable() {
        return isPublishable;
    }

    /**
     * @param isPublishable
     *            the publishable to set
     */
    public void isPublishable(Boolean isPublishable) {
        this.isPublishable = isPublishable;
    }

    /**
     * @return isDeletable
     */
    public Boolean isDeletable() {
        return isDeletable;
    }

    /**
     * @param isDeletable
     *            the deletable to set
     */
    public void isDeletable(Boolean isDeletable) {
        this.isDeletable = isDeletable;
    }

    /**
     * @return isEditableByReviewer
     */
    public Boolean isEditableByReviewer() {
        return isEditableByReviewer;
    }

    /**
     * @param isEditableByReviewer
     *            the editableByReviewer to set
     */
    public void isEditableByReviewer(Boolean isEditableByReviewer) {
        this.isEditableByReviewer = isEditableByReviewer;
    }

    /**
     * @return isEditableByStudent
     */
    public Boolean isEditableByStudent() {
        return isEditableByStudent;
    }

    /**
     * @param isEditableByStudent
     *            the editableByStudent to set
     */
    public void isEditableByStudent(Boolean isEditableByStudent) {
        this.isEditableByStudent = isEditableByStudent;
    }

    /**
     * @return isActive
     */
    public Boolean isActive() {
        return isActive;
    }

    /**
     * @param isActive
     *            the active to set
     */
    public void isActive(Boolean isActive) {
        this.isActive = isActive;
    }

    /**
     * @return the transitionSubmissionStates
     */
    public List<SubmissionStatus> getTransitionSubmissionStatuses() {
        return transitionSubmissionStatuses;
    }

    /**
     * @param transitionSubmissionStatuses
     *            the transitionSubmissionStates to set
     */
    public void setTransitionSubmissionStatuses(List<SubmissionStatus> transitionSubmissionStates) {
        this.transitionSubmissionStatuses = transitionSubmissionStates;
    }

    /**
     *
     * @param transitionSubmissionStatus
     */
    public void addTransitionSubmissionStatus(SubmissionStatus transitionSubmissionState) {
        getTransitionSubmissionStatuses().add(transitionSubmissionState);
    }

    /**
     *
     * @param transitionSubmissionStatus
     */
    public void removeTransitionSubmissionStatus(SubmissionStatus transitionSubmissionState) {
        getTransitionSubmissionStatuses().remove(transitionSubmissionState);
    }

    public SubmissionState getSubmissionState() {
        return submissionState;
    }

    public void setSubmissionState(SubmissionState submissionState) {
        this.submissionState = submissionState;
    }
}
