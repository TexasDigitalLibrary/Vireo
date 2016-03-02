package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.ERROR;
import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.tdl.vireo.model.GraduationMonth;
import org.tdl.vireo.model.repo.GraduationMonthRepo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.ApiVariable;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.aspect.annotation.Data;
import edu.tamu.framework.model.ApiResponse;

@Controller
@ApiMapping("/settings/graduation-month")
public class GraduationMonthController {

    private final Logger logger = Logger.getLogger(this.getClass());

    @Autowired
    private GraduationMonthRepo graduationMonthRepo;

    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired 
    private SimpMessagingTemplate simpMessagingTemplate;
    
    private Map<String, List<GraduationMonth>> getAll() {
        Map<String, List<GraduationMonth>> map = new HashMap<String, List<GraduationMonth>>();
        map.put("list", graduationMonthRepo.findAllByOrderByOrderAsc());
        return map;
    }
    
    @ApiMapping("/all")
    @Auth(role = "ROLE_MANAGER")
    public ApiResponse allGraduationMonths() {       
        return new ApiResponse(SUCCESS, getAll());
    }
    
    private GraduationMonth validateAndPopulateGraduationMonth(GraduationMonth graduationMonth, JsonNode dataNode) {
       
        return graduationMonth;
    }
    
    @ApiMapping("/create")
    @Auth(role = "ROLE_MANAGER")
    public ApiResponse createGraduationMonth(@Data String data) {
        
        JsonNode dataNode;
        try {
            dataNode = objectMapper.readTree(data);
        } catch (IOException e) {
            return new ApiResponse(ERROR, "Unable to parse update json ["+e.getMessage()+"]");
        }
        
        //TODO: proper validation and response
        
        GraduationMonth newGraduationMonth = null;
                
        JsonNode month = dataNode.get("month");
        if(month != null) {
            newGraduationMonth = graduationMonthRepo.create(month.asInt());           
        }
        else {
            return new ApiResponse(ERROR, "Month required to create graduation month!");
        }
        
        newGraduationMonth = validateAndPopulateGraduationMonth(newGraduationMonth, dataNode);
        
        
        newGraduationMonth.setOrder((int) graduationMonthRepo.count());
        
        newGraduationMonth = graduationMonthRepo.save(newGraduationMonth);
        
        //TODO: logging
        
        logger.info("Created graduation month with month " + newGraduationMonth.getMonth());
        
        simpMessagingTemplate.convertAndSend("/channel/settings/graduation-month", new ApiResponse(SUCCESS, getAll()));
        
        return new ApiResponse(SUCCESS);
    }
    
    @ApiMapping("/update")
    @Auth(role = "ROLE_MANAGER")
    public ApiResponse updateGraduationMonth(@Data String data) {
        
        JsonNode dataNode;
        try {
            dataNode = objectMapper.readTree(data);
        } catch (IOException e) {
            return new ApiResponse(ERROR, "Unable to parse update json ["+e.getMessage()+"]");
        }
        
        //TODO: proper validation and response
        
        //System.out.println("\n\n" + dataNode + "\n\n");
        
        GraduationMonth graduationMonth = null;
                
        JsonNode id = dataNode.get("id");
        if(id != null) {
            Long idLong = -1L;
            try {
                idLong = id.asLong();
            }
            catch(NumberFormatException nfe) {
                return new ApiResponse(ERROR, "Id required to update graduation month!");
            }
            graduationMonth = graduationMonthRepo.findOne(idLong);           
        }
        else {
            return new ApiResponse(ERROR, "Id required to update graduation month!");
        }
        
        JsonNode month = dataNode.get("month");
        if(month != null) {
            graduationMonth.setMonth(month.asInt());           
        }
        else {
            return new ApiResponse(ERROR, "Month required to create graduation month!");
        }
        
        graduationMonth = validateAndPopulateGraduationMonth(graduationMonth, dataNode);
                
        graduationMonth = graduationMonthRepo.save(graduationMonth);
        
        //TODO: logging
        
        logger.info("Created graduation month with month " + graduationMonth.getMonth());
        
        simpMessagingTemplate.convertAndSend("/channel/settings/graduation-month", new ApiResponse(SUCCESS, getAll()));
        
        return new ApiResponse(SUCCESS);
    }

    @ApiMapping("/remove/{indexString}")
    @Auth(role = "ROLE_MANAGER")
    @Transactional
    public ApiResponse removeGraduationMonth(@ApiVariable String indexString) {        
        Integer index = -1;
        
        try {
            index = Integer.parseInt(indexString);
        }
        catch(NumberFormatException nfe) {
            logger.info("\n\nNOT A NUMBER " + indexString + "\n\n");
            return new ApiResponse(ERROR, "Id is not a valid deposit location order!");
        }
        
        if(index >= 0) {               
            graduationMonthRepo.remove(index);
        }
        else {
            logger.info("\n\nINDEX" + index + "\n\n");
            return new ApiResponse(ERROR, "Id is not a valid deposit location order!");
        }
        
        logger.info("Deleted deposit location with order " + index);
        
        simpMessagingTemplate.convertAndSend("/channel/settings/graduation-month", new ApiResponse(SUCCESS, getAll()));
        
        return new ApiResponse(SUCCESS);
    }
    
    @ApiMapping("/reorder/{src}/{dest}")
    @Auth(role = "ROLE_MANAGER")
    @Transactional
    public ApiResponse reorderGraduationMonths(@ApiVariable String src, @ApiVariable String dest) {
        Integer intSrc = Integer.parseInt(src);
        Integer intDest = Integer.parseInt(dest);
        graduationMonthRepo.reorder(intSrc, intDest);
        simpMessagingTemplate.convertAndSend("/channel/settings/graduation-month", new ApiResponse(SUCCESS, getAll()));        
        return new ApiResponse(SUCCESS);
    }
    
    @ApiMapping("/sort")
    @Auth(role = "ROLE_MANAGER")
    @Transactional
    public ApiResponse sortGraduationMonths() {
        graduationMonthRepo.sort();
        simpMessagingTemplate.convertAndSend("/channel/settings/graduation-month", new ApiResponse(SUCCESS, getAll()));        
        return new ApiResponse(SUCCESS);
    }

}
