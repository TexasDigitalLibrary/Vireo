package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.ERROR;
import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaUpdate;
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
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @Autowired 
    private SimpMessagingTemplate simpMessagingTemplate;
    
    private Map<String, List<DepositLocation>> getAll() {
        Map<String, List<DepositLocation>> map = new HashMap<String, List<DepositLocation>>();
        map.put("list", depositLocationRepo.findAll());
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
            newDepositLocation.setDepositor(depositor.asText());
        }
        
        JsonNode packager = dataNode.get("packager");
        if(packager != null) {
            newDepositLocation.setPackager(packager.asText());
        }
        
        JsonNode repository = dataNode.get("repository");
        if(repository != null) {
            newDepositLocation.setRepository(repository.asText());
        }
        
        JsonNode timeout = dataNode.get("timeout");
        if(timeout != null) {
            newDepositLocation.setTimeout(timeout.asInt());
        }
        
        JsonNode username = dataNode.get("username");
        if(username != null) {
            newDepositLocation.setUsername(username.asText());
        }
        
        JsonNode password = dataNode.get("password");
        if(password != null) {
            newDepositLocation.setPassword(packager.asText());
        }
        
        JsonNode onBehalfOf = dataNode.get("onBehalfOf");
        if(onBehalfOf != null) {
            newDepositLocation.setOnBehalfOf(onBehalfOf.asText());
        }
        
        JsonNode collection = dataNode.get("collection");
        if(collection != null) {
            newDepositLocation.setCollection(collection.asText());
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
        Integer one =  new Integer(1);
        
        System.out.println("\n\nFORM: " + intFrom + "\n\n");
        System.out.println("\n\nTO: " + intTo + "\n\n");
        
        
        DepositLocation depositLocation = depositLocationRepo.findByOrder(intFrom);
        
        
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        
        CriteriaUpdate<DepositLocation> update = builder.createCriteriaUpdate(DepositLocation.class);
        
        Root<DepositLocation> root = update.from(DepositLocation.class);
                
        update.set("order", builder.sum(root.<Integer>get("order"), one));
        
        update.where(builder.greaterThanOrEqualTo(root.<Integer>get("order"), intTo));
        
        entityManager.createQuery(update).executeUpdate();
        
        
        depositLocation.setOrder(intTo);
        
        depositLocationRepo.save(depositLocation);
        
        return new ApiResponse(SUCCESS, getAll());
    }

}
