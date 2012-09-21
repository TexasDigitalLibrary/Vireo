package org.tdl.vireo.model.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.tdl.vireo.model.Degree;
import org.tdl.vireo.model.DegreeLevel;

/**
 * Jpa specific implementation of Vireo's Degree interface.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
@Entity
@Table(name = "degree",
       uniqueConstraints = { @UniqueConstraint( columnNames = { "name", "level" } ) } )
public class JpaDegreeImpl extends JpaAbstractModel<JpaDegreeImpl> implements Degree {

	@Column(nullable = false)
	public int displayOrder;

	@Column(nullable = false, length=255)
	public String name;

	@Column(nullable = false)
	public DegreeLevel level;

	/**
	 * Create a new JpaDegreeImpl
	 * 
	 * @param name
	 *            The name of the new degree.
	 * @param level
	 *            The level of the new degree.
	 */
	protected JpaDegreeImpl(String name, DegreeLevel level) {

		if (name == null || name.length() == 0)
			throw new IllegalArgumentException("Name is required");
		
		if (level == null)
			throw new IllegalArgumentException("Degree level is required");
		
		assertManager();
		
		this.displayOrder = 0;
		this.name = name;
		this.level = level;
	}

	@Override
	public JpaDegreeImpl save() {
		assertManager();

		return super.save();
	}
	
	@Override
	public JpaDegreeImpl delete() {
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
	public DegreeLevel getLevel() {
		return level;
	}

	@Override
	public void setLevel(DegreeLevel level) {
		
		if (level == null)
			throw new IllegalArgumentException("Degree level is required");
		
		assertManager();
		
		this.level = level;
	}

}
