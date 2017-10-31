package org.tdl.vireo.model;

import java.util.regex.Pattern;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Transient;

import org.tdl.vireo.model.validation.FieldPredicateValidator;

import com.fasterxml.jackson.annotation.JsonIgnore;

import edu.tamu.weaver.validation.model.ValidatingBaseEntity;

@Entity
public class FieldPredicate extends ValidatingBaseEntity {

    @Transient
    private static String period = Pattern.quote(".");

    @Column(nullable = false, unique = true)
    private String value;
    
	@Column(nullable = false, unique = false)
    private Boolean documentTypePredicate;

    public FieldPredicate() {
        setModelValidator(new FieldPredicateValidator());
    }

    /**
     *
     * @param value
     */
    public FieldPredicate(String value, Boolean documentTypePredicate) {
        this();
        setValue(value);
        setDocumentTypePredicate(documentTypePredicate);
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
     * @return documentTypePredicate
     */
    public Boolean getDocumentTypePredicate() {
        return documentTypePredicate;
    }

    /**
     *
     * @param documentTypePredicate
     */
    public void setDocumentTypePredicate(Boolean documentTypePredicate) {
        this.documentTypePredicate = documentTypePredicate;
    }

    @JsonIgnore
    public String getSchema() {
        String schema = null;
        String[] fieldLabel = value.split(period);
        if (fieldLabel.length >= 1) {
            schema = fieldLabel[0];
        }
        return schema;
    }

    @JsonIgnore
    public String getElement() {
        String schema = null;
        String[] fieldLabel = value.split(period);
        if (fieldLabel.length >= 2) {
            schema = fieldLabel[1];
        }
        return schema;
    }

    @JsonIgnore
    public String getQualifier() {
        String schema = null;
        String[] fieldLabel = value.split(period);
        if (fieldLabel.length >= 3) {
            schema = fieldLabel[2];
        }
        return schema;
    }

}
