package org.tdl.vireo.model;

import java.util.Set;

import javax.persistence.Entity;

@Entity
public class Workflow extends BaseEntity {
	Set<WorkflowStep> workflowSteps;
	String name;
	Boolean isInheritable;
}
