package org.tdl.vireo.controller;

import static edu.tamu.weaver.response.ApiStatus.SUCCESS;
import static edu.tamu.weaver.validation.model.BusinessValidationType.CREATE;
import static edu.tamu.weaver.validation.model.BusinessValidationType.DELETE;
import static edu.tamu.weaver.validation.model.BusinessValidationType.UPDATE;
import static edu.tamu.weaver.validation.model.MethodValidationType.REORDER;
import static edu.tamu.weaver.validation.model.MethodValidationType.SORT;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.tdl.vireo.model.AbstractFieldProfile;
import org.tdl.vireo.model.ControlledVocabulary;
import org.tdl.vireo.model.ControlledVocabularyCache;
import org.tdl.vireo.model.VocabularyWord;
import org.tdl.vireo.model.repo.ControlledVocabularyRepo;
import org.tdl.vireo.model.repo.VocabularyWordRepo;
import org.tdl.vireo.service.ControlledVocabularyCachingService;

import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.validation.aspect.annotation.WeaverValidatedModel;
import edu.tamu.weaver.validation.aspect.annotation.WeaverValidation;

/**
 * Controller in which to manage controlled vocabulary.
 *
 */
@RestController
@RequestMapping("/settings/controlled-vocabulary")
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
    @RequestMapping("/all")
    @PreAuthorize("hasRole('MANAGER')")
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
    @RequestMapping("/{name}")
    public ApiResponse getControlledVocabularyByName(@PathVariable String name) {
        return new ApiResponse(SUCCESS, controlledVocabularyRepo.findByName(name));
    }

    /**
     * Endpoint to create a new controlled vocabulary.
     *
     * @param data
     *            Json input data from request
     * @return ApiResponse with indicating success or error
     */
    @PreAuthorize("hasRole('MANAGER')")
    @RequestMapping(value = "/create", method = POST)
    @WeaverValidation(business = { @WeaverValidation.Business(value = CREATE) })
    public ApiResponse createControlledVocabulary(@WeaverValidatedModel ControlledVocabulary controlledVocabulary) {
        logger.info("Creating controlled vocabulary with name " + controlledVocabulary.getName());
        controlledVocabulary = controlledVocabularyRepo.create(controlledVocabulary.getName(), controlledVocabulary.getLanguage());
        simpMessagingTemplate.convertAndSend("/channel/settings/controlled-vocabulary", new ApiResponse(SUCCESS, controlledVocabularyRepo.findAllByOrderByPositionAsc()));
        simpMessagingTemplate.convertAndSend("/channel/settings/controlled-vocabulary/change", new ApiResponse(SUCCESS));
        return new ApiResponse(SUCCESS, controlledVocabulary);
    }

    /**
     * Endpoint to update controlled vocabulary.
     *
     * @param data
     *            Json input data with request
     * @return ApiResponse indicating success or error
     */
    @PreAuthorize("hasRole('MANAGER')")
    @RequestMapping(value = "/update", method = POST)
    @WeaverValidation(business = { @WeaverValidation.Business(value = UPDATE) })
    public ApiResponse updateControlledVocabulary(@WeaverValidatedModel ControlledVocabulary controlledVocabulary) {
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
    @PreAuthorize("hasRole('MANAGER')")
    @RequestMapping(value = "/remove", method = POST)
    @WeaverValidation(business = { @WeaverValidation.Business(value = DELETE, joins = { AbstractFieldProfile.class }, path = { "controlledVocabularies", "id" }), @WeaverValidation.Business(value = DELETE, path = { "isEntityProperty" }, restrict = "true") })
    public ApiResponse removeControlledVocabulary(@WeaverValidatedModel ControlledVocabulary controlledVocabulary) {
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
    @RequestMapping("/reorder/{src}/{dest}")
    @PreAuthorize("hasRole('MANAGER')")
    @WeaverValidation(method = { @WeaverValidation.Method(value = REORDER, model = ControlledVocabulary.class, params = { "0", "1" }) })
    public ApiResponse reorderControlledVocabulary(@PathVariable Long src, @PathVariable Long dest) {
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
    @RequestMapping("/sort/{column}")
    @PreAuthorize("hasRole('MANAGER')")
    @WeaverValidation(method = { @WeaverValidation.Method(value = SORT, model = ControlledVocabulary.class, params = { "0" }) })
    public ApiResponse sortControlledVocabulary(@PathVariable String column) {
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
    @RequestMapping("/export/{name}")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse exportControlledVocabulary(@PathVariable String name) {
        logger.info("Exporting controlled vocabulary for " + name);
        ControlledVocabulary cv = controlledVocabularyRepo.findByName(name);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("headers", Arrays.asList(new String[] { "name", "definition", "identifier", "contacts" }));
        List<List<Object>> rows = new ArrayList<List<Object>>();
        cv.getDictionary().forEach(vocabularyWord -> {
            List<Object> row = new ArrayList<Object>();
            VocabularyWord actualVocabularyWord = (VocabularyWord) vocabularyWord;
            row.add(actualVocabularyWord.getName());
            row.add(actualVocabularyWord.getDefinition());
            row.add(actualVocabularyWord.getIdentifier());
            row.add(String.join(",", actualVocabularyWord.getContacts()));
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
    @RequestMapping(value = "/status/{name}", method = RequestMethod.POST)
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse importControlledVocabularyStatus(@PathVariable String name) {
        return new ApiResponse(SUCCESS, controlledVocabularyCachingService.doesControlledVocabularyExist(name));
    }

    /**
     * Endpoint to cancel an inport of a given controlled vocabulary. Removes ControlledVocabularyCache from the ControlledVocabularyCacheService.
     *
     * @param name
     *            controlled vocabulary name
     * @return ApiResponse indicating success
     */
    @RequestMapping(value = "/cancel/{name}", method = RequestMethod.POST)
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse cancelImportControlledVocabulary(@PathVariable String name) {
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
    @RequestMapping(value = "/compare/{name}", method = RequestMethod.POST)
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse compareControlledVocabulary(@PathVariable String name, HttpServletRequest request) throws IOException {
        InputStream inputStream = request.getInputStream();
        logger.info("Comparing controlled vocabulary " + name);
        ControlledVocabulary controlledVocabulary = controlledVocabularyRepo.findByName(name);
        Map<String, Object> wordsMap = cacheImport(controlledVocabulary, inputStreamToString(inputStream));
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
    @RequestMapping(value = "/import/{name}")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse importControlledVocabulary(@PathVariable String name) {
        logger.info("Inporting controlled vocabulary " + name);
        ControlledVocabulary controlledVocabulary = controlledVocabularyRepo.findByName(name);
        ControlledVocabularyCache cvCache = controlledVocabularyCachingService.getControlledVocabularyCache(controlledVocabulary.getName());
        logger.info("Comparing controlled vocabulary " + name);
        for (VocabularyWord newVocabularyWord : cvCache.getNewVocabularyWords()) {
            newVocabularyWord = vocabularyWordRepo.create(controlledVocabulary, newVocabularyWord.getName(), newVocabularyWord.getDefinition(), newVocabularyWord.getIdentifier(), newVocabularyWord.getContacts());
            controlledVocabulary = controlledVocabularyRepo.findByName(name);
        }
        for (VocabularyWord[] updatingVocabularyWord : cvCache.getUpdatingVocabularyWords()) {
            VocabularyWord updatedVocabularyWord = vocabularyWordRepo.findByNameAndControlledVocabulary(updatingVocabularyWord[1].getName(), controlledVocabulary);
            updatedVocabularyWord.setDefinition(updatingVocabularyWord[1].getDefinition());
            updatedVocabularyWord.setIdentifier(updatingVocabularyWord[1].getIdentifier());
            updatedVocabularyWord.setContacts(updatingVocabularyWord[1].getContacts());
            updatedVocabularyWord = vocabularyWordRepo.save(updatedVocabularyWord);
        }
        ControlledVocabulary savedControlledVocabulary = controlledVocabularyRepo.save(controlledVocabulary);
        controlledVocabularyCachingService.removeControlledVocabularyCache(controlledVocabulary.getName());
        simpMessagingTemplate.convertAndSend("/channel/settings/controlled-vocabulary/change", new ApiResponse(SUCCESS));
        simpMessagingTemplate.convertAndSend("/channel/settings/controlled-vocabulary", new ApiResponse(SUCCESS, controlledVocabularyRepo.findAllByOrderByPositionAsc()));
        return new ApiResponse(SUCCESS, savedControlledVocabulary);
    }

    /**
     * Endpoint to add a blank vocabulart word to a controlled vocabulary.
     *
     * @param name
     *            controlled vocabulary name
     * @return ApiReponse indicating success
     */
    @Transactional
    @PreAuthorize("hasRole('MANAGER')")
    @RequestMapping(value = "/add-vocabulary-word/{cvId}", method = POST)
    public ApiResponse addVocabularyWord(@PathVariable Long cvId, @RequestBody VocabularyWord vocabularyWord) {
        ControlledVocabulary cv = controlledVocabularyRepo.findOne(cvId);

        vocabularyWord = vocabularyWordRepo.create(cv, vocabularyWord.getName(), vocabularyWord.getDefinition(), vocabularyWord.getIdentifier(), vocabularyWord.getContacts());

        cv = controlledVocabularyRepo.findOne(cv.getId());

        simpMessagingTemplate.convertAndSend("/channel/settings/controlled-vocabulary/" + cv.getId(), new ApiResponse(SUCCESS, cv));
        return new ApiResponse(SUCCESS, vocabularyWord);
    }

    /**
     * Endpoint to remove a vocabulary word from a controlled vocabulary.
     *
     * @param name
     *            controlled vocabulary name
     * @return ApiReponse indicating success
     */
    @Transactional
    @RequestMapping(value = "/remove-vocabulary-word/{cvId}/{vwId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse removeVocabularyWord(@PathVariable Long cvId, @PathVariable Long vwId) {

        ControlledVocabulary cv = controlledVocabularyRepo.findOne(cvId);
        VocabularyWord vw = vocabularyWordRepo.findOne(vwId);

        cv.removeValue(vw);
        cv = controlledVocabularyRepo.save(cv);

        simpMessagingTemplate.convertAndSend("/channel/settings/controlled-vocabulary/" + cv.getId(), new ApiResponse(SUCCESS, cv));
        return new ApiResponse(SUCCESS, cv);
    }

    /**
     * Endpoint to update a vocabulary word on a controlled vocabulary.
     *
     * @param name
     *            controlled vocabulary name
     * @return ApiReponse indicating success
     */
    @Transactional
    @RequestMapping(value = "/update-vocabulary-word/{cvId}", method = RequestMethod.POST)
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse updateVocabularyWord(@PathVariable Long cvId, @RequestBody VocabularyWord vw) {
        vw = vocabularyWordRepo.save(vw);
        ControlledVocabulary cv = controlledVocabularyRepo.findOne(cvId);
        simpMessagingTemplate.convertAndSend("/channel/settings/controlled-vocabulary/" + cv.getId(), new ApiResponse(SUCCESS, cv));
        return new ApiResponse(SUCCESS, vw);
    }

    private Map<String, Object> cacheImport(ControlledVocabulary controlledVocabulary, String csvString) throws IOException {

        Iterable<CSVRecord> records = CSVFormat.RFC4180.withHeader("name", "definition", "identifier", "contacts").parse(new InputStreamReader(new ByteArrayInputStream(csvString.getBytes(StandardCharsets.UTF_8))));

        List<VocabularyWord> newWords = new ArrayList<VocabularyWord>();
        List<VocabularyWord> repeatedWords = new ArrayList<VocabularyWord>();
        List<VocabularyWord[]> updatingWords = new ArrayList<VocabularyWord[]>();

        List<VocabularyWord> words = controlledVocabulary.getDictionary();

        Boolean headerPass = true;

        for (CSVRecord record : records) {

            if (headerPass) {
                headerPass = false;
                continue;
            }

            VocabularyWord currentVocabularyWord = new VocabularyWord(record.get("name"), record.get("definition"), record.get("identifier"), new ArrayList<String>(Arrays.asList(record.get("contacts").split(","))));

            boolean isRepeat = false;
            for (VocabularyWord newWord : newWords) {
                if (newWord.getName().equals(record.get("name"))) {
                    repeatedWords.add(currentVocabularyWord);
                    isRepeat = true;
                    break;
                }
            }

            if (!isRepeat) {
                for (VocabularyWord[] updatingWord : updatingWords) {
                    if (updatingWord[0].getName().equals(record.get("name"))) {
                        repeatedWords.add(currentVocabularyWord);
                        isRepeat = true;
                        break;
                    }
                }
            }

            if (!isRepeat) {
                boolean isNew = true;
                for (VocabularyWord word : words) {
                    if (record.get("name").equals(word.getName())) {

                        String definition = word.getDefinition();
                        String identifier = word.getIdentifier();
                        List<String> contacts = word.getContacts();

                        boolean change = false;

                        if (definition != null && !record.get("definition").equals(definition)) {
                            change = true;
                        }

                        if (identifier != null && !record.get("identifier").equals(identifier)) {
                            change = true;
                        }

                        if (contacts != null && !record.get("contacts").equals(String.join(",", word.getContacts()))) {
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
     * Converts input stream to a string which represents the csv
     *
     * @param ServletInputStream
     *            csv bitstream
     * @return string array of the csv rows
     * @throws IOException
     */
    private String inputStreamToString(java.io.InputStream bufferedIn) throws IOException {
        String[] imageData = IOUtils.toString(bufferedIn, StandardCharsets.UTF_8.displayName()).split(";");
        String[] encodedData = imageData[1].split(",");
        return new String(Base64.getDecoder().decode(encodedData[1]));
    }

}
