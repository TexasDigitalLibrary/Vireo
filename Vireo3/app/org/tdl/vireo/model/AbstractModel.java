package org.tdl.vireo.model;


/**
 * This is the abstract parent interface for all Vireo model objects. It defines
 * the very basic information that is expected of all model objects such as a
 * unique identifier, along with save and delete actions to control persistence.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 * 
 * @param <T>
 *            The specific type of vireo model.
 */
public abstract interface AbstractModel {

	/**
	 * @return The unique identifier of this object. Some implementation may use
	 *         a unique identifier across Vireo models, while other
	 *         implementations may have ids that are unique only among all
	 *         objects of the same type.
	 */
	public Long getId();

	/**
	 * Persist any changes made to this object.
	 * 
	 * @return The saved object, for convenience.
	 */
	public <T extends AbstractModel> T save();

	/**
	 * Remove the object from persistent storage.
	 * 
	 * @return The deleted object, for convenience.
	 */
	public <T extends AbstractModel> T delete();

	/**
	 * Refresh the entity state. This will reset any in memory changes to the
	 * object and reload the state from the persistent storage.
	 * 
	 * @return the refreshed object, for convenience.
	 */
	public <T extends AbstractModel> T refresh();

	/**
	 * Merge this object to obtain a managed entity. Note the object returned
	 * may be a new instance of the object merged into the current transaction.
	 * 
	 * @return The merged model
	 */
	public <T extends AbstractModel> T merge();
	
	/**
	 * Remove this object as a currently managed entity. After calling this
	 * method accessing relationship information may result in an exception, and
	 * it is no longer valid to call any persistence method. In order to restore
	 * the object to a persistable state the merge() method needs to be called.
	 * 
	 * @return the detached model, for convienence
	 */
	public <T extends AbstractModel> T detach();

}
