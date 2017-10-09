package org.tdl.vireo.controller;

import static edu.tamu.weaver.response.ApiStatus.SUCCESS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.tdl.vireo.model.repo.AbstractPackagerRepo;

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.weaver.response.ApiResponse;

@RestController
@ApiMapping("/packager")
public class PackagerController {

    @Autowired
    private AbstractPackagerRepo packagerRepo;

    @ApiMapping("/all")
    @Auth(role = "MANAGER")
    public ApiResponse allPackagers() {
        return new ApiResponse(SUCCESS, packagerRepo.findAll());
    }

}
