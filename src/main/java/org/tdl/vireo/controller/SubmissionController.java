package org.tdl.vireo.controller;

import static edu.tamu.weaver.response.ApiStatus.ERROR;
import static edu.tamu.weaver.response.ApiStatus.INVALID;
import static edu.tamu.weaver.response.ApiStatus.SUCCESS;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.tdl.vireo.enums.AppRole;
import org.tdl.vireo.exception.OrganizationDoesNotAcceptSubmissionsExcception;
import org.tdl.vireo.model.CustomActionValue;
import org.tdl.vireo.model.DepositLocation;
import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.EmailWorkflowRule;
import org.tdl.vireo.model.FieldValue;
import org.tdl.vireo.model.InputType;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.SubmissionFieldProfile;
import org.tdl.vireo.model.SubmissionListColumn;
import org.tdl.vireo.model.SubmissionStatus;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.depositor.Depositor;
import org.tdl.vireo.model.export.ExportPackage;
import org.tdl.vireo.model.packager.AbstractPackager;
import org.tdl.vireo.model.repo.ActionLogRepo;
import org.tdl.vireo.model.repo.ConfigurationRepo;
import org.tdl.vireo.model.repo.DepositLocationRepo;
import org.tdl.vireo.model.repo.EmailTemplateRepo;
import org.tdl.vireo.model.repo.FieldValueRepo;
import org.tdl.vireo.model.repo.InputTypeRepo;
import org.tdl.vireo.model.repo.OrganizationRepo;
import org.tdl.vireo.model.repo.SubmissionFieldProfileRepo;
import org.tdl.vireo.model.repo.SubmissionRepo;
import org.tdl.vireo.model.repo.SubmissionStatusRepo;
import org.tdl.vireo.model.repo.UserRepo;
import org.tdl.vireo.model.validation.FieldValueValidator;
import org.tdl.vireo.service.DepositorService;
import org.tdl.vireo.util.FileIOUtility;
import org.tdl.vireo.util.OrcidUtility;
import org.tdl.vireo.util.PackagerUtility;
import org.tdl.vireo.util.TemplateUtility;

import com.fasterxml.jackson.databind.JsonNode;

import edu.tamu.framework.aspect.annotation.ApiCredentials;
import edu.tamu.framework.aspect.annotation.ApiData;
import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.ApiModel;
import edu.tamu.framework.aspect.annotation.ApiVariable;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.model.Credentials;
import edu.tamu.framework.service.RoleService;
import edu.tamu.framework.util.EmailSender;
import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.validation.results.ValidationResults;

@RestController
@ApiMapping("/submission")
public class SubmissionController {

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
    private OrganizationRepo organizationRepo;

    @Autowired
    private InputTypeRepo inputTypeRepo;

    @Autowired
    private SubmissionStatusRepo submissionStatusRepo;

    @Autowired
    private EmailSender emailSender;

    @Autowired
    private EmailTemplateRepo emailTemplateRepo;

    @Autowired
    private TemplateUtility templateUtility;

    @Autowired
    private FileIOUtility fileIOUtility;

    @Autowired
    private OrcidUtility orcidUtility;

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
    private RoleService roleService;

    @Transactional
    @ApiMapping("/all")
    @Auth(role = "MANAGER")
    public ApiResponse getAll() {
        return new ApiResponse(SUCCESS, submissionRepo.findAll());
    }

    @Transactional
    @ApiMapping("/all-by-user")
    @Auth(role = "STUDENT")
    public ApiResponse getAllByUser(@ApiCredentials Credentials credentials) {
        User submitter = userRepo.findByEmail(credentials.getEmail());
        return new ApiResponse(SUCCESS, submissionRepo.findAllBySubmitter(submitter));
    }

    @Transactional
    @ApiMapping("/get-one/{submissionId}")
    @Auth(role = "STUDENT")
    public ApiResponse getOne(@ApiVariable Long submissionId) {
        return new ApiResponse(SUCCESS, submissionRepo.read(submissionId));
    }

    @Transactional
    @ApiMapping("/advisor-review/{submissionHash}")
    public ApiResponse getOne(@ApiVariable String submissionHash) {
        Submission submission = submissionRepo.findOneByAdvisorAccessHash(submissionHash);
        return new ApiResponse(SUCCESS, submission);
    }

    @Transactional
    @ApiMapping(value = "/create", method = RequestMethod.POST)
    @Auth(role = "STUDENT")
    public ApiResponse createSubmission(@ApiCredentials Credentials credentials, @ApiData Map<String, String> data) throws OrganizationDoesNotAcceptSubmissionsExcception {
        Submission submission = submissionRepo.create(userRepo.findByEmail(credentials.getEmail()), organizationRepo.read(Long.valueOf(data.get("organizationId"))), submissionStatusRepo.findByName(STARTING_SUBMISSION_STATUS_NAME), credentials);
        actionLogRepo.createPublicLog(submission, credentials, "Submission created.");
        return new ApiResponse(SUCCESS, submission);
    }

    @Transactional
    @ApiMapping("/delete/{submissionId}")
    @Auth(role = "STUDENT")
    public ApiResponse deleteSubmission(@ApiCredentials Credentials credentials, @ApiVariable Long submissionId) {
        Submission submissionToDelete = submissionRepo.read(submissionId);

        ApiResponse response = new ApiResponse(SUCCESS);
        if (submissionToDelete.getSubmitter().getEmail().equals(credentials.getEmail()) || roleService.valueOf(credentials.getRole()).ordinal() >= AppRole.MANAGER.ordinal()) {
            submissionRepo.delete(submissionId);
        } else {
            response = new ApiResponse(ERROR, "Insufficient permisions to delete this submission.");
        }

        return response;
    }

    @Transactional
    @ApiMapping(value = "/{submissionId}/add-comment", method = RequestMethod.POST)
    @Auth(role = "STUDENT")
    public ApiResponse addComment(@ApiCredentials Credentials credentials, @ApiVariable Long submissionId, @ApiData Map<String, Object> data) {

        Submission submission = submissionRepo.read(submissionId);

        String commentVisibility = data.get("commentVisiblity") != null ? (String) data.get("commentVisiblity") : "public";

        if (commentVisibility.equals("public")) {
            sendEmail(credentials, submission, data);
        } else {
            String subject = (String) data.get("subject");
            String templatedMessage = templateUtility.compileString((String) data.get("message"), submission);
            actionLogRepo.createPrivateLog(submission, credentials, subject + ": " + templatedMessage);
        }

        return new ApiResponse(SUCCESS);
    }

    @Transactional
    @ApiMapping(value = "/{submissionId}/send-email", method = RequestMethod.POST)
    @Auth(role = "STUDENT")
    public ApiResponse sendEmail(@ApiCredentials Credentials credentials, @ApiVariable Long submissionId, @ApiData Map<String, Object> data) {
        sendEmail(credentials, submissionRepo.read(submissionId), data);
        return new ApiResponse(SUCCESS);
    }

    private void sendEmail(Credentials credentials, Submission submission, Map<String, Object> data) {

        String subject = (String) data.get("subject");

        String templatedMessage = templateUtility.compileString((String) data.get("message"), submission);

        boolean sendRecipientEmail = data.get("sendEmailToRecipient").equals("true");

        if (sendRecipientEmail) {

            boolean sendCCRecipientEmail = data.get("sendEmailToCCRecipient").equals("true");

            SimpleMailMessage smm = new SimpleMailMessage();

            smm.setTo(((String) data.get("recipientEmail")).split(";"));

            if (sendCCRecipientEmail) {
                smm.setCc(((String) data.get("ccRecipientEmail")).split(";"));
            }

            User user = userRepo.findByEmail(credentials.getEmail());
            String preferedEmail = user.getSetting("preferedEmail");
            user.getSetting("ccEmail");
            if (user.getSetting("ccEmail").equals("true")) {
                smm.setBcc(preferedEmail == null ? credentials.getEmail() : preferedEmail);
            }

            smm.setSubject(subject);
            smm.setText(templatedMessage);

            emailSender.send(smm);

        }

        actionLogRepo.createPublicLog(submission, credentials, subject + ": " + templatedMessage);

    }

    @Transactional
    @ApiMapping(value = "/{submissionId}/update-field-value/{fieldProfileId}", method = RequestMethod.POST)
    @Auth(role = "STUDENT")
    public ApiResponse updateFieldValue(@ApiCredentials Credentials credentials, @ApiVariable Long submissionId, @ApiVariable String fieldProfileId, @ApiModel FieldValue fieldValue) {
        ApiResponse apiResponse = null;
        SubmissionFieldProfile submissionFieldProfile = submissionFieldProfileRepo.getOne(Long.parseLong(fieldProfileId));
        ValidationResults validationResults = getValidationResults(submissionFieldProfile.getId().toString(), fieldValue);
        if (validationResults.isValid()) {
            Map<String, String> orcidErrors = new HashMap<String, String>();
            if (isOrcidVerificationActive(submissionFieldProfile, fieldValue)) {
                orcidErrors = orcidUtility.verifyOrcid(credentials, fieldValue);
            }
            if (orcidErrors.isEmpty()) {
                Submission submission = submissionRepo.read(submissionId);
                if (fieldValue.getId() == null) {

                    fieldValue = fieldValueRepo.save(fieldValue);
                    submission.addFieldValue(fieldValue);
                    submission = submissionRepo.update(submission);

                    if (submissionFieldProfile.getLogged()) {
                        actionLogRepo.createPublicLog(submission, credentials, submissionFieldProfile.getFieldGlosses().get(0).getValue() + " was set to " + fieldValue.getValue());
                    }

                } else {

                    FieldValue oldFieldValue = fieldValueRepo.findOne(fieldValue.getId());
                    String oldValue = oldFieldValue.getValue();
                    fieldValue = fieldValueRepo.save(fieldValue);

                    if (submissionFieldProfile.getLogged()) {
                        actionLogRepo.createPublicLog(submission, credentials, submissionFieldProfile.getFieldGlosses().get(0).getValue() + " was changed from " + oldValue + " to " + fieldValue.getValue());
                    }

                }
                apiResponse = new ApiResponse(SUCCESS, fieldValue);

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

    @Transactional
    @ApiMapping(value = "/{submissionId}/validate-field-value/{fieldProfileId}", method = RequestMethod.POST)
    @Auth(role = "STUDENT")
    public ApiResponse validateFieldValue(@ApiCredentials Credentials credentials, @ApiVariable Long submissionId, @ApiVariable String fieldProfileId, @ApiModel FieldValue fieldValue) {
        ApiResponse apiResponse = null;
        SubmissionFieldProfile submissionFieldProfile = submissionFieldProfileRepo.getOne(Long.parseLong(fieldProfileId));
        ValidationResults validationResults = getValidationResults(submissionFieldProfile.getId().toString(), fieldValue);
        if (validationResults.isValid()) {
            apiResponse = new ApiResponse(SUCCESS, validationResults.getMessages());
        } else {
            apiResponse = new ApiResponse(INVALID, validationResults.getMessages());
        }
        return apiResponse;
    }

    private ValidationResults getValidationResults(String fieldProfileId, FieldValue fieldValue) {
        fieldValue.setModelValidator(new FieldValueValidator(submissionFieldProfileRepo.getOne(Long.parseLong(fieldProfileId))));
        return fieldValue.validate(fieldValue);
    }

    private boolean isOrcidVerificationActive(SubmissionFieldProfile submissionFieldProfile, FieldValue fieldValue) {
        return submissionFieldProfile.getInputType().getName().equals("INPUT_ORCID") && configurationRepo.getByNameAndType("orcid_authentication", "orcid").getValue().toLowerCase().equals("true");
    }

    @Transactional
    @ApiMapping(value = "/{submissionId}/update-custom-action-value", method = RequestMethod.POST)
    @Auth(role = "MANAGER")
    public ApiResponse updateCustomActionValue(@ApiVariable("submissionId") Long submissionId, @ApiModel CustomActionValue customActionValue) {
        return new ApiResponse(SUCCESS, submissionRepo.read(submissionId).editCustomActionValue(customActionValue));
    }

    @Transactional
    @ApiMapping("/{submissionId}/change-status/{submissionStatusName}")
    @Auth(role = "STUDENT")
    public ApiResponse changeStatus(@ApiCredentials Credentials credentials, @ApiVariable Long submissionId, @ApiVariable String submissionStatusName) {
        Submission submission = submissionRepo.read(submissionId);

        ApiResponse response = new ApiResponse(SUCCESS);
        if (submission != null) {

            SubmissionStatus submissionStatus = submissionStatusRepo.findByName(submissionStatusName);
            if (submissionStatus != null) {
                submission = submissionRepo.updateStatus(submission, submissionStatus, credentials);
            } else {
                response = new ApiResponse(ERROR, "Could not find a submission status name " + submissionStatusName);
            }
        } else {
            response = new ApiResponse(ERROR, "Could not find a submission with ID " + submissionId);
        }

        processEmailWorkflowRules(credentials, submission);

        return response;
    }

    @Transactional
    @ApiMapping(value = "/batch-update-status", method = RequestMethod.POST)
    @Auth(role = "MANAGER")
    public ApiResponse batchUpdateSubmissionStatuses(@ApiCredentials Credentials credentials, @ApiModel SubmissionStatus submissionStatus) {
        User user = userRepo.findByEmail(credentials.getEmail());
        submissionRepo.batchDynamicSubmissionQuery(user.getActiveFilter(), user.getSubmissionViewColumns()).forEach(submission -> {
            submission = submissionRepo.updateStatus(submission, submissionStatus, credentials);
            processEmailWorkflowRules(credentials, submission);
        });
        return new ApiResponse(SUCCESS);
    }

    @Transactional
    @ApiMapping("/{submissionId}/publish/{depositLocationId}")
    @Auth(role = "STUDENT")
    public ApiResponse publish(@ApiCredentials Credentials credentials, @ApiVariable Long submissionId, @ApiVariable Long depositLocationId) throws Exception {
        Submission submission = submissionRepo.read(submissionId);

        ApiResponse response = new ApiResponse(SUCCESS);
        if (submission != null) {

            SubmissionStatus submissionStatus = submissionStatusRepo.findByName("Published");
            if (submissionStatus != null) {

                DepositLocation depositLocation = depositLocationRepo.findOne(depositLocationId);

                if (depositLocation != null) {

                    Depositor depositor = depositorService.getDepositor(depositLocation.getDepositorName());

                    if (depositor != null) {

                        ExportPackage exportPackage = packagerUtility.packageExport(depositLocation.getPackager(), submission);

                        String result = depositor.deposit(depositLocation, exportPackage);

                        if (result != null) {
                            submission = submissionRepo.updateStatus(submission, submissionStatus, credentials);
                        } else {
                            response = new ApiResponse(ERROR, "Could not deposit submission");
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
        } else {
            response = new ApiResponse(ERROR, "Could not find a submission with ID " + submissionId);
        }

        processEmailWorkflowRules(credentials, submission);

        return response;
    }

    @Transactional
    @Auth(role = "MANAGER")
    @ApiMapping("/batch-export/{packagerName}")
    public void batchExport(HttpServletResponse response, @ApiCredentials Credentials credentials, @ApiVariable String packagerName) throws Exception {

        User user = userRepo.findByEmail(credentials.getEmail());

        AbstractPackager packager = packagerUtility.getPackager(packagerName);

        response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "inline; filename=" + packagerName + ".zip");

        try (ZipOutputStream zos = new ZipOutputStream(response.getOutputStream())) {

            // TODO: need a more dynamic way to achieve this
            if (packagerName.equals("ProQuest")) {
                // TODO: add filter for UMI Publication true
            }

            for (Submission submission : submissionRepo.batchDynamicSubmissionQuery(user.getActiveFilter(), user.getSubmissionViewColumns())) {
                ExportPackage exportPackage = packagerUtility.packageExport(packager, submission);
                File exportFile = exportPackage.getFile();
                zos.putNextEntry(new ZipEntry(exportFile.getName()));
                zos.write(Files.readAllBytes(exportFile.toPath()));
                zos.closeEntry();
            }
            zos.close();
        }
    }

    @Transactional
    @ApiMapping(value = "/batch-assign-to", method = RequestMethod.POST)
    @Auth(role = "MANAGER")
    public ApiResponse batchAssignTo(@ApiCredentials Credentials credentials, @ApiModel User assignee) {
        User user = userRepo.findByEmail(credentials.getEmail());
        submissionRepo.batchDynamicSubmissionQuery(user.getActiveFilter(), user.getSubmissionViewColumns()).forEach(sub -> {
            sub.setAssignee(assignee);
            actionLogRepo.createPublicLog(sub, credentials, "Submission was assigned to " + assignee.getFirstName() + " " + assignee.getLastName() + "(" + assignee.getEmail() + ")");
            submissionRepo.update(sub);
        });
        return new ApiResponse(SUCCESS);
    }

    @Transactional
    @ApiMapping("/batch-publish/{depositLocationId}")
    @Auth(role = "MANAGER")
    public ApiResponse batchPublish(@ApiCredentials Credentials credentials, @ApiVariable Long depositLocationId) {
        ApiResponse response = new ApiResponse(SUCCESS);
        User user = userRepo.findByEmail(credentials.getEmail());
        SubmissionStatus submissionStatus = submissionStatusRepo.findByName("Published");
        if (submissionStatus != null) {
            DepositLocation depositLocation = depositLocationRepo.findOne(depositLocationId);
            if (depositLocation != null) {
                for (Submission submission : submissionRepo.batchDynamicSubmissionQuery(user.getActiveFilter(), user.getSubmissionViewColumns())) {
                    Depositor depositor = depositorService.getDepositor(depositLocation.getDepositorName());
                    if (depositor != null) {
                        try {
                            ExportPackage exportPackage = packagerUtility.packageExport(depositLocation.getPackager(), submission);

                            String result = depositor.deposit(depositLocation, exportPackage);

                            if (result != null) {
                                submission = submissionRepo.updateStatus(submission, submissionStatus, credentials);
                            } else {
                                throw new RuntimeException("Failed batch publish on submission " + submission.getId());
                            }

                        } catch (Exception e) {
                            throw new RuntimeException("Failed package export on submission " + submission.getId());
                        }
                    } else {
                        response = new ApiResponse(ERROR, "Could not find a depositor name " + depositLocation.getDepositorName());
                    }
                }
            } else {
                response = new ApiResponse(ERROR, "Could not find a deposite location id " + depositLocationId);
            }
        } else {
            response = new ApiResponse(ERROR, "Could not find a submission status name Published");
        }
        return response;
    }

    @Transactional
    @ApiMapping(value = "/{submissionId}/submit-date", method = RequestMethod.POST)
    @Auth(role = "STUDENT")
    public ApiResponse submitDate(@ApiCredentials Credentials credentials, @ApiVariable("submissionId") Long submissionId, @ApiData String newDate) throws ParseException {

        Submission submission = submissionRepo.read(submissionId);

        ApiResponse response = new ApiResponse(SUCCESS);
        if (submission != null) {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            Calendar cal = Calendar.getInstance();
            cal.setTime(df.parse(newDate));

            submission.setSubmissionDate(cal);
            submission = submissionRepo.update(submission);

            actionLogRepo.createPublicLog(submission, credentials, "Submission submitted: " + submission.getSubmissionDate().getTime());

        } else {
            response = new ApiResponse(ERROR, "Could not find a submission with ID " + submissionId);
        }

        return response;
    }

    @Transactional
    @ApiMapping(value = "/{submissionId}/assign-to", method = RequestMethod.POST)
    @Auth(role = "STUDENT")
    public ApiResponse assign(@ApiCredentials Credentials credentials, @ApiVariable("submissionId") Long submissionId, @ApiModel User assignee) {
        Submission submission = submissionRepo.read(submissionId);

        if (assignee != null) {
            assignee = userRepo.findByEmail(assignee.getEmail());
        }

        ApiResponse response = new ApiResponse(SUCCESS);
        if (submission != null) {
            submission.setAssignee(assignee);
            submission = submissionRepo.update(submission);

            actionLogRepo.createPublicLog(submission, credentials, "Submission was assigned to " + assignee.getFirstName() + " " + assignee.getLastName() + "(" + assignee.getEmail() + ")");

        } else {
            response = new ApiResponse(ERROR, "Could not find a submission with ID " + submissionId);
        }

        return response;
    }

    @Transactional
    @ApiMapping(value = "/{submissionId}/remove-field-value", method = RequestMethod.POST)
    @Auth(role = "STUDENT")
    public ApiResponse removeFieldValue(@ApiVariable("submissionId") Long submissionId, @ApiModel FieldValue fieldValue) {
        Submission submission = submissionRepo.read(submissionId);
        submission.removeFieldValue(fieldValue);
        submission = submissionRepo.update(submission);
        return new ApiResponse(SUCCESS, submission);
    }

    @Transactional
    @ApiMapping(value = "/{submissionId}/update-reviewer-notes", method = RequestMethod.POST)
    @Auth(role = "MANAGER")
    public ApiResponse updateReviewerNotes(@ApiCredentials Credentials credentials, @ApiVariable("submissionId") Long submissionId, @ApiData Map<String, String> requestData) {
        Submission submission = submissionRepo.read(submissionId);
        String reviewerNotes = requestData.get("reviewerNotes");
        submission.setReviewerNotes(reviewerNotes);
        submission = submissionRepo.update(submission);
        actionLogRepo.createPrivateLog(submission, credentials, "Submission notes changed to \"" + reviewerNotes + "\"");
        return new ApiResponse(SUCCESS, submission);
    }

    @Transactional
    @ApiMapping("/{submissionId}/needs-correction")
    @Auth(role = "MANAGER")
    public ApiResponse setSubmissionNeedsCorrection(@ApiCredentials Credentials credentials, @ApiVariable Long submissionId) {
        Submission submission = submissionRepo.read(submissionId);
        SubmissionStatus needsCorrectionStatus = submissionStatusRepo.findByName(NEEDS_CORRECTION_SUBMISSION_STATUS_NAME);
        String oldSubmissionStatusName = submission.getSubmissionStatus().getName();
        submission.setSubmissionStatus(needsCorrectionStatus);
        submission = submissionRepo.update(submission);
        actionLogRepo.createPublicLog(submission, credentials, "Submission status was changed from " + oldSubmissionStatusName + " to " + NEEDS_CORRECTION_SUBMISSION_STATUS_NAME);
        return new ApiResponse(SUCCESS, submission);
    }

    @Transactional
    @ApiMapping("/{submissionId}/submit-corrections")
    @Auth(role = "STUDENT")
    public ApiResponse setSubmissionCorrectionsReceived(@ApiCredentials Credentials credentials, @ApiVariable Long submissionId) {
        Submission submission = submissionRepo.read(submissionId);
        String oldSubmissionStatusName = submission.getSubmissionStatus().getName();
        SubmissionStatus needsCorrectionStatus = submissionStatusRepo.findByName(CORRECTIONS_RECEIVED_SUBMISSION_STATUS_NAME);
        submission.setSubmissionStatus(needsCorrectionStatus);
        submission = submissionRepo.update(submission);
        actionLogRepo.createPublicLog(submission, credentials, "Submission status was changed from " + oldSubmissionStatusName + " to " + CORRECTIONS_RECEIVED_SUBMISSION_STATUS_NAME);
        return new ApiResponse(SUCCESS, submission);
    }

    @Transactional
    @ApiMapping(value = "/{submissionId}/add-message", method = RequestMethod.POST)
    @Auth(role = "STUDENT")
    public ApiResponse addMessage(@ApiCredentials Credentials credentials, @ApiVariable Long submissionId, @ApiData String message) {
        Submission submission = submissionRepo.read(submissionId);
        return new ApiResponse(SUCCESS, actionLogRepo.createPublicLog(submission, credentials, message));
    }

    @Transactional
    @ApiMapping(value = "/query/{page}/{size}", method = RequestMethod.POST)
    @Auth(role = "MANAGER")
    public ApiResponse querySubmission(@ApiCredentials Credentials credentials, @ApiVariable Integer page, @ApiVariable Integer size, @ApiModel List<SubmissionListColumn> submissionListColumns) {
        User user = userRepo.findByEmail(credentials.getEmail());
        return new ApiResponse(SUCCESS, submissionRepo.pageableDynamicSubmissionQuery(user.getActiveFilter(), submissionListColumns, new PageRequest(page, size)));
    }

    @ApiMapping(value = "/file", method = RequestMethod.POST)
    public void submissionFile(HttpServletResponse response, @ApiData Map<String, String> requestData) throws IOException {
        response.addHeader("Content-Disposition", "attachment");
        String uri = requestData.get("uri");
        Path path = fileIOUtility.getAbsolutePath(uri);
        Files.copy(path, response.getOutputStream());
        response.getOutputStream().flush();
    }

    @ApiMapping(value = "/file-info", method = RequestMethod.POST)
    public ApiResponse submissionFileInfo(@ApiData Map<String, String> requestData) throws IOException {
        return new ApiResponse(SUCCESS, fileIOUtility.getFileInfo(requestData.get("uri")));
    }

    @Transactional
    @ApiMapping(value = "/{submissionId}/{documentType}/upload", method = RequestMethod.POST)
    @Auth(role = "STUDENT")
    public ApiResponse uploadSubmission(@ApiCredentials Credentials credentials, @ApiVariable Long submissionId, @ApiVariable String documentType, @RequestParam("file") MultipartFile file) throws IOException {
        int hash = credentials.getEmail().hashCode();
        String fileName = file.getOriginalFilename();
        String uri = "private/" + hash + "/" + System.currentTimeMillis() + "-" + fileName;
        fileIOUtility.write(file.getBytes(), uri);

        JsonNode fileInfo = fileIOUtility.getFileInfo(uri);

        actionLogRepo.createPublicLog(submissionRepo.read(submissionId), credentials, documentType + " file " + fileInfo.get("name").asText() + " (" + (fileInfo.get("size").asInt() / 1024) + " KB) uploaded");
        return new ApiResponse(SUCCESS, uri);
    }

    @Transactional
    @ApiMapping(value = "/{submissionId}/{documentType}/rename-file", method = RequestMethod.POST)
    @Auth(role = "MANAGER")
    public ApiResponse renameFile(@ApiCredentials Credentials credentials, @ApiVariable Long submissionId, @ApiVariable String documentType, @ApiData Map<String, String> requestData) throws IOException {
        String newName = requestData.get("newName");
        String oldUri = requestData.get("uri");
        String newUri = oldUri.replace(oldUri.substring(oldUri.lastIndexOf('/') + 1, oldUri.length()), System.currentTimeMillis() + "-" + newName);
        fileIOUtility.copy(oldUri, newUri);
        fileIOUtility.delete(oldUri);

        JsonNode fileInfo = fileIOUtility.getFileInfo(newUri);

        actionLogRepo.createPublicLog(submissionRepo.read(submissionId), credentials, documentType + " file " + fileInfo.get("name").asText() + " (" + (fileInfo.get("size").asInt() / 1024) + " KB) renamed");
        return new ApiResponse(SUCCESS, newUri);
    }

    @Transactional
    @ApiMapping(value = "/{submissionId}/{documentType}/remove-file", method = RequestMethod.POST)
    @Auth(role = "STUDENT")
    public ApiResponse removeFile(@ApiCredentials Credentials credentials, @ApiVariable Long submissionId, @ApiVariable String documentType, @ApiData Map<String, String> requestData) throws IOException {
        ApiResponse apiResponse = null;
        int hash = credentials.getEmail().hashCode();
        String uri = requestData.get("uri");
        if (uri.contains(String.valueOf(hash))) {

            JsonNode fileInfo = fileIOUtility.getFileInfo(uri);

            fileIOUtility.delete(uri);

            actionLogRepo.createPublicLog(submissionRepo.read(submissionId), credentials, documentType + " file " + fileInfo.get("name").asText() + " (" + (fileInfo.get("size").asInt() / 1024) + " KB) removed");

            apiResponse = new ApiResponse(SUCCESS);
        } else {
            apiResponse = new ApiResponse(ERROR, "This is not your file to delete!");
        }
        return apiResponse;
    }

    @Transactional
    @ApiMapping(value = "/{submissionId}/{documentType}/archive-file", method = RequestMethod.POST)
    @Auth(role = "STUDENT")
    public ApiResponse archiveFile(@ApiCredentials Credentials credentials, @ApiVariable Long submissionId, @ApiVariable String documentType, @ApiData Map<String, String> requestData) throws IOException {
        String name = requestData.get("name");
        String oldUri = requestData.get("uri");
        String newUri = oldUri.replace(oldUri.substring(oldUri.lastIndexOf('/') + 1, oldUri.length()), System.currentTimeMillis() + "-archived-" + name);
        fileIOUtility.copy(oldUri, newUri);
        fileIOUtility.delete(oldUri);

        JsonNode fileInfo = fileIOUtility.getFileInfo(newUri);

        actionLogRepo.createPublicLog(submissionRepo.read(submissionId), credentials, "ARCHIVE - " + documentType + " file " + fileInfo.get("name").asText() + " (" + (fileInfo.get("size").asInt() / 1024) + " KB) archived");
        return new ApiResponse(SUCCESS, newUri);
    }

    @ApiMapping("/{submissionId}/send-advisor-email")
    @Auth(role = "MANAGER")
    @Transactional
    public ApiResponse sendAdvisorEmail(@ApiCredentials Credentials credentials, @ApiVariable Long submissionId) {

        Submission submission = submissionRepo.read(submissionId);

        InputType contactInputType = inputTypeRepo.findByName("INPUT_CONTACT");

        EmailTemplate template = emailTemplateRepo.findByNameAndSystemRequired("SYSTEM Advisor Review Request", true);

        String subject = templateUtility.compileString(template.getSubject(), submission);
        String content = templateUtility.compileTemplate(template, submission);

        // TODO: this needs to only send email to the advisor not any field value that is contact type
        submission.getFieldValuesByInputType(contactInputType).forEach(fv -> {

            SimpleMailMessage smm = new SimpleMailMessage();

            smm.setTo(String.join(",", fv.getContacts()));

            User user = userRepo.findByEmail(credentials.getEmail());
            String preferedEmail = user.getSetting("preferedEmail");
            user.getSetting("ccEmail");
            if (user.getSetting("ccEmail").equals("true")) {
                smm.setBcc(preferedEmail == null ? credentials.getEmail() : preferedEmail);
            }

            smm.setSubject(subject);
            smm.setText(content);

            emailSender.send(smm);

        });

        actionLogRepo.createPublicLog(submission, credentials, "Advisor review email manually generated.");

        return new ApiResponse(SUCCESS);
    }

    // TODO: rework, anonymous endpoint for advisor approval, no user available for action log
    @ApiMapping(value = "/{submissionId}/update-advisor-approval", method = RequestMethod.POST)
    @Transactional
    public ApiResponse updateAdvisorApproval(@ApiVariable Long submissionId, @ApiData Map<String, Object> data) {

        Submission submission = submissionRepo.read(submissionId);

        Boolean approveApplicationNode = (Boolean) data.get("approveApplication");
        Boolean approveEmbargoNode = (Boolean) data.get("approveEmbargo");
        String messageNode = (String) data.get("message");
        Boolean clearApproveEmbargoNode = (Boolean) data.get("clearApproveEmbargo");
        Boolean clearApproveApplicationNode = (Boolean) data.get("clearApproveApplication");

        if (approveApplicationNode != null) {
            submission.setApproveApplication(approveApplicationNode);
            String approveApplicationMessage = approveApplicationNode ? "The committee approved the application" : "The committee rejected the Application";
            actionLogRepo.createAdvisorPublicLog(submission, approveApplicationMessage);
        }

        if (approveEmbargoNode != null) {
            submission.setApproveEmbargo(approveEmbargoNode);
            String approveEmbargoMessage = approveEmbargoNode ? "The committee approved the Embargo Options" : "The committee rejected the Embargo Options";
            actionLogRepo.createAdvisorPublicLog(submission, approveEmbargoMessage);
        }

        if (clearApproveEmbargoNode != null && clearApproveEmbargoNode) {
            submission.clearApproveEmbargo();
            actionLogRepo.createAdvisorPublicLog(submission, "The committee has withdrawn its Embargo Approval.");
        }

        if (clearApproveApplicationNode != null && clearApproveApplicationNode) {
            submission.clearApproveApplication();
            actionLogRepo.createAdvisorPublicLog(submission, "The committee has withdrawn its Application Approval.");
        }

        if (messageNode != null)
            actionLogRepo.createAdvisorPublicLog(submission, "Advisor comments : " + messageNode);

        return new ApiResponse(SUCCESS, submission);

    }

    private void processEmailWorkflowRules(Credentials credentials, Submission submission) {

        SimpleMailMessage smm = new SimpleMailMessage();

        List<EmailWorkflowRule> rules = submission.getOrganization().getAggregateEmailWorkflowRules();

        rules.forEach(rule -> {

            if (rule.getSubmissionStatus().equals(submission.getSubmissionStatus()) && !rule.isDisabled()) {

                // TODO: Not all variables are currently being replaced.
                String subject = templateUtility.compileString(rule.getEmailTemplate().getSubject(), submission);
                String content = templateUtility.compileTemplate(rule.getEmailTemplate(), submission);

                rule.getEmailRecipient().getEmails(submission).forEach(email -> {

                    smm.setTo(email);

                    User user = userRepo.findByEmail(credentials.getEmail());
                    String preferedEmail = user.getSetting("preferedEmail");
                    user.getSetting("ccEmail");
                    if (user.getSetting("ccEmail").equals("true")) {
                        smm.setBcc(preferedEmail == null ? credentials.getEmail() : preferedEmail);
                    }

                    smm.setSubject(subject);
                    smm.setText(content);

                    emailSender.send(smm);
                });

            }
        });

    }

}