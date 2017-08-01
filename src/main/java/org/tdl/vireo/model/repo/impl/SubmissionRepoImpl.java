package org.tdl.vireo.model.repo.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.tdl.vireo.enums.Sort;
import org.tdl.vireo.enums.SubmissionState;
import org.tdl.vireo.exception.OrganizationDoesNotAcceptSubmissionsExcception;
import org.tdl.vireo.model.Configuration;
import org.tdl.vireo.model.CustomActionDefinition;
import org.tdl.vireo.model.FieldPredicate;
import org.tdl.vireo.model.FieldValue;
import org.tdl.vireo.model.NamedSearchFilterGroup;
import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.SubmissionListColumn;
import org.tdl.vireo.model.SubmissionStatus;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.ActionLogRepo;
import org.tdl.vireo.model.repo.ConfigurationRepo;
import org.tdl.vireo.model.repo.CustomActionDefinitionRepo;
import org.tdl.vireo.model.repo.CustomActionValueRepo;
import org.tdl.vireo.model.repo.FieldPredicateRepo;
import org.tdl.vireo.model.repo.FieldValueRepo;
import org.tdl.vireo.model.repo.InputTypeRepo;
import org.tdl.vireo.model.repo.SubmissionListColumnRepo;
import org.tdl.vireo.model.repo.SubmissionRepo;
import org.tdl.vireo.model.repo.SubmissionWorkflowStepRepo;
import org.tdl.vireo.model.repo.custom.SubmissionRepoCustom;
import org.tdl.vireo.util.FileIOUtility;

import edu.tamu.framework.model.Credentials;

public class SubmissionRepoImpl implements SubmissionRepoCustom {

    final static Logger logger = LoggerFactory.getLogger(SubmissionRepoImpl.class);

    @Autowired
    private SubmissionRepo submissionRepo;

    @Autowired
    private FieldPredicateRepo fieldPredicateRepo;

    @Autowired
    private FieldValueRepo fieldValueRepo;

    @Autowired
    private SubmissionWorkflowStepRepo submissionWorkflowStepRepo;

    @Autowired
    private SubmissionListColumnRepo submissionListColumnRepo;
    
    @Autowired
    private InputTypeRepo inputTypeRepo;

    @Autowired
    private CustomActionDefinitionRepo customActionDefinitionRepo;

    @Autowired
    private CustomActionValueRepo customActionValueRepo;
    
    @Autowired
    private ConfigurationRepo configurationRepo;

    @Autowired
    private ActionLogRepo actionLogRepo;
    
    @Autowired
    private FileIOUtility fileIOUtility;

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public SubmissionRepoImpl(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Submission create(User submitter, Organization organization, SubmissionStatus startingStatus, Credentials credentials) throws OrganizationDoesNotAcceptSubmissionsExcception {
        
        
        if(organization.getAcceptsSubmissions().equals(false)) {
            throw new OrganizationDoesNotAcceptSubmissionsExcception();            
        }
        
        Submission submission = submissionRepo.save(new Submission(submitter, organization, startingStatus));

        for (CustomActionDefinition cad : customActionDefinitionRepo.findAll()) {
            customActionValueRepo.create(submission, cad, false);
        }

        submission.setSubmissionWorkflowSteps(submissionWorkflowStepRepo.cloneWorkflow(organization));

        submission.getSubmissionWorkflowSteps().forEach(ws -> {
            ws.getAggregateFieldProfiles().forEach(afp -> {
                Configuration mappedShibAttribute = afp.getMappedShibAttribute();
                if (mappedShibAttribute != null) {
                    if (credentials.getAllCredentials().containsKey(mappedShibAttribute.getValue())) {
                        String credentialValue = credentials.getAllCredentials().get(mappedShibAttribute.getValue());

                        FieldValue fieldValue = fieldValueRepo.create(afp.getFieldPredicate());

                        fieldValue.setValue(credentialValue);
                        submission.addFieldValue(fieldValue);
                    }
                }
                if (afp.getDefaultValue() != null) {
                    FieldValue fieldValue = fieldValueRepo.create(afp.getFieldPredicate());
                    fieldValue.setValue(afp.getDefaultValue());
                    submission.addFieldValue(fieldValue);
                }
            });
        });

        setCheckboxDefaultValue(submission, "INPUT_CHECKBOX");
        setCheckboxDefaultValue(submission, "INPUT_LICENSE");
        setCheckboxDefaultValue(submission, "INPUT_PROQUEST");

        return submissionRepo.save(submission);
    }

    private void setCheckboxDefaultValue(Submission submission, String inputTypeName) {
        submission.getSubmissionFieldProfilesByInputTypeName(inputTypeName).forEach(sfp -> {
            FieldValue fieldValue = fieldValueRepo.create(sfp.getFieldPredicate());

            fieldValue.setValue("false");
            submission.addFieldValue(fieldValue);
        });
    }

    @Override
    public Submission updateStatus(Submission submission, SubmissionStatus submissionStatus, Credentials credentials) {
        SubmissionStatus oldSubmissionStatus = submission.getSubmissionStatus();
        String oldSubmissionStatusName = oldSubmissionStatus.getName();
        
        byte[] proquestLicenseBytes = null;
        byte[] defaultLicenseBytes = null;
                
        if(submissionStatus.getSubmissionState() == SubmissionState.SUBMITTED) {
        	        	
        	List<FieldValue> proquestFieldValues = submission.getFieldValuesByInputType(inputTypeRepo.findByName("INPUT_PROQUEST"));
        	List<FieldValue> defaultLicenseFieldValues = submission.getFieldValuesByInputType(inputTypeRepo.findByName("INPUT_LICENSE"));

        	boolean attachProquestLicense = true;
        	boolean attachDefaultLicenseFieldValues = true;
        	
        	for(FieldValue fv : proquestFieldValues) {
        		attachProquestLicense = !fv.getValue().equals("false");
        		if(!attachProquestLicense) break;
        	}
        	
        	for(FieldValue fv : defaultLicenseFieldValues) {
        		attachDefaultLicenseFieldValues = !fv.getValue().equals("false");
        		if(!attachDefaultLicenseFieldValues) break;
        	}
        	
        	if(attachProquestLicense) {
        		System.out.println("Append proquest license");
        		Configuration proquestLicense = configurationRepo.getByName("proquest_license");
        		proquestLicenseBytes = proquestLicense != null ? proquestLicense.getValue().getBytes() : null;
        	}
        	
        	if(attachDefaultLicenseFieldValues) {
        		System.out.println("Append default license");
        		Configuration defaultLicense = configurationRepo.getByName("submit_license");
        		defaultLicenseBytes = defaultLicense != null ? defaultLicense.getValue().getBytes() : null;
        	}
        	
        }
        
        if(proquestLicenseBytes != null) {
        	int hash = credentials.getEmail().hashCode();
            String uri = "private/" + hash + "/" + System.currentTimeMillis() + "-proquest_license.txt";
            
            try {
				fileIOUtility.write(proquestLicenseBytes, uri);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			FieldPredicate licensePredicate = fieldPredicateRepo.findByValue("_doctype_license");
			
            FieldValue fieldValue = fieldValueRepo.create(licensePredicate);
            fieldValue.setValue(uri);
            submission.addFieldValue(fieldValue);
            
            System.out.println(fieldValue.getValue());
			
		}
        
        if(defaultLicenseBytes != null) {
        	int hash = credentials.getEmail().hashCode();
            String uri = "private/" + hash + "/" + System.currentTimeMillis() + "-license.txt";
            
            try {
				fileIOUtility.write(defaultLicenseBytes, uri);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			FieldPredicate licensePredicate = fieldPredicateRepo.findByValue("_doctype_license");
			
            FieldValue fieldValue = fieldValueRepo.create(licensePredicate);
            fieldValue.setValue(uri);
            submission.addFieldValue(fieldValue);
            
            System.out.println(fieldValue.getValue());
			
		}
        
        submission.setSubmissionStatus(submissionStatus);
        submission = submissionRepo.saveAndFlush(submission);

        actionLogRepo.createPublicLog(submission, credentials, "Submission status was changed from " + oldSubmissionStatusName + " to " + submissionStatus.getName());
        return submission;
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
                            slc.setExactMatch(namedSearchFilter.getExactMatch());
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

        StringBuilder sqlCountSelectBuilder = new StringBuilder("SELECT COUNT(DISTINCT s.id) FROM submission s ");

        StringBuilder sqlJoinsBuilder = new StringBuilder();
        StringBuilder sqlWheresBuilder = new StringBuilder();
        StringBuilder sqlOrderBysBuilder = new StringBuilder();

        int n = 0;

        for (SubmissionListColumn submissionListColumn : allSubmissionListColumns) {

            if (submissionListColumn.getSortOrder() > 0 || submissionListColumn.getFilters().size() > 0 || allColumnSearchFilters.size() > 0 || submissionListColumn.getVisible()) {

                switch (String.join(".", submissionListColumn.getValuePath())) {
                case "fieldValues.value":

                    Long predicateId = fieldPredicateRepo.findByValue(submissionListColumn.getPredicate()).getId();

                    sqlJoinsBuilder.append("\nLEFT JOIN").append("\n  (SELECT sfv").append(n).append(".submission_id, fv").append(n).append(".*").append("\n   FROM submission_field_values sfv").append(n).append("\n   LEFT JOIN field_value fv").append(n).append(" ON fv").append(n).append(".id=sfv").append(n).append(".field_values_id ").append("\n   WHERE fv").append(n).append(".field_predicate_id=").append(predicateId).append(") pfv").append(n).append("\n	ON pfv").append(n).append(".submission_id=s.id");

                    if (submissionListColumn.getSortOrder() > 0) {
                        setColumnOrdering(submissionListColumn.getSort(), sqlSelectBuilder, sqlOrderBysBuilder, " pfv" + n + ".value");
                    }

                    for (String filterString : submissionListColumn.getFilters()) {

                        switch (submissionListColumn.getInputType().getName()) {
                        case "INPUT_DATETIME":
                            // Column's values are of type datetime
                            if (filterString.contains("|")) {
                                // Date Range
                                String[] dates = filterString.split(Pattern.quote("|"));
                                sqlWheresBuilder.append(" ( CAST(pfv").append(n).append(".value AS DATETIME) BETWEEN '").append(dates[0]).append("' AND '").append(dates[1]).append("') OR");

                            } else {
                                // Date Match
                                sqlWheresBuilder.append(" ( CAST(pfv").append(n).append(".value AS DATETIME) = '").append(filterString).append("') OR");
                            }
                            break;
                        case "INPUT_CHECKBOX":
                            // Column's values are a boolean
                            if (Boolean.valueOf(filterString)) {
                                sqlWheresBuilder.append(" pfv").append(n).append(".value = '").append(filterString).append("' OR");
                            } else {
                                sqlWheresBuilder.append(" pfv").append(n).append(".value = '").append(filterString).append("' OR").append(" pfv").append(n).append(".value IS NULL ").append(" OR");
                            }
                            break;
                        default:
                            // Column's values can be handled by this default
                            if (submissionListColumn.getExactMatch()) {
                                // perform exact match
                                sqlWheresBuilder.append(" pfv").append(n).append(".value = '").append(filterString).append("' OR");
                            } else {
                                // perform like when input from text field
                                sqlWheresBuilder.append(" LOWER(pfv").append(n).append(".value) LIKE '%").append(filterString.toLowerCase()).append("%' OR");
                            }
                            break;
                        }

                    }

                    // all column search filter
                    for (String filterString : allColumnSearchFilters) {
                        sqlWheresBuilder.append(" LOWER(pfv").append(n).append(".value) LIKE '%").append(filterString.toLowerCase()).append("%' OR");
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

                case "submissionStatus.name":

                    sqlJoinsBuilder.append("\nLEFT JOIN submission_status ss ON ss.id=s.submission_status_id");

                    if (submissionListColumn.getSortOrder() > 0) {
                        setColumnOrdering(submissionListColumn.getSort(), sqlSelectBuilder, sqlOrderBysBuilder, " ss.name");
                    }

                    for (String filterString : submissionListColumn.getFilters()) {
                        if (submissionListColumn.getExactMatch()) {
                            sqlWheresBuilder.append(" ss").append(".name = '").append(filterString).append("' OR");
                        } else {
                            // TODO: determine if status will ever be search using a like
                            sqlWheresBuilder.append(" LOWER(ss").append(".name) LIKE '%").append(filterString.toLowerCase()).append("%' OR");
                        }

                    }

                    // all column search filter
                    for (String filterString : allColumnSearchFilters) {
                        sqlWheresBuilder.append(" LOWER(ss").append(".name) LIKE '%").append(filterString.toLowerCase()).append("%' OR");
                    }

                    break;

                case "organization.name":

                    if (sqlJoinsBuilder.indexOf("LEFT JOIN organization o ON o.id=s.organization_id") == -1)
                        sqlJoinsBuilder.append("\nLEFT JOIN organization o ON o.id=s.organization_id");

                    if (submissionListColumn.getSortOrder() > 0) {
                        setColumnOrdering(submissionListColumn.getSort(), sqlSelectBuilder, sqlOrderBysBuilder, " o.name");
                    }

                    for (String filterString : submissionListColumn.getFilters()) {
                        if (submissionListColumn.getExactMatch()) {
                            sqlWheresBuilder.append(" o").append(".name = '").append(filterString).append("' OR");
                        } else {
                            // TODO: determine if organization name will ever be search using a like
                            sqlWheresBuilder.append(" LOWER(o").append(".name) LIKE '%").append(filterString.toLowerCase()).append("%' OR");
                        }
                    }

                    // all column search filter
                    for (String filterString : allColumnSearchFilters) {
                        sqlWheresBuilder.append(" LOWER(o").append(".name) LIKE '%").append(filterString.toLowerCase()).append("%' OR");
                    }

                    break;

                case "organization.category.name":

                    if (sqlJoinsBuilder.indexOf("LEFT JOIN organization o ON o.id=s.organization_id") == -1) {
                        sqlJoinsBuilder.append("\nLEFT JOIN organization o ON o.id=s.organization_id");
                    }
                    sqlJoinsBuilder.append("\nLEFT JOIN organization_category oc ON oc.id=o.category_id");

                    if (submissionListColumn.getSortOrder() > 0) {
                        setColumnOrdering(submissionListColumn.getSort(), sqlSelectBuilder, sqlOrderBysBuilder, " oc.name");
                    }

                    for (String filterString : submissionListColumn.getFilters()) {
                        if (submissionListColumn.getExactMatch()) {
                            sqlWheresBuilder.append(" oc").append(".name = '").append(filterString).append("' OR");
                        } else {
                            // TODO: determine if organization category name will ever be search using a like
                            sqlWheresBuilder.append(" LOWER(oc").append(".name) LIKE '%").append(filterString.toLowerCase()).append("%' OR");
                        }
                    }

                    // all column search filter
                    for (String filterString : allColumnSearchFilters) {
                        sqlWheresBuilder.append(" LOWER(oc").append(".name) LIKE '%").append(filterString.toLowerCase()).append("%' OR");
                    }

                    break;

                case "assignee.email":

                    sqlJoinsBuilder.append("\nLEFT JOIN core_users a ON a.id=s.assignee_id");

                    if (submissionListColumn.getSortOrder() > 0) {
                        setColumnOrdering(submissionListColumn.getSort(), sqlSelectBuilder, sqlOrderBysBuilder, " a.email");
                    }

                    for (String filterString : submissionListColumn.getFilters()) {
                        if (submissionListColumn.getExactMatch()) {
                            sqlWheresBuilder.append(" a").append(".email = '").append(filterString).append("' OR");
                        } else {
                            sqlWheresBuilder.append(" LOWER(a").append(".email) LIKE '%").append(filterString.toLowerCase()).append("%' OR");
                        }
                    }

                    // all column search filter
                    for (String filterString : allColumnSearchFilters) {
                        sqlWheresBuilder.append(" LOWER(a").append(".email) LIKE '%").append(filterString.toLowerCase()).append("%' OR");
                    }

                    break;

                case "embargoTypes.name":

                    sqlJoinsBuilder.append("\nLEFT JOIN").append("\n   (SELECT e.id, e.name, semt.submission_id").append("\n   FROM embargo e").append("\n   LEFT JOIN submission_embargo_types semt").append("\n   ON semt.embargo_types_id=e.id) embs").append("\n   ON embs.submission_id=s.id");

                    if (submissionListColumn.getSortOrder() > 0) {
                        setColumnOrdering(submissionListColumn.getSort(), sqlSelectBuilder, sqlOrderBysBuilder, " embs.name");
                    }

                    for (String filterString : submissionListColumn.getFilters()) {
                        if (filterString.equals("None")) {
                            sqlWheresBuilder.append(" embs").append(".name = '").append(filterString).append("' OR").append(" embs.id IS NULL OR");
                        } else {
                            sqlWheresBuilder.append(" embs").append(".name = '").append(filterString).append("' OR");
                        }
                    }

                    // all column search filter
                    for (String filterString : allColumnSearchFilters) {
                        sqlWheresBuilder.append(" LOWER(embs").append(".name) LIKE '%").append(filterString.toLowerCase()).append("%' OR");
                    }

                    break;

                default:
                    logger.info("No value path given for submissionListColumn " + submissionListColumn.getTitle());
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

        logger.debug("QUERY:\n" + sqlQuery);

        logger.debug("COUNT QUERY:\n" + sqlCountQuery);

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
