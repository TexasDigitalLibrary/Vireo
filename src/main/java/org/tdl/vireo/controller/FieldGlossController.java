package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.tdl.vireo.model.FieldGloss;
import org.tdl.vireo.model.Language;
import org.tdl.vireo.model.repo.FieldGlossRepo;
import org.tdl.vireo.model.repo.LanguageRepo;

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.ApiModel;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.model.ApiResponse;

/**
 * Controller in which to manage controlled vocabulary.
 * 
 */
@RestController
@ApiMapping("/settings/field-gloss")
public class FieldGlossController {
    
    @Autowired
    private FieldGlossRepo fieldGlossRepo;
    
    @Autowired
    private LanguageRepo languageRepo;
    
    /**
     * Endpoint to request all field glosses.
     * 
     * @return ApiResponse with all input types.
     */
    @ApiMapping("/all")
    @Auth(role = "MANAGER")
    public ApiResponse getAllFieldGlosses() {
        return new ApiResponse(SUCCESS, fieldGlossRepo.findAll());
    }
    
    /**
     * Endpoint to create a field gloss
     * 
     * @return ApiResponse with all input types.
     */
    @ApiMapping("/create")
    @Auth(role = "MANAGER")
    public ApiResponse createFieldGloss(@ApiModel FieldGloss fieldGloss) {
        Language alreadyPersistedLanguage = languageRepo.findByName(fieldGloss.getLanguage().getName());
        return new ApiResponse(SUCCESS, fieldGlossRepo.create(fieldGloss.getValue(), alreadyPersistedLanguage));
    }

}
