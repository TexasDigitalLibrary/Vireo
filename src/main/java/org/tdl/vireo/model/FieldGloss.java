package org.tdl.vireo.model;

import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.REFRESH;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"value" , "language_id"}))
public class FieldGloss extends BaseEntity {
	
	@Column(nullable = false)
	private String value;
	
	@ManyToOne(cascade = { DETACH, REFRESH }, optional = false)
	private Language language;
	
	public FieldGloss() { }
	
	/**
	 * 
	 * @param value
	 * @param language
	 */
	public FieldGloss(String value, Language language) {
		setValue(value);
		setLanguage(language);
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
	 * 
	 * @return the language
	 */
	public Language getLanguage() {
		return language;
	}
	
	/**
	 * 
	 * @param language
	 */
	public void setLanguage(Language language) {
		this.language = language;
	}
}
