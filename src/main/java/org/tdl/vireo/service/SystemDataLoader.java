package org.tdl.vireo.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.tdl.vireo.model.ControlledVocabulary;
import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.EmailWorkflowRule;
import org.tdl.vireo.model.Embargo;
import org.tdl.vireo.model.FieldGloss;
import org.tdl.vireo.model.FieldPredicate;
import org.tdl.vireo.model.FieldProfile;
import org.tdl.vireo.model.Language;
import org.tdl.vireo.model.Note;
import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.OrganizationCategory;
import org.tdl.vireo.model.SubmissionState;
import org.tdl.vireo.model.Workflow;
import org.tdl.vireo.model.WorkflowStep;
import org.tdl.vireo.model.repo.ControlledVocabularyRepo;
import org.tdl.vireo.model.repo.EmailTemplateRepo;
import org.tdl.vireo.model.repo.EmailWorkflowRuleRepo;
import org.tdl.vireo.model.repo.EmbargoRepo;
import org.tdl.vireo.model.repo.FieldGlossRepo;
import org.tdl.vireo.model.repo.FieldPredicateRepo;
import org.tdl.vireo.model.repo.FieldProfileRepo;
import org.tdl.vireo.model.repo.LanguageRepo;
import org.tdl.vireo.model.repo.NoteRepo;
import org.tdl.vireo.model.repo.OrganizationCategoryRepo;
import org.tdl.vireo.model.repo.OrganizationRepo;
import org.tdl.vireo.model.repo.SubmissionStateRepo;
import org.tdl.vireo.model.repo.WorkflowRepo;
import org.tdl.vireo.model.repo.WorkflowStepRepo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class SystemDataLoader {

    private static final Pattern SUBJECT_PATTERN = Pattern.compile("\\s*Subject:(.*)[\\n\\r]{1}");

    @Autowired
    private ResourcePatternResolver resourcePatternResolver;

    @Autowired
    private EmailTemplateRepo emailTemplateRepo;

    @Autowired
    private EmbargoRepo embargoRepo;

    @Autowired
    private OrganizationRepo organizationRepo;

    @Autowired
    private OrganizationCategoryRepo organizationCategoryRepo;

    @Autowired
    private WorkflowRepo workflowRepo;

    @Autowired
    private WorkflowStepRepo workflowStepRepo;

    @Autowired
    private NoteRepo noteRepo;

    @Autowired
    private FieldProfileRepo fieldProfileRepo;

    @Autowired
    private FieldPredicateRepo fieldPredicateRepo;

    @Autowired
    private FieldGlossRepo fieldGlossRepo;

    @Autowired
    private ControlledVocabularyRepo controlledVocabularyRepo;

    @Autowired
    private LanguageRepo languageRepo;

    @Autowired
    private EmailWorkflowRuleRepo emailWorkflowRuleRepo;

    @Autowired
    private SubmissionStateRepo submissionStateRepo;

    @Autowired
    private ObjectMapper objectMapper;

    final static Logger logger = LoggerFactory.getLogger(SystemDataLoader.class);

    /**
     * Loads default system organization.
     */
    public void loadSystemOrganization() {

        try {
            // read and map json to Organization
            Organization systemOrganization = objectMapper.readValue(getFileFromResource("classpath:/organization/SYSTEM_Organization_Definition.json"), Organization.class);

            // check to see if organization category exists
            OrganizationCategory category = organizationCategoryRepo.findByNameAndLevel(systemOrganization.getCategory().getName(), systemOrganization.getCategory().getLevel());

            // create organization category if does not already exists
            if (category == null) {
                category = organizationCategoryRepo.create(systemOrganization.getCategory().getName(), systemOrganization.getCategory().getLevel());
            }

            // check to see if organization with organization category exists
            Organization organization = organizationRepo.findByNameAndCategory(systemOrganization.getName(), category);

            // create new organization if not already exists
            if (organization == null) {
                organization = organizationRepo.create(systemOrganization.getName(), category);
            }

            // check to see if workflow exists
            Workflow workflow = workflowRepo.findByNameAndOrganization(systemOrganization.getWorkflow().getName(), organization);

            // create workflow if not already exists else update properties in case changed in json
            if (workflow == null) {
                workflow = workflowRepo.create(systemOrganization.getWorkflow().getName(), systemOrganization.getWorkflow().isInheritable(), organization);
            } else {
                workflow.setInheritability(systemOrganization.getWorkflow().isInheritable());
                workflow = workflowRepo.save(workflow);
            }

            // temporary list of WorkflowStep
            List<WorkflowStep> workflowSteps = new ArrayList<WorkflowStep>();

            for (WorkflowStep workflowStep : systemOrganization.getWorkflow().getWorkflowSteps()) {

                // check to see if the WorkflowStep exists
                WorkflowStep newWorkflowStep = workflowStepRepo.findByNameAndWorkflow(workflowStep.getName(), workflow);

                // create new workflow step if not already exists
                if (newWorkflowStep == null) {
                    newWorkflowStep = workflowStepRepo.create(workflowStep.getName(), workflow);
                }

                // temporary list of FieldProfile
                List<FieldProfile> fieldProfiles = new ArrayList<FieldProfile>();

                workflowStep.getFieldProfiles().forEach(fieldProfile -> {

                    // check to see if the FieldPredicate exists
                    FieldPredicate fieldPredicate = fieldPredicateRepo.findByValue(fieldProfile.getPredicate().getValue());

                    // create new FieldPredicate if not already exists
                    if (fieldPredicate == null) {
                        fieldPredicate = fieldPredicateRepo.create(fieldProfile.getPredicate().getValue());
                    }

                    // check to see if the FieldProfile exists
                    FieldProfile newFieldProfile = fieldProfileRepo.findByPredicate(fieldPredicate);

                    // create new FieldProfile if not already exists
                    if (newFieldProfile == null) {
                        newFieldProfile = fieldProfileRepo.create(fieldPredicate, fieldProfile.getInputType(), fieldProfile.getUsage(), fieldProfile.getHelp(), fieldProfile.getRepeatable(), fieldProfile.getEnabled(), fieldProfile.getOptional());
                    } else {
                        newFieldProfile.setInputType(fieldProfile.getInputType() != null ? fieldProfile.getInputType() : newFieldProfile.getInputType());
                        newFieldProfile.setUsage(fieldProfile.getUsage() != null ? fieldProfile.getUsage() : newFieldProfile.getUsage());
                        newFieldProfile.setHelp(fieldProfile.getHelp() != null ? fieldProfile.getHelp() : newFieldProfile.getHelp());
                        newFieldProfile.setRepeatable(fieldProfile.getRepeatable() != null ? fieldProfile.getRepeatable() : newFieldProfile.getRepeatable());
                        newFieldProfile.setEnabled(fieldProfile.getEnabled() != null ? fieldProfile.getEnabled() : newFieldProfile.getEnabled());
                        newFieldProfile.setOptional(fieldProfile.getOptional() != null ? fieldProfile.getOptional() : newFieldProfile.getOptional());
                        newFieldProfile = fieldProfileRepo.save(newFieldProfile);
                    }

                    // temporary list of FieldGloss
                    List<FieldGloss> fieldGlosses = new ArrayList<FieldGloss>();

                    fieldProfile.getFieldGlosses().forEach(fieldGloss -> {

                        // check to see if the Language exists
                        Language language = languageRepo.findByName(fieldGloss.getLanguage().getName());

                        // create new Language if not already exists
                        if (language == null) {
                            language = languageRepo.create(fieldGloss.getLanguage().getName());
                        }

                        // check to see if the FieldGloss exists
                        FieldGloss newFieldGloss = fieldGlossRepo.findByValueAndLanguage(fieldGloss.getValue(), language);

                        // create new FieldGloss if not already exists
                        if (newFieldGloss == null) {
                            newFieldGloss = fieldGlossRepo.create(fieldGloss.getValue(), language);
                        }

                        fieldGlosses.add(newFieldGloss);

                    });

                    newFieldProfile.setFieldGlosses(fieldGlosses);

                    // temporary list of ControlledVocabulary
                    List<ControlledVocabulary> controlledVocabularies = new ArrayList<ControlledVocabulary>();

                    fieldProfile.getControlledVocabularies().forEach(controlledVocabulary -> {

                        // check to see if the Language exists
                        Language language = languageRepo.findByName(controlledVocabulary.getLanguage().getName());

                        // create new Language if not already exists
                        if (language == null) {
                            language = languageRepo.create(controlledVocabulary.getLanguage().getName());
                        }

                        // check to see if the ControlledVocabulary exists
                        ControlledVocabulary newControlledVocabulary = controlledVocabularyRepo.findByNameAndLanguage(controlledVocabulary.getName(), language);

                        // create new ControlledVocabulary if not already exists
                        if (newControlledVocabulary == null) {
                            newControlledVocabulary = controlledVocabularyRepo.create(controlledVocabulary.getName(), language);
                        }

                        controlledVocabularies.add(newControlledVocabulary);

                    });

                    newFieldProfile.setControlledVocabularies(controlledVocabularies);

                    fieldProfileRepo.save(newFieldProfile);

                    fieldProfiles.add(newFieldProfile);

                });

                newWorkflowStep.setFieldProfiles(fieldProfiles);

                // temporary list of Note
                List<Note> notes = new ArrayList<Note>();

                workflowStep.getNotes().forEach(note -> {

                    // check to see if the Note exists
                    Note newNote = noteRepo.findByName(note.getName());

                    // create new Note if not already exists
                    if (newNote == null) {
                        newNote = noteRepo.create(note.getName(), note.getText());
                    } else {
                        newNote.setText(note.getText());
                        newNote = noteRepo.save(newNote);
                    }

                    notes.add(newNote);

                });

                newWorkflowStep.setNotes(notes);

                workflowStepRepo.save(newWorkflowStep);

                workflowSteps.add(newWorkflowStep);

            }

            workflow.setWorkflowSteps(workflowSteps);

            workflowRepo.save(workflow);

            organization.setWorkflow(workflow);

            // temporary set of EmailWorkflowRule
            List<EmailWorkflowRule> emailWorkflowRules = new ArrayList<EmailWorkflowRule>();

            systemOrganization.getEmailWorkflowRules().forEach(emailWorkflowRule -> {

                // check to see if the SubmissionState exists
                SubmissionState newSubmissionState = submissionStateRepo.findByName(emailWorkflowRule.getSubmissionState().getName());
                
                // create new SubmissionState if not already exists
                if (newSubmissionState == null) {
                    newSubmissionState = submissionStateRepo.create(emailWorkflowRule.getSubmissionState().getName(), emailWorkflowRule.getSubmissionState().isArchived(), emailWorkflowRule.getSubmissionState().isPublishable(), emailWorkflowRule.getSubmissionState().isDeletable(), emailWorkflowRule.getSubmissionState().isEditableByReviewer(), emailWorkflowRule.getSubmissionState().isEditableByStudent(), emailWorkflowRule.getSubmissionState().isActive());
                    
                    newSubmissionState = submissionStateRepo.save(recursivelyFindOrCreateSubmissionState(emailWorkflowRule.getSubmissionState()));
                } else {
                    SubmissionState tempSubmissionState = emailWorkflowRule.getSubmissionState();
                    newSubmissionState.isArchived(tempSubmissionState.isArchived() != null ? tempSubmissionState.isArchived() : newSubmissionState.isArchived());
                    newSubmissionState.isPublishable(tempSubmissionState.isPublishable() != null ? tempSubmissionState.isPublishable() : newSubmissionState.isPublishable());
                    newSubmissionState.isDeletable(tempSubmissionState.isDeletable() != null ? tempSubmissionState.isDeletable() : newSubmissionState.isDeletable());
                    newSubmissionState.isEditableByReviewer(tempSubmissionState.isEditableByReviewer() != null ? tempSubmissionState.isEditableByReviewer() : newSubmissionState.isEditableByReviewer());
                    newSubmissionState.isEditableByStudent(tempSubmissionState.isEditableByStudent() != null ? tempSubmissionState.isEditableByStudent() : newSubmissionState.isEditableByStudent());
                    newSubmissionState.isActive(tempSubmissionState.isActive() != null ? tempSubmissionState.isActive() : newSubmissionState.isActive());
                    newSubmissionState.setTransitionSubmissionStates(tempSubmissionState.getTransitionSubmissionStates() != null ? tempSubmissionState.getTransitionSubmissionStates() : newSubmissionState.getTransitionSubmissionStates());
                    
                    newSubmissionState = submissionStateRepo.save(recursivelyFindOrCreateSubmissionState(newSubmissionState));
                }
               

                // check to see if the EmailTemplate exists
                EmailTemplate newEmailTemplate = emailTemplateRepo.findByNameAndIsSystemRequired(emailWorkflowRule.getEmailTemplate().getName(), emailWorkflowRule.getEmailTemplate().isSystemRequired());

                // create new EmailTemplate if not already exists
                if (newEmailTemplate == null) {
                    newEmailTemplate = emailTemplateRepo.create(emailWorkflowRule.getEmailTemplate().getName(), emailWorkflowRule.getEmailTemplate().getSubject(), emailWorkflowRule.getEmailTemplate().getMessage());
                } else {
                    newEmailTemplate.setSubject(emailWorkflowRule.getEmailTemplate().getSubject() != null ? emailWorkflowRule.getEmailTemplate().getSubject() : newEmailTemplate.getSubject());
                    newEmailTemplate.setMessage(emailWorkflowRule.getEmailTemplate().getMessage() != null ? emailWorkflowRule.getEmailTemplate().getMessage() : newEmailTemplate.getMessage());
                    newEmailTemplate = emailTemplateRepo.save(newEmailTemplate);
                }
                
                // check to see if the EmailWorkflowRule exists
                EmailWorkflowRule newEmailWorkflowRule = emailWorkflowRuleRepo.findBySubmissionStateAndRecipientTypeAndEmailTemplate(newSubmissionState, emailWorkflowRule.getRecipientType(), newEmailTemplate);

                if (newEmailWorkflowRule == null) {
                    newEmailWorkflowRule = emailWorkflowRuleRepo.create(newSubmissionState, emailWorkflowRule.getRecipientType(), newEmailTemplate, emailWorkflowRule.isSystem());
                }

                emailWorkflowRules.add(newEmailWorkflowRule);

            });

            organization.setEmailWorkflowRules(emailWorkflowRules);

            organizationRepo.save(organization);

            category.addOrganization(organization);

            organizationCategoryRepo.save(category);

        } catch (IOException e) {
            throw new IllegalStateException("Unable to generate system organization", e);
        }
    }
    
    /**
     * Loads default system submission states.
     */
    public void loadSystemSubmissionStates() {

        try {
            // read and map json to SubmissionState
            SubmissionState systemSubmissionState = objectMapper.readValue(getFileFromResource("classpath:/submission_states/SYSTEM_Submission_States.json"), SubmissionState.class);
        
            // check to see if the SubmissionState exists
            SubmissionState newSubmissionState = submissionStateRepo.findByName(systemSubmissionState.getName());
            
            // create new SubmissionState if not already exists
            if (newSubmissionState == null) {
                newSubmissionState = submissionStateRepo.create(systemSubmissionState.getName(), systemSubmissionState.isArchived(), systemSubmissionState.isPublishable(), systemSubmissionState.isDeletable(), systemSubmissionState.isEditableByReviewer(), systemSubmissionState.isEditableByStudent(), systemSubmissionState.isActive());
                
                newSubmissionState = submissionStateRepo.save(recursivelyFindOrCreateSubmissionState(systemSubmissionState));
            } else {
                newSubmissionState.isArchived(newSubmissionState.isArchived() != null ? newSubmissionState.isArchived() : newSubmissionState.isArchived());
                newSubmissionState.isPublishable(newSubmissionState.isPublishable() != null ? newSubmissionState.isPublishable() : newSubmissionState.isPublishable());
                newSubmissionState.isDeletable(newSubmissionState.isDeletable() != null ? newSubmissionState.isDeletable() : newSubmissionState.isDeletable());
                newSubmissionState.isEditableByReviewer(newSubmissionState.isEditableByReviewer() != null ? newSubmissionState.isEditableByReviewer() : newSubmissionState.isEditableByReviewer());
                newSubmissionState.isEditableByStudent(newSubmissionState.isEditableByStudent() != null ? newSubmissionState.isEditableByStudent() : newSubmissionState.isEditableByStudent());
                newSubmissionState.isActive(newSubmissionState.isActive() != null ? newSubmissionState.isActive() : newSubmissionState.isActive());
                newSubmissionState.setTransitionSubmissionStates(newSubmissionState.getTransitionSubmissionStates() != null ? newSubmissionState.getTransitionSubmissionStates() : newSubmissionState.getTransitionSubmissionStates());
                
                newSubmissionState = submissionStateRepo.save(recursivelyFindOrCreateSubmissionState(newSubmissionState));
            }
        
        } catch (IOException e) {
            throw new IllegalStateException("Unable to generate system organization", e);
        }
    }

    /**
     * Recursively map transition submission states from json.
     */
    public SubmissionState recursivelyFindOrCreateSubmissionState(SubmissionState submissionState) {

        // check to see if the SubmissionState exists
        SubmissionState newSubmissionState = submissionStateRepo.findByName(submissionState.getName());

        // create new SubmissionState if not already exists
        if (newSubmissionState == null) {
            newSubmissionState = submissionStateRepo.create(submissionState.getName(), submissionState.isArchived(), submissionState.isPublishable(), submissionState.isDeletable(), submissionState.isEditableByReviewer(), submissionState.isEditableByStudent(), submissionState.isActive());
        } else {
            newSubmissionState.isArchived(submissionState.isArchived() != null ? submissionState.isArchived() : newSubmissionState.isArchived());
            newSubmissionState.isPublishable(submissionState.isPublishable() != null ? submissionState.isPublishable() : newSubmissionState.isPublishable());
            newSubmissionState.isDeletable(submissionState.isDeletable() != null ? submissionState.isDeletable() : newSubmissionState.isDeletable());
            newSubmissionState.isEditableByReviewer(submissionState.isEditableByReviewer() != null ? submissionState.isEditableByReviewer() : newSubmissionState.isEditableByReviewer());
            newSubmissionState.isEditableByStudent(submissionState.isEditableByStudent() != null ? submissionState.isEditableByStudent() : newSubmissionState.isEditableByStudent());
            newSubmissionState.isActive(submissionState.isActive() != null ? submissionState.isActive() : newSubmissionState.isActive());
            
            newSubmissionState = submissionStateRepo.save(newSubmissionState);
        }
        
        // temporary list of SubmissionState
        List<SubmissionState> transitionStates = new ArrayList<SubmissionState>();
        
        submissionState.getTransitionSubmissionStates().forEach(transitionState -> {

            // check to see if the Transistion SubmissionState exists
            SubmissionState newTransitionState = submissionStateRepo.findByName(transitionState.getName());
            
            // create new Transistion SubmissionState if not already exists
            if (newTransitionState == null) {
                newTransitionState = submissionStateRepo.create(transitionState.getName(), transitionState.isArchived(), transitionState.isPublishable(), transitionState.isDeletable(), transitionState.isEditableByReviewer(), transitionState.isEditableByStudent(), transitionState.isActive());
                newTransitionState = submissionStateRepo.save(recursivelyFindOrCreateSubmissionState(transitionState));                
            } else {                
                newTransitionState.isArchived(transitionState.isArchived() != null ? transitionState.isArchived() : newTransitionState.isArchived());
                newTransitionState.isPublishable(transitionState.isPublishable() != null ? transitionState.isPublishable() : newTransitionState.isPublishable());
                newTransitionState.isDeletable(transitionState.isDeletable() != null ? transitionState.isDeletable() : newTransitionState.isDeletable());
                newTransitionState.isEditableByReviewer(transitionState.isEditableByReviewer() != null ? transitionState.isEditableByReviewer() : newTransitionState.isEditableByReviewer());
                newTransitionState.isEditableByStudent(transitionState.isEditableByStudent() != null ? transitionState.isEditableByStudent() : newTransitionState.isEditableByStudent());
                newTransitionState.isActive(transitionState.isActive() != null ? transitionState.isActive() : newTransitionState.isActive());
                newTransitionState.setTransitionSubmissionStates(transitionState.getTransitionSubmissionStates() != null ? transitionState.getTransitionSubmissionStates() : newTransitionState.getTransitionSubmissionStates());
                
                newTransitionState = submissionStateRepo.save(recursivelyFindOrCreateSubmissionState(newTransitionState));
            }
            
            transitionStates.add(newTransitionState);

        });

        newSubmissionState.setTransitionSubmissionStates(transitionStates);

        return submissionStateRepo.save(newSubmissionState);
    }

    public EmailTemplate loadSystemEmailTemplate(String name) {

        try {
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

            if (subject == null || subject.length() == 0)
                throw new IllegalStateException("Unable to identify the template's subject.");

            if (message == null || message.length() == 0)
                throw new IllegalStateException("Unable to identify the template's message.");

            EmailTemplate template = emailTemplateRepo.create(name, subject, message);

            template.isSystemRequired(true);

            return emailTemplateRepo.save(template);

        } catch (IOException e) {
            throw new IllegalStateException("Unable to generate system email template: " + name, e);
        }
    }

    public void generateAllSystemEmailTemplates() {

        for (String name : getAllSystemEmailTemplateNames()) {

            // try to see if it already exists in the DB
            EmailTemplate dbTemplate = emailTemplateRepo.findByNameAndIsSystemRequired(name, true);

            // create template or upgrade the old one
            if (dbTemplate == null) {
                dbTemplate = loadSystemEmailTemplate(name);
                logger.info("New System Email template being installed [" + dbTemplate.getName() + "]");
            } else {
                EmailTemplate loadedTemplate = loadSystemEmailTemplate(name);

                // if the template in the DB doesn't match in content with the one loaded from .email file
                if (!(dbTemplate.getMessage().equals(loadedTemplate.getMessage())) || !(dbTemplate.getSubject().equals(loadedTemplate.getSubject()))) {

                    EmailTemplate possibleCustomTemplate = emailTemplateRepo.findByNameAndIsSystemRequired(name, false);

                    // if this System template already has a custom template (meaning one named the same but that is !isSystemRequired)
                    if (possibleCustomTemplate != null) {

                        // a custom version of this System email template already exists, it's safe to override dbTemplate's data and save
                        dbTemplate.isSystemRequired(false);
                        dbTemplate.setMessage(loadedTemplate.getMessage());
                        dbTemplate.setSubject(loadedTemplate.getSubject());
                        dbTemplate.isSystemRequired(true);

                        logger.info("Upgrading Old System Email Template for [" + dbTemplate.getName() + "]");

                        emailTemplateRepo.save(dbTemplate);
                    }
                    // there is no custom one yet, we need to make the dbTemplate !isSystemRequired and the save loadedTemplate
                    else {
                        logger.info("Upgrading Old System Email Template and creating custom version for [" + dbTemplate.getName() + "]");
                        dbTemplate.isSystemRequired(false);
                        emailTemplateRepo.save(dbTemplate);
                        emailTemplateRepo.save(loadedTemplate);
                    }
                }
            }
        }
    }

    public List<String> getAllSystemEmailTemplateNames() {
        try {

            List<String> names = new ArrayList<String>();
            for (Resource resource : resourcePatternResolver.getResources("classpath:/emails/*.email")) {
                String fileName = resource.getFilename();
                String templateName = decodeTemplateName(fileName);
                names.add(templateName);
            }

            return names;

        } catch (IOException e) {
            throw new IllegalStateException("Unable to get emails directory from classpath.");
        }
    }

    public void generateAllSystemEmbargos() {

        try {

            List<Embargo> embargoDefinitions = objectMapper.readValue(getFileFromResource("classpath:/embargos/SYSTEM_Embargo_Definitions.json"), new TypeReference<List<Embargo>>() {
            });

            for (Embargo embargoDefinition : embargoDefinitions) {
                Embargo dbEmbargo = embargoRepo.findByNameAndGuarantorAndIsSystemRequired(embargoDefinition.getName(), embargoDefinition.getGuarantor(), true);

                // create template or upgrade the old one, new system embargos are enabled by default unless they have a custom one that already exists
                if (dbEmbargo == null) {
                    Embargo possibleCustomEmbargo = embargoRepo.findByNameAndGuarantorAndIsSystemRequired(embargoDefinition.getName(), embargoDefinition.getGuarantor(), false);

                    dbEmbargo = embargoRepo.create(embargoDefinition.getName(), embargoDefinition.getDescription(), embargoDefinition.getDuration(), embargoDefinition.isActive());
                    dbEmbargo.setGuarantor(embargoDefinition.getGuarantor());
                    dbEmbargo.isSystemRequired(true);
                    // if we have a custom one that's named the same, make sure this new one is not active by default
                    if (possibleCustomEmbargo != null) {
                        dbEmbargo.isActive(false);
                    }
                    logger.info("New System Embargo Type being installed [" + dbEmbargo.getName() + "]@[" + dbEmbargo.getGuarantor().name() + "]");
                    embargoRepo.save(dbEmbargo);
                } else {
                    Embargo loadedEmbargo = embargoRepo.create(embargoDefinition.getName(), embargoDefinition.getDescription(), embargoDefinition.getDuration(), embargoDefinition.isActive());
                    loadedEmbargo.setGuarantor(embargoDefinition.getGuarantor());
                    loadedEmbargo.isSystemRequired(true);

                    // if the embargo in the DB doesn't match in content with the one loaded from array
                    if (!(dbEmbargo.getDescription().equals(loadedEmbargo.getDescription())) || !(dbEmbargo.getDuration() == loadedEmbargo.getDuration()) || !(dbEmbargo.getGuarantor().ordinal() == loadedEmbargo.getGuarantor().ordinal())) {
                        Embargo possibleCustomEmbargo = embargoRepo.findByNameAndGuarantorAndIsSystemRequired(embargoDefinition.getName(), embargoDefinition.getGuarantor(), false);

                        // if this System template already has a custom template (meaning one named the same but that is !isSystemRequired)
                        if (possibleCustomEmbargo != null) {
                            // a custom version of this System email template already exists, it's safe to override dbTemplate's data and save
                            // upgraded system embargos that have a custom version are disabled by default
                            dbEmbargo.isActive(false);
                            dbEmbargo.setDescription(loadedEmbargo.getDescription());
                            dbEmbargo.setDuration(loadedEmbargo.getDuration());
                            dbEmbargo.setGuarantor(loadedEmbargo.getGuarantor());
                            dbEmbargo.isSystemRequired(true);
                            logger.info("Upgrading Old System Embargo Type for [" + dbEmbargo.getName() + "]@[" + dbEmbargo.getGuarantor().name() + "]");
                            embargoRepo.save(dbEmbargo);
                        }
                        // there is no custom one yet, we need to make the dbEmbargo !isSystemRequired and the save loadedEmbargo
                        else {
                            logger.info("Upgrading Old System Embargo Type and creating custom version for [" + dbEmbargo.getName() + "]@[" + dbEmbargo.getGuarantor().name() + "]");
                            dbEmbargo.isSystemRequired(false);
                            embargoRepo.save(dbEmbargo);
                            // upgraded system embargos are disabled by default
                            loadedEmbargo.isActive(false);
                            embargoRepo.save(loadedEmbargo);
                        }
                    }
                }
            }
        } catch (RuntimeException | IOException e) {
            System.out.println("\n\nERROR Generating System Embargos\n\n");
            e.printStackTrace();
            logger.debug("Unable to initialize default embargos. ", e);
        }
    }

    /**
     * Encode a template name to a file name on disk.
     * 
     * @param name
     *            The template name.
     * @return The file path.
     */
    private static String encodeTemplateName(String name) {
        return name.replaceAll(" ", "_") + ".email";
    }

    /**
     * Decode a template file name into a template name.
     * 
     * @param path
     *            The file name.
     * @return The template name.
     */
    private String decodeTemplateName(String path) {
        if (path.endsWith(".email")) {
            path = path.substring(0, path.length() - ".email".length());
        }
        return path.replaceAll("_", " ");
    }

    private File getFileFromResource(String resourcePath) throws IOException {
        Resource resource = resourcePatternResolver.getResource(resourcePath);
        if (!resource.getURL().toString().startsWith("jar:")) {
            return resource.getFile();
        } // else (we're inside a war/jar)
        File resourceFile = File.createTempFile("temp", ".tmp");
        resourceFile.deleteOnExit();
        IOUtils.copy(resource.getInputStream(), new FileOutputStream(resourceFile));
        return resourceFile;
    }
}
