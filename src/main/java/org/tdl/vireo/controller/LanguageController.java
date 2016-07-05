package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;
import static edu.tamu.framework.enums.ApiResponseType.VALIDATION_WARNING;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.ObjectError;
import org.tdl.vireo.model.Language;
import org.tdl.vireo.model.repo.LanguageRepo;
import org.tdl.vireo.service.ProquestLanguageCodesService;
import org.tdl.vireo.service.ValidationService;

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.ApiValidatedModel;
import edu.tamu.framework.aspect.annotation.ApiVariable;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.model.ApiResponse;
import edu.tamu.framework.validation.ModelBindingResult;

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
    
    @Autowired
    private ValidationService validationService;
    
    /**
     * 
     * @return
     */
    @ApiMapping("/all")
    @Auth(role = "MANAGER")
    @Transactional
    public ApiResponse getAllLanguages() {
        return new ApiResponse(SUCCESS, languageRepo.findAllByOrderByPositionAsc());
    }
    
    /**
     * 
     * @return
     */
    @ApiMapping("/create")
    @Auth(role = "MANAGER")
    @Transactional
    public ApiResponse createLanguage(@ApiValidatedModel Language language) {
        
        // will attach any errors to the BindingResult when validating the incoming language
        language = languageRepo.validateCreate(language);
        
        // build a response based on the BindingResult state
        ApiResponse response = validationService.buildResponse(language);
        
        switch(response.getMeta().getType()){
            case SUCCESS:
            case VALIDATION_INFO:
                logger.info("Creating language with name " + language.getName());
                languageRepo.create(language.getName());
                simpMessagingTemplate.convertAndSend("/channel/settings/language", new ApiResponse(SUCCESS, languageRepo.findAllByOrderByPositionAsc()));
                break;
            case VALIDATION_WARNING:
                simpMessagingTemplate.convertAndSend("/channel/settings/language", new ApiResponse(VALIDATION_WARNING, languageRepo.findAllByOrderByPositionAsc()));
                break;
            default:
                logger.warn("Couldn't create language with name " + language.getName() + " because: " + response.getMeta().getType());
                break;
        }

        return response;
    }
    
    /**
     * 
     * @return
     */
    @ApiMapping("/update")
    @Auth(role = "MANAGER")
    @Transactional
    public ApiResponse updateLanguage(@ApiValidatedModel Language language) {
        
        // will attach any errors to the BindingResult when validating the incoming language
        language = languageRepo.validateUpdate(language);
        
        // build a response based on the BindingResult state
        ApiResponse response = validationService.buildResponse(language);
        
        switch(response.getMeta().getType()){
            case SUCCESS:
            case VALIDATION_INFO:
                logger.info("Updating language with name " + language.getName());
                languageRepo.save(language);
                simpMessagingTemplate.convertAndSend("/channel/settings/language", new ApiResponse(SUCCESS, languageRepo.findAllByOrderByPositionAsc()));
                break;
            case VALIDATION_WARNING:
                simpMessagingTemplate.convertAndSend("/channel/settings/language", new ApiResponse(VALIDATION_WARNING, languageRepo.findAllByOrderByPositionAsc()));
                break;
            default:
                logger.warn("Couldn't update language with name " + language.getName() + " because: " + response.getMeta().getType());
                break;
        }

        return response;
    }
    
    /**
     * 
     * @return
     */
    @ApiMapping("/remove")
    @Auth(role = "MANAGER")
    @Transactional //TODO: this @Transactional throws an exception when we catch DataIntegrityViolation
    public ApiResponse removeLanguage(@ApiValidatedModel Language language) {
        
        // build a response based on the BindingResult state
        ApiResponse response = validationService.buildResponse(language);
        
        switch(response.getMeta().getType()){
            case SUCCESS:
            case VALIDATION_INFO:
                logger.info("Removing language with name " + language.getName());
                try {
                    languageRepo.remove(language);
                    simpMessagingTemplate.convertAndSend("/channel/settings/language", new ApiResponse(SUCCESS, languageRepo.findAllByOrderByPositionAsc()));
                } catch (DataIntegrityViolationException e) {
                    language.getBindingResult().addError(new ObjectError("language", "Could not remove language " + language.getName() + ", it's being used!"));
                    response = validationService.buildResponse(language);
                    logger.error("Couldn't remove language " + language.getName() + " because: " + e.getLocalizedMessage());
                }
                break;
            case VALIDATION_WARNING:
                simpMessagingTemplate.convertAndSend("/channel/settings/language", new ApiResponse(VALIDATION_WARNING, languageRepo.findAllByOrderByPositionAsc()));
                break;
            default:
                logger.warn("Couldn't remove language with name " + language.getName() + " because: " + response.getMeta().getType());
                break;
        }
        
        return response;
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
    @Transactional
    public ApiResponse reorderLanguage(@ApiVariable String src, @ApiVariable String dest) {
        
        // create a ModelBindingResult since we have an @ApiVariable coming in (and not a @ApiValidatedModel)
        ModelBindingResult modelBindingResult = new ModelBindingResult(src, "language");
        
        // will attach any errors to the BindingResult when validating the incoming src, dest
        Long longSrc = validationService.validateLong(src, "position", modelBindingResult);
        Long longDest = validationService.validateLong(dest, "position", modelBindingResult);
        
        // build a response based on the BindingResult state
        ApiResponse response = validationService.buildResponse(modelBindingResult);
        
        switch(response.getMeta().getType()){
            case SUCCESS:
            case VALIDATION_INFO:
                logger.info("Reordering languages");
                languageRepo.reorder(longSrc, longDest);
                simpMessagingTemplate.convertAndSend("/channel/settings/language", new ApiResponse(SUCCESS, languageRepo.findAllByOrderByPositionAsc()));
                break;
            case VALIDATION_WARNING:
                simpMessagingTemplate.convertAndSend("/channel/settings/language", new ApiResponse(VALIDATION_WARNING, languageRepo.findAllByOrderByPositionAsc()));
                break;
            default:
                logger.warn("Couldn't reorder languages because: " + response.getMeta().getType());
                break;
        }
        
        return response;
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
    @Transactional
    public ApiResponse sortLanguage(@ApiVariable String column) {
        
        // create a ModelBindingResult since we have an @ApiVariable coming in (and not a @ApiValidatedModel)
        ModelBindingResult modelBindingResult = new ModelBindingResult(column, "language");
        
        // will attach any errors to the BindingResult when validating the incoming column
        validationService.validateColumn(Language.class, column, modelBindingResult);
        
        // build a response based on the BindingResult state
        ApiResponse response = validationService.buildResponse(modelBindingResult);
        
        switch(response.getMeta().getType()){
            case SUCCESS:
            case VALIDATION_INFO:
                logger.info("Sorting languages by " + column);
                languageRepo.sort(column);
                simpMessagingTemplate.convertAndSend("/channel/settings/language", new ApiResponse(SUCCESS, languageRepo.findAllByOrderByPositionAsc()));
                break;
            case VALIDATION_WARNING:
                simpMessagingTemplate.convertAndSend("/channel/settings/language", new ApiResponse(VALIDATION_WARNING, languageRepo.findAllByOrderByPositionAsc()));
                break;
            default:
                logger.warn("Couldn't sort languages because: " + response.getMeta().getType());
                break;
        }
        
        return response;
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
