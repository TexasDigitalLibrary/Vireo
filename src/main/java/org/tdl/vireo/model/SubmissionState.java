package org.tdl.vireo.model;

import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.REFRESH;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
public class SubmissionState extends BaseEntity {

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

    @Column(nullable = false)
    @JsonProperty("isActive")
    private Boolean isActive;

    @ManyToMany(cascade = { DETACH, REFRESH, MERGE })
    private List<SubmissionState> transitionSubmissionStates;

    public SubmissionState() {
        setTransitionSubmissionStates(new ArrayList<SubmissionState>());
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
    public SubmissionState(String name, Boolean isArchived, Boolean isPublishable, Boolean isDeletable, Boolean isEditableByReviewer, Boolean isEditableByStudent, Boolean isActive) {
        this();
        setName(name);
        isArchived(isArchived);
        isPublishable(isPublishable);
        isDeletable(isDeletable);
        isEditableByReviewer(isEditableByReviewer);
        isEditableByStudent(isEditableByStudent);
        isActive(isActive);
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
    public List<SubmissionState> getTransitionSubmissionStates() {
        return transitionSubmissionStates;
    }

    /**
     * @param transitionSubmissionStates
     *            the transitionSubmissionStates to set
     */
    public void setTransitionSubmissionStates(List<SubmissionState> transitionSubmissionStates) {
        this.transitionSubmissionStates = transitionSubmissionStates;
    }

    /**
     * 
     * @param transitionSubmissionState
     */
    public void addTransitionSubmissionState(SubmissionState transitionSubmissionState) {
        getTransitionSubmissionStates().add(transitionSubmissionState);
    }

    /**
     * 
     * @param transitionSubmissionState
     */
    public void removeTransitionSubmissionState(SubmissionState transitionSubmissionState) {
        getTransitionSubmissionStates().remove(transitionSubmissionState);
    }
}
