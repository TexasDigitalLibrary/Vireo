package org.tdl.vireo.model.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.Preference;

/**
 * Jpa specefic implementation of Vireo's Preference interface
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
@Entity
@Table(name = "preference",
		uniqueConstraints = { @UniqueConstraint( columnNames = { "person_id", "name" } ) } )
public class JpaPreferenceImpl extends JpaAbstractModel<JpaPreferenceImpl> implements Preference {

	@ManyToOne(targetEntity = JpaPersonImpl.class, optional=false)
	public Person person;

	@Column(nullable = false, length=255)
	public String name;

	@Column(length=32768) // 2^15
	public String value;

	/**
	 * Create a new JpaPreferenceImpl
	 * 
	 * @param person
	 *            The person who owns the preference.
	 * @param name
	 *            The name of the preference
	 * @param value
	 *            The value of the preference.
	 */
	protected JpaPreferenceImpl(Person person, String name, String value) {
		
		if (person == null)
			throw new IllegalArgumentException("All preferences must be associated with a person.");
		
		if (name == null || name.length() == 0)
			throw new IllegalArgumentException("Preference name's are required");
		
		assertAdministratorOrOwner(person);
		
		this.person = person;
		this.name = name;
		this.value = value;
	}

	@Override
	public JpaPreferenceImpl save() {
		assertAdministratorOrOwner(person);

		return super.save();
	}
	
	@Override
	public JpaPreferenceImpl delete() {
		
		assertAdministratorOrOwner(person);
		
		// Tell our owner we are being deleted.
		((JpaPersonImpl)person).removePreference(this);
		
		return super.delete();
	}

	@Override
	public Person getPerson() {
		return person;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		
		if (name == null || name.length() == 0)
			throw new IllegalArgumentException("Preference name's are required");
		
		assertAdministratorOrOwner(person);
		
		this.name = name;
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public void setValue(String value) {
		
		assertAdministratorOrOwner(person);
		
		this.value = value;
	}

}
