package org.tdl.vireo.model.depositor;

import java.util.Map;

import org.tdl.vireo.model.DepositLocation;

public class SWORDv1Depositor implements Depositor {
	private String name;
	
	public SWORDv1Depositor() {
		setName("SWORDv1Depositor");
	}

	public Map<String, String> getCollections(DepositLocation depLocation) {
		System.out.println("*** GETTING COLLECTIONS ***");
		return null;
	}

	public String deposit(DepositLocation depLocation, Package depPackage) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getName() {
		return name;
	}
	
	protected void setName(String name) {
		this.name = name;
	}

}
