package org.tdl.vireo.model;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class BaseOrderedEntity extends BaseEntity {

    @Column(nullable = true)
    protected Long position = null;

    public Long getPosition() {
        return position;
    }

    public void setPosition(Long position) {
        this.position = position;
    }
    
}
