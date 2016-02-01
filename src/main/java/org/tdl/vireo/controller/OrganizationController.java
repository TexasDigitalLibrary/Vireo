package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.repo.OrganizationCategoryRepo;
import org.tdl.vireo.model.repo.OrganizationRepo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.aspect.annotation.Data;
import edu.tamu.framework.model.ApiResponse;

@Controller
@ApiMapping("/organization")
public class OrganizationController {
    
    @Autowired
    private OrganizationRepo organizationRepo;

    @Autowired
    private OrganizationCategoryRepo organizationCategoryRepo;

    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired 
    private SimpMessagingTemplate simpMessagingTemplate;
    
    private static final Logger logger = Logger.getLogger(OrganizationController.class);
        
    //TODO: Resolve model issues: infinite recursion due to org category relationship. JsonIdentityInfo annotation seems to help
    @ApiMapping("/all")
    @Auth(role="ROLE_MANAGER")
    @Transactional
    public ApiResponse allOrganizations() {
        Map<String,List<Organization>> map = new HashMap<String,List<Organization>>();        
        map.put("list", organizationRepo.findAll());
        return new ApiResponse(SUCCESS, map);
    }

    //TODO: Resolve model issues: lazy initialization error when trying to get an Org Cat from the repo
    @ApiMapping("/create")
    @Auth(role="ROLE_MANAGER")
    public ApiResponse createOrganization(@Data String data) {
        Map<String,String> map = new HashMap<String,String>();      
        try {
            map = objectMapper.readValue(data, new TypeReference<HashMap<String,String>>(){});
//            System.out.println("cat name: "+organizationCategoryRepo.getOne(1L).getName());
//            organizationRepo.create(map.get("name").toString(),organizationCategoryRepo.getOne(1L));
        } catch (Exception e) {
            e.printStackTrace();
        } 
        return new ApiResponse(SUCCESS, map);
    }
}
