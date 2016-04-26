package org.tdl.vireo.service;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;
import static edu.tamu.framework.enums.ApiResponseType.VALIDATION_ERROR;
import static edu.tamu.framework.enums.ApiResponseType.VALIDATION_INFO;
import static edu.tamu.framework.enums.ApiResponseType.VALIDATION_WARNING;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.ObjectError;
import org.tdl.vireo.controller.model.ValidationResponse;
import org.tdl.vireo.model.BaseEntity;

import edu.tamu.framework.model.ApiResponse;
import edu.tamu.framework.validation.ModelBindingResult;

@Service
public class ValidationService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    /**
     * build an {@link ApiResponse} from a {@link BaseEntity}.
     * 
     * Meant to be used by controllers that receive a deserialized {@link BaseEntity} from the front-end
     * 
     * @param baseEntity
     * @return {@link ApiResponse}
     */
    public ApiResponse buildResponse(BaseEntity baseEntity) {
        return this.buildResponse(baseEntity.getBindingResult());
    }
    
    /**
     * build an {@link ApiResponse} from a {@link ModelBindingResult}.
     * 
     * Meant to be used by controllers that <b>DON'T</b> receive a deserialized {@link BaseEntity} from the front-end
     * 
     * @param modelBindingResult
     * @return {@link ApiResponse}
     */
    public ApiResponse buildResponse(ModelBindingResult modelBindingResult){
        // convert a ModelBindingResult into a ValidationResponse
        ValidationResponse validationResponse = new ValidationResponse(modelBindingResult);
        
        // if errors, with no warnings
        if (modelBindingResult.hasErrors()) {
            return new ApiResponse(VALIDATION_ERROR, validationResponse);
        }
        // else if no errors, with warnings
        else if (!modelBindingResult.hasErrors() && modelBindingResult.hasWarnings()) {
            return new ApiResponse(VALIDATION_WARNING, validationResponse);
        }
        // else if no errors, no warnings, maybe infos
        else {
            // deal with infos being set
            if (modelBindingResult.hasInfos()) {
                return new ApiResponse(VALIDATION_INFO, validationResponse);
            } else {
                return new ApiResponse(SUCCESS);
            }
        }
    }
    
    /**
     * Will look at all the @Column annotations or field names to determine if a named "column" exists for a given clazz.
     * 
     * Attaches an error to the ModelBindingResult if the field name or column name don't exist in the clazz.
     * 
     * @param clazz
     * @param column
     * @param modelBindingResult
     */
    public void validateColumn(Class<?> clazz, String column, ModelBindingResult modelBindingResult) {
        // create a list of possible column/field names for the clazz
        List<String> columnNames = new ArrayList<String>();
        for (Field field : clazz.getDeclaredFields()) {
            Column col = field.getAnnotation(Column.class);
            if (col != null && col.name() != null && !col.name().isEmpty()) {
                columnNames.add(col.name());
            } else {
                columnNames.add(field.getName());
            }
        }
        // if the column name isn't in the clazz, add error
        if(!columnNames.contains(column)) {
            modelBindingResult.addError(new ObjectError(clazz.getName(), "Cannot sort by column " + column + ": it doesn't exist!"));
        }
    }
    
    /**
     * Will convert a String to Long and append any errors to ModelBindingResult if the Long is invalid or not positive.
     * 
     * @param longString
     * @param objectName
     * @param modelBindingResult
     * @return
     */
    public Long validateLong(String longString, String objectName, ModelBindingResult modelBindingResult) {
        Long longRet = null;
        try {
            longRet = Long.parseLong(longString);
        } catch (NumberFormatException nfe) {
            modelBindingResult.addError(new ObjectError(objectName, "Long was invalid!"));
        }
        if(longRet != null && longRet < 0){
            modelBindingResult.addError(new ObjectError(objectName, "Long was not >= 0!"));
        }
        return longRet;
    }
}
