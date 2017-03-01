package org.tdl.vireo.model.depositor;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.purl.sword.base.Collection;
import org.purl.sword.base.Service;
import org.purl.sword.base.ServiceDocument;
import org.purl.sword.base.Workspace;
import org.purl.sword.client.Client;
import org.purl.sword.client.SWORDClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdl.vireo.model.DepositLocation;
import org.tdl.vireo.model.export.ExportPackage;

public class SWORDv1Depositor implements Depositor {

    private String name;

    private final String USER_AGENT = "Vireo Sword 1.0 Depositor";

    private final int DEFAULT_TIMEOUT = 60000;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public SWORDv1Depositor() {
        setName("SWORDv1Depositor");
    }

    public Map<String, String> getCollections(DepositLocation depLocation) {
        try {
            Map<String, String> foundCollections = new HashMap<String, String>();

            if (depLocation == null || depLocation.getRepository() == null)
                throw new IllegalArgumentException("Bad deposit location or repository URL when trying to getCollections()");

            logger.debug("Getting Collections via SWORD from: " + depLocation.getRepository());

            URL repositoryURL = new URL(depLocation.getRepository());

            // Building the client
            Client client = new Client();
            // get the timeout from the location, or default it to the default
            client.setSocketTimeout(depLocation.getTimeout() == null ? DEFAULT_TIMEOUT : (depLocation.getTimeout() * 1000));

            client.setServer(repositoryURL.getHost(), repositoryURL.getPort());
            client.setUserAgent(USER_AGENT);

            // If the credentials include a username and password, set those on the client.
            if (depLocation.getUsername() != null && depLocation.getPassword() != null)
                client.setCredentials(depLocation.getUsername(), depLocation.getPassword());

            // Obtaining the service document
            // If the credentials contain an onbehalfof user, retrieve the service document on behalf of that user. Otherwise, simply retrieve the service document.
            ServiceDocument serviceDocument = null;
            try {
                if (depLocation.getOnBehalfOf() != null) {
                    serviceDocument = client.getServiceDocument(depLocation.getRepository(), depLocation.getOnBehalfOf());
                } else {
                    serviceDocument = client.getServiceDocument(depLocation.getRepository());
                }
            } catch (SWORDClientException e) {
                throw new RuntimeException(e);
            }

            // Getting the service from the service document
            Service service = serviceDocument.getService();

            // Building the map of collections from the service
            for (Workspace workspace : service.getWorkspacesList()) {
                for (Collection collection : workspace.getCollections()) {
                    foundCollections.put(collection.getTitle(), collection.getLocation());
                }
            }
            return foundCollections;

        } catch (MalformedURLException murle) {
            logger.error("The repository is an invalid URL", murle);
        } catch (RuntimeException re) {

            String message = re.getMessage();

            if (re.getMessage().contains("Code: 401")) {
                message = "Unauthorized credentials";
            } else if (re.getMessage().contains("Code: 404")) {
                message = "Repository URL not found";
            } else if (re.getMessage().contains("Connection refused")) {
                message = "Connection refused";
            } else if (re.getMessage().contains("Unable to parse the XML")) {
                message = "The repository does not appear to be a valid SWORD server.";
            }

            logger.error(message, re);
        }

        return null;
    }

    public String deposit(DepositLocation depLocation, ExportPackage exportPackage) {
        // TODO deposit the package
        
        
        
        
        return null;
    }

    public String getName() {
        return name;
    }

    protected void setName(String name) {
        this.name = name;
    }

}
