package org.tdl.vireo.proquest.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tdl.vireo.model.Attachment;
import org.tdl.vireo.proquest.ProquestUtilityService;

/**
 * Implementation of the proquest utility service to provide a set of helpfull
 * algorithms while generating a proquest export.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 * 
 */
public class ProquestUtilityServiceImpl implements ProquestUtilityService {

	@Override
	public Phone parsePhone(final String fullPhone) {

		// The phone parts that we are trying extract
		String cntryCode = null;
		String areaCode  = null;
		String number    = null;
		String ext       = null;

		// Give it our best shot
		Pattern pattern = Pattern.compile(
				"^(?:[\\+]?[\\(]?([\\d]{1,3})[\\s\\-\\.\\)]+)?" +  // cntry_code (optional)
				"(?:[\\(]?([\\d]{1,3})[\\s\\-/\\)]+)"           +  // area_code
				"([2-9][0-9\\s\\-\\.]{6,}[0-9])"                +  // number
				"(?:[\\s\\D]+([\\d]{1,5}))?$"					   // ext (optional
				);

		Matcher matcher = pattern.matcher(fullPhone);

		if(matcher.matches()) {
			cntryCode = matcher.group(1);
			areaCode  = matcher.group(2);
			number     = matcher.group(3);
			ext        = matcher.group(4);
		} else {
			String trimPhone = fullPhone.replaceAll("[^0-9]","");
			if (trimPhone.length() > 7) {
				// The phone number is long enough to have an area code, split that off.
				areaCode = trimPhone.substring(0,trimPhone.length() - 7);
				number = trimPhone.substring(trimPhone.length() - 7, trimPhone.length());
			} else {
				// We don't have an area code, so the whole things goes into phone
				number = trimPhone;
			}
		}

		return new Phone(fullPhone, cntryCode, areaCode, number, ext);
	}

	@Override
	public Address parseAddress(final String fullAddress) {

		// The address parts that we are trying to extract
		String addrline = null;
		String city     = null;
		String state    = null;
		String zip      = null;
		String cntry    = null;

		// Extract the address
		String oneLineAddress = fullAddress.replaceAll("\r\n"," ");
		oneLineAddress = oneLineAddress.replaceAll("\n"," ");

		// Case Insensitive
		// This does a <em>good enough</em> job of parsing, but is often incorrect
		Pattern pattern = Pattern.compile(
				"(.+(?:(?:STREET|ST|DRIVE|DR|AVENUE|AVE|AV|ROAD|RD|LOOP|COURT|CT|CIRCLE|CR|TERRANCE|TERR|HIGHWAY|HWY|PARKWAY|PRKY|PLACE|PL|WAY|RIDGE|RDG|LANE|LN|BOULEVARD|BLVD)\\.?)(?:(?:,?\\s)(?:APT|UNIT|#)[\\.,]?\\s\\w+)?)(?:,?)" +
												// address
				"(?:\\s)([a-zA-Z\\s]+),?"  +	// city
				"(?:\\s)([a-zA-Z\\s]{2,})" +	// state
				"(?:\\s)([\\d]+[\\-\\d]+)" +	// zip
				"((?:\\s)([\\s\\D]+))?",		// country
				Pattern.CASE_INSENSITIVE);


		Matcher matcher = pattern.matcher(oneLineAddress);

		if (matcher.matches()) {
			addrline = matcher.group(1);
			city     = matcher.group(2);
			state    = matcher.group(3);
			zip      = matcher.group(4);
			cntry    = matcher.group(5);
		} else {
			addrline = fullAddress;  // if it fails, put everything in addrline
		}

		return new Address(fullAddress, addrline, city, state, zip, cntry);
	}

	/**
	 * 
	 * ProQuest Categories:
	 * 
	 * audio
	 * code/script
	 * image
	 * pdf
	 * presentation
	 * spreadsheet
	 * text
	 * video
	 * webpage
	 * data
	 * other
	 */
	@Override
	public String categorize(Attachment attachment) {


		String mimeType = attachment.getMimeType();
		String category = "other";

		// Start with the simple stuff first
		if (mimeType.startsWith("audio/"))
			category = "audio";

		if (mimeType.startsWith("image/"))
			category = "image";
		
		if (mimeType.startsWith("text/"))
			category = "text";
		
		if (mimeType.startsWith("video/"))
			category = "video";
		
		if (CAT.containsKey(mimeType))
			category = CAT.get(mimeType);

		return category;
	}
	
	
	/**
	 * A mapping of individual mimetypes to categories. We do not need to
	 * including a mapping in the mimetype is part of the category, like a
	 * mapping for text/plain to "text" because it starts with an identifiable
	 * prefix.
	 */
	public static final Map<String,String> CAT = new HashMap<String,String>();
	static {
		CAT.put("application/word", "text");
		CAT.put("application/msword", "text");
		CAT.put("application/x-latex", "text");
		CAT.put("application/postscript", "text");
		CAT.put("application/x-tex", "text");
		CAT.put("application/vnd.openxmlformats-officedocument.wordprocessingml.document", "text");
		
		CAT.put("application/postscript", "image");
		
		CAT.put("text/html", "webpage");
		CAT.put("text/xhtml", "webpage");
		CAT.put("text/css", "webpage");
		CAT.put("text/javascript", "webpage");
		CAT.put("application/x-javascript", "webpage");
		CAT.put("application/xhtml", "webpage");
		CAT.put("application/xhtml+xml", "webpage");
		
		CAT.put("text/xml", "data");
		CAT.put("application/excel", "data");
		CAT.put("application/msexcel", "data");
		CAT.put("application/vnd.ms-excel", "data");
		CAT.put("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "data");
		CAT.put("application/vnd.openxmlformats-officedocument.spreadsheetml.template", "data");
		
		CAT.put("application/powerpoint", "presentation");		
		CAT.put("application/ms-powerpoint", "presentation");
		CAT.put("application/vnd.ms-powerpoint", "presentation");
		CAT.put("application/vnd.openxmlformats-officedocument.presentationml.presentation", "presentation");

		CAT.put("text/xslt", "code/script");
		
		CAT.put("application/pdf", "pdf");
		CAT.put("application/x-pdf", "pdf");
		CAT.put("application/acrobat", "pdf");
		CAT.put("applications/vnd.pdf", "pdf");
		CAT.put("text/pdf", "pdf");
		CAT.put("text/x-pdf", "pdf");
	}
	
	
	
	
	
	
	
	

}
