package org.tdl.vireo.model.simple;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.hibernate.annotations.Immutable;

@Entity
@Immutable
@Table(name = "email_template")
public class SimpleEmailTemplate implements Serializable {

    @Transient
    private static final long serialVersionUID = -989693164688451518L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", insertable = false, updatable = false, nullable = false)
    private Long id;

    @Column(insertable = false, updatable = false, nullable = false)
    private String name;

    @Column(insertable = false, updatable = false, nullable = false)
    private String subject;

    @Column(insertable = false, updatable = false, nullable = false, columnDefinition = "text")
    private String message;

    @Column(insertable = false, updatable = false, nullable = false)
    private Boolean systemRequired;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

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
