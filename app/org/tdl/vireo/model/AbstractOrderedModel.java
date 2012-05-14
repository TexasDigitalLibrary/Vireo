package org.tdl.vireo.model;

/**
 * This abstract parent interface extends the base AbstractModel and adds the
 * ability for the objects to be sorted based upon a relative order value placed
 * on each object.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public abstract interface AbstractOrderedModel extends AbstractModel {

	/**
	 * @return The current relative order among other objects of the same type.
	 */
	public int getOrder();

	/**
	 * @param order
	 *            The new relative order among other objects of the same type.
	 */
	public void setOrder(int order);

}
