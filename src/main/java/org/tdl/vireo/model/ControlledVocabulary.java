package org.tdl.vireo.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Enumerated;

import org.tdl.vireo.enums.Language;

@Entity
public class ControlledVocabulary extends BaseEntity {

	@Column(nullable = false, unique = true)
	private String name;

	@ElementCollection
	@Column(columnDefinition = "TEXT", nullable = true, unique = true)
	private Set<String> values;
	
	@Enumerated
	@Column(nullable = false)
	private Language language;
	
	public ControlledVocabulary() {
		setValues(new HashSet<String>());
	}
	
	public ControlledVocabulary(String name) {
		this();
		setName(name);
		setLanguage(Language.ENGLISH);
	}
	
	public ControlledVocabulary(String name, Language language) {
		this();
		setName(name);
		setLanguage(language);
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

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
	
	/**
	 * 
	 * @param value
	 */
	public void addValue(String value) {
		getValues().add(value);
	}
	
	/**
	 * 
	 * @return Language language
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

	/**
	 * 
	 * @param value
	 */
	public void removeValue(String value) {
		getValues().remove(value);
	}
	
}
