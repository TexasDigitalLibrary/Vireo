package org.tdl.vireo.model.packager;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.ManyToOne;

import org.tdl.vireo.model.formatter.AbstractFormatter;

import edu.tamu.framework.model.BaseEntity;

@Entity
@Inheritance
public abstract class AbstractPackager extends BaseEntity implements Packager {

    @ManyToOne(fetch = FetchType.EAGER, optional = false, cascade = CascadeType.ALL)
    public AbstractFormatter formatter;

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AbstractFormatter getFormatter() {
        return formatter;
    }

    public void setFormatter(AbstractFormatter formatter) {
        this.formatter = formatter;
    }

}
