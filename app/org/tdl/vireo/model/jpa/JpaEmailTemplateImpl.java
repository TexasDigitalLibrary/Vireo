package org.tdl.vireo.model.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.tdl.vireo.model.AbstractModel;
import org.tdl.vireo.model.EmailTemplate;

import play.db.jpa.Model;

/**
 * Jpa specific implementation of Vireo's Email Template interface.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
@Entity
@Table(name = "EmailTemplate")
public class JpaEmailTemplateImpl extends Model implements EmailTemplate {

	@Column(nullable = false)
	public int displayOrder;

	@Column(nullable = false, unique = true)
	public String subject;

	@Column(nullable = false)
	public String message;

	/**
	 * Create a new JpaEmailTemplateImpl
	 * 
	 * @param subject
	 *            The new template's subject.
	 * @param message
	 *            The new template's message
	 */
	protected JpaEmailTemplateImpl(String subject, String message) {
		// TODO: check arguments
	    
	    this.displayOrder = 0;
		this.subject = subject;
		this.message = message;
	}

	@Override
	public JpaEmailTemplateImpl save() {
		return super.save();
	}

	@Override
	public JpaEmailTemplateImpl delete() {
		return super.delete();
	}

	@Override
	public JpaEmailTemplateImpl refresh() {
		return super.refresh();
	}

	@Override
	public JpaEmailTemplateImpl merge() {
		return super.merge();
	}

    @Override
    public int getDisplayOrder() {
        return displayOrder;
    }

    @Override
    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

	@Override
	public String getSubject() {
		return subject;
	}

	@Override
	public void setSubject(String subject) {
		
		// TODO: check subject
		
		this.subject = subject;
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public void getMessage(String message) {
		
		// TODO: check message
		
		this.message = message;
	}

}
