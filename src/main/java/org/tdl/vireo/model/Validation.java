package org.tdl.vireo.model;

import javax.persistence.Embeddable;

import org.tdl.vireo.model.response.Views;

import com.fasterxml.jackson.annotation.JsonView;

@Embeddable
public class Validation {
    @JsonView(Views.SubmissionIndividual.class)
    private String pattern;
    @JsonView(Views.SubmissionIndividual.class)
    private String message;

    public Validation() {
    }

    public Validation(String pattern, String message) {
        this();
        this.pattern = pattern;
        this.message = message;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
