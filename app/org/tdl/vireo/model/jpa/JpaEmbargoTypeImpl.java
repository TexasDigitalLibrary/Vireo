package org.tdl.vireo.model.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.tdl.vireo.model.AbstractModel;
import org.tdl.vireo.model.EmbargoType;

import play.db.jpa.Model;

/**
 * Jpa specific implementation of Vireo's Embargo Type interface.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
@Entity
@Table(name = "EmbargoType")
public class JpaEmbargoTypeImpl extends Model implements EmbargoType {

	@Column(nullable = false)
	public int order;

	@Column(nullable = false)
	public boolean active;

	@Column(nullable = false)
	public String description;

	public Long duration;

	/**
	 * Create a new JpaEmbargoTypeImpl.
	 * 
	 * @param active
	 *            Weather the embargo is active.
	 * @param description
	 *            The description of the embargo.
	 * @param duration
	 *            The duration of the embargo, or null, or -1 to signify no
	 *            duration.
	 */
	protected JpaEmbargoTypeImpl(boolean active, String description,
			Long duration) {

		// TODO: check arguments

		this.order = 0;
		this.active = active;
		this.description = description;
		this.duration = duration;
	}

	@Override
	public JpaEmbargoTypeImpl save() {
		return super.save();
	}

	@Override
	public JpaEmbargoTypeImpl delete() {
		return super.delete();
	}

	@Override
	public JpaEmbargoTypeImpl refresh() {
		return super.refresh();
	}

	@Override
	public JpaEmbargoTypeImpl merge() {
		return super.merge();
	}

	@Override
	public int getOrder() {
		return order;
	}

	@Override
	public void setOrder(int order) {
		this.order = order;
	}

	@Override
	public boolean isActive() {
		return active;
	}

	@Override
	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public void setDescription(String description) {
		
		// TODO: check description
		
		this.description = description;
	}

	@Override
	public Long getDuration() {
		return duration;
	}

	@Override
	public void setDuration(Long duration) {
		
		// TODO: check duration
		
		this.duration = duration;
	}

}
