package org.tdl.vireo.deposit.impl;

import java.io.File;
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
import org.tdl.vireo.deposit.Depositor;
import org.tdl.vireo.model.DepositLocation;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.search.SearchFilter;

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
	public void deposit(DepositLocation location, Submission submission) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deposit(DepositLocation location, SearchFilter filter) {
		// TODO Auto-generated method stub
		
	}
	
	
//	@Override
//	public int deposit(Location credentials, URL collection,
//			PackageIterator packageIterator) {
//		if(credentials == null || credentials.repositoryURL == null)
//		{
//			throw new IllegalArgumentException("Bad credentials or repository when trying to deposit()");
//		}
//		
//		if(collection == null)
//		{
//			throw new IllegalArgumentException("Null collection when trying to deposit()");
//		}
//		
//		if(packageIterator == null)
//		{
//			throw new IllegalArgumentException("Null packageIterator when trying to deposit()");
//		}
//		
//		// Note, an important side effect of this check is to double check the
//		// user's authentication with the sword server. In most cases if the
//		// user does not possess the proper access on the remote server then
//		// this call would result in an exception being thrown.
//		if(getCollectionName(credentials, collection) == null) 
//		{
//			throw new IllegalArgumentException("Collection url: '"+collection.toExternalForm()+"' is invalid upon deposit()");
//		}
//		
//		//Building the client
//		Client client = new Client();
//		client.setServer(credentials.repositoryURL.getHost(), credentials.repositoryURL.getPort());
//		client.setUserAgent(USER_AGENT);
//		
//		//If the credentials include a username and password, set those on the client.
//		if(credentials.username != null && credentials.password != null)
//			client.setCredentials(credentials.username, credentials.password);
//		
//		//create POST message of each deposit
//		int count = 0;
//		while(packageIterator.hasNext())
//		{
//			try{
//				File packageFile = packageIterator.next();
//				PostMessage message = new PostMessage();
//				message.setFilepath(packageFile.getAbsolutePath());
//				message.setDestination(collection.toExternalForm());
//				message.setFiletype(packageIterator.getMimeType());
//				message.setUseMD5(false);
//				message.setVerbose(false);
//				message.setNoOp(false);
//				message.setFormatNamespace(packageIterator.getFormat());
//				message.setSlug("");
//				message.setChecksumError(false);
//				message.setUserAgent(USER_AGENT);
//				
//				DepositResponse response = client.postFile(message);
//				packageIterator.setDepositID(response.getEntry().getId());
//				count++;
//			}
//			catch(SWORDClientException sce)
//			{
//				packageIterator.setDepositError(sce.getMessage());
//				Logger.error(sce, "Exception trying to deposit package.");
//			}
//			
//		}
//		
//		return count;
//	}
	
	
	
	
	
}
