package org.tdl.vireo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.tdl.vireo.enums.Language;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"value" , "language"}))
public class FieldGloss extends BaseEntity {
	
	@Column(nullable = false)
	private String value;
	
	@Enumerated
	@Column(nullable = false)
	private Language language;
	
	public FieldGloss() {}
	
	/**
	 * Create a FieldGloss with default ENGLISH
	 * @param value
	 */
	public FieldGloss(String value) {
		setValue(value);
		setLanguage(Language.ENGLISH);
	}
	
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
