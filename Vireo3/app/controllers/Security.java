package controllers;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.tdl.vireo.model.RoleType;

/**
 * Security annotation to enforce particular Vireo roles at the controller level.
 * 
 * How to use:
 * 
 * 1) On all vireo controllers add the @With(Authentication.class) annotation
 *    to the class. This class makes the security annotation actionable. 
 *    Without that annotation no security contstraints will be imposed by this
 *    annotation.
 * 
 * 2) On any controller action you need to ensure is only accessed by users
 *    with a particular role level or higher, add this annotation with the
 *    minimum role requirement.
 * 
 * Example:
 * 
 * @Security(RoleType.REVIEWER)
 * public void reviewSubmission() {
 *    // Something cool
 * }
 * 
 * In this example only users who are logged in with a role of either 
 * ADMINISTRATOR, MANAGER, or REVIWER are able to access this method.
 * 
 * @Security(RoleType.STUDENT)
 * public void startSubmission() {
 *    // Something cool
 * }
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Security {

	/* The role type to restrict access to */
    RoleType value();
}
