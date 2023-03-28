package org.tdl.vireo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.tdl.vireo.model.validation.OrganizationCategoryValidator;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;

import edu.tamu.weaver.response.ApiView;
import edu.tamu.weaver.validation.model.ValidatingBaseEntity;

@Entity
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "name" }))
public class OrganizationCategory extends ValidatingBaseEntity {

    @JsonView(ApiView.Partial.class)
    @Column(nullable = false)
    private String name;

    public OrganizationCategory() {
        setModelValidator(new OrganizationCategoryValidator());
    }

    /**
     *
     * @param name
     */
    public OrganizationCategory(String name) {
        this();
        setName(name);
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

}
