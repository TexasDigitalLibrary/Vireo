package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.ERROR;
import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;

import java.io.IOException;
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

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.ApiVariable;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.aspect.annotation.Data;
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
    
    @ApiMapping("/get/{id}")
    @Transactional
    public ApiResponse getStepById(@ApiVariable String id){

        Long wStepID = null;
        try {
            wStepID = Long.valueOf(id);
        } catch (NumberFormatException e) {
            return new ApiResponse(ERROR, "Enable to parse long from string: [" + id + "]");
        }

        WorkflowStep potentiallyExistingStep = workflowStepRepo.findOne(wStepID);
        if (potentiallyExistingStep != null) {
            return new ApiResponse(SUCCESS, potentiallyExistingStep);
        }

        return new ApiResponse(ERROR, "No wStep for id [" + wStepID.toString() + "]");
    }

    @ApiMapping("/{workflowStepId}/add-field-profile")
    @Auth(role="MANAGER")
    public ApiResponse createFieldProfile(@ApiVariable String workflowStepId, @Data String data) throws NumberFormatException, WorkflowStepNonOverrideableException, JsonProcessingException {
        
        JsonNode dataNode = null;
        try {
            dataNode = objectMapper.readTree(data);
        } catch (IOException e) {
            return new ApiResponse(ERROR, "Unable to parse data json ["+e.getMessage()+"]");
        }
        // TODO: validation
        Long reqOrgId = Long.parseLong(dataNode.get("requestingOrgId").toString());
        FieldGloss gloss = objectMapper.treeToValue(dataNode.get("gloss"), FieldGloss.class);
        FieldPredicate predicate = objectMapper.treeToValue(dataNode.get("predicate"), FieldPredicate.class);
        ControlledVocabulary controlledVocabulary = dataNode.get("controlledVocabulary") != null ? objectMapper.treeToValue(dataNode.get("controlledVocabulary"), ControlledVocabulary.class) : null;
        InputType inputType = objectMapper.treeToValue(dataNode.get("inputType"), InputType.class);
        Boolean repeatable = Boolean.parseBoolean(dataNode.get("repeatable").toString());
        String help = dataNode.get("help").textValue();
        String usage = "";
        
        WorkflowStep workflowStep = workflowStepRepo.findOne(Long.parseLong(workflowStepId));
        FieldProfile createdProfile = fieldProfileRepo.create(workflowStep, predicate, inputType, usage, help, repeatable, true, true, true);
        createdProfile.addControlledVocabulary(controlledVocabulary);
        createdProfile.addFieldGloss(gloss);
        
        simpMessagingTemplate.convertAndSend("/channel/organization", new ApiResponse(SUCCESS, organizationRepo.findOne(reqOrgId)));
        
        return new ApiResponse(SUCCESS, fieldProfileRepo.save(createdProfile));
    }
    
    @ApiMapping("/{workflowStepId}/reorder-field-profiles/{src}/{dest}")
    @Auth(role="MANAGER")
    public ApiResponse reorderFieldProfiles(@ApiVariable String workflowStepId, @ApiVariable String src, @ApiVariable String dest, @Data String data) throws NumberFormatException, WorkflowStepNonOverrideableException {
        
        JsonNode dataNode = null;
        try {
            dataNode = objectMapper.readTree(data);
        } catch (IOException e) {
            return new ApiResponse(ERROR, "Unable to parse data json ["+e.getMessage()+"]");
        }
        
        Long reqOrgId = Long.parseLong(dataNode.get("requestingOrgId").toString());
        
        WorkflowStep workflowStep = workflowStepRepo.findOne(Long.parseLong(workflowStepId));
        
        workflowStepRepo.reorderFieldProfiles(organizationRepo.findOne(reqOrgId), workflowStep, Integer.parseInt(src), Integer.parseInt(dest));     
        
        simpMessagingTemplate.convertAndSend("/channel/organization", new ApiResponse(SUCCESS, organizationRepo.findOne(reqOrgId)));
        
        return new ApiResponse(SUCCESS);
    }
    
    @ApiMapping("/{workflowStepId}/remove-field-profile/{fieldProfileId}")
    @Auth(role="MANAGER")
    public ApiResponse removeFieldProfile(@ApiVariable String workflowStepId, @ApiVariable String fieldProfileId, @Data String data) throws NumberFormatException, WorkflowStepNonOverrideableException, FieldProfileNonOverrideableException {
        
    	JsonNode dataNode = null;
        try {
            dataNode = objectMapper.readTree(data);
        } catch (IOException e) {
            return new ApiResponse(ERROR, "Unable to parse data json ["+e.getMessage()+"]");
        }
        
        Long reqOrgId = Long.parseLong(dataNode.get("requestingOrgId").toString());
        
        WorkflowStep workflowStep = workflowStepRepo.findOne(Long.parseLong(workflowStepId));
        FieldProfile fieldProfile = fieldProfileRepo.findOne(Long.parseLong(fieldProfileId));
        
        fieldProfileRepo.disinheritFromWorkflowStep(organizationRepo.findOne(reqOrgId), workflowStep, fieldProfile);     
        
        simpMessagingTemplate.convertAndSend("/channel/organization", new ApiResponse(SUCCESS, organizationRepo.findOne(reqOrgId)));
        
        return new ApiResponse(SUCCESS);
    }
    
    
    
    @ApiMapping("/{workflowStepId}/add-note")
    @Auth(role="MANAGER")
    public ApiResponse addNote(@ApiVariable String workflowStepId, @Data String data) throws NumberFormatException, WorkflowStepNonOverrideableException {
        
        JsonNode dataNode = null;
        try {
            dataNode = objectMapper.readTree(data);
        } catch (IOException e) {
            return new ApiResponse(ERROR, "Unable to parse data json ["+e.getMessage()+"]");
        }
        
        Long reqOrgId = Long.parseLong(dataNode.get("requestingOrgId").toString());
        String name = dataNode.get("noteName").textValue();
        String text = dataNode.get("noteText").textValue();
        
        WorkflowStep workflowStep = workflowStepRepo.findOne(Long.parseLong(workflowStepId));
        
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
    public ApiResponse updateNote(@ApiVariable String workflowStepId, @Data String data) throws NumberFormatException, WorkflowStepNonOverrideableException, NoteNonOverrideableException {
        
        JsonNode dataNode = null;
        try {
            dataNode = objectMapper.readTree(data);
        } catch (IOException e) {
            return new ApiResponse(ERROR, "Unable to parse data json ["+e.getMessage()+"]");
        }
        
        Long reqOrgId = Long.parseLong(dataNode.get("requestingOrgId").toString());
        Long id = Long.parseLong(dataNode.get("noteId").toString());
        String name = dataNode.get("noteName").textValue();
        String text = dataNode.get("noteText").textValue();
                
        Organization requestingOrganization = organizationRepo.findOne(reqOrgId);
        
        Note note = noteRepo.findOne(id);
        
        note.setName(name);
        note.setText(text);
        
        noteRepo.update(note, requestingOrganization);
                
        simpMessagingTemplate.convertAndSend("/channel/organization", new ApiResponse(SUCCESS, organizationRepo.findOne(reqOrgId)));
        
        return new ApiResponse(SUCCESS);
    }
    
    @ApiMapping("/{workflowStepId}/remove-note/{noteId}")
    @Auth(role="MANAGER")
    public ApiResponse removeNote(@ApiVariable String workflowStepId, @ApiVariable String noteId, @Data String data) throws NumberFormatException, WorkflowStepNonOverrideableException, NoteNonOverrideableException {
        
        JsonNode dataNode = null;
        try {
            dataNode = objectMapper.readTree(data);
        } catch (IOException e) {
            return new ApiResponse(ERROR, "Unable to parse data json ["+e.getMessage()+"]");
        }
        
        Long reqOrgId = Long.parseLong(dataNode.get("requestingOrgId").toString());
        
        WorkflowStep workflowStep = workflowStepRepo.findOne(Long.parseLong(workflowStepId));
        Note note = noteRepo.findOne(Long.parseLong(noteId));
        
        noteRepo.disinheritFromWorkflowStep(organizationRepo.findOne(reqOrgId), workflowStep, note);     
        
        simpMessagingTemplate.convertAndSend("/channel/organization", new ApiResponse(SUCCESS, organizationRepo.findOne(reqOrgId)));
        
        return new ApiResponse(SUCCESS);
    }
    
    @ApiMapping("/{workflowStepId}/reorder-notes/{src}/{dest}")
    @Auth(role="MANAGER")
    public ApiResponse reorderNotes(@ApiVariable String workflowStepId, @ApiVariable String src, @ApiVariable String dest, @Data String data) throws NumberFormatException, WorkflowStepNonOverrideableException {
        
        JsonNode dataNode = null;
        try {
            dataNode = objectMapper.readTree(data);
        } catch (IOException e) {
            return new ApiResponse(ERROR, "Unable to parse data json ["+e.getMessage()+"]");
        }
        
        Long reqOrgId = Long.parseLong(dataNode.get("requestingOrgId").toString());
        
        WorkflowStep workflowStep = workflowStepRepo.findOne(Long.parseLong(workflowStepId));
        
        workflowStepRepo.reorderNotes(organizationRepo.findOne(reqOrgId), workflowStep, Integer.parseInt(src), Integer.parseInt(dest));     
        
        simpMessagingTemplate.convertAndSend("/channel/organization", new ApiResponse(SUCCESS, organizationRepo.findOne(reqOrgId)));
        
        return new ApiResponse(SUCCESS);
    }
    

}
