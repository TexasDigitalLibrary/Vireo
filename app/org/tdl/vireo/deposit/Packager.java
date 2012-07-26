package org.tdl.vireo.deposit;

import java.io.File;

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
	public Package generatePackage(Submission submission);

	/**
	 * A simple inner class to contain the information necessary for depositing
	 * the package.
	 */
	public interface Package {

		/**
		 * The mimetype of the packaging format. Since all packages must be a
		 * single file, typically the mimetype is 'application/zip' for a zip
		 * archive.
		 * 
		 * @return The mimetype
		 */
		public String getMimeType();

		/**
		 * A descriptor of the format used. This may very widely between
		 * repository implementations. For DSpace items packaged using METS, the
		 * format is: http://purl.org/net/sword-types/METSDSpaceSIP
		 * 
		 * @return The format
		 */
		public String getFormat();

		/**
		 * @return A File pointer to the package.
		 */
		public File getFile();

		/**
		 * Release all resources used by this package. All callers who generate
		 * a package must call the delete() method when finished.
		 */
		public void delete();

	}

}
