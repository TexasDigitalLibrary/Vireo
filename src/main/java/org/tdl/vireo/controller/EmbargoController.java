package org.tdl.vireo.controller;

import static edu.tamu.weaver.response.ApiStatus.SUCCESS;
import static edu.tamu.weaver.validation.model.BusinessValidationType.CREATE;
import static edu.tamu.weaver.validation.model.BusinessValidationType.DELETE;
import static edu.tamu.weaver.validation.model.BusinessValidationType.UPDATE;
import static edu.tamu.weaver.validation.model.MethodValidationType.REORDER;
import static edu.tamu.weaver.validation.model.MethodValidationType.SORT;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tdl.vireo.model.Embargo;
import org.tdl.vireo.model.EmbargoGuarantor;
import org.tdl.vireo.model.repo.EmbargoRepo;

import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.validation.aspect.annotation.WeaverValidatedModel;
import edu.tamu.weaver.validation.aspect.annotation.WeaverValidation;

@RestController
@RequestMapping("/settings/embargo")
public class EmbargoController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private EmbargoRepo embargoRepo;

    @GetMapping("/all")
    public ApiResponse getEmbargoes() {
        return new ApiResponse(SUCCESS, embargoRepo.findAllByOrderByGuarantorAscPositionAsc());
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping(value = "/create")
    @WeaverValidation(business = { @WeaverValidation.Business(value = CREATE) })
    public ApiResponse createEmbargo(@WeaverValidatedModel Embargo embargo) {
        logger.info("Creating embargo with name " + embargo.getName());
        return new ApiResponse(SUCCESS, embargoRepo.create(embargo.getName(), embargo.getDescription(), embargo.getDuration(), embargo.getGuarantor(), embargo.isActive()));
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping(value = "/update")
    @WeaverValidation(business = { @WeaverValidation.Business(value = UPDATE, path = { "systemRequired" }, restrict = "true") })
    public ApiResponse updateEmbargo(@WeaverValidatedModel Embargo embargo) {
        logger.info("Updating embargo with name " + embargo.getName());
        return new ApiResponse(SUCCESS, embargoRepo.update(embargo));
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping(value = "/remove")
    @WeaverValidation(business = { @WeaverValidation.Business(value = DELETE, path = { "systemRequired" }, restrict = "true") })
    public ApiResponse removeEmbargo(@WeaverValidatedModel Embargo embargo) {
        logger.info("Removing Embargo:  " + embargo.getName());
        embargoRepo.remove(embargo);
        return new ApiResponse(SUCCESS);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping(value = "/activate/{id}")
    public ApiResponse activateEmbargo(@PathVariable Long id) {
        Embargo embargo = embargoRepo.findById(id).get();
        logger.info("Activating Embargo with name " + embargo.getName());
        embargo.isActive(true);
        return new ApiResponse(SUCCESS, embargoRepo.update(embargo));
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping(value = "/deactivate/{id}")
    public ApiResponse deactivateEmbargo(@PathVariable Long id) {
        Embargo embargo = embargoRepo.findById(id).get();
        logger.info("Deactivating Embargo with name " + embargo.getName());
        embargo.isActive(false);
        return new ApiResponse(SUCCESS, embargoRepo.update(embargo));
    }

    @GetMapping("/reorder/{guarantorString}/{src}/{dest}")
    @PreAuthorize("hasRole('MANAGER')")
    @WeaverValidation(method = { @WeaverValidation.Method(value = REORDER, model = Embargo.class, params = { "1", "2", "guarantor" }) })
    public ApiResponse reorderEmbargoes(@PathVariable String guarantorString, @PathVariable Long src, @PathVariable Long dest) {
        logger.info("Reordering Embargoes with guarantor " + guarantorString);
        EmbargoGuarantor guarantor = EmbargoGuarantor.fromString(guarantorString);
        embargoRepo.reorder(src, dest, guarantor);
        return new ApiResponse(SUCCESS);
    }

    @GetMapping("/sort/{guarantorString}/{column}")
    @PreAuthorize("hasRole('MANAGER')")
    @WeaverValidation(method = { @WeaverValidation.Method(value = SORT, model = Embargo.class, params = { "1", "0", "guarantor" }) })
    public ApiResponse sortEmbargoes(@PathVariable String guarantorString, @PathVariable String column) {
        logger.info("Sorting Embargoes with guarantor " + guarantorString + " by " + column);
        EmbargoGuarantor guarantor = EmbargoGuarantor.fromString(guarantorString);

        embargoRepo.sort(column, guarantor);
        return new ApiResponse(SUCCESS);
    }

}
