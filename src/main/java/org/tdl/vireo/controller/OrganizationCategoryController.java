package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;
import static edu.tamu.framework.enums.ApiResponseType.VALIDATION_WARNING;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.ObjectError;
import org.tdl.vireo.model.OrganizationCategory;
import org.tdl.vireo.model.repo.OrganizationCategoryRepo;
import org.tdl.vireo.service.ValidationService;

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.ApiValidatedModel;
import edu.tamu.framework.aspect.annotation.ApiVariable;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.model.ApiResponse;
import edu.tamu.framework.validation.ModelBindingResult;

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
        return new ApiResponse(SUCCESS, getAll());
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
                simpMessagingTemplate.convertAndSend("/channel/settings/organization-category", new ApiResponse(SUCCESS, getAll()));
                break;
            case VALIDATION_WARNING:
                simpMessagingTemplate.convertAndSend("/channel/settings/organization-category", new ApiResponse(VALIDATION_WARNING, getAll()));
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
                simpMessagingTemplate.convertAndSend("/channel/settings/organization-category", new ApiResponse(SUCCESS, getAll()));
                break;
            case VALIDATION_WARNING:
                simpMessagingTemplate.convertAndSend("/channel/settings/organization-category", new ApiResponse(VALIDATION_WARNING, getAll()));
                break;
            default:
                logger.warn("Couldn't update organization category with name " + organizationCategory.getName() + " because: " + response.getMeta().getType());
                break;
        }

        return response;
    }

    @ApiMapping("/remove/{idString}")
    @Auth(role = "MANAGER")
    @Transactional
    public ApiResponse removeOrganizationCategory(@ApiVariable String idString) {
        
        // create a ModelBindingResult since we have an @ApiVariable coming in (and not a @ApiValidatedModel)
        ModelBindingResult modelBindingResult = new ModelBindingResult(idString, "organization_category_id");
        
        // will attach any errors to the BindingResult when validating the incoming idString
        OrganizationCategory organizationCategory = organizationCategoryRepo.validateRemove(idString, modelBindingResult);
        
        // build a response based on the BindingResult state
        ApiResponse response = validationService.buildResponse(modelBindingResult);
        
        switch(response.getMeta().getType()){
            case SUCCESS:
            case VALIDATION_INFO:
                logger.info("Removing organization category with id " + idString);
                try {
                    organizationCategoryRepo.remove(organizationCategory);
                    simpMessagingTemplate.convertAndSend("/channel/settings/organization-category", new ApiResponse(SUCCESS, getAll()));
                } catch(DataIntegrityViolationException e) {
                    modelBindingResult.addError(new ObjectError("organizationCategory", "Could not remove organization category " + organizationCategory.getName() + ", it's being used!"));
                    response = validationService.buildResponse(modelBindingResult);
                    logger.error("Couldn't remove organization category " + organizationCategory.getName() + " because: " + e.getLocalizedMessage());
                }
                break;
            case VALIDATION_WARNING:
                simpMessagingTemplate.convertAndSend("/channel/settings/organization-category", new ApiResponse(VALIDATION_WARNING, getAll()));
                break;
            default:
                logger.warn("Couldn't remove organization category with id " + idString + " because: " + response.getMeta().getType());
                break;
        }
        
        return response;
    }
    
    private Map<String,List<OrganizationCategory>> getAll() {
        Map<String,List<OrganizationCategory>> map = new HashMap<String,List<OrganizationCategory>>();
        map.put("list", organizationCategoryRepo.findAll());
        return map;
    }
}
