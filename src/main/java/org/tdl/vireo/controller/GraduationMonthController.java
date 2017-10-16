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
import org.tdl.vireo.model.GraduationMonth;
import org.tdl.vireo.model.repo.GraduationMonthRepo;

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.ApiVariable;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.validation.aspect.annotation.WeaverValidatedModel;
import edu.tamu.weaver.validation.aspect.annotation.WeaverValidation;

@Controller
@ApiMapping("/settings/graduation-month")
public class GraduationMonthController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private GraduationMonthRepo graduationMonthRepo;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @ApiMapping("/all")
    @Auth(role = "MANAGER")
    public ApiResponse allGraduationMonths() {
        return new ApiResponse(SUCCESS, graduationMonthRepo.findAllByOrderByPositionAsc());
    }

    @Auth(role = "MANAGER")
    @ApiMapping(value = "/create", method = POST)
    @WeaverValidation(business = { @WeaverValidation.Business(value = CREATE) })
    public ApiResponse createGraduationMonth(@WeaverValidatedModel GraduationMonth graduationMonth) {
        logger.info("Creating graduation month with month " + graduationMonth.getMonth());
        graduationMonth = graduationMonthRepo.create(graduationMonth.getMonth());
        simpMessagingTemplate.convertAndSend("/channel/settings/graduation-month", new ApiResponse(SUCCESS, graduationMonthRepo.findAllByOrderByPositionAsc()));
        return new ApiResponse(SUCCESS, graduationMonth);
    }

    @Auth(role = "MANAGER")
    @ApiMapping(value = "/update", method = POST)
    @WeaverValidation(business = { @WeaverValidation.Business(value = UPDATE) })
    public ApiResponse updateGraduationMonth(@WeaverValidatedModel GraduationMonth graduationMonth) {
        logger.info("Updating graduation month with month " + graduationMonth.getMonth());
        graduationMonth = graduationMonthRepo.save(graduationMonth);
        simpMessagingTemplate.convertAndSend("/channel/settings/graduation-month", new ApiResponse(SUCCESS, graduationMonthRepo.findAllByOrderByPositionAsc()));
        return new ApiResponse(SUCCESS, graduationMonth);
    }

    @Auth(role = "MANAGER")
    @ApiMapping(value = "/remove", method = POST)
    @WeaverValidation(business = { @WeaverValidation.Business(value = DELETE) })
    public ApiResponse removeGraduationMonth(@WeaverValidatedModel GraduationMonth graduationMonth) {
        logger.info("Removing graduation month with id " + graduationMonth.getId());
        graduationMonthRepo.remove(graduationMonth);
        simpMessagingTemplate.convertAndSend("/channel/settings/graduation-month", new ApiResponse(SUCCESS, graduationMonthRepo.findAllByOrderByPositionAsc()));
        return new ApiResponse(SUCCESS);
    }

    @ApiMapping("/reorder/{src}/{dest}")
    @Auth(role = "MANAGER")
    @WeaverValidation(method = { @WeaverValidation.Method(value = REORDER, model = GraduationMonth.class, params = { "0", "1" }) })
    public ApiResponse reorderGraduationMonths(@ApiVariable Long src, @ApiVariable Long dest) {
        logger.info("Reordering graduation months");
        graduationMonthRepo.reorder(src, dest);
        simpMessagingTemplate.convertAndSend("/channel/settings/graduation-month", new ApiResponse(SUCCESS, graduationMonthRepo.findAllByOrderByPositionAsc()));
        return new ApiResponse(SUCCESS);
    }

    @ApiMapping("/sort/{column}")
    @Auth(role = "MANAGER")
    @WeaverValidation(method = { @WeaverValidation.Method(value = SORT, model = GraduationMonth.class, params = { "0" }) })
    public ApiResponse sortGraduationMonths(@ApiVariable String column) {
        logger.info("Sorting graduation months by " + column);
        graduationMonthRepo.sort(column);
        simpMessagingTemplate.convertAndSend("/channel/settings/graduation-month", new ApiResponse(SUCCESS, graduationMonthRepo.findAllByOrderByPositionAsc()));
        return new ApiResponse(SUCCESS);
    }

}
