package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.ERROR;
import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.tdl.vireo.enums.InputType;
import org.tdl.vireo.model.ControlledVocabulary;
import org.tdl.vireo.model.FieldGloss;
import org.tdl.vireo.model.FieldPredicate;
import org.tdl.vireo.model.FieldProfile;
import org.tdl.vireo.model.Note;
import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.WorkflowStep;
import org.tdl.vireo.model.repo.FieldProfileRepo;
import org.tdl.vireo.model.repo.NoteRepo;
import org.tdl.vireo.model.repo.OrganizationRepo;
import org.tdl.vireo.model.repo.WorkflowStepRepo;
import org.tdl.vireo.model.repo.impl.FieldProfileNonOverrideableException;
import org.tdl.vireo.model.repo.impl.NoteNonOverrideableException;
import org.tdl.vireo.model.repo.impl.WorkflowStepNonOverrideableException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.framework.aspect.annotation.ApiData;
import edu.tamu.framework.aspect.annotation.ApiMapping;
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
    private ObjectMapper objectMapper;
    
    @Autowired 
    private SimpMessagingTemplate simpMessagingTemplate;
    
    @ApiMapping("/all")
    @Auth(role="MANAGER")
    public ApiResponse getAll() {
        Map<String,List<WorkflowStep>> map = new HashMap<String,List<WorkflowStep>>();        
        map.put("list", workflowStepRepo.findAll());
        return new ApiResponse(SUCCESS, map);
    }
    
    @ApiMapping("/get/{workflowStepId}")
    @Transactional
    public ApiResponse getStepById(@ApiVariable Long workflowStepId){

        WorkflowStep potentiallyExistingStep = workflowStepRepo.findOne(workflowStepId);
        if (potentiallyExistingStep != null) {
            return new ApiResponse(SUCCESS, potentiallyExistingStep);
        }

        return new ApiResponse(ERROR, "No wStep for id [" + workflowStepId.toString() + "]");
    }

    @ApiMapping("/{workflowStepId}/add-field-profile")
    @Auth(role="MANAGER")
    public ApiResponse createFieldProfile(@ApiVariable Long workflowStepId, @ApiData JsonNode dataNode) throws NumberFormatException, WorkflowStepNonOverrideableException, JsonProcessingException {
        
        // TODO: validation
        
        Long reqOrgId = Long.parseLong(dataNode.get("requestingOrgId").toString());
        
        FieldGloss gloss = objectMapper.treeToValue(dataNode.get("gloss"), FieldGloss.class);
        
        ControlledVocabulary controlledVocabulary = dataNode.get("controlledVocabulary") != null ? objectMapper.treeToValue(dataNode.get("controlledVocabulary"), ControlledVocabulary.class) : null;
        
        FieldPredicate predicate = objectMapper.treeToValue(dataNode.get("predicate"), FieldPredicate.class);
        
        InputType inputType = objectMapper.treeToValue(dataNode.get("inputType"), InputType.class);
        Boolean repeatable = dataNode.get("repeatable") != null ? Boolean.parseBoolean(dataNode.get("repeatable").toString()) : false;
        String help = dataNode.get("help") != null ? dataNode.get("help").textValue() : null;
        String usage = dataNode.get("usage") != null ? dataNode.get("usage").textValue() : "";
        
        
        WorkflowStep workflowStep = workflowStepRepo.findOne(workflowStepId);
        
        Organization requestingOrganization = organizationRepo.findOne(reqOrgId);
        
        if(!requestingOrganization.getId().equals(workflowStep.getOriginatingOrganization().getId())) {
            workflowStep = workflowStepRepo.update(workflowStep, requestingOrganization);
        }
        
        FieldProfile createdProfile = fieldProfileRepo.create(workflowStep, predicate, inputType, usage, help, repeatable, true, true, true);
        createdProfile.addControlledVocabulary(controlledVocabulary);
        createdProfile.addFieldGloss(gloss);
        
        simpMessagingTemplate.convertAndSend("/channel/organization", new ApiResponse(SUCCESS, organizationRepo.findOne(reqOrgId)));
        
        return new ApiResponse(SUCCESS, fieldProfileRepo.save(createdProfile));
    }
    
    @ApiMapping("/{workflowStepId}/update-field-profile")
    @Auth(role="MANAGER")
    public ApiResponse updateFieldProfile(@ApiVariable Long workflowStepId, @ApiData JsonNode dataNode) throws NumberFormatException, WorkflowStepNonOverrideableException, JsonProcessingException, FieldProfileNonOverrideableException {
                
        // TODO: validation
        
        Long id = Long.parseLong(dataNode.get("id").toString());
        
        Long reqOrgId = Long.parseLong(dataNode.get("requestingOrgId").toString());
        
        
        FieldGloss fieldGloss = objectMapper.treeToValue(dataNode.get("gloss"), FieldGloss.class);
        ControlledVocabulary controlledVocabulary = dataNode.get("controlledVocabulary") != null ? objectMapper.treeToValue(dataNode.get("controlledVocabulary"), ControlledVocabulary.class) : null;
        
        FieldPredicate predicate = objectMapper.treeToValue(dataNode.get("predicate"), FieldPredicate.class);
        
        InputType inputType = objectMapper.treeToValue(dataNode.get("inputType"), InputType.class);
        Boolean repeatable = dataNode.get("repeatable") != null ? Boolean.parseBoolean(dataNode.get("repeatable").toString()) : false;
        String help = dataNode.get("help") != null ? dataNode.get("help").textValue() : null;
        String usage = dataNode.get("usage") != null ? dataNode.get("usage").textValue() : null;
                
        
        FieldProfile fieldProfile = fieldProfileRepo.findOne(id);
        
        fieldProfile.setPredicate(predicate);
        fieldProfile.setInputType(inputType);
        fieldProfile.setRepeatable(repeatable);
        fieldProfile.setHelp(help);
        fieldProfile.setUsage(usage);
        
        
        // TODO: should add additional field gloss instead of replacing all with newly selected or created
        //       requires UI changes
        
        List<FieldGloss> newFieldGlosses = new ArrayList<FieldGloss>();
        newFieldGlosses.add(fieldGloss);
        
        fieldProfile.setFieldGlosses(newFieldGlosses);
        
        
        // TODO: should add additional controlled vocabulary instead of replacing all with newly selected or created
        //       requires UI changes
        
        if(controlledVocabulary != null) {
            List<ControlledVocabulary> newControlledVocabularies = new ArrayList<ControlledVocabulary>();
            newControlledVocabularies.add(controlledVocabulary);
            
            fieldProfile.setControlledVocabularies(newControlledVocabularies);
        }
        else {
            fieldProfile.clearControlledVocabulary();
        }
        
        
        Organization requestingOrganization = organizationRepo.findOne(reqOrgId);
        

        fieldProfileRepo.update(fieldProfile, requestingOrganization);
        
                
        simpMessagingTemplate.convertAndSend("/channel/organization", new ApiResponse(SUCCESS, organizationRepo.findOne(reqOrgId)));
        
        return new ApiResponse(SUCCESS);
    }
    
    @ApiMapping("/{workflowStepId}/reorder-field-profiles/{src}/{dest}")
    @Auth(role="MANAGER")
    public ApiResponse reorderFieldProfiles(@ApiVariable Long workflowStepId, @ApiVariable Integer src, @ApiVariable Integer dest, @ApiData JsonNode dataNode) throws NumberFormatException, WorkflowStepNonOverrideableException {
                
        Long reqOrgId = Long.parseLong(dataNode.get("requestingOrgId").toString());
        
        WorkflowStep workflowStep = workflowStepRepo.findOne(workflowStepId);
        
        workflowStepRepo.reorderFieldProfiles(organizationRepo.findOne(reqOrgId), workflowStep, src, dest);     
        
        simpMessagingTemplate.convertAndSend("/channel/organization", new ApiResponse(SUCCESS, organizationRepo.findOne(reqOrgId)));
        
        return new ApiResponse(SUCCESS);
    }
    
    @ApiMapping("/{workflowStepId}/remove-field-profile/{fieldProfileId}")
    @Auth(role="MANAGER")
    public ApiResponse removeFieldProfile(@ApiVariable Long workflowStepId, @ApiVariable Long fieldProfileId, @ApiData JsonNode dataNode) throws NumberFormatException, WorkflowStepNonOverrideableException, FieldProfileNonOverrideableException {
        
        Long reqOrgId = Long.parseLong(dataNode.get("requestingOrgId").toString());
        
        WorkflowStep workflowStep = workflowStepRepo.findOne(workflowStepId);
        FieldProfile fieldProfile = fieldProfileRepo.findOne(fieldProfileId);
        
        fieldProfileRepo.disinheritFromWorkflowStep(organizationRepo.findOne(reqOrgId), workflowStep, fieldProfile);   
        
        if(fieldProfile.getOriginatingWorkflowStep().getId().equals(workflowStep.getId())) {
            fieldProfileRepo.delete(fieldProfile);
        }
        
        simpMessagingTemplate.convertAndSend("/channel/organization", new ApiResponse(SUCCESS, organizationRepo.findOne(reqOrgId)));
        
        return new ApiResponse(SUCCESS);
    }
    
    
    
    @ApiMapping("/{workflowStepId}/add-note")
    @Auth(role="MANAGER")
    public ApiResponse addNote(@ApiVariable Long workflowStepId, @ApiData JsonNode dataNode) throws NumberFormatException, WorkflowStepNonOverrideableException {
        
        Long reqOrgId = Long.parseLong(dataNode.get("requestingOrgId").toString());
        String name = dataNode.get("noteName").textValue();
        String text = dataNode.get("noteText").textValue();
        
        WorkflowStep workflowStep = workflowStepRepo.findOne(workflowStepId);
        
        Organization requestingOrganization = organizationRepo.findOne(reqOrgId);
        
        if(!requestingOrganization.getId().equals(workflowStep.getOriginatingOrganization().getId())) {
            workflowStep = workflowStepRepo.update(workflowStep, requestingOrganization);
        }
        
        noteRepo.create(workflowStep, name, text);
        
        simpMessagingTemplate.convertAndSend("/channel/organization", new ApiResponse(SUCCESS, organizationRepo.findOne(reqOrgId)));
        
        return new ApiResponse(SUCCESS);
    }
    
    @ApiMapping("/{workflowStepId}/update-note")
    @Auth(role="MANAGER")
    public ApiResponse updateNote(@ApiVariable Long workflowStepId, @ApiData JsonNode dataNode) throws NumberFormatException, WorkflowStepNonOverrideableException, NoteNonOverrideableException {
        
        Long reqOrgId = Long.parseLong(dataNode.get("requestingOrgId").toString());
        Long noteId = Long.parseLong(dataNode.get("noteId").toString());
        String name = dataNode.get("noteName").textValue();
        String text = dataNode.get("noteText").textValue();
                
        Organization requestingOrganization = organizationRepo.findOne(reqOrgId);
        
        Note note = noteRepo.findOne(noteId);
        
        note.setName(name);
        note.setText(text);
        
        noteRepo.update(note, requestingOrganization);
                
        simpMessagingTemplate.convertAndSend("/channel/organization", new ApiResponse(SUCCESS, organizationRepo.findOne(reqOrgId)));
        
        return new ApiResponse(SUCCESS);
    }
    
    @ApiMapping("/{workflowStepId}/remove-note/{noteId}")
    @Auth(role="MANAGER")
    public ApiResponse removeNote(@ApiVariable Long workflowStepId, @ApiVariable Long noteId, @ApiData JsonNode dataNode) throws NumberFormatException, WorkflowStepNonOverrideableException, NoteNonOverrideableException {
        
        Long reqOrgId = Long.parseLong(dataNode.get("requestingOrgId").toString());
        
        WorkflowStep workflowStep = workflowStepRepo.findOne(workflowStepId);
        
        Note note = noteRepo.findOne(noteId);
        
        noteRepo.disinheritFromWorkflowStep(organizationRepo.findOne(reqOrgId), workflowStep, note);
        
        if(note.getOriginatingWorkflowStep().getId().equals(workflowStep.getId())) {
            noteRepo.delete(note);
        }
        
        simpMessagingTemplate.convertAndSend("/channel/organization", new ApiResponse(SUCCESS, organizationRepo.findOne(reqOrgId)));
        
        return new ApiResponse(SUCCESS);
    }
    
    @ApiMapping("/{workflowStepId}/reorder-notes/{src}/{dest}")
    @Auth(role="MANAGER")
    public ApiResponse reorderNotes(@ApiVariable Long workflowStepId, @ApiVariable Integer src, @ApiVariable Integer dest, @ApiData JsonNode dataNode) throws NumberFormatException, WorkflowStepNonOverrideableException {
        
        Long reqOrgId = Long.parseLong(dataNode.get("requestingOrgId").toString());
        
        WorkflowStep workflowStep = workflowStepRepo.findOne(workflowStepId);
        
        workflowStepRepo.reorderNotes(organizationRepo.findOne(reqOrgId), workflowStep, src, dest);     
        
        simpMessagingTemplate.convertAndSend("/channel/organization", new ApiResponse(SUCCESS, organizationRepo.findOne(reqOrgId)));
        
        return new ApiResponse(SUCCESS);
    }
    

}
