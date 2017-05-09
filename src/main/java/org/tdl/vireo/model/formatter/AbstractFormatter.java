package org.tdl.vireo.model.formatter;

import javax.persistence.Entity;
import javax.persistence.Inheritance;

import edu.tamu.framework.model.BaseEntity;

@Entity
@Inheritance
public abstract class AbstractFormatter extends BaseEntity implements Formatter {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
