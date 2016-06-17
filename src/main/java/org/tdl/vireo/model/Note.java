package org.tdl.vireo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;

import edu.tamu.framework.model.BaseEntity;

@Entity
public class Note extends AbstractNote<Note> {

    public Note() {}
    
    public Note(String name, String text) {
        setName(name);
        setText(text);
    }    
}
