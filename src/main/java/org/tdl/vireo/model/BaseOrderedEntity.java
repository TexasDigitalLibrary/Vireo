package org.tdl.vireo.model;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class BaseOrderedEntity extends BaseEntity {

    @Column(name = "`order`", nullable = true) // "order" is a keyword in SQL
    protected Integer order = null;

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }
    
}
