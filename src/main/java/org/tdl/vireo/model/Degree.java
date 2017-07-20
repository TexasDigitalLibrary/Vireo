package org.tdl.vireo.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.tdl.vireo.model.validation.DegreeValidator;

import edu.tamu.framework.model.BaseOrderedEntity;

@Entity
public class Degree extends BaseOrderedEntity implements EntityControlledVocabulary {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String proquestCode;

    public Degree() {
        setModelValidator(new DegreeValidator());
    }

    public Degree(String name, String proquestCode) {
        this();
        setName(name);
        setProquestCode(proquestCode);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
