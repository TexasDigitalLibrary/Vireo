package org.tdl.vireo.model;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.tdl.vireo.model.validation.FieldPredicateValidator;

import edu.tamu.framework.model.BaseEntity;

@Entity
public class FieldPredicate extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String value;
    
    @Column(nullable = true, unique = false) //TODO nullable false
    private Boolean documentTypePredicate;

	public FieldPredicate() { 
        setModelValidator(new FieldPredicateValidator());
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
	
    /**
     * 
     * @param value
     */
    public FieldPredicate(String value) {
        this();
        setValue(value);
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

}
