package org.tdl.vireo.model.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.tdl.vireo.model.AbstractModel;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.Preference;

import play.db.jpa.Model;

/**
 * Jpa specefic implementation of Vireo's Preference interface
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
@Entity
@Table(name = "Preference",
		uniqueConstraints = { @UniqueConstraint( columnNames = { "person_id", "name" } ) } )
public class JpaPreferenceImpl extends JpaAbstractModel<JpaPreferenceImpl> implements Preference {

	@ManyToOne(targetEntity = JpaPersonImpl.class, optional=false)
	public Person person;

	@Column(nullable = false)
	public String name;

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
		
		this.person = person;
		this.name = name;
		this.value = value;
	}

	@Override
	public JpaPreferenceImpl delete() {
		
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
		
		this.name = name;
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public void setValue(String value) {
		this.value = value;
	}

}
