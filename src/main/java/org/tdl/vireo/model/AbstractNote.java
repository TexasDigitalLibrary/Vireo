package org.tdl.vireo.model;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import edu.tamu.weaver.validation.model.ValidatingBaseEntity;

@Entity
@Inheritance
@DiscriminatorColumn(name = "N_TYPE")
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "originating_workflow_step_id", "name", "n_type", "overrideable" }))
public abstract class AbstractNote<N> extends ValidatingBaseEntity {

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
