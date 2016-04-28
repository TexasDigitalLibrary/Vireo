package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;
import static edu.tamu.framework.enums.ApiResponseType.VALIDATION_ERROR;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.ObjectError;
import org.tdl.vireo.model.Language;
import org.tdl.vireo.model.repo.LanguageRepo;
import org.tdl.vireo.service.ProquestLanguageCodesService;

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.ApiValidatedModel;
import edu.tamu.framework.aspect.annotation.ApiVariable;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.model.ApiResponse;

/**
 * Controller in which to manage langauges.
 * 
 */
@Controller
@ApiMapping("/settings/languages")
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
    private Map<String, List<Language>> getAll() {
        Map<String, List<Language>> map = new HashMap<String, List<Language>>();
        map.put("list", languageRepo.findAllByOrderByPositionAsc());
        return map;
    }
    
    /**
     * 
     * @return
     */
    @ApiMapping("/all")
    @Auth(role = "MANAGER")
    @Transactional
    public ApiResponse getAllLanguages() {
        return new ApiResponse(SUCCESS, getAll());
    }
    
    /**
     * 
     * @param data
     * @return
     */
    @ApiMapping("/create")
    @Auth(role = "MANAGER")
    @Transactional
    public ApiResponse createLanguage(@ApiValidatedModel Language language) {
        // TODO: this needs to go in repo.validateCreate() -- VIR-201
        if(!language.getBindingResult().hasErrors() && languageRepo.findByName(language.getName()) != null){
            language.getBindingResult().addError(new ObjectError("language", language.getName() + " is already a language!"));
        }
        
        if(language.getBindingResult().hasErrors()) {
            return new ApiResponse(VALIDATION_ERROR, language.getBindingResult().getAll());
        }
        
        Language newLanguage = languageRepo.create(language.getName());

        // TODO: logging

        logger.info("Creating language " + newLanguage.getName());
        
        simpMessagingTemplate.convertAndSend("/channel/settings/languages", new ApiResponse(SUCCESS, getAll()));

        return new ApiResponse(SUCCESS);
    }
    
    /**
     * 
     * @param data
     * @return
     */
    @ApiMapping("/update")
    @Auth(role = "MANAGER")
    @Transactional
    public ApiResponse updateLanguage(@ApiValidatedModel Language language) {
        // TODO: this needs to go in repo.validateUpdate() -- VIR-201
        Language languageToUpdate = null;
        if(language.getId() == null) {
            language.getBindingResult().addError(new ObjectError("language", "Cannot update a language without an id!"));
        } else {
            languageToUpdate = languageRepo.findOne(language.getId());
            if(languageToUpdate == null) {
                language.getBindingResult().addError(new ObjectError("language", "Cannot update a language with an invalid id!"));
            }
            if(languageRepo.findByName(language.getName()) != null){
                language.getBindingResult().addError(new ObjectError("language", language.getName() + " is already a language!"));
            }
        }
        
        if(language.getBindingResult().hasErrors()) {
            return new ApiResponse(VALIDATION_ERROR, language.getBindingResult().getAll());
        }

        languageToUpdate.setName(language.getName());
        
        languageToUpdate = languageRepo.save(languageToUpdate);

        // TODO: logging

        logger.info("Updated language with name " + languageToUpdate.getName());

        simpMessagingTemplate.convertAndSend("/channel/settings/languages", new ApiResponse(SUCCESS, getAll()));

        return new ApiResponse(SUCCESS);
    }
    
    /**
     * Endpoint to remove language by provided index
     * 
     * @param indexString
     *            index of language to remove
     * @return ApiResponse indicating success or error
     */
    @ApiMapping("/remove/{indexString}")
    @Auth(role = "MANAGER")
    @Transactional
    public ApiResponse removeLanguage(@ApiVariable String indexString) {
        Long index = -1L;

        try {
            index = Long.parseLong(indexString);
        } catch (NumberFormatException nfe) {
            return new ApiResponse(VALIDATION_ERROR, "Id is not a valid language position!");
        }

        if (index >= 0) {
            languageRepo.remove(index);
        } else {
            return new ApiResponse(VALIDATION_ERROR, "Id is not a valid language position!");
        }

        logger.info("Deleted language with order " + index);

        simpMessagingTemplate.convertAndSend("/channel/settings/languages", new ApiResponse(SUCCESS, getAll()));

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
    @Transactional
    public ApiResponse reorderLanguage(@ApiVariable String src, @ApiVariable String dest) {
        Long intSrc = Long.parseLong(src);
        Long intDest = Long.parseLong(dest);
        languageRepo.reorder(intSrc, intDest);
        simpMessagingTemplate.convertAndSend("/channel/settings/languages", new ApiResponse(SUCCESS, getAll()));
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
    @Transactional
    public ApiResponse sortControlledVocabulary(@ApiVariable String column) {
        languageRepo.sort(column);
        simpMessagingTemplate.convertAndSend("/channel/settings/languages", new ApiResponse(SUCCESS, getAll()));
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
