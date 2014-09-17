package org.tdl.vireo.model;

import java.util.List;

import org.tdl.vireo.state.State;

/**
 * The Vireo persistent repository for workflow rules.
 * This object follows the spring repository pattern, where this is the source
 * for creating and locating all persistent model objects.
 * 
 * @author <a href="mailto:huff@library.tamu.edu">Jeremy Huff</a>
 */
public interface WorkflowEmailRuleRepository {

	// //////////////////////////
	// WorkflowEmailRule Model //
	// //////////////////////////

	/**
	 * Create a new WorkflowEmailRule model. 
	 *
	 * @param AssociatedState
	 *            Workflow Email Rules's Associated State.
	 * @param condition
	 * 			  Workflow Email Rule's condition
	 * @param emailAddresses
	 * 			  Workflow Email Rule's email addresses   
	 * @param emailTemplate
	 * 			  Workflow Email Rule's email template                
	 */
	public WorkflowEmailRule createEmailRule(State associatedState, AbstractOrderedModel condition, 
			List<String> emailAddresses, EmailTemplate emailTemplate);

	/**
	 * Find a workflow email rule based upon their unique id.
	 * 
	 * @param id
	 *            Workflow Email Rules's id.
	 * @return The Workflow Email Rule object or null if not found.
	 */
	public WorkflowEmailRule findWorkflowEmailRule(Long id);

	/**
	 * Find a Workflow Email Rule based on their associated state.
	 * 
	 * @param state
	 *            The Workflow Email Rule's associated state.
	 * @return The person object or null if not found.
	 */
	 public List<WorkflowEmailRule> findWorkflowEmailRulesByState(State type);

	/**
	 * @return All person objects
	 */
	public List<WorkflowEmailRule> findAllWorkflowEmailRules();
	
}
