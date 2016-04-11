package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;
import static edu.tamu.framework.enums.ApiResponseType.VALIDATION_ERROR;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.ObjectError;
import org.tdl.vireo.model.GraduationMonth;
import org.tdl.vireo.model.repo.GraduationMonthRepo;

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.ApiValidatedModel;
import edu.tamu.framework.aspect.annotation.ApiVariable;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.model.ApiResponse;

@Controller
@ApiMapping("/settings/graduation-month")
public class GraduationMonthController {

    private Logger logger = LoggerFactory.getLogger(this.getClass()); 

    @Autowired
    private GraduationMonthRepo graduationMonthRepo;
    
    @Autowired 
    private SimpMessagingTemplate simpMessagingTemplate;
    
    private Map<String, List<GraduationMonth>> getAll() {
        Map<String, List<GraduationMonth>> map = new HashMap<String, List<GraduationMonth>>();
        map.put("list", graduationMonthRepo.findAllByOrderByPositionAsc());
        return map;
    }
    
    @ApiMapping("/all")
    @Auth(role = "ROLE_MANAGER")
    public ApiResponse allGraduationMonths() {       
        return new ApiResponse(SUCCESS, getAll());
    }
    
    @ApiMapping("/create")
    @Auth(role = "ROLE_MANAGER")
    public ApiResponse createGraduationMonth(@ApiValidatedModel GraduationMonth graduationMonth) {
        // TODO: this needs to go in repo.validateCreate() -- VIR-201
        if(!graduationMonth.getBindingResult().hasErrors() && graduationMonthRepo.findByMonth(graduationMonth.getMonth()) != null){
            graduationMonth.getBindingResult().addError(new ObjectError("graduationMonth", graduationMonth.getMonth() + " is already a graduation month!"));
        }
        
        if(graduationMonth.getBindingResult().hasErrors()) {
            return new ApiResponse(VALIDATION_ERROR, graduationMonth.getBindingResult().getAll());
        }
        
        GraduationMonth newGraduationMonth = graduationMonthRepo.create(graduationMonth.getMonth());
        
        //TODO: logging
        
        logger.info("Created graduation month with month " + newGraduationMonth.getMonth());
        
        simpMessagingTemplate.convertAndSend("/channel/settings/graduation-month", new ApiResponse(SUCCESS, getAll()));
        
        return new ApiResponse(SUCCESS);
    }
    
    @ApiMapping("/update")
    @Auth(role = "ROLE_MANAGER")
    public ApiResponse updateGraduationMonth(@ApiValidatedModel GraduationMonth graduationMonth) {
        // TODO: this needs to go in repo.validateUpdate() -- VIR-201
        GraduationMonth graduationMonthToUpdate = null;
        if(graduationMonth.getId() == null) {
            graduationMonth.getBindingResult().addError(new ObjectError("graduationMonth", "Cannot update a GraduationMonth without an id!"));
        } else {
            graduationMonthToUpdate = graduationMonthRepo.findOne(graduationMonth.getId());
            if(graduationMonthToUpdate == null) {
                graduationMonth.getBindingResult().addError(new ObjectError("graduationMonth", "Cannot update a GraduationMonth with an invalid id!"));
            }
        }
        
        if(graduationMonth.getBindingResult().hasErrors()) {
            return new ApiResponse(VALIDATION_ERROR, graduationMonth.getBindingResult().getAll());
        }
        
        //TODO: proper validation and response
        
        graduationMonthToUpdate.setMonth(graduationMonth.getMonth());
        graduationMonthToUpdate = graduationMonthRepo.save(graduationMonthToUpdate);
        
        //TODO: logging
        
        logger.info("Updated graduation month with month " + graduationMonthToUpdate.getMonth());
        
        simpMessagingTemplate.convertAndSend("/channel/settings/graduation-month", new ApiResponse(SUCCESS, getAll()));
        
        return new ApiResponse(SUCCESS);
    }

    @ApiMapping("/remove/{indexString}")
    @Auth(role = "ROLE_MANAGER")
    @Transactional
    public ApiResponse removeGraduationMonth(@ApiVariable String indexString) {        
        Long index = -1L;
        
        try {
            index = Long.parseLong(indexString);
        }
        catch(NumberFormatException nfe) {
            logger.info("\n\nNOT A NUMBER " + indexString + "\n\n");
            return new ApiResponse(VALIDATION_ERROR, "Id is not a valid deposit location order!");
        }
        
        if(index >= 0) {
            graduationMonthRepo.remove(index);
        }
        else {
            logger.info("\n\nINDEX" + index + "\n\n");
            return new ApiResponse(VALIDATION_ERROR, "Id is not a valid deposit location order!");
        }
        
        logger.info("Deleted deposit location with order " + index);
        
        simpMessagingTemplate.convertAndSend("/channel/settings/graduation-month", new ApiResponse(SUCCESS, getAll()));
        
        return new ApiResponse(SUCCESS);
    }
    
    @ApiMapping("/reorder/{src}/{dest}")
    @Auth(role = "ROLE_MANAGER")
    @Transactional
    public ApiResponse reorderGraduationMonths(@ApiVariable String src, @ApiVariable String dest) {
        Long intSrc = Long.parseLong(src);
        Long intDest = Long.parseLong(dest);
        graduationMonthRepo.reorder(intSrc, intDest);
        simpMessagingTemplate.convertAndSend("/channel/settings/graduation-month", new ApiResponse(SUCCESS, getAll()));        
        return new ApiResponse(SUCCESS);
    }
    
    @ApiMapping("/sort/{column}")
    @Auth(role = "ROLE_MANAGER")
    @Transactional
    public ApiResponse sortGraduationMonths(@ApiVariable String column) {
        graduationMonthRepo.sort(column);
        simpMessagingTemplate.convertAndSend("/channel/settings/graduation-month", new ApiResponse(SUCCESS, getAll()));        
        return new ApiResponse(SUCCESS);
    }

}
