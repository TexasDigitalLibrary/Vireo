package org.tdl.vireo.export;

import java.util.ArrayList;
import java.util.List;

import org.tdl.vireo.model.Submission;

/**
 * Mock implementation of the packager interface.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 * @author Gad Krumholz ( gad.krumholz@austin.utexas.edu )
 */
public class MockPackager implements Packager {

	public String beanName = "MockPackager";
	public String displayName = "Mock Packager";

	// List of all the generated packages;
	public List<MockExportPackage> generated = new ArrayList<MockExportPackage>();

	@Override
	public String getBeanName() {
		return beanName;
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}

	@Override
	public ExportPackage generatePackage(Submission submission) {
		MockExportPackage pkg = new MockExportPackage();

		pkg.submission = submission;

		generated.add(pkg);
		return pkg;
	}

    @Override
    public String getExportServiceBeanName() {
        return "ExportService";
    }

}
