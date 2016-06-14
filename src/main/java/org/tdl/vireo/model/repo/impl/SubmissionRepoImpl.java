package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.SubmissionState;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.OrganizationRepo;
import org.tdl.vireo.model.repo.SubmissionRepo;
import org.tdl.vireo.model.repo.UserRepo;
import org.tdl.vireo.model.repo.custom.SubmissionRepoCustom;

import edu.tamu.framework.model.Credentials;

public class SubmissionRepoImpl implements SubmissionRepoCustom {

    @Autowired
    private SubmissionRepo submissionRepo;
    
    @Autowired
    private OrganizationRepo organizationRepo;
    
    @Autowired
    private UserRepo userRepo;

    @Override
    public Submission create(User submitter, SubmissionState state) {
        return submissionRepo.save(new Submission(submitter, state));
    }

    @Override
    public Submission create(Credentials submitterCredentials, Long organizationId) {
        
        User submitter = userRepo.findByEmail(submitterCredentials.getEmail());
        Organization organization = organizationRepo.findOne(organizationId);
        
        System.out.println(submitter.getFirstName());
        System.out.println(organization.getName());
        
        return null;
    }

}
