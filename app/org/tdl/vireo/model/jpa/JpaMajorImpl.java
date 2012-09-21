package org.tdl.vireo.model.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.tdl.vireo.model.Major;

/**
 * Jpa specific implementation of Vireo's Major interface.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
@Entity
@Table(name = "major")
public class JpaMajorImpl extends JpaAbstractModel<JpaMajorImpl> implements Major {

	@Column(nullable = false)
	public int displayOrder;

	@Column(nullable = false, unique = true, length=255) 
	public String name;

	/**
	 * Create a new JpaMajorImpl
	 * 
	 * @param name
	 *            The name of the new major.
	 */
	protected JpaMajorImpl(String name) {

		if (name == null || name.length() == 0)
			throw new IllegalArgumentException("Name is required");
		
		assertManager();
		
		this.displayOrder = 0;
		this.name = name;
	}

	@Override
	public JpaMajorImpl save() {
		assertManager();

		return super.save();
	}
	
	@Override
	public JpaMajorImpl delete() {
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

}
