package org.tdl.vireo.deposit.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.purl.sword.base.Collection;
import org.purl.sword.base.DepositResponse;
import org.purl.sword.base.Service;
import org.purl.sword.base.ServiceDocument;
import org.purl.sword.base.Workspace;
import org.purl.sword.client.Client;
import org.purl.sword.client.PostMessage;
import org.purl.sword.client.SWORDClientException;
import org.springframework.beans.factory.BeanNameAware;
import org.tdl.vireo.deposit.DepositException;
import org.tdl.vireo.deposit.DepositPackage;
import org.tdl.vireo.deposit.Depositor;
import org.tdl.vireo.model.DepositLocation;

import play.Logger;
import org.tdl.vireo.deposit.DepositException.FIELD;

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
	public String deposit(DepositLocation location, DepositPackage depositPackage) {


		// Check our input
		if (location == null)
			throw new IllegalArgumentException("The deposit location is required.");

		if (depositPackage == null)
			throw new IllegalArgumentException("The deposit package is required.");

		if ( depositPackage.getFile() == null ||  !depositPackage.getFile().exists())
			throw new IllegalArgumentException("The deposit package does not exist on disk or is inaccessable.");

		if (location.getRepository() == null)
			throw new IllegalArgumentException("The deposit location must have a repository URL defined.");

		if (location.getCollection() == null)
			throw new IllegalArgumentException("The deposit location must have a collection URL defined.");

		try{
		URL repositoryURL = new URL(location.getRepository());
		
			//Building the client
			Client client = new Client();
			client.setServer(
					repositoryURL.getHost(), 
					repositoryURL.getPort());
			client.setUserAgent(USER_AGENT);
	
			//If the credentials include a username and password, set those on the client.
			if (location.getUsername() != null && location.getPassword() != null)
				client.setCredentials(location.getUsername(), location.getPassword());
			
			PostMessage message = new PostMessage();

			message.setFilepath(depositPackage.getFile().getAbsolutePath());
			message.setDestination(location.getCollection());
			message.setFiletype(depositPackage.getMimeType());
			message.setUseMD5(false);
			message.setVerbose(false);
			message.setNoOp(false);
			message.setFormatNamespace(depositPackage.getFormat());
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
		} catch (RuntimeException re) {
			Logger.error(re,"Unable to deposit()");
			
			throw new DepositException(FIELD.OTHER, re.getMessage(), re);
		} 
	}	

}
