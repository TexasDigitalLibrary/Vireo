package org.tdl.vireo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.tdl.vireo.model.validation.EmailTemplateValidator;

import com.fasterxml.jackson.annotation.JsonProperty;

import edu.tamu.framework.model.BaseOrderedEntity;

@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "name", "isSystemRequired" }) })
public class EmailTemplate extends BaseOrderedEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false, columnDefinition = "text")
    private String message;

    @Column(nullable = false)
    @JsonProperty("isSystemRequired")
    private Boolean isSystemRequired;

    public EmailTemplate() {
        setModelValidator(new EmailTemplateValidator());
        isSystemRequired(false);
    }

    /**
     * Create a new EmailTemplate
     *
     * @param name
     *            The new template's name.
     * @param subject
     *            The new template's subject.
     * @param message
     *            The new template's message
     */
    public EmailTemplate(String name, String subject, String message) {
        this();
        setName(name);
        setSubject(subject);
        setMessage(message);
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * @param subject
     *            the subject to set
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message
     *            the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return the systemRequired
     */
    public Boolean isSystemRequired() {
        return isSystemRequired;
    }

    /**
     * @param systemRequired
     *            the systemRequired to set
     */
    public void isSystemRequired(Boolean isSystemRequired) {
        this.isSystemRequired = isSystemRequired;
    }

}
