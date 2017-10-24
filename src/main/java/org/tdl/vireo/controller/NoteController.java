package org.tdl.vireo.controller;

import static edu.tamu.weaver.response.ApiStatus.SUCCESS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tdl.vireo.model.repo.NoteRepo;

import edu.tamu.weaver.response.ApiResponse;

@RestController
@RequestMapping("/note")
public class NoteController {

    @Autowired
    private NoteRepo noteRepo;

    @RequestMapping("/all")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse getAllNotes() {
        return new ApiResponse(SUCCESS, noteRepo.findAll());
    }

}
