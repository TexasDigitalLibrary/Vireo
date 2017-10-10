package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;
import static edu.tamu.framework.enums.BusinessValidationType.CREATE;
import static edu.tamu.framework.enums.BusinessValidationType.DELETE;
import static edu.tamu.framework.enums.BusinessValidationType.EXISTS;
import static edu.tamu.framework.enums.BusinessValidationType.NONEXISTS;
import static edu.tamu.framework.enums.BusinessValidationType.UPDATE;
import static edu.tamu.framework.enums.MethodValidationType.REORDER;
import static edu.tamu.framework.enums.MethodValidationType.SORT;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.tdl.vireo.model.Degree;
import org.tdl.vireo.model.repo.DegreeRepo;
import org.tdl.vireo.service.ProquestCodesService;

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.ApiValidatedModel;
import edu.tamu.framework.aspect.annotation.ApiValidation;
import edu.tamu.framework.aspect.annotation.ApiVariable;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.model.ApiResponse;

@Controller
@ApiMapping("/settings/degree")
public class DegreeController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DegreeRepo degreeRepo;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private ProquestCodesService proquestCodesService;

    @ApiMapping("/all")
    @Auth(role = "MANAGER")
    public ApiResponse allDegrees() {
        return new ApiResponse(SUCCESS, degreeRepo.findAllByOrderByPositionAsc());
    }

    @Auth(role = "MANAGER")
    @ApiMapping(value = "/create", method = POST)
    @ApiValidation(business = { @ApiValidation.Business(value = CREATE), @ApiValidation.Business(value = EXISTS) })
    public ApiResponse createDegree(@ApiValidatedModel Degree degree) {
        logger.info("Creating degree with name " + degree.getName());
        degree = degreeRepo.create(degree.getName(), degree.getLevel());
        simpMessagingTemplate.convertAndSend("/channel/settings/degree", new ApiResponse(SUCCESS, degreeRepo.findAllByOrderByPositionAsc()));
        return new ApiResponse(SUCCESS, degree);
    }

    @Auth(role = "MANAGER")
    @ApiMapping(value = "/update", method = POST)
    @ApiValidation(business = { @ApiValidation.Business(value = UPDATE), @ApiValidation.Business(value = NONEXISTS) })
    public ApiResponse updateDegree(@ApiValidatedModel Degree degree) {
        logger.info("Updating degree with name " + degree.getName());
        degree = degreeRepo.save(degree);
        simpMessagingTemplate.convertAndSend("/channel/settings/degree", new ApiResponse(SUCCESS, degreeRepo.findAllByOrderByPositionAsc()));
        return new ApiResponse(SUCCESS, degree);
    }

    @Auth(role = "MANAGER")
    @ApiMapping(value = "/remove", method = POST)
    @ApiValidation(business = { @ApiValidation.Business(value = DELETE), @ApiValidation.Business(value = NONEXISTS) })
    public ApiResponse removeDegree(@ApiValidatedModel Degree degree) {
        logger.info("Removing graduation month with id " + degree.getId());
        degreeRepo.remove(degree);
        simpMessagingTemplate.convertAndSend("/channel/settings/degree", new ApiResponse(SUCCESS, degreeRepo.findAllByOrderByPositionAsc()));
        return new ApiResponse(SUCCESS);
    }

    @ApiMapping("/reorder/{src}/{dest}")
    @Auth(role = "MANAGER")
    @ApiValidation(method = { @ApiValidation.Method(value = REORDER, model = Degree.class, params = { "0", "1" }) })
    public ApiResponse reorderDegrees(@ApiVariable Long src, @ApiVariable Long dest) {
        logger.info("Reordering degree");
        degreeRepo.reorder(src, dest);
        simpMessagingTemplate.convertAndSend("/channel/settings/degree", new ApiResponse(SUCCESS, degreeRepo.findAllByOrderByPositionAsc()));
        return new ApiResponse(SUCCESS);
    }

    @ApiMapping("/sort/{column}")
    @Auth(role = "MANAGER")
    @ApiValidation(method = { @ApiValidation.Method(value = SORT, model = Degree.class, params = { "0" }) })
    public ApiResponse sortDegrees(@ApiVariable String column) {
        logger.info("Sorting degree by " + column);
        degreeRepo.sort(column);
        simpMessagingTemplate.convertAndSend("/channel/settings/degree", new ApiResponse(SUCCESS, degreeRepo.findAllByOrderByPositionAsc()));
        return new ApiResponse(SUCCESS);
    }

    /**
     *
     * @return
     */
    @ApiMapping("/proquest")
    @Auth(role = "MANAGER")
    public ApiResponse getProquestLanguageCodes() {
        return new ApiResponse(SUCCESS, proquestCodesService.getCodes("degrees"));
    }

}
