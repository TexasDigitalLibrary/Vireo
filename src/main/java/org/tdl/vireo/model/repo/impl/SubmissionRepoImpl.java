package org.tdl.vireo.model.repo.impl;

import static edu.tamu.weaver.response.ApiAction.CREATE;
import static edu.tamu.weaver.response.ApiAction.DELETE;
import static edu.tamu.weaver.response.ApiAction.UPDATE;
import static edu.tamu.weaver.response.ApiStatus.SUCCESS;

import edu.tamu.weaver.auth.model.Credentials;
import edu.tamu.weaver.data.model.repo.impl.AbstractWeaverRepoImpl;
import edu.tamu.weaver.response.ApiResponse;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import org.springframework.transaction.annotation.Transactional;
import org.tdl.vireo.exception.OrganizationDoesNotAcceptSubmissionsException;
import org.tdl.vireo.model.Configuration;
import org.tdl.vireo.model.CustomActionDefinition;
import org.tdl.vireo.model.FieldPredicate;
import org.tdl.vireo.model.FieldValue;
import org.tdl.vireo.model.ManagedConfiguration;
import org.tdl.vireo.model.NamedSearchFilterGroup;
import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.Sort;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.SubmissionFieldProfile;
import org.tdl.vireo.model.SubmissionListColumn;
import org.tdl.vireo.model.SubmissionStatus;
import org.tdl.vireo.model.SubmissionWorkflowStep;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.ActionLogRepo;
import org.tdl.vireo.model.repo.ConfigurationRepo;
import org.tdl.vireo.model.repo.CustomActionValueRepo;
import org.tdl.vireo.model.repo.FieldPredicateRepo;
import org.tdl.vireo.model.repo.FieldValueRepo;
import org.tdl.vireo.model.repo.InputTypeRepo;
import org.tdl.vireo.model.repo.SubmissionListColumnRepo;
import org.tdl.vireo.model.repo.SubmissionRepo;
import org.tdl.vireo.model.repo.SubmissionWorkflowStepRepo;
import org.tdl.vireo.model.repo.custom.SubmissionRepoCustom;
import org.tdl.vireo.service.AssetService;

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
    private CustomActionValueRepo customActionValueRepo;

    @Autowired
    private ConfigurationRepo configurationRepo;

    @Autowired
    private ActionLogRepo actionLogRepo;

    @Autowired
    private AssetService assetService;

    private JdbcTemplate jdbcTemplate;

    @Value("${app.document.path:private/}")
    private String documentPath;

    public SubmissionRepoImpl(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    @Transactional
    public Submission create(User submitter, Organization organization, SubmissionStatus startingStatus, Credentials credentials, List<CustomActionDefinition> customActions) throws OrganizationDoesNotAcceptSubmissionsException {
        if (organization.getAcceptsSubmissions().equals(false)) {
            throw new OrganizationDoesNotAcceptSubmissionsException();
        }

        Submission submission = submissionRepo.save(new Submission(submitter, organization, startingStatus));

        for (CustomActionDefinition cad : customActions) {
            customActionValueRepo.create(submission, cad, false);
        }

        submission.setSubmissionWorkflowSteps(submissionWorkflowStepRepo.cloneWorkflow(organization));

        for (SubmissionWorkflowStep ws : submission.getSubmissionWorkflowSteps()) {
            for (SubmissionFieldProfile afp : ws.getAggregateFieldProfiles()) {
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
            }
        }

        submission.generateAdvisorReviewUrl(url);

        setCheckboxDefaultValue(submission, "INPUT_CHECKBOX");
        setCheckboxDefaultValue(submission, "INPUT_LICENSE");
        setCheckboxDefaultValue(submission, "INPUT_PROQUEST");

        submission = super.create(submission);
        simpMessagingTemplate.convertAndSendToUser(submitter.getUsername(), "/queue/submissions", new ApiResponse(SUCCESS, CREATE, submission));
        return submission;
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
        submission = submissionRepo.save(submission);
        simpMessagingTemplate.convertAndSend(getChannel() + "/" + submission.getId(), new ApiResponse(SUCCESS, UPDATE, submission));
        simpMessagingTemplate.convertAndSendToUser(submission.getSubmitter().getUsername(), "/queue/submissions", new ApiResponse(SUCCESS, UPDATE, submission));
        return submission;
    }

    @Override
    public void delete(Submission submission) {
        super.delete(submission);
        simpMessagingTemplate.convertAndSendToUser(submission.getSubmitter().getUsername(), "/queue/submissions", new ApiResponse(SUCCESS, DELETE, submission));
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

        submission = update(submission);
        simpMessagingTemplate.convertAndSendToUser(user.getUsername(), "/queue/submissions", new ApiResponse(SUCCESS, UPDATE, submission));

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
                    assetService.delete(fieldValue.getValue());
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
                assetService.write(licenseBytes, uri);
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
        return submissionRepo.findAllById(ids);
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

        List<Submission> unordered = submissionRepo.findAllById(ids);

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
        return new PageImpl<Submission>(submissions, PageRequest.of((int) Math.floor(offset / limit), limit), total);
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

        Map<Long, ArrayList<StringBuilder>> sqlColumnsBuilders = new HashMap<Long, ArrayList<StringBuilder>>();

        StringBuilder sqlJoinsBuilder = new StringBuilder();
        StringBuilder sqlWhereBuilder;
        StringBuilder sqlWheresExcludeBuilder = new StringBuilder();
        StringBuilder sqlOrderBysBuilder = new StringBuilder();

        ArrayList<StringBuilder> sqlWhereBuilderList;
        ArrayList<StringBuilder> sqlAllColumnsWhereBuilderList = new ArrayList<StringBuilder>();

        int n = 0;

        for (SubmissionListColumn submissionListColumn : allSubmissionListColumns) {

            if (submissionListColumn.getSortOrder() > 0 || submissionListColumn.getFilters().size() > 0 || allColumnSearchFilters.size() > 0 || submissionListColumn.getVisible()) {
                if (sqlColumnsBuilders.containsKey(submissionListColumn.getId())) {
                    sqlWhereBuilderList = sqlColumnsBuilders.get(submissionListColumn.getId());
                } else {
                    sqlWhereBuilderList = new ArrayList<StringBuilder>();
                }

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
                        sqlWhereBuilder = new StringBuilder();

                        switch (submissionListColumn.getInputType().getName()) {
                        case "INPUT_DEGREEDATE":
                            // Column's values are of type datetime
                            filterString.replaceAll("[TZ:.\\-]", " ");
                            sqlWhereBuilder.append("CAST(pfv").append(n).append(".value AS TIMESTAMP) = '").append(filterString).append("'");
                            break;
                        case "INPUT_DATETIME":
                            // Column's values are of type datetime
                            if (filterString.contains("|")) {
                                // Date Range
                                String[] dates = filterString.split(Pattern.quote("|"));
                                dates[0] = dates[0].replaceAll("[TZ:.\\-]", " ");
                                dates[1] = dates[1].replaceAll("[TZ:.\\-]", " ");

                                sqlWhereBuilder.append("CAST(pfv").append(n).append(".value AS TIMESTAMP) BETWEEN to_timestamp('").append(dates[0]).append("', \'YYYY MM DD HH MI SS MS') AND to_timestamp('").append(dates[1]).append("', \'YYYY MM DD HH MI SS MS')");
                            } else {
                                // Date Match
                                filterString.replaceAll("[TZ:.\\-]", " ");
                                sqlWhereBuilder.append("CAST(pfv").append(n).append(".value AS TIMESTAMP) = '").append(filterString).append("'");
                            }
                            break;
                        case "INPUT_CHECKBOX":
                            sqlWhereBuilder.append("pfv").append(n).append(".value = '").append(filterString).append("'");

                            // Column's values are a boolean
                            if (!Boolean.valueOf(filterString)) {
                                sqlWhereBuilderList.add(sqlWhereBuilder);
                                sqlWhereBuilder = new StringBuilder();

                                sqlWhereBuilder.append(" pfv").append(n).append(".value IS NULL");
                            }
                            break;
                        default:
                            // Column's values can be handled by this default
                            if (submissionListColumn.getExactMatch()) {
                                // perform exact match
                                sqlWhereBuilder.append("pfv").append(n).append(".value = '").append(filterString).append("'");
                            } else {
                                // perform like when input from text field
                                sqlWhereBuilder.append("LOWER(pfv").append(n).append(".value) LIKE '%").append(filterString.toLowerCase()).append("%'");
                            }
                            break;
                        }

                        if (sqlWhereBuilder.length() > 0) {
                            sqlWhereBuilderList.add(sqlWhereBuilder);
                        }
                    }

                    // all column search filter
                    for (String filterString : allColumnSearchFilters) {
                        sqlWhereBuilder = new StringBuilder();
                        sqlWhereBuilder.append("LOWER(pfv").append(n).append(".value) LIKE '%").append(filterString.toLowerCase()).append("%'");
                        sqlAllColumnsWhereBuilderList.add(sqlWhereBuilder);
                    }

                    n++;

                    break;

                case "id":

                    if (submissionListColumn.getSortOrder() > 0) {
                        setColumnOrdering(submissionListColumn.getSort(), sqlSelectBuilder, sqlOrderBysBuilder, " s.id");
                    }

                    for (String filterString : submissionListColumn.getFilters()) {
                        sqlWhereBuilder = new StringBuilder();
                        sqlWhereBuilder.append("s").append(".id = ").append(filterString);
                        sqlWhereBuilderList.add(sqlWhereBuilder);
                    }

                    break;

                case "submissionStatus.name":

                    sqlJoinsBuilder.append("\nLEFT JOIN submission_status ss ON ss.id=s.submission_status_id");

                    if (submissionListColumn.getSortOrder() > 0) {
                        setColumnOrdering(submissionListColumn.getSort(), sqlSelectBuilder, sqlOrderBysBuilder, " ss.name");
                    }

                    for (String filterString : submissionListColumn.getFilters()) {
                        sqlWhereBuilder = new StringBuilder();

                        if (submissionListColumn.getExactMatch()) {
                            sqlWhereBuilder.append("ss").append(".name = '").append(filterString).append("'");
                        } else {
                            // TODO: determine if status will ever be search using a like
                            sqlWhereBuilder.append("LOWER(ss").append(".name) LIKE '%").append(filterString.toLowerCase()).append("%'");
                        }

                        sqlWhereBuilderList.add(sqlWhereBuilder);
                    }

                    // all column search filter
                    for (String filterString : allColumnSearchFilters) {
                        sqlWhereBuilder = new StringBuilder();
                        sqlWhereBuilder.append("LOWER(ss").append(".name) LIKE '%").append(filterString.toLowerCase()).append("%'");
                        sqlAllColumnsWhereBuilderList.add(sqlWhereBuilder);
                    }

                    break;

                case "organization.name":

                    if (sqlJoinsBuilder.indexOf("LEFT JOIN organization o ON o.id=s.organization_id") == -1) {
                        sqlJoinsBuilder.append("\nLEFT JOIN organization o ON o.id=s.organization_id");
                    }

                    if (submissionListColumn.getSortOrder() > 0) {
                        setColumnOrdering(submissionListColumn.getSort(), sqlSelectBuilder, sqlOrderBysBuilder, " o.name");
                    }

                    for (String filterString : submissionListColumn.getFilters()) {
                        sqlWhereBuilder = new StringBuilder();

                        if (submissionListColumn.getExactMatch()) {
                            sqlWhereBuilder.append("o").append(".name = '").append(filterString).append("'");
                        } else {
                            // TODO: determine if organization name will ever be
                            // search using a like
                            sqlWhereBuilder.append("LOWER(o").append(".name) LIKE '%").append(filterString.toLowerCase()).append("%'");
                        }

                        sqlWhereBuilderList.add(sqlWhereBuilder);
                    }

                    // all column search filter
                    for (String filterString : allColumnSearchFilters) {
                        sqlWhereBuilder = new StringBuilder();
                        sqlWhereBuilder.append("LOWER(o").append(".name) LIKE '%").append(filterString.toLowerCase()).append("%'");
                        sqlAllColumnsWhereBuilderList.add(sqlWhereBuilder);
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
                        sqlWhereBuilder = new StringBuilder();
                        if (submissionListColumn.getExactMatch()) {
                            sqlWhereBuilder.append("oc").append(".name = '").append(filterString).append("'");
                        } else {
                            // TODO: determine if organization category name
                            // will ever be search using a like
                            sqlWhereBuilder.append("LOWER(oc").append(".name) LIKE '%").append(filterString.toLowerCase()).append("%'");
                        }

                        sqlWhereBuilderList.add(sqlWhereBuilder);
                    }

                    // all column search filter
                    for (String filterString : allColumnSearchFilters) {
                        sqlWhereBuilder = new StringBuilder();
                        sqlWhereBuilder.append("LOWER(oc").append(".name) LIKE '%").append(filterString.toLowerCase()).append("%'");
                        sqlAllColumnsWhereBuilderList.add(sqlWhereBuilder);
                    }

                    break;

                case "assignee.email":

                    sqlJoinsBuilder.append("\nLEFT JOIN weaver_users a ON a.id=s.assignee_id");

                    if (submissionListColumn.getSortOrder() > 0) {
                        setColumnOrdering(submissionListColumn.getSort(), sqlSelectBuilder, sqlOrderBysBuilder, " a.email");
                    }

                    for (String filterString : submissionListColumn.getFilters()) {
                        sqlWhereBuilder = new StringBuilder();

                        if (filterString == null) {
                            sqlWhereBuilder.append("a").append(".email IS NULL");
                        } else if (submissionListColumn.getExactMatch()) {
                            sqlWhereBuilder.append("a").append(".email = '").append(filterString).append("'");
                        } else {
                            sqlWhereBuilder.append("LOWER(a").append(".email) LIKE '%").append(filterString.toLowerCase()).append("%'");
                        }

                        sqlWhereBuilderList.add(sqlWhereBuilder);
                    }

                    // all column search filter
                    for (String filterString : allColumnSearchFilters) {
                        sqlWhereBuilder = new StringBuilder();
                        sqlWhereBuilder.append("LOWER(a").append(".email) LIKE '%").append(filterString.toLowerCase()).append("%'");
                        sqlAllColumnsWhereBuilderList.add(sqlWhereBuilder);
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
                        sqlWhereBuilder = new StringBuilder();

                        sqlWhereBuilder.append(" embs").append(".name = '").append(filterString).append("'");

                        if (filterString.equals("None")) {
                            sqlWhereBuilderList.add(sqlWhereBuilder);
                            sqlWhereBuilder = new StringBuilder();
                            sqlWhereBuilder.append("embs.id IS NULL");
                        }

                        sqlWhereBuilderList.add(sqlWhereBuilder);
                    }

                    // all column search filter
                    for (String filterString : allColumnSearchFilters) {
                        sqlWhereBuilder = new StringBuilder();
                        sqlWhereBuilder.append("LOWER(embs").append(".name) LIKE '%").append(filterString.toLowerCase()).append("%'");
                        sqlAllColumnsWhereBuilderList.add(sqlWhereBuilder);
                    }

                    break;

                case "lastEvent":
                    // @formatter:off

                    sqlJoinsBuilder.append("\nLEFT JOIN")
                                   .append("\n   (SELECT al.id, al.action_date, al.entry, al.action_logs_id")
                                   .append("\n   FROM action_log al")
                                   .append("\n   WHERE (al.action_logs_id = id)")
                                   .append("\n   ORDER BY al.action_date DESC")
                                   .append("\n   LIMIT 1) als")
                                   .append("\n   ON action_logs_id = s.submission_status_id");
                    // @formatter:on

                    // TODO: finish sqlWheresBuilder.

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
                case "submissionDate":

                    if (submissionListColumn.getSortOrder() > 0) {
                        setColumnOrdering(submissionListColumn.getSort(), sqlSelectBuilder, sqlOrderBysBuilder, " s.submission_date");
                    }

                    for (String filterString : submissionListColumn.getFilters()) {
                        filterString.replaceAll("[TZ:.\\-]", " ");

                        sqlWhereBuilderList.add(new StringBuilder()
                            .append("CAST(s.submission_date AS TIMESTAMP) = '").append(filterString).append("'"));
                    }

                    break;
                case "approveApplicationDate":

                    if (submissionListColumn.getSortOrder() > 0) {
                        setColumnOrdering(submissionListColumn.getSort(), sqlSelectBuilder, sqlOrderBysBuilder, " s.approve_application_date");
                    }

                    for (String filterString : submissionListColumn.getFilters()) {
                        filterString.replaceAll("[TZ:.\\-]", " ");

                        sqlWhereBuilderList.add(new StringBuilder()
                            .append("CAST(s.approve_application_date AS TIMESTAMP) = '").append(filterString).append("'"));
                    }

                    break;
                case "approveAdvisorDate":

                    if (submissionListColumn.getSortOrder() > 0) {
                        setColumnOrdering(submissionListColumn.getSort(), sqlSelectBuilder, sqlOrderBysBuilder, " s.approve_advisor_date");
                    }

                    for (String filterString : submissionListColumn.getFilters()) {
                        filterString.replaceAll("[TZ:.\\-]", " ");

                        sqlWhereBuilderList.add(new StringBuilder()
                            .append("CAST(s.approve_advisor_date AS TIMESTAMP) = '").append(filterString).append("'"));
                    }

                    break;
                case "approveEmbargoDate":

                    if (submissionListColumn.getSortOrder() > 0) {
                        setColumnOrdering(submissionListColumn.getSort(), sqlSelectBuilder, sqlOrderBysBuilder, " s.approve_embargo_date");
                    }

                    for (String filterString : submissionListColumn.getFilters()) {
                        filterString.replaceAll("[TZ:.\\-]", " ");

                        sqlWhereBuilderList.add(new StringBuilder()
                            .append("CAST(s.approve_embargo_date AS TIMESTAMP) = '").append(filterString).append("'"));
                    }

                    break;
                case "customActionValues":

                    // @formatter:off
                    sqlJoinsBuilder.append("\nLEFT JOIN")
                                   .append("\n   (SELECT submission_id, value, label")
                                   .append("\n   FROM submission_custom_action_values scav")
                                   .append("\n   LEFT JOIN custom_action_value cav ON scav.custom_action_values_id = cav .id")
                                   .append("\n   LEFT JOIN custom_action_definition cad ON cav.definition_id = cad.id) scavcavcad")
                                   .append("\n   ON scavcavcad.submission_id = s.id");
                    // @formatter:on

                    for (String filterString : submissionListColumn.getFilters()) {
                        sqlWhereBuilder = new StringBuilder();
                        sqlWhereBuilder.append("scavcavcad.value = true AND scavcavcad.label = '" + filterString + "'");
                        sqlWhereBuilderList.add(sqlWhereBuilder);
                    }

                    break;
                default:
                    logger.info("No value path given for submission list column " + String.join(".", submissionListColumn.getValuePath()) + ": " + submissionListColumn.getTitle());
                }

                if (sqlWhereBuilderList.size() > 0) {
                    sqlColumnsBuilders.put(submissionListColumn.getId(), sqlWhereBuilderList);
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

        // build WHERE query such that OR is used for conditions inside a column and AND is used across each different column.
        sqlWhereBuilder = new StringBuilder();
        if (sqlColumnsBuilders.size() > 0 || sqlAllColumnsWhereBuilderList.size() > 0 || sqlWheresExcludeBuilder.length() > 0) {
            sqlWhereBuilder.append("\nWHERE ");

            for (Entry<Long, ArrayList<StringBuilder>> list : sqlColumnsBuilders.entrySet()) {
                sqlWhereBuilder.append("(");

                for (StringBuilder builder : list.getValue()) {
                    sqlWhereBuilder.append("(").append(builder).append(") OR ");
                }

                // remove last " OR".
                sqlWhereBuilder.setLength(sqlWhereBuilder.length() - 4);

                sqlWhereBuilder.append(") AND ");
            }

            if (sqlAllColumnsWhereBuilderList.size() > 0) {
                sqlWhereBuilder.append("(");

                for (StringBuilder builder : sqlAllColumnsWhereBuilderList) {
                    sqlWhereBuilder.append("(").append(builder).append(") OR ");
                }

                // remove last " OR".
                sqlWhereBuilder.setLength(sqlWhereBuilder.length() - 4);

                sqlWhereBuilder.append(") AND ");
            }

            if (sqlWheresExcludeBuilder.length() > 0) {
                sqlWhereBuilder.append("(").append(sqlWheresExcludeBuilder).append(")");
            } else {
                // remove last " AND"
                sqlWhereBuilder.setLength(sqlWhereBuilder.length() - 5);
            }
        }

        String sqlQuery = sqlSelectBuilder.toString() + sqlJoinsBuilder.toString() + sqlWhereBuilder.toString();
        String sqlCountQuery = sqlCountSelectBuilder.toString() + sqlJoinsBuilder.toString() + sqlWhereBuilder.toString();

        if (pageable != null) {
            // determine the offset and limit of the query
            int offset = pageable.getPageSize() * pageable.getPageNumber();
            int limit = pageable.getPageSize();
            sqlQuery += sqlOrderBysBuilder.toString() + "\nLIMIT " + limit + " OFFSET " + offset + ";";
        }

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
