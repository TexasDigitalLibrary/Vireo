package org.tdl.vireo.model;

import java.util.Set;

import javax.persistence.Entity;

@Entity
public class WorkflowStep extends BaseEntity {
	Set<Workflow> workflows;
	String name;
}
