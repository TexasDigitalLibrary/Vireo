package org.tdl.vireo.model.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.tdl.vireo.model.DegreeLevel;
import org.tdl.vireo.model.DocumentType;

/**
 * Jpa specefic implementation of Vireo's Document Type interface
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
@Entity
@Table(name = "document_type",
       uniqueConstraints = { @UniqueConstraint( columnNames = { "name", "level" } ) } )
public class JpaDocumentTypeImpl extends JpaAbstractModel<JpaDocumentTypeImpl> implements DocumentType {

	@Column(nullable = false)
	public int displayOrder;

	@Column(nullable = false, length=255) 
	public String name;

	@Column(nullable = false)
	public DegreeLevel level;

	/**
	 * Create a new JpaDocumentTypeImpl
	 * 
	 * @param name
	 *            The name of the new document type.
	 * @param level
	 *            The level of the new document type.
	 */
	protected JpaDocumentTypeImpl(String name, DegreeLevel level) {

		if (name == null || name.length() == 0)
			throw new IllegalArgumentException("Name is required");
		
		if (level == null)
			throw new IllegalArgumentException("Degree level is required");
		
		assertManager();
		
	    this.displayOrder = 0;
		this.name = name;
		this.level = level;
	}

	@Override
	public JpaDocumentTypeImpl save() {
		assertManager();

		return super.save();
	}
	
	@Override
	public JpaDocumentTypeImpl delete() {
		assertManager();

		return super.delete();
	}
	
    @Override
    public int getDisplayOrder() {
        return displayOrder;
    }

    @Override
    public void setDisplayOrder(int displayOrder) {
    	
    	assertManager();
        this.displayOrder = displayOrder;
    }

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		
		if (name == null || name.length() == 0)
			throw new IllegalArgumentException("Name is required");
		
		assertManager();
		
		this.name = name;
	}

	@Override
	public DegreeLevel getLevel() {
		return level;
	}

	@Override
	public void setLevel(DegreeLevel level) {
		
		if (level == null)
			throw new IllegalArgumentException("Degree level is required");
		
		assertManager();
		
		this.level = level;
	}

}
