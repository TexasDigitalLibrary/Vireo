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
import org.tdl.vireo.model.GraduationMonth;
import org.tdl.vireo.model.repo.GraduationMonthRepo;
import org.tdl.vireo.service.ValidationService;

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.ApiValidatedModel;
import edu.tamu.framework.aspect.annotation.ApiVariable;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.model.ApiResponse;
import edu.tamu.framework.validation.ModelBindingResult;

@Controller
@ApiMapping("/settings/graduation-month")
public class GraduationMonthController {

    private Logger logger = LoggerFactory.getLogger(this.getClass()); 

    @Autowired
    private GraduationMonthRepo graduationMonthRepo;
    
    @Autowired 
    private SimpMessagingTemplate simpMessagingTemplate;
    
    @Autowired
    private ValidationService validationService;
    
    @ApiMapping("/all")
    @Auth(role = "MANAGER")
    public ApiResponse allGraduationMonths() {       
        return new ApiResponse(SUCCESS, getAll());
    }
    
    @ApiMapping("/create")
    @Auth(role = "MANAGER")
    public ApiResponse createGraduationMonth(@ApiValidatedModel GraduationMonth graduationMonth) {
        
        // will attach any errors to the BindingResult when validating the incoming graduationMonth
        graduationMonth = graduationMonthRepo.validateCreate(graduationMonth);
        
        // build a response based on the BindingResult state
        ApiResponse response = validationService.buildResponse(graduationMonth);
        
        switch(response.getMeta().getType()){
            case SUCCESS:
            case VALIDATION_INFO:
                logger.info("Creating graduation month with month " + graduationMonth.getMonth());
                graduationMonthRepo.create(graduationMonth.getMonth());
                simpMessagingTemplate.convertAndSend("/channel/settings/graduation-month", new ApiResponse(SUCCESS, getAll()));
                break;
            case VALIDATION_WARNING:
                simpMessagingTemplate.convertAndSend("/channel/settings/graduation-month", new ApiResponse(VALIDATION_WARNING, getAll()));
                break;
            default:
                logger.warn("Couldn't create graduation month with month " + graduationMonth.getMonth() + " because: " + response.getMeta().getType());
                break;
        }

        return response;
    }
    
    @ApiMapping("/update")
    @Auth(role = "MANAGER")
    public ApiResponse updateGraduationMonth(@ApiValidatedModel GraduationMonth graduationMonth) {
        
        // will attach any errors to the BindingResult when validating the incoming graduationMonth
        graduationMonth = graduationMonthRepo.validateUpdate(graduationMonth);
        
        // build a response based on the BindingResult state
        ApiResponse response = validationService.buildResponse(graduationMonth);
        
        switch(response.getMeta().getType()){
            case SUCCESS:
            case VALIDATION_INFO:
                logger.info("Updating graduation month with month " + graduationMonth.getMonth());
                graduationMonthRepo.save(graduationMonth);
                simpMessagingTemplate.convertAndSend("/channel/settings/graduation-month", new ApiResponse(SUCCESS, getAll()));
                break;
            case VALIDATION_WARNING:
                simpMessagingTemplate.convertAndSend("/channel/settings/graduation-month", new ApiResponse(VALIDATION_WARNING, getAll()));
                break;
            default:
                logger.warn("Couldn't update graduation month with month " + graduationMonth.getMonth() + " because: " + response.getMeta().getType());
                break;
        }

        return response;
    }

    @ApiMapping("/remove/{idString}")
    @Auth(role = "MANAGER")
    @Transactional
    public ApiResponse removeGraduationMonth(@ApiVariable String idString) {
        
        // create a ModelBindingResult since we have an @ApiVariable coming in (and not a @ApiValidatedModel)
        ModelBindingResult modelBindingResult = new ModelBindingResult(idString, "graduation_month_id");
        
        // will attach any errors to the BindingResult when validating the incoming idString
        GraduationMonth graduationMonth = graduationMonthRepo.validateRemove(idString, modelBindingResult);
        
        // build a response based on the BindingResult state
        ApiResponse response = validationService.buildResponse(modelBindingResult);
        
        switch(response.getMeta().getType()){
            case SUCCESS:
            case VALIDATION_INFO:
                logger.info("Removing graduation month with id " + idString);
                graduationMonthRepo.remove(graduationMonth);
                simpMessagingTemplate.convertAndSend("/channel/settings/graduation-month", new ApiResponse(SUCCESS, getAll()));
                break;
            case VALIDATION_WARNING:
                simpMessagingTemplate.convertAndSend("/channel/settings/graduation-month", new ApiResponse(VALIDATION_WARNING, getAll()));
                break;
            default:
                logger.warn("Couldn't remove graduation month with id " + idString + " because: " + response.getMeta().getType());
                break;
        }
        
        return response;
    }
    
    @ApiMapping("/reorder/{src}/{dest}")
    @Auth(role = "MANAGER")
    @Transactional
    public ApiResponse reorderGraduationMonths(@ApiVariable String src, @ApiVariable String dest) {
        
        // create a ModelBindingResult since we have an @ApiVariable coming in (and not a @ApiValidatedModel)
        ModelBindingResult modelBindingResult = new ModelBindingResult(src, "graduationMonth");
        
        // will attach any errors to the BindingResult when validating the incoming src, dest
        Long longSrc = validationService.validateLong(src, "position", modelBindingResult);
        Long longDest = validationService.validateLong(dest, "position", modelBindingResult);
        
        // build a response based on the BindingResult state
        ApiResponse response = validationService.buildResponse(modelBindingResult);
        
        switch(response.getMeta().getType()){
            case SUCCESS:
            case VALIDATION_INFO:
                logger.info("Reordering document types");
                graduationMonthRepo.reorder(longSrc, longDest);
                simpMessagingTemplate.convertAndSend("/channel/settings/graduation-month", new ApiResponse(SUCCESS, getAll()));
                break;
            case VALIDATION_WARNING:
                simpMessagingTemplate.convertAndSend("/channel/settings/graduation-month", new ApiResponse(VALIDATION_WARNING, getAll()));
                break;
            default:
                logger.warn("Couldn't reorder document types because: " + response.getMeta().getType());
                break;
        }
        
        return response;
    }
    
    @ApiMapping("/sort/{column}")
    @Auth(role = "MANAGER")
    @Transactional
    public ApiResponse sortGraduationMonths(@ApiVariable String column) {
        
        // create a ModelBindingResult since we have an @ApiVariable coming in (and not a @ApiValidatedModel)
        ModelBindingResult modelBindingResult = new ModelBindingResult(column, "graduationMonth");
        
        // will attach any errors to the BindingResult when validating the incoming column
        validationService.validateColumn(GraduationMonth.class, column, modelBindingResult);
        
        // build a response based on the BindingResult state
        ApiResponse response = validationService.buildResponse(modelBindingResult);
        
        switch(response.getMeta().getType()){
            case SUCCESS:
            case VALIDATION_INFO:
                logger.info("Sorting graduation months by " + column);
                graduationMonthRepo.sort(column);
                simpMessagingTemplate.convertAndSend("/channel/settings/graduation-month", new ApiResponse(SUCCESS, getAll()));
                break;
            case VALIDATION_WARNING:
                simpMessagingTemplate.convertAndSend("/channel/settings/graduation-month", new ApiResponse(VALIDATION_WARNING, getAll()));
                break;
            default:
                logger.warn("Couldn't sort graduation months because: " + response.getMeta().getType());
                break;
        }
        
        return response;
    }
    
    private Map<String, List<GraduationMonth>> getAll() {
        Map<String, List<GraduationMonth>> map = new HashMap<String, List<GraduationMonth>>();
        map.put("list", graduationMonthRepo.findAllByOrderByPositionAsc());
        return map;
    }
}
