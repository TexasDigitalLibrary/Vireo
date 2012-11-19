package org.tdl.vireo.proquest.impl;

import org.tdl.vireo.proquest.ProquestLanguage;

/**
 * The most basic implementation of the ProquestLanguage interface.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class ProquestLanguageImpl implements ProquestLanguage {

	public String code;
	public String description;

	/**
	 * Create a new proquest language. Normally only a repository should call
	 * this method.
	 * 
	 * @param code
	 *            The code of the new language.
	 * @param description
	 *            The description of the new language
	 */
	public ProquestLanguageImpl(String code, String description) {
		this.code = code;
		this.description = description;
	}

	@Override
	public String getCode() {
		return code;
	}

	@Override
	public String getDescription() {
		return description;
	}

}
