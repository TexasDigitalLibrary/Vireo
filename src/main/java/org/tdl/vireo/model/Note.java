package org.tdl.vireo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;

import edu.tamu.framework.model.BaseEntity;

@Entity
public class Note extends BaseEntity {

    @Column(nullable = false)
    private String name;
    
    @Lob
    @Column(nullable = false)
    private String text;
    
    public Note() {}
    
    public Note(String name, String text) {
        setName(name);
        setText(text);
    }

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
