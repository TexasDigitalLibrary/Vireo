package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;
import static edu.tamu.framework.enums.BusinessValidationType.CREATE;
import static edu.tamu.framework.enums.BusinessValidationType.DELETE;
import static edu.tamu.framework.enums.BusinessValidationType.UPDATE;
import static edu.tamu.framework.enums.BusinessValidationType.EXISTS;
import static edu.tamu.framework.enums.BusinessValidationType.NONEXISTS;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;
import org.tdl.vireo.model.AbstractFieldProfile;
import org.tdl.vireo.model.FieldGloss;
import org.tdl.vireo.model.FieldProfile;
import org.tdl.vireo.model.FieldValue;
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
	
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private FieldGlossRepo fieldGlossRepo;
    
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

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
        simpMessagingTemplate.convertAndSend("/channel/settings/field-gloss", new ApiResponse(SUCCESS, fieldGlossRepo.findAll()));
        return new ApiResponse(SUCCESS, fieldGlossRepo.create(fieldGloss.getValue(), alreadyPersistedLanguage));
    }
    
    /**
     * Endpoint to remove a field gloss
     *
     * @return ApiResponse with all field glosses.
     */
    @ApiMapping("/remove")
    @Auth(role = "MANAGER")
    @ApiValidation(business = {@ApiValidation.Business(value = DELETE, joins = { AbstractFieldProfile.class }, path = { "fieldGlosses", "id" }), @ApiValidation.Business(value = NONEXISTS) })
    public ApiResponse RemoveFieldGloss(@ApiValidatedModel FieldGloss fieldGloss) {
        logger.info("Deleting Field Gloss:  " + fieldGloss.getValue());
    	fieldGlossRepo.delete(fieldGloss);
        simpMessagingTemplate.convertAndSend("/channel/settings/field-gloss", new ApiResponse(SUCCESS, fieldGlossRepo.findAll()));
    	return new ApiResponse(SUCCESS);
    }
    
    /**
     * Endpoint to update a field gloss
     *
     * @return ApiResponse with all field glosses.
     */
    @ApiMapping("/update")
    @Auth(role = "MANAGER")
    @ApiValidation(business = {@ApiValidation.Business(value = UPDATE), @ApiValidation.Business(value = NONEXISTS) })
    public ApiResponse UpdateFieldGloss(@ApiValidatedModel FieldGloss fieldGloss) {
        logger.info("Deleting Field Gloss:  " + fieldGloss.getValue());
    	fieldGlossRepo.save(fieldGloss);
        simpMessagingTemplate.convertAndSend("/channel/settings/field-gloss", new ApiResponse(SUCCESS, fieldGlossRepo.findAll()));
    	return new ApiResponse(SUCCESS);
    }

}
