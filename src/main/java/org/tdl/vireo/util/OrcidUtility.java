package org.tdl.vireo.util;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;
import static edu.tamu.framework.enums.ApiResponseType.INVALID;

import java.io.IOException;
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
        ApiResponse apiResponse = null;
        if (orcid == "") {
            apiResponse = new ApiResponse(INVALID, "No ORCID");
        } else {
            Document doc = this.getDocument(orcid);
            if (doc == null) {
                apiResponse = new ApiResponse(INVALID, "No document found");
            } else if (doc.getElementsByTagName("orcid-message") == null) {
                apiResponse = new ApiResponse(INVALID, "No document found");
            } else if (!credentials.getFirstName().equals(doc.getElementsByTagName("given-names").item(0).getTextContent())) {
                apiResponse = new ApiResponse(INVALID, "Wrong firstname");
            } else if (!credentials.getLastName().equals(doc.getElementsByTagName("family-name").item(0).getTextContent())) {
                apiResponse = new ApiResponse(INVALID, "Wrong lastname)");
            } else if (!credentials.getEmail().equals(doc.getElementsByTagName("email").item(0).getTextContent())) {
                apiResponse = new ApiResponse(INVALID, "wrongemail");
            } else {
                apiResponse = new ApiResponse(SUCCESS, fieldValue);
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
