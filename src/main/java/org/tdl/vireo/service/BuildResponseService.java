package org.tdl.vireo.service;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;
import static edu.tamu.framework.enums.ApiResponseType.VALIDATION_ERROR;
import static edu.tamu.framework.enums.ApiResponseType.VALIDATION_INFO;
import static edu.tamu.framework.enums.ApiResponseType.VALIDATION_WARNING;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.tdl.vireo.controller.model.ValidationResponse;
import org.tdl.vireo.model.BaseEntity;

import edu.tamu.framework.model.ApiResponse;
import edu.tamu.framework.validation.ModelBindingResult;

@Service
public class BuildResponseService {
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
}
