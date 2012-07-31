package org.tdl.vireo.deposit;

import java.util.ArrayList;
import java.util.List;

import org.tdl.vireo.model.Submission;

/**
 * Mock implementation of the packager interface.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class MockPackager implements Packager {

	public String beanName = "MockPackager";
	public String displayName = "Mock Packager";

	// List of all the generated packages;
	public List<MockDepositPackage> generated = new ArrayList<MockDepositPackage>();

	@Override
	public String getBeanName() {
		return beanName;
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}

	@Override
	public DepositPackage generatePackage(Submission submission) {
		MockDepositPackage pkg = new MockDepositPackage();

		if (submission.getDepositId() != null)
			pkg.depositId = submission.getDepositId();

		generated.add(pkg);
		return pkg;
	}

}
