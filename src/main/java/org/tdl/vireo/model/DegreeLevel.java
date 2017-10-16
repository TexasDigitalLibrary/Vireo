package org.tdl.vireo.model;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.tdl.vireo.model.validation.DegreeLevelValidator;

import edu.tamu.weaver.validation.model.ValidatingOrderedBaseEntity;

@Entity
public class DegreeLevel extends ValidatingOrderedBaseEntity {

    @Column(nullable = false, unique = true)
    private String name;

    public DegreeLevel() {
        setModelValidator(new DegreeLevelValidator());
    }

    public DegreeLevel(String name) {
        this();
        setName(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
