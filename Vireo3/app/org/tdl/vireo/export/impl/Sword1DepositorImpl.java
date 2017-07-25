package org.tdl.vireo.export.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.purl.sword.base.Collection;
import org.purl.sword.base.DepositResponse;
import org.purl.sword.base.Service;
import org.purl.sword.base.ServiceDocument;
import org.purl.sword.base.Workspace;
import org.purl.sword.client.Client;
import org.purl.sword.client.PostMessage;
import org.purl.sword.client.SWORDClientException;
import org.springframework.beans.factory.BeanNameAware;
import org.tdl.vireo.export.DepositException;
import org.tdl.vireo.export.DepositException.FIELD;
import org.tdl.vireo.export.Depositor;
import org.tdl.vireo.export.ExportPackage;
import org.tdl.vireo.model.DepositLocation;

import play.Logger;

/**
 * Sword, version 1, depositor. This supports identifying collections from the
 * service document and depositing them into the repository.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class Sword1DepositorImpl implements Depositor, BeanNameAware {

	/**
	 * The basic identity parameters for this service. All of these parameters
	 * will be injected from Spring
	 **/
	private String beanName;
	private String displayName;
	private final String USER_AGENT = "Vireo Sword 1.0 Depositor";

	// Only Spring should instantiate
	protected Sword1DepositorImpl() {

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


	@Override
	public Map<String, String> getCollections(DepositLocation location) {

		try {
			Map<String, String> foundCollections = new HashMap<String, String>();

			if(location == null || location.getRepository() == null)
				throw new IllegalArgumentException("Bad deposit location or repository URL when trying to getCollections()");

			
			URL repositoryURL = new URL(location.getRepository());
			
			//Building the client
			Client client = new Client();
			// get the timeout from the location, or default it to the default
			client.setSocketTimeout(location.getTimeout() == null ? DepositLocation.DEFAULT_TIMEOUT : (location.getTimeout() * 1000));
			client.setServer(repositoryURL.getHost(), repositoryURL.getPort());
			client.setUserAgent(USER_AGENT);
			
			//If the credentials include a username and password, set those on the client.
			if(location.getUsername() != null && location.getPassword() != null)
				client.setCredentials(location.getUsername(), location.getPassword());

			//Obtaining the service document
			//If the credentials contain an onbehalfof user, retrieve the service document on behalf of that user.  Otherwise, simply retrieve the service document.
			ServiceDocument serviceDocument = null;
			try {
				if(location.getOnBehalfOf() != null)
				{			
					serviceDocument = client.getServiceDocument(location.getRepository(), location.getOnBehalfOf());
				}
				else
				{
					serviceDocument = client.getServiceDocument(location.getRepository());
				}
			} catch (SWORDClientException e) {
				throw new RuntimeException(e);
			}
			
			//Getting the service from the service document
			Service service = serviceDocument.getService();

			//Building the map of collections from the service
			for(Workspace workspace : service.getWorkspacesList())
			{
				for(Collection collection : workspace.getCollections())
				{
					foundCollections.put(collection.getTitle(), collection.getLocation());
				}
			}		
			
			return foundCollections;

		} catch (MalformedURLException murle) {
			throw new DepositException(FIELD.REPOSITORY,"The repository is an invalid URL",murle);
		
		} catch (RuntimeException re) {
			Logger.error(re,"Unable to getCollections()");
			
			FIELD field = FIELD.OTHER;
			String message = re.getMessage();

			if (re.getMessage().contains("Code: 401")) {
				field = FIELD.AUTHENTICATION;
				message = "Unauthorized credentials";
			} else if (re.getMessage().contains("Code: 404")) {
				field = FIELD.REPOSITORY;
				message = "Repository URL not found";
			} else if (re.getMessage().contains("Connection refused")) {
				field = FIELD.REPOSITORY;
				message = "Connection refused";
			} else if (re.getMessage().contains("Unable to parse the XML")) {
				field = FIELD.REPOSITORY;
				message = "The repository does not appear to be a valid SWORD server.";
			} 

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

		boolean zippedExport = false;
		File exportFile = null;
		
		try {
			// Check our input
			if (location == null)
				throw new IllegalArgumentException("The deposit location is required.");
	
			if (exportPackage == null)
				throw new IllegalArgumentException("The deposit package is required.");
	
			if ( exportPackage.getFile() == null ||  !exportPackage.getFile().exists())
				throw new IllegalArgumentException("The deposit package does not exist on disk or is inaccessable.");
	
			if (location.getRepository() == null)
				throw new IllegalArgumentException("The deposit location must have a repository URL defined.");
	
			if (location.getCollection() == null)
				throw new IllegalArgumentException("The deposit location must have a collection URL defined.");


			URL repositoryURL = new URL(location.getRepository());
		
			// Check the package
			exportFile = exportPackage.getFile();
			String exportMimeType = exportPackage.getMimeType();
			
			// If we are given a directory, zip it up because sword requires one file submissions.
			if (exportFile.isDirectory()) {
				File zipFile = File.createTempFile(exportFile.getName()+"-", ".zip");
				zipPackage(zipFile, exportFile);
				exportFile = zipFile;
				exportMimeType = "application/zip";
				zippedExport = true;
			}
			
			
			//Building the client
			Client client = new Client();
			// get the timeout from the location, or default it to the default
			client.setSocketTimeout(location.getTimeout() == null ? DepositLocation.DEFAULT_TIMEOUT : (location.getTimeout() * 1000));
			client.setServer(
					repositoryURL.getHost(), 
					repositoryURL.getPort());
			client.setUserAgent(USER_AGENT);
	
			//If the credentials include a username and password, set those on the client.
			if (location.getUsername() != null && location.getPassword() != null)
				client.setCredentials(location.getUsername(), location.getPassword());
			
			PostMessage message = new PostMessage();

			message.setFilepath(exportFile.getAbsolutePath());
			message.setDestination(location.getCollection());
			message.setFiletype(exportMimeType);
			message.setUseMD5(false);
			message.setVerbose(false);
			message.setNoOp(false);
			message.setFormatNamespace(exportPackage.getFormat());
			message.setSlug("");
			message.setChecksumError(false);
			message.setUserAgent(USER_AGENT);
			if (location.getOnBehalfOf() != null)
				message.setOnBehalfOf(location.getOnBehalfOf());

			DepositResponse response = client.postFile(message);


			if (response.getHttpResponse() < 200 || response.getHttpResponse() > 204 )
				throw new RuntimeException("Sword server responed with a non success HTTP status code: "+response.getHttpResponse());

			String depositId = response.getEntry().getId();
			if (depositId == null)
				throw new RuntimeException("Sword server failed to return a deposit id.");
			return depositId;
			
		} catch (MalformedURLException murle) {
			Logger.error(murle,"Unable to deposit()");

			throw new DepositException(FIELD.REPOSITORY,"The repository is an invalid URL",murle);
			
		} catch(SWORDClientException sce) {
			Logger.error(sce,"Unable to deposit()");

			
			FIELD field = FIELD.OTHER;
			String message = sce.getMessage();

			if (sce.getMessage().contains("Code: 401")) {
				field = FIELD.AUTHENTICATION;
				message = "Unauthorized credentials";
			} else if (sce.getMessage().contains("Code: 404")) {
				field = FIELD.REPOSITORY;
				message = "Repository URL not found";
			} else if (sce.getMessage().contains("Connection refused")) {
				field = FIELD.REPOSITORY;
				message = "Connection refused";
			} else if (sce.getMessage().contains("Unable to parse the XML")) {
				field = FIELD.REPOSITORY;
				message = "The repository does not appear to be a valid SWORD server.";
			} 

			throw new DepositException(field, message, sce);
		} catch (IOException ioe) {
			Logger.error(ioe, "Unable to deposit()");
			
			throw new DepositException(FIELD.OTHER,ioe.getMessage(),ioe);
			
		} catch (RuntimeException re) {
			Logger.error(re,"Unable to deposit()");
			
			throw new DepositException(FIELD.OTHER, re.getMessage(), re);
		} finally {
			// If we created a zip file make sure it gets cleaned up.
			
			if (zippedExport && exportFile != null)
				exportFile.delete();
		}
	}	

	/**
	 * Internal method for ziping a directory together into a single deposit
	 * package.
	 * 
	 * One tricky note, the file name of the directory is not included in the
	 * resulting zip.
	 * 
	 * @param zipFile
	 *            The file where the zip will be created.
	 * @param dirFile
	 *            The source directory.
	 */
	protected static void zipPackage(File zipFile, File dirFile) throws IOException {

		// The result is a directory, so we need to zip the directory up.
		FileOutputStream fos = new FileOutputStream(zipFile);
		ZipOutputStream zos  = new ZipOutputStream(fos);
		byte[] buffer = new byte[1024];
		int bufferLength;
		
		
		zipDirectory("",dirFile,zos);
		
		zos.close();
	}
	
	/**
	 * Internal recursive method for zipping the files and sub-directories of a
	 * file into a single deposit package.
	 * 
	 * @param baseName
	 *            The base name to pre-pend to file names in the zip archive.
	 * @param directory
	 *            The directory to process.
	 * @param zos
	 *            The zip output stream.
	 */
	protected static void zipDirectory(String baseName, File directory, ZipOutputStream zos) throws IOException
	{
		// Add all the files
		File[] files = directory.listFiles();
		for (File file : files) {
			
			if (file.isDirectory()) {

				zipDirectory(baseName + directory.getName() + File.separator, file, zos);
			} else {
				
				InputStream is = new BufferedInputStream(new FileInputStream(file));
				
				zos.putNextEntry(new ZipEntry(baseName + file.getName()));
				
				byte[] buf = new byte[1024];
				int len;
				while ((len = is.read(buf)) > 0) {
					zos.write(buf, 0, len);
				}
				
				is.close();
				zos.closeEntry();
			}	
		}
	}
	
	
}
