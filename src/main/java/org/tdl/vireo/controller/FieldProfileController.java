package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.tdl.vireo.model.repo.FieldProfileRepo;

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.model.ApiResponse;

@RestController
@ApiMapping("/field-profile")
public class FieldProfileController {

    @Autowired
    private FieldProfileRepo fieldProfileRepo;
    
    @ApiMapping("/all")
    @Auth(role = "MANAGER")
    public ApiResponse getAllFieldProfiles() {
        return new ApiResponse(SUCCESS, fieldProfileRepo.findAll());
    }
    
}
