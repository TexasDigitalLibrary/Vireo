package org.tdl.vireo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.tdl.vireo.model.validation.FieldGlossValidator;

import edu.tamu.weaver.validation.model.ValidatingBaseEntity;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "value", "language_id" }))
public class FieldGloss extends ValidatingBaseEntity {

    @Column(nullable = false)
    private String value;

    @ManyToOne(optional = false)
    private Language language;

    public FieldGloss() {
        setModelValidator(new FieldGlossValidator());
    }

    /**
     *
     * @param value
     * @param language
     */
    public FieldGloss(String value, Language language) {
        this();
        setValue(value);
        setLanguage(language);
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value
     *            the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     *
     * @return the language
     */
    public Language getLanguage() {
        return language;
    }

    /**
     *
     * @param language
     */
    public void setLanguage(Language language) {
        this.language = language;
    }
}
