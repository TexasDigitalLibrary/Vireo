package org.tdl.vireo.model.formatter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;

import edu.tamu.framework.model.BaseEntity;

@Entity
@Inheritance
public abstract class AbstractFormatter extends BaseEntity implements Formatter {

    @Column(unique = true)
    private String name;

    @Column(unique = true)
    private String template;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

}
