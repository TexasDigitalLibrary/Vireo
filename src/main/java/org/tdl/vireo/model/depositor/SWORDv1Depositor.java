package org.tdl.vireo.model.depositor;

import java.io.File;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.purl.sword.base.Collection;
import org.purl.sword.base.DepositResponse;
import org.purl.sword.base.Service;
import org.purl.sword.base.ServiceDocument;
import org.purl.sword.base.Workspace;
import org.purl.sword.client.Client;
import org.purl.sword.client.PostMessage;
import org.purl.sword.client.SWORDClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdl.vireo.exception.SwordDepositException;
import org.tdl.vireo.model.DepositLocation;
import org.tdl.vireo.model.export.ExportPackage;
import org.tdl.vireo.utility.FileHelperUtility;

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

            if (depLocation == null || depLocation.getRepository() == null) {
                throw new SwordDepositException("Bad deposit location or repository URL when trying to getCollections()");
            }

            logger.debug("Getting Collections via SWORD from: " + depLocation.getRepository());

            URL repositoryURL = new URL(depLocation.getRepository());

            // Building the client
            Client client = new Client();
            // get the timeout from the location, or default it to the default
            client.setSocketTimeout(depLocation.getTimeout() == null ? DEFAULT_TIMEOUT : (depLocation.getTimeout() * 1000));

            client.setServer(repositoryURL.getHost(), repositoryURL.getPort());
            client.setUserAgent(USER_AGENT);

            // If the credentials include a username and password, set those on the client.
            if (depLocation.getUsername() != null && depLocation.getPassword() != null) {
                setAuthentication(client, depLocation);
            }

            // Obtaining the service document
            // If the credentials contain an onbehalfof user, retrieve the service document on
            // behalf of that user. Otherwise, simply retrieve the service document.
            ServiceDocument serviceDocument = null;
            try {
                if (depLocation.getOnBehalfOf() != null) {
                    serviceDocument = client.getServiceDocument(depLocation.getRepository(), depLocation.getOnBehalfOf());
                } else {
                    serviceDocument = client.getServiceDocument(depLocation.getRepository());
                }
            } catch (SWORDClientException e) {
                logger.debug("Could not get service document!", e);
                throw new SwordDepositException("Could not get service document!", e);
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
            logger.debug("The repository is an invalid URL", murle);
            throw new SwordDepositException("The repository is an invalid URL", murle);
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

            logger.debug(message, re);
            throw new SwordDepositException(message, re);
        }
    }

    public String deposit(DepositLocation depLocation, ExportPackage exportPackage) {

        try {
            URL repositoryURL = new URL(depLocation.getRepository());

            FileHelperUtility fileHelperUtility = new FileHelperUtility();

            File exportFile = (File) exportPackage.getPayload();

            String exportMimeType = fileHelperUtility.getMimeType(exportFile);

            // Building the client
            Client client = new Client();
            // get the timeout from the location, or default it to the default
            client.setSocketTimeout(depLocation.getTimeout() == null ? DepositLocation.DEFAULT_TIMEOUT : (depLocation.getTimeout() * 1000));
            client.setServer(repositoryURL.getHost(), repositoryURL.getPort());
            client.setUserAgent(USER_AGENT);

            // If the credentials include a username and password, set those on the client.
            if (depLocation.getUsername() != null && depLocation.getPassword() != null) {
                setAuthentication(client, depLocation);
            }

            PostMessage message = new PostMessage();

            message.setFilepath(exportFile.getAbsolutePath());
            message.setDestination(depLocation.getCollection());
            message.setFiletype(exportMimeType);
            message.setUseMD5(false);
            message.setVerbose(false);
            message.setNoOp(false);
            message.setFormatNamespace(exportPackage.getFormat());
            message.setSlug("");
            message.setChecksumError(false);
            message.setUserAgent(USER_AGENT);
            if (depLocation.getOnBehalfOf() != null) {
                message.setOnBehalfOf(depLocation.getOnBehalfOf());
            }

            DepositResponse response = client.postFile(message);

            if (response.getHttpResponse() < 200 || response.getHttpResponse() > 204) {
                throw new SwordDepositException("Sword server responed with a non success HTTP status code: " + response.getHttpResponse());
            }

            try {
                URI sword = new URI(depLocation.getRepository());
                URI handle = new URI(response.getEntry().getId());

                String depositURL = sword.getScheme() + "://" + sword.getHost();
                if (sword.getPort() != -1) {
                    depositURL += ":" + sword.getPort();
                }
                depositURL += "/handle" + handle.getPath();

                return new URI(depositURL).toString();
            } catch (URISyntaxException e) {
                throw new SwordDepositException("Unable to publish to " + depLocation.getRepository(), e);
            }

        } catch (MalformedURLException | SWORDClientException e) {
            logger.debug(e.getMessage(), e);
            throw new SwordDepositException(e.getMessage(), e);
        }
    }

    private void setAuthentication(Client client, DepositLocation depLocation) {
        client.setCredentials(depLocation.getUsername(), depLocation.getPassword());
        try {
            Field httpClientField = client.getClass().getDeclaredField("client");
            httpClientField.setAccessible(true);
            HttpClient httpClient = (HttpClient) httpClientField.get(client);
            httpClient.getParams().setAuthenticationPreemptive(true);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            logger.debug(e.getMessage(), e);
            throw new SwordDepositException(e.getMessage(), e);
        }
    }

    public String getName() {
        return name;
    }

    protected void setName(String name) {
        this.name = name;
    }

}
