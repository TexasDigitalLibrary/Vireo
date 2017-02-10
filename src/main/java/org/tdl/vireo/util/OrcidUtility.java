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
import org.xml.sax.SAXException;

import edu.tamu.framework.model.ApiResponse;
import edu.tamu.framework.model.Credentials;

@Service
public class OrcidUtility {

    private static final String ORCID_API = "http://pub.orcid.org/#/orcid-bio";

    public ApiResponse verifyOrcid(String orcid, Credentials credentials, FieldValue fieldValue) {
        Map<String, String> errors = new HashMap<String, String>();
        ApiResponse apiResponse = null;
        if (orcid == "") {
            errors.put("orcid-no-orcid", "No ORCID");
        } else {
            Document doc = this.getDocument(orcid);
            if (doc == null) {
                errors.put("orcid-no-document", "No document found");
            } else {
                if (doc.getElementsByTagName("orcid-message") == null) {
                    errors.put("orcid-no-document", "No document found");
                }
                if (!credentials.getFirstName().equals(doc.getElementsByTagName("given-names").item(0).getTextContent())) {
                    errors.put("orcid-invalid-first-name", "Wrong firstname");
                }
                if (!credentials.getLastName().equals(doc.getElementsByTagName("family-name").item(0).getTextContent())) {
                    errors.put("orcid-invalid-last-name", "Wrong lastname");
                }
                if (!credentials.getEmail().equals(doc.getElementsByTagName("email").item(0).getTextContent())) {
                    errors.put("orcid-no-invalid-email", "Wrong email");
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
