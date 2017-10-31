package org.tdl.vireo.controller;

import static edu.tamu.weaver.response.ApiStatus.SUCCESS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tdl.vireo.model.repo.DegreeLevelRepo;

import edu.tamu.weaver.response.ApiResponse;

@RestController
@RequestMapping("/settings/degree-level")
public class DegreeLevelController {

    @Autowired
    private DegreeLevelRepo degreeLevelRepo;

    @RequestMapping("/all")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse allDegreeLevels() {
        return new ApiResponse(SUCCESS, degreeLevelRepo.findAllByOrderByPositionAsc());
    }

}
