package org.tdl.vireo.util;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;
import static edu.tamu.framework.enums.ApiResponseType.INVALID;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.stereotype.Service;
import org.tdl.vireo.model.FieldValue;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.tamu.framework.model.ApiResponse;
import edu.tamu.framework.model.Credentials;

@Service
public class OrcidUtility {

    private static final String ORCID_API = "http://pub.orcid.org/#/orcid-bio";

    public ApiResponse verifyOrcid(Credentials credentials, FieldValue fieldValue) {
        Map<String, String> errors = new HashMap<String, String>();
        ApiResponse apiResponse = null;
        if (fieldValue.getValue() == "") {
            errors.put("orcid-no-orcid", "Field must be a valid ORCID");
        } else {
            Document doc = this.getDocument(fieldValue.getValue());
            if (doc == null) {
                errors.put("orcid-no-document", "No public profile was found for this ORCID");
            } else {
                if (doc.getElementsByTagName("orcid-message") == null) {
                    errors.put("orcid-no-document", "No public profile was found for this ORCID");
                }
                if (!tagMatchesCredentials(credentials.getFirstName(), doc.getElementsByTagName("given-names"))) {
                    errors.put("orcid-invalid-first-name", "The first name you registered with does not match this ORCID profile");
                }
                if (!tagMatchesCredentials(credentials.getLastName(), doc.getElementsByTagName("family-name"))) {
                    errors.put("orcid-invalid-last-name", "The last name you registered with does not match this ORCID profile");
                }
                if (!tagMatchesCredentials(credentials.getEmail(), doc.getElementsByTagName("email"))) {
                    errors.put("orcid-no-invalid-email", "The email you registered with does not match this ORCID profile");
                }
            }
            if (errors.isEmpty()) {
                apiResponse = new ApiResponse(SUCCESS, fieldValue);
            } else {
                Map<String, Map<String, String>> errorsMap = new HashMap<String, Map<String, String>>();
                errorsMap.put("value", errors);
                apiResponse = new ApiResponse(INVALID, errorsMap);
            }
        }
        return apiResponse;
    }
    
    private boolean tagMatchesCredentials(String credential, NodeList tags) {
        boolean hasMatch = false;
        for (int i = 0; i < tags.getLength(); i++) {
            if (tags.item(i).getTextContent().equals(credential)) {
                hasMatch = true;
            }
        }
        return hasMatch;
    }

    private Document getDocument(String orcid) {
        DocumentBuilder builder = this.getBuilder();
        Document doc = null;
        try {
            doc = builder.parse(ORCID_API.replace("#", orcid));
        } catch (IOException ioex) {
            System.out.println("IO error occurred while verifying ORCID: " + ioex.getMessage());
        } catch (SAXException saxex) {
            System.out.println("IO error occurred while verifying ORCID: " + saxex.getMessage());
        } catch (IllegalArgumentException argex) {
            System.out.println("IO error occurred while verifying ORCID: " + argex.getMessage());
        }
        return doc;
    }

    private DocumentBuilder getBuilder() {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException pcex) {
            System.out.println("IO error occurred while verifying ORCID: " + pcex.getMessage());
        }
        return builder;
    }
}
