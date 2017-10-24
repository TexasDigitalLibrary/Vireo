package org.tdl.vireo.model;

import javax.persistence.Entity;
import javax.persistence.Inheritance;

import edu.tamu.weaver.validation.model.ValidatingBaseEntity;

@Entity
@Inheritance
public abstract class AbstractEmailRecipient extends ValidatingBaseEntity {

    private String name;
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
