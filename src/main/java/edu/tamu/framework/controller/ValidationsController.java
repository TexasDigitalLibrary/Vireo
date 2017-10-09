/* 
 * ValidationsController.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.framework.controller;

import static edu.tamu.weaver.response.ApiStatus.INVALID;
import static edu.tamu.weaver.response.ApiStatus.SUCCESS;
import static edu.tamu.framework.util.EntityUtility.recursivelyFindField;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.ApiVariable;
import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.framework.validation.InputValidator;
import edu.tamu.framework.validation.Validator;

@Controller
@ApiMapping("/validations")
public class ValidationsController {

    protected static final String MODEL_VALIDATOR_FIELD = "modelValidator";

    @Value("${app.model.packages}")
    private String[] modelPackages;

    @ApiMapping("/{entityName}")
    public ApiResponse validations(@ApiVariable String entityName) {

        ApiResponse response = new ApiResponse(INVALID);

        Class<?> clazz = null;
        Object model = null;
        Object validator = null;

        for (String packageName : modelPackages) {
            try {
                clazz = Class.forName(packageName + "." + entityName);
                break;
            } catch (ClassNotFoundException e) {
                // e.printStackTrace();
            }
        }

        if (clazz != null) {

            try {
                model = clazz.getConstructor().newInstance();
            } catch (InstantiationException e) {
                // e.printStackTrace();
            } catch (IllegalAccessException e) {
                // e.printStackTrace();
            } catch (IllegalArgumentException e) {
                // e.printStackTrace();
            } catch (InvocationTargetException e) {
                // e.printStackTrace();
            } catch (NoSuchMethodException e) {
                // e.printStackTrace();
            } catch (SecurityException e) {
                // e.printStackTrace();
            }

            if (model != null) {

                Field field = recursivelyFindField(model.getClass(), MODEL_VALIDATOR_FIELD);

                if (field != null) {

                    field.setAccessible(true);

                    try {
                        validator = field.get(model);
                    } catch (IllegalArgumentException e) {
                        // e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        // e.printStackTrace();
                    }

                    field.setAccessible(false);
                }
            }

            if (validator != null) {

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

                response = new ApiResponse(SUCCESS, validations);

            }

        }

        return response;
    }

}
