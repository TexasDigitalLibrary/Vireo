package org.tdl.vireo.controller;

import static edu.tamu.weaver.response.ApiStatus.ERROR;
import static edu.tamu.weaver.response.ApiStatus.INVALID;
import static edu.tamu.weaver.response.ApiStatus.SUCCESS;

import javax.servlet.http.HttpServletResponse;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.CountingOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.tdl.vireo.ApplicationConstants;
import org.tdl.vireo.exception.BatchExportException;
import org.tdl.vireo.exception.DepositException;
import org.tdl.vireo.exception.OrganizationDoesNotAcceptSubmissionsException;
import org.tdl.vireo.model.Action;
import org.tdl.vireo.model.ActionLog;
import org.tdl.vireo.model.CustomActionValue;
import org.tdl.vireo.model.DepositLocation;
import org.tdl.vireo.model.Embargo;
import org.tdl.vireo.model.FieldPredicate;
import org.tdl.vireo.model.FieldValue;
import org.tdl.vireo.model.NamedSearchFilterGroup;
import org.tdl.vireo.model.Role;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.SubmissionFieldProfile;
import org.tdl.vireo.model.SubmissionListColumn;
import org.tdl.vireo.model.SubmissionStatus;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.VocabularyWord;
import org.tdl.vireo.model.depositor.Depositor;
import org.tdl.vireo.model.export.ExportPackage;
import org.tdl.vireo.model.packager.AbstractPackager;
import org.tdl.vireo.model.repo.ActionLogRepo;
import org.tdl.vireo.model.repo.ConfigurationRepo;
import org.tdl.vireo.model.repo.CustomActionDefinitionRepo;
import org.tdl.vireo.model.repo.CustomActionValueRepo;
import org.tdl.vireo.model.repo.DepositLocationRepo;
import org.tdl.vireo.model.repo.EmbargoRepo;
import org.tdl.vireo.model.repo.FieldValueRepo;
import org.tdl.vireo.model.repo.NamedSearchFilterGroupRepo;
import org.tdl.vireo.model.repo.OrganizationRepo;
import org.tdl.vireo.model.repo.SubmissionFieldProfileRepo;
import org.tdl.vireo.model.repo.SubmissionRepo;
import org.tdl.vireo.model.repo.SubmissionStatusRepo;
import org.tdl.vireo.model.repo.UserRepo;
import org.tdl.vireo.model.response.Views;
import org.tdl.vireo.model.validation.FieldValueValidator;
import org.tdl.vireo.service.AssetService;
import org.tdl.vireo.service.DepositorService;
import org.tdl.vireo.service.SubmissionEmailService;
import org.tdl.vireo.utility.OrcidUtility;
import org.tdl.vireo.utility.PackagerUtility;
import org.tdl.vireo.utility.TemplateUtility;
import org.tdl.vireo.view.FieldValueSubmissionView;

import edu.tamu.weaver.auth.annotation.WeaverCredentials;
import edu.tamu.weaver.auth.annotation.WeaverUser;
import edu.tamu.weaver.auth.model.Credentials;
import edu.tamu.weaver.data.model.ApiPage;
import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.validation.results.ValidationResults;

@RestController
@RequestMapping("/submission")
public class SubmissionController {

    private static final Logger LOG = LoggerFactory.getLogger(SubmissionController.class);

    private static final String STARTING_SUBMISSION_STATUS_NAME = "In Progress";

    private static final String NEEDS_CORRECTION_SUBMISSION_STATUS_NAME = "Needs Correction";

    private static final String CORRECTIONS_RECEIVED_SUBMISSION_STATUS_NAME = "Corrections Received";

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private SubmissionRepo submissionRepo;

    @Autowired
    private FieldValueRepo fieldValueRepo;

    @Autowired
    private SubmissionFieldProfileRepo submissionFieldProfileRepo;

    @Autowired
    private NamedSearchFilterGroupRepo namedSearchFilterGroupRepo;

    @Autowired
    private OrganizationRepo organizationRepo;

    @Autowired
    private SubmissionStatusRepo submissionStatusRepo;

    @Autowired
    private CustomActionDefinitionRepo customActionDefinitionRepo;

    @Autowired
    private SubmissionEmailService submissionEmailService;

    @Autowired
    private TemplateUtility templateUtility;

    @Autowired
    private AssetService assetService;

    @Autowired
    private ConfigurationRepo configurationRepo;

    @Autowired
    private ActionLogRepo actionLogRepo;

    @Autowired
    private DepositLocationRepo depositLocationRepo;

    @Autowired
    private DepositorService depositorService;

    @Autowired
    private PackagerUtility packagerUtility;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomActionValueRepo customActionValueRepo;

    @Autowired
    private EmbargoRepo embargoRepo;

    @Value("${app.document.folder:private}")
    private String documentFolder;

    @Value("${app.documentType.rename:}")
    private String documentTypesToRename;

    @RequestMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse getAll() {
        return new ApiResponse(SUCCESS, submissionRepo.findAll());
    }

    @RequestMapping("/all-by-user")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse getAllByUser(@WeaverUser User user) {
        return new ApiResponse(SUCCESS, submissionRepo.findAllViewBySubmitterId(user.getId(), FieldValueSubmissionView.class));
    }

    @JsonView(Views.SubmissionIndividualActionLogs.class)
    @RequestMapping("/get-one/{submissionId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse getOne(@WeaverUser User user, @PathVariable Long submissionId) {
        Submission submission = null;
        if (user.getRole().ordinal() <= Role.ROLE_REVIEWER.ordinal()) {
            submission = submissionRepo.read(submissionId);
        } else {
            submission = submissionRepo.findOneBySubmitterAndId(user, submissionId);
        }

        if (submission == null) {
            return new ApiResponse(ERROR, "Submission not found");
        }

        return new ApiResponse(SUCCESS, submission);
    }

    @GetMapping("/get-one/{submissionId}/action-logs")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse getActionLogs(@WeaverUser User user, @PathVariable Long submissionId, @PageableDefault(direction = Direction.DESC, sort = { "action_date" }) Pageable pageable) {
        return new ApiResponse(SUCCESS, actionLogRepo.getAllActionLogs(submissionId, false, pageable));
    }

    @JsonView(Views.SubmissionIndividual.class)
    @RequestMapping("/advisor-review/{advisorAccessHash}")
    public ApiResponse getOne(@PathVariable String advisorAccessHash) {
        Submission submission = submissionRepo.findOneByAdvisorAccessHash(advisorAccessHash);

        return submission.getSubmissionStatus().isEditableByReviewer()
            ? new ApiResponse(SUCCESS, submission)
            : new ApiResponse(ERROR, "Submission is not editable by reviewer");
    }

    @GetMapping("/advisor-review/{advisorAccessHash}/action-logs")
    public ApiResponse getActionLogsForAdvisement(@PathVariable String advisorAccessHash, @PageableDefault(direction = Direction.DESC, sort = { "action_date" }) Pageable pageable) {
        return new ApiResponse(SUCCESS, actionLogRepo.getAllActionLogs(advisorAccessHash, false, pageable));
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse createSubmission(@WeaverUser User user, @WeaverCredentials Credentials credentials,
        @RequestBody Map<String, String> data) throws OrganizationDoesNotAcceptSubmissionsException {

        Submission submission = submissionRepo.create(
            user,
            organizationRepo.read(Long.valueOf(data.get("organizationId"))),
            submissionStatusRepo.findByName(STARTING_SUBMISSION_STATUS_NAME),
            credentials,
            customActionDefinitionRepo.findAll()
        );
        submissionEmailService.sendWorkflowEmails(user, submission.getId());
        actionLogRepo.createPublicLog(Action.UNDETERMINED, submission, user, "Submission created.");

        return new ApiResponse(SUCCESS, submission.getId());
    }

    @Transactional
    @RequestMapping("/delete/{submissionId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse deleteSubmission(@WeaverUser User user, @PathVariable Long submissionId) {
        Submission submissionToDelete = submissionRepo.read(submissionId);

        ApiResponse response = new ApiResponse(SUCCESS);
        if (submissionToDelete.getSubmitter().getEmail().equals(user.getEmail()) || user.getRole().ordinal() <= Role.ROLE_MANAGER.ordinal()) {
            submissionRepo.delete(submissionToDelete);
        } else {
            response = new ApiResponse(ERROR, "Insufficient permisions to delete this submission.");
        }

        return response;
    }

    @RequestMapping(value = "/{submissionId}/add-comment", method = RequestMethod.POST)
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse addComment(@WeaverUser User user, @PathVariable Long submissionId,
        @RequestBody Map<String, Object> data) throws JsonProcessingException, IOException {

        Submission submission = submissionRepo.read(submissionId);

        String commentVisibility = data.get("commentVisibility") != null ? (String) data.get("commentVisibility") : "public";
        Boolean sendEmailToRecipient = data.get("sendEmailToRecipient") != null ? (Boolean) data.get("sendEmailToRecipient") : true;

        data.forEach((key, value) -> System.out.println(key + ":" + value));

        if (commentVisibility.equals("public")) {
            if (sendEmailToRecipient.equals(true)) {
                submissionEmailService.sendAutomatedEmails(user, submission.getId(), data);
            } else{
                String subject = (String) data.get("subject");
                String templatedMessage = templateUtility.compileString((String) data.get("message"), submission);
                actionLogRepo.createPublicLog(Action.UNDETERMINED, submission, user, subject + ": " + templatedMessage);
            }
        } else {
            String subject = (String) data.get("subject");
            String templatedMessage = templateUtility.compileString((String) data.get("message"), submission);
            actionLogRepo.createPrivateLog(Action.UNDETERMINED, submission, user, subject + ": " + templatedMessage);
        }

        return new ApiResponse(SUCCESS);
    }

    @RequestMapping(value = "/{submissionId}/send-email", method = RequestMethod.POST)
    @PreAuthorize("hasRole('REVIEWER')")
    public ApiResponse sendEmail(@WeaverUser User user, @PathVariable Long submissionId,
        @RequestBody Map<String, Object> data) throws JsonProcessingException, IOException {

        submissionEmailService.sendAutomatedEmails(user, submissionId, data);
        return new ApiResponse(SUCCESS);
    }

    @RequestMapping(value = "/batch-comment")
    @PreAuthorize("hasRole('REVIEWER')")
    public ApiResponse batchComment(@WeaverUser User user, @RequestBody Map<String, Object> data) {
        submissionRepo.batchDynamicSubmissionQuery(user.getActiveFilter(), user.getSubmissionViewColumns()).forEach(sub -> {

            String commentVisibility = data.get("commentVisibility") != null ? (String) data.get("commentVisibility") : "public";
            Boolean sendEmailToRecipient = data.get("sendEmailToRecipient") != null ? (Boolean) data.get("sendEmailToRecipient") : true;

            data.forEach((key, value) -> System.out.println(key + ":" + value));

            if (commentVisibility.equals("public")) {
                if (sendEmailToRecipient.equals(true)) {
                    try {
                        submissionEmailService.sendAutomatedEmails(user, sub.getId(), data);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    String subject = (String) data.get("subject");
                    String templatedMessage = templateUtility.compileString((String) data.get("message"), sub);
                    actionLogRepo.createPublicLog(Action.UNDETERMINED, sub, user, subject + ": " + templatedMessage);
                }
            } else {
                String subject = (String) data.get("subject");
                String templatedMessage = templateUtility.compileString((String) data.get("message"), sub);
                actionLogRepo.createPrivateLog(Action.UNDETERMINED, sub, user, subject + ": " + templatedMessage);
            }
        });

        return new ApiResponse(SUCCESS);
    }

    @RequestMapping(value = "/{submissionId}/update-field-value/{fieldProfileId}", method = RequestMethod.POST)
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse updateFieldValue(@WeaverUser User user, @PathVariable Long submissionId, @PathVariable String fieldProfileId, @RequestBody FieldValue fieldValue) {
        ApiResponse apiResponse = null;
        SubmissionFieldProfile submissionFieldProfile = submissionFieldProfileRepo.findById(Long.parseLong(fieldProfileId)).get();
        ValidationResults validationResults = getValidationResults(submissionFieldProfile.getId().toString(), fieldValue);

        if (validationResults.isValid()) {
            FieldPredicate fieldPredicate = fieldValue.getFieldPredicate();
            if (fieldPredicate.getValue().equalsIgnoreCase("default_embargos") ||
                fieldPredicate.getValue().equalsIgnoreCase("proquest_embargos")) {
                for (VocabularyWord vocabularyWord : submissionFieldProfile.getControlledVocabulary().getDictionary()) {
                    if (fieldValue.getValue().equals(vocabularyWord.getName())) {
                        Embargo embargo = embargoRepo.getById(Long.parseLong(vocabularyWord.getIdentifier()));
                        fieldValue.setIdentifier(String.valueOf(embargo.getDuration()));
                        break;
                    }
                }
            }

            Map<String, String> orcidErrors = new HashMap<String, String>();
            if (isOrcidVerificationActive(submissionFieldProfile, fieldValue)) {
                orcidErrors = OrcidUtility.verifyOrcid(user, fieldValue);
            }
            if (orcidErrors.isEmpty()) {
                Submission submission = submissionRepo.read(submissionId);
                if (fieldValue.getId() == null) {

                    fieldValue = fieldValueRepo.save(fieldValue);
                    submission.addFieldValue(fieldValue);
                    submission = submissionRepo.save(submission);

                    if (submissionFieldProfile.getLogged()) {
                        actionLogRepo.createPublicLog(Action.UNDETERMINED, submission, user, submissionFieldProfile.getGloss() + " was set to " + fieldValue.getValue());
                    }

                } else {

                    FieldValue oldFieldValue = fieldValueRepo.findById(fieldValue.getId()).get();
                    String oldValue = oldFieldValue.getValue();
                    fieldValue = fieldValueRepo.save(fieldValue);

                    if (submissionFieldProfile.getLogged()) {
                        actionLogRepo.createPublicLog(Action.UNDETERMINED, submission, user, submissionFieldProfile.getGloss() + " was changed from " + convertBoolean(oldValue) + " to " + convertBoolean(fieldValue.getValue()));
                    }

                }
                apiResponse = new ApiResponse(SUCCESS, fieldValue);

                simpMessagingTemplate.convertAndSend("/channel/submission/" + submission.getId() + "/field-values", apiResponse);

            } else {
                Map<String, Map<String, String>> orcidErrorsMap = new HashMap<String, Map<String, String>>();
                orcidErrorsMap.put("value", orcidErrors);
                apiResponse = new ApiResponse(INVALID, orcidErrorsMap);
            }
        } else {
            apiResponse = new ApiResponse(INVALID, validationResults.getMessages());
        }

        return apiResponse;
    }

    private String convertBoolean(final String value) {
        String result = value;
        if (result.equals("true")) {
            result = "Yes";
        } else if (result.equals("false")) {
            result = "No";
        }
        return result;
    }

    @RequestMapping(value = "/{submissionId}/validate-field-value/{fieldProfileId}", method = RequestMethod.POST)
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse validateFieldValue(@WeaverUser User user, @PathVariable Long submissionId, @PathVariable String fieldProfileId, @RequestBody FieldValue fieldValue) {
        ApiResponse apiResponse = null;
        SubmissionFieldProfile submissionFieldProfile = submissionFieldProfileRepo.findById(Long.parseLong(fieldProfileId)).get();
        ValidationResults validationResults = getValidationResults(submissionFieldProfile.getId().toString(), fieldValue);
        if (validationResults.isValid()) {
            apiResponse = new ApiResponse(SUCCESS, validationResults.getMessages());
        } else {
            apiResponse = new ApiResponse(INVALID, validationResults.getMessages());
        }
        return apiResponse;
    }

    private ValidationResults getValidationResults(String fieldProfileId, FieldValue fieldValue) {
        fieldValue.setModelValidator(new FieldValueValidator(submissionFieldProfileRepo.findById(Long.parseLong(fieldProfileId)).get()));
        return fieldValue.validate(fieldValue);
    }

    private boolean isOrcidVerificationActive(SubmissionFieldProfile submissionFieldProfile, FieldValue fieldValue) {
        return submissionFieldProfile.getInputType().getName().equals("INPUT_ORCID") && configurationRepo.getByNameAndType("orcid_authentication", "orcid").getValue().toLowerCase().equals("true");
    }

    @RequestMapping(value = "/{submissionId}/update-custom-action-value", method = RequestMethod.POST)
    @PreAuthorize("hasRole('REVIEWER')")
    public ApiResponse updateCustomActionValue(@WeaverUser User user, @PathVariable("submissionId") Long submissionId, @RequestBody CustomActionValue customActionValue) {
        Submission submission = submissionRepo.findById(submissionId).get();
        ApiResponse response = new ApiResponse(SUCCESS, customActionValueRepo.update(customActionValue));
        actionLogRepo.createPublicLog(Action.UNDETERMINED, submission, user, "Custom action " + customActionValue.getDefinition().getLabel() + " " + (customActionValue.getValue() ? "set" : "unset"));
        simpMessagingTemplate.convertAndSend("/channel/submission/" + submission.getId() + "/custom-action-values", response);
        return response;
    }

    @RequestMapping("/{submissionId}/change-status/{submissionStatusName}")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse changeStatus(@WeaverUser User user, @PathVariable Long submissionId, @PathVariable String submissionStatusName) {
        Submission submission = submissionRepo.read(submissionId);
        ApiResponse response;
        if (submission != null) {
            SubmissionStatus submissionStatus = submissionStatusRepo.findByName(submissionStatusName);
            if (submissionStatus != null) {
                submission = submissionRepo.updateStatus(submission, submissionStatus, user);
                response = new ApiResponse(SUCCESS, submission);
            } else {
                response = new ApiResponse(ERROR, "Could not find a submission status name " + submissionStatusName);
            }
            submissionEmailService.sendWorkflowEmails(user, submission.getId());
        } else {
            response = new ApiResponse(ERROR, "Could not find a submission with ID " + submissionId);
        }

        return response;
    }

    @RequestMapping("/batch-update-status/{submissionStatusName}")
    @PreAuthorize("hasRole('REVIEWER')")
    public ApiResponse batchUpdateSubmissionStatuses(@WeaverUser User user, @PathVariable String submissionStatusName) {
        submissionRepo.batchDynamicSubmissionQuery(user.getActiveFilter(), user.getSubmissionViewColumns()).forEach(submission -> {
            SubmissionStatus submissionStatus = submissionStatusRepo.findByName(submissionStatusName);
            submission = submissionRepo.updateStatus(submission, submissionStatus, user);
            submissionEmailService.sendWorkflowEmails(user, submission.getId());
        });
        return new ApiResponse(SUCCESS);
    }

    @RequestMapping("/{submissionId}/publish/{depositLocationId}")
    @PreAuthorize("hasRole('REVIEWER')")
    public ApiResponse publish(@WeaverUser User user, @PathVariable Long submissionId, @PathVariable Long depositLocationId) throws Exception {
        Submission submission = submissionRepo.read(submissionId);

        ApiResponse response;
        if (submission != null) {

            SubmissionStatus submissionStatus = submissionStatusRepo.findByName("Published");
            if (submissionStatus != null) {

                DepositLocation depositLocation = depositLocationRepo.findById(depositLocationId).get();

                if (depositLocation != null) {

                    Depositor depositor = depositorService.getDepositor(depositLocation.getDepositorName());

                    if (depositor != null) {
                        ExportPackage exportPackage = packagerUtility.packageExport(depositLocation.getPackager(), submission);
                        String depositURL = depositor.deposit(depositLocation, exportPackage);
                        submission.setDepositURL(depositURL);
                        submission = submissionRepo.updateStatus(submission, submissionStatus, user);
                        submissionEmailService.sendWorkflowEmails(user, submission.getId());
                        response = new ApiResponse(SUCCESS, submission);
                    } else {
                        response = new ApiResponse(ERROR, "Could not find a depositor name " + depositLocation.getDepositorName());
                    }
                } else {
                    response = new ApiResponse(ERROR, "Could not find a deposite location id " + depositLocationId);
                }
            } else {
                response = new ApiResponse(ERROR, "Could not find a submission status name Published");
            }
        } else {
            response = new ApiResponse(ERROR, "Could not find a submission with ID " + submissionId);
        }

        return response;
    }

    @PreAuthorize("hasRole('REVIEWER')")
    @RequestMapping("/batch-export/{packagerName}/{filterId}")
    public void batchExportWithFilter(HttpServletResponse response, @WeaverUser User user, @PathVariable String packagerName, @PathVariable Long filterId) throws IOException {
        NamedSearchFilterGroup filter = namedSearchFilterGroupRepo.findById(filterId).get();
        processBatchExport(response, user, packagerName, filter);
    }

    @PreAuthorize("hasRole('REVIEWER')")
    @RequestMapping("/batch-export/{packagerName}")
    public void batchExport(HttpServletResponse response, @WeaverUser User user, @PathVariable String packagerName) throws IOException {
        NamedSearchFilterGroup activeFilter = user.getActiveFilter();
        processBatchExport(response, user, packagerName, activeFilter);
    }

    @SuppressWarnings("unchecked")
    private void processBatchExport(
        HttpServletResponse response,
        User user,
        String packagerName,
        NamedSearchFilterGroup filter
    ) throws BatchExportException {

        final AbstractPackager<?> packager = packagerUtility.getPackager(packagerName);

        final boolean useFilter = filter != null &&
            filter.getSavedColumns() != null &&
            filter.getColumnsFlag() != null &&
            filter.getColumnsFlag().toString().equals(Boolean.TRUE.toString());

        final List<SubmissionListColumn> columns = useFilter
            ? filter.getSavedColumns()
            : user.getSubmissionViewColumns();

        long contentLength = 0;

        Path export = null;

        LOG.info("Beginning Export with packager {}", packagerName.trim());

        try {
            export = Files.createTempFile(packagerName, packager.getFileExtension());

            try (
                FileOutputStream tempFileOS = new FileOutputStream(export.toFile());
                CountingOutputStream tempFileCountingOS = new CountingOutputStream(tempFileOS);
            ) {

                switch (packagerName.trim()) {
                case "Excel":

                    // Create a streaming workbook with a window size of 100 rows
                    // (only this many rows will be kept in memory at once)
                    try (SXSSFWorkbook workbook = new SXSSFWorkbook(100)) {
                        List<Submission> submissions = submissionRepo.batchDynamicSubmissionQuery(filter, columns);

                        // Enable compression for temporary files
                        workbook.setCompressTempFiles(true);

                        Sheet worksheet = workbook.createSheet();

                        int rowCount = 0;

                        // Create header row
                        Row header = worksheet.createRow(rowCount++);
                        for (int i = 0; i < columns.size(); i++) {
                            SubmissionListColumn column = columns.get(i);
                            header.createCell(i).setCellValue(column.getTitle());
                        }

                        // Stream data rows
                        for (Submission submission : submissions) {
                            ExportPackage exportPackage = packagerUtility.packageExport(packager, submission, columns);
                            if (exportPackage.isMap()) {
                                Map<String, String> rowData = (Map<String, String>) exportPackage.getPayload();
                                Row row = worksheet.createRow(rowCount++);
                                for (int i = 0; i < columns.size(); i++) {
                                    SubmissionListColumn column = columns.get(i);
                                    Cell cell = row.createCell(i);
                                    cell.setCellValue(rowData.get(column.getTitle()));
                                }
                            }
                        }

                        // Auto-sizing is expensive with SXSSF as it can't look back at rows
                        // that have been flushed to disk. If needed, track column widths manually.
                        // If you must use auto-sizing, you'll need to track max width as you go

                        // Write to temp file via counting output stream
                        workbook.write(tempFileCountingOS);

                        // Dispose of temporary files
                        workbook.dispose();
                    }

                    // Set response headers
                    response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                    response.setHeader("Content-Disposition", "inline; filename=" + packagerName + ".xlsx");

                    break;
                case "MarcXML21":
                case "Marc21":

                    try (ZipOutputStream zos = new ZipOutputStream(tempFileCountingOS, StandardCharsets.UTF_8)) {
                        for (Submission submission : submissionRepo.batchDynamicSubmissionQuery(filter, columns)) {

                            StringBuilder contentsText = new StringBuilder();
                            ExportPackage exportPackage = packagerUtility.packageExport(packager, submission);
                            if (exportPackage.isMap()) {
                                for (Map.Entry<String, File> fileEntry : ((Map<String, File>) exportPackage.getPayload()).entrySet()) {
                                    if (packagerName.equals("MarcXML21")) {
                                        zos.putNextEntry(new ZipEntry("MarcXML21/" + fileEntry.getKey()));
                                    } else {
                                        zos.putNextEntry(new ZipEntry(fileEntry.getKey()));
                                    }
                                    contentsText.append("MD " + fileEntry.getKey() + "\n");
                                    byte[] fileBytes = Files.readAllBytes(fileEntry.getValue().toPath());
                                    zos.write(fileBytes);
                                    zos.closeEntry();
                                }
                            }
                        }
                    }

                    // Set response headers
                    response.setContentType(packager.getMimeType());
                    response.setHeader("Content-Disposition", "inline; filename=" + packagerName + "." + packager.getFileExtension());

                    break;
                case "ProQuest":

                    try (ZipOutputStream zos = new ZipOutputStream(tempFileCountingOS, StandardCharsets.UTF_8)) {
                        for (Submission submission : submissionRepo.batchDynamicSubmissionQuery(filter, columns)) {
                            String firstName = getName(submission, "first_name");

                            String lastName = getName(submission, "last_name");

                            String personEntry = lastName + "_" + firstName + "_" + submission.getId();

                            try (
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                ZipOutputStream izos = new ZipOutputStream(baos);
                            ) {
                                ExportPackage exportPackage = packagerUtility.packageExport(packager, submission);
                                if (exportPackage.isMap()) {
                                    for (Map.Entry<String, File> fileEntry : ((Map<String, File>) exportPackage.getPayload()).entrySet()) {
                                        izos.putNextEntry(new ZipEntry(personEntry + "_DATA.xml"));
                                        Path path = fileEntry.getValue().toPath();
                                        byte[] fileBytes = Files.readAllBytes(path);
                                        izos.write(fileBytes);
                                        izos.closeEntry();
                                    }
                                }
                                // LICENSES
                                Set<String> licenseFileNames = new HashSet<>();
                                for (FieldValue ldfv : submission.getLicenseDocumentFieldValues()) {
                                    Path path = assetService.getAssetsAbsolutePath(ldfv.getValue());
                                    byte[] fileBytes = Files.readAllBytes(path);

                                    int sfxIndx = ldfv.getFileName().indexOf(".");

                                    String fileName = sfxIndx > 0
                                        ? ldfv.getFileName().substring(0, sfxIndx)
                                        : ldfv.getFileName();

                                    String fileExt = sfxIndx > 0
                                        ? ldfv.getFileName().substring(sfxIndx)
                                        : StringUtils.EMPTY;

                                    String fullFileName = fileName.toUpperCase() + fileExt;

                                    int i = 0;
                                    while (licenseFileNames.contains(fullFileName)) {
                                        fullFileName = String.format("%s_%s%s", fileName.toUpperCase(), ++i, fileExt);
                                    }
                                    licenseFileNames.add(fullFileName);
                                    izos.putNextEntry(new ZipEntry(personEntry + "_permission/" + fullFileName));
                                    izos.write(fileBytes);
                                    izos.closeEntry();
                                }
                                // PRIMARY_DOC
                                FieldValue primaryDoc = submission.getPrimaryDocumentFieldValue();
                                if (primaryDoc != null && StringUtils.isNotEmpty(primaryDoc.getValue())) {
                                    Path path = assetService.getAssetsAbsolutePath(primaryDoc.getValue());
                                    byte[] fileBytes = Files.readAllBytes(path);
                                    String fName = primaryDoc.getFileName();
                                    int fNameIndx = fName.indexOf(".");
                                    String fType = ""; // default
                                    if (fNameIndx > 0) {
                                        fType = fName.substring(fNameIndx);
                                    }
                                    izos.putNextEntry(new ZipEntry(personEntry + fType));
                                    izos.write(fileBytes);
                                    izos.closeEntry();
                                }

                                izos.finish();  // Finish writing to the inner zip
                                izos.flush();   // Flush any buffered data

                                zos.putNextEntry(new ZipEntry("upload_" + personEntry + ".zip"));
                                zos.write(baos.toByteArray());
                                zos.closeEntry();
                            }
                        }
                    }

                    // Set response headers
                    response.setContentType(packager.getMimeType());
                    response.setHeader("Content-Disposition", "inline; filename=" + packagerName + "." + packager.getFileExtension());

                    break;
                case "DSpaceMETS":

                    try (ZipOutputStream zos = new ZipOutputStream(tempFileCountingOS)) {
                        for (Submission submission : submissionRepo.batchDynamicSubmissionQuery(filter, columns)) {
                            ExportPackage exportPackage = packagerUtility.packageExport(packager, submission);
                            File exportFile = (File) exportPackage.getPayload();
                            byte[] fileBytes = FileUtils.readFileToByteArray(exportFile);
                            zos.putNextEntry(new ZipEntry(exportFile.getName()));
                            zos.write(fileBytes);
                            zos.closeEntry();
                        }
                    }

                    // Set response headers
                    response.setContentType(packager.getMimeType());
                    response.setHeader("Content-Disposition", "inline; filename=" + packagerName + "." + packager.getFileExtension());

                    break;
                case "DSpaceSimple":

                    try (ZipOutputStream zos = new ZipOutputStream(tempFileCountingOS)) {
                        for (Submission submission : submissionRepo.batchDynamicSubmissionQuery(filter, columns)) {
                            String submissionName = "submission_" + submission.getId() + "/";

                            zos.putNextEntry(new ZipEntry(submissionName));

                            StringBuilder contentsText = new StringBuilder();

                            ExportPackage exportPackage = packagerUtility.packageExport(packager, submission);

                            // METADATA
                            if (exportPackage.isMap()) {
                                for (Map.Entry<String, File> fileEntry : ((Map<String, File>) exportPackage.getPayload()).entrySet()) {
                                    zos.putNextEntry(new ZipEntry(submissionName + fileEntry.getKey()));
                                    contentsText.append(fileEntry.getKey() + "\n");
                                    byte[] fileBytes = Files.readAllBytes(fileEntry.getValue().toPath());
                                    zos.write(fileBytes);
                                    zos.closeEntry();
                                }
                            }

                            // LICENSES
                            for (FieldValue ldfv : submission.getLicenseDocumentFieldValues()) {
                                Path path = assetService.getAssetsAbsolutePath(ldfv.getValue());
                                byte[] fileBytes = Files.readAllBytes(path);
                                zos.putNextEntry(new ZipEntry(submissionName + ldfv.getFileName()));
                                contentsText.append(ldfv.getFileName() + "\tBUNDLE:LICENSE\n");
                                zos.write(fileBytes);
                                zos.closeEntry();
                            }

                            // PRIMARY_DOC
                            FieldValue primaryDoc = submission.getPrimaryDocumentFieldValue();
                            Path path = assetService.getAssetsAbsolutePath(primaryDoc.getValue());
                            byte[] fileBytes = Files.readAllBytes(path);
                            zos.putNextEntry(new ZipEntry(submissionName + primaryDoc.getFileName()));
                            contentsText.append(primaryDoc.getFileName() + "\tBUNDLE:CONTENT\tprimary:true\n");
                            zos.write(fileBytes);
                            zos.closeEntry();

                            // SUPPLEMENTAL_DOCS
                            List<FieldValue> supplDocs = submission.getSupplementalAndSourceDocumentFieldValues();
                            for (FieldValue supplDoc : supplDocs) {
                                Path supplPath = assetService.getAssetsAbsolutePath(supplDoc.getValue());
                                byte[] supplFileBytes = Files.readAllBytes(supplPath);
                                zos.putNextEntry(new ZipEntry(submissionName+supplDoc.getFileName()));
                                contentsText.append(supplDoc.getFileName() + "\tBUNDLE:CONTENT\n");
                                zos.write(supplFileBytes);
                                zos.closeEntry();
                            }

                            // CONTENTS_FILE
                            zos.putNextEntry(new ZipEntry(submissionName + "contents"));
                            zos.write(contentsText.toString().getBytes());
                            zos.closeEntry();

                            // close submission zip entry
                            zos.closeEntry();
                        }
                    }

                    // Set response headers
                    response.setContentType(packager.getMimeType());
                    response.setHeader("Content-Disposition", "inline; filename=" + packagerName + "." + packager.getFileExtension());

                    break;
                default:
                    throw new BatchExportException("No packager " + packagerName + " found!");
                }

                // get byte count written to temp file
                contentLength = tempFileCountingOS.getByteCount();

            }

            LOG.info("Packaging complete for {}", packagerName);

            // if no exception occurs and bytes written to temp file
            if (contentLength > 0) {
                LOG.info("Streaming response for {}", packagerName);
                try (FileInputStream tempFileIS = new FileInputStream(export.toFile())) {
                    response.setHeader("Content-Length", Long.toString(contentLength));
                    IOUtils.copyLarge(tempFileIS, response.getOutputStream());
                }
                LOG.info("Done streaming response for {}", packagerName);
            } else {
                throw new RuntimeException("No content to write to response output stream.");
            }

        } catch (IOException e) {
            elevateBatchExportException(e);
        } catch (Exception e) {
            // catch and elevate runtime exceptions to avoid going to the incorrect global exception handler
            elevateBatchExportException(e);
        } finally {
            try {
                if (export != null) {
                    LOG.info("Cleaning up temp files for {}", packagerName);
                    Files.deleteIfExists(export);
                }
            } catch (Exception e) {
                elevateBatchExportException(e);
            }
        }
    }

    @RequestMapping(value = "/batch-assign-to", method = RequestMethod.POST)
    @PreAuthorize("hasRole('REVIEWER')")
    public ApiResponse batchAssignTo(@WeaverUser User user, @RequestBody User assignee) {
        submissionRepo.batchDynamicSubmissionQuery(user.getActiveFilter(), user.getSubmissionViewColumns()).forEach(sub -> {
            sub.setAssignee(assignee);
            actionLogRepo.createPublicLog(Action.UNDETERMINED, sub, user, "Submission was assigned to " + assignee.getFirstName() + " " + assignee.getLastName() + "(" + assignee.getEmail() + ")");
            submissionRepo.update(sub);
        });
        return new ApiResponse(SUCCESS);

    }

    @RequestMapping("/batch-publish/{depositLocationId}")
    @PreAuthorize("hasRole('REVIEWER')")
    public ApiResponse batchPublish(@WeaverUser User user, @PathVariable Long depositLocationId) {
        ApiResponse response = new ApiResponse(SUCCESS);
        SubmissionStatus submissionStatus = submissionStatusRepo.findByName("Published");
        if (submissionStatus != null) {
            DepositLocation depositLocation = depositLocationRepo.findById(depositLocationId).get();
            if (depositLocation != null) {
                Depositor depositor = depositorService.getDepositor(depositLocation.getDepositorName());
                if (depositor != null) {
                    for (Submission submission : submissionRepo.batchDynamicSubmissionQuery(user.getActiveFilter(), user.getSubmissionViewColumns())) {
                        try {
                            ExportPackage exportPackage = packagerUtility.packageExport(depositLocation.getPackager(), submission);
                            String depositURL = depositor.deposit(depositLocation, exportPackage);
                            submission.setDepositURL(depositURL);
                            submission = submissionRepo.updateStatus(submission, submissionStatus, user);
                            submissionEmailService.sendWorkflowEmails(user, submission.getId());
                        } catch (Exception e) {
                            throw new DepositException("Failed package export on submission " + submission.getId());
                        }
                    }
                } else {
                    response = new ApiResponse(ERROR, "Could not find a depositor name " + depositLocation.getDepositorName());
                }
            } else {
                response = new ApiResponse(ERROR, "Could not find a deposite location id " + depositLocationId);
            }
        } else {
            response = new ApiResponse(ERROR, "Could not find a submission status name Published");
        }
        return response;
    }

    @RequestMapping(value = "/{submissionId}/submit-date", method = RequestMethod.POST)
    @PreAuthorize("hasRole('REVIEWER')")
    public ApiResponse submitDate(@WeaverUser User user, @PathVariable("submissionId") Long submissionId, @RequestBody String newDate) throws ParseException {

        Submission submission = submissionRepo.read(submissionId);

        ApiResponse response = new ApiResponse(SUCCESS);
        if (submission != null) {
            String nd = newDate.replaceAll("[\"]", "");
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            Calendar cal = Calendar.getInstance();
            cal.setTime(df.parse(nd));

            submission.setSubmissionDate(cal);
            submission = submissionRepo.update(submission);

            SimpleDateFormat logdf = new SimpleDateFormat("MM/dd/yyyy");
            actionLogRepo.createPublicLog(Action.UNDETERMINED, submission, user, "Submission date set to: " + logdf.format(cal.getTime()));

        } else {
            response = new ApiResponse(ERROR, "Could not find a submission with ID " + submissionId);
        }

        return response;
    }

    @RequestMapping(value = "/{submissionId}/assign-to", method = RequestMethod.POST)
    @PreAuthorize("hasRole('REVIEWER')")
    public ApiResponse assign(@WeaverUser User user, @PathVariable("submissionId") Long submissionId, @RequestBody User assignee) {
        Submission submission = submissionRepo.read(submissionId);

        if (assignee != null) {
            assignee = userRepo.findByEmail(assignee.getEmail());
        }

        ApiResponse response = new ApiResponse(SUCCESS);

        if (submission != null) {
            submission.setAssignee(assignee);
            submission = submissionRepo.update(submission);

            if (assignee == null) {
                actionLogRepo.createPublicLog(Action.UNDETERMINED, submission, user, "Submission was unassigned");
            } else {
                actionLogRepo.createPublicLog(Action.UNDETERMINED, submission, user, "Submission was assigned to " + assignee.getFirstName() + " " + assignee.getLastName() + "(" + assignee.getEmail() + ")");
            }
        } else {
            response = new ApiResponse(ERROR, "Could not find a submission with ID " + submissionId);
        }

        return response;
    }

    @RequestMapping(value = "/{submissionId}/remove-field-value", method = RequestMethod.POST)
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse removeFieldValue(@PathVariable("submissionId") Long submissionId, @RequestBody FieldValue fieldValue) {
        Submission submission = submissionRepo.read(submissionId);
        submission.removeFieldValue(fieldValue);
        submission = submissionRepo.save(submission);
        simpMessagingTemplate.convertAndSend("/channel/submission/" + submission.getId() + "/removed-field-value", new ApiResponse(SUCCESS, fieldValue));
        return new ApiResponse(SUCCESS, submission);
    }

    @RequestMapping(value = "/{submissionId}/update-reviewer-notes", method = RequestMethod.POST)
    @PreAuthorize("hasRole('REVIEWER')")
    public ApiResponse updateReviewerNotes(@WeaverUser User user, @PathVariable("submissionId") Long submissionId, @RequestBody Map<String, String> requestData) {
        Submission submission = submissionRepo.read(submissionId);
        String reviewerNotes = requestData.get("reviewerNotes");
        submission.setReviewerNotes(reviewerNotes);
        submission = submissionRepo.update(submission);
        actionLogRepo.createPrivateLog(Action.UNDETERMINED, submission, user, "Submission notes changed to \"" + reviewerNotes + "\"");
        return new ApiResponse(SUCCESS, submission);
    }

    @RequestMapping("/{submissionId}/needs-correction")
    @PreAuthorize("hasRole('REVIEWER')")
    public ApiResponse setSubmissionNeedsCorrection(@WeaverUser User user, @PathVariable Long submissionId) {
        Submission submission = submissionRepo.read(submissionId);
        SubmissionStatus needsCorrectionStatus = submissionStatusRepo.findByName(NEEDS_CORRECTION_SUBMISSION_STATUS_NAME);
        String oldSubmissionStatusName = submission.getSubmissionStatus().getName();
        submission.setSubmissionStatus(needsCorrectionStatus);
        submission = submissionRepo.update(submission);
        submissionEmailService.sendWorkflowEmails(user, submission.getId());
        actionLogRepo.createPublicLog(Action.UNDETERMINED, submission, user, "Submission status was changed from " + oldSubmissionStatusName + " to " + NEEDS_CORRECTION_SUBMISSION_STATUS_NAME);

        return new ApiResponse(SUCCESS, submission);
    }

    @RequestMapping("/{submissionId}/submit-corrections")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse setSubmissionCorrectionsReceived(@WeaverUser User user, @PathVariable Long submissionId) {
        Submission submission = submissionRepo.read(submissionId);
        if (submission == null) {
          return new ApiResponse(ERROR, "Could not find a submission with ID " + submissionId);
        }
        String oldSubmissionStatusName = submission.getSubmissionStatus().getName();
        SubmissionStatus correctionsReceivedStatus = submissionStatusRepo.findByName(CORRECTIONS_RECEIVED_SUBMISSION_STATUS_NAME);
        submission.setSubmissionStatus(correctionsReceivedStatus);
        submission = submissionRepo.update(submission);
        actionLogRepo.createPublicLog(Action.UNDETERMINED, submission, user, "Submission status was changed from " + oldSubmissionStatusName + " to " + CORRECTIONS_RECEIVED_SUBMISSION_STATUS_NAME);
        submissionEmailService.sendWorkflowEmails(user, submission.getId());
        return new ApiResponse(SUCCESS, submission);
    }

    @RequestMapping(value = "/{submissionId}/add-message", method = RequestMethod.POST)
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse addMessage(@WeaverUser User user, @PathVariable Long submissionId, @RequestBody String message) {
        Submission submission = submissionRepo.read(submissionId);
        ActionLog actionLog = actionLogRepo.createPublicLog(Action.STUDENT_MESSAGE, submission, user, message);
        submissionEmailService.sendActionEmails(submission, actionLog);
        return new ApiResponse(SUCCESS, actionLog);
    }

    @Transactional(readOnly = true)
    @JsonView(Views.SubmissionList.class)
    @RequestMapping(value = "/query/{page}/{size}", method = RequestMethod.POST)
    @PreAuthorize("hasRole('REVIEWER')")
    public ApiResponse querySubmission(@WeaverUser User user, @PathVariable Integer page, @PathVariable Integer size, @RequestBody List<SubmissionListColumn> submissionListColumns) throws ExecutionException {
        long startTime = System.nanoTime();
        NamedSearchFilterGroup activeFilter = user.getActiveFilter();
        Page<Submission> submissions = submissionRepo.pageableDynamicSubmissionQuery(activeFilter, activeFilter.getColumnsFlag() ? activeFilter.getSavedColumns() : submissionListColumns, PageRequest.of(page, size));
        long endTime = System.nanoTime();
        long duration = (endTime - startTime);
        LOG.info("Dynamic query took " + duration / 1000000000.0 + " seconds");
        return new ApiResponse(SUCCESS, new ApiPage<Submission>(submissions));
    }

    @RequestMapping("/file")
    public void submissionFile(HttpServletResponse response, @RequestHeader String uri) throws IOException {
        response.addHeader("Content-Disposition", "attachment");
        Path path = assetService.getAssetsAbsolutePath(uri);
        Files.copy(path, response.getOutputStream());
        response.getOutputStream().flush();
    }

    @RequestMapping(value = "/{submissionId}/file-info", method = RequestMethod.POST)
    public ApiResponse submissionFileInfo(@PathVariable Long submissionId, @RequestBody Map<String, String> requestData) throws IOException {
        String uri = requestData.get("uri");
        Submission submission = submissionRepo.read(submissionId);

        return new ApiResponse(SUCCESS, assetService.getAssetFileInfo(uri, submission));
    }

    @RequestMapping(value = "/{submissionId}/{documentType}/upload-file", method = RequestMethod.POST)
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse uploadFile(@WeaverUser User user, @PathVariable Long submissionId, @PathVariable String documentType, @RequestParam MultipartFile file) throws IOException {
        Submission submission = submissionRepo.read(submissionId);

        int hash = user.getEmail().hashCode();

        String fileName = file.getOriginalFilename();

        String fileExtension = FilenameUtils.getExtension(fileName).equals("pdf") ? FilenameUtils.getExtension(fileName) : "pdf";

        if (documentTypesToRename.contains(documentType)) {
            String lastName = submission.getSubmitter().getLastName().toUpperCase();
            int year = Calendar.getInstance().get(Calendar.YEAR);
            fileName = lastName + "-" + documentType + "-" + String.valueOf(year) + "." + fileExtension;
        }

        String uri = documentFolder + File.separator + hash + File.separator + System.currentTimeMillis() + "-" + fileName;
        assetService.write(file.getBytes(), uri);
        JsonNode fileInfo = assetService.getAssetFileInfo(uri, submission);
        actionLogRepo.createPublicLog(Action.UNDETERMINED, submission, user, documentType + " file " + fileInfo.get("name").asText() + " (" + fileInfo.get("readableSize").asText() + ") uploaded");
        return new ApiResponse(SUCCESS, uri);
    }

    @RequestMapping(value = "/{submissionId}/{documentType}/rename-file", method = RequestMethod.POST)
    @PreAuthorize("hasRole('REVIEWER')")
    public ApiResponse renameFile(@WeaverUser User user, @PathVariable Long submissionId, @PathVariable String documentType, @RequestBody Map<String, String> requestData) throws IOException {
        Submission submission = submissionRepo.read(submissionId);

        String newName = requestData.get("newName");
        String oldUri = requestData.get("uri");
        String newUri = oldUri.replace(oldUri.substring(oldUri.lastIndexOf(File.separator) + 1, oldUri.length()), System.currentTimeMillis() + "-" + newName);
        assetService.rename(oldUri, newUri);
        JsonNode fileInfo = assetService.getAssetFileInfo(newUri, submission);
        actionLogRepo.createPublicLog(Action.UNDETERMINED, submission, user, documentType + " file " + fileInfo.get("name").asText() + " (" + fileInfo.get("readableSize").asText() + ") renamed");
        return new ApiResponse(SUCCESS, newUri);
    }

    @RequestMapping("/{submissionId}/{fieldValueId}/remove-file")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse removeFile(@WeaverUser User user, @PathVariable Long submissionId, @PathVariable Long fieldValueId) throws IOException {
        Submission submission = submissionRepo.read(submissionId);
        ApiResponse apiResponse = new ApiResponse(SUCCESS);
        int hash = user.getEmail().hashCode();
        Optional<FieldValue> fileFieldValue = fieldValueRepo.findById(fieldValueId);
        if (fileFieldValue.isPresent()) {
            String documentType = fileFieldValue.get().getFieldPredicate().getValue();
            String uri = fileFieldValue.get().getValue();
            if (user.getRole().equals(Role.ROLE_STUDENT) && documentType.equals("_doctype_license")) {
                apiResponse = new ApiResponse(ERROR, "You are not allowed to delete license files!");
            } else {
                if (user.getRole().equals(Role.ROLE_ADMIN) || user.getRole().equals(Role.ROLE_MANAGER) || uri.contains(String.valueOf(hash))) {
                    String fileName = "";
                    String fileSize = "file not found";
                    if (assetService.assetFileExists(uri)) {
                        JsonNode fileInfo = assetService.getAssetFileInfo(uri, submission);
                        fileName = fileInfo.get("name").asText();
                        fileSize = fileInfo.get("readableSize").asText();
                    } else {
                        fileName = assetService.getAssetFileName(uri);
                    }

                    assetService.delete(uri);
                    actionLogRepo.createPublicLog(Action.UNDETERMINED, submission, user, documentType.substring(9).toUpperCase() + " file " + fileName + " (" + fileSize + ") removed");
                } else {
                    apiResponse = new ApiResponse(ERROR, "This is not your file to delete!");
                }
            }
        } else {
            apiResponse = new ApiResponse(ERROR, "There is no field value with id " + fieldValueId);
        }

        return apiResponse;
    }

    @RequestMapping(value = "/{submissionId}/{documentType}/archive-file", method = RequestMethod.POST)
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse archiveFile(@WeaverUser User user, @PathVariable Long submissionId, @PathVariable String documentType, @RequestBody Map<String, String> requestData) throws IOException {
        Submission submission = submissionRepo.read(submissionId);
        String name = requestData.get("name");
        String oldUri = requestData.get("uri");
        String newUri = oldUri.replace(oldUri.substring(oldUri.lastIndexOf(File.separator) + 1, oldUri.length()), System.currentTimeMillis() + "-archived-" + name);
        assetService.rename(oldUri, newUri);
        JsonNode fileInfo = assetService.getAssetFileInfo(newUri, submission);
        actionLogRepo.createPublicLog(Action.UNDETERMINED, submission, user, "ARCHIVE - " + documentType + " file " + fileInfo.get("name").asText() + " (" + fileInfo.get("readableSize").asText() + ") archived");
        return new ApiResponse(SUCCESS, newUri);
    }

    @RequestMapping("/{submissionId}/send-advisor-email")
    @PreAuthorize("hasRole('REVIEWER')")
    public ApiResponse sendAdvisorEmail(@WeaverUser User user, @PathVariable Long submissionId) {
        submissionEmailService.sendAdvisorEmails(user, submissionId);
        return new ApiResponse(SUCCESS);
    }

    // TODO: rework, anonymous endpoint for advisor approval, no user available for
    // action log
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/{submissionId}/update-advisor-approval", method = RequestMethod.POST)
    public ApiResponse updateAdvisorApproval(@PathVariable Long submissionId, @RequestBody Map<String, Object> data) {

        Submission submission = submissionRepo.read(submissionId);
        HashMap<String, Object> embargoData = (HashMap<String, Object>) data.get("embargo");
        HashMap<String, Object> advisorData = (HashMap<String, Object>) data.get("advisor");

        Boolean approveAdvisor = (Boolean) advisorData.get("approve");
        Boolean approveEmbargo = (Boolean) embargoData.get("approve");

        Boolean clearApproveEmbargo = (Boolean) embargoData.get("clearApproval");
        Boolean clearApproveAdvisor = (Boolean) advisorData.get("clearApproval");

        String message = (String) data.get("message");

        if (approveAdvisor != null && !clearApproveAdvisor) {
            processAdvisorAction("Application", approveAdvisor, submission);
            submission.setApproveAdvisor(approveAdvisor);
            submission.setApproveAdvisorDate(Calendar.getInstance());
        }

        if (clearApproveAdvisor != null && clearApproveAdvisor) {
            processAdvisorStatusClear("Application", submission.getApproveAdvisor(), submission);
            submission.clearApproveAdvisor();
        }

        if (approveEmbargo != null && !clearApproveEmbargo) {
            processAdvisorAction("Embargo", approveEmbargo, submission);
            submission.setApproveEmbargo(approveEmbargo);
            submission.setApproveEmbargoDate(Calendar.getInstance());
        }

        if (clearApproveEmbargo != null && clearApproveEmbargo) {
            processAdvisorStatusClear("Embargo", submission.getApproveEmbargo(), submission);
            submission.clearApproveEmbargo();
        }

        if (message != null) {
            ActionLog actionLog = actionLogRepo.createAdvisorPublicLog(Action.ADVISOR_MESSAGE, submission, "Advisor comments : " + message);
            submissionEmailService.sendActionEmails(submission, actionLog);
        }

        return new ApiResponse(SUCCESS, submission);

    }

    private void processAdvisorAction(String type, Boolean approveStatus, Submission submission) {
        String approveAdvisorMessage;
        Action action;
        if (approveStatus == true) {
            approveAdvisorMessage = "The committee approved the " + type + ".";
            action = type.equals("Application") ? Action.ADVISOR_APPROVE_SUBMISSION : Action.ADVISOR_APPROVE_EMBARGO;
        } else {
            approveAdvisorMessage = "The committee rejected the " + type + ".";
            action = type.equals("Application") ? Action.ADVISOR_CLEAR_APPROVE_SUBMISSION : Action.ADVISOR_CLEAR_APPROVE_EMBARGO;
        }
        ActionLog actionLog = actionLogRepo.createAdvisorPublicLog(action, submission, approveAdvisorMessage);
        submissionEmailService.sendActionEmails(submission, actionLog);
    }

    private void processAdvisorStatusClear(String type, Boolean approvalState, Submission submission) {
        String clearAdvisorMessage = "The committee has withdrawn its " + type;
        Action action = type.equals("Application") ? Action.ADVISOR_CLEAR_APPROVE_SUBMISSION : Action.ADVISOR_CLEAR_APPROVE_EMBARGO;
        if (approvalState == true) {
            clearAdvisorMessage += " Approval.";
        } else {
            clearAdvisorMessage += " Rejection.";
        }
        ActionLog actionLog = actionLogRepo.createAdvisorPublicLog(action, submission, clearAdvisorMessage);
        submissionEmailService.sendActionEmails(submission, actionLog);
    }

    private String getName(Submission submission, String predicate) {
        List<FieldValue> fieldValues = submission.getFieldValuesByPredicateValue(predicate);

        // use Unknown if no field values with predicate or value is empty
        String name = fieldValues.isEmpty() || fieldValues.get(0).getValue().trim().isEmpty()
            ? ApplicationConstants.UNKNOWN
            : fieldValues.get(0).getValue().trim();

        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    private void elevateBatchExportException(Exception e) throws BatchExportException {
        throw new BatchExportException("Error With Export", e);
    }

}
