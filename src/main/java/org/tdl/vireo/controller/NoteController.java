package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.tdl.vireo.model.repo.NoteRepo;

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.model.ApiResponse;

@RestController
@ApiMapping("/note")
public class NoteController {

    @Autowired
    private NoteRepo noteRepo;
    
    @ApiMapping("/all")
    @Auth(role = "MANAGER")
    public ApiResponse getAllNotes() {
        return new ApiResponse(SUCCESS, noteRepo.findAll());
    }
    
}
