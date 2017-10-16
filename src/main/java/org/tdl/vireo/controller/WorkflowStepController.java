package org.tdl.vireo.controller;

import static edu.tamu.weaver.response.ApiStatus.ERROR;
import static edu.tamu.weaver.response.ApiStatus.SUCCESS;
import static edu.tamu.weaver.validation.model.BusinessValidationType.CREATE;
import static edu.tamu.weaver.validation.model.BusinessValidationType.DELETE;
import static edu.tamu.weaver.validation.model.BusinessValidationType.UPDATE;
import static edu.tamu.weaver.validation.model.MethodValidationType.LIST_REORDER;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMethod;
import org.tdl.vireo.exception.ComponentNotPresentOnOrgException;
import org.tdl.vireo.exception.HeritableModelNonOverrideableException;
import org.tdl.vireo.exception.WorkflowStepNonOverrideableException;
import org.tdl.vireo.model.FieldProfile;
import org.tdl.vireo.model.Note;
import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.WorkflowStep;
import org.tdl.vireo.model.repo.FieldProfileRepo;
import org.tdl.vireo.model.repo.NoteRepo;
import org.tdl.vireo.model.repo.OrganizationRepo;
import org.tdl.vireo.model.repo.WorkflowStepRepo;

import com.fasterxml.jackson.core.JsonProcessingException;

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.ApiVariable;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.validation.aspect.annotation.WeaverValidatedModel;
import edu.tamu.weaver.validation.aspect.annotation.WeaverValidation;

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
    @Auth(role = "MANAGER")
    public ApiResponse getAll() {
        return new ApiResponse(SUCCESS, workflowStepRepo.findAll());
    }

    @Transactional
    @ApiMapping("/get/{workflowStepId}")
    public ApiResponse getStepById(@ApiVariable Long workflowStepId) {
        WorkflowStep workflowStep = workflowStepRepo.findOne(workflowStepId);
        return (workflowStep != null) ? new ApiResponse(SUCCESS, workflowStep) : new ApiResponse(ERROR, "No workflow step for id [" + workflowStepId.toString() + "]");
    }

    @ApiMapping(value = "/{requestingOrgId}/{workflowStepId}/add-field-profile", method = RequestMethod.POST)
    @Auth(role = "MANAGER")
    @WeaverValidation(business = { @WeaverValidation.Business(value = CREATE) }) // , path = {"fieldPredicate", "documentTypePredicate"}, restrict = "true" // This was an incorrect assumption to validate restricting create of field profile when predicate is a document type field predicate
    public ApiResponse createFieldProfile(@ApiVariable Long requestingOrgId, @ApiVariable Long workflowStepId, @WeaverValidatedModel FieldProfile fieldProfile) throws WorkflowStepNonOverrideableException, JsonProcessingException, ComponentNotPresentOnOrgException {

        WorkflowStep workflowStep = workflowStepRepo.findOne(workflowStepId);

        Organization requestingOrganization = organizationRepo.findOne(requestingOrgId);

        if (!requestingOrganization.getId().equals(workflowStep.getOriginatingOrganization().getId())) {
            workflowStep = workflowStepRepo.update(workflowStep, requestingOrganization);
        }

        fieldProfileRepo.create(workflowStep, fieldProfile.getFieldPredicate(), fieldProfile.getInputType(), fieldProfile.getUsage(), fieldProfile.getHelp(), fieldProfile.getRepeatable(), fieldProfile.getOverrideable(), fieldProfile.getEnabled(), fieldProfile.getOptional(), fieldProfile.getFlagged(), fieldProfile.getLogged(), fieldProfile.getControlledVocabularies(), fieldProfile.getFieldGlosses(), fieldProfile.getDefaultValue());

        simpMessagingTemplate.convertAndSend("/channel/organization", new ApiResponse(SUCCESS, organizationRepo.findOne(requestingOrgId)));

        return new ApiResponse(SUCCESS);
    }

    @ApiMapping(value = "/{requestingOrgId}/{workflowStepId}/update-field-profile", method = RequestMethod.POST)
    @Auth(role = "MANAGER")
    @WeaverValidation(business = { @WeaverValidation.Business(value = UPDATE) })
    public ApiResponse updateFieldProfile(@ApiVariable Long requestingOrgId, @ApiVariable Long workflowStepId, @WeaverValidatedModel FieldProfile fieldProfile) throws WorkflowStepNonOverrideableException, JsonProcessingException, HeritableModelNonOverrideableException, ComponentNotPresentOnOrgException {

        fieldProfileRepo.update(fieldProfile, organizationRepo.findOne(requestingOrgId));

        simpMessagingTemplate.convertAndSend("/channel/organization", new ApiResponse(SUCCESS, organizationRepo.findOne(requestingOrgId)));

        return new ApiResponse(SUCCESS);
    }

    @ApiMapping(value = "/{requestingOrgId}/{workflowStepId}/remove-field-profile", method = RequestMethod.POST)
    @Auth(role = "MANAGER")
    @WeaverValidation(business = { @WeaverValidation.Business(value = DELETE) })
    public ApiResponse removeFieldProfile(@ApiVariable Long requestingOrgId, @ApiVariable Long workflowStepId, @WeaverValidatedModel FieldProfile fieldProfile) throws WorkflowStepNonOverrideableException, HeritableModelNonOverrideableException, ComponentNotPresentOnOrgException {

        WorkflowStep workflowStep = workflowStepRepo.findOne(workflowStepId);
        FieldProfile persistedFieldProfile = fieldProfileRepo.findOne(fieldProfile.getId());

        fieldProfileRepo.removeFromWorkflowStep(organizationRepo.findOne(requestingOrgId), workflowStep, persistedFieldProfile);

        // If the field profile is being removed from its originating workflow step by the organization that originates that step, then it should be deleted.
        if (persistedFieldProfile.getOriginatingWorkflowStep().getId().equals(workflowStep.getId()) && workflowStep.getOriginatingOrganization().getId().equals(requestingOrgId)) {
            fieldProfileRepo.delete(persistedFieldProfile);
        }

        simpMessagingTemplate.convertAndSend("/channel/organization", new ApiResponse(SUCCESS, organizationRepo.findOne(requestingOrgId)));

        return new ApiResponse(SUCCESS);
    }

    @ApiMapping("/{requestingOrgId}/{workflowStepId}/reorder-field-profiles/{src}/{dest}")
    @Auth(role = "MANAGER")
    @WeaverValidation(method = { @WeaverValidation.Method(value = LIST_REORDER, model = FieldProfile.class, params = { "2", "3", "1", "aggregateFieldProfiles" }) })
    public ApiResponse reorderFieldProfiles(@ApiVariable Long requestingOrgId, @ApiVariable Long workflowStepId, @ApiVariable Integer src, @ApiVariable Integer dest) throws WorkflowStepNonOverrideableException, ComponentNotPresentOnOrgException {

        WorkflowStep workflowStep = workflowStepRepo.findOne(workflowStepId);

        workflowStepRepo.reorderFieldProfiles(organizationRepo.findOne(requestingOrgId), workflowStep, src, dest);

        simpMessagingTemplate.convertAndSend("/channel/organization", new ApiResponse(SUCCESS, organizationRepo.findOne(requestingOrgId)));

        return new ApiResponse(SUCCESS);
    }

    @ApiMapping(value = "/{requestingOrgId}/{workflowStepId}/add-note", method = RequestMethod.POST)
    @Auth(role = "MANAGER")
    @WeaverValidation(business = { @WeaverValidation.Business(value = CREATE) })
    public ApiResponse addNote(@ApiVariable Long requestingOrgId, @ApiVariable Long workflowStepId, @WeaverValidatedModel Note note) throws WorkflowStepNonOverrideableException, ComponentNotPresentOnOrgException {

        WorkflowStep workflowStep = workflowStepRepo.findOne(workflowStepId);

        Organization requestingOrganization = organizationRepo.findOne(requestingOrgId);

        if (!requestingOrganization.getId().equals(workflowStep.getOriginatingOrganization().getId())) {
            workflowStep = workflowStepRepo.update(workflowStep, requestingOrganization);
        }

        noteRepo.create(workflowStep, note.getName(), note.getText(), note.getOverrideable());

        simpMessagingTemplate.convertAndSend("/channel/organization", new ApiResponse(SUCCESS, organizationRepo.findOne(requestingOrgId)));

        return new ApiResponse(SUCCESS);
    }

    @Auth(role = "MANAGER")
    @ApiMapping(value = "/{requestingOrgId}/{workflowStepId}/update-note", method = POST)
    @WeaverValidation(business = { @WeaverValidation.Business(value = UPDATE) })
    public ApiResponse updateNote(@ApiVariable Long requestingOrgId, @ApiVariable Long workflowStepId, @WeaverValidatedModel Note note) throws WorkflowStepNonOverrideableException, HeritableModelNonOverrideableException, ComponentNotPresentOnOrgException {

        noteRepo.update(note, organizationRepo.findOne(requestingOrgId));

        simpMessagingTemplate.convertAndSend("/channel/organization", new ApiResponse(SUCCESS, organizationRepo.findOne(requestingOrgId)));

        return new ApiResponse(SUCCESS);
    }

    @Auth(role = "MANAGER")
    @ApiMapping(value = "/{requestingOrgId}/{workflowStepId}/remove-note", method = POST)
    @WeaverValidation(business = { @WeaverValidation.Business(value = DELETE) })
    public ApiResponse removeNote(@ApiVariable Long requestingOrgId, @ApiVariable Long workflowStepId, @ApiVariable Long noteId, @WeaverValidatedModel Note note) throws NumberFormatException, WorkflowStepNonOverrideableException, HeritableModelNonOverrideableException, ComponentNotPresentOnOrgException {

        WorkflowStep workflowStep = workflowStepRepo.findOne(workflowStepId);

        Note persistedNote = noteRepo.findOne(note.getId());

        noteRepo.removeFromWorkflowStep(organizationRepo.findOne(requestingOrgId), workflowStep, persistedNote);

        if (persistedNote.getOriginatingWorkflowStep().getId().equals(workflowStep.getId())) {
            noteRepo.delete(persistedNote);
        }

        simpMessagingTemplate.convertAndSend("/channel/organization", new ApiResponse(SUCCESS, organizationRepo.findOne(requestingOrgId)));

        return new ApiResponse(SUCCESS);
    }

    @ApiMapping("/{requestingOrgId}/{workflowStepId}/reorder-notes/{src}/{dest}")
    @Auth(role = "MANAGER")
    @WeaverValidation(method = { @WeaverValidation.Method(value = LIST_REORDER, model = WorkflowStep.class, params = { "2", "3", "1", "aggregateNotes" }) })
    public ApiResponse reorderNotes(@ApiVariable Long requestingOrgId, @ApiVariable Long workflowStepId, @ApiVariable Integer src, @ApiVariable Integer dest) throws NumberFormatException, WorkflowStepNonOverrideableException, ComponentNotPresentOnOrgException {

        WorkflowStep workflowStep = workflowStepRepo.findOne(workflowStepId);

        workflowStepRepo.reorderNotes(organizationRepo.findOne(requestingOrgId), workflowStep, src, dest);

        simpMessagingTemplate.convertAndSend("/channel/organization", new ApiResponse(SUCCESS, organizationRepo.findOne(requestingOrgId)));

        return new ApiResponse(SUCCESS);
    }

}
