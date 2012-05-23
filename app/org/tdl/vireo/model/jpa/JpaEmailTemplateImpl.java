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
public class JpaEmailTemplateImpl extends JpaAbstractModel<JpaEmailTemplateImpl> implements EmailTemplate {

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
		
		if (subject == null || subject.length() == 0)
			throw new IllegalArgumentException("Subject is required");
		
		if (message == null || message.length() == 0)
			throw new IllegalArgumentException("Message is required");
	    
	    this.displayOrder = 0;
		this.subject = subject;
		this.message = message;
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
		
		if (subject == null || subject.length() == 0)
			throw new IllegalArgumentException("Subject is required");
		
		this.subject = subject;
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public void setMessage(String message) {
		
		if (message == null || message.length() == 0)
			throw new IllegalArgumentException("Message is required");
		
		this.message = message;
	}

}
