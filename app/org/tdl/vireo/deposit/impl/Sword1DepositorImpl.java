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
import org.tdl.vireo.deposit.DepositPackage;
import org.tdl.vireo.deposit.Depositor;
import org.tdl.vireo.model.DepositLocation;

import play.Logger;


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
	public Map<String, URL> getCollections(DepositLocation location) {
		Map<String, URL> foundCollections = new HashMap<String, URL>();
		
		if(location == null || location.getRepositoryURL() == null)
		{
			throw new IllegalArgumentException("Bad deposit location or repository URL when trying to getCollections()");
		}
		
		//Building the client
		Client client = new Client();
		client.setServer(location.getRepositoryURL().getHost(), location.getRepositoryURL().getPort());
		client.setUserAgent(USER_AGENT);
		
		//If the credentials include a username and password, set those on the client.
		if(location.getUsername() != null && location.getPassword() != null)
			client.setCredentials(location.getUsername(), location.getPassword());
		
		//Obtaining the service document
		//If the credentials contain an onbehalfof user, retrieve the service document on behalf of that user.  Otherwise, simply retrieve the service document.
		ServiceDocument serviceDocument = null;
		String repositoryURLString = location.getRepositoryURL().toExternalForm();
		try {
			if(location.getOnBehalfOf() != null)
			{			
				serviceDocument = client.getServiceDocument(repositoryURLString, location.getOnBehalfOf());
			}
			else
			{
				serviceDocument = client.getServiceDocument(repositoryURLString);
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
				// add this collection to our map
				URL collectionURL = null;
				try {
					collectionURL = new URL(collection.getLocation());
				} catch (MalformedURLException e) {
					Logger.error(e, "Exception in forming URL: '" + collection.getLocation() + "' from collection " + collection.getTitle() + " in workspace " + workspace.getTitle() + " for repository " + repositoryURLString);
					continue;
				}
				
				foundCollections.put(collection.getTitle(), collectionURL);
			}
		}		
		
		return foundCollections;
	}


	@Override
	public String getCollectionName(DepositLocation location,
			URL collectionURL) {
		Map<String, URL> namesToCollectionURLs = this.getCollections(location);
		for( String name : namesToCollectionURLs.keySet())
		{
			if(namesToCollectionURLs.get(name).equals(collectionURL))
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
		
		if (location.getRepositoryURL() == null)
			throw new IllegalArgumentException("The deposit location must have a repository URL defined.");

		if (location.getCollectionURL() == null)
			throw new IllegalArgumentException("The deposit location must have a collection URL defined.");

		
		//Building the client
		Client client = new Client();
		client.setServer(
				location.getRepositoryURL().getHost(), 
				location.getRepositoryURL().getPort());
		client.setUserAgent(USER_AGENT);
		
		//If the credentials include a username and password, set those on the client.
		if (location.getUsername() != null && location.getPassword() != null)
			client.setCredentials(location.getUsername(), location.getPassword());
		try{
			PostMessage message = new PostMessage();
			
			message.setFilepath(depositPackage.getFile().getAbsolutePath());
			message.setDestination(location.getCollectionURL().toExternalForm());
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
		}
		catch(SWORDClientException sce)
		{
			throw new RuntimeException(sce);
		}		
	}	
	
}
