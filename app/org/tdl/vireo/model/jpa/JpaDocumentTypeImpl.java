package org.tdl.vireo.model.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.tdl.vireo.model.AbstractModel;
import org.tdl.vireo.model.DegreeLevel;
import org.tdl.vireo.model.DocumentType;

import play.data.validation.Required;
import play.db.jpa.Model;

/**
 * Jpa specefic implementation of Vireo's Document Type interface
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
@Entity
@Table(name = "DocumentType")
public class JpaDocumentTypeImpl extends Model implements DocumentType {

	@Column(nullable = false)
	public int order;

	@Column(nullable = false)
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

		// TODO: Check the arguments;
		this.order = 0;
		this.name = name;
		this.level = level;
	}

	@Override
	public JpaDocumentTypeImpl save() {
		return super.save();
	}

	@Override
	public JpaDocumentTypeImpl delete() {
		return super.delete();
	}

	@Override
	public JpaDocumentTypeImpl refresh() {
		return super.refresh();
	}

	@Override
	public JpaDocumentTypeImpl merge() {
		return super.merge();
	}

	@Override
	public int getOrder() {
		return order;
	}

	@Override
	public void setOrder(int order) {
		this.order = order;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		
		// TODO: check name
		
		this.name = name;
	}

	@Override
	public DegreeLevel getLevel() {
		return level;
	}

	@Override
	public void setLevel(DegreeLevel level) {
		
		// TODO: check level
		
		this.level = level;
	}

}
