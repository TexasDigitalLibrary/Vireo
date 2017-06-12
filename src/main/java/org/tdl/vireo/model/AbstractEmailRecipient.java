package org.tdl.vireo.model;

import javax.persistence.Entity;
import javax.persistence.Inheritance;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import edu.tamu.framework.model.BaseEntity;

@Entity
@Inheritance
public abstract class AbstractEmailRecipient extends BaseEntity {

    private String name;
    
    private String type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

}
