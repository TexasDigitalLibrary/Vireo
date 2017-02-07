package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.tdl.vireo.model.repo.AbstractPackagerRepo;

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.model.ApiResponse;

@Controller
@ApiMapping("/packager")
public class PackagerController {
	@Autowired
	AbstractPackagerRepo packagerRepo;
	
    @ApiMapping("/all")
    @Auth(role = "MANAGER")
    public ApiResponse allPackagers() {
        return new ApiResponse(SUCCESS, packagerRepo.findAll());
    }
}
