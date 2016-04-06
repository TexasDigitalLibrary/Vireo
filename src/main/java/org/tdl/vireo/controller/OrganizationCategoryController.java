package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.tdl.vireo.model.OrganizationCategory;
import org.tdl.vireo.model.repo.OrganizationCategoryRepo;

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.model.ApiResponse;

@Controller
@ApiMapping("/organization-category")
public class OrganizationCategoryController {
    
    @Autowired
    private OrganizationCategoryRepo organizationCategoryRepo;
    
    @ApiMapping("/all")
    @Auth(role="ROLE_MANAGER")
    @Transactional
    public ApiResponse allOrganizationCategories() {
        Map<String,List<OrganizationCategory>> map = new HashMap<String,List<OrganizationCategory>>();        
        map.put("list", organizationCategoryRepo.findAll());
        return new ApiResponse(SUCCESS, map);
    }

}
