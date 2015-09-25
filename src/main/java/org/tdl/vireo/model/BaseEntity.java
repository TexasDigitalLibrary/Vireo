package org.tdl.vireo.model;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class BaseEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected Long id;

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public boolean equals(Object obj) {
	    // if we're the same entity type
	    if(obj.getClass() == this.getClass()) {
	        // and we have the same Id
	        return ((BaseEntity)obj).getId().equals(this.getId());
	    }
	    return false;
	}
	
	/**
	 * 
	 */
	@Override
	public int hashCode() {
	    return id == null ? 0 : 29 * id.hashCode();
	}
	
}
