package org.tdl.vireo.model.repo.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.tdl.vireo.model.FieldProfile;
import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.SubmissionListColumn;
import org.tdl.vireo.model.SubmissionState;
import org.tdl.vireo.model.SubmissionWorkflowStep;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.WorkflowStep;
import org.tdl.vireo.model.repo.FieldValueRepo;
import org.tdl.vireo.model.repo.OrganizationRepo;
import org.tdl.vireo.model.repo.SubmissionRepo;
import org.tdl.vireo.model.repo.SubmissionStateRepo;
import org.tdl.vireo.model.repo.SubmissionWorkflowStepRepo;
import org.tdl.vireo.model.repo.UserRepo;
import org.tdl.vireo.model.repo.custom.SubmissionRepoCustom;

import edu.tamu.framework.model.Credentials;

public class SubmissionRepoImpl implements SubmissionRepoCustom {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private SubmissionRepo submissionRepo;

    @Autowired
    private SubmissionStateRepo submissionStateRepo;

    @Autowired
    private OrganizationRepo organizationRepo;

    @Autowired
    private FieldValueRepo fieldValueRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private SubmissionWorkflowStepRepo submissionWorkflowStepRepo;

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
            
            for(FieldProfile fp : aws.getAggregateFieldProfiles()) {
                submission.addFieldValue(fieldValueRepo.create(fp.getFieldPredicate()));
            }
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
        Boolean predicateExists = false;

        for (SubmissionListColumn submissionListColumn : submissionListColums) {

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

        Page<Submission> pageResults = null;

        if (orders.size() > 0) {
            if (filterExists || predicateExists) {

                List<Order> _orders = new ArrayList<Order>();
                
                List<Expression<?>> _groupings = new ArrayList<Expression<?>>();

                Predicate combinedPrediacte = null;

                CriteriaBuilder cb = em.getCriteriaBuilder();

                CriteriaQuery<Submission> query = cb.createQuery(Submission.class);

                Root<Submission> root = query.from(Submission.class);

                Join<?, ?> join = root.join("fieldValues", JoinType.LEFT);

                for (SubmissionListColumn submissionListColumn : submissionListColums) {
                    Path<?> path = null;

                    if (submissionListColumn.getValuePath().size() > 0) {

                        System.out.println(submissionListColumn.getValuePath());

                        if (submissionListColumn.getPredicate() != null) {

                            path = join.get("value");

                            Path<?> predicatePath = join.get("fieldPredicate").get("value");

                            combinedPrediacte = cb.and(cb.equal(predicatePath, submissionListColumn.getPredicate()));

                            if (submissionListColumn.getSortOrder() > 0) {
                                _groupings.add(predicatePath);
                            }
                        } else {

                            for (String property : submissionListColumn.getValuePath()) {
                                if (path == null) {
                                    path = root.get(property);
                                } else {
                                    path = path.get(property);
                                }
                            }

                            combinedPrediacte = cb.and(cb.isNotNull(path));
                        }

                        for (String filter : submissionListColumn.getFilters()) {
                            combinedPrediacte = cb.and(cb.equal(path, filter));
                        }

                        switch (submissionListColumn.getSort()) {
                            case ASC: _orders.add(cb.asc(path)); break;
                            case DESC: _orders.add(cb.desc(path)); break;
                            default: break;
                        }

                    }
                }

                _groupings.add(root.get("id"));

                query.select(root).where(combinedPrediacte).groupBy(_groupings).orderBy(_orders);

                TypedQuery<Submission> typedQuery = em.createQuery(query);
                typedQuery.setFirstResult(pageable.getOffset());
                typedQuery.setMaxResults(pageable.getPageSize());

                System.out.println("\n\n" + typedQuery.unwrap(Query.class).getQueryString() + "\n\n");

                pageResults = new PageImpl<Submission>(typedQuery.getResultList());

            } else {
                pageResults = submissionRepo.findAll(new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), new Sort(orders)));
            }
        } else {
            pageResults = submissionRepo.findAll(pageable);
        }

        return pageResults;
    }

}
