package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.ERROR;
import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;

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
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.tdl.vireo.model.ControlledVocabulary;
import org.tdl.vireo.model.ControlledVocabularyCache;
import org.tdl.vireo.model.Language;
import org.tdl.vireo.model.VocabularyWord;
import org.tdl.vireo.model.repo.ControlledVocabularyRepo;
import org.tdl.vireo.model.repo.LanguageRepo;
import org.tdl.vireo.model.repo.VocabularyWordRepo;
import org.tdl.vireo.service.ControlledVocabularyCachingService;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.ApiVariable;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.aspect.annotation.Data;
import edu.tamu.framework.aspect.annotation.InputStream;
import edu.tamu.framework.model.ApiResponse;

@RestController
@ApiMapping("/settings/controlled-vocabulary")
public class ControlledVocabularyController {
    
    private final Logger logger = Logger.getLogger(this.getClass());
    
    @Autowired
    private ControlledVocabularyCachingService controlledVocabularyCachingService;
    
    @Autowired
    private ControlledVocabularyRepo controlledVocabularyRepo;
    
    @Autowired
    private VocabularyWordRepo vocabularyWordRepo;
    
    @Autowired
    private LanguageRepo languageRepo;
    
    @Autowired 
    private SimpMessagingTemplate simpMessagingTemplate;
    
    @Autowired
    private ObjectMapper objectMapper;
        
    private Map<String, List<ControlledVocabulary>> getAll() {
        Map<String, List<ControlledVocabulary>> map = new HashMap<String, List<ControlledVocabulary>>();
        map.put("list", controlledVocabularyRepo.findAllByOrderByOrderAsc());
        return map;
    }
    
    @ApiMapping("/all")
    @Auth(role = "ROLE_MANAGER")
    @Transactional
    public ApiResponse getAllControlledVocabulary() {
        return new ApiResponse(SUCCESS, getAll());
    }
    
    @ApiMapping("/{name}")
    @Auth(role = "ROLE_MANAGER")
    @Transactional
    public ApiResponse getControlledVocabularyByName(@ApiVariable String name) {
        return new ApiResponse(SUCCESS, controlledVocabularyRepo.findByName(name));
    }
    
    @ApiMapping("/create")
    @Auth(role = "ROLE_MANAGER")
    @Transactional
    public ApiResponse createControlledVocabulary(@Data String data) {
        
        JsonNode dataNode;
        try {
            dataNode = objectMapper.readTree(data);
        } catch (IOException e) {
            return new ApiResponse(ERROR, "Unable to parse update json ["+e.getMessage()+"]");
        }
        
        //TODO: proper validation and response
        
        ControlledVocabulary newControlledVocabulary = null;
        
        String name = null;
        Language language = null;
        
        JsonNode languageNode = dataNode.get("language");
        if(languageNode != null) {
            JsonNode languageNameNode = languageNode.get("name");
            if(languageNameNode != null) {
                language = languageRepo.findByName(languageNameNode.asText());
            }
            else {
                return new ApiResponse(ERROR, "Language name required to create controlled vocabulary!");
            }
        }
        else {
            return new ApiResponse(ERROR, "Language required to create controlled vocabulary!");
        }
        JsonNode nameNode = dataNode.get("name");
        if(nameNode != null) {
            name = nameNode.asText(); 
        }
        else {
            return new ApiResponse(ERROR, "Name required to create controlled vocabulary!");
        }
        
        if(name != null && language != null) {
            newControlledVocabulary = controlledVocabularyRepo.create(name, language);  
        }
        else {
            return new ApiResponse(ERROR, "Name and language could not be determined from input!");
        }
        
        newControlledVocabulary.setOrder((int) controlledVocabularyRepo.count());
        
        try {
            newControlledVocabulary = controlledVocabularyRepo.save(newControlledVocabulary);
        }
        catch(DataIntegrityViolationException dive) {
            return new ApiResponse(ERROR, name + " is already a controlled vocabulary!");
        }
        
        //TODO: logging
        
        logger.info("Created controlled vocabulary " + newControlledVocabulary.getName());
        
        simpMessagingTemplate.convertAndSend("/channel/settings/controlled-vocabulary", new ApiResponse(SUCCESS, getAll()));
        simpMessagingTemplate.convertAndSend("/channel/settings/controlled-vocabulary/change", new ApiResponse(SUCCESS));
        return new ApiResponse(SUCCESS);
    }
    
    @ApiMapping("/update")
    @Auth(role = "ROLE_MANAGER")
    public ApiResponse updateControlledVocabulary(@Data String data) {
        
        JsonNode dataNode;
        try {
            dataNode = objectMapper.readTree(data);
        } catch (IOException e) {
            return new ApiResponse(ERROR, "Unable to parse update json ["+e.getMessage()+"]");
        }
        
        //TODO: proper validation and response
        
        ControlledVocabulary controlledVocabulary = null;
        
        JsonNode id = dataNode.get("id");
        if(id != null) {
            Long idLong = -1L;
            try {
                idLong = id.asLong();
            }
            catch(NumberFormatException nfe) {
                return new ApiResponse(ERROR, "Id required to update graduation month!");
            }
            controlledVocabulary = controlledVocabularyRepo.findOne(idLong);      
        }
        else {
            return new ApiResponse(ERROR, "Id required to update controlled vocabulary!");
        }
        
        JsonNode nameNode = dataNode.get("name");
        if(nameNode != null) {
            controlledVocabulary.setName(nameNode.asText());  
        }
        else {
            return new ApiResponse(ERROR, "Name required to update controlled vocabulary!");
        }
        
        try {
            controlledVocabulary = controlledVocabularyRepo.save(controlledVocabulary);
        }
        catch(DataIntegrityViolationException dive) {
            return new ApiResponse(ERROR, nameNode.asText() + " is already a controlled vocabulary!");
        }
        
        //TODO: logging
        
        logger.info("Updated controlled vocabulary with name " + controlledVocabulary.getName());
        
        simpMessagingTemplate.convertAndSend("/channel/settings/graduation-month", new ApiResponse(SUCCESS, getAll()));
        
        return new ApiResponse(SUCCESS);
    }
    
    @ApiMapping("/remove/{indexString}")
    @Auth(role = "ROLE_MANAGER")
    @Transactional
    public ApiResponse removeControlledVocabulary(@ApiVariable String indexString) {        
        Integer index = -1;
        
        try {
            index = Integer.parseInt(indexString);
        }
        catch(NumberFormatException nfe) {
            return new ApiResponse(ERROR, "Id is not a valid controlled vocabulary order!");
        }
        
        if(index >= 0) {               
            controlledVocabularyRepo.remove(index);
        }
        else {
            return new ApiResponse(ERROR, "Id is not a valid controlled vocabulary order!");
        }
        
        logger.info("Deleted controlled vocabulary with order " + index);
        
        simpMessagingTemplate.convertAndSend("/channel/settings/controlled-vocabulary", new ApiResponse(SUCCESS, getAll()));
        
        return new ApiResponse(SUCCESS);
    }
    
    @ApiMapping("/reorder/{src}/{dest}")
    @Auth(role = "ROLE_MANAGER")
    @Transactional
    public ApiResponse reorderGraduationMonths(@ApiVariable String src, @ApiVariable String dest) {
        Integer intSrc = Integer.parseInt(src);
        Integer intDest = Integer.parseInt(dest);
        controlledVocabularyRepo.reorder(intSrc, intDest);
        simpMessagingTemplate.convertAndSend("/channel/settings/controlled-vocabulary", new ApiResponse(SUCCESS, getAll()));
        return new ApiResponse(SUCCESS);
    }
    
    @ApiMapping("/sort/{column}")
    @Auth(role = "ROLE_MANAGER")
    @Transactional
    public ApiResponse sortGraduationMonths(@ApiVariable String column) {
        controlledVocabularyRepo.sort(column);
        simpMessagingTemplate.convertAndSend("/channel/settings/controlled-vocabulary", new ApiResponse(SUCCESS, getAll()));
        return new ApiResponse(SUCCESS);
    }
    
    @ApiMapping("/export/{name}")
    @Auth(role = "ROLE_MANAGER")
    @Transactional
    public ApiResponse exportControlledVocabulary(@ApiVariable String name) {
        Map<String, Object> map = new HashMap<String, Object>();        
        ControlledVocabulary cv = controlledVocabularyRepo.findByName(name);        
        map.put("headers", Arrays.asList(new String[]{"name", "definition", "identifier"}));
        
        List<List<Object>> rows = new ArrayList<List<Object>>();
        
        cv.getDictionary().forEach(vocabularyWord -> {
            
            List<Object> row = new ArrayList<Object>();
            if(vocabularyWord.getClass().equals(VocabularyWord.class)) {
                VocabularyWord actualVocabularyWord = (VocabularyWord) vocabularyWord;
                row.add(actualVocabularyWord.getName());
                row.add(actualVocabularyWord.getDefinition());
                row.add(actualVocabularyWord.getIdentifier());
            }
            else {
                row.add(vocabularyWord);
            }
            
            rows.add(row);
        });
        
        map.put("rows", rows);
        
        return new ApiResponse(SUCCESS, map);
    }
    
    @ApiMapping(value = "/status/{name}", method = RequestMethod.POST)
    @Auth(role = "ROLE_MANAGER")
    public ApiResponse importControlledVocabularyStatus(@ApiVariable String name) {
        return new ApiResponse(SUCCESS, controlledVocabularyCachingService.isControlledVocabularyBeingImported(name));
    }
    
    @ApiMapping(value = "/cancel/{name}", method = RequestMethod.POST)
    @Auth(role = "ROLE_MANAGER")
    public ApiResponse cancelImportControlledVocabulary(@ApiVariable String name) {
        controlledVocabularyCachingService.removeControlledVocabularyCache(name);
        simpMessagingTemplate.convertAndSend("/channel/settings/controlled-vocabulary/change", new ApiResponse(SUCCESS));
        return new ApiResponse(SUCCESS);
    }
    
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
        
        for(String row : rows) {
            String[] cols = new String[3];            
            String[] temp = row.split(",");            
            for(int i = 0; i < temp.length; i++) {
                cols[i] = temp[i] != null ? temp[i] : "";
            }
            
            VocabularyWord currentVocabularyWord = new VocabularyWord(cols[0], cols[1], cols[2]);
            
            boolean isRepeat = false;
            for(VocabularyWord newWord : newWords) {
                if(newWord.getName().equals(cols[0])) {
                    repeatedWords.add(currentVocabularyWord);
                    isRepeat = true;
                    break;
                }
            }
            
            if(!isRepeat) {
                for(VocabularyWord[] updatingWord : updatingWords) {
                    if(updatingWord[0].getName().equals(cols[0])) {
                        repeatedWords.add(currentVocabularyWord);
                        isRepeat = true;
                        break;
                    }
                }
            }
            
            if(!isRepeat) {
                boolean isNew = true;
                for(Object word : words) {
                    VocabularyWord vocabularyWord = (VocabularyWord) word;
                    if(cols[0].equals(vocabularyWord.getName())) {
                        
                        if(!cols[1].equals(vocabularyWord.getDefinition()) || !cols[2].equals(vocabularyWord.getIdentifier())) {
                            updatingWords.add(new VocabularyWord[]{ vocabularyWord, currentVocabularyWord });
                        }
                        
                        isNew = false;
                        break;
                    }
                }
                if(isNew) {
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
    
    @ApiMapping(value = "/import/{name}", method = RequestMethod.POST)
    @Auth(role = "ROLE_MANAGER")
    @Transactional
    public ApiResponse importControlledVocabulary(@ApiVariable String name) {        
        
        ControlledVocabulary controlledVocabulary = controlledVocabularyRepo.findByName(name);
        
        ControlledVocabularyCache cvCache = controlledVocabularyCachingService.getControlledVocabularyCache(controlledVocabulary.getName());
        
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
        return new ApiResponse(SUCCESS);
    }
    
    private String[] inputStreamToRows(Object inputStream) throws IOException {
        String csvString = null;
        String[] imageData = IOUtils.toString((ServletInputStream) inputStream, "UTF-8").split(";");
        String[] encodedData = imageData[1].split(",");            
        csvString = new String(Base64.getDecoder().decode(encodedData[1]));       
        String[] rows = csvString.split("\\R");
        return Arrays.copyOfRange(rows, 1, rows.length);
    }

}
