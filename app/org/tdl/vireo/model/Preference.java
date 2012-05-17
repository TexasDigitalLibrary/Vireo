package org.tdl.vireo.model;

/**
 * A personal preference associated with a single person.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public interface Preference extends AbstractModel {

	/**
	 * @return The person associated with this preference.
	 */
	public Person getPerson();

	/**
	 * @return The name of the preference.
	 */
	public String getName();

	/**
	 * @param name
	 *            The new name of the preference.
	 */
	public void setName(String name);

	/**
	 * @return The value of the preference
	 */
	public String getValue();

	/**
	 * @param value
	 *            The new value of the preference.
	 */
	public void setValue(String value);

}
