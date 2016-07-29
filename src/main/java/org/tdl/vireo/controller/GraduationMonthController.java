package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;
import static edu.tamu.framework.enums.BusinessValidationType.CREATE;
import static edu.tamu.framework.enums.BusinessValidationType.DELETE;
import static edu.tamu.framework.enums.BusinessValidationType.EXISTS;
import static edu.tamu.framework.enums.BusinessValidationType.NONEXISTS;
import static edu.tamu.framework.enums.BusinessValidationType.UPDATE;
import static edu.tamu.framework.enums.MethodValidationType.REORDER;
import static edu.tamu.framework.enums.MethodValidationType.SORT;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.tdl.vireo.model.GraduationMonth;
import org.tdl.vireo.model.repo.GraduationMonthRepo;

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.ApiValidatedModel;
import edu.tamu.framework.aspect.annotation.ApiValidation;
import edu.tamu.framework.aspect.annotation.ApiVariable;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.model.ApiResponse;

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
    
    @ApiMapping("/create")
    @Auth(role = "MANAGER")
    @ApiValidation(business = { @ApiValidation.Business(value = CREATE), @ApiValidation.Business(value = EXISTS) })
    public ApiResponse createGraduationMonth(@ApiValidatedModel GraduationMonth graduationMonth) {
        logger.info("Creating graduation month with month " + graduationMonth.getMonth());
        graduationMonth = graduationMonthRepo.create(graduationMonth.getMonth());
        simpMessagingTemplate.convertAndSend("/channel/settings/graduation-month", new ApiResponse(SUCCESS, graduationMonthRepo.findAllByOrderByPositionAsc()));
        return new ApiResponse(SUCCESS, graduationMonth);
    }
    
    @ApiMapping("/update")
    @Auth(role = "MANAGER")
    @ApiValidation(business = { @ApiValidation.Business(value = UPDATE), @ApiValidation.Business(value = NONEXISTS) })
    public ApiResponse updateGraduationMonth(@ApiValidatedModel GraduationMonth graduationMonth) {
        logger.info("Updating graduation month with month " + graduationMonth.getMonth());
        graduationMonth = graduationMonthRepo.save(graduationMonth);
        simpMessagingTemplate.convertAndSend("/channel/settings/graduation-month", new ApiResponse(SUCCESS, graduationMonthRepo.findAllByOrderByPositionAsc()));
        return new ApiResponse(SUCCESS, graduationMonth);
    }

    @ApiMapping("/remove")
    @Auth(role = "MANAGER")
    @ApiValidation(business = { @ApiValidation.Business(value = DELETE), @ApiValidation.Business(value = NONEXISTS) })
    public ApiResponse removeGraduationMonth(@ApiValidatedModel GraduationMonth graduationMonth) {
        logger.info("Removing graduation month with id " + graduationMonth.getId());
        graduationMonthRepo.remove(graduationMonth);
        simpMessagingTemplate.convertAndSend("/channel/settings/graduation-month", new ApiResponse(SUCCESS, graduationMonthRepo.findAllByOrderByPositionAsc()));
        return new ApiResponse(SUCCESS);
    }
    
    @ApiMapping("/reorder/{src}/{dest}")
    @Auth(role = "MANAGER")
    @ApiValidation(method = { @ApiValidation.Method(value = REORDER, model = GraduationMonth.class, params = { "0", "1" }) })
    public ApiResponse reorderGraduationMonths(@ApiVariable Long src, @ApiVariable Long dest) {
        logger.info("Reordering document types");
        graduationMonthRepo.reorder(src, dest);
        simpMessagingTemplate.convertAndSend("/channel/settings/graduation-month", new ApiResponse(SUCCESS, graduationMonthRepo.findAllByOrderByPositionAsc()));
        return new ApiResponse(SUCCESS);
    }
    
    @ApiMapping("/sort/{column}")
    @Auth(role = "MANAGER")
    @ApiValidation(method = { @ApiValidation.Method(value = SORT, model = GraduationMonth.class, params = { "0" }) })
    public ApiResponse sortGraduationMonths(@ApiVariable String column) {
        logger.info("Sorting graduation months by " + column);
        graduationMonthRepo.sort(column);
        simpMessagingTemplate.convertAndSend("/channel/settings/graduation-month", new ApiResponse(SUCCESS, graduationMonthRepo.findAllByOrderByPositionAsc()));
        return new ApiResponse(SUCCESS);
    }
    
}
