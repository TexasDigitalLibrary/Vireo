package org.tdl.vireo.controller;

import static edu.tamu.weaver.response.ApiStatus.SUCCESS;
import static edu.tamu.weaver.validation.model.BusinessValidationType.CREATE;
import static edu.tamu.weaver.validation.model.BusinessValidationType.DELETE;
import static edu.tamu.weaver.validation.model.BusinessValidationType.UPDATE;
import static edu.tamu.weaver.validation.model.MethodValidationType.REORDER;
import static edu.tamu.weaver.validation.model.MethodValidationType.SORT;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.validation.aspect.annotation.WeaverValidatedModel;
import edu.tamu.weaver.validation.aspect.annotation.WeaverValidation;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.tdl.vireo.model.AbstractFieldProfile;
import org.tdl.vireo.model.ControlledVocabulary;
import org.tdl.vireo.model.ControlledVocabularyCache;
import org.tdl.vireo.model.VocabularyWord;
import org.tdl.vireo.model.repo.ControlledVocabularyRepo;
import org.tdl.vireo.model.repo.VocabularyWordRepo;
import org.tdl.vireo.service.ControlledVocabularyCachingService;
import org.tdl.vireo.view.ContactsVocabularyWordView;

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

    /**
     * Endpoint to request all controlled vocabulary.
     *
     * @return ApiResponse with all controlled vocabulary
     */
    @RequestMapping("/all")
    @PreAuthorize("hasRole('REVIEWER')")
    public ApiResponse getAllControlledVocabulary() {
        return new ApiResponse(SUCCESS, controlledVocabularyRepo.findAllByOrderByPositionAsc());
    }

    /**
     * Endpoint to request controlled vocabulary by name.
     *
     * @param name Name of controlled vocabulary
     *
     * @return ApiResponse with requested controlled vocabulary
     */
    @RequestMapping("/{name}")
    public ApiResponse getControlledVocabularyByName(@PathVariable String name) {
        return new ApiResponse(SUCCESS, controlledVocabularyRepo.findByName(name));
    }

    /**
     * Endpoint to create a new controlled vocabulary.
     *
     * @param controlledVocabulary The controlled controlledVocabulary.
     *
     * @return ApiResponse with indicating success or error
     */
    @PreAuthorize("hasRole('MANAGER')")
    @RequestMapping(value = "/create", method = POST)
    @WeaverValidation(business = { @WeaverValidation.Business(value = CREATE) })
    public ApiResponse createControlledVocabulary(@WeaverValidatedModel ControlledVocabulary controlledVocabulary) {
        logger.info("Creating controlled vocabulary with name " + controlledVocabulary.getName());
        controlledVocabulary = controlledVocabularyRepo.create(controlledVocabulary.getName());
        return new ApiResponse(SUCCESS, controlledVocabulary);
    }

    /**
     * Endpoint to update controlled vocabulary.
     *
     * @param controlledVocabulary The controlled controlledVocabulary.
     *
     * @return ApiResponse indicating success or error
     */
    @PreAuthorize("hasRole('MANAGER')")
    @RequestMapping(value = "/update", method = POST)
    @WeaverValidation(business = { @WeaverValidation.Business(value = UPDATE) })
    public ApiResponse updateControlledVocabulary(@WeaverValidatedModel ControlledVocabulary controlledVocabulary) {
        String name = controlledVocabulary.getName();
        Long id = controlledVocabulary.getId();
        controlledVocabulary = controlledVocabularyRepo.findById(id).get();
        logger.info("Updating controlled vocabulary with name " + controlledVocabulary.getName());
        controlledVocabulary.setName(name);
        controlledVocabulary = controlledVocabularyRepo.update(controlledVocabulary);
        return new ApiResponse(SUCCESS, controlledVocabulary);
    }

    /**
     * Endpoint to remove controlled vocabulary by provided index
     *
     * @param controlledVocabulary The controlled controlledVocabulary.
     *
     * @return ApiResponse indicating success or error
     */
    @PreAuthorize("hasRole('MANAGER')")
    @RequestMapping(value = "/remove", method = POST)
    @WeaverValidation(business = { @WeaverValidation.Business(value = DELETE, joins = { AbstractFieldProfile.class }, path = { "controlledVocabulary", "id" }), @WeaverValidation.Business(value = DELETE, path = { "isEntityProperty" }, restrict = "true") })
    public ApiResponse removeControlledVocabulary(@WeaverValidatedModel ControlledVocabulary controlledVocabulary) {
        logger.info("Removing Controlled Vocabulary with name " + controlledVocabulary.getName());
        controlledVocabularyRepo.remove(controlledVocabulary);
        return new ApiResponse(SUCCESS);
    }

    /**
     * Endpoint to reorder controlled vocabulary.
     *
     * @param src source position
     *
     * @param dest destination position
     *
     * @return ApiResponse indicating success
     */
    @RequestMapping("/reorder/{src}/{dest}")
    @PreAuthorize("hasRole('MANAGER')")
    @WeaverValidation(method = { @WeaverValidation.Method(value = REORDER, model = ControlledVocabulary.class, params = { "0", "1" }) })
    public ApiResponse reorderControlledVocabulary(@PathVariable Long src, @PathVariable Long dest) {
        logger.info("Reordering controlled vocabularies");
        controlledVocabularyRepo.reorder(src, dest);
        return new ApiResponse(SUCCESS);
    }

    /**
     * Endpoint to sort controlled vocabulary.
     *
     * @param column column to sort by
     *
     * @return ApiResponse indicating success
     */
    @RequestMapping("/sort/{column}")
    @PreAuthorize("hasRole('MANAGER')")
    @WeaverValidation(method = { @WeaverValidation.Method(value = SORT, model = ControlledVocabulary.class, params = { "0" }) })
    public ApiResponse sortControlledVocabulary(@PathVariable String column) {
        logger.info("Sorting controlled vocabularies by " + column);
        controlledVocabularyRepo.sort(column);
        return new ApiResponse(SUCCESS);
    }

    /**
     * Endpoint to export controlled vocabulary to populate csv
     *
     * @param name name of controlled vocabulary to export
     *
     * @return ApiResponse with map containing csv content
     */
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
            VocabularyWord actualVocabularyWord = vocabularyWord;
            row.add(actualVocabularyWord.getName());
            row.add(actualVocabularyWord.getDefinition());
            row.add(actualVocabularyWord.getIdentifier());
            row.add(String.join(",", actualVocabularyWord.getContacts()));
            rows.add(row);
        });
        map.put("rows", rows);
        return new ApiResponse(SUCCESS, map);
    }

    /**
     * Endpoint to get the import status of a given controlled vocabulary.
     *
     * @param name controlled vocabulary name
     *
     * @return ApiResponse with a boolean whether import in progress or not
     */
    @RequestMapping("/status/{name}")
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
    @RequestMapping("/cancel/{name}")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse cancelImportControlledVocabulary(@PathVariable String name) {
        logger.info("Cancelling import for cached controlled vocabulary " + name);
        controlledVocabularyCachingService.removeControlledVocabularyCache(name);
        return new ApiResponse(SUCCESS);
    }

    /**
     * Endpoint to compare a csv of controlled vocabulary to an existing controlled vocabulary.
     *
     * @param name controlled vocabulary to compare with
     * @param file The file request parameter.
     *
     * @return ApiResponse with map of new words, updating words, and duplicate words
     *
     * @throws IOException
     */
    // TODO: implement controller advice to catch exception and handle gracefully
    @PreAuthorize("hasRole('MANAGER')")
    @RequestMapping(value = "/compare/{name}", method = RequestMethod.POST)
    public ApiResponse compareControlledVocabulary(@PathVariable String name, @RequestParam MultipartFile file) throws IOException {
        logger.info("Comparing controlled vocabulary " + name);
        ControlledVocabulary controlledVocabulary = controlledVocabularyRepo.findByName(name);
        Map<String, Object> wordsMap = cacheImport(controlledVocabulary, file);
        return new ApiResponse(SUCCESS, wordsMap);
    }

    /**
     * Endpoint to import controlled vocabulary after confirmation.
     *
     * @param name controlled vocabulary name
     *
     * @return ApiReponse indicating success
     */
    @RequestMapping(value = "/import/{name}")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse importControlledVocabulary(@PathVariable String name) {
        logger.info("Importing controlled vocabulary " + name);
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
        for (VocabularyWord removedVocabularyWord : cvCache.getRemovedVocabularyWords()) {
            removeVocabularyWord(controlledVocabulary.getId(), removedVocabularyWord.getId());
            controlledVocabulary = controlledVocabularyRepo.findByName(name);
        }
        ControlledVocabulary savedControlledVocabulary = controlledVocabularyRepo.update(controlledVocabulary);
        controlledVocabularyCachingService.removeControlledVocabularyCache(controlledVocabulary.getName());
        return new ApiResponse(SUCCESS, savedControlledVocabulary);
    }

    /**
     * Endpoint to add a blank vocabulary word to a controlled vocabulary.
     *
     * @param cvId The path parameter representing the ControlledVocabulary ID.
     * @param vocabularyWord controlled vocabulary word.
     *
     * @return ApiReponse indicating success
     */
    @PreAuthorize("hasRole('MANAGER')")
    @RequestMapping(value = "/add-vocabulary-word/{cvId}", method = POST)
    public ApiResponse addVocabularyWord(@PathVariable Long cvId, @RequestBody VocabularyWord vocabularyWord) {
        ControlledVocabulary cv = controlledVocabularyRepo.findById(cvId).get();
        vocabularyWord = vocabularyWordRepo.create(cv, vocabularyWord.getName(), vocabularyWord.getDefinition(), vocabularyWord.getIdentifier(), vocabularyWord.getContacts());
        controlledVocabularyRepo.broadcast(cv.getId());
        return new ApiResponse(SUCCESS, vocabularyWord);
    }

    /**
     * Endpoint to remove a vocabulary word from a controlled vocabulary.
     *
     * @param cvId The path parameter representing the ControlledVocabulary ID.
     * @param vwId The path parameter representing the VocabularyWord ID.
     *
     * @return ApiReponse indicating success
     */
    @PreAuthorize("hasRole('MANAGER')")
    @RequestMapping(value = "/remove-vocabulary-word/{cvId}/{vwId}")
    public ApiResponse removeVocabularyWord(@PathVariable Long cvId, @PathVariable Long vwId) {
        ControlledVocabulary cv = controlledVocabularyRepo.findById(cvId).get();
        VocabularyWord vw = vocabularyWordRepo.findById(vwId).get();
        cv.removeValue(vw);
        cv = controlledVocabularyRepo.update(cv);
        return new ApiResponse(SUCCESS, cv);
    }

    /**
     * Endpoint to update a vocabulary word on a controlled vocabulary.
     *
     * @param cvId The path parameter representing the ControlledVocabulary ID.
     * @param vw The VocabularyWord.
     *
     * @return ApiReponse indicating success
     */
    @PreAuthorize("hasRole('MANAGER')")
    @RequestMapping(value = "/update-vocabulary-word/{cvId}", method = RequestMethod.POST)
    public ApiResponse updateVocabularyWord(@PathVariable Long cvId, @RequestBody VocabularyWord vw) {
        ControlledVocabulary cv = controlledVocabularyRepo.findById(cvId).get();
        vw.setControlledVocabulary(cv);
        vw = vocabularyWordRepo.update(vw);
        controlledVocabularyRepo.broadcast(cv.getId());
        return new ApiResponse(SUCCESS, vw);
    }

    /**
     * Search the given Controlled Vocabulary for a dictionary name (vocabulary word name).
     *
     * This is intended to be used for type aheads.
     *
     * @param cvId The path parameter representing the ControlledVocabulary ID.
     * @param search The search string to use in the type ahead.
     *
     * @return ApiReponse
     *   SUCCESS is always returned.
     *   An array of Vocabulary Words representing the matches is returned.
     *   If the ControlledVocabulary is not found, then an empty array is returned.
     */
    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping(value = "/typeahead-vocabulary-word/{cvId}")
    public ApiResponse typeaheadVocabularyWord(@PathVariable Long cvId, @RequestBody String search) {
        return new ApiResponse(SUCCESS, vocabularyWordRepo.findAllByNameContainsIgnoreCaseAndControlledVocabularyIdOrderByName(search, cvId, ContactsVocabularyWordView.class));
    }

    private Map<String, Object> cacheImport(ControlledVocabulary controlledVocabulary, MultipartFile file) throws IOException {

        CSVFormat csv = CSVFormat.RFC4180.builder().setHeader("name", "definition", "identifier", "contacts").build();
        Iterable<CSVRecord> records = csv.parse(new InputStreamReader(file.getInputStream()));

        List<VocabularyWord> newWords = new ArrayList<VocabularyWord>();
        List<VocabularyWord> repeatedWords = new ArrayList<VocabularyWord>();
        List<VocabularyWord[]> updatingWords = new ArrayList<VocabularyWord[]>();
        List<VocabularyWord> removedWords = new ArrayList<VocabularyWord>();

        List<VocabularyWord> words = controlledVocabulary.getDictionary();

        List<String> importedNames = new ArrayList<String>();

        Boolean headerPass = true;

        for (CSVRecord record : records) {

            if (headerPass) {
                headerPass = false;
                continue;
            }

            VocabularyWord currentVocabularyWord = new VocabularyWord(record.get("name"), record.get("definition"), record.get("identifier"), new ArrayList<String>(Arrays.asList(record.get("contacts").split(","))));

            importedNames.add(currentVocabularyWord.getName());

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

        for (VocabularyWord existingVocabularyWord : words) {
            if (!importedNames.contains(existingVocabularyWord.getName())) {
                removedWords.add(existingVocabularyWord);
            }
        }

        Map<String, Object> wordsMap = new HashMap<String, Object>();
        wordsMap.put("new", newWords);
        wordsMap.put("repeats", repeatedWords);
        wordsMap.put("updating", updatingWords);
        wordsMap.put("removed", removedWords);

        ControlledVocabularyCache cvCache = new ControlledVocabularyCache(new Date().getTime(), controlledVocabulary.getName());
        cvCache.setNewVocabularyWords(newWords);
        cvCache.setDuplicateVocabularyWords(repeatedWords);
        cvCache.setUpdatingVocabularyWords(updatingWords);
        cvCache.setRemovedVocabularyWords(removedWords);
        controlledVocabularyCachingService.addControlledVocabularyCache(cvCache);

        return wordsMap;
    }

}
