package org.tdl.vireo.export;

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

	public Map<String, String> collectionsMap = new HashMap<String, String>();

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
			ExportPackage depositPackage) {

		return String.format(depositIdFormat, depositsPerformed++);

	}

	@Override
	public Map<String, String> getCollections(DepositLocation location) {
		return collectionsMap;
	}

	@Override
	public String getCollectionName(DepositLocation location, String collection) {
		Map<String, String> namesToCollections = this.getCollections(location);
		for (String name : namesToCollections.keySet()) {
			if (namesToCollections.get(name).equals(collection))
				return name;
		}
		return null;
	}

}
