package org.tdl.vireo.model.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.tdl.vireo.model.AbstractModel;
import org.tdl.vireo.model.Degree;
import org.tdl.vireo.model.DegreeLevel;

import play.data.validation.Required;
import play.db.jpa.Model;

/**
 * Jpa specific implementation of Vireo's Degree interface.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
@Entity
@Table(name = "Degree")
public class JpaDegreeImpl extends Model implements Degree {

	@Column(nullable = false)
	public int displayOrder;

	@Column(nullable = false)
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

		// TODO: check arguments

		this.displayOrder = 0;
		this.name = name;
		this.level = level;
	}

	@Override
	public JpaDegreeImpl save() {
		return super.save();
	}

	@Override
	public JpaDegreeImpl delete() {
		return super.delete();
	}

	@Override
	public JpaDegreeImpl refresh() {
		return super.refresh();
	}

	@Override
	public JpaDegreeImpl merge() {
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
		return name;
	}

	@Override
	public void setName(String name) {
		
		// Check name
		
		this.name = name;
	}

	@Override
	public DegreeLevel getLevel() {
		return level;
	}

	@Override
	public void setLevel(DegreeLevel level) {
		
		// Check level
		
		this.level = level;
	}

}
