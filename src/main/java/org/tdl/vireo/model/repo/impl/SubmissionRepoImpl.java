package org.tdl.vireo.model.repo.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.tdl.vireo.enums.Sort;
import org.tdl.vireo.model.NamedSearchFilterGroup;
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
import org.tdl.vireo.model.repo.custom.SubmissionRepoCustom;

public class SubmissionRepoImpl implements SubmissionRepoCustom {

    @Autowired
    private SubmissionRepo submissionRepo;

    @Autowired
    private SubmissionStateRepo submissionStateRepo;

    @Autowired
    FieldPredicateRepo fieldPredicateRepo;

    @Autowired
    private OrganizationRepo organizationRepo;

    @Autowired
    private SubmissionWorkflowStepRepo submissionWorkflowStepRepo;

    @Autowired
    private SubmissionListColumnRepo submissionListColumnRepo;

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public SubmissionRepoImpl(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Submission create(User submitter, Long organizationId) {
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

    @Override
    public List<Submission> batchDynamicSubmissionQuery(NamedSearchFilterGroup activeFilter, List<SubmissionListColumn> submissionListColums) {
        String[] queryStrings = craftDynamicSubmissionQuery(activeFilter, submissionListColums, null);
        List<Long> ids = new ArrayList<Long>();
        this.jdbcTemplate.queryForList(queryStrings[0]).forEach(row -> {
            ids.add((Long) row.get("ID"));
        });
        return submissionRepo.findAll(ids);
    }

    @Override
    public Page<Submission> pageableDynamicSubmissionQuery(NamedSearchFilterGroup activeFilter, List<SubmissionListColumn> submissionListColums, Pageable pageable) {
        String[] queryStrings = craftDynamicSubmissionQuery(activeFilter, submissionListColums, pageable);
        List<Long> ids = new ArrayList<Long>();
        this.jdbcTemplate.queryForList(queryStrings[0]).forEach(row -> {
            ids.add((Long) row.get("ID"));
        });
        List<Submission> actualResults = new ArrayList<Submission>();
        
        // order them
        for (Long id : ids) {
            for (Submission ps : submissionRepo.findAll(ids)) {
                if (ps.getId().equals(id)) {
                    actualResults.add(ps);
                }
            }
        }

        Long total = this.jdbcTemplate.queryForObject(queryStrings[1], Long.class);
        int offset = pageable.getPageSize() * pageable.getPageNumber();
        int limit = pageable.getPageSize();
        return new PageImpl<Submission>(actualResults, new PageRequest((int) Math.floor(offset / limit), limit), total);
    }

    private String[] craftDynamicSubmissionQuery(NamedSearchFilterGroup activeFilter, List<SubmissionListColumn> submissionListColums, Pageable pageable) {

        // set up storage for user's preferred columns
        Set<String> allColumnSearchFilters = new HashSet<String>();

        // get all the possible columns, some of which we will make visible
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

        // sort all submission list columns by sort order provided by users submission list columns
        Collections.sort(allSubmissionListColumns, new Comparator<SubmissionListColumn>() {
            @Override
            public int compare(SubmissionListColumn svc1, SubmissionListColumn svc2) {
                return svc1.getSortOrder().compareTo(svc2.getSortOrder());
            }
        });

        
        StringBuilder sqlSelectBuilder = new StringBuilder("SELECT DISTINCT s.id,");
        
        StringBuilder sqlCountSelectBuilder = new StringBuilder("SELECT COUNT(*) FROM submission s ");
        
        StringBuilder sqlJoinsBuilder = new StringBuilder();
        StringBuilder sqlWheresBuilder = new StringBuilder();
        StringBuilder sqlOrderBysBuilder = new StringBuilder();

        int n = 0;

        for (SubmissionListColumn submissionListColumn : allSubmissionListColumns) {

            if (submissionListColumn.getSortOrder() > 0 || submissionListColumn.getFilters().size() > 0 || submissionListColumn.getVisible()) {
            	
            	
            	String pathString = String.join(".", submissionListColumn.getValuePath());
            	
            	System.out.println("\n\n\n"+pathString+"\n\n\n");
            	
            	switch(pathString) {
            		case "fieldValues.value":
            			
            			Long predicateId = fieldPredicateRepo.findByValue(submissionListColumn.getPredicate()).getId();

                        sqlJoinsBuilder.append("\nLEFT JOIN")
                                       .append("\n  (SELECT sfv").append(n).append(".submission_id, fv").append(n).append(".*")
                                       .append("\n   FROM submission_field_values sfv").append(n)
                                       .append("\n   LEFT JOIN field_value fv").append(n).append(" ON fv").append(n).append(".id=sfv").append(n).append(".field_values_id ")
                                       .append("\n   WHERE fv").append(n).append(".field_predicate_id=").append(predicateId).append(") pfv").append(n).append("\n	ON pfv").append(n).append(".submission_id=s.id");

                        if (submissionListColumn.getSortOrder() > 0) {
                            setColumnOrdering(submissionListColumn.getSort(), sqlSelectBuilder, sqlOrderBysBuilder, " pfv" + n + ".value");
                        }

                        for (String filterString : submissionListColumn.getFilters()) {
                        	                        	
                        	if(submissionListColumn.getInputType().getName().equals("INPUT_DATETIME")) {
                        		// Columns values are of type datetime
                        		if(filterString.contains("|")) {
                        			// Date Range 
                        			String[] dates = filterString.split(Pattern.quote("|"));                        			
                        			sqlWheresBuilder.append(" ( CAST(pfv").append(n).append(".value AS DATETIME) BETWEEN '").append(dates[0]).append("' AND '").append(dates[1]).append("') OR");
                        			
                        		} else {
                        			// Date Match                        			
                        			sqlWheresBuilder.append(" ( CAST(pfv").append(n).append(".value AS DATETIME) = '").append(filterString).append("') OR");
                        		}
                        		
                        	} else {
                        		// Columns values can be handled by this default
                        		sqlWheresBuilder.append(" LOWER(pfv").append(n).append(".value) LIKE '%").append(filterString.toLowerCase()).append("%' OR");
                        	}
                        
                        }

                        n++;
            		
            			break;
            		
            		case "id":
            			
            			if (submissionListColumn.getSortOrder() > 0) {
                            setColumnOrdering(submissionListColumn.getSort(), sqlSelectBuilder, sqlOrderBysBuilder, " s.id");
                        }

                        for (String filterString : submissionListColumn.getFilters()) {
                            sqlWheresBuilder.append(" s").append(".id = ").append(filterString).append(" OR");
                        }
            			
            			break;
            		
            		case "state.name":
            			
            			 sqlJoinsBuilder.append("\nLEFT JOIN submission_state ss ON ss.id=s.state_id");

                         if (submissionListColumn.getSortOrder() > 0) {
                             setColumnOrdering(submissionListColumn.getSort(), sqlSelectBuilder, sqlOrderBysBuilder, " ss.name");
                         }
                    	
                    	for (String filterString : submissionListColumn.getFilters()) {
                            sqlWheresBuilder.append(" LOWER(ss").append(".name) LIKE '%").append(filterString.toLowerCase()).append("%' OR");
                        }
            			
            			break;
                	
            		case "organization.name":
            			
            			if(sqlJoinsBuilder.indexOf("LEFT JOIN organization o ON o.id=s.organization_id") == -1)
	        				sqlJoinsBuilder.append("\nLEFT JOIN organization o ON o.id=s.organization_id");

                        if (submissionListColumn.getSortOrder() > 0) {
                            setColumnOrdering(submissionListColumn.getSort(), sqlSelectBuilder, sqlOrderBysBuilder, " o.name");
                        }

                        for (String filterString : submissionListColumn.getFilters()) {
                            sqlWheresBuilder.append(" LOWER(o").append(".name) LIKE '%").append(filterString.toLowerCase()).append("%' OR");
                        }
            			
                    	break;
                    
            		case "organization.category.name":
            			
            			
            			if(sqlJoinsBuilder.indexOf("LEFT JOIN organization o ON o.id=s.organization_id") == -1)
            				sqlJoinsBuilder.append("\nLEFT JOIN organization o ON o.id=s.organization_id");
            			sqlJoinsBuilder.append("\nLEFT JOIN organization_category oc ON oc.id=o.category_id");

                        if (submissionListColumn.getSortOrder() > 0) {
                            setColumnOrdering(submissionListColumn.getSort(), sqlSelectBuilder, sqlOrderBysBuilder, " oc.name");
                        }

                        for (String filterString : submissionListColumn.getFilters()) {
                            sqlWheresBuilder.append(" LOWER(oc").append(".name) LIKE '%").append(filterString.toLowerCase()).append("%' OR");
                        }
            			
                    	break;
                    	
            		case "assignee.email":
            			
            			sqlJoinsBuilder.append("\nLEFT JOIN users a ON a.id=s.assignee_id");

                        if (submissionListColumn.getSortOrder() > 0) {
                            setColumnOrdering(submissionListColumn.getSort(), sqlSelectBuilder, sqlOrderBysBuilder, " a.email");
                        }

                        for (String filterString : submissionListColumn.getFilters()) {
                            sqlWheresBuilder.append(" LOWER(a").append(".email) LIKE '%").append(filterString.toLowerCase()).append("%' OR");
                        }
            			
                    	break;
            		
            		default: System.out.println("No value path given for submissionListColumn " + submissionListColumn.getTitle());
            	}

            }
        }

        
        // complete select clause
        sqlSelectBuilder.setLength(sqlSelectBuilder.length() - 1);
        sqlSelectBuilder.append(" FROM submission s");
        
        // if ordering, complete order by clause and strip the tailing comma
        if (sqlOrderBysBuilder.length() > 0) {
            sqlOrderBysBuilder.insert(0, "\nORDER BY");
            sqlOrderBysBuilder.setLength(sqlOrderBysBuilder.length() - 1);
        }

        // if where, complete where clause and strip the tailing OR
        if (sqlWheresBuilder.length() > 0) {
            sqlWheresBuilder.insert(0, "\nWHERE");
            sqlWheresBuilder.setLength(sqlWheresBuilder.length() - 3);
        }

        String sqlQuery;

        if (pageable != null) {

            // determine the offset and limit of the query
            int offset = pageable.getPageSize() * pageable.getPageNumber();
            int limit = pageable.getPageSize();

            sqlQuery = sqlSelectBuilder.toString() + sqlJoinsBuilder.toString() + sqlWheresBuilder.toString() + sqlOrderBysBuilder.toString() + "\nLIMIT " + limit + " OFFSET " + offset + ";";

        } else {
            sqlQuery = sqlSelectBuilder.toString() + sqlJoinsBuilder.toString() + sqlWheresBuilder.toString();
        }

        String sqlCountQuery = sqlCountSelectBuilder.toString() + sqlJoinsBuilder.toString() + sqlWheresBuilder.toString();
        
        
        System.out.println("QUERY:\n" + sqlQuery);

        System.out.println("COUNT QUERY:\n" + sqlCountQuery);

        return new String[] { sqlQuery, sqlCountQuery };
    }

    public void setColumnOrdering(Sort sort, StringBuilder sqlSelectBuilder, StringBuilder sqlOrderBysBuilder, String value) {
        sqlSelectBuilder.append(value).append(",");        
        switch (sort) {
        case ASC:
            sqlOrderBysBuilder.append(value).append(" ASC,");
            break;
        case DESC:
            sqlOrderBysBuilder.append(value).append(" DESC,");
            break;
        default:
            break;
        }
    }
    
}
