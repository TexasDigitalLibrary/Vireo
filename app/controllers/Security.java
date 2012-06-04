package controllers;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.tdl.vireo.model.RoleType;

/**
 * Security annotation to enforce particular Vireo roles at the controller level.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Security {

	/* The role type to restrict access to */
    RoleType value();
}
