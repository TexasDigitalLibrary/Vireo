package org.tdl.vireo.model;

import java.util.Set;

import javax.persistence.Entity;

@Entity
public class Submission extends BaseEntity {
	Set<Organization> organizations;
	Set<FieldValue> fieldvalues;
	SubmissionState state;
}
