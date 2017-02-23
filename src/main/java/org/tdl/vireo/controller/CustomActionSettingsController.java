package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;
import static edu.tamu.framework.enums.BusinessValidationType.CREATE;
import static edu.tamu.framework.enums.BusinessValidationType.DELETE;
import static edu.tamu.framework.enums.BusinessValidationType.EXISTS;
import static edu.tamu.framework.enums.BusinessValidationType.NONEXISTS;
import static edu.tamu.framework.enums.BusinessValidationType.UPDATE;
import static edu.tamu.framework.enums.MethodValidationType.REORDER;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.tdl.vireo.model.CustomActionDefinition;
import org.tdl.vireo.model.repo.CustomActionDefinitionRepo;

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.ApiValidatedModel;
import edu.tamu.framework.aspect.annotation.ApiValidation;
import edu.tamu.framework.aspect.annotation.ApiVariable;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.model.ApiResponse;

@Controller
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

    @ApiMapping("/create")
    @Auth(role = "MANAGER")
    @ApiValidation(business = { @ApiValidation.Business(value = CREATE), @ApiValidation.Business(value = EXISTS) })
    public ApiResponse createCustomAction(@ApiValidatedModel CustomActionDefinition customActionDefinition) {
        logger.info("Creating custom action definition with label " + customActionDefinition.getLabel());
        customActionDefinition = customActionDefinitionRepo.create(customActionDefinition.getLabel(), customActionDefinition.isStudentVisible());
        simpMessagingTemplate.convertAndSend("/channel/settings/custom-action", new ApiResponse(SUCCESS, customActionDefinitionRepo.findAllByOrderByPositionAsc()));
        return new ApiResponse(SUCCESS, customActionDefinition);
    }

    @ApiMapping("/update")
    @Auth(role = "MANAGER")
    @ApiValidation(business = { @ApiValidation.Business(value = UPDATE), @ApiValidation.Business(value = NONEXISTS) })
    public ApiResponse updateCustomAction(@ApiValidatedModel CustomActionDefinition customActionDefinition) {
        logger.info("Updating custom action definition with label " + customActionDefinition.getLabel());
        customActionDefinition = customActionDefinitionRepo.save(customActionDefinition);
        simpMessagingTemplate.convertAndSend("/channel/settings/custom-action", new ApiResponse(SUCCESS, customActionDefinitionRepo.findAllByOrderByPositionAsc()));
        return new ApiResponse(SUCCESS, customActionDefinition);
    }

    @ApiMapping("/remove")
    @Auth(role = "MANAGER")
    @ApiValidation(business = { @ApiValidation.Business(value = DELETE), @ApiValidation.Business(value = NONEXISTS) })
    public ApiResponse removeCustomAction(@ApiValidatedModel CustomActionDefinition customActionDefinition) {
        logger.info("Removing custom action definition with label " + customActionDefinition.getLabel());
        customActionDefinitionRepo.remove(customActionDefinition);
        simpMessagingTemplate.convertAndSend("/channel/settings/custom-action", new ApiResponse(SUCCESS, customActionDefinitionRepo.findAllByOrderByPositionAsc()));
        return new ApiResponse(SUCCESS);
    }

    @ApiMapping("/reorder/{src}/{dest}")
    @Auth(role = "MANAGER")
    @ApiValidation(method = { @ApiValidation.Method(value = REORDER, model = CustomActionDefinition.class, params = { "0", "1" }) })
    public ApiResponse reorderCustomActions(@ApiVariable Long src, @ApiVariable Long dest) {
        logger.info("Reordering custom action definitions");
        customActionDefinitionRepo.reorder(src, dest);
        simpMessagingTemplate.convertAndSend("/channel/settings/custom-action", new ApiResponse(SUCCESS, customActionDefinitionRepo.findAllByOrderByPositionAsc()));
        return new ApiResponse(SUCCESS);
    }

}
