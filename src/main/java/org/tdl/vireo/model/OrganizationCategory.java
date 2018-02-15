package org.tdl.vireo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.tdl.vireo.model.validation.OrganizationCategoryValidator;

import edu.tamu.weaver.validation.model.ValidatingBaseEntity;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "name" }))
public class OrganizationCategory extends ValidatingBaseEntity {

    @Column(nullable = false)
    private String name;

    public OrganizationCategory() {
        setModelValidator(new OrganizationCategoryValidator());
    }

    /**
     *
     * @param name
     * @param level
     */
    public OrganizationCategory(String name) {
        this();
        setName(name);
    }

    /**
     *
     * @return String name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

}
