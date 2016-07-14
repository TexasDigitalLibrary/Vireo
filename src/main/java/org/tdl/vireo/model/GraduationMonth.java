package org.tdl.vireo.model;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.tdl.vireo.model.validation.GraduationMonthValidator;

import edu.tamu.framework.model.BaseOrderedEntity;

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
	    setModelValidator(new GraduationMonthValidator());
    }

	/**
	 * Create a new JpaGraduationMonthImpl
	 * 
	 * @param month
	 *            The integer of the month, starting with 0 = january.
	 */
	public GraduationMonth(int month) {
	    this();
	    setMonth(month);
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
