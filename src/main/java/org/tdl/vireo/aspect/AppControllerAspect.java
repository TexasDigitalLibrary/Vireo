package org.tdl.vireo.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.tdl.vireo.model.User;

import edu.tamu.framework.aspect.CoreControllerAspect;

@Component
@Aspect
public class AppControllerAspect extends CoreControllerAspect<User> {

}
