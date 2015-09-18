package org.tdl.vireo.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;

@Entity
public class ControlledVocabulary extends BaseEntity {
	Set<String> values = new HashSet<String>();

	/**
	 * @return the values
	 */
	public Set<String> getValues() {
		return values;
	}

	/**
	 * @param values
	 *            the values to set
	 */
	public void setValues(Set<String> values) {
		this.values = values;
	}
}
