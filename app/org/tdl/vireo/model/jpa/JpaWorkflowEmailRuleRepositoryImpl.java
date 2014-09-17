package org.tdl.vireo.model.jpa;

import java.util.List;

import javax.persistence.TypedQuery;

import org.tdl.vireo.model.AbstractOrderedModel;
import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.Preference;
import org.tdl.vireo.model.WorkflowEmailRule;
import org.tdl.vireo.model.WorkflowEmailRuleRepository;
import org.tdl.vireo.state.State;

import play.db.jpa.JPA;

/**
 * Jpa specific implementation of the Vireo Workflow Email Rule Repository interface.
 * 
 * @author Jeremy Huff, huff@library.tamu.edu
 */
public class JpaWorkflowEmailRuleRepositoryImpl implements WorkflowEmailRuleRepository {
	
	// //////////////
	// WorkflowEmailRule Model
	// //////////////
	
	@Override
	public WorkflowEmailRule createEmailRule(State associatedState,
			AbstractOrderedModel condition, List<String> emailAddresses,
			EmailTemplate emailTemplate) {
		return new JpaWorkflowEmailRuleImpl(associatedState, condition, emailAddresses, emailTemplate);
	}

	@Override
	public WorkflowEmailRule findWorkflowEmailRule(Long id) {
		return (WorkflowEmailRule) JpaWorkflowEmailRuleImpl.findById(id);
	}

	@Override
	public List<WorkflowEmailRule> findWorkflowEmailRulesByState(State state) {
		final String select = "SELECT * FROM JpaWorkflowEmailRuleImpl AS w WHERE w.state = " + state;
		TypedQuery<JpaWorkflowEmailRuleImpl> query = JPA.em().createQuery(select, JpaWorkflowEmailRuleImpl.class);
		query.setParameter("type", state);
		
		List<JpaWorkflowEmailRuleImpl> results = query.getResultList();

		return (List) results;
	}

	@Override
	public List<WorkflowEmailRule> findAllWorkflowEmailRules() {
		return (List) JpaWorkflowEmailRuleImpl.findAll();
	}
	
}
