package org.tdl.vireo.model.jpa;

import java.util.HashMap;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.tdl.vireo.model.Department;

/**
 * Jpa specific implementation of Vireo's Department interface
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 * 
 */
@Entity
@Table(name = "department")
public class JpaDepartmentImpl extends JpaAbstractModel<JpaDepartmentImpl> implements Department {

	@Column(nullable = false)
	public int displayOrder;

	@Column(nullable = false, unique = true, length=255) 
	public String name;
	
	@Column
	public HashMap<Integer, String> emails = new HashMap<Integer, String>();

	/**
	 * Create a new JpaDepartmentImpl
	 * 
	 * @param name
	 *            The name of the new department.
	 */
	protected JpaDepartmentImpl(String name) {

		if (name == null || name.length() == 0)
			throw new IllegalArgumentException("Name is required");

		assertManager();
		
		this.displayOrder = 0;
		this.name = name;
	}
	
	protected JpaDepartmentImpl(String name, HashMap<Integer, String> emails) {
		this(name);
		this.setEmails(emails);
	}
	
	@Override
	public JpaDepartmentImpl save() {
		assertManager();

		return super.save();
	}
	
	@Override
	public JpaDepartmentImpl delete() {
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
		return this.name;
	}

	@Override
	public void setName(String name) {
		
		if (name == null || name.length() == 0)
			throw new IllegalArgumentException("Name is required");
		
		assertManager();
		
		this.name = name;
	}
	
	@Override
	public HashMap<Integer, String> getEmails() {
		return this.emails;
	}

	@Override
	public void setEmails(HashMap emails) {
		this.emails = emails;
	}

	@Override
	public void addEmail(String email) {
		Integer index = this.emails.size();
		this.emails.put(index, email);
	}

	@Override
	public void removeEmail(int index) {
		this.emails.remove(index);
	}
	
}
