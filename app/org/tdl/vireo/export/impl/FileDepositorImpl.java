package org.tdl.vireo.export.impl;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.BeanNameAware;
import org.tdl.vireo.export.DepositException;
import org.tdl.vireo.export.DepositException.FIELD;
import org.tdl.vireo.export.Depositor;
import org.tdl.vireo.export.ExportPackage;
import org.tdl.vireo.model.DepositLocation;
import org.tdl.vireo.model.Submission;

import play.Logger;
import play.Play;

/**
 * A simple file depositor. This implementation will just copy the deposit
 * packages into an out put directory. This is nice for testing.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class FileDepositorImpl implements Depositor, BeanNameAware {

	/**
	 * The basic identity parameters for this service. All of these parameters
	 * will be injected from Spring
	 **/
	public String beanName;
	public String displayName;
	public File baseDir;

	// Only Spring should instantiate
	protected FileDepositorImpl() {
	}

	@Override
	public String getBeanName() {
		return beanName;
	}

	@Override
	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}
	
	@Override
	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public void setBaseDirectory(String path) {
		this.baseDir = new File(Play.applicationPath + File.separator + path);
	}
	

	@Override
	public Map<String, String> getCollections(DepositLocation location) {

		try {
			if (baseDir == null)
				throw new IllegalArgumentException("The preconfigured base deposit directory has not been defined. Please contact your system administrator to correct Vireo's configuration");
			
			if (!baseDir.exists())
				throw new IllegalArgumentException("The preconfigured '"+baseDir.getPath()+"' base deposit directory does not exist. Please contact your system administrator to either create the base deposit directory or correct vireo's configuration");
			
			if (!baseDir.canRead())
				throw new IllegalArgumentException("The preconfigured '"+baseDir.getPath()+"' base deposit directory is not readable. Please contact your system administrator to either create the base deposit directory or correct vireo's configuration");
			
			if (!baseDir.isDirectory())
				throw new IllegalArgumentException("The preconfigured '"+baseDir.getPath()+"' base deposit directory is not a directory. Please contact your system administrator to either create the base deposit directory or correct vireo's configuration");
			
			// Check that the repository contained within our base directory
			String repoPath = location.getRepository();		
			File repoDir = new File(repoPath);
			String baseDirCanonical = baseDir.getCanonicalPath();
			String repoDirCanonical = repoDir.getCanonicalPath();
			if (!repoDirCanonical.startsWith(baseDirCanonical))
				throw new SecurityException("Unauthorized deposit location; the repository path '"+repoDirCanonical+"' must be restricted to the base directory '"+baseDirCanonical+"'");

			if (!repoDir.exists())
				throw new IllegalArgumentException("The deposit directory '"+repoPath+"' does not exist.");
			
			if (!repoDir.canRead())
				throw new IllegalArgumentException("The deposit directory '"+repoPath+"' is not readable.");
			
			if (!repoDir.isDirectory())
				throw new IllegalArgumentException("The deposit directory '"+repoPath+"' is not a directory.");

			
			
			File[] children = repoDir.listFiles();
			
			Map<String,String> collections = new HashMap<String,String>();
			collections.put("Base directory", baseDir.getCanonicalPath());
			for (File child : children) {
				if (child.isDirectory())
					collections.put(child.getName(), child.getCanonicalPath());
			}
			
			return collections;

		} catch (IllegalArgumentException iae) {
			Logger.error(iae,"Unable to getCollections()");
					
			FIELD field = FIELD.REPOSITORY;
			String message = iae.getMessage();

			throw new DepositException(field, message, iae);
			
		} catch (SecurityException se) {
			Logger.error(se,"Unable to deposit()");
					
			FIELD field = FIELD.REPOSITORY;
			String message = se.getMessage();

			throw new DepositException(field, message, se);
			
		} catch (IOException ioe) {
			Logger.error(ioe,"Unable to getCollections()");

			FIELD field = FIELD.REPOSITORY;
			String message = ioe.getMessage();
			
			throw new DepositException(field, message, ioe);
					
		} catch (RuntimeException re) {
			Logger.error(re,"Unable to getCollections()");
			
			FIELD field = FIELD.OTHER;
			String message = re.getMessage();
			
			throw new DepositException(field, message, re);
		} 
	}


	@Override
	public String getCollectionName(DepositLocation location, String collection) {
		Map<String, String> namesToCollections = this.getCollections(location);
		for( String name : namesToCollections.keySet())
		{
			if(namesToCollections.get(name).equals(collection))
				return name;
		}
		return null;
	}

	@Override
	public String deposit(DepositLocation location, ExportPackage exportPackage) {

		try {
		// Check our input
		if (location == null)
			throw new IllegalArgumentException("The deposit location is required.");

		if (exportPackage == null)
			throw new IllegalArgumentException("The deposit package is required.");

		if ( exportPackage.getFile() == null ||  !exportPackage.getFile().exists())
			throw new IllegalArgumentException("The deposit package does not exist on disk or is inaccessable.");

		if (location.getRepository() == null)
			throw new IllegalArgumentException("The deposit location must have a repository path defined.");

		if (location.getCollection() == null)
			throw new IllegalArgumentException("The deposit location must have a collection path defined.");

		if (baseDir == null)
			throw new IllegalArgumentException("The preconfigured base deposit directory has not been defined. Please contact your system administrator to correct Vireo's configuration");
		
		if (!baseDir.exists())
			throw new IllegalArgumentException("The preconfigured '"+baseDir.getPath()+"' base deposit directory does not exist. Please contact your system administrator to either create the base deposit directory or correct vireo's configuration");
		
		if (!baseDir.canRead())
			throw new IllegalArgumentException("The preconfigured '"+baseDir.getPath()+"' base deposit directory is not readable. Please contact your system administrator to either create the base deposit directory or correct vireo's configuration");
		
		if (!baseDir.isDirectory())
			throw new IllegalArgumentException("The preconfigured '"+baseDir.getPath()+"' base deposit directory is not a directory. Please contact your system administrator to either create the base deposit directory or correct vireo's configuration");

		// Check that the depositDir contained within our base directory
		String depositPath = location.getCollection();		
		File depositDir = new File(depositPath);
		String baseDirCanonical = baseDir.getCanonicalPath();
		String depositDirCanonical = depositDir.getCanonicalPath();
		if (!depositDirCanonical.startsWith(baseDirCanonical))
			throw new SecurityException("Unauthorized deposit location; the collection path '"+depositDirCanonical+"' must be restricted to the base directory '"+baseDirCanonical+"'");
		
		
		
		if (!depositDir.exists())
			throw new IllegalArgumentException("The deposit directory '"+depositPath+"' does not exist.");
		
		if (!depositDir.canWrite())
			throw new IllegalArgumentException("The deposit directory '"+depositPath+"' is not writable.");
		
		if (!depositDir.isDirectory())
			throw new IllegalArgumentException("The deposit directory '"+depositPath+"' is not a directory.");

		
		
		
		
		// Figure out the destination file
		Submission submission = exportPackage.getSubmission();
		File packageFile = exportPackage.getFile();
		String packageName = packageFile.getName();
		String packageExt = ".pkg";
		if (packageName.lastIndexOf(".") > 0) 
			packageExt = packageName.substring(packageName.lastIndexOf("."),packageName.length());
		
		File exportFile = new File(depositPath + File.separator + "package_"+submission.getId()+packageExt);
		
		// Do the actual deposit
		if (packageFile.isDirectory()) {
			FileUtils.copyDirectory(packageFile, exportFile);
		} else {
			FileUtils.copyFile(packageFile, exportFile);
		}
		
		// We don't return a deposit id
		return null;
			
		} catch (SecurityException se) {
			Logger.error(se,"Unable to deposit()");
					
			FIELD field = FIELD.COLLECTION;
			String message = se.getMessage();

			throw new DepositException(field, message, se);
			
		} catch (IllegalArgumentException iae) {
			Logger.error(iae,"Unable to deposit()");
					
			FIELD field = FIELD.REPOSITORY;
			String message = iae.getMessage();

			throw new DepositException(field, message, iae);
			
		} catch (IOException ioe) {
			Logger.error(ioe,"Unable to deposit()");

			FIELD field = FIELD.REPOSITORY;
			String message = ioe.getMessage();
			
			throw new DepositException(field, message, ioe);
					
		} catch (RuntimeException re) {
			Logger.error(re,"Unable to deposit()");
			
			FIELD field = FIELD.OTHER;
			String message = re.getMessage();
			
			throw new DepositException(field, message, re);
		}
	}	

}
