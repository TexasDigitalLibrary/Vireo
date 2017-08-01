package org.tdl.vireo.proquest.impl;

import org.tdl.vireo.proquest.ProquestSubject;

/**
 * The most basic implementation of the proquest subject interface.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class ProquestSubjectImpl implements ProquestSubject {

	
	public String code;
	public String description;
	
	/**
	 * Construct a new proquest subject. Normally only a repository will call
	 * this method.
	 * 
	 * @param code
	 *            The code of the new subject.
	 * @param description
	 *            The description of the new subject.
	 */
	public ProquestSubjectImpl(String code, String description) {
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
