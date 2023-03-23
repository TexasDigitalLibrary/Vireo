package org.tdl.vireo.model.repo;

import edu.tamu.weaver.data.model.repo.WeaverRepo;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.custom.SubmissionRepoCustom;

public interface SubmissionRepo extends WeaverRepo<Submission>, SubmissionRepoCustom {

    public List<Submission> findAllBySubmitterAndOrganization(User submitter, Organization organization);

    public List<Submission> findByOrganization(Organization organization);

    public List<Submission> findByActionLogsId(Long id);

    public List<Submission> findAllBySubmitterId(Long submitterId);

    public Submission findOneBySubmitterAndId(User submitter, Long id);

    public Submission findOneByAdvisorAccessHash(String hash);

    public Submission findByCustomActionValuesDefinitionLabel(String label);

    public Long countByOrganizationId(Long id);

    @EntityGraph(attributePaths = {
        "submitter",
        "assignee",
        "submissionStatus",
        "organization",
        "fieldValues",
        "submissionWorkflowSteps",
        "approveEmbargoDate",
        "approveApplicationDate",
        "submissionDate",
        "approveAdvisorDate",
        "approveEmbargo",
        "approveApplication",
        "approveAdvisor",
        "customActionValues",
        "reviewerNotes",
        "advisorAccessHash",
        "advisorReviewURL",
        "depositURL"
     })
    public Submission findGraphForEmailById(Long id);

    @Override
    public Submission update(Submission submission);

    @Override
    public void delete(Submission submission);

}
