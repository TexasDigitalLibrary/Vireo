package org.tdl.vireo.model.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.custom.SubmissionRepoCustom;

public interface SubmissionRepo extends JpaRepository<Submission, Long>, SubmissionRepoCustom {

    public Submission findBySubmitterAndOrganization(User submitter, Organization organization);

    public List<Submission> findByOrganization(Organization organization);

    public List<Submission> findByActionLogsId(Long id);

    public List<Submission> findAllBySubmitter(User submitter);

    public Submission findOneByAdvisorAccessHash(String hash);

}
