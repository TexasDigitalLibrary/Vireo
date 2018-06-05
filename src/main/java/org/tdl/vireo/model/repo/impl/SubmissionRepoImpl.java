package org.tdl.vireo.model.repo.impl;

import static edu.tamu.weaver.response.ApiAction.UPDATE;
import static edu.tamu.weaver.response.ApiStatus.SUCCESS;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.tdl.vireo.exception.OrganizationDoesNotAcceptSubmissionsExcception;
import org.tdl.vireo.model.Configuration;
import org.tdl.vireo.model.CustomActionDefinition;
import org.tdl.vireo.model.FieldPredicate;
import org.tdl.vireo.model.FieldValue;
import org.tdl.vireo.model.ManagedConfiguration;
import org.tdl.vireo.model.NamedSearchFilterGroup;
import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.Sort;
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
import org.tdl.vireo.utility.FileIOUtility;

import edu.tamu.weaver.auth.model.Credentials;
import edu.tamu.weaver.data.model.repo.impl.AbstractWeaverRepoImpl;
import edu.tamu.weaver.response.ApiResponse;

public class SubmissionRepoImpl extends AbstractWeaverRepoImpl<Submission, SubmissionRepo> implements SubmissionRepoCustom {

    final static Logger logger = LoggerFactory.getLogger(SubmissionRepoImpl.class);

    @Value("${app.url}")
    private String url;

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

    @Value("${app.document.path:private/}")
    private String documentPath;

    @Autowired
    public SubmissionRepoImpl(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Submission create(User submitter, Organization organization, SubmissionStatus startingStatus, Credentials credentials) throws OrganizationDoesNotAcceptSubmissionsExcception {
        if (organization.getAcceptsSubmissions().equals(false)) {
            throw new OrganizationDoesNotAcceptSubmissionsExcception();
        }

        Submission submission = submissionRepo.save(new Submission(submitter, organization, startingStatus));

        for (CustomActionDefinition cad : customActionDefinitionRepo.findAll()) {
            customActionValueRepo.create(submission, cad, false);
        }

        submission.setSubmissionWorkflowSteps(submissionWorkflowStepRepo.cloneWorkflow(organization));

        submission.getSubmissionWorkflowSteps().forEach(ws -> {
            ws.getAggregateFieldProfiles().forEach(afp -> {
                ManagedConfiguration mappedShibAttribute = afp.getMappedShibAttribute();
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

        submission.generateAdvisorReviewUrl(url);

        setCheckboxDefaultValue(submission, "INPUT_CHECKBOX");
        setCheckboxDefaultValue(submission, "INPUT_LICENSE");
        setCheckboxDefaultValue(submission, "INPUT_PROQUEST");

        return super.create(submission);
    }

    private void setCheckboxDefaultValue(Submission submission, String inputTypeName) {
        submission.getSubmissionFieldProfilesByInputTypeName(inputTypeName).forEach(sfp -> {
            FieldValue fieldValue = fieldValueRepo.create(sfp.getFieldPredicate());

            fieldValue.setValue("false");
            submission.addFieldValue(fieldValue);
        });
    }

    @Override
    public Submission update(Submission submission) {
        submission = super.update(submission);
        simpMessagingTemplate.convertAndSend(getChannel() + "/" + submission.getId(), new ApiResponse(SUCCESS, UPDATE, submission));
        return submission;
    }

    @Override
    public Submission updateStatus(Submission submission, SubmissionStatus submissionStatus, User user) {

        SubmissionStatus oldSubmissionStatus = submission.getSubmissionStatus();
        String oldSubmissionStatusName = oldSubmissionStatus.getName();

        if (submission.getSubmissionStatus() != null) {
            logger.debug("Changing status of submission " + submission.getId() + " from " + submission.getSubmissionStatus().getName() + " to " + submissionStatus.getName());
        } else {
            logger.debug("Changing status of submission " + submission.getId() + "to " + submissionStatus.getName());
        }

        submission.setSubmissionStatus(submissionStatus);

        switch (submissionStatus.getSubmissionState()) {
        case SUBMITTED:

            submission.setApproveApplication(false);
            submission.setApproveApplicationDate(null);

            submission.setSubmissionDate(Calendar.getInstance());

            List<FieldValue> proquestFieldValues = submission.getFieldValuesByInputType(inputTypeRepo.findByName("INPUT_PROQUEST"));
            List<FieldValue> defaultLicenseFieldValues = submission.getFieldValuesByInputType(inputTypeRepo.findByName("INPUT_LICENSE"));

            boolean attachProquestLicense = true;
            boolean attachDefaultLicenseFieldValues = true;

            for (FieldValue fv : proquestFieldValues) {
                attachProquestLicense = !fv.getValue().equals("false");

                if (!attachProquestLicense)
                    break;
            }

            for (FieldValue fv : defaultLicenseFieldValues) {
                attachDefaultLicenseFieldValues = !fv.getValue().equals("false");
                if (!attachDefaultLicenseFieldValues)
                    break;
            }

            if (attachProquestLicense) {
                removeLicenseFile(submission, "proquest_license");
                writeLicenseFile(user, submission, "proquest_license", "proquest_license", "proquest_umi_degree_code");
            }

            if (attachDefaultLicenseFieldValues) {
                removeLicenseFile(submission, "license");
                writeLicenseFile(user, submission, "submit_license", "license", "submission");
            }
            break;
        case APPROVED:
            submission.setApproveApplication(true);
            submission.setApproveApplicationDate(Calendar.getInstance());
            break;
        case PENDING_PUBLICATION:
            break;
        case PUBLISHED:
            break;
        case WITHDRAWN:
            break;
        case ON_HOLD:
        case CANCELED:
        case CORRECTIONS_RECIEVED:
        case IN_PROGRESS:
        case NEEDS_CORRECTIONS:
        case NONE:
        case UNDER_REVIEW:
        case WAITING_ON_REQUIREMENTS:
        default:
            submission.setApproveApplication(false);
            submission.setApproveApplicationDate(null);
            break;
        }

        super.update(submission);

        actionLogRepo.createPublicLog(submission, user, "Submission status was changed from " + oldSubmissionStatusName + " to " + submissionStatus.getName());
        return submission;
    }

    private void removeLicenseFile(Submission submission, String fileName) {
        FieldPredicate licensePredicate = fieldPredicateRepo.findByValue("_doctype_license");
        List<FieldValue> fieldValues = submission.getFieldValuesByPredicate(licensePredicate);
        for (FieldValue fieldValue : fieldValues) {
            String licenceUri = fieldValue.getValue();
            if (licenceUri.substring(licenceUri.lastIndexOf("-") + 1).equals(fileName + ".txt")) {
                try {
                    fileIOUtility.delete(fieldValue.getValue());
                    submission.removeFieldValue(fieldValue);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    private void writeLicenseFile(User user, Submission submission, String licenseName, String fileName, String configurationType) {

        byte[] licenseBytes = null;

        Configuration proquestLicense = configurationRepo.getByNameAndType(licenseName, configurationType);

        User submitter = submission.getSubmitter();

        StringBuilder proquestLicenseStringBuilder = new StringBuilder();

        DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);

        String acceptedDate = formatter.format(submission.getSubmissionDate().getTime());

        proquestLicenseStringBuilder.append("\n").append("The license above was accepted by ").append(submitter.getFirstName()).append(" ").append(submitter.getLastName()).append(" on ").append(acceptedDate);

        int seporatorLength = proquestLicenseStringBuilder.length();

        for (int i = 0; i < seporatorLength; i++)
            proquestLicenseStringBuilder.insert(0, "-");

        proquestLicenseStringBuilder.insert(0, "\n\n");

        proquestLicenseStringBuilder.insert(0, proquestLicense.getValue());

        licenseBytes = proquestLicenseStringBuilder.toString().getBytes();

        if (licenseBytes != null) {
            int hash = user.getEmail().hashCode();
            String uri = documentPath + hash + "/" + System.currentTimeMillis() + "-" + fileName + ".txt";

            try {
                fileIOUtility.write(licenseBytes, uri);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            FieldPredicate licensePredicate = fieldPredicateRepo.findByValue("_doctype_license");

            FieldValue fieldValue = fieldValueRepo.create(licensePredicate);
            fieldValue.setValue(uri);
            submission.addFieldValue(fieldValue);
        }

    }

    @Override
    public List<Submission> batchDynamicSubmissionQuery(NamedSearchFilterGroup activeFilter, List<SubmissionListColumn> submissionListColums) {
        QueryStrings queryBuilder = craftDynamicSubmissionQuery(activeFilter, submissionListColums, null);
        List<Long> ids = new ArrayList<Long>();
        jdbcTemplate.queryForList(queryBuilder.getQuery()).forEach(row -> {
            ids.add((Long) row.get("ID"));
        });
        return submissionRepo.findAll(ids);
    }

    @Override
    public Page<Submission> pageableDynamicSubmissionQuery(NamedSearchFilterGroup activeFilter, List<SubmissionListColumn> submissionListColums, Pageable pageable) throws ExecutionException {
        QueryStrings queryBuilder = craftDynamicSubmissionQuery(activeFilter, submissionListColums, pageable);

        Long total = jdbcTemplate.queryForObject(queryBuilder.getCountQuery(), Long.class);

        List<Long> ids = jdbcTemplate.query(queryBuilder.getQuery(), new RowMapper<Long>() {
            public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getLong("ID");
            }
        });

        List<Submission> submissions = new ArrayList<Submission>();

        List<Submission> unordered = submissionRepo.findAll(ids);

        // order them
        for (Long id : ids) {
            for (Submission sub : unordered) {
                if (sub.getId().equals(id)) {
                    submissions.add(sub);
                    unordered.remove(sub);
                    break;
                }
            }
        }

        int offset = pageable.getPageSize() * pageable.getPageNumber();
        int limit = pageable.getPageSize();
        return new PageImpl<Submission>(submissions, new PageRequest((int) Math.floor(offset / limit), limit), total);
    }

    private QueryStrings craftDynamicSubmissionQuery(NamedSearchFilterGroup activeFilter, List<SubmissionListColumn> submissionListColums, Pageable pageable) {

        // set up storage for user's preferred columns
        Set<String> allColumnSearchFilters = new HashSet<String>();
    
        // get all the possible columns, some of which we will make visible
        List<SubmissionListColumn> allSubmissionListColumns = submissionListColumnRepo.findAll();

        // set sort and sort order on all submission list columns that are set
        // on the requesting user's submission list columns
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
        StringBuilder sqlWheresExcludeBuilder = new StringBuilder();
        StringBuilder sqlOrderBysBuilder = new StringBuilder();

        int n = 0;

        for (SubmissionListColumn submissionListColumn : allSubmissionListColumns) {

            if (submissionListColumn.getSortOrder() > 0 || submissionListColumn.getFilters().size() > 0 || allColumnSearchFilters.size() > 0 || submissionListColumn.getVisible()) {

                switch (String.join(".", submissionListColumn.getValuePath())) {
                case "fieldValues.value":

                    Long predicateId = fieldPredicateRepo.findByValue(submissionListColumn.getPredicate()).getId();

                    // @formatter:off
                    sqlJoinsBuilder.append("\nLEFT JOIN")
                                  .append("\n  (SELECT sfv").append(n).append(".submission_id, fv").append(n).append(".*")
                                  .append("\n   FROM submission_field_values sfv").append(n)
                                  .append("\n   LEFT JOIN field_value fv").append(n).append(" ON fv").append(n).append(".id=sfv").append(n).append(".field_values_id ")
                                  .append("\n   WHERE fv").append(n).append(".field_predicate_id=").append(predicateId).append(") pfv").append(n)
                                  .append("\n ON pfv").append(n).append(".submission_id=s.id");

                    // @formatter:on

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
                                dates[0] = dates[0].replaceAll("[TZ:.\\-]", " ");
                                dates[1] = dates[1].replaceAll("[TZ:.\\-]", " ");

                                sqlWheresBuilder.append(" ( CAST(pfv").append(n).append(".value AS TIMESTAMP) BETWEEN to_timestamp('").append(dates[0]).append("', \'YYYY MM DD HH MI SS MS') AND to_timestamp('").append(dates[1]).append("', \'YYYY MM DD HH MI SS MS')) OR");
                            } else {
                                // Date Match
                                filterString.replaceAll("[TZ:.\\-]", " ");
                                sqlWheresBuilder.append(" ( CAST(pfv").append(n).append(".value AS TIMESTAMP) = '").append(filterString).append("') OR");
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
                            // TODO: determine if status will ever be search
                            // using a like
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
                            // TODO: determine if organization name will ever be
                            // search using a like
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
                            // TODO: determine if organization category name
                            // will ever be search using a like
                            sqlWheresBuilder.append(" LOWER(oc").append(".name) LIKE '%").append(filterString.toLowerCase()).append("%' OR");
                        }
                    }

                    // all column search filter
                    for (String filterString : allColumnSearchFilters) {
                        sqlWheresBuilder.append(" LOWER(oc").append(".name) LIKE '%").append(filterString.toLowerCase()).append("%' OR");
                    }

                    break;

                case "assignee.email":

                    sqlJoinsBuilder.append("\nLEFT JOIN weaver_users a ON a.id=s.assignee_id");

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
                    // @formatter:off
                    sqlJoinsBuilder.append("\nLEFT JOIN")
                                   .append("\n   (SELECT e.id, e.name, semt.submission_id")
                                   .append("\n   FROM embargo e")
                                   .append("\n   LEFT JOIN submission_embargo_types semt")
                                   .append("\n   ON semt.embargo_types_id=e.id) embs")
                                   .append("\n   ON embs.submission_id=s.id");
                    // @formatter:on

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

                // exclude individual submissions from submission list
                case "exclude":
                	
                    if (submissionListColumn.getSortOrder() > 0) {
                        setColumnOrdering(submissionListColumn.getSort(), sqlSelectBuilder, sqlOrderBysBuilder, " s.id");
                    }

                    for (String filterString : submissionListColumn.getFilters()) {
                    	if (sqlWheresExcludeBuilder.length() > 0) {
                    		sqlWheresExcludeBuilder.append(" AND s").append(".id <> ").append(filterString);
                    	} else {
                    		sqlWheresExcludeBuilder.append(" s").append(".id <> ").append(filterString);
                    	}
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
            sqlWheresBuilder.insert(0, "\nWHERE (");
            sqlWheresBuilder.setLength(sqlWheresBuilder.length() - 3);
            sqlWheresBuilder.append(" )");
            // append excluded submissions
            if (sqlWheresExcludeBuilder.length() > 0) {
            	sqlWheresBuilder.append(" AND ").append(sqlWheresExcludeBuilder);
            }
        // if only exclude filter, complete where clause
        } else {
            if (sqlWheresExcludeBuilder.length() > 0) {
            	sqlWheresBuilder.insert(0, "\nWHERE");
            	sqlWheresBuilder.append(sqlWheresExcludeBuilder);
            }
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
        
        return new QueryStrings(sqlCountQuery, sqlQuery);
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

    @Override
    protected String getChannel() {
        return "/channel/submission";
    }

    private class QueryStrings {

        private final String countQuery;

        private final String query;

        private QueryStrings(String countQuery, String query) {
            this.countQuery = countQuery;
            this.query = query;
        }

        public String getCountQuery() {
            return countQuery;
        }

        public String getQuery() {
           return query;
        }

    }

}