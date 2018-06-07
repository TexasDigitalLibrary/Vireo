package org.tdl.vireo.model;

import javax.persistence.Embeddable;

@Embeddable
public class Validation {
    private String pattern;
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
