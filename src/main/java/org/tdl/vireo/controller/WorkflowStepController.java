package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.ERROR;
import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;
import static edu.tamu.framework.enums.BusinessValidationType.CREATE;
import static edu.tamu.framework.enums.BusinessValidationType.DELETE;
import static edu.tamu.framework.enums.BusinessValidationType.EXISTS;
import static edu.tamu.framework.enums.BusinessValidationType.NONEXISTS;
import static edu.tamu.framework.enums.BusinessValidationType.UPDATE;
import static edu.tamu.framework.enums.MethodValidationType.LIST_REORDER;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.tdl.vireo.model.FieldProfile;
import org.tdl.vireo.model.Note;
import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.WorkflowStep;
import org.tdl.vireo.model.repo.FieldProfileRepo;
import org.tdl.vireo.model.repo.NoteRepo;
import org.tdl.vireo.model.repo.OrganizationRepo;
import org.tdl.vireo.model.repo.WorkflowStepRepo;
import org.tdl.vireo.model.repo.impl.ComponentNotPresentOnOrgException;
import org.tdl.vireo.model.repo.impl.HeritableModelNonOverrideableException;
import org.tdl.vireo.model.repo.impl.WorkflowStepNonOverrideableException;

import com.fasterxml.jackson.core.JsonProcessingException;

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.ApiValidatedModel;
import edu.tamu.framework.aspect.annotation.ApiValidation;
import edu.tamu.framework.aspect.annotation.ApiVariable;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.model.ApiResponse;

@Controller
@ApiMapping("/workflow-step")
public class WorkflowStepController {
    
    @Autowired
    private OrganizationRepo organizationRepo;
    
    @Autowired
    private WorkflowStepRepo workflowStepRepo;

    @Autowired
    private FieldProfileRepo fieldProfileRepo;
        
    @Autowired
    private NoteRepo noteRepo;
    
    @Autowired 
    private SimpMessagingTemplate simpMessagingTemplate;
    
    @ApiMapping("/all")
    @Auth(role="MANAGER")
    public ApiResponse getAll() {
        return new ApiResponse(SUCCESS, workflowStepRepo.findAll());
    }
    
    @Transactional
    @ApiMapping("/get/{workflowStepId}")    
    public ApiResponse getStepById(@ApiVariable Long workflowStepId) {
        WorkflowStep workflowStep = workflowStepRepo.findOne(workflowStepId);
        return (workflowStep != null) ? new ApiResponse(SUCCESS, workflowStep) : new ApiResponse(ERROR, "No wStep for id [" + workflowStepId.toString() + "]");
    }

    @ApiMapping("/{requestingOrgId}/{workflowStepId}/add-field-profile")
    @Auth(role="MANAGER")
    @ApiValidation(business = { @ApiValidation.Business(value = CREATE), @ApiValidation.Business(value = EXISTS) })
    public ApiResponse createFieldProfile(@ApiVariable Long requestingOrgId, @ApiVariable Long workflowStepId, @ApiValidatedModel FieldProfile fieldProfile) throws WorkflowStepNonOverrideableException, JsonProcessingException, ComponentNotPresentOnOrgException {
        
    	WorkflowStep workflowStep = workflowStepRepo.findOne(workflowStepId);
        
        Organization requestingOrganization = organizationRepo.findOne(requestingOrgId);
        
        if(!requestingOrganization.getId().equals(workflowStep.getOriginatingOrganization().getId())) {
            workflowStep = workflowStepRepo.update(workflowStep, requestingOrganization);
        }
        
        fieldProfileRepo.create(workflowStep, fieldProfile.getFieldPredicate(), fieldProfile.getInputType(), fieldProfile.getUsage(), fieldProfile.getHelp(), fieldProfile.getRepeatable(), fieldProfile.getOverrideable(), true, true, fieldProfile.getControlledVocabularies(), fieldProfile.getFieldGlosses());
                
        simpMessagingTemplate.convertAndSend("/channel/organization", new ApiResponse(SUCCESS, organizationRepo.findOne(requestingOrgId)));
        
        return new ApiResponse(SUCCESS);
    }
    
    @ApiMapping("/{requestingOrgId}/{workflowStepId}/update-field-profile")
    @Auth(role="MANAGER")
    @ApiValidation(business = { @ApiValidation.Business(value = UPDATE), @ApiValidation.Business(value = NONEXISTS) })
    public ApiResponse updateFieldProfile(@ApiVariable Long requestingOrgId, @ApiVariable Long workflowStepId, @ApiValidatedModel FieldProfile fieldProfile) throws WorkflowStepNonOverrideableException, JsonProcessingException, HeritableModelNonOverrideableException, ComponentNotPresentOnOrgException {
        
        fieldProfileRepo.update(fieldProfile, organizationRepo.findOne(requestingOrgId));
                
        simpMessagingTemplate.convertAndSend("/channel/organization", new ApiResponse(SUCCESS, organizationRepo.findOne(requestingOrgId)));
        
        return new ApiResponse(SUCCESS);
    }
    
    @ApiMapping("/{requestingOrgId}/{workflowStepId}/remove-field-profile")
    @Auth(role="MANAGER")
    @ApiValidation(business = { @ApiValidation.Business(value = DELETE), @ApiValidation.Business(value = NONEXISTS) })
    public ApiResponse removeFieldProfile(@ApiVariable Long requestingOrgId, @ApiVariable Long workflowStepId, @ApiValidatedModel FieldProfile fieldProfile) throws WorkflowStepNonOverrideableException, HeritableModelNonOverrideableException, ComponentNotPresentOnOrgException {
        
        WorkflowStep workflowStep = workflowStepRepo.findOne(workflowStepId);
        FieldProfile persistedFieldProfile = fieldProfileRepo.findOne(fieldProfile.getId());
        
        fieldProfileRepo.removeFromWorkflowStep(organizationRepo.findOne(requestingOrgId), workflowStep, persistedFieldProfile);   
        
        if(persistedFieldProfile.getOriginatingWorkflowStep().getId().equals(workflowStep.getId())) {
            fieldProfileRepo.delete(persistedFieldProfile);
        }
        
        simpMessagingTemplate.convertAndSend("/channel/organization", new ApiResponse(SUCCESS, organizationRepo.findOne(requestingOrgId)));
        
        return new ApiResponse(SUCCESS);
    }
    
    @ApiMapping("/{requestingOrgId}/{workflowStepId}/reorder-field-profiles/{src}/{dest}")
    @Auth(role="MANAGER")
    @ApiValidation(method = { @ApiValidation.Method(value = LIST_REORDER, model = FieldProfile.class, params = { "2", "3", "1", "aggregateFieldProfiles" }) })
    public ApiResponse reorderFieldProfiles(@ApiVariable Long requestingOrgId, @ApiVariable Long workflowStepId, @ApiVariable Integer src, @ApiVariable Integer dest) throws WorkflowStepNonOverrideableException, ComponentNotPresentOnOrgException {
        
        WorkflowStep workflowStep = workflowStepRepo.findOne(workflowStepId);
        
        workflowStepRepo.reorderFieldProfiles(organizationRepo.findOne(requestingOrgId), workflowStep, src, dest);     
        
        simpMessagingTemplate.convertAndSend("/channel/organization", new ApiResponse(SUCCESS, organizationRepo.findOne(requestingOrgId)));
        
        return new ApiResponse(SUCCESS);
    }
    
    
    
    
    @ApiMapping("/{requestingOrgId}/{workflowStepId}/add-note")
    @Auth(role="MANAGER")
    @ApiValidation(business = { @ApiValidation.Business(value = CREATE), @ApiValidation.Business(value = EXISTS) })
    public ApiResponse addNote(@ApiVariable Long requestingOrgId, @ApiVariable Long workflowStepId, @ApiValidatedModel Note note) throws WorkflowStepNonOverrideableException, ComponentNotPresentOnOrgException {
        
        WorkflowStep workflowStep = workflowStepRepo.findOne(workflowStepId);
        
        Organization requestingOrganization = organizationRepo.findOne(requestingOrgId);
        
        if(!requestingOrganization.getId().equals(workflowStep.getOriginatingOrganization().getId())) {
            workflowStep = workflowStepRepo.update(workflowStep, requestingOrganization);
        }
        
        noteRepo.create(workflowStep, note.getName(), note.getText(), note.getOverrideable());
        
        simpMessagingTemplate.convertAndSend("/channel/organization", new ApiResponse(SUCCESS, organizationRepo.findOne(requestingOrgId)));
        
        return new ApiResponse(SUCCESS);
    }
    
    @ApiMapping("/{requestingOrgId}/{workflowStepId}/update-note")
    @Auth(role="MANAGER")
    @ApiValidation(business = { @ApiValidation.Business(value = UPDATE), @ApiValidation.Business(value = NONEXISTS) })
    public ApiResponse updateNote(@ApiVariable Long requestingOrgId, @ApiVariable Long workflowStepId, @ApiValidatedModel Note note) throws WorkflowStepNonOverrideableException, HeritableModelNonOverrideableException, ComponentNotPresentOnOrgException {

        noteRepo.update(note, organizationRepo.findOne(requestingOrgId));
                
        simpMessagingTemplate.convertAndSend("/channel/organization", new ApiResponse(SUCCESS, organizationRepo.findOne(requestingOrgId)));
        
        return new ApiResponse(SUCCESS);
    }
    
    @ApiMapping("/{requestingOrgId}/{workflowStepId}/remove-note")
    @Auth(role="MANAGER")
    @ApiValidation(business = { @ApiValidation.Business(value = DELETE), @ApiValidation.Business(value = NONEXISTS) })
    public ApiResponse removeNote(@ApiVariable Long requestingOrgId, @ApiVariable Long workflowStepId, @ApiVariable Long noteId, @ApiValidatedModel Note note) throws NumberFormatException, WorkflowStepNonOverrideableException, HeritableModelNonOverrideableException, ComponentNotPresentOnOrgException {
        
        WorkflowStep workflowStep = workflowStepRepo.findOne(workflowStepId);
        
        Note persistedNote = noteRepo.findOne(note.getId());
        
        noteRepo.removeFromWorkflowStep(organizationRepo.findOne(requestingOrgId), workflowStep, persistedNote);
        
        if(persistedNote.getOriginatingWorkflowStep().getId().equals(workflowStep.getId())) {
            noteRepo.delete(persistedNote);
        }
        
        simpMessagingTemplate.convertAndSend("/channel/organization", new ApiResponse(SUCCESS, organizationRepo.findOne(requestingOrgId)));
        
        return new ApiResponse(SUCCESS);
    }
    
    @ApiMapping("/{requestingOrgId}/{workflowStepId}/reorder-notes/{src}/{dest}")
    @Auth(role="MANAGER")
    @ApiValidation(method = { @ApiValidation.Method(value = LIST_REORDER, model = WorkflowStep.class, params = { "2", "3", "1", "aggregateNotes" }) })
    public ApiResponse reorderNotes(@ApiVariable Long requestingOrgId, @ApiVariable Long workflowStepId, @ApiVariable Integer src, @ApiVariable Integer dest) throws NumberFormatException, WorkflowStepNonOverrideableException, ComponentNotPresentOnOrgException {
        
        WorkflowStep workflowStep = workflowStepRepo.findOne(workflowStepId);
        
        workflowStepRepo.reorderNotes(organizationRepo.findOne(requestingOrgId), workflowStep, src, dest);     
        
        simpMessagingTemplate.convertAndSend("/channel/organization", new ApiResponse(SUCCESS, organizationRepo.findOne(requestingOrgId)));
        
        return new ApiResponse(SUCCESS);
    }
    

}
