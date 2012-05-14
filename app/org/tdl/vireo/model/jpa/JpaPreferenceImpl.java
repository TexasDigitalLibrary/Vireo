package org.tdl.vireo.model.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

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
@Table(name = "Preference")
public class JpaPreferenceImpl extends Model implements Preference {

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
		// TODO: check arguments

		this.person = person;
		this.name = name;
		this.value = value;
	}

	@Override
	public JpaPreferenceImpl save() {
		return super.save();
	}

	@Override
	public JpaPreferenceImpl delete() {
		
		// TODO: callback to the owner at tell them the preference has been deleted.
		
		return super.delete();
	}

	@Override
	public JpaPreferenceImpl refresh() {
		return super.refresh();
	}

	@Override
	public JpaPreferenceImpl merge() {
		return super.merge();
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
		
		// TODO: check that name is not null or empty
		
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
