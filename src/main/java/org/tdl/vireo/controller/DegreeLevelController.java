package org.tdl.vireo.controller;

import static edu.tamu.weaver.response.ApiStatus.SUCCESS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.tdl.vireo.model.repo.DegreeLevelRepo;

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.weaver.response.ApiResponse;

@Controller
@ApiMapping("/settings/degree-level")
public class DegreeLevelController {

    @Autowired
    private DegreeLevelRepo degreeLevelRepo;

    @ApiMapping("/all")
    @Auth(role = "MANAGER")
    public ApiResponse allDegreeLevels() {
        return new ApiResponse(SUCCESS, degreeLevelRepo.findAllByOrderByPositionAsc());
    }

}
