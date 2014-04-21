package org.tdl.vireo.services;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.DOMBuilder;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;
import org.mortbay.log.Log;

/**
 * A catch-all class for various Vireo utilities
 * 
 * @author Alexey Maslov
 */
public class Utilities {

	private static final String[] CONTROL_RANGES = {
		"\u0000-\u0009", // CO Control (including: Bell, Backspace, and Horizontal Tab)
		"\u000B-\u000C", // CO Control (Line Tab and Form Feed)
		"\u000E-\u001F", // CO Control (including: Escape)
		"\u007F",        // CO Control (Delete Character)
		"\u0080-\u009F"  // C1 Control
	};

	private static final String ORCID_API = "http://pub.orcid.org/#/orcid-bio";
	private static final Namespace ORCIDns = Namespace.getNamespace("orcid", "http://www.orcid.org/ns/orcid");

	/**
	 * Scrub UNICODE control characters out of the provided string, deleteing them 
	 * @param input
	 * @param replace
	 * @return
	 */
	public static String scrubControl(String input) {
		return scrubControl(input, "");
	}
	
	/**
	 * Scrub UNICODE control characters out of the provided string, replacing them with the specified string 
	 * @param input
	 * @param replace
	 * @return
	 */
	public static String scrubControl(String input, String replace) {
		
		if (input == null)
			return null;
		if ("".equals(input))
			return "";
		
		return input.replaceAll("[" + CONTROL_RANGES[0] + CONTROL_RANGES[1] + 
				CONTROL_RANGES[2] + CONTROL_RANGES[3] + CONTROL_RANGES[4] + "]", replace);
	}	
	
	/**
	 * Given an ORCID identifier, this function will ping the ORCID public API and return
	 * true if a user is found.  
	 * @param orcid id in standard ORCID form "xxxx-xxxx-xxxx-xxxx"
	 * @return true if a user is found matching this id, false in all other cases
	 */
	public static boolean verifyOrcid(String orcid) {
		return verifyOrcid(orcid, null, null);
	}
	
	/**
	 * Given an ORCID identifier, this function will ping the ORCID public API and return
	 * true if a user is found that matches the provided name and id combination.  
	 * @param orcid id in standard ORCID form "xxxx-xxxx-xxxx-xxxx"
	 * @return true if a user is found matching this id and name, false in all other cases
	 */
	public static boolean verifyOrcid(String orcid, String firstName, String lastName) {
		
		try {
			URL orcidURL = new URL(ORCID_API.replace("#", orcid));

			SAXBuilder builder = new SAXBuilder();
			Document doc = builder.build(orcidURL);
			
			List<Namespace> ns = new ArrayList<Namespace>();
			ns.add(ORCIDns);
			
			// Basic sanity check on the document itself
			if (singleXPath(doc, "/orcid:orcid-message", ns) == null) {
				Log.warn("Failed to get an ORCID message");
				return false;
			}
			// Check for the error (orcid not found) message 
			if (singleXPath(doc, "//orcid:error-desc", ns) != null)
			{
				Log.info("Invalid ORCID specified");
				return false;		
			}
			// Validate the name
			Element personalDetails = singleXPath(doc, "//orcid:personal-details", ns);
			
			if (personalDetails != null) {
				// If we are not actually checking the name, we are done
				if (firstName == null || lastName == null) {
					return true;
				}
				// Otherwise, do the comparison
				/* <personal-details>
	                <given-names>Alexey</given-names>
	                <family-name>Maslov</family-name>
	            </personal-details> */			
				else {
					Element orcidFirstName = personalDetails.getChild("given-names", ORCIDns);
					Element orcidLastName = personalDetails.getChild("family-name", ORCIDns);
					
					if (orcidFirstName == null || orcidLastName == null) {
						Log.warn("ORCID response had missing names");
						return false;
					}
					else if (!firstName.equals(orcidFirstName.getTextNormalize()) || 
							!lastName.equals(orcidLastName.getTextNormalize())) {
						Log.warn("ORCID response had wrong name. Expected " + firstName + " " + lastName + ", but got " + 
							orcidFirstName.getTextNormalize() + " " + orcidLastName.getTextNormalize() + ".");
						return false;
					}
					
					// Anything that's made it through the gauntlet, should be fine
					return true;
				}
			}
						
		} catch (MalformedURLException muex) {
			Log.warn("URL error occured while validating ORCID: " + muex.getMessage());
			//muex.printStackTrace();
			return false;
		} catch (JDOMException jdex) {
			Log.warn("JDOM error occured while validating ORCID: " + jdex.getMessage());
			//jdex.printStackTrace();
			return false;
		} catch (IOException ioex) {
			Log.warn("IO error occured while validating ORCID: " + ioex.getMessage());
			//ioex.printStackTrace();
			return false;
		} 
		
		return false;
	}
	
	
	
	/**
	 * Utility method to execute an XPath expression and return back a single result	
	 * @param doc a JDOM document that the XPath is being executed on
	 * @param xpath an XPath expression encoded as a string
	 * @return a JDOM element matching the query or null
	 * @throws JDOMException
	 */
	public static Element singleXPath(Document doc, String xpath) throws JDOMException
	{
		return singleXPath(doc, xpath, null);
	}
	
	public static Element singleXPath(Document doc, String xpath, List<Namespace> namespaces) throws JDOMException 
	{
		XPath xp = XPath.newInstance(xpath);
		
		if (namespaces != null) {
			for (Namespace ns : namespaces)
				xp.addNamespace(ns);
		}
		
		List<Element> results = xp.selectNodes(doc);
		if (results == null || results.size() == 0 || !(results.get(0) instanceof Element))
			return null;
		else
			return results.get(0);
	}
}
