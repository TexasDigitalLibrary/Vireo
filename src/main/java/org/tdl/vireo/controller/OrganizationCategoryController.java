package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;
import static edu.tamu.framework.enums.BusinessValidationType.CREATE;
import static edu.tamu.framework.enums.BusinessValidationType.DELETE;
import static edu.tamu.framework.enums.BusinessValidationType.EXISTS;
import static edu.tamu.framework.enums.BusinessValidationType.NONEXISTS;
import static edu.tamu.framework.enums.BusinessValidationType.UPDATE;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.tdl.vireo.model.OrganizationCategory;
import org.tdl.vireo.model.repo.OrganizationCategoryRepo;

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.ApiValidatedModel;
import edu.tamu.framework.aspect.annotation.ApiValidation;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.model.ApiResponse;

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

    @ApiMapping("/create")
    @Auth(role = "MANAGER")
    @ApiValidation(business = { @ApiValidation.Business(value = CREATE), @ApiValidation.Business(value = EXISTS) })
    public ApiResponse createOrganizationCategory(@ApiValidatedModel OrganizationCategory organizationCategory) {
        logger.info("Creating organization category with name " + organizationCategory.getName());
        organizationCategory = organizationCategoryRepo.create(organizationCategory.getName());
        simpMessagingTemplate.convertAndSend("/channel/settings/organization-category", new ApiResponse(SUCCESS, organizationCategoryRepo.findAll()));
        return new ApiResponse(SUCCESS, organizationCategory);
    }

    @ApiMapping("/update")
    @Auth(role = "MANAGER")
    @ApiValidation(business = { @ApiValidation.Business(value = UPDATE), @ApiValidation.Business(value = NONEXISTS) })
    public ApiResponse updateOrganizationCategory(@ApiValidatedModel OrganizationCategory organizationCategory) {
        logger.info("Updating organization category with name " + organizationCategory.getName());
        organizationCategory = organizationCategoryRepo.save(organizationCategory);
        simpMessagingTemplate.convertAndSend("/channel/settings/organization-category", new ApiResponse(SUCCESS, organizationCategoryRepo.findAll()));
        return new ApiResponse(SUCCESS, organizationCategory);
    }

    @ApiMapping("/remove")
    @Auth(role = "MANAGER")
    @ApiValidation(business = { @ApiValidation.Business(value = DELETE, params = { "organizations" }), @ApiValidation.Business(value = NONEXISTS) })
    public ApiResponse removeOrganizationCategory(@ApiValidatedModel OrganizationCategory organizationCategory) {
        logger.info("Removing organization category with name " + organizationCategory.getName());
        organizationCategoryRepo.remove(organizationCategory);
        simpMessagingTemplate.convertAndSend("/channel/settings/organization-category", new ApiResponse(SUCCESS, organizationCategoryRepo.findAll()));
        return new ApiResponse(SUCCESS);
    }

}
