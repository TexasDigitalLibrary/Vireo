package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;
import static edu.tamu.framework.enums.ApiResponseType.VALIDATION_WARNING;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.tdl.vireo.model.ControlledVocabulary;
import org.tdl.vireo.model.ControlledVocabularyCache;
import org.tdl.vireo.model.VocabularyWord;
import org.tdl.vireo.model.repo.ControlledVocabularyRepo;
import org.tdl.vireo.model.repo.VocabularyWordRepo;
import org.tdl.vireo.service.ControlledVocabularyCachingService;
import org.tdl.vireo.service.ValidationService;

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.ApiValidatedModel;
import edu.tamu.framework.aspect.annotation.ApiVariable;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.aspect.annotation.InputStream;
import edu.tamu.framework.model.ApiResponse;
import edu.tamu.framework.validation.ModelBindingResult;

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
    
    @Autowired
    private ValidationService validationService;

    /**
     * Endpoint to request all controlled vocabulary.
     * 
     * @return ApiResponse with all controlled vocabulary
     */
    @ApiMapping("/all")
    @Auth(role = "MANAGER")
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
    @Auth(role = "MANAGER")
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
    @Auth(role = "MANAGER")
    @Transactional
    public ApiResponse createControlledVocabulary(@ApiValidatedModel ControlledVocabulary controlledVocabulary) {
        
        // will attach any errors to the BindingResult when validating the incoming controlledVocabulary
        controlledVocabulary = controlledVocabularyRepo.validateCreate(controlledVocabulary);
        
        // build a response based on the BindingResult state
        ApiResponse response = validationService.buildResponse(controlledVocabulary);
        
        switch(response.getMeta().getType()){
            case SUCCESS:
            case VALIDATION_INFO:
                logger.info("Creating controlled vocabulary with name " + controlledVocabulary.getName());
                controlledVocabularyRepo.create(controlledVocabulary.getName(), controlledVocabulary.getLanguage());
                simpMessagingTemplate.convertAndSend("/channel/settings/controlled-vocabulary", new ApiResponse(SUCCESS, getAll()));
                simpMessagingTemplate.convertAndSend("/channel/settings/controlled-vocabulary/change", new ApiResponse(SUCCESS));
                break;
            case VALIDATION_WARNING:
                simpMessagingTemplate.convertAndSend("/channel/settings/controlled-vocabulary", new ApiResponse(VALIDATION_WARNING, getAll()));
                simpMessagingTemplate.convertAndSend("/channel/settings/controlled-vocabulary/change", new ApiResponse(VALIDATION_WARNING));
                break;
            default:
                logger.warn("Couldn't create controlled vocabulary with name " + controlledVocabulary.getName() + " because: " + response.getMeta().getType());
                break;
        }

        return response;
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
    @Transactional
    public ApiResponse updateControlledVocabulary(@ApiValidatedModel ControlledVocabulary controlledVocabulary) {
        // will attach any errors to the BindingResult when validating the incoming controlledVocabulary
        controlledVocabulary = controlledVocabularyRepo.validateUpdate(controlledVocabulary);
        
        // build a response based on the BindingResult state
        ApiResponse response = validationService.buildResponse(controlledVocabulary);
        
        switch(response.getMeta().getType()){
            case SUCCESS:
            case VALIDATION_INFO:
                logger.info("Updating controlled vocabulary with name " + controlledVocabulary.getName());
                controlledVocabularyRepo.save(controlledVocabulary);
                simpMessagingTemplate.convertAndSend("/channel/settings/controlled-vocabulary", new ApiResponse(SUCCESS, getAll()));
                simpMessagingTemplate.convertAndSend("/channel/settings/controlled-vocabulary/change", new ApiResponse(SUCCESS));
                break;
            case VALIDATION_WARNING:
                simpMessagingTemplate.convertAndSend("/channel/settings/controlled-vocabulary", new ApiResponse(VALIDATION_WARNING, getAll()));
                simpMessagingTemplate.convertAndSend("/channel/settings/controlled-vocabulary/change", new ApiResponse(VALIDATION_WARNING));
                break;
            default:
                logger.warn("Couldn't update controlled vocabulary with name " + controlledVocabulary.getName() + " because: " + response.getMeta().getType());
                break;
        }

        return response;
    }

    /**
     * Endpoint to remove controlled vocabulary by provided index
     * 
     * @param idString
     *            index of controlled vocabulary to remove
     * @return ApiResponse indicating success or error
     */
    @ApiMapping("/remove/{idString}")
    @Auth(role = "MANAGER")
    @Transactional
    public ApiResponse removeControlledVocabulary(@ApiVariable String idString) {
        
        // create a ModelBindingResult since we have an @ApiVariable coming in (and not a @ApiValidatedModel)
        ModelBindingResult modelBindingResult = new ModelBindingResult(idString, "controlled_vocabulary_id");
        
        // will attach any errors to the BindingResult when validating the incoming idString
        ControlledVocabulary controlledVocabulary = controlledVocabularyRepo.validateRemove(idString, modelBindingResult);
        
        // build a response based on the BindingResult state
        ApiResponse response = validationService.buildResponse(modelBindingResult);
        
        switch(response.getMeta().getType()){
            case SUCCESS:
            case VALIDATION_INFO:
                logger.info("Removing Controlled Vocabulary with id " + idString);
                controlledVocabularyRepo.remove(controlledVocabulary);
                simpMessagingTemplate.convertAndSend("/channel/settings/controlled-vocabulary", new ApiResponse(SUCCESS, getAll()));
                simpMessagingTemplate.convertAndSend("/channel/settings/controlled-vocabulary/change", new ApiResponse(SUCCESS));
                break;
            case VALIDATION_WARNING:
                simpMessagingTemplate.convertAndSend("/channel/settings/controlled-vocabulary", new ApiResponse(VALIDATION_WARNING, getAll()));
                simpMessagingTemplate.convertAndSend("/channel/settings/controlled-vocabulary/change", new ApiResponse(VALIDATION_WARNING));
                break;
            default:
                logger.warn("Couldn't remove Controlled Vocabulary with id " + idString + " because: " + response.getMeta().getType());
                break;
        }
    
        return response;
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
    @Transactional
    public ApiResponse reorderControlledVocabulary(@ApiVariable String src, @ApiVariable String dest) {
        
        // create a ModelBindingResult since we have an @ApiVariable coming in (and not a @ApiValidatedModel)
        ModelBindingResult modelBindingResult = new ModelBindingResult(src, "controlled-vocabulary");
        
        // will attach any errors to the BindingResult when validating the incoming src and dest
        Long longSrc = validationService.validateLong(src, "position", modelBindingResult);
        Long longDest = validationService.validateLong(dest, "position", modelBindingResult);
        
        // build a response based on the BindingResult state
        ApiResponse response = validationService.buildResponse(modelBindingResult);
        
        switch(response.getMeta().getType()){
            case SUCCESS:
            case VALIDATION_INFO:
                logger.info("Reordering controlled vocabularies");
                controlledVocabularyRepo.reorder(longSrc, longDest);
                simpMessagingTemplate.convertAndSend("/channel/settings/controlled-vocabulary", new ApiResponse(SUCCESS, getAll()));
                simpMessagingTemplate.convertAndSend("/channel/settings/controlled-vocabulary/change", new ApiResponse(SUCCESS));
                break;
            case VALIDATION_WARNING:
                simpMessagingTemplate.convertAndSend("/channel/settings/controlled-vocabulary", new ApiResponse(VALIDATION_WARNING, getAll()));
                simpMessagingTemplate.convertAndSend("/channel/settings/controlled-vocabulary/change", new ApiResponse(VALIDATION_WARNING));
                break;
            default:
                logger.warn("Couldn't reorder controlled vocabularies because: " + response.getMeta().getType());
                break;
        }
        
        return response;
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
    @Transactional
    public ApiResponse sortControlledVocabulary(@ApiVariable String column) {
        
        // create a ModelBindingResult since we have an @ApiVariable coming in (and not a @ApiValidatedModel)
        ModelBindingResult modelBindingResult = new ModelBindingResult(column, "controlled-vocabulary");
        
        // will attach any errors to the BindingResult when validating the incoming column
        validationService.validateColumn(ControlledVocabulary.class, column, modelBindingResult);
        
        // build a response based on the BindingResult state
        ApiResponse response = validationService.buildResponse(modelBindingResult);
        
        switch(response.getMeta().getType()){
            case SUCCESS:
            case VALIDATION_INFO:
                logger.info("Sorting controlled vocabularies by " + column);
                controlledVocabularyRepo.sort(column);
                simpMessagingTemplate.convertAndSend("/channel/settings/controlled-vocabulary", new ApiResponse(SUCCESS, getAll()));
                simpMessagingTemplate.convertAndSend("/channel/settings/controlled-vocabulary/change", new ApiResponse(SUCCESS));
                break;
            case VALIDATION_WARNING:
                simpMessagingTemplate.convertAndSend("/channel/settings/controlled-vocabulary", new ApiResponse(VALIDATION_WARNING, getAll()));
                simpMessagingTemplate.convertAndSend("/channel/settings/controlled-vocabulary/change", new ApiResponse(VALIDATION_WARNING));
                break;
            default:
                logger.warn("Couldn't sort controlled vocabularies because: " + response.getMeta().getType());
                break;
        }
    
        return response;
    }

    /**
     * Endpoint to export controlled vocabulary to populate csv
     * 
     * @param name
     *            name of controlled vocabulary to export
     * @return ApiResponse with map containing csv content
     */
    @ApiMapping("/export/{name}")
    @Auth(role = "MANAGER")
    @Transactional
    public ApiResponse exportControlledVocabulary(@ApiVariable String name) {
        
        // create a ModelBindingResult since we have an @ApiVariable coming in (and not a @ApiValidatedModel)
        ModelBindingResult modelBindingResult = new ModelBindingResult(name, "controlled-vocabulary");
        
        // will attach any errors to the BindingResult when validating the incoming cv name
        ControlledVocabulary cv = controlledVocabularyRepo.validateExport(name, modelBindingResult);
        
        // build a response based on the BindingResult state
        ApiResponse response = validationService.buildResponse(modelBindingResult);
        
        switch(response.getMeta().getType()){
            case SUCCESS:
            case VALIDATION_INFO:
                logger.info("Exporting controlled vocabulary for " + name);
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
                response.getPayload().put(map.getClass().getSimpleName(), map);
                break;
            case VALIDATION_WARNING:
                simpMessagingTemplate.convertAndSend("/channel/settings/controlled-vocabulary", new ApiResponse(VALIDATION_WARNING, getAll()));
                break;
            default:
                logger.warn("Couldn't export controlled vocabulary because: " + response.getMeta().getType());
                break;
        }
    
        return response;
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
        
        // create a ModelBindingResult since we have an @ApiVariable coming in (and not a @ApiValidatedModel)
        ModelBindingResult modelBindingResult = new ModelBindingResult(name, "controlled-vocabulary");
        
        if(!controlledVocabularyCachingService.doesControlledVocabularyExist(name)){
            modelBindingResult.addError(new ObjectError("controlledVocabulary", "Cannot cancel import for cached Controlled Vocabulary, name did not exist!"));
        }
        
        // build a response based on the BindingResult state
        ApiResponse response = validationService.buildResponse(modelBindingResult);
        
        switch(response.getMeta().getType()){
            case SUCCESS:
            case VALIDATION_INFO:
                logger.info("Cancelling import for cached controlled vocabulary " + name);
                controlledVocabularyCachingService.removeControlledVocabularyCache(name);
                simpMessagingTemplate.convertAndSend("/channel/settings/controlled-vocabulary/change", new ApiResponse(SUCCESS));
                break;
            case VALIDATION_WARNING:
                simpMessagingTemplate.convertAndSend("/channel/settings/controlled-vocabulary", new ApiResponse(VALIDATION_WARNING, getAll()));
                break;
            default:
                logger.warn("Couldn't cancel import for cached controlled vocabulary because: " + response.getMeta().getType());
                break;
        }
    
        return response;
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
    @Auth(role = "MANAGER")
    @Transactional
    public ApiResponse compareControlledVocabulary(@ApiVariable String name, @InputStream Object inputStream) {
        
        // create a ModelBindingResult since we have an @ApiVariable coming in (and not a @ApiValidatedModel)
        ModelBindingResult modelBindingResult = new ModelBindingResult(name, "controlled-vocabulary");
        
        // will attach any errors to the BindingResult when validating the incoming name and inputStream
        ControlledVocabulary controlledVocabulary = controlledVocabularyRepo.validateCompareCV(name, modelBindingResult);
        String[] rows = controlledVocabularyRepo.validateCompareRows(inputStream, modelBindingResult);
        
        // build a response based on the BindingResult state
        ApiResponse response = validationService.buildResponse(modelBindingResult);
        
        Map<String, Object> wordsMap = null;
        
        switch(response.getMeta().getType()){
            case SUCCESS:
            case VALIDATION_INFO:
                logger.info("Comparing controlled vocabulary " + name);
                wordsMap = cacheImport(controlledVocabulary, rows);
                simpMessagingTemplate.convertAndSend("/channel/settings/controlled-vocabulary/change", new ApiResponse(SUCCESS));
                response.getPayload().put(wordsMap.getClass().getSimpleName(), wordsMap);
                break;
            case VALIDATION_WARNING:
                simpMessagingTemplate.convertAndSend("/channel/settings/controlled-vocabulary", new ApiResponse(VALIDATION_WARNING, getAll()));
                simpMessagingTemplate.convertAndSend("/channel/settings/controlled-vocabulary/change", new ApiResponse(VALIDATION_WARNING));
                break;
            default:
                logger.warn("Couldn't compare controlled vocabularies because: " + response.getMeta().getType());
                break;
        }
    
        return response;
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
        
        // create a ModelBindingResult since we have an @ApiVariable coming in (and not a @ApiValidatedModel)
        ModelBindingResult modelBindingResult = new ModelBindingResult(name, "controlled-vocabulary");
        
        // will attach any errors to the BindingResult when validating the incoming name
        ControlledVocabulary controlledVocabulary = controlledVocabularyRepo.validateImport(name, modelBindingResult);

        ControlledVocabularyCache cvCache = controlledVocabularyCachingService.getControlledVocabularyCache(controlledVocabulary.getName());
        if(cvCache == null) {
            modelBindingResult.addError(new ObjectError("controlledVocabulary", "ControlledVocabulary " + name + " is not cached for import"));
        }
        
        // build a response based on the BindingResult state
        ApiResponse response = validationService.buildResponse(modelBindingResult);
        
        switch(response.getMeta().getType()){
            case SUCCESS:
            case VALIDATION_INFO:
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
                controlledVocabularyRepo.save(controlledVocabulary);
                controlledVocabularyCachingService.removeControlledVocabularyCache(controlledVocabulary.getName());
                simpMessagingTemplate.convertAndSend("/channel/settings/controlled-vocabulary/change", new ApiResponse(SUCCESS));
                break;
            case VALIDATION_WARNING:
                simpMessagingTemplate.convertAndSend("/channel/settings/controlled-vocabulary", new ApiResponse(VALIDATION_WARNING, getAll()));
                simpMessagingTemplate.convertAndSend("/channel/settings/controlled-vocabulary/change", new ApiResponse(VALIDATION_WARNING));
                break;
            default:
                logger.warn("Couldn't compare controlled vocabularies because: " + response.getMeta().getType());
                break;
        }
    
        return response;
    }
    
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
}
