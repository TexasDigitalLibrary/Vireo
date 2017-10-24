package org.tdl.vireo.controller;

import static edu.tamu.weaver.response.ApiStatus.SUCCESS;
import static edu.tamu.weaver.validation.model.BusinessValidationType.CREATE;
import static edu.tamu.weaver.validation.model.BusinessValidationType.DELETE;
import static edu.tamu.weaver.validation.model.BusinessValidationType.UPDATE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tdl.vireo.model.AbstractFieldProfile;
import org.tdl.vireo.model.FieldGloss;
import org.tdl.vireo.model.Language;
import org.tdl.vireo.model.repo.FieldGlossRepo;
import org.tdl.vireo.model.repo.LanguageRepo;

import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.validation.aspect.annotation.WeaverValidatedModel;
import edu.tamu.weaver.validation.aspect.annotation.WeaverValidation;

/**
 * Controller in which to manage controlled vocabulary.
 *
 */
@RestController
@RequestMapping("/settings/field-gloss")
public class FieldGlossController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private FieldGlossRepo fieldGlossRepo;

    @Autowired
    private LanguageRepo languageRepo;

    /**
     * Endpoint to request all field glosses.
     *
     * @return ApiResponse with all input types.
     */
    @RequestMapping("/all")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse getAllFieldGlosses() {
        return new ApiResponse(SUCCESS, fieldGlossRepo.findAll());
    }

    /**
     * Endpoint to create a field gloss
     *
     * @return ApiResponse with all input types.
     */
    @PreAuthorize("hasRole('MANAGER')")
    @RequestMapping(value = "/create", method = POST)
    @WeaverValidation(business = { @WeaverValidation.Business(value = CREATE) })
    public ApiResponse createFieldGloss(@WeaverValidatedModel FieldGloss fieldGloss) {
        Language alreadyPersistedLanguage = languageRepo.findByName(fieldGloss.getLanguage().getName());
        return new ApiResponse(SUCCESS, fieldGlossRepo.create(fieldGloss.getValue(), alreadyPersistedLanguage));
    }

    /**
     * Endpoint to remove a field gloss
     *
     * @return ApiResponse with all field glosses.
     */
    @PreAuthorize("hasRole('MANAGER')")
    @RequestMapping(value = "/remove", method = POST)
    @WeaverValidation(business = { @WeaverValidation.Business(value = DELETE, joins = { AbstractFieldProfile.class }, path = { "fieldGlosses", "id" }) })
    public ApiResponse RemoveFieldGloss(@WeaverValidatedModel FieldGloss fieldGloss) {
        logger.info("Deleting Field Gloss:  " + fieldGloss.getValue());
        fieldGlossRepo.delete(fieldGloss);
        return new ApiResponse(SUCCESS);
    }

    /**
     * Endpoint to update a field gloss
     *
     * @return ApiResponse with all field glosses.
     */
    @PreAuthorize("hasRole('MANAGER')")
    @RequestMapping(value = "/update", method = POST)
    @WeaverValidation(business = { @WeaverValidation.Business(value = UPDATE) })
    public ApiResponse UpdateFieldGloss(@WeaverValidatedModel FieldGloss fieldGloss) {
        logger.info("Deleting Field Gloss:  " + fieldGloss.getValue());
        return new ApiResponse(SUCCESS, fieldGlossRepo.update(fieldGloss));
    }

}
