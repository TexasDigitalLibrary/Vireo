package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.ERROR;
import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.stereotype.Controller;

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.ApiVariable;
import edu.tamu.framework.controller.CoreValidationsController;
import edu.tamu.framework.model.ApiResponse;
import edu.tamu.framework.util.ValidationUtility;
import edu.tamu.framework.validation.InputValidator;
import edu.tamu.framework.validation.Validator;

@Controller
@ApiMapping("/validations")
public class AppValidationsController extends CoreValidationsController {

	private static final String APP_MODEL_PACKAGE = "org.tdl.vireo.model";
	
    @ApiMapping("/{entityName}")
    public ApiResponse validations(@ApiVariable String entityName) {
        
        Class<?> clazz = null;        
        Object model = null;        
        Object validator = null;        
        
        try {
            clazz = Class.forName(APP_MODEL_PACKAGE + "." + entityName);
        } catch (ClassNotFoundException e) {
            //e.printStackTrace();
        }
        
        if(clazz != null) {
            
            try {
                model = clazz.getConstructor().newInstance();
            } catch (InstantiationException e) {
                //e.printStackTrace();
            } catch (IllegalAccessException e) {
                //e.printStackTrace();
            } catch (IllegalArgumentException e) {
                //e.printStackTrace();
            } catch (InvocationTargetException e) {
                //e.printStackTrace();
            } catch (NoSuchMethodException e) {
                //e.printStackTrace();
            } catch (SecurityException e) {
                //e.printStackTrace();
            }
            
            if(model != null) {
                
                Field field = ValidationUtility.recursivelyFindField(model.getClass(), MODEL_VALIDATOR_FIELD);
                
                if(field != null) {
                    
                    field.setAccessible(true);
                    
                    try {
                    	validator = field.get(model);
                    } catch (IllegalArgumentException e) {
                        //e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        //e.printStackTrace();
                    }
            
                    field.setAccessible(false);                
                }            
            }        
        }
        
        if(validator == null) {
            new ApiResponse(ERROR);
        }
        
        Map<String, Map<String, InputValidator>> validations = new HashMap<String, Map<String, InputValidator>>();
        
        for (Entry<String, List<InputValidator>> entry : ((Validator) validator).getInputValidators().entrySet()) {
            String key = entry.getKey();            
            List<InputValidator> inputValidators = entry.getValue();
            Map<String, InputValidator> inputValidatorMap = new HashMap<String, InputValidator>();            
            inputValidators.forEach(inputValidator -> {
                inputValidatorMap.put(inputValidator.getType().toString(), inputValidator);
            });            
            validations.put(key, inputValidatorMap);
        }
        
        
        return new ApiResponse(SUCCESS, validations);
    }
    
}
