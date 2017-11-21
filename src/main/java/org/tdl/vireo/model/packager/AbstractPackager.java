package org.tdl.vireo.model.packager;

import java.nio.file.Path;
import java.nio.file.Paths;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.ManyToOne;

import org.tdl.vireo.model.formatter.AbstractFormatter;
import org.tdl.vireo.utility.FileHelperUtility;

import edu.tamu.weaver.data.model.BaseEntity;

@Entity
@Inheritance
public abstract class AbstractPackager extends BaseEntity implements Packager {

    @ManyToOne(fetch = FetchType.EAGER, optional = false, cascade = CascadeType.ALL)
    public AbstractFormatter formatter;

    @Column(unique = true)
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
    
    protected Path getAbsolutePath(String relativePath) {
        return Paths.get(FileHelperUtility.getPath(relativePath));
    }

}
