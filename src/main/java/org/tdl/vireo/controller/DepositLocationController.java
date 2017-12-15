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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tdl.vireo.model.DepositLocation;
import org.tdl.vireo.model.depositor.Depositor;
import org.tdl.vireo.model.repo.DepositLocationRepo;
import org.tdl.vireo.service.DepositorService;

import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.validation.aspect.annotation.WeaverValidatedModel;
import edu.tamu.weaver.validation.aspect.annotation.WeaverValidation;

@RestController
@RequestMapping("/settings/deposit-location")
public class DepositLocationController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DepositLocationRepo depositLocationRepo;

    @Autowired
    private DepositorService depositorService;

    @RequestMapping("/all")
    @PreAuthorize("hasRole('REVIEWER')")
    public ApiResponse allDepositLocations() {
        return new ApiResponse(SUCCESS, depositLocationRepo.findAllByOrderByPositionAsc());
    }

    @PreAuthorize("hasRole('MANAGER')")
    @RequestMapping(value = "/create", method = POST)
    @WeaverValidation(business = { @WeaverValidation.Business(value = CREATE) })
    public ApiResponse createDepositLocation(@RequestBody Map<String, Object> depositLocationJson) {
        return new ApiResponse(SUCCESS, depositLocationRepo.create(depositLocationJson));
    }

    // This endpoint is broken. Unable to deserialize Packager interface!!
    @PreAuthorize("hasRole('MANAGER')")
    @RequestMapping(value = "/update", method = POST)
    @WeaverValidation(business = { @WeaverValidation.Business(value = UPDATE) })
    public ApiResponse updateDepositLocation(@WeaverValidatedModel DepositLocation depositLocation) {
        logger.info("Updating deposit location with name " + depositLocation.getName());
        return new ApiResponse(SUCCESS, depositLocationRepo.update(depositLocation));
    }

    // This endpoint is broken. Unable to deserialize Packager interface!!
    @PreAuthorize("hasRole('MANAGER')")
    @RequestMapping(value = "/remove", method = POST)
    @WeaverValidation(business = { @WeaverValidation.Business(value = DELETE) })
    public ApiResponse removeDepositLocation(@WeaverValidatedModel DepositLocation depositLocation) {
        logger.info("Removing deposit location with name " + depositLocation.getName());
        depositLocationRepo.remove(depositLocation);
        return new ApiResponse(SUCCESS);
    }

    @RequestMapping("/reorder/{src}/{dest}")
    @PreAuthorize("hasRole('MANAGER')")
    @WeaverValidation(method = { @WeaverValidation.Method(value = REORDER, model = DepositLocation.class, params = { "0", "1" }) })
    public ApiResponse reorderDepositLocations(@PathVariable Long src, @PathVariable Long dest) {
        logger.info("Reordering custom action definitions");
        depositLocationRepo.reorder(src, dest);
        return new ApiResponse(SUCCESS);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @RequestMapping(value = "/test-connection", method = POST)
    public ApiResponse testConnection(@RequestBody Map<String, Object> depositLocationJson) {
        DepositLocation depositLocation = depositLocationRepo.createDetached(depositLocationJson);
        Depositor depositor = depositorService.getDepositor(depositLocation.getDepositorName());
        return new ApiResponse(SUCCESS, depositor.getCollections(depositLocation));
    }

}
