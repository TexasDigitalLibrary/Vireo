package org.tdl.vireo.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tdl.vireo.model.Action;
import org.tdl.vireo.model.ControlledVocabulary;
import org.tdl.vireo.model.Degree;
import org.tdl.vireo.model.DegreeLevel;
import org.tdl.vireo.model.DocumentType;
import org.tdl.vireo.model.EmailRecipient;
import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.EmailWorkflowRuleByAction;
import org.tdl.vireo.model.EmailWorkflowRuleByStatus;
import org.tdl.vireo.model.Embargo;
import org.tdl.vireo.model.FieldPredicate;
import org.tdl.vireo.model.FieldProfile;
import org.tdl.vireo.model.GraduationMonth;
import org.tdl.vireo.model.InputType;
import org.tdl.vireo.model.Language;
import org.tdl.vireo.model.ManagedConfiguration;
import org.tdl.vireo.model.Note;
import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.OrganizationCategory;
import org.tdl.vireo.model.Sort;
import org.tdl.vireo.model.SubmissionListColumn;
import org.tdl.vireo.model.SubmissionState;
import org.tdl.vireo.model.SubmissionStatus;
import org.tdl.vireo.model.VocabularyWord;
import org.tdl.vireo.model.WorkflowStep;
import org.tdl.vireo.model.formatter.DSpaceMetsFormatter;
import org.tdl.vireo.model.formatter.DSpaceSimpleFormatter;
import org.tdl.vireo.model.formatter.ExcelFormatter;
import org.tdl.vireo.model.formatter.Marc21Formatter;
import org.tdl.vireo.model.formatter.MarcXML21Formatter;
import org.tdl.vireo.model.formatter.ProQuestUmiFormatter;
import org.tdl.vireo.model.repo.AbstractEmailRecipientRepo;
import org.tdl.vireo.model.repo.AbstractPackagerRepo;
import org.tdl.vireo.model.repo.ConfigurationRepo;
import org.tdl.vireo.model.repo.ControlledVocabularyRepo;
import org.tdl.vireo.model.repo.DegreeLevelRepo;
import org.tdl.vireo.model.repo.DegreeRepo;
import org.tdl.vireo.model.repo.DocumentTypeRepo;
import org.tdl.vireo.model.repo.EmailTemplateRepo;
import org.tdl.vireo.model.repo.EmailWorkflowRuleByActionRepo;
import org.tdl.vireo.model.repo.EmailWorkflowRuleRepo;
import org.tdl.vireo.model.repo.EmbargoRepo;
import org.tdl.vireo.model.repo.FieldPredicateRepo;
import org.tdl.vireo.model.repo.FieldProfileRepo;
import org.tdl.vireo.model.repo.GraduationMonthRepo;
import org.tdl.vireo.model.repo.InputTypeRepo;
import org.tdl.vireo.model.repo.LanguageRepo;
import org.tdl.vireo.model.repo.NoteRepo;
import org.tdl.vireo.model.repo.OrganizationCategoryRepo;
import org.tdl.vireo.model.repo.OrganizationRepo;
import org.tdl.vireo.model.repo.SubmissionListColumnRepo;
import org.tdl.vireo.model.repo.SubmissionStatusRepo;
import org.tdl.vireo.model.repo.VocabularyWordRepo;
import org.tdl.vireo.model.repo.WorkflowStepRepo;

/**
 * This class is to load and persist system data from resources.
 */
@Service
public class SystemDataLoader {

    private final Logger logger = LoggerFactory.getLogger(SystemDataLoader.class);

    private final Pattern SUBJECT_PATTERN = Pattern.compile("\\s*Subject:(.*)[\\n\\r]{1}");

    @Value("${app.dataLoader.initialize:true}")
    private Boolean doInitialize;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ResourcePatternResolver resourcePatternResolver;

    @Autowired
    private InputTypeRepo inputTypeRepo;

    @Autowired
    private EmailTemplateRepo emailTemplateRepo;

    @Autowired
    private AbstractEmailRecipientRepo abstractEmailRecipientRepo;

    @Autowired
    private EmbargoRepo embargoRepo;

    @Autowired
    private OrganizationRepo organizationRepo;

    @Autowired
    private OrganizationCategoryRepo organizationCategoryRepo;

    @Autowired
    private WorkflowStepRepo workflowStepRepo;

    @Autowired
    private NoteRepo noteRepo;

    @Autowired
    private FieldProfileRepo fieldProfileRepo;

    @Autowired
    private ConfigurationRepo configurationRepo;

    @Autowired
    private FieldPredicateRepo fieldPredicateRepo;

    @Autowired
    private ControlledVocabularyRepo controlledVocabularyRepo;

    @Autowired
    private VocabularyWordRepo vocabularyRepo;

    @Autowired
    private LanguageRepo languageRepo;

    @Autowired
    private DegreeRepo degreeRepo;

    @Autowired
    private DegreeLevelRepo degreeLevelRepo;

    @Autowired
    private GraduationMonthRepo graduationMonthRepo;

    @Autowired
    private DocumentTypeRepo documentTypeRepo;

    @Autowired
    private EmailWorkflowRuleRepo emailWorkflowRuleRepo;

    @Autowired
    private EmailWorkflowRuleByActionRepo emailWorkflowRuleByActionRepo;

    @Autowired
    private SubmissionStatusRepo submissionStatusRepo;

    @Autowired
    private SubmissionListColumnRepo submissionListColumnRepo;

    @Autowired
    private AbstractPackagerRepo abstractPackagerRepo;

    @Autowired
    private ProquestCodesService proquesteCodesService;

    @Transactional(rollbackFor = IOException.class)
    public void loadSystemData() throws IOException {
        if (!doInitialize) {
            logger.info("Bypassing data load based on app configuration");
        } else {
            logger.info("Loading system languages");
            loadLanguages();

            logger.info("Loading system input types");
            loadInputTypes();

            logger.info("Loading system email templates");
            loadEmailTemplates();

            logger.info("Loading system degree levels");
            loadDegreeLevels();

            logger.info("Loading system degrees");
            loadDegrees();

            logger.info("Loading system graduation months");
            loadGraduationMonths();

            logger.info("Loading system embargos");
            loadEmbargos();

            logger.info("Loading system submission statuses");
            loadSubmissionStatuses();

            logger.info("Loading system organization catagories");
            loadOrganizationCategories();

            logger.info("Loading system document types");
            loadDocumentTypes();

            logger.info("Loading system organization");
            loadOrganization();

            logger.info("Loading system controlled vocabularies");
            loadControlledVocabularies();

            logger.info("Loading system Proquest subject codes controlled vocabulary");
            loadProquestSubjectCodesControlledVocabulary();

            logger.info("Loading system submission list columns");
            loadSubmissionListColumns();

            logger.info("Loading system packagers");
            loadPackagers();

            logger.info("Finished loading system data");
        }
    }

    private void loadLanguages() throws IOException {
        List<Language> languages = objectMapper.readValue(getInputStreamFromResource("classpath:/languages/SYSTEM_Languages.json"), new TypeReference<List<Language>>() {});

        for (Language language : languages) {
            Language persistedLanguage = languageRepo.findByName(language.getName());

            if (persistedLanguage == null) {
                persistedLanguage = languageRepo.create(language.getName());
            } else {
                persistedLanguage.setName(language.getName());
                persistedLanguage = languageRepo.save(persistedLanguage);
            }
        }
    }

    private void loadInputTypes() throws IOException {
        List<InputType> inputTypes = objectMapper.readValue(getInputStreamFromResource("classpath:/input_types/SYSTEM_Input_Types.json"), new TypeReference<List<InputType>>() {});

        for (InputType inputType : inputTypes) {
            InputType persistedInputType = inputTypeRepo.findByName(inputType.getName());

            if (persistedInputType == null) {
                persistedInputType = inputTypeRepo.create(inputType);
            } else {
                persistedInputType.setName(inputType.getName());
                persistedInputType = inputTypeRepo.save(persistedInputType);
            }
        }
    }

    private void loadEmailTemplates() throws IOException {
        for (String name : getAllSystemEmailTemplateNames()) {

            // try to see if it already exists in the DB
            EmailTemplate dbTemplate = emailTemplateRepo.findByNameAndSystemRequired(name, true);

            // create template or upgrade the old one
            if (dbTemplate == null) {
                dbTemplate = loadSystemEmailTemplate(name);
                logger.info("New System Email template being installed [" + dbTemplate.getName() + "]");
            } else {
                EmailTemplate loadedTemplate = loadSystemEmailTemplate(name);

                // if the template in the DB doesn't match in content with the
                // one loaded from .email file
                if (!(dbTemplate.getMessage().equals(loadedTemplate.getMessage())) || !(dbTemplate.getSubject().equals(loadedTemplate.getSubject()))) {

                    EmailTemplate possibleCustomTemplate = emailTemplateRepo.findByNameAndSystemRequired(name, false);

                    // if this System template already has a custom template
                    // (meaning one named the same but that is
                    // !systemRequired)
                    if (possibleCustomTemplate != null) {

                        // a custom version of this System email template
                        // already exists, it's safe to override dbTemplate's
                        // data and save
                        dbTemplate.setMessage(loadedTemplate.getMessage());
                        dbTemplate.setSubject(loadedTemplate.getSubject());
                        dbTemplate.setSystemRequired(true);

                        logger.info("Upgrading Old System Email Template for [" + dbTemplate.getName() + "]");

                        emailTemplateRepo.save(dbTemplate);
                    }
                    // there is no custom one yet, we need to make the
                    // dbTemplate !systemRequired and the save loadedTemplate
                    else {
                        logger.info("Upgrading Old System Email Template and creating custom version for [" + dbTemplate.getName() + "]");
                        dbTemplate.setSystemRequired(false);
                        emailTemplateRepo.save(dbTemplate);
                        emailTemplateRepo.save(loadedTemplate);
                    }
                }
            }
        }
    }

    private List<String> getAllSystemEmailTemplateNames() throws IOException {
        List<String> names = new ArrayList<String>();
        for (Resource resource : resourcePatternResolver.getResources("classpath:/emails/*.email")) {
            String fileName = resource.getFilename();
            String templateName = decodeTemplateName(fileName);
            names.add(templateName);
        }

        return names;
    }

    private String decodeTemplateName(String path) {
        if (path.endsWith(".email")) {
            path = path.substring(0, path.length() - ".email".length());
        }
        return path.replaceAll("_", " ");
    }

    private EmailTemplate loadSystemEmailTemplate(String name) throws IOException {
        Resource resource = resourcePatternResolver.getResource("classpath:/emails/" + encodeTemplateName(name));
        String data = IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8);

        // Remove any comment lines
        data = data.replaceAll("\\s*#.*[\\n\\r]{1}", "");

        // Extract the subject
        Matcher subjectMatcher = SUBJECT_PATTERN.matcher(data);
        if (!subjectMatcher.find()) {
            throw new IllegalStateException("Unable to identify the template's subject.");
        }
        String subject = subjectMatcher.group(1).trim();

        // Trim the subject leaving just the body.
        int index = data.indexOf("\n");
        if (index < 0) {
            index = data.indexOf("\r");
        }

        String message = data.substring(index);

        if (subject == null || subject.length() == 0) {
            throw new IllegalStateException("Unable to identify the template's subject.");
        }

        if (message == null || message.length() == 0) {
            throw new IllegalStateException("Unable to identify the template's message.");
        }

        EmailTemplate template = emailTemplateRepo.findByNameAndSystemRequired(name, true);

        if (template == null) {
            template = emailTemplateRepo.create(name, subject, message);
            template.setSystemRequired(true);
        } else {
            template.setSubject(subject);
            template.setMessage(message);
        }

        return emailTemplateRepo.save(template);
    }

    private static String encodeTemplateName(String name) {
        return name.replaceAll(" ", "_") + ".email";
    }

    private void loadDegreeLevels() throws IOException {
        List<DegreeLevel> degreeLevels = objectMapper.readValue(getInputStreamFromResource("classpath:/degree_levels/SYSTEM_Degree_Levels.json"), new TypeReference<List<DegreeLevel>>() {});

        for (DegreeLevel degreeLevel : degreeLevels) {
            DegreeLevel dbDegreeLevel = degreeLevelRepo.findByName(degreeLevel.getName());

            if (dbDegreeLevel == null) {
                dbDegreeLevel = degreeLevelRepo.create(degreeLevel.getName());
            }

        }
    }

    private void loadDegrees() throws IOException {
        List<Degree> degrees = objectMapper.readValue(getInputStreamFromResource("classpath:/degrees/SYSTEM_Degrees.json"), new TypeReference<List<Degree>>() {});

        for (Degree degree : degrees) {

            DegreeLevel degreeLevel = degreeLevelRepo.findByName(degree.getLevel().getName());

            if (degreeLevel == null) {
                degreeLevel = degreeLevelRepo.create(degree.getLevel().getName());
            } else {
                degreeLevel.setName(degree.getLevel().getName());
                degreeLevel = degreeLevelRepo.save(degreeLevel);
            }

            Degree dbDegree = degreeRepo.findByNameAndLevel(degree.getName(), degreeLevel);

            if (dbDegree == null) {
                dbDegree = degreeRepo.create(degree.getName(), degreeLevel);
            } else {
                dbDegree.setName(degree.getName());
                dbDegree.setLevel(degreeLevel);
                dbDegree = degreeRepo.save(dbDegree);
            }

        }
    }

    private void loadGraduationMonths() throws IOException {
        List<GraduationMonth> graduationMonths = objectMapper.readValue(getInputStreamFromResource("classpath:/graduation_months/SYSTEM_Graduation_Months.json"), new TypeReference<List<GraduationMonth>>() {});

        for (GraduationMonth graduationMonth : graduationMonths) {
            GraduationMonth persistedGraduationMonth = graduationMonthRepo.findByMonth(graduationMonth.getMonth());

            if (persistedGraduationMonth == null) {
                persistedGraduationMonth = graduationMonthRepo.create(graduationMonth.getMonth());
            }

        }
    }

    private void loadEmbargos() throws IOException {
        List<Embargo> embargoDefinitions = objectMapper.readValue(getInputStreamFromResource("classpath:/embargos/SYSTEM_Embargo_Definitions.json"), new TypeReference<List<Embargo>>() {});

        for (Embargo embargoDefinition : embargoDefinitions) {
            Embargo dbEmbargo = embargoRepo.findByNameAndGuarantorAndSystemRequired(embargoDefinition.getName(), embargoDefinition.getGuarantor(), true);

            if (dbEmbargo == null) {
                dbEmbargo = embargoRepo.create(embargoDefinition.getName(), embargoDefinition.getDescription(), embargoDefinition.getDuration(), embargoDefinition.getGuarantor(), embargoDefinition.isActive());
                dbEmbargo.setSystemRequired(true);
                embargoRepo.save(dbEmbargo);
            } else {
                dbEmbargo.setDescription(embargoDefinition.getDescription());
                dbEmbargo.setDuration(embargoDefinition.getDuration());
                dbEmbargo.setGuarantor(embargoDefinition.getGuarantor());
                dbEmbargo.isActive(embargoDefinition.isActive());
                dbEmbargo.setSystemRequired(embargoDefinition.getSystemRequired());
                embargoRepo.save(dbEmbargo);
            }
        }
    }

    private void loadSubmissionStatuses() throws IOException {
        // read System Submission Status as JsonNode
        JsonNode systemSubmissionStatus = objectMapper.readTree(getInputStreamFromResource("classpath:/submission_statuses/SYSTEM_Submission_Status.json"));

        // check to see if the SubmissionStatus exists
        SubmissionStatus newSubmissionStatus = submissionStatusRepo.findByName(systemSubmissionStatus.get("name").asText());

        // recursively find or create new SubmissionStatus if not already exists
        if (newSubmissionStatus == null) {
            recursivelyFindOrCreateSubmissionStatus(systemSubmissionStatus);
        }
    }

    private SubmissionStatus recursivelyFindOrCreateSubmissionStatus(JsonNode submissionStatus) {
        // check to see if the SubmissionStatus exists
        SubmissionStatus newSubmissionStatus = submissionStatusRepo.findByName(submissionStatus.get("name").asText());

        // create new SubmissionStatus if not already exists
        if (newSubmissionStatus == null) {
            newSubmissionStatus = createSubmissionStatus(submissionStatus);
        }

        // temporary list of transition SubmissionState
        List<SubmissionStatus> transitionStatuses = new ArrayList<SubmissionStatus>();

        ((ArrayNode) submissionStatus.get("transitionSubmissionStatuses")).forEach(transitionStatus -> {

            // check to see if the Transistion SubmissionStatus exists
            SubmissionStatus newTransitionStatus = submissionStatusRepo.findByName(transitionStatus.get("name").asText());

            // create new Transistion SubmissionStatus if not already exists
            if (newTransitionStatus == null) {
                newTransitionStatus = recursivelyFindOrCreateSubmissionStatus(transitionStatus);
            }

            transitionStatuses.add(newTransitionStatus);

        });

        newSubmissionStatus.setTransitionSubmissionStatuses(transitionStatuses);

        return submissionStatusRepo.save(newSubmissionStatus);
    }

    private SubmissionStatus createSubmissionStatus(JsonNode submissionStatus) {
        JsonNode isActive = submissionStatus.get("isActive");
        return submissionStatusRepo.create(
            submissionStatus.get("name").asText(),
            submissionStatus.get("isArchived").asBoolean(),
            submissionStatus.get("isPublishable").asBoolean(),
            submissionStatus.get("isDeletable").asBoolean(),
            submissionStatus.get("isEditableByReviewer").asBoolean(),
            submissionStatus.get("isEditableByStudent").asBoolean(),
            isActive.isNull() ? null : isActive.asBoolean(),
            SubmissionState.from(submissionStatus.get("submissionState").asInt())
        );
    }

    private void loadOrganizationCategories() throws IOException {
        List<OrganizationCategory> organizationCategories = objectMapper.readValue(getInputStreamFromResource("classpath:/organization_categories/SYSTEM_Organizaiton_Categories.json"), new TypeReference<List<OrganizationCategory>>() {});

        for (OrganizationCategory organizationCategory : organizationCategories) {
            OrganizationCategory dbOrganizationCategory = organizationCategoryRepo.findByName(organizationCategory.getName());

            if (dbOrganizationCategory == null) {
                dbOrganizationCategory = organizationCategoryRepo.create(organizationCategory.getName());
            }
        }
    }

     private void loadDocumentTypes() throws IOException {
        List<DocumentType> documentTypes = objectMapper.readValue(getInputStreamFromResource("classpath:/document_types/SYSTEM_Document_Types.json"), new TypeReference<List<DocumentType>>() {});

        for (DocumentType documentType : documentTypes) {

            FieldPredicate fieldPredicate = documentType.getFieldPredicate();

            FieldPredicate dbFieldPredicate = fieldPredicateRepo.findByValue(fieldPredicate.getValue());
            if (dbFieldPredicate == null) {
                dbFieldPredicate = fieldPredicateRepo.create(fieldPredicate.getValue(), Boolean.valueOf(true));
            } else {
                dbFieldPredicate.setValue(fieldPredicate.getValue());
                dbFieldPredicate.setDocumentTypePredicate(fieldPredicate.getDocumentTypePredicate());
                dbFieldPredicate = fieldPredicateRepo.save(dbFieldPredicate);
            }

            DocumentType dbDocumentType = documentTypeRepo.findByNameAndFieldPredicate(documentType.getName(), dbFieldPredicate);

            if (dbDocumentType == null) {
                dbDocumentType = documentTypeRepo.create(documentType.getName(), dbFieldPredicate);
            } else {
                dbDocumentType.setName(documentType.getName());
                dbDocumentType.setFieldPredicate(dbFieldPredicate);
                documentTypeRepo.save(dbDocumentType);
            }
        }
    }

    private void loadOrganization() throws IOException {
        // read and map json to Organization
        Organization systemOrganization = objectMapper.readValue(getInputStreamFromResource("classpath:/organization/SYSTEM_Organization_Definition.json"), Organization.class);

        // check to see if organization category exists
        OrganizationCategory category = organizationCategoryRepo.findByName(systemOrganization.getCategory().getName());

        // create organization category if does not already exists
        if (category == null) {
            category = organizationCategoryRepo.create(systemOrganization.getCategory().getName());
        }

        // check to see if organization with organization category exists
        Organization organization = organizationRepo.findByNameAndCategory(systemOrganization.getName(), category);

        // create new organization if not already exists
        if (organization == null) {
            organization = organizationRepo.create(systemOrganization.getName(), category);
            organization.setAcceptsSubmissions(systemOrganization.getAcceptsSubmissions());
        }

        processWorkflowSteps(organization, systemOrganization);

        processEmailWorflowRules(organization, systemOrganization);

        organizationRepo.save(organization);
    }

    private void processWorkflowSteps(Organization organization, Organization systemOrganization) {

        for (WorkflowStep workflowStep : systemOrganization.getOriginalWorkflowSteps()) {

            // check to see if the WorkflowStep exists
            WorkflowStep newWorkflowStep = workflowStepRepo.findByNameAndOriginatingOrganization(workflowStep.getName(), organization);

            // create new workflow step if not already exists
            if (newWorkflowStep == null) {
                organization = organizationRepo.findById(organization.getId()).get();
                newWorkflowStep = workflowStepRepo.create(workflowStep.getName(), organization);
            }

            List<FieldProfile> originalFieldProfiles = new ArrayList<>(workflowStep.getOriginalFieldProfiles());

            for (FieldProfile fieldProfile : originalFieldProfiles) {
                // check to see if the FieldPredicate exists
                FieldPredicate fieldPredicate = fieldPredicateRepo.findByValue(fieldProfile.getFieldPredicate().getValue());

                // create new FieldPredicate if not already exists
                if (fieldPredicate == null) {
                    fieldPredicate = fieldPredicateRepo.create(fieldProfile.getFieldPredicate().getValue(), fieldProfile.getFieldPredicate().getDocumentTypePredicate());
                }

                // check to see if the InputType exists
                InputType inputType = inputTypeRepo.findByName(fieldProfile.getInputType().getName());

                if (inputType == null) {
                    inputType = inputTypeRepo.create(fieldProfile.getInputType().getName());
                }

                // check to see if the ControlledVocabulary exists
                ControlledVocabulary controlledVocabulary = fieldProfile.getControlledVocabulary();

                // create new ControlledVocabulary if not already exists
                if (controlledVocabulary != null) {
                    ControlledVocabulary persistedControlledVocabulary = controlledVocabularyRepo.findByName(controlledVocabulary.getName());
                    if (persistedControlledVocabulary == null) {
                        persistedControlledVocabulary = controlledVocabularyRepo.create(controlledVocabulary.getName(), controlledVocabulary.getIsEntityProperty());
                    }
                    fieldProfile.setControlledVocabulary(persistedControlledVocabulary);
                }

                // check to see if the ManagedConfiguration exists
                ManagedConfiguration managedConfiguration = fieldProfile.getMappedShibAttribute();

                // create new ManagedConfiguration if not already exists
                if (managedConfiguration != null) {
                    ManagedConfiguration persistedManagedConfiguration = configurationRepo.findByNameAndType(managedConfiguration.getName(), managedConfiguration.getType());
                    if (persistedManagedConfiguration == null) {
                        persistedManagedConfiguration = configurationRepo.create(managedConfiguration);
                    }
                    fieldProfile.setMappedShibAttribute(persistedManagedConfiguration);
                }

                // check to see if the FieldProfile exists
                FieldProfile newFieldProfile = fieldProfileRepo.findByFieldPredicateAndOriginatingWorkflowStep(fieldPredicate, newWorkflowStep);

                // create new FieldProfile if not already exists
                if (newFieldProfile == null) {
                    newWorkflowStep = workflowStepRepo.findById(newWorkflowStep.getId()).get();
                    newFieldProfile = fieldProfileRepo.create(newWorkflowStep, fieldPredicate, inputType, fieldProfile.getUsage(), fieldProfile.getHelp(), fieldProfile.getGloss(), fieldProfile.getRepeatable(), fieldProfile.getOverrideable(), fieldProfile.getEnabled(), fieldProfile.getOptional(), fieldProfile.getHidden(), fieldProfile.getFlagged(), fieldProfile.getLogged(), fieldProfile.getControlledVocabulary(), fieldProfile.getMappedShibAttribute(), fieldProfile.getDefaultValue());
                }

                newWorkflowStep.addOriginalFieldProfile(newFieldProfile);

                newWorkflowStep = workflowStepRepo.save(newWorkflowStep);
            }

            // temporary list of Note
            List<Note> notes = new ArrayList<Note>();

            for (Note note : workflowStep.getOriginalNotes()) {

                // check to see if the Note exists
                Note newNote = noteRepo.findByNameAndOriginatingWorkflowStep(note.getName(), newWorkflowStep);

                // create new Note if not already exists
                if (newNote == null) {
                    newNote = noteRepo.create(newWorkflowStep, note.getName(), note.getText());
                    newWorkflowStep = workflowStepRepo.findById(newWorkflowStep.getId()).get();
                }

                notes.add(newNote);

            }

            newWorkflowStep.setOriginalNotes(notes);

            newWorkflowStep.setInstructions(workflowStep.getInstructions());

            newWorkflowStep = workflowStepRepo.save(newWorkflowStep);
        }
    }

    /**
     * @param organization
     * @param systemOrganization
     */
    private void processEmailWorflowRules(Organization organization, Organization systemOrganization) {
        processEmailWorkflowRulesByStatus(organization, systemOrganization);
        processEmailWorkflowRulesByAction(organization, systemOrganization);
    }

    private void processEmailWorkflowRulesByStatus(Organization organization, Organization systemOrganization) {
        List<EmailWorkflowRuleByStatus> emailWorkflowRules = organization.getEmailWorkflowRules();

        for (EmailWorkflowRuleByStatus emailWorkflowRule : systemOrganization.getEmailWorkflowRules()) {

            // check to see if the SubmissionStatus exists
            SubmissionStatus newSubmissionStatus = submissionStatusRepo.findByName(emailWorkflowRule.getSubmissionStatus().getName());

            // create new SubmissionStatus if not already exists
            if (newSubmissionStatus == null) {
                throw new RuntimeException("No submission status found with name " + emailWorkflowRule.getSubmissionStatus().getName());
            }

            // check to see if the EmailTemplate exists
            EmailTemplate newEmailTemplate = emailTemplateRepo.findByNameAndSystemRequired(emailWorkflowRule.getEmailTemplate().getName(), emailWorkflowRule.getEmailTemplate().getSystemRequired());

            // create new EmailTemplate if not already exists
            if (newEmailTemplate == null) {
                newEmailTemplate = emailTemplateRepo.create(emailWorkflowRule.getEmailTemplate().getName(), emailWorkflowRule.getEmailTemplate().getSubject(), emailWorkflowRule.getEmailTemplate().getMessage());
            }

            if (emailWorkflowRule.getEmailRecipient() == null) {

                // NOTE: this is a mapping directly to the SYSTEM_Organization_Definition.json for `emailWorkflowRules` property.

                if (newEmailTemplate.getName().equals("SYSTEM Advisor Review Request")) {
                    organization.getAggregateWorkflowSteps().forEach(awfs -> {
                        awfs.getAggregateFieldProfiles().forEach(afp -> {
                            if (afp.getFieldPredicate().getValue().equals("dc.contributor.advisor")) {
                                EmailRecipient recipient = abstractEmailRecipientRepo.createContactRecipient(afp.getGloss(), afp.getFieldPredicate());
                                emailWorkflowRule.setEmailRecipient(recipient);
                            }
                        });
                    });
                }

                if (newEmailTemplate.getName().equals("SYSTEM Initial Submission")) {
                    EmailRecipient recipient = abstractEmailRecipientRepo.createOrganizationRecipient(organization);
                    emailWorkflowRule.setEmailRecipient(recipient);
                }

            }

            // check to see if the EmailWorkflowRule exists
            EmailWorkflowRuleByStatus existingEmailWorkflowRule = emailWorkflowRuleRepo.findBySubmissionStatusAndEmailRecipientAndEmailTemplate(newSubmissionStatus, emailWorkflowRule.getEmailRecipient(), newEmailTemplate);

            if (existingEmailWorkflowRule == null) {
                emailWorkflowRules.add(emailWorkflowRuleRepo.create(newSubmissionStatus, emailWorkflowRule.getEmailRecipient(), newEmailTemplate, emailWorkflowRule.isSystem()));
            }

        }

        organization.setEmailWorkflowRules(emailWorkflowRules);
    }

    // TODO: fix duplicate method with different typing for repo and entity
    // NOTE: applies same dependent persistent email recipient creation in both
    private void processEmailWorkflowRulesByAction(Organization organization, Organization systemOrganization) {
        List<EmailWorkflowRuleByAction> emailWorkflowRulesByAction = organization.getEmailWorkflowRulesByAction();

        EmailRecipient submitterRecipient = abstractEmailRecipientRepo.createSubmitterRecipient();

        for (EmailWorkflowRuleByAction emailWorkflowRuleByAction : systemOrganization.getEmailWorkflowRulesByAction()) {

            // check to see if the action is defined
            Action action = emailWorkflowRuleByAction.getAction();
            try {
                logger.info("Checking if action is defined: {}", action.name());
            } catch (Exception e) {
                throw new RuntimeException(String.format("Action is required on EmailWorfklowRuleByAction %s", emailWorkflowRuleByAction), e);
            }

            // check to see if the EmailTemplate exists
            // see ../model/EmailTemplate.java => { "name", "systemRequired" }
            EmailTemplate newEmailTemplate = emailTemplateRepo.findByNameAndSystemRequired(emailWorkflowRuleByAction.getEmailTemplate().getName(), emailWorkflowRuleByAction.getEmailTemplate().getSystemRequired());

            // create new EmailTemplate if not already exists
            if (newEmailTemplate == null) {
                newEmailTemplate = emailTemplateRepo.create(emailWorkflowRuleByAction.getEmailTemplate().getName(), emailWorkflowRuleByAction.getEmailTemplate().getSubject(), emailWorkflowRuleByAction.getEmailTemplate().getMessage());
            }

            if (emailWorkflowRuleByAction.getEmailRecipient() == null) {

                // NOTE: this is a mapping directly to the SYSTEM_Organization_Definition.json for `emailWorkflowRulesByAction` property.

                switch (newEmailTemplate.getName()) {
                    case "SYSTEM Notify Assignee of Student Comment":
                    case "SYSTEM Notify Assignee of Advisor Comment":
                    case "SYSTEM Notify Assignee of Advisor Approved Submission":
                    case "SYSTEM Notify Assignee of Advisor Cleared Submission Approval":
                    case "SYSTEM Notify Assignee of Advisor Approved Embargo":
                    case "SYSTEM Notify Assignee of Advisor Cleared Embargo Approval": {
                        EmailRecipient recipient = abstractEmailRecipientRepo.createOrganizationRecipient(organization);
                        emailWorkflowRuleByAction.setEmailRecipient(recipient);
                    } break;
                    case "SYSTEM Notify Advisor of Student Comment": {
                        organization.getAggregateWorkflowSteps().forEach(awfs -> {
                            awfs.getAggregateFieldProfiles().forEach(afp -> {
                                if (afp.getFieldPredicate().getValue().equals("dc.contributor.advisor")) {
                                    EmailRecipient recipient = abstractEmailRecipientRepo.createContactRecipient(afp.getGloss(), afp.getFieldPredicate());
                                    emailWorkflowRuleByAction.setEmailRecipient(recipient);
                                }
                            });
                        });
                    } break;
                    case "SYSTEM Notify Submitter of Advisor Comment":
                    case "SYSTEM Notify Submitter of Advisor Approved Submission":
                    case "SYSTEM Notify Submitter of Advisor Cleared Submission Approval":
                    case "SYSTEM Notify Submitter of Advisor Approved Embargo":
                    case "SYSTEM Notify Submitter of Advisor Cleared Embargo Approval":
                    default:
                        emailWorkflowRuleByAction.setEmailRecipient(submitterRecipient);
                        break;
                }
            }

            // check to see if the EmailWorkflowRule exists
            EmailWorkflowRuleByAction existingEmailWorkflowRule = emailWorkflowRuleByActionRepo.findByActionAndEmailRecipientAndEmailTemplate(action, emailWorkflowRuleByAction.getEmailRecipient(), newEmailTemplate);

            if (existingEmailWorkflowRule == null) {
                emailWorkflowRulesByAction.add(emailWorkflowRuleByActionRepo.create(action, emailWorkflowRuleByAction.getEmailRecipient(), newEmailTemplate, emailWorkflowRuleByAction.isSystem()));
            }

        }

        organization.setEmailWorkflowRulesByAction(emailWorkflowRulesByAction);
    }

    private void loadControlledVocabularies() throws IOException {
        for (Resource vocabularyResourceJson : resourcePatternResolver.getResources("classpath:/controlled_vocabularies/*.json")) {
            ControlledVocabulary cv = objectMapper.readValue(vocabularyResourceJson.getInputStream(), ControlledVocabulary.class);

            // check to see if Controlled Vocabulary exists, and if so, merge up with it
            ControlledVocabulary persistedCV = controlledVocabularyRepo.findByName(cv.getName());

            if (persistedCV == null) {
                persistedCV = controlledVocabularyRepo.create(cv.getName());
            }

            for (VocabularyWord vw : cv.getDictionary()) {

                VocabularyWord persistedVW = vocabularyRepo.findByNameAndControlledVocabulary(vw.getName(), persistedCV);

                if (persistedVW == null) {
                    persistedVW = vocabularyRepo.create(persistedCV, vw.getName(), vw.getDefinition(), vw.getIdentifier(), vw.getContacts());
                    persistedCV = controlledVocabularyRepo.findByName(cv.getName());
                } else {
                    persistedVW.setDefinition(vw.getDefinition());
                    persistedVW.setIdentifier(vw.getIdentifier());
                    persistedVW.setContacts(vw.getContacts());
                    persistedVW = vocabularyRepo.save(persistedVW);
                }
            }
        }
    }

    private void loadProquestSubjectCodesControlledVocabulary() {
        Map<String, String> subjectCodes = proquesteCodesService.getCodes("subjects");

        // check to see if Controlled Vocabulary exists, and if so, merge up with it
        ControlledVocabulary persistedCV = controlledVocabularyRepo.findByName("Subjects");

        if (persistedCV == null) {
            persistedCV = controlledVocabularyRepo.create("Subjects");
        }

        for (Map.Entry<String, String> entry : subjectCodes.entrySet()) {
            String code = entry.getKey();
            String description = entry.getValue();

            VocabularyWord persistedVW = vocabularyRepo.findByNameAndControlledVocabulary(description, persistedCV);

            if (persistedVW == null) {
                persistedVW = vocabularyRepo.create(persistedCV, description, "", code, new ArrayList<String>());
                persistedCV = controlledVocabularyRepo.findByName("Subjects");
            } else {
                persistedVW.setDefinition("");
                persistedVW.setIdentifier(code);
                persistedVW = vocabularyRepo.save(persistedVW);
            }
        }
    }

    private void loadSubmissionListColumns() throws IOException {
        List<SubmissionListColumn> submissionListColumns = objectMapper.readValue(getInputStreamFromResource("classpath:/submission_list_columns/SYSTEM_Default_Submission_List_Columns.json"), new TypeReference<List<SubmissionListColumn>>() {});

        for (SubmissionListColumn submissionListColumn : submissionListColumns) {
            SubmissionListColumn dbSubmissionListColumn = submissionListColumnRepo.findByTitle(submissionListColumn.getTitle());

            // check to see if the InputType exists
            InputType inputType = inputTypeRepo.findByName(submissionListColumn.getInputType().getName());

            if (inputType == null) {
                inputType = inputTypeRepo.create(submissionListColumn.getInputType().getName());
            } else {
                inputType.setName(submissionListColumn.getInputType().getName());
                inputType = inputTypeRepo.save(inputType);
            }

            if (dbSubmissionListColumn == null) {
                if (submissionListColumn.getPredicate() != null) {
                    submissionListColumnRepo.create(submissionListColumn.getTitle(), submissionListColumn.getSort(), submissionListColumn.getPredicate(), inputType);
                } else {
                    submissionListColumnRepo.create(submissionListColumn.getTitle(), submissionListColumn.getSort(), submissionListColumn.getValuePath(), inputType);
                }
            } else {
                dbSubmissionListColumn.setSort(submissionListColumn.getSort());
                if (submissionListColumn.getPredicate() != null) {
                    dbSubmissionListColumn.setPredicate(submissionListColumn.getPredicate());
                }
                dbSubmissionListColumn.setValuePath(submissionListColumn.getValuePath());
                submissionListColumnRepo.save(dbSubmissionListColumn);
            }
        }

        String[] defaultSubmissionListColumnTitles = objectMapper.readValue(getInputStreamFromResource("classpath:/submission_list_columns/SYSTEM_Default_Submission_List_Column_Titles.json"), new TypeReference<String[]>() { });
        int count = 0;
        for (String defaultTitle : defaultSubmissionListColumnTitles) {
            SubmissionListColumn dbSubmissionListColumn = submissionListColumnRepo.findByTitle(defaultTitle);
            if (dbSubmissionListColumn != null) {
                if (dbSubmissionListColumn.getSort() != Sort.NONE) {
                    logger.warn("Updating sort order for default submission list column with title " + defaultTitle);
                    dbSubmissionListColumn.setSortOrder(++count);
                    submissionListColumnRepo.update(dbSubmissionListColumn);
                }
            } else {
                logger.warn("Unable to find submission list column with title " + defaultTitle);
            }
        }
    }

    private void loadPackagers() {
        if (abstractPackagerRepo.findByName("DSpaceMETS") == null) {
            abstractPackagerRepo.createDSpaceMetsPackager("DSpaceMETS", new DSpaceMetsFormatter());
        }
        if (abstractPackagerRepo.findByName("DSpaceSimple") == null) {
            abstractPackagerRepo.createDSpaceSimplePackager("DSpaceSimple", new DSpaceSimpleFormatter());
        }
        if (abstractPackagerRepo.findByName("ProQuest") == null) {
            abstractPackagerRepo.createProQuestUmiPackager("ProQuest", new ProQuestUmiFormatter());
        }
        if (abstractPackagerRepo.findByName("Excel") == null) {
            abstractPackagerRepo.createExcelPackager("Excel", new ExcelFormatter());
        }
        if (abstractPackagerRepo.findByName("MarcXML21") == null) {
            abstractPackagerRepo.createMARC21XMLPackager("MarcXML21", new MarcXML21Formatter());
        }
        if (abstractPackagerRepo.findByName("Marc21") == null) {
            abstractPackagerRepo.createMARC21Packager("Marc21", new Marc21Formatter());
        }
    }

    private InputStream getInputStreamFromResource(String resourcePath) throws IOException {
        return resourcePatternResolver.getResource(resourcePath).getInputStream();
    }

}
