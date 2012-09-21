package org.tdl.vireo.model.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.tdl.vireo.model.EmailTemplate;

/**
 * Jpa specific implementation of Vireo's Email Template interface.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
@Entity
@Table(name = "email_template")
public class JpaEmailTemplateImpl extends JpaAbstractModel<JpaEmailTemplateImpl> implements EmailTemplate {

	@Column(nullable = false)
	public int displayOrder;

	@Column(nullable = false, unique = true, length=255) 
	public String name;
	
	@Column(nullable = false, length=32768) // 2^15
	public String subject;

	@Column(nullable = false, length=32768) // 2^15
	public String message;
	
	@Column(nullable = false)
	public Boolean systemRequired;

	/**
	 * Create a new JpaEmailTemplateImpl
	 * 
	 * @param name
	 * 			  The new template's name.
	 * @param subject
	 *            The new template's subject.
	 * @param message
	 *            The new template's message
	 */
	protected JpaEmailTemplateImpl(String name, String subject, String message) {

		if (name == null || name.length() == 0)
			throw new IllegalArgumentException("Name is required");
		
		if (subject == null || subject.length() == 0)
			throw new IllegalArgumentException("Subject is required");
		
		if (message == null || message.length() == 0)
			throw new IllegalArgumentException("Message is required");
	    
		assertManager();
		
		this.systemRequired = false;
	    this.displayOrder = 0;
	    this.name = name;
		this.subject = subject;
		this.message = message;
	}

	@Override
	public JpaEmailTemplateImpl save() {
		assertManager();

		return super.save();
	}
	
	@Override
	public JpaEmailTemplateImpl delete() {
		assertManager();

		if (isSystemRequired())
			throw new IllegalStateException("Unable to delete the email template '"+name+"' because it is required by the system.");
		
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
		
		// Just to be nice so that if you're not changing it we won't do the system required check.
		if (name.equals(this.name))
			return;
		
		if (isSystemRequired())
			throw new IllegalStateException("Unable to rename the email template '"+name+"' because it is required by the system.");
		
		this.name = name;
	}

    
	@Override
	public String getSubject() {
		return subject;
	}

	@Override
	public void setSubject(String subject) {
		
		if (subject == null || subject.length() == 0)
			throw new IllegalArgumentException("Subject is required");
		
		assertManager();
		
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
		
		assertManager();
		
		this.message = message;
	}
	
	@Override
	public boolean isSystemRequired() {
		return systemRequired;
	}
	
	@Override
	public void setSystemRequired(boolean systemRequired) {
		assertAdministrator();
		
		this.systemRequired = systemRequired;
	}

}
