package org.tdl.vireo.model.repo.impl;

import static edu.tamu.weaver.response.ApiAction.CREATE;
import static edu.tamu.weaver.response.ApiAction.DELETE;
import static edu.tamu.weaver.response.ApiAction.UPDATE;
import static edu.tamu.weaver.response.ApiStatus.SUCCESS;

import javax.sql.DataSource;

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
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
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
import org.tdl.vireo.config.AppFilterConfig;
import org.tdl.vireo.config.VireoDatabaseConfig;
import org.tdl.vireo.exception.OrganizationDoesNotAcceptSubmissionsException;
import org.tdl.vireo.model.Action;
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
    private CustomActionValueRepo customActionValueRepo;

    @Autowired
    private ConfigurationRepo configurationRepo;

    @Autowired
    private ActionLogRepo actionLogRepo;

    @Autowired
    private AssetService assetService;

    @Autowired
    private AppFilterConfig appFilterConfig;

    @Autowired
    private VireoDatabaseConfig vireoDatabaseConfig;

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

        actionLogRepo.createPublicLog(Action.UNDETERMINED, submission, user, "Submission status was changed from " + oldSubmissionStatusName + " to " + submissionStatus.getName());
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
    public List<Submission> batchDynamicSubmissionQuery(NamedSearchFilterGroup activeFilter, List<SubmissionListColumn> submissionListColumns) {
        QueryStrings queryBuilder = craftDynamicSubmissionQuery(activeFilter, submissionListColumns, null);
        List<Long> ids = new ArrayList<Long>();
        jdbcTemplate.queryForList(queryBuilder.getQuery()).forEach(row -> {
            ids.add((Long) row.get("ID"));
        });

        return submissionRepo.findAllById(ids);
    }

    @Override
    public Page<Submission> pageableDynamicSubmissionQuery(NamedSearchFilterGroup activeFilter, List<SubmissionListColumn> submissionListColumns, Pageable pageable) throws ExecutionException {
        long startTime = System.nanoTime();

        QueryStrings queryBuilder = craftDynamicSubmissionQuery(activeFilter, new ArrayList<>(submissionListColumns), pageable);

        Long total = jdbcTemplate.queryForObject(queryBuilder.getCountQuery(), Long.class);

        logger.debug("Count query for dynamic query took " + ((System.nanoTime() - startTime) / 1000000000.0) + " seconds");
        startTime = System.nanoTime();

        List<Long> ids = jdbcTemplate.query(queryBuilder.getQuery(), new RowMapper<>() {
            public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getLong("ID");
            }
        });

        logger.debug("ID query for dynamic query took " + ((System.nanoTime() - startTime) / 1000000000.0) + " seconds");
        startTime = System.nanoTime();

        List<Submission> submissions = submissionRepo.findAllById(ids);

        logger.debug("Find all query for dynamic query took " + ((System.nanoTime() - startTime) / 1000000000.0) + " seconds");

        // order them
        Map<Long, Integer> idToIndexMap = new HashMap<>();
        for (int i = 0; i < ids.size(); i++) {
            idToIndexMap.put(ids.get(i), i);
        }

        submissions.forEach(submission -> mapColumnValues(submission, submissionListColumns));

        submissions.sort((s1, s2) -> {
            int index1 = idToIndexMap.get(s1.getId());
            int index2 = idToIndexMap.get(s2.getId());
            return Integer.compare(index1, index2);
        });

        logger.debug("Sorting and mapping results took " + ((System.nanoTime() - startTime) / 1000000000.0) + " seconds");

        int offset = pageable.getPageSize() * pageable.getPageNumber();
        int limit = pageable.getPageSize();
        return new PageImpl<Submission>(submissions, PageRequest.of((int) Math.floor(offset / limit), limit), total);
    }

    private void mapColumnValues(Submission submission, List<SubmissionListColumn> submissionListColumns) {
        if (Objects.isNull(submission.getColumnValues())) {
            Map<Long, String> columnValues = new HashMap<>();

            Map<String, List<String>> groupedByPredicate = submission.getFieldValues().stream()
                .collect(Collectors.groupingBy(
                    fv -> fv.getFieldPredicate().getValue(),
                    Collectors.mapping(FieldValue::getValue, Collectors.toList())
                ));

            for (SubmissionListColumn slc : submissionListColumns) {
                if (StringUtils.isNotEmpty(slc.getPredicate())) {
                    StringBuilder value = new StringBuilder();

                    String predicate = slc.getPredicate().trim();

                    if (groupedByPredicate.containsKey(predicate)) {
                        value.append(String.join(", ", groupedByPredicate.get(predicate)));
                    } else {
                        // if the predicate is not found, see if it is space delimited set of predicates
                        if (predicate.contains(" ")) {
                            for (String p : predicate.split(" ")) {
                                String tp = p.replace(",", " ").trim();
                                if (groupedByPredicate.containsKey(tp)) {
                                    value.append(String.join(", ", groupedByPredicate.get(tp)) + (p.endsWith(",") ? ", " : " "));
                                }
                            }
                        }
                    }

                    columnValues.put(slc.getId(), value.toString());
                }
            }

            submission.setColumnValues(columnValues);
        }
    }

    private QueryStrings craftDynamicSubmissionQuery(NamedSearchFilterGroup activeFilter, List<SubmissionListColumn> submissionListColumns, Pageable pageable) {

        // set up storage for user's preferred columns
        Set<String> allColumnSearchFilters = new HashSet<>();

        // get all the possible columns, some of which we will make visible
        List<SubmissionListColumn> allSubmissionListColumns = submissionListColumnRepo.findAll();

        // set sort and sort order on all submission list columns that are set
        // on the requesting user's submission list columns
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
        Collections.sort(allSubmissionListColumns, new Comparator<>() {
            @Override
            public int compare(SubmissionListColumn svc1, SubmissionListColumn svc2) {
                return svc1.getSortOrder().compareTo(svc2.getSortOrder());
            }
        });

        StringBuilder sqlSelectBuilder = new StringBuilder("SELECT DISTINCT ");
        StringBuilder sqlCountSelectBuilder = new StringBuilder();

        Map<Long, ArrayList<StringBuilder>> sqlColumnsBuilders = new HashMap<>();
        Map<String, ArrayList<StringBuilder>> sqlCountWhereFilterBuilders = new HashMap<>();
        Map<Long, StringBuilder> sqlCountWherePredicate = new HashMap<>();
        List<String> sqlAliasBuilders = new ArrayList<>();

        StringBuilder sqlJoinsBuilder = new StringBuilder();
        StringBuilder sqlBuilder;
        StringBuilder sqlCountBuilder;
        StringBuilder sqlWheresExcludeBuilder = new StringBuilder();
        StringBuilder sqlOrderBysBuilder = new StringBuilder();

        ArrayList<StringBuilder> sqlWhereBuilderList;
        ArrayList<StringBuilder> sqlAllColumnsWhereBuilderList = new ArrayList<>();

        // Always have "s.id" alias.
        sqlAliasBuilders.add("s.id");

        int n = 0;
        int totalFieldValueConditions = 0;

        for (int i = 0; i < allSubmissionListColumns.size(); i++) {
            SubmissionListColumn submissionListColumn = allSubmissionListColumns.get(i);

            if (submissionListColumn.getSortOrder() > 0 || submissionListColumn.getFilters().size() > 0 || allColumnSearchFilters.size() > 0 || submissionListColumn.getVisible()) {
                if (sqlColumnsBuilders.containsKey(submissionListColumn.getId())) {
                    sqlWhereBuilderList = sqlColumnsBuilders.get(submissionListColumn.getId());
                } else {
                    sqlWhereBuilderList = new ArrayList<StringBuilder>();
                }

                switch (String.join(".", submissionListColumn.getValuePath())) {
                case "fieldValues.value":

                    FieldPredicate fieldPredicate = fieldPredicateRepo.findByValue(submissionListColumn.getPredicate());

                    // if the predicate is not found, see if it is space delimited set of predicates
                    // for each predicate, add a submission list column clone with predicate and sort order updated
                    if (fieldPredicate == null) {
                        String[] predicates = submissionListColumn.getPredicate().split(" ");
                        int sortOrder = submissionListColumn.getSortOrder();
                        for(int j = 0; j < predicates.length; ++j) {

                            SubmissionListColumn column = new SubmissionListColumn(
                                submissionListColumn.getTitle(),
                                submissionListColumn.getSort(),
                                predicates[j].replace(",", "").trim(),
                                submissionListColumn.getInputType()
                            );

                            column.setSortOrder(sortOrder++);

                            allSubmissionListColumns.add(i + j + 1, column);
                        }

                        continue;
                    }

                    Long predicateId = fieldPredicate.getId();

                    // @formatter:off
                    if (submissionListColumn.getSortOrder() > 0 || submissionListColumn.getFilters().size() > 0) {
                        sqlJoinsBuilder
                            .append("\nLEFT JOIN")
                            .append("\n  (SELECT sfv").append(n).append(".submission_id, fv").append(n).append(".*")
                            .append("\n   FROM submission_field_values sfv").append(n)
                            .append("\n   LEFT JOIN field_value fv").append(n)
                            .append(" ON fv").append(n).append(".id=sfv").append(n).append(".field_values_id ")
                            .append("\n   WHERE fv").append(n).append(".field_predicate_id=").append(predicateId).append(") pfv").append(n)
                            .append("\n ON pfv").append(n).append(".submission_id=s.id");
                    }

                    if (submissionListColumn.getSortOrder() > 0) {
                        if (submissionListColumn.getInputType().getName().equals("INPUT_DEGREEDATE")) {
                            setColumnOrderingForMonthYearDateFormat(submissionListColumn.getSort(), sqlAliasBuilders, sqlOrderBysBuilder, "pfv" + n);
                        } else {
                            setColumnOrdering(submissionListColumn.getSort(), sqlAliasBuilders, sqlOrderBysBuilder, "pfv" + n + ".value");
                        }
                    }

                    for (String filterString : submissionListColumn.getFilters()) {
                        sqlBuilder = new StringBuilder();
                        sqlCountBuilder = new StringBuilder();

                        switch (submissionListColumn.getInputType().getName()) {
                        case "INPUT_DEGREEDATE":
                            // Column's values are of type 'MMMM yyyy' (in SQL date format would be 'Month YYYY').
                            sqlBuilder.append("LOWER(pfv").append(n).append(".value) = LOWER('").append(filterString).append("')");
                            sqlCountBuilder.append("LOWER(fv.value) = LOWER('").append(filterString).append("')");
                            break;
                        case "INPUT_DATE":
                            // Column's values are of type 'yyyy-mm-dd' as required by the SQL standard to represent a date without time.
                            if (filterString.contains("|")) {
                                // Date Range
                                String[] dates = filterString.split(Pattern.quote("|"));
                                sqlBuilder
                                    .append("CAST(pfv").append(n)
                                    .append(".value AS DATE) BETWEEN CAST('").append(dates[0])
                                    .append("' AS DATE) AND CAST('").append(dates[1])
                                    .append("' AS DATE)");
                                sqlCountBuilder
                                    .append("CAST(fv.value AS DATE) BETWEEN CAST('").append(dates[0])
                                    .append("' AS DATE) AND CAST('").append(dates[1])
                                    .append("' AS DATE)");
                            } else {
                                // Date Match
                                sqlBuilder.append("pfv").append(n).append(".value = '").append(filterString).append("'");
                                sqlCountBuilder.append("fv.value = '").append(filterString).append("'");
                            }
                            break;
                        case "INPUT_CHECKBOX":
                            sqlBuilder.append("pfv").append(n).append(".value = '").append(filterString).append("'");
                            sqlCountBuilder.append("fv.value = '").append(filterString).append("'");

                            // Column's values are a boolean
                            if (!Boolean.valueOf(filterString)) {
                                sqlWhereBuilderList.add(sqlBuilder);

                                if (!sqlCountWherePredicate.containsKey(predicateId)) {
                                    sqlCountWherePredicate.put(predicateId, new StringBuilder());
                                }

                                sqlCountWherePredicate.get(predicateId).append(" (").append(sqlCountBuilder).append(") OR");

                                sqlBuilder = new StringBuilder();
                                sqlBuilder.append(" pfv").append(n).append(".value IS NULL");

                                sqlCountBuilder = new StringBuilder();
                                sqlCountBuilder.append(" fv.value IS NULL");
                            }

                            break;
                        default:
                            // Column's values can be handled by this default
                            if (submissionListColumn.getExactMatch()) {
                                // perform exact match
                                sqlBuilder.append("pfv").append(n).append(".value = '").append(filterString).append("'");
                                sqlCountBuilder.append("fv.value = '").append(filterString).append("'");
                            } else {
                                // perform like when input from text field
                                sqlBuilder.append("LOWER(pfv").append(n).append(".value) LIKE '%").append(escapeString(filterString)).append("%'");
                                sqlCountBuilder.append("LOWER(fv.value) LIKE '%").append(escapeString(filterString)).append("%'");
                            }

                            break;
                        }

                        if (sqlBuilder.length() > 0) {
                            totalFieldValueConditions++;
                            sqlWhereBuilderList.add(sqlBuilder);

                            if (!sqlCountWherePredicate.containsKey(predicateId)) {
                                sqlCountWherePredicate.put(predicateId, new StringBuilder());
                            }

                            sqlCountWherePredicate.get(predicateId).append(" (").append(sqlCountBuilder).append(") OR");
                        }
                    }

                    if (submissionListColumn.getSortOrder() > 0 || submissionListColumn.getFilters().size() > 0) {
                        // all column search filter
                        for (String filterString : allColumnSearchFilters) {
                            sqlBuilder = new StringBuilder();
                            sqlBuilder.append("LOWER(pfv").append(n).append(".value) LIKE '%").append(escapeString(filterString)).append("%'");
                            sqlAllColumnsWhereBuilderList.add(sqlBuilder);
                        }

                        n++;
                    }

                    break;

                case "id":

                    if (submissionListColumn.getSortOrder() > 0) {
                        // The s.id is already on the submission, such just add it to the order by rather than call setColumnOrdering().
                        Sort sort = submissionListColumn.getSort();
                        if (sort == Sort.ASC || sort == Sort.DESC) {
                            sqlOrderBysBuilder.append(" s.id ").append(sort.name()).append(",");
                        }
                    }

                    for (String filterString : submissionListColumn.getFilters()) {
                        sqlBuilder = new StringBuilder();
                        sqlBuilder.append("s.id = ").append(filterString);
                        sqlWhereBuilderList.add(sqlBuilder);
                        getFromBuildersMap(sqlCountWhereFilterBuilders, "id").add(sqlBuilder);
                    }

                    break;

                case "submissionStatus.name":
                    sqlBuilder = new StringBuilder()
                        .append("\nLEFT JOIN submission_status ss ON ss.id=s.submission_status_id");

                    sqlJoinsBuilder.append(sqlBuilder);
                    sqlCountSelectBuilder.append(sqlBuilder);

                    if (submissionListColumn.getSortOrder() > 0) {
                        setColumnOrdering(submissionListColumn.getSort(), sqlAliasBuilders, sqlOrderBysBuilder, "ss.name");
                    }

                    for (String filterString : submissionListColumn.getFilters()) {
                        sqlBuilder = new StringBuilder();

                        if (submissionListColumn.getExactMatch()) {
                            sqlBuilder.append("ss.name = '").append(filterString).append("'");
                        } else {
                            // TODO: determine if status will ever be search using a like
                            sqlBuilder.append("LOWER(ss.name) LIKE '%").append(escapeString(filterString)).append("%'");
                        }

                        sqlWhereBuilderList.add(sqlBuilder);
                        getFromBuildersMap(sqlCountWhereFilterBuilders, "submissionStatus.name").add(sqlBuilder);
                    }

                    // all column search filter
                    for (String filterString : allColumnSearchFilters) {
                        sqlBuilder = new StringBuilder();
                        sqlBuilder.append("LOWER(ss.name) LIKE '%").append(escapeString(filterString)).append("%'");
                        sqlAllColumnsWhereBuilderList.add(sqlBuilder);
                    }

                    break;

                case "organization.name":

                    if (sqlJoinsBuilder.indexOf("LEFT JOIN organization o ON o.id=s.organization_id") == -1) {
                        sqlBuilder = new StringBuilder()
                            .append("\nLEFT JOIN organization o ON o.id=s.organization_id");

                        sqlJoinsBuilder.append(sqlBuilder);
                        sqlCountSelectBuilder.append(sqlBuilder);
                    }

                    if (submissionListColumn.getSortOrder() > 0) {
                        setColumnOrdering(submissionListColumn.getSort(), sqlAliasBuilders, sqlOrderBysBuilder, "o.name");
                    }

                    for (String filterString : submissionListColumn.getFilters()) {
                        sqlBuilder = new StringBuilder();

                        if (submissionListColumn.getExactMatch()) {
                            sqlBuilder.append("o.name = '").append(filterString).append("'");
                        } else {
                            // TODO: determine if organization name will ever be search using a like
                            sqlBuilder.append("LOWER(o.name) LIKE '%").append(escapeString(filterString)).append("%'");
                        }

                        sqlWhereBuilderList.add(sqlBuilder);
                        getFromBuildersMap(sqlCountWhereFilterBuilders, "organization.name").add(sqlBuilder);
                    }

                    // all column search filter
                    for (String filterString : allColumnSearchFilters) {
                        sqlBuilder = new StringBuilder();
                        sqlBuilder.append("LOWER(o.name) LIKE '%").append(escapeString(filterString)).append("%'");
                        sqlAllColumnsWhereBuilderList.add(sqlBuilder);
                    }

                    break;

                case "organization.category.name":
                    sqlBuilder = new StringBuilder();

                    if (sqlJoinsBuilder.indexOf("LEFT JOIN organization o ON o.id=s.organization_id") == -1) {
                        sqlBuilder.append("\nLEFT JOIN organization o ON o.id=s.organization_id");
                    }
                    sqlBuilder.append("\nLEFT JOIN organization_category oc ON oc.id=o.category_id");

                    sqlJoinsBuilder.append(sqlBuilder);
                    sqlCountSelectBuilder.append(sqlBuilder);

                    if (submissionListColumn.getSortOrder() > 0) {
                        setColumnOrdering(submissionListColumn.getSort(), sqlAliasBuilders, sqlOrderBysBuilder, "oc.name");
                    }

                    for (String filterString : submissionListColumn.getFilters()) {
                        sqlBuilder = new StringBuilder();
                        if (submissionListColumn.getExactMatch()) {
                            sqlBuilder.append("oc.name = '").append(filterString).append("'");
                        } else {
                            // TODO: determine if organization category name
                            // will ever be search using a like
                            sqlBuilder.append("LOWER(oc.name) LIKE '%").append(escapeString(filterString)).append("%'");
                        }

                        sqlWhereBuilderList.add(sqlBuilder);
                        getFromBuildersMap(sqlCountWhereFilterBuilders, "organization.category.name").add(sqlBuilder);
                    }

                    // all column search filter
                    for (String filterString : allColumnSearchFilters) {
                        sqlBuilder = new StringBuilder();
                        sqlBuilder.append("LOWER(oc.name) LIKE '%").append(escapeString(filterString)).append("%'");
                        sqlAllColumnsWhereBuilderList.add(sqlBuilder);
                    }

                    break;

                case "assignee.email":
                    sqlBuilder = new StringBuilder()
                        .append("\nLEFT JOIN weaver_users a ON a.id=s.assignee_id");

                    sqlJoinsBuilder.append(sqlBuilder);
                    sqlCountSelectBuilder.append(sqlBuilder);

                    if (submissionListColumn.getSortOrder() > 0) {
                        setColumnOrdering(submissionListColumn.getSort(), sqlAliasBuilders, sqlOrderBysBuilder, "a.email");
                    }

                    for (String filterString : submissionListColumn.getFilters()) {
                        sqlBuilder = new StringBuilder();

                        if (filterString == null) {
                            sqlBuilder.append("a.email IS NULL");
                        } else if (submissionListColumn.getExactMatch()) {
                            sqlBuilder.append("a.email = '").append(filterString).append("'");
                        } else {
                            sqlBuilder.append("LOWER(a.email) LIKE '%").append(escapeString(filterString)).append("%'");
                        }

                        sqlWhereBuilderList.add(sqlBuilder);
                        getFromBuildersMap(sqlCountWhereFilterBuilders, "assignee.email").add(sqlBuilder);
                    }

                    // all column search filter
                    for (String filterString : allColumnSearchFilters) {
                        sqlBuilder = new StringBuilder();
                        sqlBuilder.append("LOWER(a.email) LIKE '%").append(escapeString(filterString)).append("%'");
                        sqlAllColumnsWhereBuilderList.add(sqlBuilder);
                    }

                    break;

                case "lastAction.entry":
                    sqlBuilder = new StringBuilder();
                    if (!sqlJoinsBuilder.toString().contains("LEFT JOIN action_log al ON al.id=s.last_action_id")) {
                        sqlBuilder.append("\nLEFT JOIN action_log al ON al.id=s.last_action_id");
                    }

                    sqlJoinsBuilder.append(sqlBuilder);
                    sqlCountSelectBuilder.append(sqlBuilder);

                    if (submissionListColumn.getSortOrder() > 0) {
                        setColumnOrdering(submissionListColumn.getSort(), sqlAliasBuilders, sqlOrderBysBuilder, "al.entry");
                    }

                    for (String filterString : submissionListColumn.getFilters()) {
                        sqlBuilder = new StringBuilder();

                        if (filterString == null) {
                            sqlBuilder.append("al.entry IS NULL");
                        } else if (submissionListColumn.getExactMatch()) {
                            sqlBuilder.append("al.entry = '").append(filterString).append("'");
                        } else {
                            sqlBuilder.append("LOWER(al.entry) LIKE '%").append(escapeString(filterString)).append("%'");
                        }

                        sqlWhereBuilderList.add(sqlBuilder);
                        getFromBuildersMap(sqlCountWhereFilterBuilders, "lastAction.entry").add(sqlBuilder);
                    }

                    // all column search filter
                    for (String filterString : allColumnSearchFilters) {
                        sqlBuilder = new StringBuilder();
                        sqlBuilder.append("LOWER(al.entry) LIKE '%").append(escapeString(filterString)).append("%'");
                        sqlAllColumnsWhereBuilderList.add(sqlBuilder);
                    }

                    break;

                case "lastAction.actionDate":
                    sqlBuilder = new StringBuilder();
                    if (!sqlJoinsBuilder.toString().contains("LEFT JOIN action_log al ON al.id=s.last_action_id")) {
                        sqlBuilder.append("\nLEFT JOIN action_log al ON al.id=s.last_action_id");
                    }

                    sqlJoinsBuilder.append(sqlBuilder);
                    sqlCountSelectBuilder.append(sqlBuilder);

                    if (submissionListColumn.getSortOrder() > 0) {
                        setColumnOrdering(submissionListColumn.getSort(), sqlAliasBuilders, sqlOrderBysBuilder, "al.action_date");
                    }

                    for (String filterString : submissionListColumn.getFilters()) {
                        if (filterString.contains("|")) {
                            String[] dates = filterString.split(Pattern.quote("|"));
                            sqlBuilder = new StringBuilder()
                                .append("al.action_date")
                                .append(" BETWEEN CAST('").append(dates[0])
                                .append("' AS DATE) AND CAST('").append(dates[1])
                                .append("' AS DATE)");
                        } else {
                            sqlBuilder = new StringBuilder()
                                .append("al.action_date")
                                .append(" = CAST('").append(filterString)
                                .append("' AS DATE)");
                        }

                        sqlWhereBuilderList.add(sqlBuilder);
                        getFromBuildersMap(sqlCountWhereFilterBuilders, "lastAction.actionDate").add(sqlBuilder);
                    }

                    break;

                case "embargoTypes.name":
                    // This is not a select column but is instead only a custom filter.
                    if (submissionListColumn.getFilters().size() > 0) {
                        sqlBuilder = new StringBuilder();
                        sqlBuilder.append("s.id IN (SELECT submission_id FROM submission_field_values WHERE field_values_id IN (select id FROM field_value WHERE field_predicate_id IN (SELECT id FROM field_predicate WHERE value IN ('default_embargos', 'proquest_embargos')) and (");

                        // Note that the OR query is used inside the column, represented by both default_embargos and proquest_embargos.
                        boolean hasNone = false;
                        for (String filterString : submissionListColumn.getFilters()) {
                            if (filterString != null) {
                                sqlBuilder.append(" value = '").append(escapeString(filterString, false, true)).append("' OR");
                            }

                            if (appFilterConfig.getEmbargoTypeNone() != null) {
                              if (appFilterConfig.getEmbargoTypeNone().equalsIgnoreCase(filterString)) {
                                  hasNone = true;
                              }
                            }
                        }
                        sqlBuilder.setLength(sqlBuilder.length() - 3);
                        sqlBuilder.append(")))");

                        if (hasNone) {
                            sqlBuilder.append(" OR s.id NOT IN (SELECT submission_id FROM submission_field_values WHERE field_values_id IN (SELECT id FROM field_value WHERE field_predicate_id IN (SELECT id FROM field_predicate WHERE value IN ('default_embargos', 'proquest_embargos'))))");
                        }

                        sqlWhereBuilderList.add(sqlBuilder);
                        getFromBuildersMap(sqlCountWhereFilterBuilders, "embargoTypes.name").add(sqlBuilder);
                    }

                    break;

                case "submissionTypes.name":
                    // This is not a select column but is instead only a custom filter.
                    if (submissionListColumn.getFilters().size() > 0) {
                        sqlBuilder = new StringBuilder();
                        sqlBuilder.append("s.id IN (SELECT submission_id FROM submission_field_values WHERE field_values_id IN (select id FROM field_value WHERE field_predicate_id IN (SELECT id FROM field_predicate WHERE value = 'submission_type') and (");

                        // Note that the OR query is used inside the column, represented by submission_type.
                        boolean hasNone = false;
                        for (String filterString : submissionListColumn.getFilters()) {
                            if (filterString != null) {
                                sqlBuilder.append(" value = '").append(escapeString(filterString, false, true)).append("' OR");
                            }

                            if (appFilterConfig.getSubmissionTypeNone() != null) {
                              if (appFilterConfig.getSubmissionTypeNone().equalsIgnoreCase(filterString)) {
                                  hasNone = true;
                              }
                            }
                        }
                        sqlBuilder.setLength(sqlBuilder.length() - 3);
                        sqlBuilder.append(")))");

                        if (hasNone) {
                            sqlBuilder.append(" OR s.id NOT IN (SELECT submission_id FROM submission_field_values WHERE field_values_id IN (SELECT id FROM field_value WHERE field_predicate_id IN (SELECT id FROM field_predicate WHERE value = 'submission_type')))");
                        }

                        sqlWhereBuilderList.add(sqlBuilder);
                        getFromBuildersMap(sqlCountWhereFilterBuilders, "submissionTypes.name").add(sqlBuilder);
                    }

                    break;

                // exclude individual submissions from submission list
                case "exclude":

                    if (submissionListColumn.getSortOrder() > 0) {
                        setColumnOrdering(submissionListColumn.getSort(), sqlAliasBuilders, sqlOrderBysBuilder, "s.id");
                    }

                    for (String filterString : submissionListColumn.getFilters()) {
                        if (sqlWheresExcludeBuilder.length() > 0) {
                            sqlWheresExcludeBuilder.append(" AND s.id <> ").append(filterString);
                        } else {
                            sqlWheresExcludeBuilder.append(" s.id <> ").append(filterString);
                        }
                    }

                    break;
                case "submissionDate":

                    if (submissionListColumn.getSortOrder() > 0) {
                        setColumnOrdering(submissionListColumn.getSort(), sqlAliasBuilders, sqlOrderBysBuilder, "s.submission_date");
                    }

                    for (String filterString : submissionListColumn.getFilters()) {
                        sqlBuilder = buildSubmissionDateFieldString("submission_date", filterString);
                        sqlWhereBuilderList.add(sqlBuilder);
                        getFromBuildersMap(sqlCountWhereFilterBuilders, "submissionDate").add(sqlBuilder);
                    }

                    break;
                case "approveApplicationDate":

                    if (submissionListColumn.getSortOrder() > 0) {
                        setColumnOrdering(submissionListColumn.getSort(), sqlAliasBuilders, sqlOrderBysBuilder, "s.approve_application_date");
                    }

                    for (String filterString : submissionListColumn.getFilters()) {
                        sqlBuilder = buildSubmissionDateFieldString("approve_application_date", filterString);
                        sqlWhereBuilderList.add(sqlBuilder);
                        getFromBuildersMap(sqlCountWhereFilterBuilders, "approveApplicationDate").add(sqlBuilder);
                    }

                    break;
                case "approveAdvisorDate":

                    if (submissionListColumn.getSortOrder() > 0) {
                        setColumnOrdering(submissionListColumn.getSort(), sqlAliasBuilders, sqlOrderBysBuilder, "s.approve_advisor_date");
                    }

                    for (String filterString : submissionListColumn.getFilters()) {
                        sqlBuilder = buildSubmissionDateFieldString("approve_advisor_date", filterString);
                        sqlWhereBuilderList.add(sqlBuilder);
                        getFromBuildersMap(sqlCountWhereFilterBuilders, "approveAdvisorDate").add(sqlBuilder);
                    }

                    break;
                case "approveEmbargoDate":

                    if (submissionListColumn.getSortOrder() > 0) {
                        setColumnOrdering(submissionListColumn.getSort(), sqlAliasBuilders, sqlOrderBysBuilder, "s.approve_embargo_date");
                    }

                    for (String filterString : submissionListColumn.getFilters()) {
                        sqlBuilder = buildSubmissionDateFieldString("approve_embargo_date", filterString);
                        sqlWhereBuilderList.add(sqlBuilder);
                        getFromBuildersMap(sqlCountWhereFilterBuilders, "approveEmbargoDate").add(sqlBuilder);
                    }

                    break;
                case "customActionValues":
                    sqlBuilder = new StringBuilder()
                        .append("\nLEFT JOIN")
                        .append("\n   (SELECT submission_id, value, label")
                        .append("\n   FROM submission_custom_action_values scav")
                        .append("\n   LEFT JOIN custom_action_value cav ON scav.custom_action_values_id = cav .id")
                        .append("\n   LEFT JOIN custom_action_definition cad ON cav.definition_id = cad.id) scavcavcad")
                        .append("\n   ON scavcavcad.submission_id = s.id");

                    sqlJoinsBuilder.append(sqlBuilder);
                    sqlCountSelectBuilder.append(sqlBuilder);

                    for (String filterString : submissionListColumn.getFilters()) {
                        sqlBuilder = new StringBuilder();
                        sqlBuilder.append("scavcavcad.value = true AND scavcavcad.label = '" + filterString + "'");
                        sqlWhereBuilderList.add(sqlBuilder);
                        getFromBuildersMap(sqlCountWhereFilterBuilders, "customActionValues").add(sqlBuilder);
                    }

                    break;

                case "depositURL":
                    if (submissionListColumn.getSortOrder() > 0) {
                        setColumnOrdering(submissionListColumn.getSort(), sqlAliasBuilders, sqlOrderBysBuilder, "s.depositurl");
                    }

                    for (String filterString : submissionListColumn.getFilters()) {
                        sqlBuilder = new StringBuilder();
                        sqlBuilder.append("LOWER(s.depositurl) LIKE '%").append(escapeString(filterString)).append("%'");
                        sqlWhereBuilderList.add(sqlBuilder);
                        getFromBuildersMap(sqlCountWhereFilterBuilders, "depositurl").add(sqlBuilder);
                    }

                    // all column search filter
                    for (String filterString : allColumnSearchFilters) {
                        sqlBuilder = new StringBuilder();
                        sqlBuilder.append("LOWER(s.depositurl) LIKE '%").append(escapeString(filterString)).append("%'");
                        sqlAllColumnsWhereBuilderList.add(sqlBuilder);
                    }

                    break;

                case "reviewerNotes":
                    if (submissionListColumn.getSortOrder() > 0) {
                        setColumnOrdering(submissionListColumn.getSort(), sqlAliasBuilders, sqlOrderBysBuilder, "s.reviewer_notes");
                    }

                    for (String filterString : submissionListColumn.getFilters()) {
                        sqlBuilder = new StringBuilder();
                        sqlBuilder.append("LOWER(s.reviewer_notes) LIKE '%").append(escapeString(filterString)).append("%'");
                        sqlWhereBuilderList.add(sqlBuilder);
                        getFromBuildersMap(sqlCountWhereFilterBuilders, "reviewer_notes").add(sqlBuilder);
                    }

                    // all column search filter
                    for (String filterString : allColumnSearchFilters) {
                        sqlBuilder = new StringBuilder();
                        sqlBuilder.append("LOWER(s.reviewer_notes) LIKE '%").append(escapeString(filterString)).append("%'");
                        sqlAllColumnsWhereBuilderList.add(sqlBuilder);
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

        // Complete the select clause.
        sqlAliasBuilders.forEach(string -> {
            sqlSelectBuilder.append(string).append(", ");
        });
        sqlSelectBuilder.setLength(sqlSelectBuilder.length() - 2);
        sqlSelectBuilder.append(" FROM submission s");

        // if ordering, complete order by clause and strip the tailing comma
        if (sqlOrderBysBuilder.length() > 0) {
            sqlOrderBysBuilder.insert(0, "\nORDER BY");
            sqlOrderBysBuilder.setLength(sqlOrderBysBuilder.length() - 1);
        }

        // build WHERE query such that OR is used for conditions inside a column and AND is used across each different column.
        sqlBuilder = new StringBuilder();
        if (sqlColumnsBuilders.size() > 0 || sqlAllColumnsWhereBuilderList.size() > 0 || sqlWheresExcludeBuilder.length() > 0) {
            sqlBuilder.append("\nWHERE ");

            for (Entry<Long, ArrayList<StringBuilder>> list : sqlColumnsBuilders.entrySet()) {
                sqlBuilder.append("(");

                for (StringBuilder builder : list.getValue()) {
                    sqlBuilder.append("(").append(builder).append(") OR ");
                }

                // remove last " OR ".
                sqlBuilder.setLength(sqlBuilder.length() - 4);

                sqlBuilder.append(") AND ");
            }

            if (sqlAllColumnsWhereBuilderList.size() > 0) {
                sqlBuilder.append("(");

                for (StringBuilder builder : sqlAllColumnsWhereBuilderList) {
                    sqlBuilder.append("(").append(builder).append(") OR ");
                }

                // remove last " OR ".
                sqlBuilder.setLength(sqlBuilder.length() - 4);

                sqlBuilder.append(") AND ");
            }

            if (sqlWheresExcludeBuilder.length() > 0) {
                sqlBuilder.append("(").append(sqlWheresExcludeBuilder).append(")");
            } else {
                // remove last " AND "
                sqlBuilder.setLength(sqlBuilder.length() - 5);
            }
        }

        if (sqlCountWherePredicate.size() > 0 || sqlCountWhereFilterBuilders.size() > 0 || sqlWheresExcludeBuilder.length() > 0) {

            // Conditions are AND across different predicates but are OR within the same predicate.
            if (sqlCountWherePredicate.size() > 0) {
                sqlCountSelectBuilder.append("\nLEFT JOIN submission_field_values sfv ON s.id = sfv.submission_id");
                sqlCountSelectBuilder.append("\nINNER JOIN field_value fv ON sfv.field_values_id = fv.id");
                sqlCountSelectBuilder.append("\nWHERE");

                sqlCountWherePredicate.forEach((id, filter) -> {
                    if (filter.length() > 0) {
                        // Remove the last " OR".
                        filter.setLength(filter.length() - 3);

                        sqlCountSelectBuilder
                            .append("\n(fv.field_predicate_id = ").append(id)
                            .append(" AND (").append(filter).append(")) AND");
                    }
                });
            } else {
                sqlCountSelectBuilder.append("\nWHERE");
            }

            // Conditions are AND across different filters and OR within the same filter.
            if (sqlCountWhereFilterBuilders.size() > 0) {
                sqlCountWhereFilterBuilders.forEach((key, list) -> {
                    sqlCountSelectBuilder.append(" (");

                    list.forEach(filter -> {
                        sqlCountSelectBuilder.append(" (").append(filter).append(") OR");
                    });

                    // Remove the last " OR".
                    sqlCountSelectBuilder.setLength(sqlCountSelectBuilder.length() - 3);

                    sqlCountSelectBuilder.append(")\n AND");
                });
            }

            if (sqlWheresExcludeBuilder.length() > 0) {
                sqlCountSelectBuilder.append("\n(").append(sqlWheresExcludeBuilder).append(")");
            } else {
                // remove last " AND"
                sqlCountSelectBuilder.setLength(sqlCountSelectBuilder.length() - 4);
            }
        }

        String sqlQuery = sqlSelectBuilder.toString() + sqlJoinsBuilder.toString() + sqlBuilder.toString();
        String sqlCountQuery = "SELECT COUNT(DISTINCT s.id) FROM submission s";

        // Use count query optimization only when there are fewer than 2 field values in the where clause.
        if (totalFieldValueConditions > 1) {
            sqlCountQuery += sqlJoinsBuilder.toString() + sqlBuilder.toString();
        } else {
            sqlCountQuery += sqlCountSelectBuilder.toString();
        }

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

    public void setColumnOrdering(Sort sort, List<String> sqlAliasBuilders, StringBuilder sqlOrderBysBuilder, String value) {
        if (sort == Sort.ASC || sort == Sort.DESC) {
            if (!sqlAliasBuilders.contains(value)) {
                sqlAliasBuilders.add(value);
            }
            sqlOrderBysBuilder.append("\n ").append(value).append(" ").append(sort.name()).append(",");
        }
    }

    /**
     * Handle case where Postgresql requires help casting the date string to a date type.
     *
     * Some SQL engines can directly cast the "Month Year" format to a date but postgresql cannot.
     * Because of this limitation in Postgresql, all dates cannot be cast.
     * Instead, the "Month Year" formatted data must be handled as an exception case.
     *
     * This converts the "Month Year" format into a "Month Day, Year" format to make Postgresql happy.
     * Then this converts that date resulting string into a date type for proper SQL sorting.
     *
     * This would also be easier to add the cast on the ORDER BY and not need to add a column in the select clause.
     * However, another Postgresql problem prevents this from working when DISTINCT is in use.
     * Postgresql will falsely claim that pfv0.value is not in the SELECT clause when it actually is while DISTINCT is present.
     *
     * @param sort The sort direction.
     * @param sqlAliasBuilders The SQL select alias builder string.
     * @param sqlOrderBysBuilder The SQL order by builder string.
     * @param table The table to select from when joining on the assumption that the value is "table".value.
     */
    public void setColumnOrderingForMonthYearDateFormat(Sort sort, List<String> sqlAliasBuilders, StringBuilder sqlOrderBysBuilder, String table) {
        if (sort == Sort.ASC || sort == Sort.DESC) {
            StringBuilder value = new StringBuilder(table).append(".value, ");

            if ("h2".equals(vireoDatabaseConfig.getPlatform())) {
                value.append("PARSEDATETIME(").append(table).append(".value, 'MMM yyyy') AS ");
            } else {
                value.append("CAST(REPLACE(").append(table).append(".value, ' ', ' 1, ') AS DATE) AS ");
            }

            value.append(table).append("_date");

            if (!sqlAliasBuilders.contains(value.toString())) {
                sqlAliasBuilders.add(value.toString());
            }

            sqlOrderBysBuilder.append(" ").append(table).append("_date ").append(sort.name()).append(",");
        }
    }

    @Override
    protected String getChannel() {
        return "/channel/submission";
    }

    /**
     * Build a submission date field string given some filter.
     *
     * This is form submission date fields that are already stored in the SQL date format.
     *
     * @param column The column name to filter.
     * @param filter The filter.
     * @return A constructed string builder appropriately casting the date.
     */
    private StringBuilder buildSubmissionDateFieldString(String column, String filter) {
        if (filter.contains("|")) {
            String[] dates = filter.split(Pattern.quote("|"));
            return new StringBuilder()
                .append("s.").append(column)
                .append(" BETWEEN CAST('").append(dates[0])
                .append("' AS DATE) AND CAST('").append(dates[1])
                .append("' AS DATE)");
        }
        return new StringBuilder()
            .append("s.").append(column)
            .append(" = CAST('").append(filter)
            .append("' AS DATE)");
    }

    /**
     * Get the builders list array for some key, initializing that key if not found.
     *
     * @param map The map of builders to select from.
     * @param key The identifier.
     *
     * @return An array of the builders for the given key.
     */
    private ArrayList<StringBuilder> getFromBuildersMap(Map<String, ArrayList<StringBuilder>> map, String key) {
        if (!map.containsKey(key)) {
            map.put(key, new ArrayList<StringBuilder>());
        }

        return map.get(key);
    }

    /**
     * Add SQL escape protection for string, defaulting to forcing lower case.
     *
     * This handles escaping: "\", "_", and "%".
     * This is expected to be used for LIKE statements.
     *
     * @param original
     * @return
     *   An escaped string.
     */
    private String escapeString(String original) {
        return escapeString(original, true, false);
    }

    /**
     * Add SQL escape protection for string.
     *
     * This handles escaping: "\", "_", "%", and "'".
     * This is expected to be used for LIKE statements.
     *
     * @param original The original string to escape.
     * @param lower TRUE to make lower case, FALSE to leave case alone.
     * @param quoted If TRUE, then only perform escaping on "\" for strings inside of a quote.
     * @return
     *   An escaped string.
     */
    private String escapeString(String original, boolean lower, boolean quoted) {
        String escaped = original.replace("\\", "\\\\");

        if (!quoted) {
            escaped = escaped.replace("'", "''");
            escaped = escaped.replace("_", "\\_");
            escaped = escaped.replace("%", "\\%");
        }

        return lower ? escaped.toLowerCase() : escaped;
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
