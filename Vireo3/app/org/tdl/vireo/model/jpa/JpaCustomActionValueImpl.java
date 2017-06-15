package org.tdl.vireo.model.jpa;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.tdl.vireo.model.CustomActionDefinition;
import org.tdl.vireo.model.CustomActionValue;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.state.StateManager;

import play.modules.spring.Spring;

/**
 * Jpa specific implementation of Vireo's Custom Action Value interface.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
@Entity
@Table(name = "custom_action_value",
	   uniqueConstraints = { @UniqueConstraint( columnNames = { "submission_id", "definition_id" } ) } )
public class JpaCustomActionValueImpl extends JpaAbstractModel<JpaCustomActionValueImpl> implements
		CustomActionValue {

	@ManyToOne(targetEntity=JpaSubmissionImpl.class, optional=false)
	public Submission submission;

	@ManyToOne(targetEntity = JpaCustomActionDefinitionImpl.class, optional=false)
	public CustomActionDefinition definition;

	public boolean value;

	/**
	 * Create a new JpaCustomActionValueImpl
	 * 
	 * @param submission
	 *            The submission this custom action relates too.
	 * @param definition
	 *            The definition of this value.
	 * @param value
	 *            The actual value.
	 */
	protected JpaCustomActionValueImpl(Submission submission,
			CustomActionDefinition definition, boolean value) {
		
		if (submission == null)
			throw new IllegalArgumentException("Submission is required.");
		
		if (definition == null)
			throw new IllegalArgumentException("Custom action definition is required");

		assertReviewerOrOwner(submission.getSubmitter());
		
		this.submission = submission;
		this.definition = definition;
		this.value = value;
		
		// Ignore the log message if the submission is in the initial state.
		StateManager manager = Spring.getBeanOfType(StateManager.class);
		if (manager.getInitialState() != submission.getState()) {			
			String entry = "Custom action "+definition.getLabel()+" " + (value ? "set" : "unset");
			submission.logAction(entry).save();
		}
	}

	@Override
	public JpaCustomActionValueImpl save() {
		
		assertReviewerOrOwner(submission.getSubmitter());
		
		super.save();
		
		// Ignore the log message if the submission is in the initial state.
		StateManager manager = Spring.getBeanOfType(StateManager.class);
		if (manager.getInitialState() != submission.getState()) {			
			String entry = "Custom action "+definition.getLabel()+" " + (value ? "set" : "unset");
			submission.logAction(entry).save();
		}
		
		return this;
	}
	
	@Override
	public JpaCustomActionValueImpl delete() {

		assertReviewerOrOwner(submission.getSubmitter());

		((JpaSubmissionImpl) submission).removeCustomAction(this);
		
		// Ignore log message if the submission is in the initial state.
		StateManager manager = Spring.getBeanOfType(StateManager.class);
		if (manager.getInitialState() != submission.getState()) {
			String entry = "Custom action "+definition.getLabel()+" unset";
			submission.logAction(entry).save();
		}
		
		return super.delete();
	}

	@Override
	public Submission getSubmission() {
		return submission;
	}

	@Override
	public CustomActionDefinition getDefinition() {
		return definition;
	}

	@Override
	public boolean getValue() {
		return value;
	}

	@Override
	public void setValue(boolean value) {
		assertReviewerOrOwner(submission.getSubmitter());
		this.value = value;
	}

}
