package org.tdl.vireo.model.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.tdl.vireo.model.AbstractModel;
import org.tdl.vireo.model.Department;

import play.data.validation.Required;
import play.db.jpa.Model;

/**
 * Jpa specific implementation of Vireo's Department interface
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 * 
 */
@Entity
@Table(name = "Department")
public class JpaDepartmentImpl extends Model implements Department {

	@Column(nullable = false)
	public int displayOrder;

	@Column(nullable = false, unique = true)
	public String name;

	/**
	 * Create a new JpaDepartmentImpl
	 * 
	 * @param name
	 *            The name of the new department.
	 */
	protected JpaDepartmentImpl(String name) {

		if (name == null || name.length() == 0)
			throw new IllegalArgumentException("Name is required");

		this.displayOrder = 0;
		this.name = name;
	}

	@Override
	public JpaDepartmentImpl save() {
		return super.save();
	}

	@Override
	public JpaDepartmentImpl delete() {
		return super.delete();
	}

	@Override
	public JpaDepartmentImpl refresh() {
		return super.refresh();
	}

	@Override
	public JpaDepartmentImpl merge() {
		return super.merge();
	}

    @Override
    public int getDisplayOrder() {
        return displayOrder;
    }

    @Override
    public void setDisplayOrder(int displayOrder) {
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
		
		this.name = name;
	}

}
