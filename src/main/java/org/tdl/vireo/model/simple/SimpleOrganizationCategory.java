package org.tdl.vireo.model.simple;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.hibernate.annotations.Immutable;
import org.tdl.vireo.model.OrganizationCategory;

@Entity
@Immutable
@Table(name = "organization_category")
public class SimpleOrganizationCategory implements Serializable {

    @Transient
    private static final long serialVersionUID = 6761882663334170936L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", insertable = false, updatable = false, nullable = false)
    private Long id;

    @Column(insertable = false, updatable = false, nullable = false)
    private String name;

    public static OrganizationCategory toOrganizationCategory(SimpleOrganizationCategory simpleOrganizationCategory) {
        if (simpleOrganizationCategory == null) {
            return null;
        }

        OrganizationCategory organizationCategory = new OrganizationCategory();

        organizationCategory.setId(simpleOrganizationCategory.getId());
        organizationCategory.setName(simpleOrganizationCategory.getName());

        return organizationCategory;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
