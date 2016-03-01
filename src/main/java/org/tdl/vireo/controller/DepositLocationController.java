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
import org.tdl.vireo.model.DepositLocation;
import org.tdl.vireo.model.repo.DepositLocationRepo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.ApiVariable;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.aspect.annotation.Data;
import edu.tamu.framework.model.ApiResponse;

@Controller
@ApiMapping("/settings/deposit-location")
public class DepositLocationController {

    private final Logger logger = Logger.getLogger(this.getClass());

    @Autowired
    private DepositLocationRepo depositLocationRepo;

    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired 
    private SimpMessagingTemplate simpMessagingTemplate;
    
    private Map<String, List<DepositLocation>> getAll() {
        Map<String, List<DepositLocation>> map = new HashMap<String, List<DepositLocation>>();
        map.put("list", depositLocationRepo.findAllByOrderByOrderAsc());
        return map;
    }
    
    @ApiMapping("/all")
    @Auth(role = "ROLE_MANAGER")
    public ApiResponse allDepositLocations() {       
        return new ApiResponse(SUCCESS, getAll());
    }
    
    private DepositLocation validateAndPopulateDepositLocation(DepositLocation depositLocation, JsonNode dataNode) {
        JsonNode depositor = dataNode.get("depositor");
        if(depositor != null) {
            String depositorString = depositor.asText();
            if(depositorString.length() > 0) 
                depositLocation.setDepositor(depositorString);
        }
        
        JsonNode packager = dataNode.get("packager");
        if(packager != null) {
            String packagerString = packager.asText();
            if(packagerString.length() > 0) 
                depositLocation.setPackager(packagerString);
        }
        
        JsonNode repository = dataNode.get("repository");
        if(repository != null) {
            String repositoryString = repository.asText();
            if(repositoryString.length() > 0) 
                depositLocation.setRepository(repositoryString);
        }
        
        JsonNode timeout = dataNode.get("timeout");
        if(timeout != null) {
            depositLocation.setTimeout(timeout.asInt());
        }
        
        JsonNode username = dataNode.get("username");
        if(username != null) {
            String usernameString = username.asText();
            if(usernameString.length() > 0) 
                depositLocation.setUsername(usernameString);
        }
        
        JsonNode password = dataNode.get("password");
        if(password != null) {
            String passwordString = password.asText();
            if(passwordString.length() > 0) 
                depositLocation.setPassword(passwordString);
        }
        
        JsonNode onBehalfOf = dataNode.get("onBehalfOf");
        if(onBehalfOf != null) {
            String onBehalfOfString = onBehalfOf.asText();
            if(onBehalfOfString.length() > 0) 
                depositLocation.setOnBehalfOf(onBehalfOfString);
        }
        
        JsonNode collection = dataNode.get("collection");
        if(collection != null) {
            String collectionString = collection.asText();
            if(collectionString.length() > 0) 
                depositLocation.setCollection(collectionString);
        }
        return depositLocation;
    }
    
    @ApiMapping("/create")
    @Auth(role = "ROLE_MANAGER")
    public ApiResponse createDepositLocation(@Data String data) {
        
        JsonNode dataNode;
        try {
            dataNode = objectMapper.readTree(data);
        } catch (IOException e) {
            return new ApiResponse(ERROR, "Unable to parse update json ["+e.getMessage()+"]");
        }
        
        //TODO: proper validation and response
        
        DepositLocation newDepositLocation = null;
                
        JsonNode name = dataNode.get("name");
        if(name != null) {
            String nameString = name.asText();
            if(nameString.length() > 0) {
                newDepositLocation = depositLocationRepo.create(nameString);
            }
            else {
                return new ApiResponse(ERROR, "Name required to create deposit location!");
            }
        }
        else {
            return new ApiResponse(ERROR, "Name required to create deposit location!");
        }
        
        newDepositLocation = validateAndPopulateDepositLocation(newDepositLocation, dataNode);
        
        
        newDepositLocation.setOrder((int) depositLocationRepo.count());
        
        newDepositLocation = depositLocationRepo.save(newDepositLocation);
        
        //TODO: logging
        
        logger.info("Created deposit location with name " + newDepositLocation.getName());
        
        simpMessagingTemplate.convertAndSend("/channel/settings/deposit-location", new ApiResponse(SUCCESS, getAll()));
        
        return new ApiResponse(SUCCESS);
    }
    
    @ApiMapping("/update")
    @Auth(role = "ROLE_MANAGER")
    public ApiResponse updateDepositLocation(@Data String data) {
        
        JsonNode dataNode;
        try {
            dataNode = objectMapper.readTree(data);
        } catch (IOException e) {
            return new ApiResponse(ERROR, "Unable to parse update json ["+e.getMessage()+"]");
        }
        
        //TODO: proper validation and response
        
        //System.out.println("\n\n" + dataNode + "\n\n");
        
        DepositLocation depositLocation = null;
                
        JsonNode id = dataNode.get("id");
        if(id != null) {
            Long idLong = -1L;
            try {
                idLong = id.asLong();
            }
            catch(NumberFormatException nfe) {
                return new ApiResponse(ERROR, "Id required to update deposit location!");
            }
            depositLocation = depositLocationRepo.findOne(idLong);           
        }
        else {
            return new ApiResponse(ERROR, "Id required to update deposit location!");
        }
        
        JsonNode name = dataNode.get("name");
        if(name != null) {
            String nameString = name.asText();
            if(nameString != null && nameString.length() > 0) 
                depositLocation.setName(nameString);
        }
        
        depositLocation = validateAndPopulateDepositLocation(depositLocation, dataNode);
                
        depositLocation = depositLocationRepo.save(depositLocation);
        
        //TODO: logging
        
        logger.info("Created deposit location with name " + depositLocation.getName());
        
        simpMessagingTemplate.convertAndSend("/channel/settings/deposit-location", new ApiResponse(SUCCESS, getAll()));
        
        return new ApiResponse(SUCCESS);
    }

    @ApiMapping("/remove/{indexString}")
    @Auth(role = "ROLE_MANAGER")
    @Transactional
    public ApiResponse removeDepositLocation(@ApiVariable String indexString) {        
        Integer index = -1;
        
        try {
            index = Integer.parseInt(indexString);
        }
        catch(NumberFormatException nfe) {
            logger.info("\n\nNOT A NUMBER " + indexString + "\n\n");
            return new ApiResponse(ERROR, "Id is not a valid deposit location order!");
        }
        
        if(index >= 0) {               
            depositLocationRepo.remove(index);
        }
        else {
            logger.info("\n\nINDEX" + index + "\n\n");
            return new ApiResponse(ERROR, "Id is not a valid deposit location order!");
        }
        
        logger.info("Deleted deposit location with order " + index);
        
        simpMessagingTemplate.convertAndSend("/channel/settings/deposit-location", new ApiResponse(SUCCESS, getAll()));
        
        return new ApiResponse(SUCCESS);
    }
    
    @ApiMapping("/reorder/{src}/{dest}")
    @Auth(role = "ROLE_MANAGER")
    @Transactional
    public ApiResponse reorderDepositLocations(@ApiVariable String src, @ApiVariable String dest) {
        Integer intSrc = Integer.parseInt(src);
        Integer intDest = Integer.parseInt(dest);
        depositLocationRepo.reorder(intSrc, intDest);
        simpMessagingTemplate.convertAndSend("/channel/settings/deposit-location", new ApiResponse(SUCCESS, getAll()));        
        return new ApiResponse(SUCCESS);
    }

}
