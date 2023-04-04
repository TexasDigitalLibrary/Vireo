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

import org.tdl.vireo.model.response.Views;
import org.tdl.vireo.model.validation.SubmissionStatusValidator;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import edu.tamu.weaver.validation.model.ValidatingBaseEntity;

@Entity
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class SubmissionStatus extends ValidatingBaseEntity {

    @JsonView(Views.SubmissionList.class)
    @Column(nullable = false, unique = true)
    private String name;

    @JsonView(Views.SubmissionIndividual.class)
    @Column(nullable = false)
    @JsonProperty("isArchived")
    private Boolean isArchived;

    @JsonView(Views.SubmissionIndividual.class)
    @Column(nullable = false)
    @JsonProperty("isPublishable")
    private Boolean isPublishable;

    @JsonView(Views.SubmissionIndividual.class)
    @Column(nullable = false)
    @JsonProperty("isDeletable")
    private Boolean isDeletable;

    @JsonView(Views.SubmissionIndividual.class)
    @Column(nullable = false)
    @JsonProperty("isEditableByReviewer")
    private Boolean isEditableByReviewer;

    @JsonView(Views.SubmissionIndividual.class)
    @Column(nullable = false)
    @JsonProperty("isEditableByStudent")
    private Boolean isEditableByStudent;

    @JsonView(Views.SubmissionIndividual.class)
    @Column(nullable = true)
    @JsonProperty("isActive")
    private Boolean isActive;

    @JsonView(Views.Partial.class)
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
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the isArchived
     */
    public Boolean isArchived() {
        return isArchived;
    }

    /**
     * @param isArchived the isArchived to set
     */
    public void isArchived(Boolean isArchived) {
        this.isArchived = isArchived;
    }

    /**
     * @return the isPublishable
     */
    public Boolean isPublishable() {
        return isPublishable;
    }

    /**
     * @param isPublishable the isPublishable to set
     */
    public void isPublishable(Boolean isPublishable) {
        this.isPublishable = isPublishable;
    }

    /**
     * @return the isDeletable
     */
    public Boolean isDeletable() {
        return isDeletable;
    }

    /**
     * @param isDeletable the isDeletable to set
     */
    public void isDeletable(Boolean isDeletable) {
        this.isDeletable = isDeletable;
    }

    /**
     * @return the isEditableByReviewer
     */
    public Boolean isEditableByReviewer() {
        return isEditableByReviewer;
    }

    /**
     * @param isEditableByReviewer the isEditableByReviewer to set
     */
    public void isEditableByReviewer(Boolean isEditableByReviewer) {
        this.isEditableByReviewer = isEditableByReviewer;
    }

    /**
     * @return the isEditableByStudent
     */
    public Boolean isEditableByStudent() {
        return isEditableByStudent;
    }

    /**
     * @param isEditableByStudent the isEditableByStudent to set
     */
    public void isEditableByStudent(Boolean isEditableByStudent) {
        this.isEditableByStudent = isEditableByStudent;
    }

    /**
     * @return the isActive
     */
    public Boolean isActive() {
        return isActive;
    }

    /**
     * @param isActive the isActive to set
     */
    public void isActive(Boolean isActive) {
        this.isActive = isActive;
    }

    /**
     * @return the submissionState
     */
    public SubmissionState getSubmissionState() {
        return submissionState;
    }

    /**
     * @param submissionState the submissionState to set
     */
    public void setSubmissionState(SubmissionState submissionState) {
        this.submissionState = submissionState;
    }

    /**
     * @return the transitionSubmissionStatuses
     */
    public List<SubmissionStatus> getTransitionSubmissionStatuses() {
        return transitionSubmissionStatuses;
    }

    /**
     * @param transitionSubmissionStatuses the transitionSubmissionStatuses to set
     */
    public void setTransitionSubmissionStatuses(List<SubmissionStatus> transitionSubmissionStatuses) {
        this.transitionSubmissionStatuses = transitionSubmissionStatuses;
    }

    /**
     * Add the transition submission state.
     *
     * @param transitionSubmissionStatus The SubmissionStatus to add.
     */
    public void addTransitionSubmissionStatus(SubmissionStatus transitionSubmissionStatus) {
        getTransitionSubmissionStatuses().add(transitionSubmissionStatus);
    }

    /**
     * Remove the transition submission state.
     *
     * @param transitionSubmissionStatus The SubmissionStatus to remove.
     */
    public void removeTransitionSubmissionStatus(SubmissionStatus transitionSubmissionStatus) {
        getTransitionSubmissionStatuses().remove(transitionSubmissionStatus);
    }

}
