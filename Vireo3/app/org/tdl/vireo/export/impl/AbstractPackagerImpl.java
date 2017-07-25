package org.tdl.vireo.export.impl;

import org.springframework.beans.factory.BeanNameAware;
import org.tdl.vireo.export.Packager;

/**
 * Abstract packager implementation.
 * 
 * It is expected that there will be many different packager implementations.
 * This class may be used to share common code between these various
 * implementations. The goal is to help remove some of the burden of creating a
 * new packager from scratch.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public abstract class AbstractPackagerImpl implements Packager, BeanNameAware {

	// Spring injected state.
	public String beanName;
	public String displayName;

	@Override
	public String getBeanName() {
		return beanName;
	}

	/**
	 * Spring injected bean name, from beanNameAware
	 * 
	 * @param beanName
	 *            The new bean name of this object.
	 */
	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * Spring injected display name.
	 * 
	 * @param displayName
	 *            The new display name of this object.
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

}
