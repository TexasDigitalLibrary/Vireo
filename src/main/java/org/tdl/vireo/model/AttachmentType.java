package org.tdl.vireo.model;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.tdl.vireo.model.validation.AttachmentTypeValidator;

import edu.tamu.framework.model.BaseEntity;

@Entity
public class AttachmentType extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;
    
    public AttachmentType() {
        setModelValidator(new AttachmentTypeValidator());
        setName("PRIMARY");
    }
    
    public AttachmentType(String name) {
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
