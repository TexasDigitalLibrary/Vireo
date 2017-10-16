package org.tdl.vireo.controller;

import static edu.tamu.weaver.response.ApiStatus.SUCCESS;
import static edu.tamu.weaver.validation.model.BusinessValidationType.CREATE;
import static edu.tamu.weaver.validation.model.BusinessValidationType.DELETE;
import static edu.tamu.weaver.validation.model.BusinessValidationType.UPDATE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.tdl.vireo.model.OrganizationCategory;
import org.tdl.vireo.model.repo.OrganizationCategoryRepo;

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.validation.aspect.annotation.WeaverValidatedModel;
import edu.tamu.weaver.validation.aspect.annotation.WeaverValidation;

@Controller
@ApiMapping("/settings/organization-category")
public class OrganizationCategoryController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private OrganizationCategoryRepo organizationCategoryRepo;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Transactional
    @ApiMapping("/all")
    @Auth(role = "MANAGER")
    public ApiResponse getOrganizationCategories() {
        return new ApiResponse(SUCCESS, organizationCategoryRepo.findAll());
    }

    @Auth(role = "MANAGER")
    @ApiMapping(value = "/create", method = POST)
    @WeaverValidation(business = { @WeaverValidation.Business(value = CREATE) })
    public ApiResponse createOrganizationCategory(@WeaverValidatedModel OrganizationCategory organizationCategory) {
        logger.info("Creating organization category with name " + organizationCategory.getName());
        organizationCategory = organizationCategoryRepo.create(organizationCategory.getName());
        simpMessagingTemplate.convertAndSend("/channel/settings/organization-category", new ApiResponse(SUCCESS, organizationCategoryRepo.findAll()));
        return new ApiResponse(SUCCESS, organizationCategory);
    }

    @Auth(role = "MANAGER")
    @ApiMapping(value = "/update", method = POST)
    @WeaverValidation(business = { @WeaverValidation.Business(value = UPDATE) })
    public ApiResponse updateOrganizationCategory(@WeaverValidatedModel OrganizationCategory organizationCategory) {
        logger.info("Updating organization category with name " + organizationCategory.getName());
        organizationCategory = organizationCategoryRepo.save(organizationCategory);
        simpMessagingTemplate.convertAndSend("/channel/settings/organization-category", new ApiResponse(SUCCESS, organizationCategoryRepo.findAll()));
        return new ApiResponse(SUCCESS, organizationCategory);
    }

    @Auth(role = "MANAGER")
    @ApiMapping(value = "/remove", method = POST)
    @WeaverValidation(business = { @WeaverValidation.Business(value = DELETE, params = { "organizations" }) })
    public ApiResponse removeOrganizationCategory(@WeaverValidatedModel OrganizationCategory organizationCategory) {
        logger.info("Removing organization category with name " + organizationCategory.getName());
        organizationCategoryRepo.remove(organizationCategory);
        simpMessagingTemplate.convertAndSend("/channel/settings/organization-category", new ApiResponse(SUCCESS, organizationCategoryRepo.findAll()));
        return new ApiResponse(SUCCESS);
    }

}
