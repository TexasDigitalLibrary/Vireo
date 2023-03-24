package org.tdl.vireo.model.simple;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.hibernate.annotations.Immutable;
import org.tdl.vireo.model.SubmissionState;
import org.tdl.vireo.model.SubmissionStatus;

@Entity
@Immutable
@Table(name = "submission_status")
public class SimpleSubmissionStatus implements Serializable {

    @Transient
    private static final long serialVersionUID = 7297110154119569868L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", insertable = false, updatable = false, nullable = false)
    private Long id;

    @Column(insertable = false, updatable = false, nullable = false, unique = true)
    private String name;

    @Column(insertable = false, updatable = false, nullable = false)
    private Boolean isArchived;

    @Column(insertable = false, updatable = false, nullable = false)
    private Boolean isPublishable;

    @Column(insertable = false, updatable = false, nullable = false)
    private Boolean isDeletable;

    @Column(insertable = false, updatable = false, nullable = false)
    private Boolean isEditableByReviewer;

    @Column(insertable = false, updatable = false, nullable = false)
    private Boolean isEditableByStudent;

    @Column(insertable = false, updatable = false, nullable = true)
    private Boolean isActive;

    @Column(insertable = false, updatable = false, nullable = false)
    private SubmissionState submissionState;

    @Transient
    private List<SubmissionStatus> transitionSubmissionStatuses;

    public static SubmissionStatus toSubmissionStatus(SimpleSubmissionStatus simpleSubmissionStatus) {
        if (simpleSubmissionStatus == null) {
            return null;
        }

        SubmissionStatus submission = new SubmissionStatus();

        submission.setId(simpleSubmissionStatus.getId());
        submission.setName(simpleSubmissionStatus.getName());
        submission.isArchived(simpleSubmissionStatus.isArchived());
        submission.isPublishable(simpleSubmissionStatus.isPublishable());
        submission.isDeletable(simpleSubmissionStatus.isDeletable());
        submission.isEditableByReviewer(simpleSubmissionStatus.isEditableByReviewer());
        submission.isEditableByStudent(simpleSubmissionStatus.isEditableByStudent());
        submission.isActive(simpleSubmissionStatus.isActive());
        submission.setSubmissionState(simpleSubmissionStatus.getSubmissionState());
        submission.setTransitionSubmissionStatuses(simpleSubmissionStatus.getTransitionSubmissionStatuses());

        return submission;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean isArchived() {
        return isArchived;
    }

    public void setArchived(Boolean isArchived) {
        this.isArchived = isArchived;
    }

    public Boolean isPublishable() {
        return isPublishable;
    }

    public void setPublishable(Boolean isPublishable) {
        this.isPublishable = isPublishable;
    }

    public Boolean isDeletable() {
        return isDeletable;
    }

    public void setDeletable(Boolean isDeletable) {
        this.isDeletable = isDeletable;
    }

    public Boolean isEditableByReviewer() {
        return isEditableByReviewer;
    }

    public void setEditableByReviewer(Boolean isEditableByReviewer) {
        this.isEditableByReviewer = isEditableByReviewer;
    }

    public Boolean isEditableByStudent() {
        return isEditableByStudent;
    }

    public void setEditableByStudent(Boolean isEditableByStudent) {
        this.isEditableByStudent = isEditableByStudent;
    }

    public Boolean isActive() {
        return isActive;
    }

    public void setActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public SubmissionState getSubmissionState() {
        return submissionState;
    }

    public void setSubmissionState(SubmissionState submissionState) {
        this.submissionState = submissionState;
    }

    public List<SubmissionStatus> getTransitionSubmissionStatuses() {
        return transitionSubmissionStatuses;
    }

    public void setTransitionSubmissionStatuses(List<SubmissionStatus> transitionSubmissionStatuses) {
        this.transitionSubmissionStatuses = transitionSubmissionStatuses;
    }

}
