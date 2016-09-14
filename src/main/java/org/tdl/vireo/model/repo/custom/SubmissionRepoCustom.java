package org.tdl.vireo.model.repo.custom;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.SubmissionListColumn;

import edu.tamu.framework.model.Credentials;

public interface SubmissionRepoCustom {
    
    public Submission create(Credentials submitterCredentials, Long organizationId);
    
    public Page<Submission> pageableDynamicSubmissionQuery(Credentials credentials, List<SubmissionListColumn> submissionListColums, Pageable pageable);

}
