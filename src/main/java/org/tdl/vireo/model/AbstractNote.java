package org.tdl.vireo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import edu.tamu.framework.model.BaseEntity;

@Entity
@Inheritance
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "name", "originating_workflow_step_id", "overrideable" }))
public abstract class AbstractNote<N> extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, columnDefinition = "text")
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
