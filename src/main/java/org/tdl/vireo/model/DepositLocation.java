package org.tdl.vireo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

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
public class DepositLocation extends BaseEntity {
    
    @Transient
    public static final Integer DEFAULT_TIMEOUT = 60;

	@Column(nullable = false)
	private int displayOrder;

	@Column(nullable = false, unique = true, length=255)
	private String name;
	
	@Column(length=1024)
	private String repository;
	
	@Column(length=1024)
	private String collection;
	
	@Column(length=255)
	private String username;
	
	@Column(length=255)
	private String password;
	
	@Column(length=255)
	private String onBehalfOf;

	@Column(length=255)
	private String packager;
	
	@Column(length=255)
	private String depositor;
	
	@Column(columnDefinition="INTEGER DEFAULT '60'")
	private Integer timeout;
	
	public DepositLocation() {
	    this.timeout = DEFAULT_TIMEOUT;
    }
	
	/**
	 * Construct a new DepositLocation
	 * 
	 * @param name
	 *            The name of the new deposit location.
	 */
	public DepositLocation(String name) {
	    this();
		this.name = name;
	}

    /**
     * @return the displayOrder
     */
    public int getDisplayOrder() {
        return displayOrder;
    }

    /**
     * @param displayOrder the displayOrder to set
     */
    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the repository
     */
    public String getRepository() {
        return repository;
    }

    /**
     * @param repository the repository to set
     */
    public void setRepository(String repository) {
        this.repository = repository;
    }

    /**
     * @return the collection
     */
    public String getCollection() {
        return collection;
    }

    /**
     * @param collection the collection to set
     */
    public void setCollection(String collection) {
        this.collection = collection;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the onBehalfOf
     */
    public String getOnBehalfOf() {
        return onBehalfOf;
    }

    /**
     * @param onBehalfOf the onBehalfOf to set
     */
    public void setOnBehalfOf(String onBehalfOf) {
        this.onBehalfOf = onBehalfOf;
    }

    /**
     * @return the packager
     */
    public String getPackager() {
        return packager;
    }

    /**
     * @param packager the packager to set
     */
    public void setPackager(String packager) {
        this.packager = packager;
    }

    /**
     * @return the depositor
     */
    public String getDepositor() {
        return depositor;
    }

    /**
     * @param depositor the depositor to set
     */
    public void setDepositor(String depositor) {
        this.depositor = depositor;
    }

    /**
     * @return the timeout
     */
    public Integer getTimeout() {
        return timeout;
    }

    /**
     * @param timeout the timeout to set
     */
    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }	
}
