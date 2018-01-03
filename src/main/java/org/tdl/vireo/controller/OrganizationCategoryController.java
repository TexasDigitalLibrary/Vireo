package org.tdl.vireo.controller;

import static edu.tamu.weaver.response.ApiStatus.SUCCESS;
import static edu.tamu.weaver.validation.model.BusinessValidationType.CREATE;
import static edu.tamu.weaver.validation.model.BusinessValidationType.DELETE;
import static edu.tamu.weaver.validation.model.BusinessValidationType.UPDATE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tdl.vireo.model.OrganizationCategory;
import org.tdl.vireo.model.repo.OrganizationCategoryRepo;

import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.validation.aspect.annotation.WeaverValidatedModel;
import edu.tamu.weaver.validation.aspect.annotation.WeaverValidation;

@RestController
@RequestMapping("/settings/organization-category")
public class OrganizationCategoryController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private OrganizationCategoryRepo organizationCategoryRepo;

    @Transactional
    @RequestMapping("/all")
    @PreAuthorize("hasRole('REVIEWER')")
    public ApiResponse getOrganizationCategories() {
        return new ApiResponse(SUCCESS, organizationCategoryRepo.findAll());
    }

    @PreAuthorize("hasRole('MANAGER')")
    @RequestMapping(value = "/create", method = POST)
    @WeaverValidation(business = { @WeaverValidation.Business(value = CREATE) })
    public ApiResponse createOrganizationCategory(@WeaverValidatedModel OrganizationCategory organizationCategory) {
        logger.info("Creating organization category with name " + organizationCategory.getName());
        return new ApiResponse(SUCCESS, organizationCategoryRepo.create(organizationCategory.getName()));
    }

    @PreAuthorize("hasRole('MANAGER')")
    @RequestMapping(value = "/update", method = POST)
    @WeaverValidation(business = { @WeaverValidation.Business(value = UPDATE) })
    public ApiResponse updateOrganizationCategory(@WeaverValidatedModel OrganizationCategory organizationCategory) {
        logger.info("Updating organization category with name " + organizationCategory.getName());
        return new ApiResponse(SUCCESS, organizationCategoryRepo.update(organizationCategory));
    }

    @PreAuthorize("hasRole('MANAGER')")
    @RequestMapping(value = "/remove", method = POST)
    @WeaverValidation(business = { @WeaverValidation.Business(value = DELETE, params = { "organizations" }) })
    public ApiResponse removeOrganizationCategory(@WeaverValidatedModel OrganizationCategory organizationCategory) {
        logger.info("Removing organization category with name " + organizationCategory.getName());
        organizationCategoryRepo.delete(organizationCategory);
        return new ApiResponse(SUCCESS);
    }

}
