package org.tdl.vireo.model.repo.custom;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.tdl.vireo.model.NamedSearchFilterGroup;
import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.SubmissionListColumn;
import org.tdl.vireo.model.SubmissionState;
import org.tdl.vireo.model.User;

import edu.tamu.framework.model.Credentials;

public interface SubmissionRepoCustom {

    public Submission create(User submitter, Organization organization, SubmissionState submissionState, Credentials credentials);
    
    public Submission updateStatus(Submission submission, SubmissionState state, Credentials credentials);

    public Page<Submission> pageableDynamicSubmissionQuery(NamedSearchFilterGroup activeFilter, List<SubmissionListColumn> submissionListColums, Pageable pageable);

    public List<Submission> batchDynamicSubmissionQuery(NamedSearchFilterGroup activeFilter, List<SubmissionListColumn> submissionListColums);

}
