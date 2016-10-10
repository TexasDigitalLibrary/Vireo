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
import javax.sql.DataSource;

import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.tdl.vireo.model.NamedSearchFilter;
import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.SubmissionListColumn;
import org.tdl.vireo.model.SubmissionState;
import org.tdl.vireo.model.SubmissionWorkflowStep;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.WorkflowStep;
import org.tdl.vireo.model.repo.FieldPredicateRepo;
import org.tdl.vireo.model.repo.OrganizationRepo;
import org.tdl.vireo.model.repo.SubmissionListColumnRepo;
import org.tdl.vireo.model.repo.SubmissionRepo;
import org.tdl.vireo.model.repo.SubmissionStateRepo;
import org.tdl.vireo.model.repo.SubmissionWorkflowStepRepo;
import org.tdl.vireo.model.repo.UserRepo;
import org.tdl.vireo.model.repo.custom.SubmissionRepoCustom;

import edu.tamu.framework.model.Credentials;

public class SubmissionRepoImpl implements SubmissionRepoCustom {

	JdbcTemplate jdbcTemplate = new JdbcTemplate(SubmissionRepoImpl.getDataSource());
	
    @PersistenceContext
    private EntityManager em;

    @Autowired
    private SubmissionRepo submissionRepo;

    @Autowired
    private SubmissionStateRepo submissionStateRepo;
    
    @Autowired FieldPredicateRepo fieldPredicateRepo;

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

        NamedSearchFilter activeFilter = user.getActiveFilter();
        
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
    	String sqlSelect = "SELECT DISTINCT s.id,";
    	String sqlCountSelect = "SELECT COUNT(*) FROM submission s ";
    	String sqlJoins = "";
    	String sqlWheres = "";
    	String sqlOrderBys = "";
    	
    	//get the order-by columns by which to sort the query results
//    	Sort sort = pageable.getSort();
//    	Iterator<org.springframework.data.domain.Sort.Order> orders1 = sort.iterator();
//    	String orderByString = "ORDER BY ";
//    	while(orders1.hasNext())
//    	{
//    		String orderByColumnNameString = orders1.next().getProperty();
//    		orderByString += orderByColumnNameString;
//    		if(orders1.hasNext()) orderByString += ", ";
//    	}
    	
    	
    	
    	
    	//determine the requesting user
    	User user = userRepo.findByEmail(credentials.getEmail());
    	
    	//set up storage for user's preferred columns
        Set<String> allColumnSearchFilters = new HashSet<String>();
        
        //get the user's active filter
        NamedSearchFilter activeFilter = user.getActiveFilter();

        //get all the possible columns, some of which we will make visible
        List<SubmissionListColumn> allSubmissionListColumns = submissionListColumnRepo.findAll();

        // set sort and sort order on all submission list columns that are set on the requesting user's submission list columns
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
            	System.out.println("The filter for submissionListColumn " + submissionListColumn.getTitle() + " exists.");
                filterExists = true;
            }

            if (submissionListColumn.getSort() != org.tdl.vireo.enums.Sort.NONE && submissionListColumn.getPredicate() != null) {
            	System.out.println("The predicate for submissionListColumn " + submissionListColumn.getTitle() + " exists.");
                predicateExists = true;
            }

            if (submissionListColumn.getValuePath().size() > 0) {
                String fullPath = String.join(".", submissionListColumn.getValuePath());
                switch (submissionListColumn.getSort()) {
                case ASC:
                	System.out.println("Adding an ascending order for fullPath " + fullPath);
                    orders.add(new Sort.Order(Sort.Direction.ASC, fullPath));
                    break;
                case DESC:
                	System.out.println("Adding a descending order for fullPath " + fullPath);
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
        List<Submission> pageSubmissions = new ArrayList<Submission>();

        if (filterExists || orders.size() > 0) {
        	System.out.println("A filter exists, or there is an order.");
            if (filterExists || predicateExists) {
            	System.out.println("A filter exists, or a predicate exists.");

                CriteriaBuilder cb = em.getCriteriaBuilder();

                CriteriaQuery<Submission> query = cb.createQuery(Submission.class);

                Root<Submission> root = query.from(Submission.class);

                List<Order> _orders = new ArrayList<Order>();

                List<Predicate> _groupPredicates = new ArrayList<Predicate>();

                List<Predicate> _filterPredicates = new ArrayList<Predicate>();

                Map<String, Fetch<?, ?>> fetches = new HashMap<String, Fetch<?, ?>>();

                Path<?> fieldValueFetchPath = null;
                
                int n = 0;

                for (SubmissionListColumn submissionListColumn : allSubmissionListColumns) {
                    Path<?> path = null;
                    
                    
                    
                    System.out.println("submissionListColumn has valuePath of " + String.join(".", submissionListColumn.getValuePath()));
                    
                    
                    //generate the join columns for each of the possible field values
                    if(String.join(".", submissionListColumn.getValuePath()).equals("fieldValues.value") && submissionListColumn.getPredicate() != null)
                    {
                    	n++;
                    	
                    	System.out.println("Predicate value is " + submissionListColumn.getPredicate());
                    	
                    	Long predicateId = fieldPredicateRepo.findByValue(submissionListColumn.getPredicate()).getId();
                    	
                    	System.out.println("Predicate has id " + predicateId);
                    			
                    	sqlJoins += "\nLEFT JOIN" + 
                    				"\n	(SELECT sfv"+n+".submission_id, fv"+n+".*" +
                    				"\n	 FROM submission_field_values sfv"+n +
                    				"\n	 LEFT JOIN field_value fv"+n+" ON fv"+n+".id=sfv"+n+".field_values_id " +
                    				"\n	 WHERE fv"+n+".field_predicate_id="+predicateId+") pfv"+n +
                    				"\n	ON pfv"+n+".submission_id=s.id";
                    	
                    	//generate the where clauses for each filter on the field values
                    	if(submissionListColumn.getSortOrder() > 0)
                    	{
		                	switch (submissionListColumn.getSort()) {
		                    case ASC:
		                    	System.out.println("Ordering ascending on predicate "+ submissionListColumn.getPredicate());
		                    	sqlSelect += " pfv"+n+".value,";
		                    	sqlOrderBys += " pfv"+n+".value ASC,";
		                        break;
		                    case DESC:
		                    	System.out.println("Ordering descending on predicate "+ submissionListColumn.getPredicate());
		                    	sqlSelect += " pfv"+n+".value,";
		                    	sqlOrderBys += " pfv"+n+".value DESC,";
		                        break;
		                    default:
		                        break;
		                    }
                    	}
                    	
                    	//TODO:
                    	sqlWheres += "";
                    	
                    }
                    else {
                    	System.out.println("No value path given for submissionListColumn " + submissionListColumn.getTitle());
                    }
                }
                
                //finish out the select clause
                sqlSelect = sqlSelect.substring(0, sqlSelect.length()-1) + " FROM submission s";
                
                //if ordering, finish the clause and strip the tailing comma
                if( !sqlOrderBys.isEmpty()) {
                	sqlOrderBys = "\nORDER BY" + sqlOrderBys.substring(0, sqlOrderBys.length()-1);
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
            	System.out.println("Neither a filter nor predicate exists.");
                pageResults = submissionRepo.findAll(new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), new Sort(orders)));
            }
        } else {
        	System.out.println("Neither a filter nor order exists.");
            pageResults = submissionRepo.findAll(pageable);
        }
        
        
        
        
        String sqlCountQuery = sqlCountSelect + sqlJoins + sqlWheres;
    	
    	Long total = this.jdbcTemplate.queryForObject(sqlCountQuery, Long.class);
    	
    	System.out.println("Returning " + total + " submissions.");
    	
    	
        
        //determine the offset and limit of the query
    	int offset = pageable.getPageSize() * pageable.getPageNumber();
    	int limit = pageable.getPageSize();
    	
    	String sqlQueryLimitAndOffset = "\nLIMIT " + limit + " OFFSET " + offset + ";";
    	String sqlQuery = sqlSelect + sqlJoins + sqlWheres + sqlOrderBys + sqlQueryLimitAndOffset;
    	
    	
    	System.out.println("QUERY:\n" + sqlQuery);
    	
    	
    	
    	List<Long> ids = new ArrayList<Long>();
    	
    	List<Map<String, Object>> rows = this.jdbcTemplate.queryForList(sqlQuery);
    	
    	for (Map row : rows) {
    		ids.add((Long) row.get("ID"));
    		System.out.println("Adding submission " + row.get("ID"));

    	}
    	
    	
    	
    	
    	List<Submission> actualResults = new ArrayList<Submission>();
    	
    	
    	for(Long id : ids) {
    		for(Submission ps : submissionRepo.findAll(ids)) {
    			if(ps.getId().equals(id)) {
    				actualResults.add(ps);
    			}
	    	}
    	}
    	
    	
    	
    	
    	
    	//TODO:  custom Page implementation that does the object mapping
    	
    	pageResults = new PageImpl<Submission>(actualResults, new PageRequest((int) Math.floor(offset/limit), limit), total);
    	
    	
    	System.out.println("Total element " + pageResults.getTotalElements());
    	
    	System.out.println("Total pages " + pageResults.getTotalPages());
    	
    	System.out.println("Offset " + offset);
    	
    	System.out.println("Page size " + limit);
    	

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
    
    public class PageableSubmissions implements Pageable {

		@Override
		public int getPageNumber() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getPageSize() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getOffset() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public Sort getSort() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Pageable next() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Pageable previousOrFirst() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Pageable first() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean hasPrevious() {
			// TODO Auto-generated method stub
			return false;
		}
    	
    }
    
    public static DataSource getDataSource() {
    	//TODO:  get values from config, and get this code out of this repo
    	DriverManagerDataSource dataSource = new org.springframework.jdbc.datasource.DriverManagerDataSource();
    	dataSource.setDriverClassName("org.postgresql.Driver");
    	dataSource.setUrl("jdbc:postgresql://localhost:5432/vireo");
    	dataSource.setUsername("vireo");
    	dataSource.setPassword("vireo");
    	return dataSource;
    }

}
