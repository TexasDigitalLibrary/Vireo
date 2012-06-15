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
@Table(name = "embargo_type")
public class JpaEmbargoTypeImpl extends JpaAbstractModel<JpaEmbargoTypeImpl> implements EmbargoType {
	
	@Column(nullable = false)
	public int displayOrder;
	
	@Column(nullable = false, unique = true)
	public String name;

	@Column(nullable = false, length=32768) // 2^15
	public String description;

	public Integer duration;
	
	@Column(nullable = false)
	public boolean active;

	/**
	 * Create a new JpaEmbargoTypeImpl.
	 * 
	 * @param name
	 *            The unique name of the embargo
	 * @param description
	 *            The description of the embargo.
	 * @param duration
	 *            The duration of the embargo, or null, may not be negative
	 * @param active
	 *            Weather the embargo is active.
	 */
	protected JpaEmbargoTypeImpl(String name, String description,
			Integer duration, boolean active) {

		if (name == null || name.length() == 0)
			throw new IllegalArgumentException("Name is required");
		
		if (description == null || description.length() == 0)
			throw new IllegalArgumentException("Description is required");
		
		if (duration != null && duration < 0)
			throw new IllegalArgumentException("Duration must be positive, or null");

		assertManager();
		
		this.displayOrder = 0;
		this.name = name;
		this.description = description;
		this.duration = duration;
		this.active = active;
	}

	@Override
	public JpaEmbargoTypeImpl save() {
		assertManager();

		return super.save();
	}
	
	@Override
	public JpaEmbargoTypeImpl delete() {
		assertManager();

		return super.delete();
	}
	
    @Override
    public int getDisplayOrder() {
        return displayOrder;
    }

    @Override
    public void setDisplayOrder(int displayOrder) {
    	
    	assertManager();
    	
        this.displayOrder = displayOrder;
    }

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		
		if (name == null || name.length() == 0)
			throw new IllegalArgumentException("Name is required");
		
		assertManager();
		
		this.name = name;
	}
	
	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public void setDescription(String description) {
		
		if (description == null || description.length() == 0)
			throw new IllegalArgumentException("Description is required");
		
		assertManager();
		this.description = description;
	}

	@Override
	public Integer getDuration() {
		return duration;
	}

	@Override
	public void setDuration(Integer duration) {
		
		if (duration != null && duration < 0)
			throw new IllegalArgumentException("Duration must be positive, or null");
		
		assertManager();
		this.duration = duration;
	}
	
	@Override
	public boolean isActive() {
		return active;
	}

	@Override
	public void setActive(boolean active) {
		
		assertManager();
		this.active = active;
	}

}
