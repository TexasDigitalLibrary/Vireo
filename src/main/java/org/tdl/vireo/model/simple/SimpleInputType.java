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
@Table(name = "input_type")
public class SimpleInputType implements Serializable {

    @Transient
    private static final long serialVersionUID = 8703635502639470746L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", insertable = false, updatable = false, nullable = false)
    private Long id;

    @Column(insertable = false, updatable = false, unique = true, nullable = false)
    private String name;

    @Column(insertable = false, updatable = false)
    private String validationPattern;

    @Column(insertable = false, updatable = false)
    private String validationMessage;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValidationPattern() {
        return validationPattern;
    }

    public void setValidationPattern(String validationPattern) {
        this.validationPattern = validationPattern;
    }

    public String getValidationMessage() {
        return validationMessage;
    }

    public void setValidationMessage(String validationMessage) {
        this.validationMessage = validationMessage;
    }

}
