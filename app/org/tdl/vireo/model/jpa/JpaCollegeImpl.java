package org.tdl.vireo.model.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.tdl.vireo.model.College;

/**
 * Jpa specific implementation of Vireo's College interface.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
@Entity
@Table(name = "college")
public class JpaCollegeImpl extends JpaAbstractModel<JpaCollegeImpl> implements College {

	@Column(nullable = false)
	public int displayOrder;

	@Column(nullable = false, unique = true, length=255)
	public String name;

	/**
	 * Construct a new JpaCollegeImpl
	 * 
	 * @param name
	 *            The name of the new college.
	 */
	protected JpaCollegeImpl(String name) {

		if (name == null || name.length() == 0)
			throw new IllegalArgumentException("Name is required");

		assertManager();
		
		this.displayOrder = 0;
		this.name = name;
	}
	
	@Override
	public JpaCollegeImpl save() {
		assertManager();

		return super.save();
	}
	
	@Override
	public JpaCollegeImpl delete() {
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
		return this.name;
	}

	@Override
	public void setName(String name) {
		
		if (name == null || name.length() == 0)
			throw new IllegalArgumentException("Name is required");
		assertManager();

		this.name = name;
	}

}
