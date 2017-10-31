package org.tdl.vireo.controller;

import static edu.tamu.weaver.response.ApiStatus.SUCCESS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tdl.vireo.model.repo.FieldProfileRepo;

import edu.tamu.weaver.response.ApiResponse;

@RestController
@RequestMapping("/field-profile")
public class FieldProfileController {

    @Autowired
    private FieldProfileRepo fieldProfileRepo;

    @RequestMapping("/all")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse getAllFieldProfiles() {
        return new ApiResponse(SUCCESS, fieldProfileRepo.findAll());
    }

}
