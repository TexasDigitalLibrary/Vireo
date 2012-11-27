package org.tdl.vireo.model.jpa;

import java.util.Locale;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang.LocaleUtils;
import org.tdl.vireo.model.Language;

/**
 * Jpa specific implementation of Vireo's Language interface.
 * 
 * @author Micah Cooper
 */
@Entity
@Table(name = "language")
public class JpaLanguageImpl extends JpaAbstractModel<JpaLanguageImpl> implements Language {

	@Column(nullable = false)
	public int displayOrder;
	
	@Column(nullable = false, unique = true, length=255)
	public String name;

	/**
	 * Construct a new JpaLanguageImpl
	 * 
	 * @param code
	 *            The code of the new language.
	 * @param description
	 * 			  The description of the new language.
	 */
	protected JpaLanguageImpl(String name) {
		
		if (name == null || name.length() == 0)
			throw new IllegalArgumentException("Name is required");

		if (_toLocale(name) == null)
			throw new IllegalArgumentException("Name is not a valid locale");
		
		assertManager();
		
		this.displayOrder = 0;
		this.name = name;
	}
	
	@Override
	public JpaLanguageImpl save() {
		assertManager();

		return super.save();
	}
	
	@Override
	public JpaLanguageImpl delete() {
		assertManager();

		return super.delete();
	}

	@Override
	public int getDisplayOrder() {
		return displayOrder;
	}

	@Override
	public void setDisplayOrder(int displayOrder) {
		
		assertManager();
		this.displayOrder = displayOrder;
	}
	
	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setName(String name) {
		
		if (name == null || name.length() == 0)
			throw new IllegalArgumentException("Name is required");
		assertManager();

		this.name = name;
	}
	
	@Override
	public Locale getLocale() {
		return _toLocale(this.name);
	}
	
	/**
	 * Convert the given name to a Java locale.
	 * 
	 * @param name
	 *            The name of a java locale
	 * @return A java locale, or null if none found.
	 */
	protected static Locale _toLocale(String name) {
		if (name == null)
			return null;
		
		Locale locale = null;
		try {
			locale = LocaleUtils.toLocale(name);
		} catch (RuntimeException re) { /* ignore */ }
		
		return locale;
	}

}
