package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.ERROR;
import static edu.tamu.framework.enums.ApiResponseType.INVALID;
import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;

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

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.tdl.vireo.enums.AppRole;
import org.tdl.vireo.model.CustomActionValue;
import org.tdl.vireo.model.DepositLocation;
import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.EmailWorkflowRule;
import org.tdl.vireo.model.FieldValue;
import org.tdl.vireo.model.InputType;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.SubmissionFieldProfile;
import org.tdl.vireo.model.SubmissionListColumn;
import org.tdl.vireo.model.SubmissionState;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.depositor.Depositor;
import org.tdl.vireo.model.export.ExportPackage;
import org.tdl.vireo.model.repo.ActionLogRepo;
import org.tdl.vireo.model.repo.ConfigurationRepo;
import org.tdl.vireo.model.repo.DepositLocationRepo;
import org.tdl.vireo.model.repo.EmailTemplateRepo;
import org.tdl.vireo.model.repo.FieldValueRepo;
import org.tdl.vireo.model.repo.InputTypeRepo;
import org.tdl.vireo.model.repo.OrganizationRepo;
import org.tdl.vireo.model.repo.SubmissionFieldProfileRepo;
import org.tdl.vireo.model.repo.SubmissionRepo;
import org.tdl.vireo.model.repo.SubmissionStateRepo;
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
import edu.tamu.framework.model.ApiResponse;
import edu.tamu.framework.model.Credentials;
import edu.tamu.framework.util.EmailSender;
import edu.tamu.framework.validation.ValidationResults;

@Controller
@ApiMapping("/submission")
public class SubmissionController {

    private static final String STARTING_SUBMISSION_STATE_NAME = "In Progress";

    private static final String NEEDS_CORRECTION_SUBMISSION_STATE_NAME = "Needs Correction";

    private static final String CORRECTIONS_RECEIVED_SUBMISSION_STATE_NAME = "Corrections Received";

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
    private SubmissionStateRepo submissionStateRepo;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

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
        return new ApiResponse(SUCCESS, submissionRepo.findOne(submissionId));
    }

    @Transactional
    @ApiMapping("/advisor-review/{submissionHash}")
    public ApiResponse getOne(@ApiVariable String submissionHash) {
        Submission submission = submissionRepo.findOneByAdvisorAccessHash(submissionHash);
        return new ApiResponse(SUCCESS, submission);
    }

    @Transactional
    @ApiMapping("/create")
    @Auth(role = "STUDENT")
    public ApiResponse createSubmission(@ApiCredentials Credentials credentials, @ApiData JsonNode dataNode) {
        Submission submission = submissionRepo.create(userRepo.findByEmail(credentials.getEmail()), organizationRepo.findOne(dataNode.get("organizationId").asLong()), submissionStateRepo.findByName(STARTING_SUBMISSION_STATE_NAME), credentials);
        simpMessagingTemplate.convertAndSend("/channel/submission", new ApiResponse(SUCCESS, submissionRepo.findAll()));
        actionLogRepo.createPublicLog(submission, credentials, "Submission created.");
        return new ApiResponse(SUCCESS, submission);
    }

    @Transactional
    @ApiMapping("/delete/{submissionId}")
    @Auth(role = "STUDENT")
    public ApiResponse deleteSubmission(@ApiCredentials Credentials credentials, @ApiVariable Long submissionId) {
        Submission submissionToDelete = submissionRepo.findOne(submissionId);

        ApiResponse response = new ApiResponse(SUCCESS);
        if (!submissionToDelete.getSubmitter().getEmail().equals(credentials.getEmail()) || AppRole.valueOf(credentials.getRole()).ordinal() < AppRole.MANAGER.ordinal()) {
            response = new ApiResponse(ERROR, "Insufficient permisions to delete this submission.");
        } else {
            submissionRepo.delete(submissionId);
        }

        return response;
    }

    @Transactional
    @ApiMapping("/{submissionId}/add-comment")
    @Auth(role = "STUDENT")
    public ApiResponse addComment(@ApiCredentials Credentials credentials, @ApiVariable Long submissionId, @ApiData JsonNode dataNode) {

        Submission submission = submissionRepo.findOne(submissionId);

        String commentVisibility = dataNode.get("commentVisiblity").asText();

        if (commentVisibility.equals("public")) {
            sendEmail(credentials, submission, dataNode);
        } else {
            String subject = dataNode.get("subject").asText();
            String templatedMessage = templateUtility.compileString(dataNode.get("message").asText(), submission);
            actionLogRepo.createPrivateLog(submission, credentials, subject + ": " + templatedMessage);
        }

        return new ApiResponse(SUCCESS);
    }

    @Transactional
    @ApiMapping("/{submissionId}/send-email")
    @Auth(role = "STUDENT")
    public ApiResponse sendEmail(@ApiCredentials Credentials credentials, @ApiVariable Long submissionId, @ApiData JsonNode dataNode) {
        sendEmail(credentials, submissionRepo.findOne(submissionId), dataNode);
        return new ApiResponse(SUCCESS);
    }

    private void sendEmail(Credentials credentials, Submission submission, JsonNode dataNode) {

        String subject = dataNode.get("subject").asText();

        String templatedMessage = templateUtility.compileString(dataNode.get("message").asText(), submission);

        boolean emailTo = dataNode.get("emailTo").asBoolean();

        if (emailTo) {

            boolean emailCc = dataNode.get("emailCc").asBoolean();

            SimpleMailMessage smm = new SimpleMailMessage();

            smm.setTo(dataNode.get("emailToRecipient").asText().split(";"));

            if (emailCc) {
                smm.setCc(dataNode.get("emailCcRecipient").asText().split(";"));
            }

            smm.setSubject(subject);
            smm.setText(templatedMessage);

            emailSender.send(smm);

        }

        actionLogRepo.createPublicLog(submission, credentials, subject + ": " + templatedMessage);

    }

    @Transactional
    @ApiMapping("/{submissionId}/update-field-value/{fieldProfileId}")
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
                Submission submission = submissionRepo.findOne(submissionId);
                if (fieldValue.getId() == null) {
                    submission.addFieldValue(fieldValueRepo.save(fieldValue));
                    submission = submissionRepo.save(submission);
                    fieldValue = submission.getFieldValueByValueAndPredicate(fieldValue.getValue() == null ? "" : fieldValue.getValue(), fieldValue.getFieldPredicate());

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

    @Transactional
    @ApiMapping("/{submissionId}/validate-field-value/{fieldProfileId}")
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
        return submissionFieldProfile.getInputType().getName().equals("INPUT_ORCID") && configurationRepo.getByName("orcid_authentication").getValue().toLowerCase().equals("true");
    }

    @Transactional
    @ApiMapping("/{submissionId}/update-custom-action-value")
    @Auth(role = "MANAGER")
    public ApiResponse updateCustomActionValue(@ApiVariable("submissionId") Long submissionId, @ApiModel CustomActionValue customActionValue) {
        return new ApiResponse(SUCCESS, submissionRepo.findOne(submissionId).editCustomActionValue(customActionValue));
    }

    @Transactional
    @ApiMapping("/{submissionId}/change-status/{submissionStateName}")
    @Auth(role = "STUDENT")
    public ApiResponse changeStatus(@ApiCredentials Credentials credentials, @ApiVariable Long submissionId, @ApiVariable String submissionStateName) {
        Submission submission = submissionRepo.findOne(submissionId);

        ApiResponse response = new ApiResponse(SUCCESS);
        if (submission != null) {

            SubmissionState submissionState = submissionStateRepo.findByName(submissionStateName);
            if (submissionState != null) {
                submission = submissionRepo.updateStatus(submission, submissionState, credentials);
                simpMessagingTemplate.convertAndSend("/channel/submission/" + submissionId, new ApiResponse(SUCCESS, submission));
            } else {
                response = new ApiResponse(ERROR, "Could not find a submission state name " + submissionStateName);
            }
        } else {
            response = new ApiResponse(ERROR, "Could not find a submission with ID " + submissionId);
        }

        processEmailWorkflowRules(submission);

        return response;
    }

    @Transactional
    @ApiMapping("/batch-update-state")
    @Auth(role = "MANAGER")
    public ApiResponse batchUpdateSubmissionStates(@ApiCredentials Credentials credentials, @ApiModel SubmissionState submissionState) {
        User user = userRepo.findByEmail(credentials.getEmail());
        submissionRepo.batchDynamicSubmissionQuery(user.getActiveFilter(), user.getSubmissionViewColumns()).forEach(submission -> {
            submission = submissionRepo.updateStatus(submission, submissionState, credentials);
            processEmailWorkflowRules(submission);
        });
        return new ApiResponse(SUCCESS);
    }

    @Transactional
    @ApiMapping("/{submissionId}/publish/{depositLocationId}")
    @Auth(role = "STUDENT")
    public ApiResponse publish(@ApiCredentials Credentials credentials, @ApiVariable Long submissionId, @ApiVariable Long depositLocationId) throws Exception {
        Submission submission = submissionRepo.findOne(submissionId);

        ApiResponse response = new ApiResponse(SUCCESS);
        if (submission != null) {

            SubmissionState submissionState = submissionStateRepo.findByName("Published");
            if (submissionState != null) {

                DepositLocation depositLocation = depositLocationRepo.findOne(depositLocationId);

                if (depositLocation != null) {

                    Depositor depositor = depositorService.getDepositor(depositLocation.getDepositorName());

                    if (depositor != null) {

                        ExportPackage exportPackage = packagerUtility.packageExport(depositLocation.getPackager(), submission);

                        String result = depositor.deposit(depositLocation, exportPackage);

                        if (result != null) {
                            submission = submissionRepo.updateStatus(submission, submissionState, credentials);
                            simpMessagingTemplate.convertAndSend("/channel/submission/" + submissionId, new ApiResponse(SUCCESS, submission));
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
                response = new ApiResponse(ERROR, "Could not find a submission state name Published");
            }
        } else {
            response = new ApiResponse(ERROR, "Could not find a submission with ID " + submissionId);
        }

        processEmailWorkflowRules(submission);

        return response;
    }

    @Transactional
    @ApiMapping("/batch-publish/{depositLocationId}")
    @Auth(role = "MANAGER")
    public ApiResponse batchPublish(@ApiCredentials Credentials credentials, @ApiVariable Long depositLocationId) {
        ApiResponse response = new ApiResponse(SUCCESS);
        User user = userRepo.findByEmail(credentials.getEmail());
        SubmissionState submissionState = submissionStateRepo.findByName("Published");
        if (submissionState != null) {
            DepositLocation depositLocation = depositLocationRepo.findOne(depositLocationId);
            if (depositLocation != null) {
                for (Submission submission : submissionRepo.batchDynamicSubmissionQuery(user.getActiveFilter(), user.getSubmissionViewColumns())) {
                    Depositor depositor = depositorService.getDepositor(depositLocation.getDepositorName());
                    if (depositor != null) {
                        try {
                            ExportPackage exportPackage = packagerUtility.packageExport(depositLocation.getPackager(), submission);

                            String result = depositor.deposit(depositLocation, exportPackage);

                            if (result != null) {
                                submission = submissionRepo.updateStatus(submission, submissionState, credentials);
                                simpMessagingTemplate.convertAndSend("/channel/submission/" + submission.getId(), new ApiResponse(SUCCESS, submission));
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
            response = new ApiResponse(ERROR, "Could not find a submission state name Published");
        }
        return response;
    }

    @Transactional
    @ApiMapping("/{submissionId}/submit-date")
    @Auth(role = "STUDENT")
    public ApiResponse submitDate(@ApiCredentials Credentials credentials, @ApiVariable("submissionId") Long submissionId, @ApiData String newDate) throws ParseException {

        Submission submission = submissionRepo.findOne(submissionId);

        ApiResponse response = new ApiResponse(SUCCESS);
        if (submission != null) {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            Calendar cal = Calendar.getInstance();
            cal.setTime(df.parse(newDate));

            submission.setSubmissionDate(cal);
            submission = submissionRepo.save(submission);
            simpMessagingTemplate.convertAndSend("/channel/submission/" + submissionId, new ApiResponse(SUCCESS, submission));

            actionLogRepo.createPublicLog(submission, credentials, "Submission submitted: " + submission.getSubmissionDate().getTime());

        } else {
            response = new ApiResponse(ERROR, "Could not find a submission with ID " + submissionId);
        }

        return response;
    }

    @Transactional
    @ApiMapping("/{submissionId}/assign-to")
    @Auth(role = "STUDENT")
    public ApiResponse assign(@ApiCredentials Credentials credentials, @ApiVariable("submissionId") Long submissionId, @ApiModel User assignee) {
        Submission submission = submissionRepo.findOne(submissionId);

        if (assignee != null) {
            assignee = userRepo.findByEmail(assignee.getEmail());
        }

        ApiResponse response = new ApiResponse(SUCCESS);
        if (submission != null) {
            submission.setAssignee(assignee);
            submission = submissionRepo.save(submission);

            actionLogRepo.createPublicLog(submission, credentials, "Submission was assigned to " + assignee.getFirstName() + " " + assignee.getLastName() + "(" + assignee.getEmail() + ")");

            simpMessagingTemplate.convertAndSend("/channel/submission/" + submissionId, new ApiResponse(SUCCESS, submission));

        } else {
            response = new ApiResponse(ERROR, "Could not find a submission with ID " + submissionId);
        }

        return response;
    }

    @Transactional
    @ApiMapping("/{submissionId}/remove-field-value")
    @Auth(role = "STUDENT")
    public ApiResponse removeFieldValue(@ApiVariable("submissionId") Long submissionId, @ApiModel FieldValue fieldValue) {
        Submission submission = submissionRepo.findOne(submissionId);
        submission.removeFieldValue(fieldValue);
        submissionRepo.save(submission);
        simpMessagingTemplate.convertAndSend("/channel/submission/" + submission.getId() + "/removed-field-value", new ApiResponse(SUCCESS, fieldValue));
        return new ApiResponse(SUCCESS);
    }

    @Transactional
    @ApiMapping("/{submissionId}/update-reviewer-notes")
    @Auth(role = "MANAGER")
    public ApiResponse updateReviewerNotes(@ApiVariable("submissionId") Long submissionId, @ApiData Map<String, String> requestData) {
        Submission submission = submissionRepo.findOne(submissionId);
        submission.setReviewerNotes(requestData.get("reviewerNotes"));
        submissionRepo.save(submission);
        return new ApiResponse(SUCCESS);
    }

    @Transactional
    @ApiMapping("/{submissionId}/needs-correction")
    @Auth(role = "MANAGER")
    public ApiResponse setSubmissionNeedsCorrection(@ApiCredentials Credentials credentials, @ApiVariable Long submissionId) {
        Submission submission = submissionRepo.findOne(submissionId);
        SubmissionState needsCorrectionState = submissionStateRepo.findByName(NEEDS_CORRECTION_SUBMISSION_STATE_NAME);
        String oldSubmissionStateName = submission.getSubmissionState().getName();
        submission.setSubmissionState(needsCorrectionState);
        submissionRepo.save(submission);
        actionLogRepo.createPublicLog(submission, credentials, "Submission status was changed from " + oldSubmissionStateName + " to " + NEEDS_CORRECTION_SUBMISSION_STATE_NAME);
        ApiResponse apiResponse = new ApiResponse(SUCCESS, submission);
        simpMessagingTemplate.convertAndSend("/channel/submission/" + submissionId, apiResponse);
        return apiResponse;
    }

    @Transactional
    @ApiMapping("/{submissionId}/submit-corrections")
    @Auth(role = "STUDENT")
    public ApiResponse setSubmissionCorrectionsReceived(@ApiCredentials Credentials credentials, @ApiVariable Long submissionId) {
        Submission submission = submissionRepo.findOne(submissionId);
        String oldSubmissionStateName = submission.getSubmissionState().getName();
        SubmissionState needsCorrectionState = submissionStateRepo.findByName(CORRECTIONS_RECEIVED_SUBMISSION_STATE_NAME);
        submission.setSubmissionState(needsCorrectionState);
        submissionRepo.save(submission);
        actionLogRepo.createPublicLog(submission, credentials, "Submission status was changed from " + oldSubmissionStateName + " to " + CORRECTIONS_RECEIVED_SUBMISSION_STATE_NAME);
        ApiResponse apiResponse = new ApiResponse(SUCCESS, submission);
        simpMessagingTemplate.convertAndSend("/channel/submission/" + submissionId, apiResponse);
        return apiResponse;
    }

    @Transactional
    @ApiMapping("/{submissionId}/add-message")
    @Auth(role = "STUDENT")
    public ApiResponse addMessage(@ApiCredentials Credentials credentials, @ApiVariable Long submissionId, @ApiData String message) {
        Submission submission = submissionRepo.findOne(submissionId);
        actionLogRepo.createPublicLog(submission, credentials, message);
        return new ApiResponse(SUCCESS);
    }

    @Transactional
    @ApiMapping("/query/{page}/{size}")
    @Auth(role = "MANAGER")
    public ApiResponse querySubmission(@ApiCredentials Credentials credentials, @ApiVariable Integer page, @ApiVariable Integer size, @ApiModel List<SubmissionListColumn> submissionListColumns) {
        User user = userRepo.findByEmail(credentials.getEmail());
        return new ApiResponse(SUCCESS, submissionRepo.pageableDynamicSubmissionQuery(user.getActiveFilter(), submissionListColumns, new PageRequest(page, size)));
    }

    @Transactional
    @ApiMapping("/batch-assign-to")
    @Auth(role = "MANAGER")
    public ApiResponse batchAssignTo(@ApiCredentials Credentials credentials, @ApiModel User assignee) {
        User user = userRepo.findByEmail(credentials.getEmail());
        submissionRepo.batchDynamicSubmissionQuery(user.getActiveFilter(), user.getSubmissionViewColumns()).forEach(sub -> {
            sub.setAssignee(assignee);
            actionLogRepo.createPublicLog(sub, credentials, "Submission was assigned to " + assignee.getFirstName() + " " + assignee.getLastName() + "(" + assignee.getEmail() + ")");
            submissionRepo.save(sub);
        });
        return new ApiResponse(SUCCESS);
    }

    @ApiMapping(value = "/file")
    @Auth(role = "STUDENT")
    public void submissionFile(HttpServletResponse response, @ApiCredentials Credentials credentials, @ApiData Map<String, String> requestData) throws IOException {
        response.addHeader("Content-Disposition", "attachment");
        String uri = requestData.get("uri");
        Path path = fileIOUtility.getAbsolutePath(uri);
        Files.copy(path, response.getOutputStream());
        response.getOutputStream().flush();
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

        actionLogRepo.createPublicLog(submissionRepo.findOne(submissionId), credentials, documentType + " file " + fileInfo.get("name").asText() + " (" + (fileInfo.get("size").asInt() / 1024) + " KB) uploaded");
        return new ApiResponse(SUCCESS, uri);
    }

    @Transactional
    @ApiMapping(value = "/{submissionId}/{documentType}/rename-file")
    @Auth(role = "MANAGER")
    public ApiResponse renameFile(@ApiCredentials Credentials credentials, @ApiVariable Long submissionId, @ApiVariable String documentType, @ApiData Map<String, String> requestData) throws IOException {
        String newName = requestData.get("newName");
        String oldUri = requestData.get("uri");
        String newUri = oldUri.replace(oldUri.substring(oldUri.lastIndexOf('/') + 1, oldUri.length()), System.currentTimeMillis() + "-" + newName);
        fileIOUtility.copy(oldUri, newUri);
        fileIOUtility.delete(oldUri);

        JsonNode fileInfo = fileIOUtility.getFileInfo(newUri);

        actionLogRepo.createPublicLog(submissionRepo.findOne(submissionId), credentials, documentType + " file " + fileInfo.get("name").asText() + " (" + (fileInfo.get("size").asInt() / 1024) + " KB) renamed");
        return new ApiResponse(SUCCESS, newUri);
    }

    @Transactional
    @ApiMapping(value = "/{submissionId}/{documentType}/remove-file")
    @Auth(role = "STUDENT")
    public ApiResponse removeFile(@ApiCredentials Credentials credentials, @ApiVariable Long submissionId, @ApiVariable String documentType, @ApiData Map<String, String> requestData) throws IOException {
        ApiResponse apiResponse = null;
        int hash = credentials.getEmail().hashCode();
        String uri = requestData.get("uri");
        if (uri.contains(String.valueOf(hash))) {

            JsonNode fileInfo = fileIOUtility.getFileInfo(uri);

            fileIOUtility.delete(uri);

            actionLogRepo.createPublicLog(submissionRepo.findOne(submissionId), credentials, documentType + " file " + fileInfo.get("name").asText() + " (" + (fileInfo.get("size").asInt() / 1024) + " KB) removed");

            apiResponse = new ApiResponse(SUCCESS);
        } else {
            apiResponse = new ApiResponse(ERROR, "This is not your file to delete!");
        }
        return apiResponse;
    }

    @Transactional
    @ApiMapping(value = "/{submissionId}/{documentType}/archive-file")
    @Auth(role = "STUDENT")
    public ApiResponse archiveFile(@ApiCredentials Credentials credentials, @ApiVariable Long submissionId, @ApiVariable String documentType, @ApiData Map<String, String> requestData) throws IOException {
        ApiResponse apiResponse = null;
        String name = requestData.get("name");
        String oldUri = requestData.get("uri");
        String newUri = oldUri.replace(oldUri.substring(oldUri.lastIndexOf('/') + 1, oldUri.length()), System.currentTimeMillis() + "-archived-" + name);
        fileIOUtility.copy(oldUri, newUri);
        fileIOUtility.delete(oldUri);

        JsonNode fileInfo = fileIOUtility.getFileInfo(newUri);

        actionLogRepo.createPublicLog(submissionRepo.findOne(submissionId), credentials, "ARCHIVE - " + documentType + " file " + fileInfo.get("name").asText() + " (" + (fileInfo.get("size").asInt() / 1024) + " KB) archived");
        return apiResponse;
    }

    @ApiMapping(value = "/file-info")
    @Auth(role = "STUDENT")
    public ApiResponse submissionFileInfo(@ApiCredentials Credentials credentials, @ApiData Map<String, String> requestData) throws IOException {
        return new ApiResponse(SUCCESS, fileIOUtility.getFileInfo(requestData.get("uri")));
    }

    @ApiMapping("/{submissionId}/send-advisor-email")
    @Auth(role = "MANAGER")
    @Transactional
    public ApiResponse sendAdvisorEmail(@ApiCredentials Credentials credentials, @ApiVariable Long submissionId) {

        Submission submission = submissionRepo.findOne(submissionId);

        InputType contactInputType = inputTypeRepo.findByName("INPUT_CONTACT");

        EmailTemplate template = emailTemplateRepo.findByNameAndIsSystemRequired("SYSTEM Advisor Review Request", true);

        String subject = templateUtility.compileString(template.getSubject(), submission);
        String content = templateUtility.compileTemplate(template, submission);

        submission.getFieldValuesByInputType(contactInputType).forEach(fv -> {
            try {
                emailSender.sendEmail(fv.getIdentifier(), subject, content);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        });

        actionLogRepo.createPublicLog(submission, credentials, "Advisor review email manually generated.");

        return new ApiResponse(SUCCESS);
    }

    @ApiMapping("/{submissionId}/update-advisor-approval")
    @Transactional
    public ApiResponse updateAdvisorApproval(@ApiCredentials Credentials credentials, @ApiVariable Long submissionId, @ApiData JsonNode dataNode) {

        Submission submission = submissionRepo.findOne(submissionId);

        JsonNode approveApplicationNode = dataNode.get("approveApplication");
        JsonNode approveEmbargoNode = dataNode.get("approveEmbargo");
        JsonNode messageNode = dataNode.get("message");
        JsonNode clearApproveEmbargoNode = dataNode.get("clearApproveEmbargo");
        JsonNode clearApproveApplicationNode = dataNode.get("clearApproveApplication");

        if (approveApplicationNode != null) {
            submission.setApproveApplication(approveApplicationNode.asBoolean());
            String approveApplicationMessage = approveApplicationNode.asBoolean() ? "The committee approved the application" : "The committee rejected the Application";
            actionLogRepo.createPublicLog(submission, credentials, approveApplicationMessage);
        }

        if (approveEmbargoNode != null) {
            submission.setApproveEmbargo(approveEmbargoNode.asBoolean());
            String approveEmbargoMessage = approveEmbargoNode.asBoolean() ? "The committee approved the Embargo Options" : "The committee rejected the Embargo Options";
            actionLogRepo.createPublicLog(submission, credentials, approveEmbargoMessage);
        }

        if (clearApproveEmbargoNode != null && clearApproveEmbargoNode.asBoolean()) {
            submission.clearApproveEmbargo();
            actionLogRepo.createPublicLog(submission, credentials, "The committee has withdrawn its Embargo Approval.");
        }

        if (clearApproveApplicationNode != null && clearApproveApplicationNode.asBoolean()) {
            submission.clearApproveApplication();
            actionLogRepo.createPublicLog(submission, credentials, "The committee has withdrawn its Application Approval.");
        }

        if (messageNode != null)
            actionLogRepo.createPublicLog(submission, credentials, "Advisor comments : " + messageNode.asText());

        return new ApiResponse(SUCCESS, submission);

    }

    private void processEmailWorkflowRules(Submission submission) {

        List<EmailWorkflowRule> rules = submission.getOrganization().getEmailWorkflowRules();

        rules.forEach(rule -> {

            if (rule.getSubmissionState().equals(submission.getSubmissionState()) && !rule.isDisabled()) {

                // TODO: Not all variables are currently being replaced.
                String subject = templateUtility.compileString(rule.getEmailTemplate().getSubject(), submission);
                String content = templateUtility.compileTemplate(rule.getEmailTemplate(), submission);

                rule.getEmailRecipient().getEmails(submission).forEach(email -> {
                    try {
                        emailSender.sendEmail(email, subject, content);
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }
                });

            }
        });

    }

}