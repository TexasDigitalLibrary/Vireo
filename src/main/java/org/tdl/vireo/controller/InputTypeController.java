package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;
import org.tdl.vireo.model.repo.InputTypeRepo;
import org.tdl.vireo.service.ValidationService;

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.model.ApiResponse;

/**
 * Controller in which to manage controlled vocabulary.
 * 
 */
@RestController
@ApiMapping("/settings/input-types")
public class InputTypeController {

    private Logger logger = LoggerFactory.getLogger(this.getClass()); 

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    
    @Autowired
    private ValidationService validationService;
    
    @Autowired
    private InputTypeRepo inputTypeRepo;

	/**
     * Endpoint to request all input types.
     * 
     * @return ApiResponse with all input types.
     */
    @ApiMapping("/all")
    @Auth(role = "MANAGER")
    public ApiResponse getAllInputTypes() {
        return new ApiResponse(SUCCESS, inputTypeRepo.findAll());
    }

}
