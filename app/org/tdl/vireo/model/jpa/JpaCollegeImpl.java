package org.tdl.vireo.model.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.tdl.vireo.model.AbstractModel;
import org.tdl.vireo.model.College;

import play.data.validation.Required;
import play.db.jpa.Model;

/**
 * Jpa specific implementation of Vireo's College interface.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
@Entity
@Table(name = "College")
public class JpaCollegeImpl extends Model implements College {

	@Column(nullable = false)
	public int displayOrder;

	@Column(nullable = false, unique = true)
	public String name;

	/**
	 * Construct a new JpaCollegeImpl
	 * 
	 * @param name
	 *            The name of the new college.
	 */
	protected JpaCollegeImpl(String name) {

		// TODO: check incoming parameters;

		this.displayOrder = 0;
		this.name = name;
	}

	@Override
	public JpaCollegeImpl save() {
		return super.save();
	}

	@Override
	public JpaCollegeImpl delete() {
		return super.delete();
	}

	@Override
	public JpaCollegeImpl refresh() {
		return super.refresh();
	}

	@Override
	public JpaCollegeImpl merge() {
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
		
		// TODO: check name
		
		this.name = name;
	}

}
