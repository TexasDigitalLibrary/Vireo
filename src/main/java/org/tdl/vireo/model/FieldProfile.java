package org.tdl.vireo.model;

import java.util.Map;
import java.util.Set;

import javax.persistence.Entity;

@Entity
public class FieldProfile {
	Set<WorkflowStep> workflowSteps;
	Map<String, FieldGloss> fieldGlosses;
	FieldPredicate predicate;
	Set<ControlledVocabulary> controlledVocab;
	Boolean repeatable, required;
	String type;
	
}
