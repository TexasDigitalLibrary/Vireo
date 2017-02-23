package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;
import static edu.tamu.framework.enums.BusinessValidationType.CREATE;
import static edu.tamu.framework.enums.BusinessValidationType.EXISTS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.tdl.vireo.model.FieldPredicate;
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

    @Autowired
    private FieldPredicateRepo fieldPredicateRepo;

    /**
     * Endpoint to request all field predicates.
     * 
     * @return ApiResponse with all input types.
     */
    @ApiMapping("/all")
    @Auth(role = "MANAGER")
    public ApiResponse getAllFieldPredicates() {
        return new ApiResponse(SUCCESS, fieldPredicateRepo.findAll());
    }

    /**
     * Endpoint to request all field predicates.
     * 
     * @return ApiResponse with all input types.
     */
    @ApiMapping("/{value}")
    @Auth(role = "MANAGER")
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
        return new ApiResponse(SUCCESS, fieldPredicateRepo.create(fieldPredicate.getValue(), new Boolean(false)));
    }

}
