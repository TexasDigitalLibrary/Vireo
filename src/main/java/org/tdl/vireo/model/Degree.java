package org.tdl.vireo.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.tdl.vireo.model.validation.DegreeValidator;

import edu.tamu.framework.model.BaseOrderedEntity;

@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "name", "level_id" }) })
public class Degree extends BaseOrderedEntity implements EntityControlledVocabulary {

    @Column(nullable = false)
    private String name;

    @Column(nullable = true)
    private String proquestCode;

    @ManyToOne(optional = false)
    private DegreeLevel level;

    public Degree() {
        setModelValidator(new DegreeValidator());
    }

    public Degree(String name, DegreeLevel level) {
        this();
        setName(name);
        setLevel(level);
    }

    public Degree(String name, DegreeLevel level, String proquestCode) {
        this(name, level);
        setProquestCode(proquestCode);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DegreeLevel getLevel() {
        return level;
    }

    public void setLevel(DegreeLevel level) {
        this.level = level;
    }

    public String getProquestCode() {
        return proquestCode;
    }

    public void setProquestCode(String proquestCode) {
        this.proquestCode = proquestCode;
    }

    @Override
    public String getControlledName() {
        return name;
    }

    @Override
    public String getControlledDefinition() {
        return proquestCode;
    }

    @Override
    public String getControlledIdentifier() {
        return "";
    }

    @Override
    public List<String> getControlledContacts() {
        return new ArrayList<String>();
    }

}
