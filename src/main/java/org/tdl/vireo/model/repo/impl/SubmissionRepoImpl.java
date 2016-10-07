package org.tdl.vireo.model.repo.impl;

import static edu.tamu.framework.util.EntityUtility.recursivelyFindField;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.tdl.vireo.model.NamedSearchFilterGroup;
import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.SubmissionListColumn;
import org.tdl.vireo.model.SubmissionState;
import org.tdl.vireo.model.SubmissionWorkflowStep;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.WorkflowStep;
import org.tdl.vireo.model.repo.OrganizationRepo;
import org.tdl.vireo.model.repo.SubmissionListColumnRepo;
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
    private UserRepo userRepo;

    @Autowired
    private SubmissionWorkflowStepRepo submissionWorkflowStepRepo;

    @Autowired
    private SubmissionListColumnRepo submissionListColumnRepo;

    @Override
    public Submission create(Credentials submitterCredentials, Long organizationId) {

        User submitter = userRepo.findByEmail(submitterCredentials.getEmail());

        SubmissionState startingState = submissionStateRepo.findByName("In Progress");

        Organization organization = organizationRepo.findOne(organizationId);

        Submission submission = new Submission(submitter, organization);

        submission.setState(startingState);

        // Clone (as SubmissionWorkflowSteps) all the aggregate workflow steps of the requesting org
        for (WorkflowStep aws : organization.getAggregateWorkflowSteps()) {
            SubmissionWorkflowStep submissionWorkflowStep = submissionWorkflowStepRepo.findOrCreate(organization, aws);
            submission.addSubmissionWorkflowStep(submissionWorkflowStep);
        }

        return submissionRepo.save(submission);
    }
    
    public List<Submission> dynamicSubmissionQuery(Credentials credentials, List<SubmissionListColumn> submissionListColums) {

        User user = userRepo.findByEmail(credentials.getEmail());
        
        Set<String> allColumnSearchFilters = new HashSet<String>();

        NamedSearchFilterGroup activeFilter = user.getActiveFilter();
        
        List<SubmissionListColumn> allSubmissionListColumns = submissionListColumnRepo.findAll();
        
        // set sort and sort order on all submission list columns that are set on users submission list columns
        submissionListColums.forEach(submissionListColumn -> {
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
            activeFilter.getNamedSearchFilters().forEach(namedSearchFilter -> {
                if (namedSearchFilter.getAllColumnSearch()) {
                    allColumnSearchFilters.addAll(namedSearchFilter.getFilterValues());
                } else {
                    for (SubmissionListColumn slc : allSubmissionListColumns) {
                        if (namedSearchFilter.getSubmissionListColumn().equals(slc)) {
                            slc.addAllFilters(namedSearchFilter.getFilterValues());
                            break;
                        }
                    }
                }
            });
        }
        
        
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<Submission> query = cb.createQuery(Submission.class);

        Root<Submission> root = query.from(Submission.class);

        List<Predicate> _groupPredicates = new ArrayList<Predicate>();

        List<Predicate> _filterPredicates = new ArrayList<Predicate>();

        Map<String, Fetch<?, ?>> fetches = new HashMap<String, Fetch<?, ?>>();

        Path<?> fieldValueFetchPath = null;

        for (SubmissionListColumn submissionListColumn : allSubmissionListColumns) {
            Path<?> path = null;
            if (submissionListColumn.getValuePath().size() > 0) {
                if (submissionListColumn.getPredicate() != null) {
                    if (submissionListColumn.getSortOrder() > 0 || submissionListColumn.getFilters().size() > 0 || submissionListColumn.getVisible()) {

                        Join<?, ?> join = (Join<?, ?>) root.fetch("fieldValues", JoinType.LEFT);

                        path = join.get("value");

                        Subquery<Submission> subquery = query.subquery(Submission.class);
                        Root<Submission> subQueryRoot = subquery.from(Submission.class);

                        subquery.select(subQueryRoot.get("id")).distinct(true);
                        subquery.where(cb.equal(subQueryRoot.joinSet("fieldValues", JoinType.LEFT).get("fieldPredicate").get("value"), submissionListColumn.getPredicate()));

                        _groupPredicates.add(cb.or(cb.equal(join.get("fieldPredicate").get("value"), submissionListColumn.getPredicate()), root.get("id").in(subquery).not()));

                    }
                } else {

                    String navPath = null;

                    for (String property : submissionListColumn.getValuePath()) {
                        if (path == null) {
                            navPath = property;
                            if (requiresFetch(root, property)) {
                                Fetch<?, ?> fetch = fetches.get(navPath);
                                if (fetch == null) {
                                    fetch = root.fetch(property, JoinType.LEFT);
                                    fetches.put(navPath, fetch);
                                }
                                path = (Path<?>) fetch;
                            } else {
                                path = root.get(property);
                            }
                        } else {
                            navPath += "." + property;
                            if (requiresFetch(path, property)) {
                                Fetch<?, ?> fetch = fetches.get(navPath);
                                if (fetch == null) {
                                    fetch = ((Fetch<?, ?>) path).fetch(property, JoinType.LEFT);
                                    fetches.put(navPath, fetch);
                                }
                                path = (Path<?>) fetch;
                            } else {
                                path = path.get(property);
                            }
                        }
                    }

                }

                for (String filter : submissionListColumn.getFilters()) {
                    _filterPredicates.add(cb.like(cb.lower(path.as(String.class)), "%" + filter.toLowerCase() + "%"));
                }

                for (String filter : allColumnSearchFilters) {
                    if (submissionListColumn.getPredicate() != null) {
                        if (fieldValueFetchPath == null) {
                            fieldValueFetchPath = (Path<?>) root.fetch("fieldValues");
                        }
                        _filterPredicates.add(cb.like(cb.lower(fieldValueFetchPath.get("value").as(String.class)), "%" + filter.toLowerCase() + "%"));
                    } else {
                        _filterPredicates.add(cb.like(cb.lower(path.as(String.class)), "%" + filter.toLowerCase() + "%"));
                    }
                }

            }
        }

        Predicate predicate = null;

        if (_filterPredicates.size() == 0) {
            predicate = cb.and(_groupPredicates.toArray(new Predicate[_groupPredicates.size()]));
        } else {
            predicate = cb.and(cb.and(_groupPredicates.toArray(new Predicate[_groupPredicates.size()])), cb.or(_filterPredicates.toArray(new Predicate[_filterPredicates.size()])));
        }

        query.select(root).distinct(true).where(predicate);

        TypedQuery<Submission> typedQuery = em.createQuery(query);

        System.out.println("\n" + typedQuery.unwrap(Query.class).getQueryString() + "\n");

        return typedQuery.getResultList();
        
    }

    @Override
    public Page<Submission> pageableDynamicSubmissionQuery(Credentials credentials, List<SubmissionListColumn> submissionListColums, Pageable pageable) {

        User user = userRepo.findByEmail(credentials.getEmail());

        Set<String> allColumnSearchFilters = new HashSet<String>();

        NamedSearchFilterGroup activeFilter = user.getActiveFilter();

        List<SubmissionListColumn> allSubmissionListColumns = submissionListColumnRepo.findAll();

        // set sort and sort order on all submission list columns that are set on users submission list columns
        submissionListColums.forEach(submissionListColumn -> {
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
            activeFilter.getNamedSearchFilters().forEach(filterCriterion -> {
                if (filterCriterion.getAllColumnSearch()) {
                    allColumnSearchFilters.addAll(filterCriterion.getFilterValues());
                } else {
                    for (SubmissionListColumn slc : allSubmissionListColumns) {
                        if (filterCriterion.getSubmissionListColumn().equals(slc)) {
                            slc.addAllFilters(filterCriterion.getFilterValues());
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

                CriteriaBuilder cb = em.getCriteriaBuilder();

                CriteriaQuery<Submission> query = cb.createQuery(Submission.class);

                Root<Submission> root = query.from(Submission.class);

                List<Order> _orders = new ArrayList<Order>();

                List<Predicate> _groupPredicates = new ArrayList<Predicate>();

                List<Predicate> _filterPredicates = new ArrayList<Predicate>();

                Map<String, Fetch<?, ?>> fetches = new HashMap<String, Fetch<?, ?>>();

                Path<?> fieldValueFetchPath = null;

                for (SubmissionListColumn submissionListColumn : allSubmissionListColumns) {
                    Path<?> path = null;
                    if (submissionListColumn.getValuePath().size() > 0) {
                        if (submissionListColumn.getPredicate() != null) {
                            if (submissionListColumn.getSortOrder() > 0 || submissionListColumn.getFilters().size() > 0 || submissionListColumn.getVisible()) {

                                Join<?, ?> join = (Join<?, ?>) root.fetch("fieldValues", JoinType.LEFT);

                                path = join.get("value");

                                Subquery<Submission> subquery = query.subquery(Submission.class);
                                Root<Submission> subQueryRoot = subquery.from(Submission.class);

                                subquery.select(subQueryRoot.get("id")).distinct(true);
                                subquery.where(cb.equal(subQueryRoot.joinSet("fieldValues", JoinType.LEFT).get("fieldPredicate").get("value"), submissionListColumn.getPredicate()));

                                _groupPredicates.add(cb.or(cb.equal(join.get("fieldPredicate").get("value"), submissionListColumn.getPredicate()), root.get("id").in(subquery).not()));

                            }
                        } else {

                            String navPath = null;

                            for (String property : submissionListColumn.getValuePath()) {
                                if (path == null) {
                                    navPath = property;
                                    if (requiresFetch(root, property)) {
                                        Fetch<?, ?> fetch = fetches.get(navPath);
                                        if (fetch == null) {
                                            fetch = root.fetch(property, JoinType.LEFT);
                                            fetches.put(navPath, fetch);
                                        }
                                        path = (Path<?>) fetch;
                                    } else {
                                        path = root.get(property);
                                    }
                                } else {
                                    navPath += "." + property;
                                    if (requiresFetch(path, property)) {
                                        Fetch<?, ?> fetch = fetches.get(navPath);
                                        if (fetch == null) {
                                            fetch = ((Fetch<?, ?>) path).fetch(property, JoinType.LEFT);
                                            fetches.put(navPath, fetch);
                                        }
                                        path = (Path<?>) fetch;
                                    } else {
                                        path = path.get(property);
                                    }
                                }
                            }

                        }

                        for (String filter : submissionListColumn.getFilters()) {
                            _filterPredicates.add(cb.like(cb.lower(path.as(String.class)), "%" + filter.toLowerCase() + "%"));
                        }

                        for (String filter : allColumnSearchFilters) {
                            if (submissionListColumn.getPredicate() != null) {
                                if (fieldValueFetchPath == null) {
                                    fieldValueFetchPath = (Path<?>) root.fetch("fieldValues");
                                }
                                _filterPredicates.add(cb.like(cb.lower(fieldValueFetchPath.get("value").as(String.class)), "%" + filter.toLowerCase() + "%"));
                            } else {
                                _filterPredicates.add(cb.like(cb.lower(path.as(String.class)), "%" + filter.toLowerCase() + "%"));
                            }
                        }

                        switch (submissionListColumn.getSort()) {
                        case ASC:
                            _orders.add(cb.asc(path));
                            break;
                        case DESC:
                            _orders.add(cb.desc(path));
                            break;
                        default:
                            break;
                        }
                    }
                }

                Predicate predicate = null;

                if (_filterPredicates.size() == 0) {
                    predicate = cb.and(_groupPredicates.toArray(new Predicate[_groupPredicates.size()]));
                } else {
                    predicate = cb.and(cb.and(_groupPredicates.toArray(new Predicate[_groupPredicates.size()])), cb.or(_filterPredicates.toArray(new Predicate[_filterPredicates.size()])));
                }

                query.select(root).distinct(true).where(predicate).orderBy(_orders);

                TypedQuery<Submission> typedQuery = em.createQuery(query);

                System.out.println("\n" + typedQuery.unwrap(Query.class).getQueryString() + "\n");

                long total = typedQuery.getResultList().size();
                typedQuery.setFirstResult(pageable.getOffset());
                typedQuery.setMaxResults(pageable.getPageSize());

                pageResults = new PageImpl<Submission>(typedQuery.getResultList(), pageable, total);

                // pageResults = submissionRepo.findAll(new SubmissionSpecification<Submission>(allSubmissionListColumns, allColumnSearchFilters), new PageRequest(pageable.getPageNumber(), pageable.getPageSize()));
            } else {
                pageResults = submissionRepo.findAll(new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), new Sort(orders)));
            }
        } else {
            pageResults = submissionRepo.findAll(pageable);
        }

        return pageResults;
    }

    private Boolean requiresFetch(Path<?> path, String property) {
        Field field = recursivelyFindField(path.getJavaType(), property);
        Boolean reqFetch = true;
        for (Annotation annotation : field.getAnnotations()) {
            if (annotation instanceof Column || annotation instanceof Id) {
                reqFetch = false;
            }
        }
        return reqFetch;
    }

}
