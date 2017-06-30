package org.tdl.vireo.model.repo;

import java.util.List;

import org.tdl.vireo.model.FieldPredicate;
import org.tdl.vireo.model.FieldProfile;
import org.tdl.vireo.model.WorkflowStep;
import org.tdl.vireo.model.inheritance.HeritableJpaRepo;
import org.tdl.vireo.model.repo.custom.FieldProfileRepoCustom;

public interface FieldProfileRepo extends HeritableJpaRepo<FieldProfile>, FieldProfileRepoCustom {

	public FieldProfile findByFieldPredicateAndOriginatingWorkflowStep(FieldPredicate fieldPredicate, WorkflowStep originatingWorkflowStep);

	public List<FieldProfile> findByOriginatingWorkflowStep(WorkflowStep originatingWorkflowStep);

	public List<FieldProfile> findByOriginating(FieldProfile originatingFieldProfile);

	public void delete(FieldProfile fieldProfile);

}
