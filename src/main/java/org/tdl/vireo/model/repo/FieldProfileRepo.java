package org.tdl.vireo.model.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.tdl.vireo.model.FieldPredicate;
import org.tdl.vireo.model.FieldProfile;
import org.tdl.vireo.model.WorkflowStep;
import org.tdl.vireo.model.inheritance.HeritableRepo;
import org.tdl.vireo.model.repo.custom.FieldProfileRepoCustom;

public interface FieldProfileRepo extends HeritableRepo<FieldProfile>, FieldProfileRepoCustom {

    public FieldProfile findByFieldPredicateAndOriginatingWorkflowStep(FieldPredicate fieldPredicate, WorkflowStep originatingWorkflowStep);

    public List<FieldProfile> findByOriginatingWorkflowStep(WorkflowStep originatingWorkflowStep);

    public List<FieldProfile> findByOriginating(FieldProfile originatingFieldProfile);

    @Query(value = "SELECT fp.gloss FROM FieldProfile fp WHERE fp_type = 'Org' AND fp.id = :id")
    public String findGlossById(@Param("id") Long id);

}
