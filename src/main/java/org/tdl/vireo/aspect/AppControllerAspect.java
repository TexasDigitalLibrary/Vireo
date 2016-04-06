package org.tdl.vireo.aspect;

import java.lang.annotation.Annotation;

import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import edu.tamu.framework.aspect.CoreControllerAspect;

@Component
@Aspect
public class AppControllerAspect extends CoreControllerAspect {
    
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public Object validate(Object arg0, Annotation arg1, String arg2) {
        // TODO Auto-generated method stub
        return null;
    } 

}
