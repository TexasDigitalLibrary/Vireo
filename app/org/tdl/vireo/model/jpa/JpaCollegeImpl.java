package org.tdl.vireo.model.jpa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.tdl.vireo.model.College;

/**
 * Jpa specific implementation of Vireo's College interface.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
@Entity
@Table(name = "college")
public class JpaCollegeImpl extends JpaAbstractModel<JpaCollegeImpl> implements College {

	@Column(nullable = false)
	public int displayOrder;

	@Column(nullable = false, unique = true, length=255)
	public String name;
	
	@Column
	public HashMap<Integer, String> emails = new HashMap<Integer, String>();
		
	/**
	 * Construct a new JpaCollegeImpl
	 * 
	 * @param name
	 *            The name of the new college.
	 */
	protected JpaCollegeImpl(String name) {

		if (name == null || name.length() == 0)
			throw new IllegalArgumentException("Name is required");

		assertManager();
		
		this.displayOrder = 0;
		this.name = name;
	}
	
	protected JpaCollegeImpl(String name, HashMap<Integer, String> emails) {
		this(name);
		this.setEmails(emails);
	}

	
	@Override
	public JpaCollegeImpl save() {
		assertManager();

		return super.save();
	}
	
	@Override
	public JpaCollegeImpl delete() {
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
