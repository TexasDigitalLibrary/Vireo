package org.tdl.vireo.model.repo.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.tdl.vireo.model.NamedSearchFilter;
import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.SubmissionFieldProfile;
import org.tdl.vireo.model.SubmissionListColumn;
import org.tdl.vireo.model.SubmissionState;
import org.tdl.vireo.model.SubmissionWorkflowStep;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.FieldValueRepo;
import org.tdl.vireo.model.repo.SubmissionListColumnRepo;
import org.tdl.vireo.model.repo.SubmissionRepo;
import org.tdl.vireo.model.repo.SubmissionWorkflowStepRepo;
import org.tdl.vireo.model.repo.UserRepo;
import org.tdl.vireo.model.repo.custom.SubmissionRepoCustom;
import org.tdl.vireo.model.repo.specification.SubmissionSpecification;

import edu.tamu.framework.model.Credentials;

public class SubmissionRepoImpl implements SubmissionRepoCustom {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private SubmissionRepo submissionRepo;

    @Autowired
    private FieldValueRepo fieldValueRepo;

    @Autowired
    private SubmissionWorkflowStepRepo submissionWorkflowStepRepo;

    @Autowired
    private SubmissionListColumnRepo submissionListColumnRepo;

    @Override
    public Submission create(User submitter, Organization organization, SubmissionState startingState) {

        Submission submission = new Submission(submitter, organization, startingState);
        
        submission.setSubmissionWorkflowSteps(submissionWorkflowStepRepo.cloneWorkflow(organization));
        
        for(SubmissionWorkflowStep sws : submission.getSubmissionWorkflowSteps()) {
            // Pre-populate field values for dynamic query to function correctly
            for(SubmissionFieldProfile sfp : sws.getAggregateFieldProfiles()) {
                submission.addFieldValue(fieldValueRepo.create(sfp.getFieldPredicate()));
            }
        }

        return submissionRepo.save(submission);
    }

    public List<Submission> dynamicSubmissionQuery(Credentials credentials) {

        Set<String> allColumnSearchFilters = new HashSet<String>();

        User user = userRepo.findByEmail(credentials.getEmail());

        List<SubmissionListColumn> submissionListColumns = user.getSubmissionViewColumns();

        NamedSearchFilter activeFilter = user.getActiveFilter();

        List<SubmissionListColumn> allSubmissionListColumns = submissionListColumnRepo.findAll();

        // set sort and sort order on all submission list columns that are set on users submission list columns
        submissionListColumns.parallelStream().forEach(submissionListColumn -> {
            for (SubmissionListColumn slc : allSubmissionListColumns) {
                if (submissionListColumn.equals(slc)) {
                    slc.setVisible(true);
                    slc.setSort(submissionListColumn.getSort());
                    slc.setSortOrder(submissionListColumn.getSortOrder());
                    break;
                }
            }
        });

        // add column filters to SubmissionListColumns, add all column filters to allColumnSearchFilters
        if (activeFilter != null) {
            activeFilter.getFilterCriteria().parallelStream().forEach(filterCriterion -> {
                if (filterCriterion.getAllColumnSearch()) {
                    allColumnSearchFilters.addAll(filterCriterion.getFilters());
                } else {
                    for (SubmissionListColumn slc : allSubmissionListColumns) {
                        if (filterCriterion.getSubmissionListColumn().equals(slc)) {
                            slc.addAllFilters(filterCriterion.getFilters());
                            break;
                        }
                    }
                }
            });
        }

        return submissionRepo.findAll(new SubmissionSpecification<Submission>(allSubmissionListColumns, allColumnSearchFilters));
    }

    @Override
    public Page<Submission> pageableDynamicSubmissionQuery(Credentials credentials, List<SubmissionListColumn> submissionListColumns, Pageable pageable) {
        
        Set<String> allColumnSearchFilters = new HashSet<String>();

        User user = userRepo.findByEmail(credentials.getEmail());

        NamedSearchFilter activeFilter = user.getActiveFilter();

        List<SubmissionListColumn> allSubmissionListColumns = submissionListColumnRepo.findAll();

        // set sort and sort order on all submission list columns that are set on users submission list columns
        submissionListColumns.forEach(submissionListColumn -> {
            for (SubmissionListColumn slc : allSubmissionListColumns) {
                if (submissionListColumn.equals(slc)) {
                    slc.setVisible(true);
                    slc.setSort(submissionListColumn.getSort());
                    slc.setSortOrder(submissionListColumn.getSortOrder());
                    break;
                }
            }
        });

        // add column filters to SubmissionListColumns, add all column filters to allColumnSearchFilters
        if (activeFilter != null) {
            activeFilter.getFilterCriteria().forEach(filterCriterion -> {
                if (filterCriterion.getAllColumnSearch()) {
                    allColumnSearchFilters.addAll(filterCriterion.getFilters());
                } else {
                    for (SubmissionListColumn slc : allSubmissionListColumns) {
                        if (filterCriterion.getSubmissionListColumn().equals(slc)) {
                            slc.addAllFilters(filterCriterion.getFilters());
                            break;
                        }
                    }
                }
            });
        }

        // sort all submission list columns by sort order provided by users submission list columns
        Collections.sort(allSubmissionListColumns, new Comparator<SubmissionListColumn>() {
            @Override
            public int compare(SubmissionListColumn svc1, SubmissionListColumn svc2) {
                return svc1.getSortOrder().compareTo(svc2.getSortOrder());
            }
        });

        Boolean filterExists = false;
        Boolean predicateExists = false;

        List<Sort.Order> orders = new ArrayList<Sort.Order>();

        for (SubmissionListColumn submissionListColumn : allSubmissionListColumns) {

            if (submissionListColumn.getFilters().size() > 0) {
                filterExists = true;
            }

            if (submissionListColumn.getSort() != org.tdl.vireo.enums.Sort.NONE && submissionListColumn.getPredicate() != null) {
                predicateExists = true;
            }

            if (submissionListColumn.getValuePath().size() > 0) {
                String fullPath = String.join(".", submissionListColumn.getValuePath());
                switch (submissionListColumn.getSort()) {
                case ASC:
                    orders.add(new Sort.Order(Sort.Direction.ASC, fullPath));
                    break;
                case DESC:
                    orders.add(new Sort.Order(Sort.Direction.DESC, fullPath));
                    break;
                default:
                    break;
                }
            }
        }

        if (!filterExists && !allColumnSearchFilters.isEmpty()) {
            filterExists = true;
        }

        Page<Submission> pageResults = null;

        if (filterExists || orders.size() > 0) {
            if (filterExists || predicateExists) {
                pageResults = submissionRepo.findAll(new SubmissionSpecification<Submission>(allSubmissionListColumns, allColumnSearchFilters), new PageRequest(pageable.getPageNumber(), pageable.getPageSize()));
            } else {
                pageResults = submissionRepo.findAll(new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), new Sort(orders)));
            }
        } else {
            pageResults = submissionRepo.findAll(pageable);
        }

        return pageResults;
    }

}
