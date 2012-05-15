package org.tdl.vireo.model.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.tdl.vireo.model.AbstractModel;
import org.tdl.vireo.model.Major;

import play.db.jpa.Model;

/**
 * Jpa specific implementation of Vireo's Major interface.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
@Entity
@Table(name = "Major")
public class JpaMajorImpl extends Model implements Major {

	@Column(nullable = false)
	public int displayOrder;

	@Column(nullable = false, unique = true)
	public String name;

	/**
	 * Create a new JpaMajorImpl
	 * 
	 * @param name
	 *            The name of the new major.
	 */
	protected JpaMajorImpl(String name) {
		// TODO: Check arguments

		this.displayOrder = 0;
		this.name = name;
	}

	@Override
	public JpaMajorImpl save() {
		return super.save();
	}

	@Override
	public JpaMajorImpl delete() {
		return super.delete();
	}

	@Override
	public JpaMajorImpl refresh() {
		return super.refresh();
	}

	@Override
	public JpaMajorImpl merge() {
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
		
		// TODO: check name
		
		this.name = name;
	}

}
