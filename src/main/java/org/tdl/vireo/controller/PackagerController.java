package org.tdl.vireo.controller;

import static edu.tamu.weaver.response.ApiStatus.SUCCESS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tdl.vireo.model.repo.AbstractPackagerRepo;

import edu.tamu.weaver.response.ApiResponse;

@RestController
@RequestMapping("/packager")
public class PackagerController {

    @Autowired
    private AbstractPackagerRepo packagerRepo;

    @RequestMapping("/all")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse allPackagers() {
        return new ApiResponse(SUCCESS, packagerRepo.findAll());
    }

}
