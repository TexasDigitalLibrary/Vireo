package org.tdl.vireo.model;

/**
 * Custom actions are check lists that may be associated with a submission.
 * These actions are intended to record internal status of various workflow
 * processes performed by the review staff using Vireo. Vireo managers are able
 * to modify the list of custom actions that are available, this is the list of
 * definitions.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public interface CustomActionDefinition extends AbstractOrderedModel {

	/**
	 * @return The label of this custom action.
	 */
	public String getLabel();

	/**
	 * @param label
	 *            The new label of this custom action.
	 */
	public void setLabel(String label);
}
