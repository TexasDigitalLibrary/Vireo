package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;
import org.tdl.vireo.model.FieldPredicate;
import org.tdl.vireo.model.repo.FieldPredicateRepo;
import org.tdl.vireo.service.ValidationService;

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.ApiModel;
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
    private SimpMessagingTemplate simpMessagingTemplate;
    
    @Autowired
    private ValidationService validationService;
    
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
     * Endpoint to create a field predicate
     * 
     * @return ApiResponse with all input types.
     */
    @ApiMapping("/create")
    @Auth(role = "MANAGER")
    public ApiResponse createFieldPredicate(@ApiModel FieldPredicate fieldPredicate) {
        return new ApiResponse(SUCCESS, fieldPredicateRepo.create(fieldPredicate.getValue()));
    }

}
