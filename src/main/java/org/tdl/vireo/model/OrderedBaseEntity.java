package org.tdl.vireo.model;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class OrderedBaseEntity extends BaseEntity {

    @Column(nullable = true)
    protected Integer order = null;

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }
    
}
