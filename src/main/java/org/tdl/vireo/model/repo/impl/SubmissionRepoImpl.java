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
import org.tdl.vireo.model.FieldProfile;
import org.tdl.vireo.model.NamedSearchFilter;
import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.SubmissionListColumn;
import org.tdl.vireo.model.SubmissionState;
import org.tdl.vireo.model.SubmissionWorkflowStep;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.WorkflowStep;
import org.tdl.vireo.model.repo.FieldValueRepo;
import org.tdl.vireo.model.repo.OrganizationRepo;
import org.tdl.vireo.model.repo.SubmissionListColumnRepo;
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
    private FieldValueRepo fieldValueRepo;

    @Autowired
    private SubmissionStateRepo submissionStateRepo;

    @Autowired
    private OrganizationRepo organizationRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private SubmissionWorkflowStepRepo submissionWorkflowStepRepo;
    
    @Autowired
    private SubmissionListColumnRepo submissionListColumnRepo;
    
    @Override
    public Submission create(Credentials submitterCredentials, Long organizationId) {

        User submitter = userRepo.findByEmail(submitterCredentials.getEmail());

        // TODO: Instead of being hardcoded this could be dynamic either at the app level, or per organization
        SubmissionState startingState = submissionStateRepo.findByName("In Progress");
        Organization organization = organizationRepo.findOne(organizationId);

        Submission submission = new Submission(submitter, organization);
        submission.setState(startingState);

        // Clone (as SubmissionWorkflowSteps) all the aggregate workflow steps of the requesting org
        for (WorkflowStep aws : organization.getAggregateWorkflowSteps()) {
            SubmissionWorkflowStep submissionWorkflowStep = submissionWorkflowStepRepo.findOrCreate(organization, aws);
            submission.addSubmissionWorkflowStep(submissionWorkflowStep);

            // pre populate field values for all field profiles in aggregate workflow
            // this is undesirable but currently only way to get dynamic query to function correctly
            for (FieldProfile fp : aws.getAggregateFieldProfiles()) {
                submission.addFieldValue(fieldValueRepo.create(fp.getFieldPredicate()));
            }
        }

        return submissionRepo.save(submission);
    }
    
    // TODO: determine how to handle date range on filter

    @Override
    public Page<Submission> pageableDynamicSubmissionQuery(Credentials credentials, List<SubmissionListColumn> submissionListColums, Pageable pageable) {

        User user = userRepo.findByEmail(credentials.getEmail());
        
        List<String> fullSearchFilters = new ArrayList<String>();
        
        List<NamedSearchFilter> activeFilters = user.getActiveFilters();
        
        List<SubmissionListColumn> allSubmissionListColums = submissionListColumnRepo.findAll();
        
        // set sort and sort order on all submission list columns that are set on users submission list columns
        submissionListColums.forEach(submissionListColumn -> {
            for(SubmissionListColumn slc : allSubmissionListColums) {
                if(submissionListColumn.equals(slc)) {
                    slc.setSort(submissionListColumn.getSort());
                    slc.setSortOrder(submissionListColumn.getSortOrder());
                    break;
                }
            }
        });
        
        // add filters to columns that are active on user
        activeFilters.forEach(activeFilter -> {
            for(SubmissionListColumn slc : allSubmissionListColums) {
                if(activeFilter.getSubmissionListColumn().equals(slc)) {
                    slc.addFilter(activeFilter.getValue());
                    break;
                }
            }
            if(activeFilter.getFullSearch()) {
                fullSearchFilters.add(activeFilter.getValue());
            }
        });
        
        // sort all submission list columns by sort order provided by users submission list columns
        Collections.sort(allSubmissionListColums, new Comparator<SubmissionListColumn>() {
            @Override
            public int compare(SubmissionListColumn svc1, SubmissionListColumn svc2) {
                return svc1.getSortOrder().compareTo(svc2.getSortOrder());
            }
        });

        Boolean filterExists = false;
        Boolean predicateExists = false;
        
        List<Sort.Order> orders = new ArrayList<Sort.Order>();

        for (SubmissionListColumn submissionListColumn : allSubmissionListColums) {

            if (submissionListColumn.getFilters().size() > 0) {
                filterExists = true;
            }

            if (submissionListColumn.getSort() != org.tdl.vireo.enums.Sort.NONE && submissionListColumn.getPredicate() != null) {
                predicateExists = true;
            }

            if (submissionListColumn.getValuePath().size() > 0) {
                String fullPath = String.join(".", submissionListColumn.getValuePath());
                switch (submissionListColumn.getSort()) {
                    case ASC: orders.add(new Sort.Order(Sort.Direction.ASC, fullPath)); break;
                    case DESC: orders.add(new Sort.Order(Sort.Direction.DESC, fullPath)); break;
                    default: break;
                }
            }
        }
        
        if(fullSearchFilters.size() > 0) {
            filterExists = true;
        }

        Page<Submission> pageResults = null;

        if (filterExists || orders.size() > 0) {
            if (filterExists || predicateExists) {
                pageResults = submissionRepo.findAll(new SubmissionSpecification<Submission>(submissionListColums, fullSearchFilters), new PageRequest(pageable.getPageNumber(), pageable.getPageSize()));
            } else {
                pageResults = submissionRepo.findAll(new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), new Sort(orders)));
            }
        } else {
            pageResults = submissionRepo.findAll(pageable);
        }

        return pageResults;
    }

}
