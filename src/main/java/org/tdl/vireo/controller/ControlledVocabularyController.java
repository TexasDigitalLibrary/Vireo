package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.ERROR;
import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;
import static edu.tamu.framework.enums.ApiResponseType.VALIDATION_ERROR;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletInputStream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.tdl.vireo.model.ControlledVocabulary;
import org.tdl.vireo.model.ControlledVocabularyCache;
import org.tdl.vireo.model.VocabularyWord;
import org.tdl.vireo.model.repo.ControlledVocabularyRepo;
import org.tdl.vireo.model.repo.VocabularyWordRepo;
import org.tdl.vireo.service.ControlledVocabularyCachingService;

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.ApiValidatedModel;
import edu.tamu.framework.aspect.annotation.ApiVariable;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.aspect.annotation.InputStream;
import edu.tamu.framework.model.ApiResponse;

/**
 * Controller in which to manage controlled vocabulary.
 * 
 */
@RestController
@ApiMapping("/settings/controlled-vocabulary")
public class ControlledVocabularyController {

    private Logger logger = LoggerFactory.getLogger(this.getClass()); 

    @Autowired
    private ControlledVocabularyCachingService controlledVocabularyCachingService;

    @Autowired
    private ControlledVocabularyRepo controlledVocabularyRepo;

    @Autowired
    private VocabularyWordRepo vocabularyWordRepo;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    /**
     * Get all controlled vocabulary from repo.
     * 
     * @return map with list of all controlled vocabulary
     */
    private Map<String, List<ControlledVocabulary>> getAll() {
        Map<String, List<ControlledVocabulary>> map = new HashMap<String, List<ControlledVocabulary>>();
        map.put("list", controlledVocabularyRepo.findAllByOrderByPositionAsc());
        return map;
    }

    /**
     * Endpoint to request all controlled vocabulary.
     * 
     * @return ApiResponse with all controlled vocabulary
     */
    @ApiMapping("/all")
    @Auth(role = "ROLE_MANAGER")
    @Transactional
    public ApiResponse getAllControlledVocabulary() {
        return new ApiResponse(SUCCESS, getAll());
    }

    /**
     * Endpoint to request controlled vocabulary by name.
     * 
     * @param name
     *            Name of controlled vocabulary
     * @return ApiResponse with requested controlled vocabulary
     */
    @ApiMapping("/{name}")
    @Auth(role = "ROLE_MANAGER")
    @Transactional
    public ApiResponse getControlledVocabularyByName(@ApiVariable String name) {
        return new ApiResponse(SUCCESS, controlledVocabularyRepo.findByName(name));
    }

    /**
     * Endpoint to create a new controlled vocabulary.
     * 
     * @param data
     *            Json input data from request
     * @return ApiResponse with indicating success or error
     */
    @ApiMapping("/create")
    @Auth(role = "ROLE_MANAGER")
    @Transactional
    public ApiResponse createControlledVocabulary(@ApiValidatedModel ControlledVocabulary controlledVocabulary) {
        
        if(controlledVocabulary.getBindingResult().hasErrors()){
            return new ApiResponse(VALIDATION_ERROR, controlledVocabulary.getBindingResult().getAll());
        }
        
        if(controlledVocabularyRepo.findByName(controlledVocabulary.getName()) != null) {
            return new ApiResponse(VALIDATION_ERROR, controlledVocabulary.getName() + " is already a controlled vocabulary!");
        }
        
        ControlledVocabulary newControlledVocabulary = controlledVocabularyRepo.create(controlledVocabulary.getName(), controlledVocabulary.getLanguage());
        

        // TODO: logging

        logger.info("Created controlled vocabulary " + newControlledVocabulary.getName());

        simpMessagingTemplate.convertAndSend("/channel/settings/controlled-vocabulary", new ApiResponse(SUCCESS, getAll()));
        simpMessagingTemplate.convertAndSend("/channel/settings/controlled-vocabulary/change", new ApiResponse(SUCCESS));

        return new ApiResponse(SUCCESS);
    }

    /**
     * Endpoint to update controlled vacabulary.
     * 
     * @param data
     *            Json input data with request
     * @return ApiResponse indicating success or error
     */
    @ApiMapping("/update")
    @Auth(role = "ROLE_MANAGER")
    @Transactional
    public ApiResponse updateControlledVocabulary(@ApiValidatedModel ControlledVocabulary controlledVocabulary) {
        // make sure we don't have any deserialization errors
        if(controlledVocabulary.getBindingResult().hasErrors()){
            return new ApiResponse(VALIDATION_ERROR, controlledVocabulary.getBindingResult().getAll());
        }
        
        //make sure we're receiving an Id from the front-end
        if (controlledVocabulary.getId() == null) {
                return new ApiResponse(VALIDATION_ERROR, "Id required to update graduation month!");
        }
        
        ControlledVocabulary controlledVocabularyToUpdate = controlledVocabularyRepo.findOne(controlledVocabulary.getId());
        ControlledVocabulary controlledVocabularyExistingName = controlledVocabularyRepo.findByName(controlledVocabulary.getName());

        // make sure we won't have any unique constraint violations
        if(controlledVocabularyExistingName != null) {
            return new ApiResponse(VALIDATION_ERROR, controlledVocabulary.getName() + " is already a controlled vocabulary!");
        }
        
        // make sure we're updating an existing controlled vocabulary
        if(controlledVocabularyToUpdate == null) {
            return new ApiResponse(VALIDATION_ERROR, controlledVocabulary.getName() + " can't be updated, it doesn't exist!");
        }
        
        // save!
        controlledVocabularyToUpdate.setName(controlledVocabulary.getName());
        controlledVocabulary = controlledVocabularyRepo.save(controlledVocabularyToUpdate);

        // TODO: logging

        logger.info("Updated controlled vocabulary with name " + controlledVocabulary.getName());

        simpMessagingTemplate.convertAndSend("/channel/settings/controlled-vocabulary", new ApiResponse(SUCCESS, getAll()));
        simpMessagingTemplate.convertAndSend("/channel/settings/controlled-vocabulary/change", new ApiResponse(SUCCESS));

        return new ApiResponse(SUCCESS);
    }

    /**
     * Endpoint to remove controlled vocabulary by provided index
     * 
     * @param indexString
     *            index of controlled vocabulary to remove
     * @return ApiResponse indicating success or error
     */
    @ApiMapping("/remove/{indexString}")
    @Auth(role = "ROLE_MANAGER")
    @Transactional
    public ApiResponse removeControlledVocabulary(@ApiVariable String indexString) {
        Long index = -1L;

        try {
            index = Long.parseLong(indexString);
        } catch (NumberFormatException nfe) {
            return new ApiResponse(ERROR, "Id is not a valid controlled vocabulary order!");
        }

        if (index >= 0) {
            controlledVocabularyRepo.remove(index);
        } else {
            return new ApiResponse(ERROR, "Id is not a valid controlled vocabulary order!");
        }

        logger.info("Deleted controlled vocabulary with order " + index);

        simpMessagingTemplate.convertAndSend("/channel/settings/controlled-vocabulary", new ApiResponse(SUCCESS, getAll()));

        return new ApiResponse(SUCCESS);
    }

    /**
     * Endpoint to reorder controlled vocabulary.
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
    public ApiResponse reorderControlledVocabulary(@ApiVariable String src, @ApiVariable String dest) {
        Long intSrc = Long.parseLong(src);
        Long intDest = Long.parseLong(dest);
        controlledVocabularyRepo.reorder(intSrc, intDest);
        simpMessagingTemplate.convertAndSend("/channel/settings/controlled-vocabulary", new ApiResponse(SUCCESS, getAll()));
        return new ApiResponse(SUCCESS);
    }

    /**
     * Endpoint to sort controlled vocabulary.
     * 
     * @param column
     *            column to sort by
     * @return ApiResponse indicating success
     */
    @ApiMapping("/sort/{column}")
    @Auth(role = "ROLE_MANAGER")
    @Transactional
    public ApiResponse sortControlledVocabulary(@ApiVariable String column) {
        controlledVocabularyRepo.sort(column);
        simpMessagingTemplate.convertAndSend("/channel/settings/controlled-vocabulary", new ApiResponse(SUCCESS, getAll()));
        return new ApiResponse(SUCCESS);
    }

    /**
     * Endoing to export controlled vocabulary to populate csv
     * 
     * @param name
     *            name of controlled vocabulary to export
     * @return ApiResponse with map containing csv content
     */
    @ApiMapping("/export/{name}")
    @Auth(role = "ROLE_MANAGER")
    @Transactional
    public ApiResponse exportControlledVocabulary(@ApiVariable String name) {
        Map<String, Object> map = new HashMap<String, Object>();        
        ControlledVocabulary cv = controlledVocabularyRepo.findByName(name);        
        map.put("headers", Arrays.asList(new String[] { "name", "definition", "identifier" }));
        List<List<Object>> rows = new ArrayList<List<Object>>();
        cv.getDictionary().forEach(vocabularyWord -> {
            List<Object> row = new ArrayList<Object>();
            if (vocabularyWord.getClass().equals(VocabularyWord.class)) {
                VocabularyWord actualVocabularyWord = (VocabularyWord) vocabularyWord;
                row.add(actualVocabularyWord.getName());
                row.add(actualVocabularyWord.getDefinition());
                row.add(actualVocabularyWord.getIdentifier());
            } else {
                row.add(vocabularyWord);
            }
            rows.add(row);
        });
        map.put("rows", rows);
        return new ApiResponse(SUCCESS, map);
    }

    /**
     * Endpoint to get the import status of a given controlled vocabulary.
     * 
     * @param name
     *            controlled vocabulary name
     * @return ApiResponse with a boolean whether import in progress or not
     */
    @ApiMapping(value = "/status/{name}", method = RequestMethod.POST)
    @Auth(role = "ROLE_MANAGER")
    public ApiResponse importControlledVocabularyStatus(@ApiVariable String name) {
        return new ApiResponse(SUCCESS, controlledVocabularyCachingService.doesControlledVocabularyExist(name));
    }

    /**
     * Endpoint to cancel an inport of a given controlled vocabulary. Removes ControlledVocabularyCache from the ControlledVocabularyCacheService.
     * 
     * @param name
     *            controlled vocabulary name
     * @return ApiResponse indicating success
     */
    @ApiMapping(value = "/cancel/{name}", method = RequestMethod.POST)
    @Auth(role = "ROLE_MANAGER")
    public ApiResponse cancelImportControlledVocabulary(@ApiVariable String name) {
        controlledVocabularyCachingService.removeControlledVocabularyCache(name);
        simpMessagingTemplate.convertAndSend("/channel/settings/controlled-vocabulary/change", new ApiResponse(SUCCESS));
        return new ApiResponse(SUCCESS);
    }

    /**
     * Enpoint to compare a csv of controlled vocabulary to an existing controlled vocabulary.
     * 
     * @param name
     *            controlled vocabulary to compare with
     * @param inputStream
     *            csv bitstream
     * @return ApiResponse with map of new words, updating words, and duplicate words
     */
    @ApiMapping(value = "/compare/{name}", method = RequestMethod.POST)
    @Auth(role = "ROLE_MANAGER")
    @Transactional
    public ApiResponse compareControlledVocabulary(@ApiVariable String name, @InputStream Object inputStream) {

        String[] rows;
        try {
            rows = inputStreamToRows(inputStream);
        } catch (IOException e) {
            return new ApiResponse(ERROR, "Invalid input.");
        }

        List<VocabularyWord> newWords = new ArrayList<VocabularyWord>();
        List<VocabularyWord> repeatedWords = new ArrayList<VocabularyWord>();
        List<VocabularyWord[]> updatingWords = new ArrayList<VocabularyWord[]>();

        ControlledVocabulary controlledVocabulary = controlledVocabularyRepo.findByName(name);
        List<Object> words = controlledVocabulary.getDictionary();

        for (String row : rows) {

            String[] cols = new String[] { "", "", "" };

            String[] temp = row.split(",");
            for (int i = 0; i < temp.length; i++) {
                cols[i] = temp[i] != null ? temp[i] : "";
            }

            VocabularyWord currentVocabularyWord = new VocabularyWord(cols[0], cols[1], cols[2]);

            boolean isRepeat = false;
            for (VocabularyWord newWord : newWords) {
                if (newWord.getName().equals(cols[0])) {
                    repeatedWords.add(currentVocabularyWord);
                    isRepeat = true;
                    break;
                }
            }

            if (!isRepeat) {
                for (VocabularyWord[] updatingWord : updatingWords) {
                    if (updatingWord[0].getName().equals(cols[0])) {
                        repeatedWords.add(currentVocabularyWord);
                        isRepeat = true;
                        break;
                    }
                }
            }

            if (!isRepeat) {
                boolean isNew = true;
                for (Object word : words) {
                    VocabularyWord vocabularyWord = (VocabularyWord) word;
                    if (cols[0].equals(vocabularyWord.getName())) {

                        String definition = vocabularyWord.getDefinition();
                        String identifier = vocabularyWord.getIdentifier();

                        boolean change = false;

                        if (definition != null && !cols[1].equals(definition)) {
                            change = true;
                        }

                        if (identifier != null && !cols[2].equals(identifier)) {
                            change = true;
                        }

                        if (change) {
                            updatingWords.add(new VocabularyWord[] { vocabularyWord, currentVocabularyWord });
                        }

                        isNew = false;
                        break;
                    }
                }
                if (isNew) {
                    newWords.add(currentVocabularyWord);
                }
            }
        }

        Map<String, Object> wordsMap = new HashMap<String, Object>();
        wordsMap.put("new", newWords);
        wordsMap.put("repeats", repeatedWords);
        wordsMap.put("updating", updatingWords);

        ControlledVocabularyCache cvCache = new ControlledVocabularyCache(new Date().getTime(), controlledVocabulary.getName());
        cvCache.setNewVocabularyWords(newWords);
        cvCache.setDuplicateVocabularyWords(repeatedWords);
        cvCache.setUpdatingVocabularyWords(updatingWords);
        controlledVocabularyCachingService.addControlledVocabularyCache(cvCache);

        simpMessagingTemplate.convertAndSend("/channel/settings/controlled-vocabulary/change", new ApiResponse(SUCCESS));
        return new ApiResponse(SUCCESS, wordsMap);
    }

    /**
     * Endpoint to import controlled vocabulary after confirmation.
     * 
     * @param name
     *            controlled vocabulary name
     * @return ApiReponse indicating success
     */
    @ApiMapping(value = "/import/{name}", method = RequestMethod.POST)
    @Auth(role = "ROLE_MANAGER")
    @Transactional
    public ApiResponse importControlledVocabulary(@ApiVariable String name) {

        ControlledVocabulary controlledVocabulary = controlledVocabularyRepo.findByName(name);

        ControlledVocabularyCache cvCache = controlledVocabularyCachingService.getControlledVocabularyCache(controlledVocabulary.getName());
        
        if(cvCache != null) {
            cvCache.getNewVocabularyWords().parallelStream().forEach(newVocabularyWord -> {
                newVocabularyWord = vocabularyWordRepo.create(controlledVocabulary, newVocabularyWord.getName(), newVocabularyWord.getDefinition(), newVocabularyWord.getIdentifier());
                controlledVocabulary.addValue(newVocabularyWord);
            });
    
            cvCache.getUpdatingVocabularyWords().parallelStream().forEach(updatingVocabularyWord -> {
                VocabularyWord updatedVocabularyWord = vocabularyWordRepo.findByNameAndControlledVocabulary(updatingVocabularyWord[1].getName(), controlledVocabulary);
                updatedVocabularyWord.setDefinition(updatingVocabularyWord[1].getDefinition());
                updatedVocabularyWord.setIdentifier(updatingVocabularyWord[1].getIdentifier());
                updatedVocabularyWord = vocabularyWordRepo.save(updatedVocabularyWord);
            });
    
            controlledVocabularyRepo.save(controlledVocabulary);
    
            controlledVocabularyCachingService.removeControlledVocabularyCache(controlledVocabulary.getName());
    
            simpMessagingTemplate.convertAndSend("/channel/settings/controlled-vocabulary/change", new ApiResponse(SUCCESS));
        }
        else {
            return new ApiResponse(ERROR, "Controlled vocabulary " + name + " is not cached for import.");
        }
        
        return new ApiResponse(SUCCESS);
    }

    /**
     * Converts input stream to array of strings which represent the rows
     * 
     * @param inputStream
     *            csv bitstream
     * @return string array of the csv rows
     * @throws IOException
     */
    private String[] inputStreamToRows(Object inputStream) throws IOException {
        String csvString = null;
        String[] imageData = IOUtils.toString((ServletInputStream) inputStream, "UTF-8").split(";");
        String[] encodedData = imageData[1].split(",");
        csvString = new String(Base64.getDecoder().decode(encodedData[1]));
        String[] rows = csvString.split("\\R");
        return Arrays.copyOfRange(rows, 1, rows.length);
    }

}
