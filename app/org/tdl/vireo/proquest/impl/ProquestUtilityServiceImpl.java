package org.tdl.vireo.proquest.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tdl.vireo.model.Attachment;
import org.tdl.vireo.proquest.ProquestLanguage;
import org.tdl.vireo.proquest.ProquestUtilityService;
import org.tdl.vireo.proquest.ProquestVocabularyRepository;

/**
 * Implementation of the proquest utility service to provide a set of helpfull
 * algorithms while generating a proquest export.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 * 
 */
public class ProquestUtilityServiceImpl implements ProquestUtilityService {

	// Spring dependencies
	public ProquestVocabularyRepository proquestRepo = null;
	
	/**
	 * Inject the proquest repository dependency
	 * 
	 * @param proquestRepo
	 *            The proquest vocabulary repository.
	 */
	public void setProquestVocabularyRepository(
			ProquestVocabularyRepository proquestRepo) {
		this.proquestRepo = proquestRepo;
	}
	
	
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

	@Override
	public ProquestLanguage languageCode(String iso1799) {
		
		if (iso1799 == null || iso1799.trim().length() == 0)
			return null;
		
		// Trim out any country or varianets from the iso code.
		if (iso1799.contains("-"))
			iso1799 = iso1799.substring(0, iso1799.indexOf("-"));
		if (iso1799.contains("_"))
			iso1799 = iso1799.substring(0, iso1799.indexOf("_"));
		iso1799 = iso1799.toUpperCase();
		
		
		// First, search by special case mapping
		ProquestLanguage lang = null;
		if (LANG.containsKey(iso1799.toUpperCase()))
			lang = proquestRepo.findLanguageByCode(LANG.get(iso1799));
		
		// Don't implement secondary fall back search because this causes incorrect mappings.
		// if (lang == null)
		//		lang = proquestRepo.findLanguageByCode(iso1799);
		
		return lang;
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
	 * Special mapping from iso's 2 and 3 letter language codes to proquest's
	 * crazy-on-crack language codes that follow no standard what-so-ever.
	 */
	public static final Map<String,String> LANG = new HashMap<String,String>();
	static {
		
		LANG.put("AR","AR");  // Arabic
		LANG.put("ARA","AR"); // Arabic

		LANG.put("CA","CA");  // Catalan
		LANG.put("CAT","CA"); // Catalan

		LANG.put("ZH","CH");  // Chinese
		LANG.put("CHI","CH"); // Chinese
		LANG.put("ZHO","CH"); // Chinese
		
		LANG.put("HR","CR");  // Croatian
		LANG.put("HRV","CR"); // Croatian

		LANG.put("CS","CZ");  // Czech
		LANG.put("CZE","CZ"); // Czech
		LANG.put("CES","CZ"); // Czech
		
		LANG.put("DA","DA");  // Danish
		LANG.put("DAN","DA"); // Danish
		
		LANG.put("NL","DU");  // Dutch
		LANG.put("DUT","DU"); // Dutch
		LANG.put("NLD","DU"); // Dutch

		LANG.put("EN","EN");  // English
		LANG.put("ENG","EN"); // English

		LANG.put("FI","FI");  // Finnish
		LANG.put("FIN","FI"); // Finnish

		LANG.put("VLS","FL"); // Flemish
		
		LANG.put("FR","FR");  // French
		LANG.put("FRE","FR"); // French
		LANG.put("FRA","FR"); // French

		LANG.put("GL","GA");  // Galician
		LANG.put("GLG","GA"); // Galician
		
		LANG.put("DE","GE");  // German
		LANG.put("GER","GE"); // German
		LANG.put("DEU","GE"); // German

		LANG.put("EL","GR");  // Greek
		LANG.put("GRE","GR"); // Greek
		LANG.put("ELL","GR"); // Greek

		LANG.put("HE","HE");  // Hebrew
		LANG.put("IW","HE");  // Hebrew ?
		LANG.put("HEB","HE"); // Hebrew
		
		LANG.put("HU","HU");  // Hungarian
		LANG.put("HUN","HU"); // Hungarian

		LANG.put("IT","IT");  // Italian
		LANG.put("ITA","IT"); // Italian
		
		LANG.put("JA","JA");  // Japanese
		LANG.put("JAN","JA"); // Japanese

		LANG.put("LA","LA");  // Latin
		LANG.put("LAT","LA"); // Latin
		
		LANG.put("NO","NO");  // Norwegian
		LANG.put("NOR","NO"); // Norwegian

		LANG.put("PL","PL");  // Polish
		LANG.put("POL","PL"); // Polish

		LANG.put("PT","PR");  // Portuguese
		LANG.put("POR","PR"); // Portuguese

		LANG.put("RU","RU");  // Russian
		LANG.put("RUS","RU"); // Russian

		LANG.put("ST","SO");  // Sotho
		LANG.put("SOT","SO"); // Sotho

		LANG.put("ES","SP");  // Spanish
		LANG.put("SPA","SP"); // Spanish

		LANG.put("SV","SW");  // Swedish
		LANG.put("SWE","SW"); // Swedish

		LANG.put("TR","TU");  // Turkish
		LANG.put("TUR","TU"); // Turkish

		LANG.put("CY","WE");  // Welsh
		LANG.put("WEL","WE"); // Welsh
		LANG.put("CYM","WE"); // Welsh

		LANG.put("YI","YI");  // Yiddish
		LANG.put("YID","YI"); // Yiddish

		LANG.put("UK","UK");  // Ukrainian
		LANG.put("UKR","UK"); // Ukrainian

		LANG.put("ET","ES");  // Estonian
		LANG.put("EST","ES"); // Estonian

		LANG.put("RO","RO");  // Romanian
		LANG.put("RUM","RO"); // Romanian
		LANG.put("RON","RO"); // Romanian

		LANG.put("KO","KO");  // Korean
		LANG.put("KOR","KO"); // Korean

		LANG.put("EU","BQ");  // Basque
		LANG.put("BAQ","BQ"); // Basque
		LANG.put("EUS","BQ"); // Basque

		LANG.put("GA","IR");  // Irish
		LANG.put("GLE","IR"); // Irish

		LANG.put("LV","LV");  // Latvian
		LANG.put("LAV","LV"); // Latvian

		LANG.put("HAW","HI"); // Hawaiian
		
		LANG.put("SA","SA");  // Sanskrit
		LANG.put("SAN","SA"); // Sanskrit

		LANG.put("JPR","JP"); // Judeo-Persian
		LANG.put("ENM","ME"); // Middle English
		LANG.put("LT","LI");  // Lithuanian
		LANG.put("LIT","LI"); // Lithuanian

		LANG.put("IS","IC");  // Icelandic
		LANG.put("ICE","IC"); // Icelandic
		LANG.put("ISL","IC"); // Icelandic

		LANG.put("ANG","AN"); // Anglo-Saxon
		
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
