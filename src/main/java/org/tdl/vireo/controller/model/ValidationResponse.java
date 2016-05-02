package org.tdl.vireo.controller.model;

import java.util.LinkedList;
import java.util.List;

import org.springframework.validation.ObjectError;

import edu.tamu.framework.validation.ModelBindingResult;

public class ValidationResponse {
    private final List<ObjectError> errors = new LinkedList<ObjectError>();
    private final List<ObjectError> warnings = new LinkedList<ObjectError>();
    private final List<ObjectError> infos = new LinkedList<ObjectError>();

    /**
     * Constructor for building a ValidationResponse from a {@link ModelBindingResult}
     * 
     * @param modelBindingResult
     */
    public ValidationResponse(ModelBindingResult modelBindingResult) {
        this(modelBindingResult.getAllErrors(), modelBindingResult.getAllWarnings(), modelBindingResult.getAllInfos());
    }
    
    /**
     * Constructor for building a ValidationResponse from lists of errors, warnings, infos.
     * 
     * @param errors
     * @param warnings
     * @param infos
     */
    public ValidationResponse(List<ObjectError> errors, List<ObjectError> warnings, List<ObjectError> infos) {
        this.errors.addAll(errors);
        this.warnings.addAll(warnings);
        this.infos.addAll(infos);
    }

    /**
     * @return the errors
     */
    public List<ObjectError> getErrors() {
        return errors;
    }

    /**
     * @return the warnings
     */
    public List<ObjectError> getWarnings() {
        return warnings;
    }

    /**
     * @return the infos
     */
    public List<ObjectError> getInfos() {
        return infos;
    }
}
