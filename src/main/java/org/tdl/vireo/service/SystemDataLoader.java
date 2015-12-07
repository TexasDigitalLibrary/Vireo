package org.tdl.vireo.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
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
    private ApplicationContext applicationContext;
    
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
    
    
    public String resourceToString(Resource resource) throws IOException {
        return new String(Files.readAllBytes(Paths.get(resource.getFile().getAbsolutePath())));
    }
    
    /**
     * Loads default system organization.
     */
    public void loadSystemOrganization() {
        
        try {
            // read and map json to Organization
            Organization systemOrganization = objectMapper.readValue(applicationContext.getResource("classpath:/organization/SYSTEM_Organization_Definition.json").getFile(), Organization.class);

            // check to see if organization category exists
            OrganizationCategory category = organizationCategoryRepo.findByNameAndLevel(systemOrganization.getCategory().getName(), systemOrganization.getCategory().getLevel());
            
            // create organization category if does not already exists
            if(category == null) {
                category = organizationCategoryRepo.create(systemOrganization.getCategory().getName(), systemOrganization.getCategory().getLevel());
            }
            
            // check to see if organization with organization category exists
            Organization organization = organizationRepo.findByNameAndCategory(systemOrganization.getName(), category);
            
            // create new organization if not already exists
            if(organization == null) {
                organization = organizationRepo.create(systemOrganization.getName(), category);
            }
            
            // check to see if workflow exists
            Workflow workflow = workflowRepo.findByNameAndOrganization(systemOrganization.getWorkflow().getName(), organization);
            
            // create workflow if not already exists else update properties in case changed in json
            if(workflow == null) {
                workflow = workflowRepo.create(systemOrganization.getWorkflow().getName(), systemOrganization.getWorkflow().isInheritable(), organization);
            }
            else {
                workflow.setInheritability(systemOrganization.getWorkflow().isInheritable());
                workflow = workflowRepo.save(workflow);
            }
            
            // temporary list of WorkflowStep
            List<WorkflowStep> workflowSteps = new ArrayList<WorkflowStep>();
                                    
            for(WorkflowStep workflowStep : systemOrganization.getWorkflow().getWorkflowSteps()) {
                
                // check to see if the WorkflowStep exists
                WorkflowStep newWorkflowStep = workflowStepRepo.findByNameAndWorkflow(workflowStep.getName(), workflow);
                
                // create new workflow step if not already exists
                if(newWorkflowStep == null) {
                    newWorkflowStep = workflowStepRepo.create(workflowStep.getName(), workflow);
                }

                // temporary list of FieldProfile
                List<FieldProfile> fieldProfiles = new ArrayList<FieldProfile>();
                
                workflowStep.getFieldProfiles().forEach(fieldProfile -> {
                    
                    // check to see if the FieldPredicate exists
                    FieldPredicate fieldPredicate = fieldPredicateRepo.findByValue(fieldProfile.getPredicate().getValue());
                    
                    // create new FieldPredicate if not already exists
                    if(fieldPredicate == null) {
                        fieldPredicate = fieldPredicateRepo.create(fieldProfile.getPredicate().getValue());
                    }
                    
                    // check to see if the FieldProfile exists
                    FieldProfile newFieldProfile = fieldProfileRepo.findByPredicate(fieldPredicate);
                    
                    // create new FieldProfile if not already exists
                    if(newFieldProfile == null) {
                        newFieldProfile = fieldProfileRepo.create(fieldPredicate, fieldProfile.getInputType(), fieldProfile.getUsage(), fieldProfile.getHelp(), fieldProfile.getRepeatable(), fieldProfile.getEnabled(), fieldProfile.getOptional());
                    }
                    else {
                        newFieldProfile.setInputType(fieldProfile.getInputType());
                        newFieldProfile.setUsage(fieldProfile.getUsage());
                        newFieldProfile.setHelp(fieldProfile.getHelp());
                        newFieldProfile.setRepeatable(fieldProfile.getRepeatable());
                        newFieldProfile.setEnabled(fieldProfile.getEnabled());
                        newFieldProfile.setOptional(fieldProfile.getOptional());
                        newFieldProfile = fieldProfileRepo.save(newFieldProfile);
                    }
                    
                    // temporary list of FieldGloss
                    List<FieldGloss> fieldGlosses = new ArrayList<FieldGloss>();
                    
                    fieldProfile.getFieldGlosses().forEach(fieldGloss -> {
                        
                        // check to see if the Language exists
                        Language language = languageRepo.findByName(fieldGloss.getLanguage().getName());
                        
                        // create new Language if not already exists
                        if(language == null) {
                            language = languageRepo.create(fieldGloss.getLanguage().getName());
                        }
                        
                        // check to see if the FieldGloss exists
                        FieldGloss newFieldGloss = fieldGlossRepo.findByValueAndLanguage(fieldGloss.getValue(), language);
                        
                        // create new FieldGloss if not already exists
                        if(newFieldGloss == null) {
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
                        if(language == null) {
                            language = languageRepo.create(controlledVocabulary.getLanguage().getName());
                        }
                        
                        // check to see if the ControlledVocabulary exists
                        ControlledVocabulary newControlledVocabulary = controlledVocabularyRepo.findByNameAndLanguage(controlledVocabulary.getName(), language);
                        
                        // create new ControlledVocabulary if not already exists
                        if(newControlledVocabulary == null) {
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
                    if(newNote == null) {
                        newNote = noteRepo.create(note.getName(), note.getText());
                    }
                    else {
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
            Set<EmailWorkflowRule> emailWorkflowRules = new TreeSet<EmailWorkflowRule>();
            
            systemOrganization.getEmailWorkflowRules().forEach(emailWorkflowRule -> {
                
                // check to see if the SubmissionState exists
                SubmissionState newSubmissionState = submissionStateRepo.findByName(emailWorkflowRule.getSubmissionState().getName());
                
                // create new SubmissionState if not already exists
                if(newSubmissionState == null) {
                    newSubmissionState = submissionStateRepo.create(emailWorkflowRule.getSubmissionState().getName(), emailWorkflowRule.getSubmissionState().isArchived(), emailWorkflowRule.getSubmissionState().isPublishable(), emailWorkflowRule.getSubmissionState().isDeletable(), emailWorkflowRule.getSubmissionState().isEditableByReviewer(), emailWorkflowRule.getSubmissionState().isEditableByStudent(), emailWorkflowRule.getSubmissionState().isActive());
                }
                else {
                    SubmissionState tempSubmissionState = emailWorkflowRule.getSubmissionState();
                    newSubmissionState.isArchived(tempSubmissionState.isArchived());
                    newSubmissionState.isPublishable(tempSubmissionState.isPublishable());
                    newSubmissionState.isDeletable(tempSubmissionState.isDeletable());
                    newSubmissionState.isEditableByReviewer(tempSubmissionState.isEditableByReviewer());
                    newSubmissionState.isEditableByStudent(tempSubmissionState.isEditableByStudent());
                    newSubmissionState.isActive(tempSubmissionState.isActive());
                    newSubmissionState = submissionStateRepo.save(newSubmissionState);
                }
                                
                newSubmissionState = submissionStateRepo.save(recursivelyFindOurCreateSubmissionState(newSubmissionState));
                         
                // check to see if the EmailTemplate exists
                EmailTemplate newEmailTemplate = emailTemplateRepo.findByNameAndIsSystemRequired(emailWorkflowRule.getEmailTemplate().getName(), emailWorkflowRule.getEmailTemplate().isSystemRequired());
                
                // create new EmailTemplate if not already exists
                if(newEmailTemplate == null) {
                    newEmailTemplate = emailTemplateRepo.create(emailWorkflowRule.getEmailTemplate().getName(), emailWorkflowRule.getEmailTemplate().getSubject(), emailWorkflowRule.getEmailTemplate().getMessage());
                }
                else {
                    newEmailTemplate.setSubject(emailWorkflowRule.getEmailTemplate().getSubject());
                    newEmailTemplate.setMessage(emailWorkflowRule.getEmailTemplate().getMessage());
                    newEmailTemplate = emailTemplateRepo.save(newEmailTemplate);
                }
                
                // check to see if the EmailWorkflowRule exists
                EmailWorkflowRule newEmailWorkflowRule = emailWorkflowRuleRepo.findBySubmissionStateAndRecipientTypeAndEmailTemplate(newSubmissionState, emailWorkflowRule.getRecipientType(), newEmailTemplate);
                
                if(newEmailWorkflowRule == null) {
                    newEmailWorkflowRule = emailWorkflowRuleRepo.create(newSubmissionState, emailWorkflowRule.getRecipientType(), newEmailTemplate);
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
     * Recursively map transition submission states from json.
     */
    public SubmissionState recursivelyFindOurCreateSubmissionState(SubmissionState submissionState) {
        
        // check to see if the SubmissionState exists
        SubmissionState newSubmissionState = submissionStateRepo.findByName(submissionState.getName());
        
        // create new SubmissionState if not already exists
        if(newSubmissionState == null) {
            newSubmissionState = submissionStateRepo.create(submissionState.getName(), submissionState.isArchived(), submissionState.isPublishable(), submissionState.isDeletable(), submissionState.isEditableByReviewer(), submissionState.isEditableByStudent(), submissionState.isActive());
        }
        else {
            newSubmissionState.isArchived(submissionState.isArchived());
            newSubmissionState.isPublishable(submissionState.isPublishable());
            newSubmissionState.isDeletable(submissionState.isDeletable());
            newSubmissionState.isEditableByReviewer(submissionState.isEditableByReviewer());
            newSubmissionState.isEditableByStudent(submissionState.isEditableByStudent());
            newSubmissionState.isActive(submissionState.isActive());
            newSubmissionState = submissionStateRepo.save(newSubmissionState);
        }
        
        // temporary list of SubmissionState
        List<SubmissionState> transitionStates = new ArrayList<SubmissionState>();
        
        submissionState.getTransitionSubmissionStates().forEach(transitionState -> {
            
            // check to see if the Transistion SubmissionState exists
            SubmissionState newTransitionState = submissionStateRepo.findByName(transitionState.getName());
            
            // create new Transistion SubmissionState if not already exists
            if(newTransitionState == null) {
                newTransitionState = submissionStateRepo.create(transitionState.getName(), transitionState.isArchived(), transitionState.isPublishable(), transitionState.isDeletable(), transitionState.isEditableByReviewer(), transitionState.isEditableByStudent(), transitionState.isActive());
            }
            else {
                newTransitionState.isArchived(transitionState.isArchived());
                newTransitionState.isPublishable(transitionState.isPublishable());
                newTransitionState.isDeletable(transitionState.isDeletable());
                newTransitionState.isEditableByReviewer(transitionState.isEditableByReviewer());
                newTransitionState.isEditableByStudent(transitionState.isEditableByStudent());
                newTransitionState.isActive(transitionState.isActive());
                newTransitionState = submissionStateRepo.save(newTransitionState);
            }
            
            newTransitionState = recursivelyFindOurCreateSubmissionState(newTransitionState);

            transitionStates.add(newTransitionState);
            
        });
        
        newSubmissionState.setTransitionSubmissionStates(transitionStates);
        
        submissionStateRepo.save(newSubmissionState);
        
        return newSubmissionState;
    }
    
    public EmailTemplate loadSystemEmailTemplate(String name) {
        
        try {
            String data = resourceToString(applicationContext.getResource("classpath:/emails/" + encodeTemplateName(name)));
            
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
            File directory = applicationContext.getResource("classpath:/emails/").getFile();
            
            List<String> names = new ArrayList<String>();
            for (File file : directory.listFiles()) {

                if (file.isFile()) {
                    String fileName = file.getName();
                    String templateName = decodeTemplateName(fileName);
                    names.add(templateName);
                }
            }
            
            return names;
            
        } catch (IOException e) {
            throw new IllegalStateException("Unable to get emails directory from classpath.");
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
    
    public void generateAllSystemEmbargos() {
        
        try {
            
            List<Embargo> embargoDefinitions = objectMapper.readValue(applicationContext.getResource("classpath:/embargos/SYSTEM_Embargo_Definitions.json").getFile(), new TypeReference<List<Embargo>>() { });

            for (Embargo embargoDefinition : embargoDefinitions) {
                Embargo dbEmbargo = embargoRepo.findByNameAndGuarantorAndIsSystemRequired(embargoDefinition.getName(), embargoDefinition.getGuarantor(), true);

                // create template or upgrade the old one, new system embargos are enabled by default unless they have a custom one that already exists
                if (dbEmbargo == null) {
                    Embargo possibleCustomEmbargo = embargoRepo.findByNameAndGuarantorAndIsSystemRequired(embargoDefinition.getName(), embargoDefinition.getGuarantor(), false);
                    
                    dbEmbargo = embargoRepo.create(embargoDefinition.getName(), embargoDefinition.getDescription(), embargoDefinition.getDuration(), embargoDefinition.isActive());
                    dbEmbargo.setGuarantor(embargoDefinition.getGuarantor());
                    dbEmbargo.isSystemRequired(true);
                    // if we have a custom one that's named the same, make sure this new one is not active by default
                    if(possibleCustomEmbargo != null) {
                        dbEmbargo.isActive(false);
                    }
                    logger.info("New System Embargo Type being installed ["+dbEmbargo.getName()+"]@["+dbEmbargo.getGuarantor().name()+"]");
                    embargoRepo.save(dbEmbargo);
                } else {
                    Embargo loadedEmbargo = embargoRepo.create(embargoDefinition.getName(), embargoDefinition.getDescription(), embargoDefinition.getDuration(), embargoDefinition.isActive());
                    loadedEmbargo.setGuarantor(embargoDefinition.getGuarantor());
                    loadedEmbargo.isSystemRequired(true);

                    // if the embargo in the DB doesn't match in content with the one loaded from array
                    if (!(dbEmbargo.getDescription().equals(loadedEmbargo.getDescription())) || !( dbEmbargo.getDuration() == loadedEmbargo.getDuration()) || !(dbEmbargo.getGuarantor().ordinal() == loadedEmbargo.getGuarantor().ordinal())) {
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
                            logger.info("Upgrading Old System Embargo Type for ["+dbEmbargo.getName()+"]@["+dbEmbargo.getGuarantor().name()+"]");
                            embargoRepo.save(dbEmbargo);
                        }
                        // there is no custom one yet, we need to make the dbEmbargo !isSystemRequired and the save loadedEmbargo
                        else {
                            logger.info("Upgrading Old System Embargo Type and creating custom version for ["+dbEmbargo.getName()+"]@["+dbEmbargo.getGuarantor().name()+"]");
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
        
}
