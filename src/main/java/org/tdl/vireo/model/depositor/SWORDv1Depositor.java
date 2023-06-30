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
import org.tdl.vireo.exception.SwordDepositBadGatewayException;
import org.tdl.vireo.exception.SwordDepositBadRequestException;
import org.tdl.vireo.exception.SwordDepositConflictException;
import org.tdl.vireo.exception.SwordDepositException;
import org.tdl.vireo.exception.SwordDepositForbiddenException;
import org.tdl.vireo.exception.SwordDepositGatewayTimeoutException;
import org.tdl.vireo.exception.SwordDepositInternalServerErrorException;
import org.tdl.vireo.exception.SwordDepositNotFoundException;
import org.tdl.vireo.exception.SwordDepositNotImplementedException;
import org.tdl.vireo.exception.SwordDepositRequestTimeoutException;
import org.tdl.vireo.exception.SwordDepositServiceUnavailableException;
import org.tdl.vireo.exception.SwordDepositUnauthorizedException;
import org.tdl.vireo.exception.SwordDepositUnprocessableEntityException;
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
        if (depLocation == null || depLocation.getRepository() == null) {
            throw new SwordDepositInternalServerErrorException("Bad deposit location or repository URL when trying to getCollections().");
        }

        ServiceDocument serviceDocument = null;
        String serviceDocumentUrl = depLocation.getRepository() + "/servicedocument";

        try {
            Map<String, String> foundCollections = new HashMap<String, String>();

            logger.debug("Getting Collections via SWORD from: " + serviceDocumentUrl);

            URL repositoryURL = new URL(serviceDocumentUrl);

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
            if (depLocation.getOnBehalfOf() != null) {
                serviceDocument = client.getServiceDocument(serviceDocumentUrl, depLocation.getOnBehalfOf());
            } else {
                serviceDocument = client.getServiceDocument(serviceDocumentUrl);
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
        } catch (RuntimeException | SWORDClientException re) {
            String message = re.getMessage();

            if (re.getMessage().contains("Code: 400")) {
                message = "Bad request to the SWORD server.";
            } else if (messageContainsCode(re.getMessage(), "401")) {
                throw new SwordDepositUnauthorizedException("Unauthorized credentials.", re);
            } else if (messageContainsCode(re.getMessage(), "404")) {
                throw new SwordDepositNotFoundException("Repository URL not found.", re);
            } else if (messageContainsCode(re.getMessage(), "408")) {
                throw new SwordDepositRequestTimeoutException("Request timed out.", re);
            } else if (messageContainsCode(re.getMessage(), "409")) {
                throw new SwordDepositConflictException(message, re);
            } else if (messageContainsCode(re.getMessage(), "500")) {
                throw new SwordDepositInternalServerErrorException("Internal server error returned by the SWORD server.", re);
            } else if (messageContainsCode(re.getMessage(), "501")) {
                throw new SwordDepositNotImplementedException("Deposit Location collection end point is not implemented on the SWORD server.", re);
            } else if (messageContainsCode(re.getMessage(), "502")) {
                throw new SwordDepositBadGatewayException("Bad gateway.", re);
            } else if (messageContainsCode(re.getMessage(), "503")) {
                throw new SwordDepositServiceUnavailableException("Service unavailable.", re);
            } else if (messageContainsCode(re.getMessage(), "504")) {
                throw new SwordDepositGatewayTimeoutException("Gateway Timeout.", re);
            } else if (re.getMessage().contains("Connection refused")) {
                throw new SwordDepositForbiddenException("Connection refused by the SWORD server.", re);
            } else if (re.getMessage().contains("Unable to parse the XML")) {
                throw new SwordDepositUnprocessableEntityException("The repository does not appear to be a valid SWORD server.", re);
            }
            else if (serviceDocument == null) {
                message = "Could not get service document!";
            }

            throw new SwordDepositBadRequestException(message, re);
        }
    }

    public String deposit(DepositLocation depLocation, ExportPackage exportPackage) {
        if (depLocation == null || depLocation.getRepository() == null) {
            throw new SwordDepositInternalServerErrorException("Bad deposit location or repository URL when trying to deposit().");
        }

        String depositUrl = depLocation.getRepository() + "/deposit";

        try {

            URL repositoryURL = new URL(depositUrl);

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
                URI sword = new URI(depositUrl);
                URI handle = new URI(response.getEntry().getId());

                String depositURL = sword.getScheme() + "://" + sword.getHost();
                if (sword.getPort() != -1) {
                    depositURL += ":" + sword.getPort();
                }
                depositURL += "/handle" + handle.getPath();

                return new URI(depositURL).toString();
            } catch (URISyntaxException e) {
                throw new SwordDepositException("Unable to publish to " + depositUrl, e);
            }
        } catch (MalformedURLException | SWORDClientException re) {
            logger.debug(re.getMessage(), re);
            String message = re.getMessage();
            String repoMessage = ", unable to publish to " + depositUrl;

            if (re.getMessage().contains("Unable to parse the XML")) {
                repoMessage += ", SWORD server cannot parse the XML.";
            }
            else {
                repoMessage += ".";
            }

            if (re.getMessage().contains("Code: 400")) {
                message = "Bad request" + repoMessage;
            } else if (messageContainsCode(re.getMessage(), "401")) {
                throw new SwordDepositUnauthorizedException("Unauthorized credentials" + repoMessage, re);
            } else if (messageContainsCode(re.getMessage(), "404")) {
                throw new SwordDepositNotFoundException("Repository URL not found" + repoMessage, re);
            } else if (messageContainsCode(re.getMessage(), "408")) {
                throw new SwordDepositRequestTimeoutException("Request timed out" + repoMessage, re);
            } else if (messageContainsCode(re.getMessage(), "409")) {
                throw new SwordDepositConflictException("Conflict" + repoMessage, re);
            } else if (messageContainsCode(re.getMessage(), "500")) {
                throw new SwordDepositInternalServerErrorException("Internal server error" + repoMessage, re);
            } else if (messageContainsCode(re.getMessage(), "501")) {
                throw new SwordDepositNotImplementedException("Publish end point is not implemented" + repoMessage, re);
            } else if (messageContainsCode(re.getMessage(), "502")) {
                throw new SwordDepositBadGatewayException("Bad gateway" + repoMessage, re);
            } else if (messageContainsCode(re.getMessage(), "503")) {
                throw new SwordDepositServiceUnavailableException("Service unavailable" + repoMessage, re);
            } else if (messageContainsCode(re.getMessage(), "504")) {
                throw new SwordDepositGatewayTimeoutException("Gateway Timeout" + repoMessage, re);
            } else if (re.getMessage().contains("Connection refused")) {
                throw new SwordDepositForbiddenException("Connection refused" + repoMessage, re);
            } else if (re.getMessage().contains("Unable to parse the XML")) {
                throw new SwordDepositUnprocessableEntityException("XML parse failure, unable to publish to " + depositUrl + ".", re);
            }
            else {
                message = "Unable to publish to " + depositUrl + ".";
            }

            throw new SwordDepositBadRequestException(message, re);
        }
    }

    private boolean messageContainsCode(String message, String code) {
        return message.contains("Code: " + code) || message.contains("HTTP Status [" + code + "]");
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
