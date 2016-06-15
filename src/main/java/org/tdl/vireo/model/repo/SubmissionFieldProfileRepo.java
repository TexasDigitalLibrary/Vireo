package org.tdl.vireo.model.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tdl.vireo.model.FieldPredicate;
import org.tdl.vireo.model.SubmissionFieldProfile;
import org.tdl.vireo.model.SubmissionWorkflowStep;
import org.tdl.vireo.model.repo.custom.SubmissionFieldProfileRepoCustom;

public interface SubmissionFieldProfileRepo extends JpaRepository<SubmissionFieldProfile, Long>, SubmissionFieldProfileRepoCustom {

    public SubmissionFieldProfile findByPredicateAndOriginatingWorkflowStep(FieldPredicate fieldPredicate, SubmissionWorkflowStep originatingWorkflowStep);
    
    public List<SubmissionFieldProfile> findByOriginatingWorkflowStep(SubmissionWorkflowStep originatingWorkflowStep);
    
    public List<SubmissionFieldProfile> findByOriginatingFieldProfile(SubmissionFieldProfile originatingFieldProfile);
    
    public void delete(SubmissionFieldProfile fieldProfile);

}
