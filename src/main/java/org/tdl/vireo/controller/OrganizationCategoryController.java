package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;

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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.framework.aspect.annotation.ApiMapping;
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
        
    @ApiMapping("/all")
    @Auth(role="ROLE_MANAGER")
    @Transactional
    public ApiResponse allOrganizationCategorys() {
        Map<String,List<OrganizationCategory>> map = new HashMap<String,List<OrganizationCategory>>();        
        map.put("list", organizationCategoryRepo.findAll());
        return new ApiResponse(SUCCESS, map);
    }

    //TODO: Resolve model issues: lazy initialization error when trying to get an Org Cat from the repo
    // @ApiMapping("/create")
//     @Auth(role="ROLE_MANAGER")
//     public ApiResponse createOrganization(@Data String data) {
//         Map<String,String> map = new HashMap<String,String>();      
//         try {
//             map = objectMapper.readValue(data, new TypeReference<HashMap<String,String>>(){});
// //            System.out.println("cat name: "+organizationCategoryRepo.getOne(1L).getName());
// //            organizationRepo.create(map.get("name").toString(),organizationCategoryRepo.getOne(1L));
//         } catch (Exception e) {
//             e.printStackTrace();
//         } 
//         return new ApiResponse(SUCCESS, map);
//     }



}
