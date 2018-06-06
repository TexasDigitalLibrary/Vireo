package org.tdl.vireo.controller;

import static edu.tamu.weaver.response.ApiStatus.ERROR;
import static edu.tamu.weaver.response.ApiStatus.INVALID;
import static edu.tamu.weaver.response.ApiStatus.SUCCESS;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
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
import java.util.concurrent.ExecutionException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.tdl.vireo.exception.OrganizationDoesNotAcceptSubmissionsExcception;
import org.tdl.vireo.model.CustomActionValue;
import org.tdl.vireo.model.DepositLocation;
import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.EmailWorkflowRule;
import org.tdl.vireo.model.FieldValue;
import org.tdl.vireo.model.InputType;
import org.tdl.vireo.model.NamedSearchFilterGroup;
import org.tdl.vireo.model.Role;
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
import org.tdl.vireo.model.repo.NamedSearchFilterGroupRepo;
import org.tdl.vireo.model.repo.OrganizationRepo;
import org.tdl.vireo.model.repo.SubmissionFieldProfileRepo;
import org.tdl.vireo.model.repo.SubmissionRepo;
import org.tdl.vireo.model.repo.SubmissionStatusRepo;
import org.tdl.vireo.model.repo.UserRepo;
import org.tdl.vireo.model.validation.FieldValueValidator;
import org.tdl.vireo.service.DepositorService;
import org.tdl.vireo.utility.FileIOUtility;
import org.tdl.vireo.utility.OrcidUtility;
import org.tdl.vireo.utility.PackagerUtility;
import org.tdl.vireo.utility.TemplateUtility;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.weaver.auth.annotation.WeaverCredentials;
import edu.tamu.weaver.auth.annotation.WeaverUser;
import edu.tamu.weaver.auth.model.Credentials;
import edu.tamu.weaver.data.model.ApiPage;
import edu.tamu.weaver.email.service.EmailSender;
import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.response.ApiView;
import edu.tamu.weaver.validation.results.ValidationResults;

@RestController
@RequestMapping("/submission")
public class SubmissionController {

    private Logger LOG = LoggerFactory.getLogger(this.getClass());

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

    @Value("${app.document.path:private/}")
    private String documentPath;

    @RequestMapping("/all")
    @PreAuthorize("hasRole('REVIEWER')")
    public ApiResponse getAll() {
        return new ApiResponse(SUCCESS, submissionRepo.findAll());
    }

    @RequestMapping("/all-by-user")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse getAllByUser(@WeaverUser User user) {
        return new ApiResponse(SUCCESS, submissionRepo.findAllBySubmitter(user));
    }

    @RequestMapping("/get-one/{submissionId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse getOne(@WeaverUser User user, @PathVariable Long submissionId) {
        Submission submission = null;
        if (user.getRole().ordinal() <= Role.ROLE_MANAGER.ordinal()) {
            submission = submissionRepo.read(submissionId);
        } else {
            submission = submissionRepo.findOneBySubmitterAndId(user, submissionId);
        }
        if (submission == null) {
            return new ApiResponse(ERROR, "Submission not found");
        }
        return new ApiResponse(SUCCESS, submission);
    }

    @RequestMapping("/advisor-review/{submissionHash}")
    public ApiResponse getOne(@PathVariable String submissionHash) {
        Submission submission = submissionRepo.findOneByAdvisorAccessHash(submissionHash);
        return new ApiResponse(SUCCESS, submission);
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse createSubmission(@WeaverUser User user, @WeaverCredentials Credentials credentials, @RequestBody Map<String, String> data) throws OrganizationDoesNotAcceptSubmissionsExcception {
        Submission submission = submissionRepo.create(user, organizationRepo.read(Long.valueOf(data.get("organizationId"))), submissionStatusRepo.findByName(STARTING_SUBMISSION_STATUS_NAME), credentials);
        actionLogRepo.createPublicLog(submission, user, "Submission created.");
        return new ApiResponse(SUCCESS, submission);
    }

    @RequestMapping("/delete/{submissionId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse deleteSubmission(@WeaverUser User user, @PathVariable Long submissionId) {
        Submission submissionToDelete = submissionRepo.read(submissionId);

        ApiResponse response = new ApiResponse(SUCCESS);
        if (submissionToDelete.getSubmitter().getEmail().equals(user.getEmail()) || user.getRole().ordinal() <= Role.ROLE_MANAGER.ordinal()) {
            submissionRepo.delete(submissionId);
        } else {
            response = new ApiResponse(ERROR, "Insufficient permisions to delete this submission.");
        }

        return response;
    }

    @RequestMapping(value = "/{submissionId}/add-comment", method = RequestMethod.POST)
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse addComment(@WeaverUser User user, @PathVariable Long submissionId, @RequestBody Map<String, Object> data) {

        Submission submission = submissionRepo.read(submissionId);

        String commentVisibility = data.get("commentVisiblity") != null ? (String) data.get("commentVisiblity") : "public";

        if (commentVisibility.equals("public")) {
            sendEmail(user, submission, data);
        } else {
            String subject = (String) data.get("subject");
            String templatedMessage = templateUtility.compileString((String) data.get("message"), submission);
            actionLogRepo.createPrivateLog(submission, user, subject + ": " + templatedMessage);
        }

        return new ApiResponse(SUCCESS);
    }

    @RequestMapping(value = "/{submissionId}/send-email", method = RequestMethod.POST)
    @PreAuthorize("hasRole('REVIEWER')")
    public ApiResponse sendEmail(@WeaverUser User user, @PathVariable Long submissionId, @RequestBody Map<String, Object> data) {
        sendEmail(user, submissionRepo.read(submissionId), data);
        return new ApiResponse(SUCCESS);
    }

    private void sendEmail(User user, Submission submission, Map<String, Object> data) {

        String subject = (String) data.get("subject");

        String templatedMessage = templateUtility.compileString((String) data.get("message"), submission);
        
        String recipientEmails = new String();
        
        boolean sendRecipientEmail = (boolean) data.get("sendEmailToRecipient");

        if (sendRecipientEmail) {
            boolean sendCCRecipientEmail = (boolean) data.get("sendEmailToCCRecipient");

            SimpleMailMessage smm = new SimpleMailMessage();

            String recipientEmail = (String) data.get("recipientEmail");

            recipientEmails = "Email sent to: [ " + recipientEmail + " ] ";

            smm.setTo(recipientEmail.split(";"));

            if (sendCCRecipientEmail) {
                String ccRecipientEmail = (String) data.get("ccRecipientEmail");
                smm.setCc(ccRecipientEmail.split(";"));
                recipientEmails = recipientEmails + " and cc to: [ " + ccRecipientEmail + " ] ";
            }

            String preferredEmail = user.getSetting("preferedEmail");

            if (user.getSetting("ccEmail") != null && user.getSetting("ccEmail").equals("true")) {
                smm.setBcc(preferredEmail == null ? user.getEmail() : preferredEmail);
            }

            smm.setSubject(subject);
            smm.setText(templatedMessage);

            emailSender.send(smm);

        }

        actionLogRepo.createPublicLog(submission, user, recipientEmails + subject + ": " + templatedMessage);
    }

    @RequestMapping(value = "/batch-comment")
    @PreAuthorize("hasRole('REVIEWER')")
    public ApiResponse batchComment(@WeaverUser User user, @RequestBody Map<String, Object> data) {
        submissionRepo.batchDynamicSubmissionQuery(user.getActiveFilter(), user.getSubmissionViewColumns()).forEach(sub -> {
            Map<String, Object> subMessage = new HashMap<String, Object>(data);
            if ((boolean) data.get("sendEmailToRecipient") || (boolean) data.get("sendEmailToCCRecipient")) {
                subMessage.put("recipientEmail", findEmailValue(sub, subMessage.get("recipientEmail").toString()));
                subMessage.put("ccRecipientEmail", findEmailValue(sub, subMessage.get("ccRecipientEmail").toString()));
            }

            addComment(user, sub.getId(), subMessage);
        });
        return new ApiResponse(SUCCESS);

    }

    private String findEmailValue(Submission submission, String recipient) {
        if (recipient.equals("student")) {
            // data.put("recipientEmail", )
            return submission.getSubmitter().getSetting("preferedEmail");

        } else if (recipient.equals("advisor")) {
            return submission.getFieldValuesByInputType(inputTypeRepo.findByName("INPUT_CONTACT")).get(0).getContacts().get(0);
        } else {
            return "";
        }
    }

    @RequestMapping(value = "/{submissionId}/update-field-value/{fieldProfileId}", method = RequestMethod.POST)
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse updateFieldValue(@WeaverUser User user, @PathVariable Long submissionId, @PathVariable String fieldProfileId, @RequestBody FieldValue fieldValue) {
        ApiResponse apiResponse = null;
        SubmissionFieldProfile submissionFieldProfile = submissionFieldProfileRepo.getOne(Long.parseLong(fieldProfileId));
        ValidationResults validationResults = getValidationResults(submissionFieldProfile.getId().toString(), fieldValue);
        if (validationResults.isValid()) {
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
                        actionLogRepo.createPublicLog(submission, user, submissionFieldProfile.getFieldGlosses().get(0).getValue() + " was set to " + fieldValue.getValue());
                    }

                } else {

                    FieldValue oldFieldValue = fieldValueRepo.findOne(fieldValue.getId());
                    String oldValue = oldFieldValue.getValue();
                    fieldValue = fieldValueRepo.save(fieldValue);

                    if (submissionFieldProfile.getLogged()) {
                        actionLogRepo.createPublicLog(submission, user, submissionFieldProfile.getFieldGlosses().get(0).getValue() + " was changed from " + oldValue + " to " + fieldValue.getValue());
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

    @RequestMapping(value = "/{submissionId}/validate-field-value/{fieldProfileId}", method = RequestMethod.POST)
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse validateFieldValue(@WeaverUser User user, @PathVariable Long submissionId, @PathVariable String fieldProfileId, @RequestBody FieldValue fieldValue) {
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

    @RequestMapping(value = "/{submissionId}/update-custom-action-value", method = RequestMethod.POST)
    @PreAuthorize("hasRole('REVIEWER')")
    public ApiResponse updateCustomActionValue(@PathVariable("submissionId") Long submissionId, @RequestBody CustomActionValue customActionValue) {
        return new ApiResponse(SUCCESS, submissionRepo.read(submissionId).editCustomActionValue(customActionValue));
    }

    @RequestMapping("/{submissionId}/change-status/{submissionStatusName}")
    @PreAuthorize("hasRole('REVIEWER')")
    public ApiResponse changeStatus(@WeaverUser User user, @PathVariable Long submissionId, @PathVariable String submissionStatusName) {
        Submission submission = submissionRepo.read(submissionId);
        ApiResponse response;
        if (submission != null) {
            SubmissionStatus submissionStatus = submissionStatusRepo.findByName(submissionStatusName);
            if (submissionStatus != null) {
                submission = submissionRepo.updateStatus(submission, submissionStatus, user);
                response = new ApiResponse(SUCCESS, submission);
                simpMessagingTemplate.convertAndSend("/channel/submission/" + submissionId, response);
            } else {
                response = new ApiResponse(ERROR, "Could not find a submission status name " + submissionStatusName);
            }
        } else {
            response = new ApiResponse(ERROR, "Could not find a submission with ID " + submissionId);
        }
        processEmailWorkflowRules(user, submission);
        return response;
    }

    @RequestMapping("/batch-update-status/{submissionStatusName}")
    @PreAuthorize("hasRole('REVIEWER')")
    public ApiResponse batchUpdateSubmissionStatuses(@WeaverUser User user, @PathVariable String submissionStatusName) {
        submissionRepo.batchDynamicSubmissionQuery(user.getActiveFilter(), user.getSubmissionViewColumns()).forEach(submission -> {
            SubmissionStatus submissionStatus = submissionStatusRepo.findByName(submissionStatusName);
            submission = submissionRepo.updateStatus(submission, submissionStatus, user);
            processEmailWorkflowRules(user, submission);
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

                DepositLocation depositLocation = depositLocationRepo.findOne(depositLocationId);

                if (depositLocation != null) {

                    Depositor depositor = depositorService.getDepositor(depositLocation.getDepositorName());

                    if (depositor != null) {

                        ExportPackage exportPackage = packagerUtility.packageExport(depositLocation.getPackager(), submission);

                        String result = depositor.deposit(depositLocation, exportPackage);

                        if (result != null) {
                            submission = submissionRepo.updateStatus(submission, submissionStatus, user);
                            response = new ApiResponse(SUCCESS, submission);
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

        processEmailWorkflowRules(user, submission);

        return response;
    }

    @PreAuthorize("hasRole('REVIEWER')")
    @RequestMapping("/batch-export/{packagerName}/{filterId}")
    public void batchExportWithFilter(HttpServletResponse response, @WeaverUser User user, @PathVariable String packagerName, @PathVariable Long filterId) throws IOException {
        NamedSearchFilterGroup filter = namedSearchFilterGroupRepo.findOne(filterId);
        processBatchExport(response, user, packagerName, filter);
    }

    @PreAuthorize("hasRole('REVIEWER')")
    @RequestMapping("/batch-export/{packagerName}")
    public void batchExport(HttpServletResponse response, @WeaverUser User user, @PathVariable String packagerName) throws IOException {
        NamedSearchFilterGroup activeFilter = user.getActiveFilter();
        processBatchExport(response, user, packagerName, activeFilter);
    }

    private void processBatchExport(HttpServletResponse response, User user, String packagerName, NamedSearchFilterGroup filter) throws IOException {
        AbstractPackager<?> packager = packagerUtility.getPackager(packagerName);

        List<SubmissionListColumn> columns = filter.getColumnsFlag() ? filter.getSavedColumns() : user.getSubmissionViewColumns();

        switch (packagerName.trim()) {
        case "Excel":
            List<Submission> submissions = submissionRepo.batchDynamicSubmissionQuery(filter, columns);

            HSSFWorkbook workbook = new HSSFWorkbook();

            HSSFSheet worksheet = workbook.createSheet();

            int rowCount = 0;

            HSSFRow header = worksheet.createRow(rowCount++);

            for (int i = 0; i < columns.size(); i++) {
                SubmissionListColumn column = columns.get(i);
                header.createCell(i).setCellValue(column.getTitle());
            }

            for (Submission submission : submissions) {
                ExportPackage exportPackage = packagerUtility.packageExport(packager, submission, columns);
                if (exportPackage.isMap()) {
                    @SuppressWarnings({ "unchecked" })
                    Map<String, String> rowData = (Map<String, String>) exportPackage.getPayload();
                    HSSFRow row = worksheet.createRow(rowCount++);
                    for (int i = 0; i < columns.size(); i++) {
                        SubmissionListColumn column = columns.get(i);
                        row.createCell(i).setCellValue(rowData.get(column.getTitle()));
                    }
                }
            }

            for (int i = 0; i < columns.size(); i++) {
                worksheet.autoSizeColumn(i);
            }

            response.setContentType(packager.getMimeType());
            response.setHeader("Content-Disposition", "inline; filename=" + packagerName + "." + packager.getFileExtension());
            workbook.write(response.getOutputStream());

            break;
        case "DSpaceMETS":
        case "ProQuest":
            try {
                ZipOutputStream zos = new ZipOutputStream(response.getOutputStream());

                // TODO: need a more dynamic way to achieve this
                if (packagerName.equals("ProQuest")) {
                    // TODO: add filter for UMI Publication true
                }

                for (Submission submission : submissionRepo.batchDynamicSubmissionQuery(filter, columns)) {
                    ExportPackage exportPackage = packagerUtility.packageExport(packager, submission);

                    if (exportPackage.isFile()) {
                        File exportFile = (File) exportPackage.getPayload();
                        zos.putNextEntry(new ZipEntry(exportFile.getName()));
                        zos.write(Files.readAllBytes(exportFile.toPath()));
                        zos.closeEntry();
                    }

                }
                zos.close();

                response.setContentType(packager.getMimeType());
                response.setHeader("Content-Disposition", "inline; filename=" + packagerName + "." + packager.getFileExtension());
            } catch (Exception e) {
                response.setContentType("application/json");

                ApiResponse apiResponse = new ApiResponse(ERROR, "Something went wrong with the export!");
                PrintWriter out = response.getWriter();
                out.print(objectMapper.writeValueAsString(apiResponse));
                out.close();
            }
            break;
        default:
            response.setContentType("application/json");

            ApiResponse apiResponse = new ApiResponse(ERROR, "No packager " + packagerName + " found!");
            PrintWriter out = response.getWriter();
            out.print(objectMapper.writeValueAsString(apiResponse));
            out.close();
        }

        response.getOutputStream().close();
    }

    @RequestMapping(value = "/batch-assign-to", method = RequestMethod.POST)
    @PreAuthorize("hasRole('REVIEWER')")
    public ApiResponse batchAssignTo(@WeaverUser User user, @RequestBody User assignee) {
        submissionRepo.batchDynamicSubmissionQuery(user.getActiveFilter(), user.getSubmissionViewColumns()).forEach(sub -> {
            sub.setAssignee(assignee);
            actionLogRepo.createPublicLog(sub, user, "Submission was assigned to " + assignee.getFirstName() + " " + assignee.getLastName() + "(" + assignee.getEmail() + ")");
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
            DepositLocation depositLocation = depositLocationRepo.findOne(depositLocationId);
            if (depositLocation != null) {
                for (Submission submission : submissionRepo.batchDynamicSubmissionQuery(user.getActiveFilter(), user.getSubmissionViewColumns())) {
                    Depositor depositor = depositorService.getDepositor(depositLocation.getDepositorName());
                    if (depositor != null) {
                        try {
                            ExportPackage exportPackage = packagerUtility.packageExport(depositLocation.getPackager(), submission);

                            String result = depositor.deposit(depositLocation, exportPackage);

                            if (result != null) {
                                submission = submissionRepo.updateStatus(submission, submissionStatus, user);
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
            actionLogRepo.createPublicLog(submission, user, "Submission date set to: " + logdf.format(cal.getTime()));

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

        if (assignee != null) {
            if (submission != null) {
                submission.setAssignee(assignee);
                submission = submissionRepo.update(submission);
                actionLogRepo.createPublicLog(submission, user, "Submission was assigned to " + assignee.getFirstName() + " " + assignee.getLastName() + "(" + assignee.getEmail() + ")");

            } else {
                response = new ApiResponse(ERROR, "Could not find a submission with ID " + submissionId);
            }
        } else {
            response = new ApiResponse(ERROR, "Could not find a assignee!");
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
        actionLogRepo.createPrivateLog(submission, user, "Submission notes changed to \"" + reviewerNotes + "\"");
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
        actionLogRepo.createPublicLog(submission, user, "Submission status was changed from " + oldSubmissionStatusName + " to " + NEEDS_CORRECTION_SUBMISSION_STATUS_NAME);
        return new ApiResponse(SUCCESS, submission);
    }

    @RequestMapping("/{submissionId}/submit-corrections")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse setSubmissionCorrectionsReceived(@WeaverUser User user, @PathVariable Long submissionId) {
        Submission submission = submissionRepo.read(submissionId);
        String oldSubmissionStatusName = submission.getSubmissionStatus().getName();
        SubmissionStatus needsCorrectionStatus = submissionStatusRepo.findByName(CORRECTIONS_RECEIVED_SUBMISSION_STATUS_NAME);
        submission.setSubmissionStatus(needsCorrectionStatus);
        submission = submissionRepo.update(submission);
        actionLogRepo.createPublicLog(submission, user, "Submission status was changed from " + oldSubmissionStatusName + " to " + CORRECTIONS_RECEIVED_SUBMISSION_STATUS_NAME);
        return new ApiResponse(SUCCESS, submission);
    }

    @RequestMapping(value = "/{submissionId}/add-message", method = RequestMethod.POST)
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse addMessage(@WeaverUser User user, @PathVariable Long submissionId, @RequestBody String message) {
        Submission submission = submissionRepo.read(submissionId);
        return new ApiResponse(SUCCESS, actionLogRepo.createPublicLog(submission, user, message));
    }

    @JsonView(ApiView.Partial.class)
    @RequestMapping("/query/{page}/{size}")
    @PreAuthorize("hasRole('REVIEWER')")
    public ApiResponse querySubmission(@WeaverUser User user, @PathVariable Integer page, @PathVariable Integer size) throws ExecutionException {
        long startTime = System.nanoTime();
        NamedSearchFilterGroup activeFilter = user.getActiveFilter();
        List<SubmissionListColumn> submissionListColumns = activeFilter.getColumnsFlag() ? activeFilter.getSavedColumns() : user.getSubmissionViewColumns();
        Page<Submission> submissions = submissionRepo.pageableDynamicSubmissionQuery(activeFilter, submissionListColumns, new PageRequest(page, size));
        long endTime = System.nanoTime();
        long duration = (endTime - startTime);
        LOG.info("Dynamic query took " + (double) (duration / 1000000000.0) + " seconds");
        return new ApiResponse(SUCCESS, new ApiPage<Submission>(submissions));
    }

    @RequestMapping("/file")
    public void submissionFile(HttpServletResponse response, @RequestHeader String uri) throws IOException {
        response.addHeader("Content-Disposition", "attachment");
        Path path = fileIOUtility.getAbsolutePath(uri);
        Files.copy(path, response.getOutputStream());
        response.getOutputStream().flush();
    }

    @RequestMapping(value = "/file-info", method = RequestMethod.POST)
    public ApiResponse submissionFileInfo(@RequestBody Map<String, String> requestData) throws IOException {
        return new ApiResponse(SUCCESS, fileIOUtility.getFileInfo(requestData.get("uri")));
    }

    @RequestMapping(value = "/{submissionId}/{documentType}/upload-file", method = RequestMethod.POST)
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse uploadFile(@WeaverUser User user, @PathVariable Long submissionId, @PathVariable String documentType, @RequestParam MultipartFile file) throws IOException {
        int hash = user.getEmail().hashCode();
        String fileName = file.getOriginalFilename();
        String uri = documentPath + hash + "/" + System.currentTimeMillis() + "-" + fileName;
        fileIOUtility.write(file.getBytes(), uri);
        JsonNode fileInfo = fileIOUtility.getFileInfo(uri);
        actionLogRepo.createPublicLog(submissionRepo.read(submissionId), user, documentType + " file " + fileInfo.get("name").asText() + " (" + (fileInfo.get("size").asInt() / 1024) + " KB) uploaded");
        return new ApiResponse(SUCCESS, uri);
    }

    @RequestMapping(value = "/{submissionId}/{documentType}/rename-file", method = RequestMethod.POST)
    @PreAuthorize("hasRole('REVIEWER')")
    public ApiResponse renameFile(@WeaverUser User user, @PathVariable Long submissionId, @PathVariable String documentType, @RequestBody Map<String, String> requestData) throws IOException {
        String newName = requestData.get("newName");
        String oldUri = requestData.get("uri");
        String newUri = oldUri.replace(oldUri.substring(oldUri.lastIndexOf('/') + 1, oldUri.length()), System.currentTimeMillis() + "-" + newName);
        fileIOUtility.copy(oldUri, newUri);
        fileIOUtility.delete(oldUri);
        JsonNode fileInfo = fileIOUtility.getFileInfo(newUri);
        actionLogRepo.createPublicLog(submissionRepo.read(submissionId), user, documentType + " file " + fileInfo.get("name").asText() + " (" + (fileInfo.get("size").asInt() / 1024) + " KB) renamed");
        return new ApiResponse(SUCCESS, newUri);
    }

    @RequestMapping("/{submissionId}/{fieldValueId}/remove-file")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse removeFile(@WeaverUser User user, @PathVariable Long submissionId, @PathVariable Long fieldValueId) throws IOException {
        ApiResponse apiResponse = new ApiResponse(SUCCESS);
        int hash = user.getEmail().hashCode();
        FieldValue fileFieldValue = fieldValueRepo.findOne(fieldValueId);
        String documentType = fileFieldValue.getFieldPredicate().getValue();
        String uri = fileFieldValue.getValue();
        if (user.getRole().equals(Role.ROLE_STUDENT) && documentType.equals("_doctype_license")) {
            apiResponse = new ApiResponse(ERROR, "You are not allowed to delete license files!");
        } else {
            if (user.getRole().equals(Role.ROLE_ADMIN) || user.getRole().equals(Role.ROLE_MANAGER) || uri.contains(String.valueOf(hash))) {
                JsonNode fileInfo = fileIOUtility.getFileInfo(uri);
                fileIOUtility.delete(uri);
                actionLogRepo.createPublicLog(submissionRepo.read(submissionId), user, documentType.substring(9).toUpperCase() + " file " + fileInfo.get("name").asText() + " (" + (fileInfo.get("size").asInt() / 1024) + " KB) removed");
            } else {
                apiResponse = new ApiResponse(ERROR, "This is not your file to delete!");
            }
        }
        return apiResponse;
    }

    @RequestMapping(value = "/{submissionId}/{documentType}/archive-file", method = RequestMethod.POST)
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse archiveFile(@WeaverUser User user, @PathVariable Long submissionId, @PathVariable String documentType, @RequestBody Map<String, String> requestData) throws IOException {
        String name = requestData.get("name");
        String oldUri = requestData.get("uri");
        String newUri = oldUri.replace(oldUri.substring(oldUri.lastIndexOf('/') + 1, oldUri.length()), System.currentTimeMillis() + "-archived-" + name);
        fileIOUtility.copy(oldUri, newUri);
        fileIOUtility.delete(oldUri);
        JsonNode fileInfo = fileIOUtility.getFileInfo(newUri);
        actionLogRepo.createPublicLog(submissionRepo.read(submissionId), user, "ARCHIVE - " + documentType + " file " + fileInfo.get("name").asText() + " (" + (fileInfo.get("size").asInt() / 1024) + " KB) archived");
        return new ApiResponse(SUCCESS, newUri);
    }

    @RequestMapping("/{submissionId}/send-advisor-email")
    @PreAuthorize("hasRole('REVIEWER')")
    public ApiResponse sendAdvisorEmail(@WeaverUser User user, @PathVariable Long submissionId) {

        Submission submission = submissionRepo.read(submissionId);

        InputType contactInputType = inputTypeRepo.findByName("INPUT_CONTACT");

        EmailTemplate template = emailTemplateRepo.findByNameAndSystemRequired("SYSTEM Advisor Review Request", true);

        String subject = templateUtility.compileString(template.getSubject(), submission);
        String content = templateUtility.compileTemplate(template, submission);

        // TODO: this needs to only send email to the advisor not any field value that is contact type
        submission.getFieldValuesByInputType(contactInputType).forEach(fv -> {

            SimpleMailMessage smm = new SimpleMailMessage();

            smm.setTo(String.join(",", fv.getContacts()));

            String preferedEmail = user.getSetting("preferedEmail");

            if ("true".equals(user.getSetting("ccEmail"))) {
                smm.setBcc(preferedEmail == null ? user.getEmail() : preferedEmail);
            }

            smm.setSubject(subject);
            smm.setText(content);

            emailSender.send(smm);

        });

        actionLogRepo.createPublicLog(submission, user, "Advisor review email manually generated.");

        return new ApiResponse(SUCCESS);
    }

    // TODO: rework, anonymous endpoint for advisor approval, no user available for action log
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
            actionLogRepo.createAdvisorPublicLog(submission, "Advisor comments : " + message);
        }

        return new ApiResponse(SUCCESS, submission);

    }

    private void processAdvisorAction(String type, Boolean approveStatus, Submission submission) {
        String approveAdvisorMessage;
        if (approveStatus == true) {
            approveAdvisorMessage = "The committee approved the " + type + ".";
        } else {
            approveAdvisorMessage = "The committee rejected the " + type + ".";
        }
        actionLogRepo.createAdvisorPublicLog(submission, approveAdvisorMessage);
    }

    private void processAdvisorStatusClear(String type, Boolean approvalState, Submission submission) {
        String clearAdvisorMessage = "The committee has withdrawn its " + type;
        if (approvalState == true) {
            clearAdvisorMessage += " Approval.";
        } else {
            clearAdvisorMessage += " Rejection.";
        }
        actionLogRepo.createAdvisorPublicLog(submission, clearAdvisorMessage);
    }

    private void processEmailWorkflowRules(User user, Submission submission) {

        SimpleMailMessage smm = new SimpleMailMessage();

        List<EmailWorkflowRule> rules = submission.getOrganization().getAggregateEmailWorkflowRules();

        for (EmailWorkflowRule rule : rules) {

            LOG.debug("Email Workflow Rule " + rule.getId() + " firing for submission " + submission.getId());

            if (rule.getSubmissionStatus().equals(submission.getSubmissionStatus()) && !rule.isDisabled()) {

                // TODO: Not all variables are currently being replaced.
                String subject = templateUtility.compileString(rule.getEmailTemplate().getSubject(), submission);
                String content = templateUtility.compileTemplate(rule.getEmailTemplate(), submission);

                for (String email : rule.getEmailRecipient().getEmails(submission)) {

                    try {
                        LOG.debug("\tSending email to recipient at address " + email);

                        smm.setTo(email);

                        String preferedEmail = user.getSetting("preferedEmail");

                        if ("true".equals(user.getSetting("ccEmail"))) {
                            smm.setBcc(preferedEmail == null ? user.getEmail() : preferedEmail);
                        }

                        smm.setSubject(subject);
                        smm.setText(content);

                        emailSender.send(smm);
                    } catch (MailException me) {
                        LOG.error("Problem sending email: " + me.getMessage());
                    }
                }
            } else {
                LOG.debug("\tRule disabled or of irrelevant status condition.");
            }
        }
    }

}