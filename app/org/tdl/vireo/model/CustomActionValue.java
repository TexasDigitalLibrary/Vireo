package org.tdl.vireo.model;

/**
 * The value of a custom action associated with a particular submission.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public interface CustomActionValue extends AbstractModel {

	/**
	 * @return The submission this action value is associated with.
	 */
	public Submission getSubmission();

	/**
	 * @return the definition of this custom action.
	 */
	public CustomActionDefinition getDefinition();

	/**
	 * @return The boolean value of this action.
	 */
	public boolean getValue();

	/**
	 * @param value
	 *            The new value of this action.
	 */
	public void setValue(boolean value);

}
