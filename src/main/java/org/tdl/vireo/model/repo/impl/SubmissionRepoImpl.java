package org.tdl.vireo.model.repo.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.SubmissionListColumn;
import org.tdl.vireo.model.SubmissionState;
import org.tdl.vireo.model.SubmissionWorkflowStep;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.WorkflowStep;
import org.tdl.vireo.model.repo.OrganizationRepo;
import org.tdl.vireo.model.repo.SubmissionRepo;
import org.tdl.vireo.model.repo.SubmissionStateRepo;
import org.tdl.vireo.model.repo.SubmissionWorkflowStepRepo;
import org.tdl.vireo.model.repo.UserRepo;
import org.tdl.vireo.model.repo.custom.SubmissionRepoCustom;
import org.tdl.vireo.model.repo.specification.SubmissionSpecification;

import edu.tamu.framework.model.Credentials;

public class SubmissionRepoImpl implements SubmissionRepoCustom {

	@Autowired
	private SubmissionRepo submissionRepo;

	@Autowired
	private SubmissionStateRepo submissionStateRepo;

	@Autowired
	private OrganizationRepo organizationRepo;

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private SubmissionWorkflowStepRepo submissionWorkflowStepRepo;

	@Override
	public Submission create(Credentials submitterCredentials, Long organizationId) {

		User submitter = userRepo.findByEmail(submitterCredentials.getEmail());
		// TODO: Instead of being hardcoded this could be dynamic either at the
		// app level, or per organization
		SubmissionState startingState = submissionStateRepo.findByName("In Progress");
		Organization organization = organizationRepo.findOne(organizationId);

		Submission submission = new Submission(submitter, organization);
		submission.setState(startingState);

		// Clone (as SubmissionWorkflowSteps) all the aggregate workflow steps
		// of the requesting org
		for (WorkflowStep aws : organization.getAggregateWorkflowSteps()) {
			SubmissionWorkflowStep submissionWorkflowStep = submissionWorkflowStepRepo.findOrCreate(organization, aws);
			submission.addSubmissionWorkflowStep(submissionWorkflowStep);
		}

		return submissionRepo.save(submission);
	}

	@Override
	public Page<Submission> pageableDynamicSubmissionQuery(List<SubmissionListColumn> submissionListColums, Pageable pageable) {
	    
	    List<Sort.Order> orders = new ArrayList<Sort.Order>();


	    Collections.sort(submissionListColums, new Comparator<SubmissionListColumn>() {
	        @Override
	        public int compare(SubmissionListColumn svc1, SubmissionListColumn svc2) {
	            return svc1.getSortOrder().compareTo(svc2.getSortOrder());
	        }
	    });
	    
	    Boolean filterExists = false;
	    
	    for(SubmissionListColumn submissionListColumn : submissionListColums) {
	        
	        if(!filterExists && submissionListColumn.getFilters().size() > 0) {
	            filterExists = true;
	        }

            if(submissionListColumn.getValuePath().size() > 0) {
                
                String fullPath = String.join(".", submissionListColumn.getValuePath());

                switch(submissionListColumn.getSort()) {
                    case ASC: orders.add(new Sort.Order(Sort.Direction.ASC, fullPath)); break;
                    case DESC: orders.add(new Sort.Order(Sort.Direction.DESC, fullPath)); break;
                    default: break;
                }
            }

        }

        Page<Submission> pageResults;
        
        if(orders.size() > 0) {
            if(filterExists) {
                pageResults = submissionRepo.findAll(new SubmissionSpecification<Submission>(submissionListColums), new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), new Sort(orders)));
            }
            else {
                pageResults = submissionRepo.findAll(new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), new Sort(orders)));
            }
        }
        else {
            pageResults = submissionRepo.findAll(pageable);
        }

		return pageResults;
	}
	
}


