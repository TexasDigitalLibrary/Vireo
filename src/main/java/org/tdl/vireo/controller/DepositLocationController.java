package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;
import static edu.tamu.framework.enums.ApiResponseType.VALIDATION_WARNING;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.tdl.vireo.model.DepositLocation;
import org.tdl.vireo.model.repo.DepositLocationRepo;
import org.tdl.vireo.service.ValidationService;

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.ApiValidatedModel;
import edu.tamu.framework.aspect.annotation.ApiVariable;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.model.ApiResponse;
import edu.tamu.framework.validation.ModelBindingResult;

@Controller
@ApiMapping("/settings/deposit-locations")
public class DepositLocationController {

    private Logger logger = LoggerFactory.getLogger(this.getClass()); 

    @Autowired
    private DepositLocationRepo depositLocationRepo;
    
    @Autowired 
    private SimpMessagingTemplate simpMessagingTemplate;
    
    @Autowired
    private ValidationService validationService;
    
    @ApiMapping("/all")
    @Auth(role = "MANAGER")
    public ApiResponse allDepositLocations() {       
        return new ApiResponse(SUCCESS, depositLocationRepo.findAllByOrderByPositionAsc());
    }
    
    @ApiMapping("/create")
    @Auth(role = "MANAGER")
    public ApiResponse createDepositLocation(@ApiValidatedModel DepositLocation depositLocation) {
        
        // will attach any errors to the BindingResult when validating the incoming depositLocation
        depositLocation = depositLocationRepo.validateCreate(depositLocation);
        
        // build a response based on the BindingResult state
        ApiResponse response = validationService.buildResponse(depositLocation);
        
        switch(response.getMeta().getType()){
            case SUCCESS:
            case VALIDATION_INFO:
                logger.info("Creating deposit location with name " + depositLocation.getName());
                depositLocationRepo.create(depositLocation.getName(), depositLocation.getRepository(), depositLocation.getCollection(), depositLocation.getUsername(), depositLocation.getPassword(), depositLocation.getOnBehalfOf(), depositLocation.getPackager(), depositLocation.getDepositor());
                simpMessagingTemplate.convertAndSend("/channel/settings/deposit-locations", new ApiResponse(SUCCESS, depositLocationRepo.findAllByOrderByPositionAsc()));
                break;
            case VALIDATION_WARNING:
                simpMessagingTemplate.convertAndSend("/channel/settings/deposit-locations", new ApiResponse(VALIDATION_WARNING, depositLocationRepo.findAllByOrderByPositionAsc()));
                break;
            default:
                logger.warn("Couldn't create deposit location with name " + depositLocation.getName() + " because: " + response.getMeta().getType());
                break;
        }

        return response;
    }
    
    @ApiMapping("/update")
    @Auth(role = "MANAGER")
    public ApiResponse updateDepositLocation(@ApiValidatedModel DepositLocation depositLocation) {
        
        // will attach any errors to the BindingResult when validating the incoming depositLocation
        depositLocation = depositLocationRepo.validateUpdate(depositLocation);
        
        // build a response based on the BindingResult state
        ApiResponse response = validationService.buildResponse(depositLocation);
        
        switch(response.getMeta().getType()){
            case SUCCESS:
            case VALIDATION_INFO:
                logger.info("Updating deposit location with name " + depositLocation.getName());
                depositLocationRepo.save(depositLocation);
                simpMessagingTemplate.convertAndSend("/channel/settings/deposit-locations", new ApiResponse(SUCCESS, depositLocationRepo.findAllByOrderByPositionAsc()));
                break;
            case VALIDATION_WARNING:
                simpMessagingTemplate.convertAndSend("/channel/settings/deposit-locations", new ApiResponse(VALIDATION_WARNING, depositLocationRepo.findAllByOrderByPositionAsc()));
                break;
            default:
                logger.warn("Couldn't update deposit location with name " + depositLocation.getName() + " because: " + response.getMeta().getType());
                break;
        }

        return response;
    }

    @ApiMapping("/remove/{idString}")
    @Auth(role = "MANAGER")
    @Transactional
    public ApiResponse removeDepositLocation(@ApiVariable String idString) {
        
        // create a ModelBindingResult since we have an @ApiVariable coming in (and not a @ApiValidatedModel)
        ModelBindingResult modelBindingResult = new ModelBindingResult(idString, "deposit_location_id");
        
        // will attach any errors to the BindingResult when validating the incoming idString
        DepositLocation depositLocation = depositLocationRepo.validateRemove(idString, modelBindingResult);
        
        // build a response based on the BindingResult state
        ApiResponse response = validationService.buildResponse(modelBindingResult);
        
        switch(response.getMeta().getType()){
            case SUCCESS:
            case VALIDATION_INFO:
                logger.info("Removing deposit location with id " + idString);
                depositLocationRepo.remove(depositLocation);
                simpMessagingTemplate.convertAndSend("/channel/settings/deposit-locations", new ApiResponse(SUCCESS, depositLocationRepo.findAllByOrderByPositionAsc()));
                break;
            case VALIDATION_WARNING:
                simpMessagingTemplate.convertAndSend("/channel/settings/deposit-locations", new ApiResponse(VALIDATION_WARNING, depositLocationRepo.findAllByOrderByPositionAsc()));
                break;
            default:
                logger.warn("Couldn't remove deposit location with id " + idString + " because: " + response.getMeta().getType());
                break;
        }
        
        return response;
    }
    
    @ApiMapping("/reorder/{src}/{dest}")
    @Auth(role = "MANAGER")
    @Transactional
    public ApiResponse reorderDepositLocations(@ApiVariable String src, @ApiVariable String dest) {
        
        // create a ModelBindingResult since we have an @ApiVariable coming in (and not a @ApiValidatedModel)
        ModelBindingResult modelBindingResult = new ModelBindingResult(src, "depositLocation");
        
        // will attach any errors to the BindingResult when validating the incoming src, dest
        Long longSrc = validationService.validateLong(src, "position", modelBindingResult);
        Long longDest = validationService.validateLong(dest, "position", modelBindingResult);
        
        // build a response based on the BindingResult state
        ApiResponse response = validationService.buildResponse(modelBindingResult);
        
        switch(response.getMeta().getType()){
            case SUCCESS:
            case VALIDATION_INFO:
                logger.info("Reordering custom action definitions");
                depositLocationRepo.reorder(longSrc, longDest);
                simpMessagingTemplate.convertAndSend("/channel/settings/deposit-locations", new ApiResponse(SUCCESS, depositLocationRepo.findAllByOrderByPositionAsc()));
                break;
            case VALIDATION_WARNING:
                simpMessagingTemplate.convertAndSend("/channel/settings/deposit-locations", new ApiResponse(VALIDATION_WARNING, depositLocationRepo.findAllByOrderByPositionAsc()));
                break;
            default:
                logger.warn("Couldn't reorder custom action definitions because: " + response.getMeta().getType());
                break;
        }
        
        return response;
    }
    
}
