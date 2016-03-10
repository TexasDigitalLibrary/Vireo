package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.tdl.vireo.model.Language;
import org.tdl.vireo.model.repo.LanguageRepo;

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.model.ApiResponse;

@Controller
@ApiMapping("/settings/language")
public class LanguageController {
    
    @Autowired
    private LanguageRepo languageRepo;
    
    private Map<String, List<Language>> getAll() {
        Map<String, List<Language>> map = new HashMap<String, List<Language>>();
        map.put("list", languageRepo.findAll());
        return map;
    }
    
    @ApiMapping("/all")
    @Auth(role = "ROLE_MANAGER")
    @Transactional
    public ApiResponse getAllLanguages() {
        return new ApiResponse(SUCCESS, getAll());
    }

}
