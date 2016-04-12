package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.ERROR;
import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.tdl.vireo.model.OrganizationCategory;
import org.tdl.vireo.model.repo.OrganizationCategoryRepo;
import org.tdl.vireo.model.repo.OrganizationRepo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.ApiVariable;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.aspect.annotation.Data;
import edu.tamu.framework.model.ApiResponse;

@Controller
@ApiMapping("/settings/organization-category")
public class OrganizationCategoryController {
    
    private Logger logger = LoggerFactory.getLogger(this.getClass()); 
    
    @Autowired
    private OrganizationRepo organizationRepo;

    @Autowired
    private OrganizationCategoryRepo organizationCategoryRepo;

    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired 
    private SimpMessagingTemplate simpMessagingTemplate;
        

    private Map<String,List<OrganizationCategory>> getAllHelper() {
        Map<String,List<OrganizationCategory>> map = new HashMap<String,List<OrganizationCategory>>();
        map.put("list", organizationCategoryRepo.findAll());
        return map;
    }

    @ApiMapping("/all")
    @Auth(role="ROLE_MANAGER")
    @Transactional
    public ApiResponse getAll() {
        return new ApiResponse(SUCCESS, getAllHelper());
    }
    
    @ApiMapping("/create")
    @Auth(role = "ROLE_MANAGER")
    @Transactional
    public ApiResponse createOrganizationCategory(@Data String data) {
        
        JsonNode dataNode;
        try {
            dataNode = objectMapper.readTree(data);
        } catch (IOException e) {
            return new ApiResponse(ERROR, "Unable to parse update json ["+e.getMessage()+"]");
        }
        
        OrganizationCategory newOrganizationCategory = null;
                
        JsonNode name = dataNode.get("name");
        if(name != null) {
            newOrganizationCategory = organizationCategoryRepo.create(name.asText());           
        }
        else {
            return new ApiResponse(ERROR, "Name required to create organization category!");
        }
        
        logger.info("Created organization category with name " + newOrganizationCategory.getName());
        
        simpMessagingTemplate.convertAndSend("/channel/settings/organization-category", new ApiResponse(SUCCESS, getAllHelper()));
        
        return new ApiResponse(SUCCESS);
    }
    
    @ApiMapping("/update")
    @Auth(role = "ROLE_MANAGER")
    @Transactional
    public ApiResponse updateOrganizationCategory(@Data String data) {
        
        JsonNode dataNode;
        try {
            dataNode = objectMapper.readTree(data);
        } catch (IOException e) {
            return new ApiResponse(ERROR, "Unable to parse update json ["+e.getMessage()+"]");
        }
        
        OrganizationCategory organizationCategory = null;
                
        JsonNode id = dataNode.get("id");
        if(id != null) {
            Long idLong = -1L;
            try {
                idLong = id.asLong();
            }
            catch(NumberFormatException nfe) {
                return new ApiResponse(ERROR, "Id required to update organization category!");
            }
            organizationCategory = organizationCategoryRepo.findOne(idLong);           
        }
        else {
            return new ApiResponse(ERROR, "Id required to update organization category!");
        }
        
        JsonNode name = dataNode.get("name");
        if(name != null) {
            organizationCategory.setName(name.asText());           
        }
        else {
            return new ApiResponse(ERROR, "Name required to create organization category!");
        }
        
        organizationCategory = organizationCategoryRepo.save(organizationCategory);
        
        logger.info("Updated organization category with name " + organizationCategory.getName());
        
        simpMessagingTemplate.convertAndSend("/channel/settings/organization-category", new ApiResponse(SUCCESS, getAllHelper()) );
        
        return new ApiResponse(SUCCESS);
    }

    @ApiMapping("/remove/{idString}")
    @Auth(role = "ROLE_MANAGER")
    @Transactional
    public ApiResponse removeOrganizationCategory(@ApiVariable String idString) {
        Long id = -1L;

        try {
            id = Long.parseLong(idString);
        } catch (NumberFormatException nfe) {
            return new ApiResponse(ERROR, "Id is not a valid organization category id!");
        }

        if (id >= 0) {
            OrganizationCategory toRemove = organizationCategoryRepo.findOne(id);
            if(toRemove != null) {
                organizationCategoryRepo.delete(toRemove);
                logger.info("Deleted organization category with id " + id);
                simpMessagingTemplate.convertAndSend("/channel/settings/organization-category", new ApiResponse(SUCCESS, getAllHelper()));
                return new ApiResponse(SUCCESS);
            } else {
                return new ApiResponse(ERROR, "Id for organization category not found!");
            }
        } else {
            return new ApiResponse(ERROR, "Id is not a valid organization category id!");
        }
    }
}
