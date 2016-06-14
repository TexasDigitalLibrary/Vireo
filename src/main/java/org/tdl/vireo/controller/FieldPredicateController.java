package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;
import org.tdl.vireo.enums.InputType;
import org.tdl.vireo.model.FieldPredicate;
import org.tdl.vireo.model.repo.FieldPredicateRepo;
import org.tdl.vireo.service.ValidationService;

import edu.tamu.framework.aspect.annotation.ApiMapping;
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

}
