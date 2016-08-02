package org.tdl.vireo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.tdl.vireo.enums.DegreeLevel;
import org.tdl.vireo.model.validation.DocumentTypeValidator;

import edu.tamu.framework.model.BaseOrderedEntity;

@Entity
@Table(uniqueConstraints = { @UniqueConstraint( columnNames = { "name", "degreeLevel" } ) } )
public class DocumentType extends BaseOrderedEntity {

	@Column(nullable = false, length = 255)
	private String name;

	@Column(nullable = false)
	private DegreeLevel degreeLevel;

	@OneToOne(cascade = javax.persistence.CascadeType.DETACH, orphanRemoval = true, optional = false)
	private FieldPredicate fieldPredicate;

	public DocumentType() {
	    setModelValidator(new DocumentTypeValidator());
	}

	/**
	 * Create a new JpaDocumentTypeImpl
	 * 
	 * @param name
	 *            The name of the new document type.
	 * @param degreeLevel
	 *            The degreeLevel of the new document type.
	 */
	public DocumentType(String name, DegreeLevel degreeLevel) {
	    this();
		setName(name);
		setDegreeLevel(degreeLevel);
	}

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the degreeLevel
     */
    public DegreeLevel getDegreeLevel() {
        return degreeLevel;
    }

    /**
     * @param degreeLevel the level to set
     */
    public void setDegreeLevel(DegreeLevel degreeLevel) {
        this.degreeLevel = degreeLevel;
    }
    
    /**
     * @return the fieldPredicate
     */
    public FieldPredicate getFieldPredicate() {
        return fieldPredicate;
    }
    
    /**
     * @param fieldPredicate the predicate to set
     */
    public void setFieldPredicate(FieldPredicate fieldPredicate) {
        this.fieldPredicate = fieldPredicate;
    }
}
