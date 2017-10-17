package org.tdl.vireo.controller;

import static edu.tamu.weaver.response.ApiStatus.SUCCESS;
import static edu.tamu.weaver.validation.model.BusinessValidationType.CREATE;
import static edu.tamu.weaver.validation.model.BusinessValidationType.DELETE;
import static edu.tamu.weaver.validation.model.BusinessValidationType.UPDATE;
import static edu.tamu.weaver.validation.model.MethodValidationType.REORDER;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;
import org.tdl.vireo.model.CustomActionDefinition;
import org.tdl.vireo.model.repo.CustomActionDefinitionRepo;

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.ApiVariable;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.validation.aspect.annotation.WeaverValidatedModel;
import edu.tamu.weaver.validation.aspect.annotation.WeaverValidation;

@RestController
@ApiMapping("/settings/custom-action")
public class CustomActionSettingsController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CustomActionDefinitionRepo customActionDefinitionRepo;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @ApiMapping("/all")
    public ApiResponse getCustomActions() {
        return new ApiResponse(SUCCESS, customActionDefinitionRepo.findAllByOrderByPositionAsc());
    }

    @Auth(role = "MANAGER")
    @ApiMapping(value = "/create", method = POST)
    @WeaverValidation(business = { @WeaverValidation.Business(value = CREATE) })
    public ApiResponse createCustomAction(@WeaverValidatedModel CustomActionDefinition customActionDefinition) {
        logger.info("Creating custom action definition with label " + customActionDefinition.getLabel());
        customActionDefinition = customActionDefinitionRepo.create(customActionDefinition.getLabel(), customActionDefinition.isStudentVisible());
        simpMessagingTemplate.convertAndSend("/channel/settings/custom-action", new ApiResponse(SUCCESS, customActionDefinitionRepo.findAllByOrderByPositionAsc()));
        return new ApiResponse(SUCCESS, customActionDefinition);
    }

    @Auth(role = "MANAGER")
    @ApiMapping(value = "/update", method = POST)
    @WeaverValidation(business = { @WeaverValidation.Business(value = UPDATE) })
    public ApiResponse updateCustomAction(@WeaverValidatedModel CustomActionDefinition customActionDefinition) {
        logger.info("Updating custom action definition with label " + customActionDefinition.getLabel());
        customActionDefinition = customActionDefinitionRepo.save(customActionDefinition);
        simpMessagingTemplate.convertAndSend("/channel/settings/custom-action", new ApiResponse(SUCCESS, customActionDefinitionRepo.findAllByOrderByPositionAsc()));
        return new ApiResponse(SUCCESS, customActionDefinition);
    }

    @Auth(role = "MANAGER")
    @ApiMapping(value = "/remove", method = POST)
    @WeaverValidation(business = { @WeaverValidation.Business(value = DELETE) })
    public ApiResponse removeCustomAction(@WeaverValidatedModel CustomActionDefinition customActionDefinition) {
        logger.info("Removing custom action definition with label " + customActionDefinition.getLabel());
        customActionDefinitionRepo.remove(customActionDefinition);
        simpMessagingTemplate.convertAndSend("/channel/settings/custom-action", new ApiResponse(SUCCESS, customActionDefinitionRepo.findAllByOrderByPositionAsc()));
        return new ApiResponse(SUCCESS);
    }

    @ApiMapping("/reorder/{src}/{dest}")
    @Auth(role = "MANAGER")
    @WeaverValidation(method = { @WeaverValidation.Method(value = REORDER, model = CustomActionDefinition.class, params = { "0", "1" }) })
    public ApiResponse reorderCustomActions(@ApiVariable Long src, @ApiVariable Long dest) {
        logger.info("Reordering custom action definitions");
        customActionDefinitionRepo.reorder(src, dest);
        simpMessagingTemplate.convertAndSend("/channel/settings/custom-action", new ApiResponse(SUCCESS, customActionDefinitionRepo.findAllByOrderByPositionAsc()));
        return new ApiResponse(SUCCESS);
    }

}
