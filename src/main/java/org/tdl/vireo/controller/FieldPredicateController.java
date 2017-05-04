package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;
import static edu.tamu.framework.enums.BusinessValidationType.CREATE;
import static edu.tamu.framework.enums.BusinessValidationType.DELETE;
import static edu.tamu.framework.enums.BusinessValidationType.EXISTS;
import static edu.tamu.framework.enums.BusinessValidationType.NONEXISTS;
import static edu.tamu.framework.enums.BusinessValidationType.UPDATE;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;
import org.tdl.vireo.model.AbstractFieldProfile;
import org.tdl.vireo.model.Embargo;
import org.tdl.vireo.model.FieldPredicate;
import org.tdl.vireo.model.FieldProfile;
import org.tdl.vireo.model.FieldValue;
import org.tdl.vireo.model.repo.FieldPredicateRepo;

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.ApiValidatedModel;
import edu.tamu.framework.aspect.annotation.ApiValidation;
import edu.tamu.framework.aspect.annotation.ApiVariable;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.model.ApiResponse;

/**
 * Controller in which to manage controlled vocabulary.
 *
 */
@RestController
@ApiMapping("/settings/field-predicates")
public class FieldPredicateController {
	
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private FieldPredicateRepo fieldPredicateRepo;
    
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    /**
     * Endpoint to request all field predicates.
     *
     * @return ApiResponse with all input types.
     */
    @ApiMapping("/all")
    @Auth(role = "STUDENT")
    public ApiResponse getAllFieldPredicates() {
        return new ApiResponse(SUCCESS, fieldPredicateRepo.findAll());
    }

    /**
     * Endpoint to request a field predicate by value.
     *
     * @return ApiResponse with all input types.
     */
    @ApiMapping("/{value}")
    @Auth(role = "STUDENT")
    public ApiResponse getFieldPredicateByValue(@ApiVariable String value) {
        return new ApiResponse(SUCCESS, fieldPredicateRepo.findByValue(value));
    }

    /**
     * Endpoint to create a field predicate
     *
     * @return ApiResponse with all input types.
     */
    @ApiMapping("/create")
    @Auth(role = "MANAGER")
    @ApiValidation(business = { @ApiValidation.Business(value = CREATE), @ApiValidation.Business(value = EXISTS) })
    public ApiResponse createFieldPredicate(@ApiValidatedModel FieldPredicate fieldPredicate) {
        logger.info("Creating Field Predicate:  " + fieldPredicate.getValue());
    	FieldPredicate fp = fieldPredicateRepo.create(fieldPredicate.getValue(), new Boolean(false));
        simpMessagingTemplate.convertAndSend("/channel/settings/field-predicates", new ApiResponse(SUCCESS, fieldPredicateRepo.findAll()));
        return new ApiResponse(SUCCESS, fp);
    }
    
    /**
     * Endpoint to remove a field predicate
     *
     * @return ApiResponse with all input types.
     */
    @ApiMapping("/remove")
    @Auth(role = "MANAGER")
    @ApiValidation(business = { @ApiValidation.Business(value = DELETE, joins = { AbstractFieldProfile.class, FieldValue.class }), @ApiValidation.Business(value = NONEXISTS) })
    public ApiResponse removeEmbargo(@ApiValidatedModel FieldPredicate fieldPredicate) {
        logger.info("Deleting Field Predicate:  " + fieldPredicate.getValue());
        fieldPredicateRepo.delete(fieldPredicate);
        simpMessagingTemplate.convertAndSend("/channel/settings/field-predicates", new ApiResponse(SUCCESS, fieldPredicateRepo.findAll()));
        return new ApiResponse(SUCCESS);
    }
    
    /**
     * Endpoint to create a field predicate
     *
     * @return ApiResponse with all input types.
     */
    @ApiMapping("/update")
    @Auth(role = "MANAGER")
    @ApiValidation(business = { @ApiValidation.Business(value = UPDATE), @ApiValidation.Business(value = NONEXISTS)})
    public ApiResponse updateFieldPredicate(@ApiValidatedModel FieldPredicate fieldPredicate) {
        logger.info("Updating Field Predicate:  " + fieldPredicate.getValue());
    	fieldPredicateRepo.save(fieldPredicate);
        simpMessagingTemplate.convertAndSend("/channel/settings/field-predicates", new ApiResponse(SUCCESS, fieldPredicateRepo.findAll()));
        return new ApiResponse(SUCCESS);
    }

}
