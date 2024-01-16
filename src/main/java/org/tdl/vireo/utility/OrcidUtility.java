package org.tdl.vireo.utility;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdl.vireo.model.FieldValue;
import org.tdl.vireo.model.User;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class OrcidUtility {

    private static final Logger logger = LoggerFactory.getLogger(OrcidUtility.class);

    private static final String ORCID_API = "https://pub.orcid.org/v3.0/#/";

    private OrcidUtility() {

    }

    public static Map<String, String> verifyOrcid(User user, FieldValue fieldValue) {
        Map<String, String> errors = new HashMap<String, String>();
        if (fieldValue.getValue() == "") {
            errors.put("orcid-no-orcid", "Field must be a valid ORCID");
        } else {
            Document doc = getDocument(fieldValue.getValue());
            if (doc == null) {
                errors.put("orcid-no-document", "No public profile was found for this ORCID");
            } else {
                if (doc.getElementsByTagName("record") == null) {
                    errors.put("orcid-no-document", "No public profile was found for this ORCID");
                }
                if (!tagMatchesCredentials(user.getFirstName(), doc.getElementsByTagName("personal-details:given-names"))) {
                    errors.put("orcid-invalid-first-name", "The first name you registered with does not match this ORCID profile");
                }
                if (!tagMatchesCredentials(user.getLastName(), doc.getElementsByTagName("personal-details:family-name"))) {
                    errors.put("orcid-invalid-last-name", "The last name you registered with does not match this ORCID profile");
                }
                if (!tagMatchesCredentials(user.getEmail(), doc.getElementsByTagName("email:email"))) {
                    errors.put("orcid-no-invalid-email", "The email you registered with does not match this ORCID profile");
                }
            }
        }
        return errors;
    }

    private static boolean tagMatchesCredentials(String credential, NodeList tags) {
        boolean hasMatch = false;
        for (int i = 0; i < tags.getLength(); i++) {
            if (tags.item(i).getTextContent().equals(credential)) {
                hasMatch = true;
            }
        }
        return hasMatch;
    }

    private static Document getDocument(String orcid) {
        DocumentBuilder builder = getBuilder();
        Document doc = null;
        try {
            doc = builder.parse(ORCID_API.replace("#", orcid));
        } catch (IOException | SAXException | IllegalArgumentException e) {
            String message = "IO error occurred while verifying ORCID";
            logger.error(message + ": " + e.getMessage());
            logger.debug(message, e);
        }
        return doc;
    }

    private static DocumentBuilder getBuilder() {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            String message = "IO error occurred while verifying ORCID";
            logger.error(message + ": " + e.getMessage());
            logger.debug(message, e);
        }
        return builder;
    }

}
