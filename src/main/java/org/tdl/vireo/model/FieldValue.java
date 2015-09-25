package org.tdl.vireo.model;

import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.REFRESH;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class FieldValue extends BaseEntity {
	
	@Column(columnDefinition = "TEXT", nullable = true)
	private String value;
	
	@ManyToOne(cascade = { DETACH, REFRESH, MERGE }, optional = false)
	private FieldPredicate fieldPredicate;
	
	public FieldValue() {}
	
	public FieldValue(FieldPredicate fieldPredicate) {
		setFieldPredicate(fieldPredicate);
	}
		
	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return the fieldProfile
	 */
	public FieldPredicate getFieldPredicate() {
		return fieldPredicate;
	}

	/**
	 * @param fieldProfile
	 *            the fieldProfile to set
	 */
	public void setFieldPredicate(FieldPredicate fieldPredicate) {
		this.fieldPredicate = fieldPredicate;
	}
	
}
