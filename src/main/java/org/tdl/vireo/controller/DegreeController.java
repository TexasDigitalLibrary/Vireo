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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tdl.vireo.model.Degree;
import org.tdl.vireo.model.repo.DegreeRepo;
import org.tdl.vireo.service.ProquestCodesService;

import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.validation.aspect.annotation.WeaverValidatedModel;
import edu.tamu.weaver.validation.aspect.annotation.WeaverValidation;

@RestController
@RequestMapping("/settings/degree")
public class DegreeController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DegreeRepo degreeRepo;

    @Autowired
    private ProquestCodesService proquestCodesService;

    @RequestMapping("/all")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse allDegrees() {
        return new ApiResponse(SUCCESS, degreeRepo.findAllByOrderByPositionAsc());
    }

    @PreAuthorize("hasRole('MANAGER')")
    @RequestMapping(value = "/create", method = POST)
    @WeaverValidation(business = { @WeaverValidation.Business(value = CREATE) })
    public ApiResponse createDegree(@WeaverValidatedModel Degree degree) {
        logger.info("Creating degree with name " + degree.getName());
        degree = degreeRepo.create(degree.getName(), degree.getLevel());
        return new ApiResponse(SUCCESS, degree);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @RequestMapping(value = "/update", method = POST)
    @WeaverValidation(business = { @WeaverValidation.Business(value = UPDATE) })
    public ApiResponse updateDegree(@WeaverValidatedModel Degree degree) {
        logger.info("Updating degree with name " + degree.getName());
        degree = degreeRepo.update(degree);
        return new ApiResponse(SUCCESS, degree);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @RequestMapping(value = "/remove", method = POST)
    @WeaverValidation(business = { @WeaverValidation.Business(value = DELETE) })
    public ApiResponse removeDegree(@WeaverValidatedModel Degree degree) {
        logger.info("Removing graduation month with id " + degree.getId());
        degreeRepo.remove(degree);
        return new ApiResponse(SUCCESS);
    }

    @RequestMapping("/reorder/{src}/{dest}")
    @PreAuthorize("hasRole('MANAGER')")
    @WeaverValidation(method = { @WeaverValidation.Method(value = REORDER, model = Degree.class, params = { "0", "1" }) })
    public ApiResponse reorderDegrees(@PathVariable Long src, @PathVariable Long dest) {
        logger.info("Reordering degree");
        degreeRepo.reorder(src, dest);
        return new ApiResponse(SUCCESS);
    }

    @RequestMapping("/sort/{column}")
    @PreAuthorize("hasRole('MANAGER')")
    @WeaverValidation(method = { @WeaverValidation.Method(value = SORT, model = Degree.class, params = { "0" }) })
    public ApiResponse sortDegrees(@PathVariable String column) {
        logger.info("Sorting degree by " + column);
        degreeRepo.sort(column);
        return new ApiResponse(SUCCESS);
    }

    /**
     *
     * @return
     */
    @RequestMapping("/proquest")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse getProquestLanguageCodes() {
        return new ApiResponse(SUCCESS, proquestCodesService.getCodes("degrees"));
    }

}
