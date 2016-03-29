package org.tdl.vireo.model;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * 
 * @author gad
 */
@Entity
public class GraduationMonth extends BaseOrderedEntity {

	@Column(nullable = false, unique = true)
	private int month;
	
	/**
	 * 
	 */
	public GraduationMonth() {
    }

	/**
	 * Create a new JpaGraduationMonthImpl
	 * 
	 * @param month
	 *            The integer of the month, starting with 0 = january.
	 */
	public GraduationMonth(int month) {
	    setMonth(month);
	}
	
	public GraduationMonth(int month, int order) {
        this(month);
        setOrder(order);
    }

    /**
     * @return the month
     */
    public int getMonth() {
        return month;
    }

    /**
     * @param month the month to set
     */
    public void setMonth(int month) {
        this.month = month;
    }
}
