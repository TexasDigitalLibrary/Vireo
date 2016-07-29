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
import org.springframework.transaction.annotation.Transactional;
import org.tdl.vireo.model.ControlledVocabulary;
import org.tdl.vireo.model.FieldGloss;
import org.tdl.vireo.model.Language;
import org.tdl.vireo.model.repo.LanguageRepo;
import org.tdl.vireo.service.ProquestLanguageCodesService;

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.ApiValidatedModel;
import edu.tamu.framework.aspect.annotation.ApiValidation;
import edu.tamu.framework.aspect.annotation.ApiVariable;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.model.ApiResponse;

/**
 * Controller in which to manage langauges.
 * 
 */
@Controller
@ApiMapping("/settings/language")
public class LanguageController {
    
    private Logger logger = LoggerFactory.getLogger(this.getClass()); 
    
    @Autowired
    private LanguageRepo languageRepo;
    
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    
    @Autowired
    private ProquestLanguageCodesService proquestLanguageCodes;
    
    /**
     * 
     * @return
     */
    @Transactional
    @ApiMapping("/all")
    @Auth(role = "MANAGER")
    public ApiResponse getAllLanguages() {
        return new ApiResponse(SUCCESS, languageRepo.findAllByOrderByPositionAsc());
    }
    
    /**
     * 
     * @return
     */
    @ApiMapping("/create")
    @Auth(role = "MANAGER")
    @ApiValidation(business = { @ApiValidation.Business(value = CREATE), @ApiValidation.Business(value = EXISTS) })
    public ApiResponse createLanguage(@ApiValidatedModel Language language) {
        logger.info("Creating language with name " + language.getName());
        language = languageRepo.create(language.getName());
        simpMessagingTemplate.convertAndSend("/channel/settings/language", new ApiResponse(SUCCESS, languageRepo.findAllByOrderByPositionAsc()));
        return new ApiResponse(SUCCESS, language);
    }
    
    /**
     * 
     * @return
     */
    @ApiMapping("/update")
    @Auth(role = "MANAGER")
    @ApiValidation(business = { @ApiValidation.Business(value = UPDATE), @ApiValidation.Business(value = NONEXISTS) })
    public ApiResponse updateLanguage(@ApiValidatedModel Language language) {
        logger.info("Updating language with name " + language.getName());
        language = languageRepo.save(language);
        simpMessagingTemplate.convertAndSend("/channel/settings/language", new ApiResponse(SUCCESS, languageRepo.findAllByOrderByPositionAsc()));
        return new ApiResponse(SUCCESS, language);
    }
    
    /**
     * 
     * @return
     */
    @ApiMapping("/remove")
    @Auth(role = "MANAGER")
    @ApiValidation(business = { @ApiValidation.Business(value = DELETE, joins = { FieldGloss.class, ControlledVocabulary.class }), @ApiValidation.Business(value = NONEXISTS) })
    public ApiResponse removeLanguage(@ApiValidatedModel Language language) {
        logger.info("Removing language with name " + language.getName());
        languageRepo.remove(language);
        simpMessagingTemplate.convertAndSend("/channel/settings/language", new ApiResponse(SUCCESS, languageRepo.findAllByOrderByPositionAsc()));
        return new ApiResponse(SUCCESS);
    }
    
    /**
     * Endpoint to reorder languages.
     * 
     * @param src
     *            source position
     * @param dest
     *            destination position
     * @return ApiResponse indicating success
     */
    @ApiMapping("/reorder/{src}/{dest}")
    @Auth(role = "MANAGER")
    @ApiValidation(method = { @ApiValidation.Method(value = REORDER, model = Language.class, params = { "0", "1" }) })
    public ApiResponse reorderLanguage(@ApiVariable Long src, @ApiVariable Long dest) {
        logger.info("Reordering languages");
        languageRepo.reorder(src, dest);
        simpMessagingTemplate.convertAndSend("/channel/settings/language", new ApiResponse(SUCCESS, languageRepo.findAllByOrderByPositionAsc()));
        return new ApiResponse(SUCCESS);
    }

    /**
     * Endpoint to sort languages.
     * 
     * @param column
     *            column to sort by
     * @return ApiResponse indicating success
     */
    @ApiMapping("/sort/{column}")
    @Auth(role = "MANAGER")
    @ApiValidation(method = { @ApiValidation.Method(value = SORT, model = Language.class, params = { "0" }) })
    public ApiResponse sortLanguage(@ApiVariable String column) {
        logger.info("Sorting languages by " + column);
        languageRepo.sort(column);
        simpMessagingTemplate.convertAndSend("/channel/settings/language", new ApiResponse(SUCCESS, languageRepo.findAllByOrderByPositionAsc()));
        return new ApiResponse(SUCCESS);
    }
    
    /**
     * 
     * @return
     */
    @ApiMapping("/proquest")
    @Auth(role = "MANAGER")
    public ApiResponse getProquestLanguageCodes() {        
        return new ApiResponse(SUCCESS, proquestLanguageCodes.getLanguageCodes());
    }
    
}
