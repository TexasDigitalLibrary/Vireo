package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.ERROR;
import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.tdl.vireo.model.Language;
import org.tdl.vireo.model.repo.LanguageRepo;
import org.tdl.vireo.service.ProquestLanguageCodes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.ApiVariable;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.aspect.annotation.Data;
import edu.tamu.framework.model.ApiResponse;

/**
 * Controller in which to manage langauges.
 * 
 */
@Controller
@ApiMapping("/settings/languages")
public class LanguageController {
    
    private final Logger logger = Logger.getLogger(this.getClass());
    
    @Autowired
    private LanguageRepo languageRepo;
    
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private ProquestLanguageCodes proquestLanguageCodes;
    
    /**
     * 
     * @return
     */
    private Map<String, List<Language>> getAll() {
        Map<String, List<Language>> map = new HashMap<String, List<Language>>();
        map.put("list", languageRepo.findAllByOrderByOrderAsc());
        return map;
    }
    
    /**
     * 
     * @return
     */
    @ApiMapping("/all")
    @Auth(role = "ROLE_MANAGER")
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
    @Auth(role = "ROLE_MANAGER")
    @Transactional
    public ApiResponse createLanguage(@Data String data) {
        
        JsonNode dataNode;
        try {
            dataNode = objectMapper.readTree(data);
        } catch (IOException e) {
            return new ApiResponse(ERROR, "Unable to parse update json [" + e.getMessage() + "]");
        }
        
        // TODO: proper validation and response

        Language newLanguage = null;

        String name = null;
        
        JsonNode nameNode = dataNode.get("name");
        if (nameNode != null) {
            name = nameNode.asText();
        } else {
            return new ApiResponse(ERROR, "Name required to create language!");
        }

        if (name != null) {
            if(languageRepo.findByName(name) != null) {
                return new ApiResponse(ERROR, name + " is already a language!");
            }
            else {
                newLanguage = languageRepo.create(name);
            }
        } else {
            return new ApiResponse(ERROR, "Name and language could not be determined from input!");
        }

        // TODO: logging

        logger.info("Created language " + newLanguage.getName());

        simpMessagingTemplate.convertAndSend("/channel/settings/languages", new ApiResponse(SUCCESS, getAll()));

        return new ApiResponse(SUCCESS);
    }
    
    /**
     * 
     * @param data
     * @return
     */
    @ApiMapping("/update")
    @Auth(role = "ROLE_MANAGER")
    @Transactional
    public ApiResponse updateLanguage(@Data String data) {

        JsonNode dataNode;
        try {
            dataNode = objectMapper.readTree(data);
        } catch (IOException e) {
            return new ApiResponse(ERROR, "Unable to parse update json [" + e.getMessage() + "]");
        }

        // TODO: proper validation and response

        Language language = null;

        JsonNode id = dataNode.get("id");
        if (id != null) {
            Long idLong = -1L;
            try {
                idLong = id.asLong();
            } catch (NumberFormatException nfe) {
                return new ApiResponse(ERROR, "Id required to update graduation month!");
            }
            language = languageRepo.findOne(idLong);
        } else {
            return new ApiResponse(ERROR, "Id required to update language!");
        }

        JsonNode nameNode = dataNode.get("name");
        if (nameNode != null) {
            language.setName(nameNode.asText());
        } else {
            return new ApiResponse(ERROR, "Name required to update language!");
        }

        try {
            language = languageRepo.save(language);
        } catch (DataIntegrityViolationException dive) {
            return new ApiResponse(ERROR, nameNode.asText() + " is already a language!");
        }

        // TODO: logging

        logger.info("Updated language with name " + language.getName());

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
    @Auth(role = "ROLE_MANAGER")
    @Transactional
    public ApiResponse removeLanguage(@ApiVariable String indexString) {
        Integer index = -1;

        try {
            index = Integer.parseInt(indexString);
        } catch (NumberFormatException nfe) {
            return new ApiResponse(ERROR, "Id is not a valid language order!");
        }

        if (index >= 0) {
            languageRepo.remove(index);
        } else {
            return new ApiResponse(ERROR, "Id is not a valid language order!");
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
    @Auth(role = "ROLE_MANAGER")
    @Transactional
    public ApiResponse reorderLanguage(@ApiVariable String src, @ApiVariable String dest) {
        Integer intSrc = Integer.parseInt(src);
        Integer intDest = Integer.parseInt(dest);
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
    @Auth(role = "ROLE_MANAGER")
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
    @Auth(role = "ROLE_MANAGER")
    public ApiResponse getProquestLanguageCodes() {        
        return new ApiResponse(SUCCESS, proquestLanguageCodes.getLanguageCodes());
    }

}
