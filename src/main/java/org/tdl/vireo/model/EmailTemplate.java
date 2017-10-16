package org.tdl.vireo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.tdl.vireo.model.validation.EmailTemplateValidator;

import edu.tamu.weaver.validation.model.ValidatingOrderedBaseEntity;

@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "name", "systemRequired" }) })
public class EmailTemplate extends ValidatingOrderedBaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false, columnDefinition = "text")
    private String message;

    @Column(nullable = false)
    private Boolean systemRequired;

    public EmailTemplate() {
        setModelValidator(new EmailTemplateValidator());
        setSystemRequired(false);
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

    public Boolean getSystemRequired() {
        return systemRequired;
    }

    public void setSystemRequired(Boolean systemRequired) {
        this.systemRequired = systemRequired;
    }

}
