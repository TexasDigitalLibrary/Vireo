package org.tdl.vireo.model;

import com.fasterxml.jackson.annotation.JsonView;
import edu.tamu.weaver.response.ApiView;
import edu.tamu.weaver.validation.model.ValidatingBaseEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import org.tdl.vireo.model.validation.OrganizationCategoryValidator;

@Entity
@Table(name = "organization_category", uniqueConstraints = @UniqueConstraint(columnNames = { "name" }))
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
