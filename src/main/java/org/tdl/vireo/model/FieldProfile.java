package org.tdl.vireo.model;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.REFRESH;
import static javax.persistence.FetchType.EAGER;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import org.tdl.vireo.enums.InputType;

@Entity
public class FieldProfile extends BaseEntity {

	@Enumerated
	@Column(nullable = false)
	private InputType inputType;
	
	@Column(nullable = false)
	private Boolean repeatable;

	@Column(nullable = false)
	private Boolean required;
	
	@ManyToMany(cascade = ALL, fetch = EAGER)
	private Set<FieldGloss> fieldGlosses;

	@ManyToOne(cascade = { DETACH, REFRESH, MERGE }, optional = false)
	private FieldPredicate fieldPredicate;

	@ManyToMany(cascade = { DETACH, REFRESH, MERGE })
	private Set<ControlledVocabulary> controlledVocabularies;
	
	public FieldProfile() {
		setFieldGlosses(new HashSet<FieldGloss>());
		setControlledVocabularies(new HashSet<ControlledVocabulary>());
	}

	public FieldProfile(FieldPredicate fieldPredicate, InputType inputType, Boolean repeatable, Boolean required) {
		this();
		setFieldPredicate(fieldPredicate);
		setInputType(inputType);
		setRepeatable(repeatable);
		setRequired(required);		
	}
	
	/**
	 * @return the inputType
	 */
	public InputType getInputType() {
		return inputType;
	}

	/**
	 * @param inputType
	 *            the inputType to set
	 */
	public void setInputType(InputType inputType) {
		this.inputType = inputType;
	}	
	
	/**
	 * @return the repeatable
	 */
	public Boolean isRepeatable() {
		return repeatable;
	}

	/**
	 * @param repeatable
	 *            the repeatable to set
	 */
	public void setRepeatable(Boolean repeatable) {
		this.repeatable = repeatable;
	}

	/**
	 * @return the required
	 */
	public Boolean isRequired() {
		return required;
	}

	/**
	 * @param required
	 *            the required to set
	 */
	public void setRequired(Boolean required) {
		this.required = required;
	}
	
	/**
	 * @return the fieldGlosses
	 */
	public Set<FieldGloss> getFieldGlosses() {
		return fieldGlosses;
	}

	/**
	 * @param fieldGlosses
	 *            the fieldGlosses to set
	 */
	public void setFieldGlosses(Set<FieldGloss> fieldGlosses) {
		this.fieldGlosses = fieldGlosses;
	}

	/**
	 * 
	 * @param fieldGloss
	 */
	public void addFieldGloss(FieldGloss fieldGloss) {
		getFieldGlosses().add(fieldGloss);
	}

	/**
	 * 
	 * @param fieldGloss
	 */
	public void removeFieldGloss(FieldGloss fieldGloss) {
		getFieldGlosses().remove(fieldGloss);
	}

	/**
	 * @return the fieldPredicate
	 */
	public FieldPredicate getFieldPredicate() {
		return fieldPredicate;
	}

	/**
	 * @param fieldPredicate
	 *            the fieldPredicate to set
	 */
	public void setFieldPredicate(FieldPredicate fieldPredicate) {
		this.fieldPredicate = fieldPredicate;
	}

	/**
	 * @return the controlledVocabularies
	 */
	public Set<ControlledVocabulary> getControlledVocabularies() {
		return controlledVocabularies;
	}

	/**
	 * @param controlledVocabularies
	 *            the controlledVocab to set
	 */
	public void setControlledVocabularies(Set<ControlledVocabulary> controlledVocabularies) {
		this.controlledVocabularies = controlledVocabularies;
	}

	/**
	 * 
	 * @param controlledVocabularies
	 */
	public void addControlledVocabulary(ControlledVocabulary controlledVocabulary) {
		getControlledVocabularies().add(controlledVocabulary);
	}

	/**
	 * 
	 * @param controlledVocabulary
	 */
	public void removeControlledVocabulary(ControlledVocabulary controlledVocabulary) {
		getControlledVocabularies().remove(controlledVocabulary);
	}
	
	/**
	 * 
	 */
	@Override
	public boolean equals(Object obj) {
		if(obj == null) {
			return false;
		}
		if(!(obj instanceof FieldProfile)) {
			return false;
		}
		FieldProfile other = (FieldProfile) obj;
		return id.equals(other.id);
	}

	/**
	 * 
	 */
	@Override
	public int hashCode() {
	    return id == null ? 0 : 29 * id.hashCode() + 31 * repeatable.hashCode() + 41 * required.hashCode() + 43 * inputType.hashCode();
	}

}
