package org.tdl.vireo.controller;

import static edu.tamu.weaver.response.ApiStatus.SUCCESS;
import static edu.tamu.weaver.validation.model.BusinessValidationType.CREATE;
import static edu.tamu.weaver.validation.model.BusinessValidationType.DELETE;
import static edu.tamu.weaver.validation.model.BusinessValidationType.UPDATE;
import static edu.tamu.weaver.validation.model.MethodValidationType.REORDER;
import static edu.tamu.weaver.validation.model.MethodValidationType.SORT;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.tdl.vireo.enums.EmbargoGuarantor;
import org.tdl.vireo.model.Embargo;
import org.tdl.vireo.model.repo.EmbargoRepo;

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.ApiVariable;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.validation.aspect.annotation.WeaverValidatedModel;
import edu.tamu.weaver.validation.aspect.annotation.WeaverValidation;

@Controller
@ApiMapping("/settings/embargo")
public class EmbargoController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private EmbargoRepo embargoRepo;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @ApiMapping("/all")
    @Auth(role = "MANAGER")
    public ApiResponse getEmbargoes() {
        return new ApiResponse(SUCCESS, embargoRepo.findAllByOrderByGuarantorAscPositionAsc());
    }

    @Auth(role = "MANAGER")
    @ApiMapping(value = "/create", method = POST)
    @WeaverValidation(business = { @WeaverValidation.Business(value = CREATE) })
    public ApiResponse createEmbargo(@WeaverValidatedModel Embargo embargo) {
        logger.info("Creating embargo with name " + embargo.getName());
        embargo = embargoRepo.create(embargo.getName(), embargo.getDescription(), embargo.getDuration(), embargo.getGuarantor(), embargo.isActive());
        simpMessagingTemplate.convertAndSend("/channel/settings/embargo", new ApiResponse(SUCCESS, embargoRepo.findAllByOrderByGuarantorAscPositionAsc()));
        return new ApiResponse(SUCCESS, embargo);
    }

    @Auth(role = "MANAGER")
    @ApiMapping(value = "/update", method = POST)
    @WeaverValidation(business = { @WeaverValidation.Business(value = UPDATE) })
    public ApiResponse updateEmbargo(@WeaverValidatedModel Embargo embargo) {
        logger.info("Updating embargo with name " + embargo.getName());
        embargo = embargoRepo.save(embargo);
        simpMessagingTemplate.convertAndSend("/channel/settings/embargo", new ApiResponse(SUCCESS, embargoRepo.findAllByOrderByGuarantorAscPositionAsc()));
        return new ApiResponse(SUCCESS, embargo);
    }

    @Auth(role = "MANAGER")
    @ApiMapping(value = "/remove", method = POST)
    @WeaverValidation(business = { @WeaverValidation.Business(value = DELETE) })
    public ApiResponse removeEmbargo(@WeaverValidatedModel Embargo embargo) {
        logger.info("Removing Embargo:  " + embargo.getName());
        embargoRepo.remove(embargo);
        simpMessagingTemplate.convertAndSend("/channel/settings/embargo", new ApiResponse(SUCCESS, embargoRepo.findAllByOrderByGuarantorAscPositionAsc()));
        return new ApiResponse(SUCCESS);
    }

    @ApiMapping("/reorder/{guarantorString}/{src}/{dest}")
    @Auth(role = "MANAGER")
    @WeaverValidation(method = { @WeaverValidation.Method(value = REORDER, model = Embargo.class, params = { "1", "2", "guarantor" }) })
    public ApiResponse reorderEmbargoes(@ApiVariable String guarantorString, @ApiVariable Long src, @ApiVariable Long dest) {
        logger.info("Reordering Embargoes with guarantor " + guarantorString);
        EmbargoGuarantor guarantor = EmbargoGuarantor.fromString(guarantorString);
        embargoRepo.reorder(src, dest, guarantor);
        simpMessagingTemplate.convertAndSend("/channel/settings/embargo", new ApiResponse(SUCCESS, embargoRepo.findAllByOrderByGuarantorAscPositionAsc()));
        return new ApiResponse(SUCCESS);
    }

    @ApiMapping("/sort/{guarantorString}/{column}")
    @Auth(role = "MANAGER")
    @WeaverValidation(method = { @WeaverValidation.Method(value = SORT, model = Embargo.class, params = { "1", "0", "guarantor" }) })
    public ApiResponse sortEmbargoes(@ApiVariable String guarantorString, @ApiVariable String column) {
        logger.info("Sorting Embargoes with guarantor " + guarantorString + " by " + column);
        EmbargoGuarantor guarantor = EmbargoGuarantor.fromString(guarantorString);
        embargoRepo.sort(column, guarantor);
        simpMessagingTemplate.convertAndSend("/channel/settings/embargo", new ApiResponse(SUCCESS, embargoRepo.findAllByOrderByGuarantorAscPositionAsc()));
        return new ApiResponse(SUCCESS);
    }

}
