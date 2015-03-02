package org.tdl.vireo.model.jpa;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.tdl.vireo.model.EmbargoGuarantor;
import org.tdl.vireo.model.EmbargoType;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.search.Indexer;

import play.Logger;
import play.modules.spring.Spring;

/**
 * Jpa specific implementation of Vireo's Embargo Type interface.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
@Entity
@Table(name = "embargo_type", uniqueConstraints=
@UniqueConstraint(columnNames = {"name", "guarantor", "systemRequired"}))
public class JpaEmbargoTypeImpl extends JpaAbstractModel<JpaEmbargoTypeImpl> implements EmbargoType {
	
	@Column(nullable = false)
	public int displayOrder;
	
	@Column(nullable = false, length=255)
	public String name;

	@Column(nullable = false, length=32768) // 2^15
	public String description;

	public Integer duration;
	
	@Column(nullable = false)
	public boolean active;
	
	@Column(nullable = false, columnDefinition="BOOLEAN DEFAULT false")
	public Boolean systemRequired;

	// this requires a column definition in order to update the table column on old DB's (default value) 
	@Column(nullable = false, columnDefinition="INTEGER DEFAULT '0'")
	public EmbargoGuarantor guarantor;
	
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
			Integer duration, boolean active, EmbargoGuarantor guarantor) {

		if (name == null || name.length() == 0)
			throw new IllegalArgumentException("Name is required");
		
		if (description == null || description.length() == 0)
			throw new IllegalArgumentException("Description is required");
		
		if (duration != null && duration < 0)
			throw new IllegalArgumentException("Duration must be positive, or null");
		
		assertManager();
		
		this.systemRequired = false;
		this.displayOrder = 0;
		this.name = name;
		this.description = description;
		this.duration = duration;
		this.active = active;
		
		if(guarantor == null)
			this.guarantor = EmbargoGuarantor.DEFAULT;
		else
			this.guarantor = guarantor;
		
		//this.submissions = new ArrayList<Submission>();
	}

	@Override
	public JpaEmbargoTypeImpl save() {
		assertManager();

		return super.save();
	}
	
	@Override
	public JpaEmbargoTypeImpl delete() {
		assertManager();
		
		if (isSystemRequired())
			throw new IllegalStateException("Unable to delete the embargo '"+name+"' because it is required by the system.");
		
//		JpaSubmissionRepositoryImpl subRepo = Spring.getBeanOfType(JpaSubmissionRepositoryImpl.class);
		// Tell the indexer about all the submissions that will be affected by
		// this deletion.
//		TypedQuery<Long> affectedQuery = em().createQuery(
//				"SELECT sub.id "+
//				"FROM JpaSubmissionImpl AS sub "+
//				"WHERE sub.embargoType = :embargo ",
//				Long.class);
//		affectedQuery.setParameter("embargo", this);
		List<Long> affectedIds = new ArrayList<Long>();
		
//		Iterator<Submission> submissionsItr = subRepo.findAllSubmissions();
//		while(submissionsItr.hasNext()) {
//			Submission sub = submissionsItr.next();
//			if(sub.getEmbargoTypes().contains(this)) {
//				affectedIds.add(sub.getId());
//				sub.getEmbargoTypes().remove(this);
//				sub.save();
//			}
//		}
		
		for (Submission sub : getSubmissions()) {
			affectedIds.add(sub.getId());
			sub.removeEmbargoType(this);
        }
		
		Logger.info("Indexer affected IDs: " + affectedIds.size());
		Indexer indexer = Spring.getBeanOfType(Indexer.class);
		indexer.updated(affectedIds);		
		
		// Delete all values associated with this definition
//		em().createQuery(
//			"UPDATE JpaSubmissionImpl AS sub "+
//		    "SET sub.embargoType = null "+
//			"WHERE sub.embargoType = :embargo"
//			).setParameter("embargo", this)
//			.executeUpdate();
		
		
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
		
		// Just to be nice so that if you're not changing it we won't do the system required check.
		if (name.equals(this.name))
			return;
		
		if (isSystemRequired())
			throw new IllegalStateException("Unable to rename the embargo '"+this.name+"' because it is required by the system.");
		
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
		
		// Just to be nice so that if you're not changing it we won't do the system required check.
		if (description.equals(this.description))
			return;
		
		if (isSystemRequired())
			throw new IllegalStateException("Unable to change the embargo '"+name+"' because it is required by the system.");
		
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
		
		// Just to be nice so that if you're not changing it we won't do the system required check.
		if(duration == null && duration == this.duration)
			return;
		else if (duration == null && duration != this.duration) {
			// do nothing
		}
		else if (duration.equals(this.duration))
			return;
		
		if (isSystemRequired())
			throw new IllegalStateException("Unable to change the embargo '"+name+"' because it is required by the system.");
		
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
	
	@Override
	public boolean isSystemRequired() {
		return systemRequired;
	}
	
	@Override
	public void setSystemRequired(boolean systemRequired) {
		assertAdministrator();
		
		this.systemRequired = systemRequired;
	}
	
	@Override
	public EmbargoGuarantor getGuarantor() {
		return this.guarantor;
	}
	
	public void setGuarantor(EmbargoGuarantor guarantor) {
		if (guarantor == null)
			throw new IllegalArgumentException("Guarantor is required");
		
		assertManager();
		
		// Just to be nice so that if you're not changing it we won't do the system required check.
		if (guarantor.equals(this.guarantor))
			return;
		
		if (isSystemRequired())
			throw new IllegalStateException("Unable to change the embargo '"+name+"' because it is required by the system.");
				
		this.guarantor = guarantor;
	}

	@Override
	public List<Submission> getSubmissions() {
		List<Submission> ret = new ArrayList<Submission>();
		JpaSubmissionRepositoryImpl subRepo = Spring.getBeanOfType(JpaSubmissionRepositoryImpl.class);
		Iterator<Submission> submissionsItr = subRepo.findAllSubmissions();
		while(submissionsItr.hasNext()) {
			Submission sub = submissionsItr.next();
			if(sub.getEmbargoTypes().contains(this)) {
				ret.add(sub);
			}
		}
		return ret;
	}
	
}
