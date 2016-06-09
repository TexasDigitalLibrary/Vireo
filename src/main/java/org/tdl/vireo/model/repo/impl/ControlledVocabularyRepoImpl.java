package org.tdl.vireo.model.repo.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Base64;

import javax.servlet.ServletInputStream;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.ObjectError;
import org.tdl.vireo.model.ControlledVocabulary;
import org.tdl.vireo.model.Language;
import org.tdl.vireo.model.repo.ControlledVocabularyRepo;
import org.tdl.vireo.model.repo.custom.ControlledVocabularyRepoCustom;
import org.tdl.vireo.service.OrderedEntityService;
import org.tdl.vireo.service.ValidationService;

import edu.tamu.framework.validation.ModelBindingResult;

public class ControlledVocabularyRepoImpl implements ControlledVocabularyRepoCustom {

    @Autowired
    private OrderedEntityService orderedEntityService;
    
    @Autowired
    private ControlledVocabularyRepo controlledVocabularyRepo;
    
    @Autowired
    private ValidationService validationService;

    @Override
    public ControlledVocabulary create(String name, Language language) {
        ControlledVocabulary controlledVocabulary = new ControlledVocabulary(name, language);
        controlledVocabulary.setPosition(controlledVocabularyRepo.count() + 1);
        return controlledVocabularyRepo.save(controlledVocabulary);
    }
    
    @Override
    public ControlledVocabulary create(String name, String entityName, Language language) {
        ControlledVocabulary controlledVocabulary = new ControlledVocabulary(name, entityName, language);
        controlledVocabulary.setPosition(controlledVocabularyRepo.count() + 1);
        return controlledVocabularyRepo.save(controlledVocabulary);
    }
    
    @Override
    public void reorder(Long src, Long dest) {
        orderedEntityService.reorder(ControlledVocabulary.class, src, dest);
    }
    
    @Override
    public void sort(String column) {
        orderedEntityService.sort(ControlledVocabulary.class, column);
    }
    
    @Override
    public void remove(ControlledVocabulary controlledVocabulary) {
        orderedEntityService.remove(controlledVocabularyRepo, ControlledVocabulary.class, controlledVocabulary.getPosition());
    }

    @Override
    public ControlledVocabulary validateCreate(ControlledVocabulary controlledVocabulary) {
        if(controlledVocabularyRepo.findByName(controlledVocabulary.getName()) != null) {
            controlledVocabulary.getBindingResult().addError(new ObjectError("controlledVocabulary", controlledVocabulary.getName() + " is already a controlled vocabulary!"));
        }
        return controlledVocabulary;
    }
    
    @Override
    public ControlledVocabulary validateUpdate(ControlledVocabulary controlledVocabulary) {
        ControlledVocabulary controlledVocabularyToUpdate = null;
        // make sure we're receiving an Id from the front-end
        if (controlledVocabulary.getId() == null) {
            controlledVocabulary.getBindingResult().addError(new ObjectError("controlledVocabulary", "Cannot update a ControlledVocabulary without an id!"));
        }
        // we have an id
        else {
            controlledVocabularyToUpdate = controlledVocabularyRepo.findOne(controlledVocabulary.getId());
            ControlledVocabulary controlledVocabularyExistingName = controlledVocabularyRepo.findByName(controlledVocabulary.getName());

            // make sure we won't have any unique constraint violations
            if(controlledVocabularyExistingName != null) {
                controlledVocabulary.getBindingResult().addError(new ObjectError("controlledVocabulary", controlledVocabulary.getName() + " is already a controlled vocabulary!"));
            }
            
            // make sure we're updating an existing controlled vocabulary
            if(controlledVocabularyToUpdate == null) {
                controlledVocabulary.getBindingResult().addError(new ObjectError("controlledVocabulary", controlledVocabulary.getName() + " can't be updated, it doesn't exist!"));
            }
        }
        // if we have no errors, do the update!
        if(!controlledVocabulary.getBindingResult().hasErrors()){
            controlledVocabularyToUpdate.setName(controlledVocabulary.getName());
            controlledVocabularyToUpdate.setBindingResult(controlledVocabulary.getBindingResult());
            controlledVocabulary = controlledVocabularyToUpdate;
        }
        
        return controlledVocabulary;
    }
    
    @Override
    public ControlledVocabulary validateRemove(String idString, ModelBindingResult modelBindingResult) {
        ControlledVocabulary toRemove = null;
        Long id = validationService.validateLong(idString, "controlledVocabulary", modelBindingResult);
        
        if(!modelBindingResult.hasErrors()){
            toRemove = controlledVocabularyRepo.findOne(id);
            if (toRemove == null) {
                modelBindingResult.addError(new ObjectError("controlledVocabulary", "Cannot remove Controlled Vocabulary, id did not exist!"));
            } else if (toRemove.isEnum() || toRemove.isEntityProperty()) {
                modelBindingResult.addError(new ObjectError("controlledVocabulary", "Cannot remove Controlled Vocabulary, it's an internal system one!"));
            }
        }
        return toRemove;
    }
    
    @Override
    public ControlledVocabulary validateExport(String name, ModelBindingResult modelBindingResult) {
        ControlledVocabulary toExport = null;
        
        if(!modelBindingResult.hasErrors()){
            toExport = controlledVocabularyRepo.findByName(name);
            if (toExport == null) {
                modelBindingResult.addError(new ObjectError("controlledVocabulary", "Cannot export Controlled Vocabulary, name did not exist!"));
            }
        }
        return toExport;
    }
    
    @Override
    public ControlledVocabulary validateCompareCV(String name, ModelBindingResult modelBindingResult) {
        ControlledVocabulary toCompare = null;
        
        if(!modelBindingResult.hasErrors()){
            toCompare = controlledVocabularyRepo.findByName(name);
            if (toCompare == null) {
                modelBindingResult.addError(new ObjectError("controlledVocabulary", "Cannot compare Controlled Vocabulary, name did not exist!"));
            }
        }
        return toCompare;
    }
    
    @Override
    public String[] validateCompareRows(ServletInputStream inputStream, ModelBindingResult modelBindingResult) {
        String[] rows = new String[0];
        
        try {
            rows = inputStreamToRows(inputStream);
        } catch (IOException e1) {
            modelBindingResult.addError(new ObjectError("controlledVocabulary", "Invalid file uploaded!"));
        }
        
        return rows;
    }
    
    @Override
    public ControlledVocabulary validateImport(String name, ModelBindingResult modelBindingResult) {
        ControlledVocabulary toImport = null;
        
        if(!modelBindingResult.hasErrors()){
            toImport = controlledVocabularyRepo.findByName(name);
            if (toImport == null) {
                modelBindingResult.addError(new ObjectError("controlledVocabulary", "Cannot import Controlled Vocabulary, name did not exist!"));
            }
        }
        return toImport;
    }
    
    /**
     * Converts input stream to array of strings which represent the rows
     * 
     * @param ServletInputStream
     *            csv bitstream
     * @return string array of the csv rows
     * @throws IOException
     */
    private String[] inputStreamToRows(InputStream bufferedIn) throws IOException {
        String csvString = null;
        String[] imageData = IOUtils.toString(bufferedIn, "UTF-8").split(";");
        String[] encodedData = imageData[1].split(",");
        csvString = new String(Base64.getDecoder().decode(encodedData[1]));
        String[] rows = csvString.split("\\R");
        return Arrays.copyOfRange(rows, 1, rows.length);
    }
}
