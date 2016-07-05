package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;
import static edu.tamu.framework.enums.ApiResponseType.VALIDATION_WARNING;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.ObjectError;
import org.tdl.vireo.model.OrganizationCategory;
import org.tdl.vireo.model.repo.OrganizationCategoryRepo;
import org.tdl.vireo.service.ValidationService;

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.ApiValidatedModel;
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
    
    @Autowired
    private ValidationService validationService;

    @ApiMapping("/all")
    @Auth(role="MANAGER")
    @Transactional
    public ApiResponse getOrganizationCategories() {
        return new ApiResponse(SUCCESS, organizationCategoryRepo.findAll());
    }
    
    @ApiMapping("/create")
    @Auth(role = "MANAGER")
    @Transactional
    public ApiResponse createOrganizationCategory(@ApiValidatedModel OrganizationCategory organizationCategory) {
        
        // will attach any errors to the BindingResult when validating the incoming organizationCategory
        organizationCategory = organizationCategoryRepo.validateCreate(organizationCategory);
        
        // build a response based on the BindingResult state
        ApiResponse response = validationService.buildResponse(organizationCategory);
        
        switch(response.getMeta().getType()){
            case SUCCESS:
            case VALIDATION_INFO:
                logger.info("Creating organization category with name " + organizationCategory.getName());
                organizationCategoryRepo.create(organizationCategory.getName());
                simpMessagingTemplate.convertAndSend("/channel/settings/organization-category", new ApiResponse(SUCCESS, organizationCategoryRepo.findAll()));
                break;
            case VALIDATION_WARNING:
                simpMessagingTemplate.convertAndSend("/channel/settings/organization-category", new ApiResponse(VALIDATION_WARNING, organizationCategoryRepo.findAll()));
                break;
            default:
                logger.warn("Couldn't create organization category with name " + organizationCategory.getName() + " because: " + response.getMeta().getType());
                break;
        }

        return response;
    }
    
    @ApiMapping("/update")
    @Auth(role = "MANAGER")
    @Transactional
    public ApiResponse updateOrganizationCategory(@ApiValidatedModel OrganizationCategory organizationCategory) {
        
        // will attach any errors to the BindingResult when validating the incoming organizationCategory
        organizationCategory = organizationCategoryRepo.validateUpdate(organizationCategory);
        
        // build a response based on the BindingResult state
        ApiResponse response = validationService.buildResponse(organizationCategory);
        
        switch(response.getMeta().getType()){
            case SUCCESS:
            case VALIDATION_INFO:
                logger.info("Updating organization category with name " + organizationCategory.getName());
                organizationCategoryRepo.save(organizationCategory);
                simpMessagingTemplate.convertAndSend("/channel/settings/organization-category", new ApiResponse(SUCCESS, organizationCategoryRepo.findAll()));
                break;
            case VALIDATION_WARNING:
                simpMessagingTemplate.convertAndSend("/channel/settings/organization-category", new ApiResponse(VALIDATION_WARNING, organizationCategoryRepo.findAll()));
                break;
            default:
                logger.warn("Couldn't update organization category with name " + organizationCategory.getName() + " because: " + response.getMeta().getType());
                break;
        }

        return response;
    }

    @ApiMapping("/remove")
    @Auth(role = "MANAGER")
    @Transactional
    public ApiResponse removeOrganizationCategory(@ApiValidatedModel OrganizationCategory organizationCategory) {
        
        // build a response based on the BindingResult state
        ApiResponse response = validationService.buildResponse(organizationCategory);
        
        switch(response.getMeta().getType()){
            case SUCCESS:
            case VALIDATION_INFO:
                logger.info("Removing organization category with name " + organizationCategory.getName());
                if(organizationCategory.getOrganizations().size() == 0) {
                    organizationCategoryRepo.remove(organizationCategory);
                    simpMessagingTemplate.convertAndSend("/channel/settings/organization-category", new ApiResponse(SUCCESS, organizationCategoryRepo.findAll()));
                }
                else {
                    organizationCategory.getBindingResult().addError(new ObjectError("organizationCategory", "Could not remove organization category " + organizationCategory.getName() + ", it's being used!"));
                    response = validationService.buildResponse(organizationCategory);
                    logger.error("Couldn't remove organization category " + organizationCategory.getName() + " because: is the category of " + organizationCategory.getOrganizations().size() + " organizations!");
                }
                break;
            case VALIDATION_WARNING:
                simpMessagingTemplate.convertAndSend("/channel/settings/organization-category", new ApiResponse(VALIDATION_WARNING, organizationCategoryRepo.findAll()));
                break;
            default:
                logger.warn("Couldn't remove organization category with name " + organizationCategory.getName() + " because: " + response.getMeta().getType());
                break;
        }
        
        return response;
    }
    

}
