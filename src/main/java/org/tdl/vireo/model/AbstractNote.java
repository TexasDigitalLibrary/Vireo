package org.tdl.vireo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import edu.tamu.framework.model.BaseEntity;

@Entity
@Inheritance
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "name", "originating_workflow_step_id" }) )
public abstract class AbstractNote<N> extends BaseEntity {

    @Column(nullable = false)
    private String name;
    
    @Lob
    @Column(nullable = false)
    private String text;
    
        
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }    
}
