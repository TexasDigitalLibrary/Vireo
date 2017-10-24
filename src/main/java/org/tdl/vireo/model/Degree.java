package org.tdl.vireo.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.tdl.vireo.model.validation.DegreeValidator;

import com.fasterxml.jackson.annotation.JsonIgnore;

import edu.tamu.weaver.validation.model.ValidatingOrderedBaseEntity;

@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "name", "level_id" }) })
public class Degree extends ValidatingOrderedBaseEntity implements EntityControlledVocabulary {

    @Column(nullable = false)
    private String name;

    @Column(nullable = true)
    private String degreeCode;

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
        setDegreeCode(degreeCode);
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

    public String getDegreeCode() {
        return degreeCode;
    }

    public void setDegreeCode(String degreeCode) {
        this.degreeCode = degreeCode;
    }

    @Override
    @JsonIgnore
    public String getControlledName() {
        return name;
    }

    @Override
    @JsonIgnore
    public String getControlledDefinition() {
        return degreeCode;
    }

    @Override
    @JsonIgnore
    public String getControlledIdentifier() {
        return level.getName();
    }

    @Override
    @JsonIgnore
    public List<String> getControlledContacts() {
        return new ArrayList<String>();
    }

}
