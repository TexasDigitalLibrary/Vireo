package org.tdl.vireo.model;

import javax.persistence.Column;
import javax.persistence.Entity;

import edu.tamu.framework.model.BaseEntity;

@Entity
public class AttachmentType extends BaseEntity {

    @Column(unique = true)
    private String name;
    
    public AttachmentType() {
        setName("PRIMARY");
    }
    
    public AttachmentType(String name) {
        setName(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
}
