package org.tdl.vireo.model;

import javax.persistence.Entity;

@Entity
public class FieldValue extends BaseEntity {
	FieldProfile fieldProfile;
	Submission submission;
	String value;
}
