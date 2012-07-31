package org.tdl.vireo.deposit;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.tdl.vireo.model.DepositLocation;

/**
 * Mock implementation of the depositor interface
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class MockDepositor implements Depositor {

	public int depositsPerformed = 0;

	public String beanName = "MockDepositor";
	public String displayName = "Mock Depositor";

	public String depositIdFormat = "http://repository.edu/deposit/%d1";

	public Map<String, URL> collectionsMap = new HashMap<String, URL>();

	@Override
	public String getBeanName() {
		return "MockDepositor";
	}

	@Override
	public String getDisplayName() {
		return "Mock Depositor";
	}

	@Override
	public String deposit(DepositLocation location,
			DepositPackage depositPackage) {

		return String.format(depositIdFormat, depositsPerformed++);

	}

	@Override
	public Map<String, URL> getCollections(DepositLocation location) {
		return null;
	}

	@Override
	public String getCollectionName(DepositLocation location, URL collectionURL) {
		Map<String, URL> namesToCollectionURLs = this.getCollections(location);
		for (String name : namesToCollectionURLs.keySet()) {
			if (namesToCollectionURLs.get(name).equals(collectionURL))
				return name;
		}
		return null;
	}

}
