package org.tdl.vireo.model;

import java.util.Set;

import javax.persistence.Entity;

@Entity
public class Submission {
	Set<Organization> organizations;
	Set<FieldValue> fieldvalues;
	SubmissionState state;
}
