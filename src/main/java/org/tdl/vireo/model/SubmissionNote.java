package org.tdl.vireo.model;

import javax.persistence.Entity;

import org.tdl.vireo.model.validation.SubmissionNoteValidator;

@Entity
public class SubmissionNote extends AbstractNote<SubmissionNote> {
    
    public SubmissionNote() {
        setModelValidator(new SubmissionNoteValidator());
    }
    
    public SubmissionNote(String name, String text) {
        this();
        setName(name);
        setText(text);
    }    
    
}
