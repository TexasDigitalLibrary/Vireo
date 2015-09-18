package org.tdl.vireo.model;

import java.util.Set;

import javax.persistence.Entity;

@Entity
public class SubmissionState extends BaseEntity {
	String name;
	Boolean archived, publishable, deletable, editableByReviewer, editableByStudent, active;
	Set<SubmissionState> transitionSubmissionStates;
}
