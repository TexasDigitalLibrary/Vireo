package org.tdl.vireo.model;

import java.util.HashMap;
import java.util.List;

import org.tdl.vireo.email.RecipientType;
import org.tdl.vireo.model.jpa.JpaAdministrativeGroupImpl;
import org.tdl.vireo.model.jpa.JpaEmailTemplateImpl;
import org.tdl.vireo.model.jpa.JpaEmailWorkflowRuleConditionImpl;

/**
 * This class represents the email rules which may be created in Vireo
 * 
 * @author <a href="mailto:huff@library.tamu.edu">Jeremy Huff</a>
 */
public interface EmailWorkflowRule extends AbstractWorkflowRule {

	/**
	 * Mark this rule as a system-installed rule or not
	 * @param isSystem - whether this rule is a system-installed rule or not
	 */
	public void setIsSystem(boolean isSystem);
	
	/**
	 * Gets whether this rule is a system-installed rule
	 * @return - true if it's a system-installed rule
	 */
	public boolean isSystem();
	
	/**
	 * Marks this rule as disabled
	 */
	public void disable();
	
	/**
	 * Marks this rule as enabled
	 */
	public void enable();
	
	/**
	 * Gets whether this rule is disabled
	 * @return - true if disabled
	 */
	public boolean isDisabled();
	
	/**
	 * @return The email template attached to this rule
	 */
	public JpaEmailTemplateImpl getEmailTemplate();

	/**
	 * @param emailTemplate
	 *            Set the email template associated with this rule
	 */
	public void setEmailTemplate(JpaEmailTemplateImpl emailTemplate);
	
	/**
	 * @return The runtime generated list of recipients for this current rule+submission combination
	 */
	public List<String> getRecipients(Submission submission);
	
	/**
	 * 
	 * @return RecipientType enum
	 */
	public RecipientType getRecipientType();
	
	/**
	 * @param adminGroup - Set the administrative group recipient for this rule if any (null if none)
	 */
	public void setAdminGroupRecipient(JpaAdministrativeGroupImpl adminGroup);
	
	/**
	 * 
	 * @return the administrative group associated with this rule if any (null if none)
	 */
	public JpaAdministrativeGroupImpl getAdminGroupRecipient();
	
	/**
	 * @param recipientType - Set the {@link RecipientType} for this rule
	 */
	public void setRecipientType(RecipientType recipientType);

}
