package org.tdl.vireo.model.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.tdl.vireo.model.AbstractModel;
import org.tdl.vireo.model.CustomActionDefinition;
import org.tdl.vireo.model.CustomActionValue;
import org.tdl.vireo.model.Submission;

import play.data.validation.Required;
import play.db.jpa.Model;

/**
 * Jpa specific implementation of Vireo's Custom Action Value interface.
 * 
 * TODO: Create actionLog items when the submission is changed.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
@Entity
@Table(name = "CustomActionValue")
public class JpaCustomActionValueImpl extends Model implements
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

		// TODO: check arguments

		this.submission = submission;
		this.definition = definition;
		this.value = value;
	}

	@Override
	public JpaCustomActionValueImpl save() {
		return super.save();
	}

	@Override
	public JpaCustomActionValueImpl delete() {
		
		// TODO: Callback to submission and tell it that this value is being deleted.
		
		return super.delete();
	}

	@Override
	public JpaCustomActionValueImpl refresh() {
		return super.refresh();
	}

	@Override
	public JpaCustomActionValueImpl merge() {
		return super.merge();
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
		this.value = value;
	}

}
