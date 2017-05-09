package org.tdl.vireo.model;

import javax.persistence.Entity;
import javax.persistence.Inheritance;

import edu.tamu.framework.model.BaseEntity;

@Entity
@Inheritance
public class AbstractEmailRecipient extends BaseEntity {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
