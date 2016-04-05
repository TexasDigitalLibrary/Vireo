package org.tdl.vireo.aspect;

import java.lang.annotation.Annotation;

import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.Validator;
import org.tdl.vireo.model.BaseEntity;

import edu.tamu.framework.aspect.CoreControllerAspect;

@Component
@Aspect
public class AppControllerAspect extends CoreControllerAspect {
    
    private Logger logger = LoggerFactory.getLogger(this.getClass()); 
    
    private static final String MVC_VALIDATOR_NAME = "mvcValidator";
        
    private Validator validator;
    
    @Autowired
    public AppControllerAspect(ApplicationContext applicationContext) {
        if (applicationContext.containsBean(MVC_VALIDATOR_NAME)) {
            this.validator = applicationContext.getBean(MVC_VALIDATOR_NAME, Validator.class);
        }
        else if (ClassUtils.isPresent("javax.validation.Validator", getClass().getClassLoader())) {
            Class<?> clazz;
            try {
                String className = "org.springframework.validation.beanvalidation.OptionalValidatorFactoryBean";
                clazz = ClassUtils.forName(className, this.getClass().getClassLoader());
            }
            catch (Throwable ex) {
                throw new BeanInitializationException("Could not find default validator class", ex);
            }
            this.validator = (Validator) BeanUtils.instantiate(clazz);          
            
        }
        else {          
            this.validator = new Validator() {
                @Override
                public boolean supports(Class<?> clazz) {
                    return false;
                }
                @Override
                public void validate(Object target, Errors errors) {
                    System.out.println("\n\nDOH!!!\n\n");
                }
            };
        }
    }
    
    public Object validate(Object object, Annotation ann, String typeName) {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(object, typeName);
        
        Object hints = AnnotationUtils.getValue(ann);
        Object[] validationHints = (hints instanceof Object[] ? (Object[]) hints : new Object[] {hints});
        
        if (this.validator instanceof SmartValidator) {
            ((SmartValidator) this.validator).validate(object, bindingResult, validationHints);
        }
        else {
            this.validator.validate(object, bindingResult);
        }
        
        ((BaseEntity) object).setBindingResult(bindingResult);
        
        return object;
    }

}
