package org.tdl.vireo.model.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.JpaRepository;

import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.custom.SubmissionRepoCustom;

public interface SubmissionRepo extends JpaRepository<Submission, Long>, SubmissionRepoCustom, JpaSpecificationExecutor<Submission> {

    public Submission findBySubmitterAndOrganization(User submitter, Organization organization);

    public List<Submission> findByOrganization(Organization organization);

    public List<Submission> findAllBySubmitter(User submitter);

    public Page<Submission> findAll(Specification<Submission> specification, Pageable pageable);
    
    public Page<Submission> findAll(Pageable pageable);

}
