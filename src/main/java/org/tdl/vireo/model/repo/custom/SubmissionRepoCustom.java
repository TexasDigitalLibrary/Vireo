package org.tdl.vireo.model.repo.custom;

import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.SubmissionState;
import org.tdl.vireo.model.User;

import edu.tamu.framework.model.Credentials;

public interface SubmissionRepoCustom {
    
    public Submission create(Credentials submitterCredentials, Long organizationId);

}
