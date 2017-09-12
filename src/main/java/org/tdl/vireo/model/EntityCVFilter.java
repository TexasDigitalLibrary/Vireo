package org.tdl.vireo.model;

import javax.persistence.Embeddable;

@Embeddable
public class EntityCVFilter {

    private String path;

    private String value;

    public EntityCVFilter() {

    }

    public EntityCVFilter(String path, String value) {
        this();
        setPath(path);
        setValue(value);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
