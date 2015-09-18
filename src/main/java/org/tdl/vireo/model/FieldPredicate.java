package org.tdl.vireo.model;

import javax.persistence.Entity;

@Entity
public class FieldPredicate extends BaseEntity {
	String value;

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
}
