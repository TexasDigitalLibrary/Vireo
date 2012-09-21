package org.tdl.vireo.model;

import org.tdl.vireo.export.Depositor;
import org.tdl.vireo.export.Packager;

/**
 * This is a simple mock object of deposit locations that may be useful for
 * testing. Feel free to extend this or add in extra parameters that you feel
 * appropriate.
 * 
 * Note, this class will not save or deal with bean names for the packager /
 * depositor like the real implementations would. They keep full concrete
 * objects in memory.
 * 
 * The basic concept is all properties are public so you can create the mock
 * object and then set whatever relevant properties are needed for your
 * particular test.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class MockDepositLocation extends AbstractMock implements
		DepositLocation {

	/* Deposit Location Properties */
	public Long id;
	public int displayOrder;
	public String name;
	public String repository;
	public String collection;
	public String username;
	public String password;
	public String onBehalfOf;
	public Packager packager;
	public Depositor depositor;

	@Override
	public MockDepositLocation save() {
		return this;
	}

	@Override
	public MockDepositLocation delete() {
		return this;
	}

	@Override
	public MockDepositLocation refresh() {
		return this;
	}

	@Override
	public MockDepositLocation merge() {
		return this;
	}
	
	@Override
	public MockDepositLocation detach() {
		return this;
	}

	@Override
	public int getDisplayOrder() {
		return displayOrder;
	}

	@Override
	public void setDisplayOrder(int displayOrder) {
		this.displayOrder = displayOrder;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getRepository() {
		return repository;
	}

	@Override
	public void setRepository(String location) {
		this.repository = location;
	}

	@Override
	public String getCollection() {
		return collection;
	}

	@Override
	public void setCollection(String collection) {
		this.collection = collection;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String getOnBehalfOf() {
		return onBehalfOf;
	}

	@Override
	public void setOnBehalfOf(String onBehalfOf) {
		this.onBehalfOf = onBehalfOf;
	}

	@Override
	public Packager getPackager() {
		return packager;
	}

	@Override
	public void setPackager(Packager packager) {
		this.packager = packager;
	}

	@Override
	public Depositor getDepositor() {
		return depositor;
	}

	@Override
	public void setDepositor(Depositor depositor) {
		this.depositor = depositor;
	}

}
