package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.ERROR;
import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

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
    
    @ApiMapping("/create")
    @Auth(role = "ROLE_MANAGER")
    public ApiResponse createDepositLocation(@Data String data) {
        
        JsonNode dataNode;
        try {
            dataNode = objectMapper.readTree(data);
        } catch (IOException e) {
            return new ApiResponse(ERROR, "Unable to parse update json ["+e.getMessage()+"]");
        }

        System.out.println("\n\n" + dataNode + "\n\n");
        
        
        //TODO: proper validation and response
        
        DepositLocation newDepositLocation = depositLocationRepo.create(dataNode.get("name").asText());

        JsonNode depositor = dataNode.get("depositor");
        if(depositor != null) {
            String depositorString = depositor.asText();
            if(depositorString.length() > 0) 
                newDepositLocation.setDepositor(depositorString);
        }
        
        JsonNode packager = dataNode.get("packager");
        if(packager != null) {
            String packagerString = packager.asText();
            if(packagerString.length() > 0) 
                newDepositLocation.setPackager(packagerString);
        }
        
        JsonNode repository = dataNode.get("repository");
        if(repository != null) {
            String repositoryString = repository.asText();
            if(repositoryString.length() > 0) 
                newDepositLocation.setRepository(repositoryString);
        }
        
        JsonNode timeout = dataNode.get("timeout");
        if(timeout != null) {
            newDepositLocation.setTimeout(timeout.asInt());
        }
        
        JsonNode username = dataNode.get("username");
        if(username != null) {
            String usernameString = username.asText();
            if(usernameString.length() > 0) 
                newDepositLocation.setUsername(usernameString);
        }
        
        JsonNode password = dataNode.get("password");
        if(password != null) {
            String passwordString = password.asText();
            if(passwordString.length() > 0) 
                newDepositLocation.setPassword(passwordString);
        }
        
        JsonNode onBehalfOf = dataNode.get("onBehalfOf");
        if(onBehalfOf != null) {
            String onBehalfOfString = onBehalfOf.asText();
            if(onBehalfOfString.length() > 0) 
                newDepositLocation.setOnBehalfOf(onBehalfOfString);
        }
        
        JsonNode collection = dataNode.get("collection");
        if(collection != null) {
            String collectionString = collection.asText();
            if(collectionString.length() > 0) 
                newDepositLocation.setCollection(collectionString);
        }
        
        
        newDepositLocation.setOrder((int) depositLocationRepo.count());
        
        newDepositLocation = depositLocationRepo.save(newDepositLocation);
        
        //TODO: logging
        
        simpMessagingTemplate.convertAndSend("/channel/settings/deposit-location", new ApiResponse(SUCCESS, getAll()));
        
        return new ApiResponse(SUCCESS);
    }
    
    @ApiMapping("/reorder/{from}/{to}")
    @Auth(role = "ROLE_MANAGER")
    @Transactional
    public ApiResponse reorderDepositLocations(@ApiVariable String from, @ApiVariable String to) {
        Integer intFrom = Integer.parseInt(from);
        Integer intTo = Integer.parseInt(to);
        depositLocationRepo.reorder(intFrom, intTo);        
        simpMessagingTemplate.convertAndSend("/channel/settings/deposit-location", new ApiResponse(SUCCESS, getAll()));        
        return new ApiResponse(SUCCESS);
    }

}
