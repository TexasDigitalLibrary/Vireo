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
import org.tdl.vireo.model.DepositLocation;
import org.tdl.vireo.model.repo.DepositLocationRepo;

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.ApiValidatedModel;
import edu.tamu.framework.aspect.annotation.ApiVariable;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.model.ApiResponse;

@Controller
@ApiMapping("/settings/deposit-location")
public class DepositLocationController {

    private Logger logger = LoggerFactory.getLogger(this.getClass()); 

    @Autowired
    private DepositLocationRepo depositLocationRepo;
    
    @Autowired 
    private SimpMessagingTemplate simpMessagingTemplate;
    
    private Map<String, List<DepositLocation>> getAll() {
        Map<String, List<DepositLocation>> map = new HashMap<String, List<DepositLocation>>();
        map.put("list", depositLocationRepo.findAllByOrderByPositionAsc());
        return map;
    }
    
    @ApiMapping("/all")
    @Auth(role = "ROLE_MANAGER")
    public ApiResponse allDepositLocations() {       
        return new ApiResponse(SUCCESS, getAll());
    }
    
    @ApiMapping("/create")
    @Auth(role = "ROLE_MANAGER")
    public ApiResponse createDepositLocation(@ApiValidatedModel DepositLocation depositLocation) {
        
        // TODO: this needs to go in repo.validateCreate() -- VIR-201
        if(!depositLocation.getBindingResult().hasErrors() && depositLocationRepo.findByName(depositLocation.getName()) != null){
            depositLocation.getBindingResult().addError(new ObjectError("depositLocation", depositLocation.getName() + " is already a deposit location!"));
        }
        
        if(depositLocation.getBindingResult().hasErrors()){
            return new ApiResponse(VALIDATION_ERROR, depositLocation.getBindingResult().getAll());
        }
        
        DepositLocation newDepositLocation = depositLocationRepo.create(depositLocation.getName(), depositLocation.getRepository(), depositLocation.getCollection(), depositLocation.getUsername(), depositLocation.getPassword(), depositLocation.getOnBehalfOf(), depositLocation.getPackager(), depositLocation.getDepositor());
        
        //TODO: logging
        
        logger.info("Created deposit location with name " + newDepositLocation.getName());
        
        simpMessagingTemplate.convertAndSend("/channel/settings/deposit-location", new ApiResponse(SUCCESS, getAll()));
        
        return new ApiResponse(SUCCESS);
    }
    
    @ApiMapping("/update")
    @Auth(role = "ROLE_MANAGER")
    public ApiResponse updateDepositLocation(@ApiValidatedModel DepositLocation depositLocation) {
        
        //TODO: this needs to go in repo.validateUpdate() -- VIR-201
        DepositLocation depositLocationtoUpdate = null;
        if(depositLocation.getId() == null) {
            depositLocation.getBindingResult().addError(new ObjectError("depositLocation", "Cannot update a DepositLocation without an id!"));
        } else {
            depositLocationtoUpdate = depositLocationRepo.findOne(depositLocation.getId());
            if(depositLocationtoUpdate == null) {
                depositLocation.getBindingResult().addError(new ObjectError("depositLocation", "Cannot update a DepositLocation that doesn't exist!"));
            }
        }
        if(depositLocation.getBindingResult().hasErrors()){
            return new ApiResponse(VALIDATION_ERROR, depositLocation.getBindingResult().getAll());
        }
         
        depositLocationtoUpdate.setName(depositLocation.getName());
        depositLocationtoUpdate.setRepository(depositLocation.getRepository());
        depositLocationtoUpdate.setCollection(depositLocation.getCollection());
        depositLocationtoUpdate.setUsername(depositLocation.getUsername());
        depositLocationtoUpdate.setPassword(depositLocation.getPassword());
        depositLocationtoUpdate.setOnBehalfOf(depositLocation.getOnBehalfOf());
        depositLocationtoUpdate.setPackager(depositLocation.getPackager());
        depositLocationtoUpdate.setDepositor(depositLocation.getDepositor());
        depositLocationtoUpdate.setTimeout(depositLocation.getTimeout());
                
        depositLocationtoUpdate = depositLocationRepo.save(depositLocation);
        
        //TODO: logging
        
        logger.info("Created deposit location with name " + depositLocationtoUpdate.getName());
        
        simpMessagingTemplate.convertAndSend("/channel/settings/deposit-location", new ApiResponse(SUCCESS, getAll()));
        
        return new ApiResponse(SUCCESS);
    }

    @ApiMapping("/remove/{indexString}")
    @Auth(role = "ROLE_MANAGER")
    @Transactional
    public ApiResponse removeDepositLocation(@ApiVariable String indexString) {        
        Long index = -1L;
        
        try {
            index = Long.parseLong(indexString);
        }
        catch(NumberFormatException nfe) {
            logger.info("\n\nNOT A NUMBER " + indexString + "\n\n");
            return new ApiResponse(VALIDATION_ERROR, "Id is not a valid deposit location order!");
        }
        
        if(index >= 0) {               
            depositLocationRepo.remove(index);
        }
        else {
            logger.info("\n\nINDEX" + index + "\n\n");
            return new ApiResponse(VALIDATION_ERROR, "Id is not a valid deposit location order!");
        }
        
        logger.info("Deleted deposit location with order " + index);
        
        simpMessagingTemplate.convertAndSend("/channel/settings/deposit-location", new ApiResponse(SUCCESS, getAll()));
        
        return new ApiResponse(SUCCESS);
    }
    
    @ApiMapping("/reorder/{src}/{dest}")
    @Auth(role = "ROLE_MANAGER")
    @Transactional
    public ApiResponse reorderDepositLocations(@ApiVariable String src, @ApiVariable String dest) {
        Long intSrc = Long.parseLong(src);
        Long intDest = Long.parseLong(dest);
        depositLocationRepo.reorder(intSrc, intDest);
        simpMessagingTemplate.convertAndSend("/channel/settings/deposit-location", new ApiResponse(SUCCESS, getAll()));        
        return new ApiResponse(SUCCESS);
    }

}
