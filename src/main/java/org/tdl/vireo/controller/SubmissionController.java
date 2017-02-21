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
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.tdl.vireo.enums.AppRole;
import org.tdl.vireo.model.CustomActionValue;
import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.EmailWorkflowRule;
import org.tdl.vireo.model.FieldValue;
import org.tdl.vireo.model.InputType;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.SubmissionFieldProfile;
import org.tdl.vireo.model.SubmissionListColumn;
import org.tdl.vireo.model.SubmissionState;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.ConfigurationRepo;
import org.tdl.vireo.model.repo.EmailTemplateRepo;
import org.tdl.vireo.model.repo.FieldValueRepo;
import org.tdl.vireo.model.repo.InputTypeRepo;
import org.tdl.vireo.model.repo.OrganizationRepo;
import org.tdl.vireo.model.repo.SubmissionFieldProfileRepo;
import org.tdl.vireo.model.repo.SubmissionRepo;
import org.tdl.vireo.model.repo.SubmissionStateRepo;
import org.tdl.vireo.model.repo.UserRepo;
import org.tdl.vireo.model.validation.FieldValueValidator;
import org.tdl.vireo.util.FileIOUtility;
import org.tdl.vireo.util.OrcidUtility;
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
        return new ApiResponse(SUCCESS, submission);
    }

    @Transactional
    @ApiMapping("/delete/{id}")
    @Auth(role = "STUDENT")
    public ApiResponse deleteSubmission(@ApiCredentials Credentials credentials, @ApiVariable Long id) {
        Submission submissionToDelete = submissionRepo.findOne(id);

        ApiResponse response = new ApiResponse(SUCCESS);
        if (!submissionToDelete.getSubmitter().getEmail().equals(credentials.getEmail()) || AppRole.valueOf(credentials.getRole()).ordinal() < AppRole.MANAGER.ordinal()) {
            response = new ApiResponse(ERROR, "Insufficient permisions to delete this submission.");
        } else {
            submissionRepo.delete(id);
        }

        return response;
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
                    
                } else {
                    fieldValue = fieldValueRepo.save(fieldValue);
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
    public ApiResponse changeStatus(@ApiVariable Long submissionId, @ApiVariable String submissionStateName) {
        Submission submission = submissionRepo.findOne(submissionId);

        ApiResponse response = new ApiResponse(SUCCESS);
        if (submission != null) {

            SubmissionState submissionState = submissionStateRepo.findByName(submissionStateName);
            if (submissionState != null) {
                submission.setSubmissionState(submissionState);
                submission = submissionRepo.saveAndFlush(submission);
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
    @ApiMapping("/{submissionId}/submit-date")
    @Auth(role = "STUDENT")
    public ApiResponse submitDate(@ApiVariable("submissionId") Long submissionId, @ApiData String newDate) throws ParseException {

        Submission submission = submissionRepo.findOne(submissionId);

        ApiResponse response = new ApiResponse(SUCCESS);
        if (submission != null) {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            Calendar cal = Calendar.getInstance();
            cal.setTime(df.parse(newDate));

            submission.setSubmissionDate(cal);
            submission = submissionRepo.save(submission);
            simpMessagingTemplate.convertAndSend("/channel/submission/" + submissionId, new ApiResponse(SUCCESS, submission));
        } else {
            response = new ApiResponse(ERROR, "Could not find a submission with ID " + submissionId);
        }

        return response;
    }

    @Transactional
    @ApiMapping("/{submissionId}/assign-to")
    @Auth(role = "STUDENT")
    public ApiResponse assign(@ApiVariable("submissionId") Long submissionId, @ApiModel User assignee) {
        Submission submission = submissionRepo.findOne(submissionId);

        if (assignee != null) {
            assignee = userRepo.findByEmail(assignee.getEmail());
        }

        ApiResponse response = new ApiResponse(SUCCESS);
        if (submission != null) {
            submission.setAssignee(assignee);
            submission = submissionRepo.save(submission);
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
    public ApiResponse setSubmissionNeedsCorrection(@ApiVariable Long submissionId) {
        Submission submission = submissionRepo.findOne(submissionId);
        SubmissionState needsCorrectionState = submissionStateRepo.findByName(NEEDS_CORRECTION_SUBMISSION_STATE_NAME);
        submission.setSubmissionState(needsCorrectionState);
        submissionRepo.save(submission);
        simpMessagingTemplate.convertAndSend("/channel/submission", new ApiResponse(SUCCESS, submission));
        return new ApiResponse(SUCCESS, new ApiResponse(SUCCESS, submission));
    }
    
    @Transactional
    @ApiMapping("/{submissionId}/submit-corrections")
    @Auth(role = "STUDENT")
    public ApiResponse setSubmissionCorrectionsReceived(@ApiVariable Long submissionId) {
        Submission submission = submissionRepo.findOne(submissionId);
        SubmissionState needsCorrectionState = submissionStateRepo.findByName(CORRECTIONS_RECEIVED_SUBMISSION_STATE_NAME);
        submission.setSubmissionState(needsCorrectionState);
        submissionRepo.save(submission);
        simpMessagingTemplate.convertAndSend("/channel/submission", new ApiResponse(SUCCESS, submission));
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
    @ApiMapping("/batch-update-state")
    @Auth(role = "MANAGER")
    public ApiResponse batchUpdateSubmissionStates(@ApiCredentials Credentials credentials, @ApiModel SubmissionState submissionState) {
        User user = userRepo.findByEmail(credentials.getEmail());
        submissionRepo.batchDynamicSubmissionQuery(user.getActiveFilter(), user.getSubmissionViewColumns()).forEach(sub -> {
            sub.setSubmissionState(submissionState);
            submissionRepo.saveAndFlush(sub);
            processEmailWorkflowRules(sub);
        });
        return new ApiResponse(SUCCESS);
    }

    @Transactional
    @ApiMapping("/batch-assign-to")
    @Auth(role = "MANAGER")
    public ApiResponse batchAssignTo(@ApiCredentials Credentials credentials, @ApiModel User assignee) {
        User user = userRepo.findByEmail(credentials.getEmail());
        submissionRepo.batchDynamicSubmissionQuery(user.getActiveFilter(), user.getSubmissionViewColumns()).forEach(sub -> {
            sub.setAssignee(assignee);
            submissionRepo.save(sub);
        });
        return new ApiResponse(SUCCESS);
    }

    @ApiMapping(value = "/upload", method = RequestMethod.POST)
    @Auth(role = "STUDENT")
    public ApiResponse uploadSubmission(@ApiCredentials Credentials credentials, @RequestParam("file") MultipartFile file) throws IOException {
        int hash = credentials.getEmail().hashCode();
        String fileName = file.getOriginalFilename();
        String uri = "private/" + hash + "/" + System.currentTimeMillis() + "-" + fileName;
        fileIOUtility.write(file.getBytes(), uri);
        return new ApiResponse(SUCCESS, uri);
    }

    @ApiMapping(value = "/file")
    @Auth(role = "STUDENT")
    public void submissionFile(HttpServletResponse response, @ApiCredentials Credentials credentials, @ApiData Map<String, String> requestHeaders) throws IOException {
        response.addHeader("Content-Disposition", "attachment");
        Path path = fileIOUtility.getAbsolutePath(requestHeaders.get("uri"));
        Files.copy(path, response.getOutputStream());
        response.getOutputStream().flush();
    }

    @ApiMapping(value = "/remove-file")
    @Auth(role = "STUDENT")
    public ApiResponse submissionFile(@ApiCredentials Credentials credentials, @ApiData Map<String, String> requestHeaders) throws IOException {
        ApiResponse apiResponse = null;
        int hash = credentials.getEmail().hashCode();
        String uri = requestHeaders.get("uri");
        if (uri.contains(String.valueOf(hash))) {
            fileIOUtility.delete(uri);
            apiResponse = new ApiResponse(SUCCESS);
        } else {
            apiResponse = new ApiResponse(ERROR, "This is not your file to delete!");
        }
        return apiResponse;
    }

    @ApiMapping(value = "/file-info")
    @Auth(role = "STUDENT")
    public ApiResponse submissionFileInfo(@ApiCredentials Credentials credentials, @ApiData Map<String, String> requestHeaders) throws IOException {
        return new ApiResponse(SUCCESS, fileIOUtility.getFileInfo(requestHeaders.get("uri")));
    }

    @ApiMapping(value = "/rename-file")
    @Auth(role = "MANAGER")
    public ApiResponse renameFile(@ApiData Map<String, String> requestData) throws IOException {
        String newName = requestData.get("newName");
        String oldUri = requestData.get("uri");
        String newUri = oldUri.replace(oldUri.substring(oldUri.lastIndexOf('/') + 1, oldUri.length()), System.currentTimeMillis() + "-" + newName);
        fileIOUtility.copy(oldUri, newUri);
        fileIOUtility.delete(oldUri);
        return new ApiResponse(SUCCESS, newUri);
    }

    @ApiMapping("/{submissionId}/send-advisor-email")
    @Auth(role = "MANAGER")
    public ApiResponse sendAdvisorEmail(@ApiVariable Long submissionId) {

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

        return new ApiResponse(SUCCESS);
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
