package org.tdl.vireo.deposit;

import java.io.File;

/**
 * A deposit package contains all the information necessary for a depositor to
 * submit the submission to a remote repository. This class is build by a
 * packager and consumed by a depositor.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public interface DepositPackage {

	/**
	 * If the package allready has a deposit id assigned, possibily by a
	 * previous deposit then this is the id assigned.
	 * 
	 * @return The deposit id for this package, may be null if not available.
	 */
	public String getDepositId();
	
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
