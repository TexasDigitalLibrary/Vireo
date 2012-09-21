package org.tdl.vireo.model.jpa;

import java.text.DateFormatSymbols;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.tdl.vireo.model.GraduationMonth;

/**
 * Jpa specific implementation of Vireo's Graduation Month.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
@Entity
@Table(name = "graduation_month")
public class JpaGraduationMonthImpl extends JpaAbstractModel<JpaGraduationMonthImpl> implements GraduationMonth {

	@Column(nullable = false)
	public int displayOrder;

	@Column(nullable = false, unique = true)
	public int month;

	/**
	 * Create a new JpaGraduationMonthImpl
	 * 
	 * @param month
	 *            The integer of the month, starting with 0 = january.
	 */
	protected JpaGraduationMonthImpl(int month) {

		if (month < 0 || month > 11)
			throw new IllegalArgumentException("Month value is out of range");

		assertManager();
		
		this.displayOrder = 0;
		this.month = month;
	}

	@Override
	public JpaGraduationMonthImpl save() {
		assertManager();

		return super.save();
	}
	
	@Override
	public JpaGraduationMonthImpl delete() {
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
	public int getMonth() {
		return month;
	}

	@Override
	public String getMonthName() {
		return new DateFormatSymbols().getMonths()[month];
	}

	@Override
	public void setMonth(int month) {
		
		if (month < 0 || month > 11)
			throw new IllegalArgumentException("Month value is out of range");
		
		assertManager();
		this.month = month;
	}

}
