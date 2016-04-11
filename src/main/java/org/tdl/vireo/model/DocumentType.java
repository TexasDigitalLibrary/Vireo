package org.tdl.vireo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;
import org.tdl.vireo.enums.DegreeLevel;

/**
 * 
 * @author gad
 */
@Entity
@Table(uniqueConstraints = { @UniqueConstraint( columnNames = { "name", "degreeLevel" } ) } )
public class DocumentType extends BaseOrderedEntity {

	@Column(nullable = false, length=255)
	@NotEmpty
	@Size(max=255)
	private String name;

	@Column(nullable = false)
	@NotNull
	private DegreeLevel degreeLevel;

	public DocumentType() {}

	/**
	 * Create a new JpaDocumentTypeImpl
	 * 
	 * @param name
	 *            The name of the new document type.
	 * @param degreeLevel
	 *            The degreeLevel of the new document type.
	 */
	public DocumentType(String name, DegreeLevel degreeLevel) {
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
    
}
