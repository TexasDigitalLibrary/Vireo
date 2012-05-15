package org.tdl.vireo.model.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.tdl.vireo.model.AbstractModel;
import org.tdl.vireo.model.GraduationMonth;

import play.db.jpa.Model;

/**
 * Jpa specific implementation of Vireo's Graduation Month.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
@Entity
@Table(name = "GraduationMonth")
public class JpaGraduationMonthImpl extends Model implements GraduationMonth {

	@Column(nullable = false)
	public int displayOrder;

	@Column(nullable = false)
	public int month;

	/**
	 * Create a new JpaGraduationMonthImpl
	 * 
	 * @param month
	 *            The integer of the month, starting with 0 = january.
	 */
	protected JpaGraduationMonthImpl(int month) {

		// TODO: check the arguments

		this.displayOrder = 0;
		this.month = month;
	}

	@Override
	public JpaGraduationMonthImpl save() {
		return super.save();
	}

	@Override
	public JpaGraduationMonthImpl delete() {
		return super.delete();
	}

	@Override
	public JpaGraduationMonthImpl refresh() {
		return super.refresh();
	}

	@Override
	public JpaGraduationMonthImpl merge() {
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
	public int getMonth() {
		return month;
	}

	@Override
	public String getMonthName() {
		// TODO: translate month number into string.
		return null;
	}

	@Override
	public void setMonth(int month) {
		
		// TODO: check month
		
		this.month = month;
	}

}
