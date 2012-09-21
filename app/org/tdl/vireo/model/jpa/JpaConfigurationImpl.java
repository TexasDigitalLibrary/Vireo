package org.tdl.vireo.model.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.tdl.vireo.model.Configuration;

/**
 * Jpa specific implementation of Vireo's Configuration interface
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
@Entity
@Table(name = "configuration")
public class JpaConfigurationImpl extends JpaAbstractModel<JpaConfigurationImpl> implements Configuration {

	@Column(nullable = false, unique = true, length=255)
	public String name;

	@Column(length=32768) // 2^15
	public String value;

	/**
	 * Construct a new JpaConfigurationImpl
	 * 
	 * @param name
	 *            The name of the configuration parameter.
	 * @param value
	 *            The value of the configuration parameter.
	 */
	protected JpaConfigurationImpl(String name, String value) {
	
		if (name == null || name.length() == 0)
			throw new IllegalArgumentException("Name is required");

		assertManager();
		
		this.name = name;
		this.value = value;
	}
	
	@Override
	public JpaConfigurationImpl save() {
		assertManager();

		return super.save();
	}
	
	@Override
	public JpaConfigurationImpl delete() {
		assertManager();

		return super.delete();
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
	public String getValue() {
		return this.value;
	}

	@Override
	public void setValue(String value) {
		
		assertManager();
		this.value = value;
	}

}
