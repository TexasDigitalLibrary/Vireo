package org.tdl.vireo.controller;

import static edu.tamu.weaver.response.ApiStatus.SUCCESS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tdl.vireo.model.repo.InputTypeRepo;

import edu.tamu.weaver.response.ApiResponse;

/**
 * Controller in which to manage controlled vocabulary.
 *
 */
@RestController
@RequestMapping("/settings/input-types")
public class InputTypeController {

    @Autowired
    private InputTypeRepo inputTypeRepo;

    /**
     * Endpoint to request all input types.
     *
     * @return ApiResponse with all input types.
     */
    @RequestMapping("/all")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse getAllInputTypes() {
        return new ApiResponse(SUCCESS, inputTypeRepo.findAll());
    }

}
