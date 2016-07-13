package org.tdl.vireo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Transient;

import org.tdl.vireo.model.validation.DepositLocationValidator;

@Entity
public class DepositLocation extends BaseOrderedEntity {
    
    @Transient
    public static final Integer DEFAULT_TIMEOUT = 60;

	@Column(nullable = false, unique = true)
	private String name;
	
	// TODO: this is really a URL
	@Column(nullable = false)
	private String repository;
	
	@Column(nullable = false)
	private String collection;
	
	@Column(nullable = false)
	private String username;
	
	// TODO: encrypt!!!
	@Column(nullable = false)
	private String password;
	
	@Column(nullable = true)
	private String onBehalfOf;

	// TODO, this used to be a Bean name in Vireo 3. (Deposit Format -- DSPace METS)
	@Column(nullable = false)
	private String packager;
	
	// TODO, this used to be a Bean name in Vireo 3. (Deposit Protocol -- SWORDv1)
	@Column(nullable = false)
	private String depositor;
	
	@Column(nullable = false)
	private Integer timeout;
	
	public DepositLocation() {
	    setModelValidator(new DepositLocationValidator());
	    setTimeout(DEFAULT_TIMEOUT);
    }
	
	/**
	 * Construct a new DepositLocation
	 * 
	 * @param name
	 *            The name of the new deposit location.
	 */
	public DepositLocation(String name, String repository, String collection, String username, String password, String onBehalfOf, String packager, String depositor) {
	    this();
		setName(name);
		setRepository(repository);
		setCollection(collection);
		setUsername(username);
		setPassword(password);
		setOnBehalfOf(onBehalfOf);
		setPackager(packager);
		setDepositor(depositor);
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
