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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tdl.vireo.model.GraduationMonth;
import org.tdl.vireo.model.repo.GraduationMonthRepo;

import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.validation.aspect.annotation.WeaverValidatedModel;
import edu.tamu.weaver.validation.aspect.annotation.WeaverValidation;

@RestController
@RequestMapping("/settings/graduation-month")
public class GraduationMonthController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private GraduationMonthRepo graduationMonthRepo;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @RequestMapping("/all")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse allGraduationMonths() {
        return new ApiResponse(SUCCESS, graduationMonthRepo.findAllByOrderByPositionAsc());
    }

    @PreAuthorize("hasRole('MANAGER')")
    @RequestMapping(value = "/create", method = POST)
    @WeaverValidation(business = { @WeaverValidation.Business(value = CREATE) })
    public ApiResponse createGraduationMonth(@WeaverValidatedModel GraduationMonth graduationMonth) {
        logger.info("Creating graduation month with month " + graduationMonth.getMonth());
        graduationMonth = graduationMonthRepo.create(graduationMonth.getMonth());
        simpMessagingTemplate.convertAndSend("/channel/settings/graduation-month", new ApiResponse(SUCCESS, graduationMonthRepo.findAllByOrderByPositionAsc()));
        return new ApiResponse(SUCCESS, graduationMonth);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @RequestMapping(value = "/update", method = POST)
    @WeaverValidation(business = { @WeaverValidation.Business(value = UPDATE) })
    public ApiResponse updateGraduationMonth(@WeaverValidatedModel GraduationMonth graduationMonth) {
        logger.info("Updating graduation month with month " + graduationMonth.getMonth());
        graduationMonth = graduationMonthRepo.save(graduationMonth);
        simpMessagingTemplate.convertAndSend("/channel/settings/graduation-month", new ApiResponse(SUCCESS, graduationMonthRepo.findAllByOrderByPositionAsc()));
        return new ApiResponse(SUCCESS, graduationMonth);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @RequestMapping(value = "/remove", method = POST)
    @WeaverValidation(business = { @WeaverValidation.Business(value = DELETE) })
    public ApiResponse removeGraduationMonth(@WeaverValidatedModel GraduationMonth graduationMonth) {
        logger.info("Removing graduation month with id " + graduationMonth.getId());
        graduationMonthRepo.remove(graduationMonth);
        simpMessagingTemplate.convertAndSend("/channel/settings/graduation-month", new ApiResponse(SUCCESS, graduationMonthRepo.findAllByOrderByPositionAsc()));
        return new ApiResponse(SUCCESS);
    }

    @RequestMapping("/reorder/{src}/{dest}")
    @PreAuthorize("hasRole('MANAGER')")
    @WeaverValidation(method = { @WeaverValidation.Method(value = REORDER, model = GraduationMonth.class, params = { "0", "1" }) })
    public ApiResponse reorderGraduationMonths(@PathVariable Long src, @PathVariable Long dest) {
        logger.info("Reordering graduation months");
        graduationMonthRepo.reorder(src, dest);
        simpMessagingTemplate.convertAndSend("/channel/settings/graduation-month", new ApiResponse(SUCCESS, graduationMonthRepo.findAllByOrderByPositionAsc()));
        return new ApiResponse(SUCCESS);
    }

    @RequestMapping("/sort/{column}")
    @PreAuthorize("hasRole('MANAGER')")
    @WeaverValidation(method = { @WeaverValidation.Method(value = SORT, model = GraduationMonth.class, params = { "0" }) })
    public ApiResponse sortGraduationMonths(@PathVariable String column) {
        logger.info("Sorting graduation months by " + column);
        graduationMonthRepo.sort(column);
        simpMessagingTemplate.convertAndSend("/channel/settings/graduation-month", new ApiResponse(SUCCESS, graduationMonthRepo.findAllByOrderByPositionAsc()));
        return new ApiResponse(SUCCESS);
    }

}
