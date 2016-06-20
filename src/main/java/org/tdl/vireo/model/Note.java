package org.tdl.vireo.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("Org")
public class Note extends AbstractNote<Note> {

    public Note() {}
    
    public Note(String name, String text) {
        setName(name);
        setText(text);
    }    
}
