package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;
import static edu.tamu.framework.enums.ApiResponseType.VALIDATION_WARNING;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.tdl.vireo.enums.EmbargoGuarantor;
import org.tdl.vireo.model.Embargo;
import org.tdl.vireo.model.repo.EmbargoRepo;
import org.tdl.vireo.service.ValidationService;

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.ApiValidatedModel;
import edu.tamu.framework.aspect.annotation.ApiVariable;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.model.ApiResponse;
import edu.tamu.framework.validation.ModelBindingResult;

@Controller
@ApiMapping("/settings/embargo")
public class EmbargoController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private EmbargoRepo embargoRepo;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    
    @Autowired
    private ValidationService validationService;
    
    @ApiMapping("/all")
    @Auth(role = "MANAGER")
    public ApiResponse getEmbargoes() {
        return new ApiResponse(SUCCESS, embargoRepo.findAllByOrderByGuarantorAscPositionAsc());
    }

    @ApiMapping("/create")
    @Auth(role = "MANAGER")
    public ApiResponse createEmbargo(@ApiValidatedModel Embargo embargo) {

        // will attach any errors to the BindingResult when validating the incoming embargo
        embargo = embargoRepo.validateCreate(embargo);
        
        // build a response based on the BindingResult state
        ApiResponse response = validationService.buildResponse(embargo);
        
        switch(response.getMeta().getType()){
            case SUCCESS:
            case VALIDATION_INFO:
                logger.info("Creating embargo with name " + embargo.getName());
                embargoRepo.create(embargo.getName(), embargo.getDescription(), embargo.getDuration(), embargo.getGuarantor(), embargo.isActive());
                simpMessagingTemplate.convertAndSend("/channel/settings/embargo", new ApiResponse(SUCCESS, embargoRepo.findAllByOrderByGuarantorAscPositionAsc()));
                break;
            case VALIDATION_WARNING:
                simpMessagingTemplate.convertAndSend("/channel/settings/embargo", new ApiResponse(VALIDATION_WARNING, embargoRepo.findAllByOrderByGuarantorAscPositionAsc()));
                break;
            default:
                logger.warn("Couldn't create embargo with name " + embargo.getName() + " because: " + response.getMeta().getType());
                break;
        }

        return response;
    }

    @ApiMapping("/update")
    @Auth(role = "MANAGER")
    public ApiResponse updateEmbargo(@ApiValidatedModel Embargo embargo) {

        // will attach any errors to the BindingResult when validating the incoming embargo
        embargo = embargoRepo.validateUpdate(embargo);
        
        // build a response based on the BindingResult state
        ApiResponse response = validationService.buildResponse(embargo);
        
        switch(response.getMeta().getType()){
            case SUCCESS:
            case VALIDATION_INFO:
                logger.info("Updating embargo with name " + embargo.getName());
                embargoRepo.save(embargo);
                simpMessagingTemplate.convertAndSend("/channel/settings/embargo", new ApiResponse(SUCCESS, embargoRepo.findAllByOrderByGuarantorAscPositionAsc()));
                break;
            case VALIDATION_WARNING:
                simpMessagingTemplate.convertAndSend("/channel/settings/embargo", new ApiResponse(VALIDATION_WARNING, embargoRepo.findAllByOrderByGuarantorAscPositionAsc()));
                break;
            default:
                logger.warn("Couldn't update embargo with name " + embargo.getName() + " because: " + response.getMeta().getType());
                break;
        }

        return response;
    }

    @ApiMapping("/remove")
    @Auth(role = "MANAGER")
    @Transactional
    public ApiResponse removeEmbargo(@ApiValidatedModel Embargo embargo) {

        // will attach any errors to the BindingResult when validating the incoming idString
        embargo = embargoRepo.validateRemove(embargo);
        
        // build a response based on the BindingResult state
        ApiResponse response = validationService.buildResponse(embargo);
        
        switch(response.getMeta().getType()){
            case SUCCESS:
            case VALIDATION_INFO:
                logger.info("Removing Embargo:  " + embargo.getName());
                embargoRepo.remove(embargo);
                simpMessagingTemplate.convertAndSend("/channel/settings/embargo", new ApiResponse(SUCCESS, embargoRepo.findAllByOrderByGuarantorAscPositionAsc()));
                break;
            case VALIDATION_WARNING:
                simpMessagingTemplate.convertAndSend("/channel/settings/embargo", new ApiResponse(VALIDATION_WARNING, embargoRepo.findAllByOrderByGuarantorAscPositionAsc()));
                break;
            default:
                logger.warn("Couldn't remove embargo ("+embargo.getName()+") because: " + response.getMeta().getType());
                break;
        }
        
        return response;
    }

    @ApiMapping("/reorder/{guarantorString}/{src}/{dest}")
    @Auth(role = "MANAGER")
    @Transactional
    public ApiResponse reorderEmbargoes(@ApiVariable String guarantorString, @ApiVariable String src, @ApiVariable String dest) {
        
        // create a ModelBindingResult since we have an @ApiVariable coming in (and not a @ApiValidatedModel)
        ModelBindingResult modelBindingResult = new ModelBindingResult(guarantorString, "guarantor");
        
        // will attach any errors to the BindingResult when validating the incoming src, dest, or guarantor
        Long longSrc = validationService.validateLong(src, "position", modelBindingResult);
        Long longDest = validationService.validateLong(dest, "position", modelBindingResult);
        EmbargoGuarantor guarantor = embargoRepo.validateGuarantor(guarantorString, modelBindingResult);
        
        // build a response based on the BindingResult state
        ApiResponse response = validationService.buildResponse(modelBindingResult);
        
        switch(response.getMeta().getType()){
            case SUCCESS:
            case VALIDATION_INFO:
                logger.info("Reordering Embargoes with guarantor " + guarantor);
                embargoRepo.reorder(longSrc, longDest, guarantor);
                simpMessagingTemplate.convertAndSend("/channel/settings/embargo", new ApiResponse(SUCCESS, embargoRepo.findAllByOrderByGuarantorAscPositionAsc()));
                break;
            case VALIDATION_WARNING:
                simpMessagingTemplate.convertAndSend("/channel/settings/embargo", new ApiResponse(VALIDATION_WARNING, embargoRepo.findAllByOrderByGuarantorAscPositionAsc()));
                break;
            default:
                logger.warn("Couldn't reorder embargoes with guarantor " + guarantor + " because: " + response.getMeta().getType());
                break;
        }
        
        return response;
    }

    @ApiMapping("/sort/{guarantorString}/{column}")
    @Auth(role = "MANAGER")
    @Transactional
    public ApiResponse sortEmbargoes(@ApiVariable String guarantorString, @ApiVariable String column) {
        
        // create a ModelBindingResult since we have an @ApiVariable coming in (and not a @ApiValidatedModel)
        ModelBindingResult modelBindingResult = new ModelBindingResult(guarantorString, "guarantor");
        
        // will attach any errors to the BindingResult when validating the incoming column and guarantorString
        validationService.validateColumn(Embargo.class, column, modelBindingResult);
        EmbargoGuarantor guarantor = embargoRepo.validateGuarantor(guarantorString, modelBindingResult);
        
        // build a response based on the BindingResult state
        ApiResponse response = validationService.buildResponse(modelBindingResult);
        
        switch(response.getMeta().getType()){
            case SUCCESS:
            case VALIDATION_INFO:
                logger.info("Sorting Embargoes with guarantor " + guarantor + " by " + column);
                embargoRepo.sort(column, guarantor);
                simpMessagingTemplate.convertAndSend("/channel/settings/embargo", new ApiResponse(SUCCESS, embargoRepo.findAllByOrderByGuarantorAscPositionAsc()));
                break;
            case VALIDATION_WARNING:
                simpMessagingTemplate.convertAndSend("/channel/settings/embargo", new ApiResponse(VALIDATION_WARNING, embargoRepo.findAllByOrderByGuarantorAscPositionAsc()));
                break;
            default:
                logger.warn("Couldn't sort embargoes with guarantor " + guarantor + " because: " + response.getMeta().getType());
                break;
        }
        
        return response;
    }
    
}
