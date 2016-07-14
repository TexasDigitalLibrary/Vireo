package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;
import static edu.tamu.framework.enums.BusinessValidationType.CREATE;
import static edu.tamu.framework.enums.BusinessValidationType.EXISTS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.tdl.vireo.model.FieldGloss;
import org.tdl.vireo.model.Language;
import org.tdl.vireo.model.repo.FieldGlossRepo;
import org.tdl.vireo.model.repo.LanguageRepo;

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.ApiValidatedModel;
import edu.tamu.framework.aspect.annotation.ApiValidation;
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
    @ApiValidation(business = { @ApiValidation.Business(value = CREATE), @ApiValidation.Business(value = EXISTS) })
    public ApiResponse createFieldGloss(@ApiValidatedModel FieldGloss fieldGloss) {
        Language alreadyPersistedLanguage = languageRepo.findByName(fieldGloss.getLanguage().getName());
        return new ApiResponse(SUCCESS, fieldGlossRepo.create(fieldGloss.getValue(), alreadyPersistedLanguage));
    }

}
