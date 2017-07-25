package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;
import static edu.tamu.framework.enums.BusinessValidationType.CREATE;
import static edu.tamu.framework.enums.BusinessValidationType.DELETE;
import static edu.tamu.framework.enums.BusinessValidationType.EXISTS;
import static edu.tamu.framework.enums.BusinessValidationType.NONEXISTS;
import static edu.tamu.framework.enums.BusinessValidationType.UPDATE;
import static edu.tamu.framework.enums.MethodValidationType.REORDER;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.tdl.vireo.model.DepositLocation;
import org.tdl.vireo.model.depositor.Depositor;
import org.tdl.vireo.model.repo.DepositLocationRepo;
import org.tdl.vireo.service.DepositorService;

import com.fasterxml.jackson.databind.JsonNode;

import edu.tamu.framework.aspect.annotation.ApiData;
import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.ApiValidatedModel;
import edu.tamu.framework.aspect.annotation.ApiValidation;
import edu.tamu.framework.aspect.annotation.ApiVariable;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.model.ApiResponse;

@Controller
@ApiMapping("/settings/deposit-location")
public class DepositLocationController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DepositLocationRepo depositLocationRepo;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private DepositorService depositorService;

    @ApiMapping("/all")
    @Auth(role = "MANAGER")
    public ApiResponse allDepositLocations() {
        return new ApiResponse(SUCCESS, depositLocationRepo.findAllByOrderByPositionAsc());
    }

    @ApiMapping("/create")
    @Auth(role = "MANAGER")
    @ApiValidation(business = { @ApiValidation.Business(value = CREATE), @ApiValidation.Business(value = EXISTS) })
    public ApiResponse createDepositLocation(@ApiData JsonNode depositLocationJson) {
        DepositLocation depositLocation = depositLocationRepo.create(depositLocationJson);
        simpMessagingTemplate.convertAndSend("/channel/settings/deposit-location", new ApiResponse(SUCCESS, depositLocationRepo.findAllByOrderByPositionAsc()));
        return new ApiResponse(SUCCESS, depositLocation);
    }

    // This endpoint is broken. Unable to deserialize Packager interface!!
    @ApiMapping("/update")
    @Auth(role = "MANAGER")
    @ApiValidation(business = { @ApiValidation.Business(value = UPDATE), @ApiValidation.Business(value = NONEXISTS) })
    public ApiResponse updateDepositLocation(@ApiValidatedModel DepositLocation depositLocation) {
        logger.info("Updating deposit location with name " + depositLocation.getName());
        depositLocation = depositLocationRepo.save(depositLocation);
        simpMessagingTemplate.convertAndSend("/channel/settings/deposit-location", new ApiResponse(SUCCESS, depositLocationRepo.findAllByOrderByPositionAsc()));
        return new ApiResponse(SUCCESS, depositLocation);
    }

    // This endpoint is broken. Unable to deserialize Packager interface!!
    @ApiMapping("/remove")
    @Auth(role = "MANAGER")
    @ApiValidation(business = { @ApiValidation.Business(value = DELETE), @ApiValidation.Business(value = NONEXISTS) })
    public ApiResponse removeDepositLocation(@ApiValidatedModel DepositLocation depositLocation) {
        logger.info("Removing deposit location with name " + depositLocation.getName());
        depositLocationRepo.remove(depositLocation);
        simpMessagingTemplate.convertAndSend("/channel/settings/deposit-location", new ApiResponse(SUCCESS, depositLocationRepo.findAllByOrderByPositionAsc()));
        return new ApiResponse(SUCCESS);
    }

    @ApiMapping("/reorder/{src}/{dest}")
    @Auth(role = "MANAGER")
    @ApiValidation(method = { @ApiValidation.Method(value = REORDER, model = DepositLocation.class, params = { "0", "1" }) })
    public ApiResponse reorderDepositLocations(@ApiVariable Long src, @ApiVariable Long dest) {
        logger.info("Reordering custom action definitions");
        depositLocationRepo.reorder(src, dest);
        simpMessagingTemplate.convertAndSend("/channel/settings/deposit-location", new ApiResponse(SUCCESS, depositLocationRepo.findAllByOrderByPositionAsc()));
        return new ApiResponse(SUCCESS);
    }

    @ApiMapping("/test-connection")
    @Auth(role = "MANAGER")
    public ApiResponse testConnection(@ApiData JsonNode depositLocationJson) {
        DepositLocation depositLocation = depositLocationRepo.createDetached(depositLocationJson);
        Depositor depositor = depositorService.getDepositor(depositLocation.getDepositorName());
        return new ApiResponse(SUCCESS, depositor.getCollections(depositLocation));
    }

    @ApiMapping("/find-collections")
    public ApiResponse findCollection(@ApiValidatedModel DepositLocation depositLocation) {
        System.out.println(depositLocation);
        return new ApiResponse(SUCCESS);
    }

}
