package org.tdl.vireo.model;

/**
 * This abstract parent interface extends the base AbstractModel and adds the
 * ability for the objects to be sorted based upon a relative display order value placed
 * on each object.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public abstract interface AbstractOrderedModel extends AbstractModel {

	/**
	 * @return The current relative display order among other objects of the same type.
	 */
	public int getDisplayOrder();

	/**
	 * @param displayOrder
	 *            The new relative display order among other objects of the same type.
	 */
	public void setDisplayOrder(int displayOrder);

}
