package org.tdl.vireo.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.tdl.vireo.model.validation.GraduationMonthValidator;

import edu.tamu.weaver.validation.model.ValidatingOrderedBaseEntity;

/**
 *
 * @author gad
 */
@Entity
public class GraduationMonth extends ValidatingOrderedBaseEntity implements EntityControlledVocabulary {

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
     * @param month
     *            the month to set
     */
    public void setMonth(int month) {
        this.month = month;
    }

    @Override
    public String getControlledName() {
        return String.valueOf(month);
    }

    @Override
    public String getControlledDefinition() {
        return "";
    }

    @Override
    public String getControlledIdentifier() {
        return "";
    }

    @Override
    public List<String> getControlledContacts() {
        return new ArrayList<String>();
    }

}
