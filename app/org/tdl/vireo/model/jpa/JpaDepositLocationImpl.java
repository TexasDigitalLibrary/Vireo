package org.tdl.vireo.model.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.tdl.vireo.export.Depositor;
import org.tdl.vireo.export.Packager;
import org.tdl.vireo.model.DepositLocation;

import play.Logger;
import play.modules.spring.Spring;

/**
 * JPA specific implementation of the Deposit Location interface.
 * 
 * This class will store all URL datatypes as strings in the database, and then
 * re-parse them when requested. Also in a similar manner when packagers and
 * depositors will be stored based upon their spring bean names. If those names
 * change then problems will occure.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
@Entity
@Table(name = "deposit_location")
public class JpaDepositLocationImpl extends JpaAbstractModel<JpaDepositLocationImpl> implements DepositLocation {

	@Column(nullable = false)
	public int displayOrder;

	@Column(nullable = false, unique = true, length=255)
	public String name;
	
	@Column(length=1024)
	public String repository;
	
	@Column(length=1024)
	public String collection;
	
	@Column(length=255)
	public String username;
	
	@Column(length=255)
	public String password;
	
	@Column(length=255)
	public String onBehalfOf;

	@Column(length=255)
	public String packager;
	
	@Column(length=255)
	public String depositor;
	
	@Column(columnDefinition="INTEGER DEFAULT '60'")
	public Integer timeout;
	
	/**
	 * Construct a new JpaDepositLocation
	 * 
	 * @param name
	 *            The name of the new deposit location.
	 */
	protected JpaDepositLocationImpl(String name) {

		if (name == null || name.length() == 0)
			throw new IllegalArgumentException("Name is required");

		assertManager();
		
		this.displayOrder = 0;
		this.name = name;
		this.timeout = DEFAULT_TIMEOUT;
	}
	
	@Override
	public JpaDepositLocationImpl save() {
		assertManager();

		return super.save();
	}
	
	@Override
	public JpaDepositLocationImpl delete() {
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
	public String getRepository() {
		
		return repository;
	}

	@Override
	public void setRepository(String repository) {
		
		assertManager();
		
		this.repository = repository;
	}

	@Override
	public String getCollection() {
		return collection;
		
	}

	@Override
	public void setCollection(String collection) {

		assertManager();
		
		this.collection = collection;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public void setUsername(String username) {
		assertManager();
		
		this.username = username;
	}

	@Override
	public String getPassword() {
		
		return password;
	}

	@Override
	public void setPassword(String password) {
		assertManager();
		
		this.password = password;
	}

	@Override
	public String getOnBehalfOf() {
		return onBehalfOf;
	}

	@Override
	public void setOnBehalfOf(String onBehalfOf) {
		assertManager();
		
		this.onBehalfOf = onBehalfOf;
	}

	@Override
	public Packager getPackager() {
		
		if (packager == null)
			return null;
		
		try {
			Object bean = Spring.getBean(packager);
			return (Packager) bean;
		} catch (RuntimeException re) {
			Logger.warn(re,"Unable to packager for deposit location "+id+" because of an exception");
			return null;
		}
		
	}

	@Override
	public void setPackager(Packager packager) {
		assertManager();
		
		if (packager == null) 
			this.packager = null;
		else 
			this.packager = packager.getBeanName();
	}

	@Override
	public Depositor getDepositor() {
		
		if (depositor == null)
			return null;
		
		try {
			Object bean = Spring.getBean(depositor);
			return (Depositor) bean;
		} catch (RuntimeException re) {
			Logger.warn(re,"Unable to depositor for deposit location "+id+" because of an exception");
			return null;
		}
		
	}

	@Override
	public void setDepositor(Depositor depositor) {
		assertManager();
		
		if (depositor == null) 
			this.depositor = null;
		else 
			this.depositor = depositor.getBeanName();
	}

	@Override
    public void setTimeout(Integer seconds) {
		assertManager();
		
		if(seconds == null) {
			this.timeout = DEFAULT_TIMEOUT;
		} else {
			this.timeout = seconds;
		}
    }

	@Override
    public Integer getTimeout() {
	    return (this.timeout == null ? DEFAULT_TIMEOUT : this.timeout);
    }	
}
