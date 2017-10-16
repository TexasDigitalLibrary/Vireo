package org.tdl.vireo.controller;

import static edu.tamu.weaver.response.ApiStatus.SUCCESS;
import static edu.tamu.weaver.validation.model.BusinessValidationType.CREATE;
import static edu.tamu.weaver.validation.model.BusinessValidationType.DELETE;
import static edu.tamu.weaver.validation.model.BusinessValidationType.EXISTS;
import static edu.tamu.weaver.validation.model.BusinessValidationType.NONEXISTS;
import static edu.tamu.weaver.validation.model.BusinessValidationType.UPDATE;
import static edu.tamu.weaver.validation.model.MethodValidationType.REORDER;
import static edu.tamu.weaver.validation.model.MethodValidationType.SORT;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.tdl.vireo.model.Degree;
import org.tdl.vireo.model.repo.DegreeRepo;
import org.tdl.vireo.service.ProquestCodesService;

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.ApiVariable;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.validation.aspect.annotation.WeaverValidatedModel;
import edu.tamu.weaver.validation.aspect.annotation.WeaverValidation;

@Controller
@ApiMapping("/settings/degree")
public class DegreeController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DegreeRepo degreeRepo;

    @Autowired
    private ProquestCodesService proquestCodesService;

    @ApiMapping("/all")
    @Auth(role = "MANAGER")
    public ApiResponse allDegrees() {
        return new ApiResponse(SUCCESS, degreeRepo.findAllByOrderByPositionAsc());
    }

    @Auth(role = "MANAGER")
    @ApiMapping(value = "/create", method = POST)
    @WeaverValidation(business = { @WeaverValidation.Business(value = CREATE), @WeaverValidation.Business(value = EXISTS) })
    public ApiResponse createDegree(@WeaverValidatedModel Degree degree) {
        logger.info("Creating degree with name " + degree.getName());
        degree = degreeRepo.create(degree.getName(), degree.getLevel());
        return new ApiResponse(SUCCESS, degree);
    }

    @Auth(role = "MANAGER")
    @ApiMapping(value = "/update", method = POST)
    @WeaverValidation(business = { @WeaverValidation.Business(value = UPDATE), @WeaverValidation.Business(value = NONEXISTS) })
    public ApiResponse updateDegree(@WeaverValidatedModel Degree degree) {
        logger.info("Updating degree with name " + degree.getName());
        degree = degreeRepo.update(degree);
        return new ApiResponse(SUCCESS, degree);
    }

    @Auth(role = "MANAGER")
    @ApiMapping(value = "/remove", method = POST)
    @WeaverValidation(business = { @WeaverValidation.Business(value = DELETE), @WeaverValidation.Business(value = NONEXISTS) })
    public ApiResponse removeDegree(@WeaverValidatedModel Degree degree) {
        logger.info("Removing graduation month with id " + degree.getId());
        degreeRepo.remove(degree);
        return new ApiResponse(SUCCESS);
    }

    @ApiMapping("/reorder/{src}/{dest}")
    @Auth(role = "MANAGER")
    @WeaverValidation(method = { @WeaverValidation.Method(value = REORDER, model = Degree.class, params = { "0", "1" }) })
    public ApiResponse reorderDegrees(@ApiVariable Long src, @ApiVariable Long dest) {
        logger.info("Reordering degree");
        degreeRepo.reorder(src, dest);
        return new ApiResponse(SUCCESS);
    }

    @ApiMapping("/sort/{column}")
    @Auth(role = "MANAGER")
    @WeaverValidation(method = { @WeaverValidation.Method(value = SORT, model = Degree.class, params = { "0" }) })
    public ApiResponse sortDegrees(@ApiVariable String column) {
        logger.info("Sorting degree by " + column);
        degreeRepo.sort(column);
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
