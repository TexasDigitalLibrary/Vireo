package org.tdl.vireo.model;

/**
 * Embargoes restrict the release of a submission for a length of time. If an
 * embargo type is active then it may be selected by a student for any new
 * application. Each embargo may have a pre-defined duration before it can be
 * released. The duration is defined in months. If the type has no 
 * pre-defined duration then the duration should be set to Null, and a 
 * duration of zero means no embargo.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public interface EmbargoType extends AbstractOrderedModel {

	/**
	 * @return The name of this embargo type
	 */
	public String getName();

	/**
	 * @param name
	 *            The new name of this embargo type
	 */
	public void setName(String name);

	/**
	 * @return The English description for this embargo type.
	 */
	public String getDescription();

	/**
	 * @param description
	 *            The new English description for this embargo type.
	 */
	public void setDescription(String description);

	/**
	 * @return Return the static duration for this embargo type measured in
	 *         months. If there is no predefined duration then null is
	 *         returned. A duration of "zero" means there is no embargo. 
	 *         Negative durations are not permitted.
	 */
	public Integer getDuration();

	/**
	 * @param duration
	 *            The new duration of this embargo type measured in
	 *            months.
	 */
	public void setDuration(Integer duration);
	
	/**
	 * @return true if this embargo type is active.
	 */
	public boolean isActive();

	/**
	 * @param active
	 *            Set the new active value for this type.
	 */
	public void setActive(boolean active);

}
