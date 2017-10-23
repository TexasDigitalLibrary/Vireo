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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tdl.vireo.model.CustomActionDefinition;
import org.tdl.vireo.model.repo.CustomActionDefinitionRepo;

import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.validation.aspect.annotation.WeaverValidatedModel;
import edu.tamu.weaver.validation.aspect.annotation.WeaverValidation;

@RestController
@RequestMapping("/settings/custom-action")
public class CustomActionSettingsController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CustomActionDefinitionRepo customActionDefinitionRepo;

    @RequestMapping("/all")
    public ApiResponse getCustomActions() {
        return new ApiResponse(SUCCESS, customActionDefinitionRepo.findAllByOrderByPositionAsc());
    }

    @PreAuthorize("hasRole('MANAGER')")
    @RequestMapping(value = "/create", method = POST)
    @WeaverValidation(business = { @WeaverValidation.Business(value = CREATE) })
    public ApiResponse createCustomAction(@WeaverValidatedModel CustomActionDefinition customActionDefinition) {
        logger.info("Creating custom action definition with label " + customActionDefinition.getLabel());
        return new ApiResponse(SUCCESS, customActionDefinitionRepo.create(customActionDefinition.getLabel(), customActionDefinition.isStudentVisible()));
    }

    @PreAuthorize("hasRole('MANAGER')")
    @RequestMapping(value = "/update", method = POST)
    @WeaverValidation(business = { @WeaverValidation.Business(value = UPDATE) })
    public ApiResponse updateCustomAction(@WeaverValidatedModel CustomActionDefinition customActionDefinition) {
        logger.info("Updating custom action definition with label " + customActionDefinition.getLabel());
        return new ApiResponse(SUCCESS, customActionDefinitionRepo.update(customActionDefinition));
    }

    @PreAuthorize("hasRole('MANAGER')")
    @RequestMapping(value = "/remove", method = POST)
    @WeaverValidation(business = { @WeaverValidation.Business(value = DELETE) })
    public ApiResponse removeCustomAction(@WeaverValidatedModel CustomActionDefinition customActionDefinition) {
        logger.info("Removing custom action definition with label " + customActionDefinition.getLabel());
        customActionDefinitionRepo.remove(customActionDefinition);
        return new ApiResponse(SUCCESS);
    }

    @RequestMapping("/reorder/{src}/{dest}")
    @PreAuthorize("hasRole('MANAGER')")
    @WeaverValidation(method = { @WeaverValidation.Method(value = REORDER, model = CustomActionDefinition.class, params = { "0", "1" }) })
    public ApiResponse reorderCustomActions(@PathVariable Long src, @PathVariable Long dest) {
        logger.info("Reordering custom action definitions");
        customActionDefinitionRepo.reorder(src, dest);
        return new ApiResponse(SUCCESS);
    }

}
