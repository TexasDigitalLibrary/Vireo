package org.tdl.vireo.export;

import org.tdl.vireo.model.Submission;

/**
 * Create packages suitable for depositing.
 * 
 * This object is responsible for translating a Vireo Submission into a single
 * package file that fully describes the submission according to the standard it
 * implements. This package is then deposited into a repository via the
 * Depositor.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public interface Packager {

	/**
	 * @return The technical spring bean name of this packager implementation.
	 */
	public String getBeanName();

	/**
	 * @return The displayable name of this bean.
	 */
	public String getDisplayName();

	/**
	 * Generate a new package for this submission.
	 * 
	 * The package returned will file system resources so it is very important
	 * that any caller who constructs a package also calls delete() to free
	 * those resources used.
	 * 
	 * 
	 * @param submission
	 *            The submission
	 * @return A package
	 */
	public ExportPackage generatePackage(Submission submission);

}
