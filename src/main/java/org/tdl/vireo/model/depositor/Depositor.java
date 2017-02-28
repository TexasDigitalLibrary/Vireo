package org.tdl.vireo.model.depositor;

import java.util.Map;

import org.tdl.vireo.model.DepositLocation;

public interface Depositor {
	public Map<String,String> getCollections(DepositLocation depLocation);
	public String getName();
	public String deposit(DepositLocation depLocation, Package depPackage);
}
