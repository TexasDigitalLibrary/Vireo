package org.tdl.vireo.proquest.impl;

import org.tdl.vireo.proquest.ProquestDegree;

/**
 * The most basic implementation of the ProquestDegree interface.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class ProquestDegreeImpl implements ProquestDegree {

	public String code;
	public String description;

	/**
	 * Create a new proquest degree. Normally only a repository should call
	 * this method.
	 * 
	 * @param code
	 *            The code of the new degree.
	 * @param description
	 *            The description of the new degree
	 */
	public ProquestDegreeImpl(String code, String description) {
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
