package org.tdl.vireo.controller;

import static edu.tamu.weaver.response.ApiStatus.SUCCESS;
import static edu.tamu.weaver.validation.model.BusinessValidationType.CREATE;
import static edu.tamu.weaver.validation.model.BusinessValidationType.DELETE;
import static edu.tamu.weaver.validation.model.BusinessValidationType.UPDATE;
import static edu.tamu.weaver.validation.model.MethodValidationType.REORDER;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.tdl.vireo.model.DepositLocation;
import org.tdl.vireo.model.depositor.Depositor;
import org.tdl.vireo.model.repo.DepositLocationRepo;
import org.tdl.vireo.service.DepositorService;

import edu.tamu.framework.aspect.annotation.ApiData;
import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.ApiVariable;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.validation.aspect.annotation.WeaverValidatedModel;
import edu.tamu.weaver.validation.aspect.annotation.WeaverValidation;

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

    @Auth(role = "MANAGER")
    @ApiMapping(value = "/create", method = POST)
    @WeaverValidation(business = { @WeaverValidation.Business(value = CREATE) })
    public ApiResponse createDepositLocation(@ApiData Map<String, Object> depositLocationJson) {
        DepositLocation depositLocation = depositLocationRepo.create(depositLocationJson);
        simpMessagingTemplate.convertAndSend("/channel/settings/deposit-location", new ApiResponse(SUCCESS, depositLocationRepo.findAllByOrderByPositionAsc()));
        return new ApiResponse(SUCCESS, depositLocation);
    }

    // This endpoint is broken. Unable to deserialize Packager interface!!
    @Auth(role = "MANAGER")
    @ApiMapping(value = "/update", method = POST)
    @WeaverValidation(business = { @WeaverValidation.Business(value = UPDATE) })
    public ApiResponse updateDepositLocation(@WeaverValidatedModel DepositLocation depositLocation) {
        logger.info("Updating deposit location with name " + depositLocation.getName());
        depositLocation = depositLocationRepo.save(depositLocation);
        simpMessagingTemplate.convertAndSend("/channel/settings/deposit-location", new ApiResponse(SUCCESS, depositLocationRepo.findAllByOrderByPositionAsc()));
        return new ApiResponse(SUCCESS, depositLocation);
    }

    // This endpoint is broken. Unable to deserialize Packager interface!!
    @Auth(role = "MANAGER")
    @ApiMapping(value = "/remove", method = POST)
    @WeaverValidation(business = { @WeaverValidation.Business(value = DELETE) })
    public ApiResponse removeDepositLocation(@WeaverValidatedModel DepositLocation depositLocation) {
        logger.info("Removing deposit location with name " + depositLocation.getName());
        depositLocationRepo.remove(depositLocation);
        simpMessagingTemplate.convertAndSend("/channel/settings/deposit-location", new ApiResponse(SUCCESS, depositLocationRepo.findAllByOrderByPositionAsc()));
        return new ApiResponse(SUCCESS);
    }

    @ApiMapping("/reorder/{src}/{dest}")
    @Auth(role = "MANAGER")
    @WeaverValidation(method = { @WeaverValidation.Method(value = REORDER, model = DepositLocation.class, params = { "0", "1" }) })
    public ApiResponse reorderDepositLocations(@ApiVariable Long src, @ApiVariable Long dest) {
        logger.info("Reordering custom action definitions");
        depositLocationRepo.reorder(src, dest);
        simpMessagingTemplate.convertAndSend("/channel/settings/deposit-location", new ApiResponse(SUCCESS, depositLocationRepo.findAllByOrderByPositionAsc()));
        return new ApiResponse(SUCCESS);
    }

    @Auth(role = "MANAGER")
    @ApiMapping(value = "/test-connection", method = POST)
    public ApiResponse testConnection(@ApiData Map<String, Object> depositLocationJson) {
        DepositLocation depositLocation = depositLocationRepo.createDetached(depositLocationJson);
        Depositor depositor = depositorService.getDepositor(depositLocation.getDepositorName());
        return new ApiResponse(SUCCESS, depositor.getCollections(depositLocation));
    }

    @ApiMapping(value = "/find-collections", method = POST)
    public ApiResponse findCollection(@WeaverValidatedModel DepositLocation depositLocation) {
        System.out.println(depositLocation);
        return new ApiResponse(SUCCESS);
    }

}
