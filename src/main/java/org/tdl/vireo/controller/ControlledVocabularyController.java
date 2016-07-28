package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;
import static edu.tamu.framework.enums.BusinessValidationType.CREATE;
import static edu.tamu.framework.enums.BusinessValidationType.DELETE;
import static edu.tamu.framework.enums.BusinessValidationType.EXISTS;
import static edu.tamu.framework.enums.BusinessValidationType.NONEXISTS;
import static edu.tamu.framework.enums.BusinessValidationType.UPDATE;
import static edu.tamu.framework.enums.MethodValidationType.REORDER;
import static edu.tamu.framework.enums.MethodValidationType.SORT;

import java.io.IOException;
import java.io.InputStream;
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
import edu.tamu.framework.aspect.annotation.ApiValidation;
import edu.tamu.framework.aspect.annotation.ApiVariable;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.aspect.annotation.ApiInputStream;
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
     * Endpoint to request all controlled vocabulary.
     * 
     * @return ApiResponse with all controlled vocabulary
     */
    @Transactional
    @ApiMapping("/all")
    @Auth(role = "MANAGER")    
    public ApiResponse getAllControlledVocabulary() {
        return new ApiResponse(SUCCESS, controlledVocabularyRepo.findAllByOrderByPositionAsc());
    }

    /**
     * Endpoint to request controlled vocabulary by name.
     * 
     * @param name
     *            Name of controlled vocabulary
     * @return ApiResponse with requested controlled vocabulary
     */
    @Transactional
    @ApiMapping("/{name}")
    @Auth(role = "MANAGER")    
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
    @Auth(role = "MANAGER")
    @ApiValidation(business = { @ApiValidation.Business(value = CREATE), @ApiValidation.Business(value = EXISTS) })
    public ApiResponse createControlledVocabulary(@ApiValidatedModel ControlledVocabulary controlledVocabulary) {
        logger.info("Creating controlled vocabulary with name " + controlledVocabulary.getName());
        controlledVocabulary = controlledVocabularyRepo.create(controlledVocabulary.getName(), controlledVocabulary.getLanguage());
        simpMessagingTemplate.convertAndSend("/channel/settings/controlled-vocabulary", new ApiResponse(SUCCESS, controlledVocabularyRepo.findAllByOrderByPositionAsc()));
        simpMessagingTemplate.convertAndSend("/channel/settings/controlled-vocabulary/change", new ApiResponse(SUCCESS));
        return new ApiResponse(SUCCESS, controlledVocabulary);
    }

    /**
     * Endpoint to update controlled vacabulary.
     * 
     * @param data
     *            Json input data with request
     * @return ApiResponse indicating success or error
     */
    @ApiMapping("/update")
    @Auth(role = "MANAGER")
    @ApiValidation(business = { @ApiValidation.Business(value = UPDATE), @ApiValidation.Business(value = NONEXISTS) })
    public ApiResponse updateControlledVocabulary(@ApiValidatedModel ControlledVocabulary controlledVocabulary) {
        logger.info("Updating controlled vocabulary with name " + controlledVocabulary.getName());
        controlledVocabulary = controlledVocabularyRepo.save(controlledVocabulary);
        simpMessagingTemplate.convertAndSend("/channel/settings/controlled-vocabulary", new ApiResponse(SUCCESS, controlledVocabularyRepo.findAllByOrderByPositionAsc()));
        simpMessagingTemplate.convertAndSend("/channel/settings/controlled-vocabulary/change", new ApiResponse(SUCCESS));
        return new ApiResponse(SUCCESS, controlledVocabulary);
    }

    /**
     * Endpoint to remove controlled vocabulary by provided index
     * 
     * @param data
     *            Json input data with request
     * @return ApiResponse indicating success or error
     */
    @ApiMapping("/remove")
    @Auth(role = "MANAGER")
    @ApiValidation(business = { @ApiValidation.Business(value = DELETE), @ApiValidation.Business(value = NONEXISTS) })
    public ApiResponse removeControlledVocabulary(@ApiValidatedModel ControlledVocabulary controlledVocabulary) {
        logger.info("Removing Controlled Vocabulary with name " + controlledVocabulary.getName());
        controlledVocabularyRepo.remove(controlledVocabulary);
        simpMessagingTemplate.convertAndSend("/channel/settings/controlled-vocabulary", new ApiResponse(SUCCESS, controlledVocabularyRepo.findAllByOrderByPositionAsc()));
        simpMessagingTemplate.convertAndSend("/channel/settings/controlled-vocabulary/change", new ApiResponse(SUCCESS));
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
    @Auth(role = "MANAGER")
    @ApiValidation(method = { @ApiValidation.Method(value = REORDER, model = ControlledVocabulary.class, params = { "0", "1" }) })
    public ApiResponse reorderControlledVocabulary(@ApiVariable Long src, @ApiVariable Long dest) {
        logger.info("Reordering controlled vocabularies");
        controlledVocabularyRepo.reorder(src, dest);
        simpMessagingTemplate.convertAndSend("/channel/settings/controlled-vocabulary", new ApiResponse(SUCCESS, controlledVocabularyRepo.findAllByOrderByPositionAsc()));
        simpMessagingTemplate.convertAndSend("/channel/settings/controlled-vocabulary/change", new ApiResponse(SUCCESS));
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
    @Auth(role = "MANAGER")
    @ApiValidation(method = { @ApiValidation.Method(value = SORT, model = ControlledVocabulary.class, params = { "0" }) })
    public ApiResponse sortControlledVocabulary(@ApiVariable String column) {
        logger.info("Sorting controlled vocabularies by " + column);
        controlledVocabularyRepo.sort(column);
        simpMessagingTemplate.convertAndSend("/channel/settings/controlled-vocabulary", new ApiResponse(SUCCESS, controlledVocabularyRepo.findAllByOrderByPositionAsc()));
        simpMessagingTemplate.convertAndSend("/channel/settings/controlled-vocabulary/change", new ApiResponse(SUCCESS));
        return new ApiResponse(SUCCESS);
    }

    /**
     * Endpoint to export controlled vocabulary to populate csv
     * 
     * @param name
     *            name of controlled vocabulary to export
     * @return ApiResponse with map containing csv content
     */
    @Transactional
    @ApiMapping("/export/{name}")
    @Auth(role = "MANAGER")    
    public ApiResponse exportControlledVocabulary(@ApiVariable String name) {
        logger.info("Exporting controlled vocabulary for " + name);
        ControlledVocabulary cv = controlledVocabularyRepo.findByName(name);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("headers", Arrays.asList(new String[] { "name", "definition", "identifier" }));
        List<List<Object>> rows = new ArrayList<List<Object>>();
        cv.getDictionary().forEach(vocabularyWord -> {
            List<Object> row = new ArrayList<Object>();
            VocabularyWord actualVocabularyWord = (VocabularyWord) vocabularyWord;
            row.add(actualVocabularyWord.getName());
            row.add(actualVocabularyWord.getDefinition());
            row.add(actualVocabularyWord.getIdentifier());
            rows.add(row);
        });
        map.put("rows", rows);
        simpMessagingTemplate.convertAndSend("/channel/settings/controlled-vocabulary", new ApiResponse(SUCCESS, controlledVocabularyRepo.findAllByOrderByPositionAsc()));
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
    @Auth(role = "MANAGER")
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
    @Auth(role = "MANAGER")
    public ApiResponse cancelImportControlledVocabulary(@ApiVariable String name) {
        logger.info("Cancelling import for cached controlled vocabulary " + name);
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
     * @throws IOException 
     */
    // TODO: implement controller advice to catch exception and handle gracefully
    @Transactional
    @ApiMapping(value = "/compare/{name}", method = RequestMethod.POST)
    @Auth(role = "MANAGER")    
    public ApiResponse compareControlledVocabulary(@ApiVariable String name, @ApiInputStream InputStream inputStream) throws IOException {
        logger.info("Comparing controlled vocabulary " + name);
        ControlledVocabulary controlledVocabulary = controlledVocabularyRepo.findByName(name);
        String[] rows = inputStreamToRows(inputStream);
        Map<String, Object> wordsMap = cacheImport(controlledVocabulary, rows);
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
    @Transactional
    @ApiMapping(value = "/import/{name}", method = RequestMethod.POST)
    @Auth(role = "MANAGER")    
    public ApiResponse importControlledVocabulary(@ApiVariable String name) {
        logger.info("Inporting controlled vocabulary " + name);
        ControlledVocabulary controlledVocabulary = controlledVocabularyRepo.findByName(name);
        ControlledVocabularyCache cvCache = controlledVocabularyCachingService.getControlledVocabularyCache(controlledVocabulary.getName());
        logger.info("Comparing controlled vocabulary " + name);
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
        ControlledVocabulary savedControlledVocabulary = controlledVocabularyRepo.save(controlledVocabulary);
        controlledVocabularyCachingService.removeControlledVocabularyCache(controlledVocabulary.getName());
        simpMessagingTemplate.convertAndSend("/channel/settings/controlled-vocabulary/change", new ApiResponse(SUCCESS));
        simpMessagingTemplate.convertAndSend("/channel/settings/controlled-vocabulary", new ApiResponse(SUCCESS, controlledVocabularyRepo.findAllByOrderByPositionAsc()));
        return new ApiResponse(SUCCESS, savedControlledVocabulary);
    }
       
    private Map<String, Object> cacheImport(ControlledVocabulary controlledVocabulary, String[] rows){
        
        List<VocabularyWord> newWords = new ArrayList<VocabularyWord>();
        List<VocabularyWord> repeatedWords = new ArrayList<VocabularyWord>();
        List<VocabularyWord[]> updatingWords = new ArrayList<VocabularyWord[]>();

        List<VocabularyWord> words = controlledVocabulary.getDictionary();

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
                for (VocabularyWord word : words) {
                    if (cols[0].equals(word.getName())) {

                        String definition = word.getDefinition();
                        String identifier = word.getIdentifier();

                        boolean change = false;

                        if (definition != null && !cols[1].equals(definition)) {
                            change = true;
                        }

                        if (identifier != null && !cols[2].equals(identifier)) {
                            change = true;
                        }

                        if (change) {
                            updatingWords.add(new VocabularyWord[] { word, currentVocabularyWord });
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
        
        return wordsMap;
    }
    
    /**
     * Converts input stream to array of strings which represent the rows
     * 
     * @param ServletInputStream
     *            csv bitstream
     * @return string array of the csv rows
     * @throws IOException
     */
    private String[] inputStreamToRows(java.io.InputStream bufferedIn) throws IOException {
        String csvString = null;
        String[] imageData = IOUtils.toString(bufferedIn, "UTF-8").split(";");
        String[] encodedData = imageData[1].split(",");
        csvString = new String(Base64.getDecoder().decode(encodedData[1]));
        String[] rows = csvString.split("\\R");
        return Arrays.copyOfRange(rows, 1, rows.length);
    }
    
}
