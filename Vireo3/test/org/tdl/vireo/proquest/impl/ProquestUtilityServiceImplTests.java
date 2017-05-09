package org.tdl.vireo.proquest.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.LocaleUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.Resource;
import org.tdl.vireo.model.MockAttachment;
import org.tdl.vireo.proquest.ProquestUtilityService;
import org.tdl.vireo.proquest.ProquestUtilityService.Address;
import org.tdl.vireo.proquest.ProquestUtilityService.Phone;
import org.tdl.vireo.proquest.ProquestLanguage;
import org.tdl.vireo.proquest.ProquestSubject;
import org.tdl.vireo.proquest.ProquestVocabularyRepository;

import play.modules.spring.Spring;
import play.test.UnitTest;

/**
 * Test the proquest utility service.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 *
 */
public class ProquestUtilityServiceImplTests extends UnitTest {
	
	// The service to test
	public static final ProquestUtilityService service = Spring.getBeanOfType(ProquestUtilityService.class);
	
	/**
	 * Test the mapping of attachment mimetypes to proquest categories.
	 */
	@Test
	public void testCategories() {
		
		assertEquals("text",service.categorize(new MockMimeAttachment("text/plain")));
		assertEquals("text",service.categorize(new MockMimeAttachment("application/msword")));
		assertEquals("image",service.categorize(new MockMimeAttachment("image/png")));
		assertEquals("audio",service.categorize(new MockMimeAttachment("audio/wave")));
		assertEquals("data",service.categorize(new MockMimeAttachment("text/xml")));
		assertEquals("data",service.categorize(new MockMimeAttachment("application/msexcel")));
		assertEquals("pdf",service.categorize(new MockMimeAttachment("text/pdf")));
		assertEquals("other",service.categorize(new MockMimeAttachment("??????")));
		assertEquals("other",service.categorize(new MockMimeAttachment("")));
	}
	
	/**
	 * Test mapping iso1799 language codes into proquest's language codes.
	 */
	@Test
	public void testLanguageCode() {
		
		assertEquals("EN",service.languageCode(LocaleUtils.toLocale("en")).getCode());
		assertEquals("FR",service.languageCode(LocaleUtils.toLocale("fr")).getCode());
		assertEquals("EN",service.languageCode(LocaleUtils.toLocale("en_US")).getCode());
		assertEquals(null,service.languageCode(null));
	}
	
	/**
	 * Test mapping iso1799 language codes into proquest's language codes.
	 */
	@Test
	public void testDegreeCode() {
		
		assertEquals("Ph.D.",service.degreeCode("Doctor of Philosophy"));
		assertEquals("This Does Not Exist",service.degreeCode("This Does Not Exist"));
		assertEquals(null, service.degreeCode(null));
	}
	
	/**
	 * Test parsing phone numbers
	 */
	@Test
	public void testParsePhone() {
		
		// All components
		Phone phone = service.parsePhone("+1-979-555-4921 ext 123");
		
		assertEquals("+1-979-555-4921 ext 123",phone.fullPhone);
		assertEquals("1",phone.cntryCode);
		assertEquals("979",phone.areaCode);
		assertEquals("555-4921",phone.number);
		assertEquals("123",phone.ext);
		
		// Minimal components
		phone = service.parsePhone("979-555-4921");
		
		assertEquals("979-555-4921",phone.fullPhone);
		assertEquals(null,phone.cntryCode);
		assertEquals("979",phone.areaCode);
		assertEquals("555-4921",phone.number);
		assertEquals(null,phone.ext);
		
		// Minimal plus extension
		phone = service.parsePhone("979-555-4921 ext 123");
		
		assertEquals("979-555-4921 ext 123",phone.fullPhone);
		assertEquals(null,phone.cntryCode);
		assertEquals("979",phone.areaCode);
		assertEquals("555-4921",phone.number);
		assertEquals("123",phone.ext);
		
		// Minimal pluss country
		phone = service.parsePhone("+1-979-555-4921");
		
		assertEquals("+1-979-555-4921",phone.fullPhone);
		assertEquals("1",phone.cntryCode);
		assertEquals("979",phone.areaCode);
		assertEquals("555-4921",phone.number);
		assertEquals(null,phone.ext);
		
	}
	
	/**
	 * Test parse postal address
	 */
	@Test
	public void testParseAddress() {
		// Full address
		Address address = service.parseAddress("4606 Baron Creek Dr APT 11\nSomewhere TX 77845 USA");
		
		assertEquals("4606 Baron Creek Dr APT 11\nSomewhere TX 77845 USA",address.fullAddress);
		assertEquals("4606 Baron Creek Dr APT 11",address.addrline);
		assertEquals("Somewhere",address.city);
		assertEquals("TX",address.state);
		assertEquals("77845",address.zip);
		assertEquals("USA",address.cntry);
		
		// Without country
		address = service.parseAddress("4606 Baron Creek Dr APT 11\nSomewhere TX 77845");
		
		assertEquals("4606 Baron Creek Dr APT 11\nSomewhere TX 77845",address.fullAddress);
		assertEquals("4606 Baron Creek Dr APT 11",address.addrline);
		assertEquals("Somewhere",address.city);
		assertEquals("TX",address.state);
		assertEquals("77845",address.zip);
		assertEquals(null,address.cntry);
		
		// Without apartment address
		address = service.parseAddress("4606 Baron Creek Dr\nSomewhere TX 77845 USA");
		
		assertEquals("4606 Baron Creek Dr\nSomewhere TX 77845 USA",address.fullAddress);
		assertEquals("4606 Baron Creek Dr",address.addrline);
		assertEquals("Somewhere",address.city);
		assertEquals("TX",address.state);
		assertEquals("77845",address.zip);
		assertEquals("USA",address.cntry);
		
		// On one line
		address = service.parseAddress("4606 Baron Creek Dr APT 11 Somewhere TX 77845 USA");
		
		assertEquals("4606 Baron Creek Dr APT 11 Somewhere TX 77845 USA",address.fullAddress);
		assertEquals("4606 Baron Creek Dr APT 11",address.addrline);
		assertEquals("Somewhere",address.city);
		assertEquals("TX",address.state);
		assertEquals("77845",address.zip);
		assertEquals("USA",address.cntry);
		
	}
	
	/**
	 * Test parsing real postal address from texas
	 */
	@Test
	public void testRealTexasAddresses() {
		
		Address address = service.parseAddress("8401 Spring Creek\nCollege Station, TX 77845");
		
		assertEquals("8401 Spring Creek\nCollege Station, TX 77845",address.fullAddress);
		assertEquals("8401 Spring Creek",address.addrline);
		assertEquals("College Station",address.city);
		assertEquals("TX",address.state);
		assertEquals("77845",address.zip);
		assertEquals(null,address.cntry);
		
		address = service.parseAddress("301 Holleman Dr East Apt 1113\nCollege Station TX, 77840");
		
		assertEquals("301 Holleman Dr East Apt 1113\nCollege Station TX, 77840",address.fullAddress);
		assertEquals("301 Holleman Dr East Apt 1113",address.addrline);
		assertEquals("College Station",address.city);
		assertEquals("TX",address.state);
		assertEquals("77840",address.zip);
		assertEquals(null,address.cntry);
		
		address = service.parseAddress("2900 Durango Court\nCollege Station, TX  77845");
		
		assertEquals("2900 Durango Court\nCollege Station, TX  77845",address.fullAddress);
		assertEquals("2900 Durango Court",address.addrline);
		assertEquals("College Station",address.city);
		assertEquals("TX",address.state);
		assertEquals("77845",address.zip);
		assertEquals(null,address.cntry);
		
		address = service.parseAddress("1700 George Bush Dr. Apt. 205\nCollege Station, Texas 77840");
		
		assertEquals("1700 George Bush Dr. Apt. 205\nCollege Station, Texas 77840",address.fullAddress);
		assertEquals("1700 George Bush Dr. Apt. 205",address.addrline);
		assertEquals("College Station",address.city);
		assertEquals("Texas",address.state);
		assertEquals("77840",address.zip);
		assertEquals(null,address.cntry);
		
		address = service.parseAddress("6202 Roxbury Drive, Apt 10201,\nSan Antonio, TX\n78238");
		
		assertEquals("6202 Roxbury Drive, Apt 10201,\nSan Antonio, TX\n78238",address.fullAddress);
		assertEquals("6202 Roxbury Drive, Apt 10201",address.addrline);
		assertEquals("San Antonio",address.city);
		assertEquals("TX",address.state);
		assertEquals("78238",address.zip);
		assertEquals(null,address.cntry);
		
		address = service.parseAddress("1000 University Dr.E. #102\nCollege Station, TX, 77840");
		
		assertEquals("1000 University Dr.E. #102\nCollege Station, TX, 77840",address.fullAddress);
		assertEquals("1000 University Dr.E. #102",address.addrline);
		assertEquals("College Station",address.city);
		assertEquals("TX",address.state);
		assertEquals("77840",address.zip);
		assertEquals(null,address.cntry);
		
		address = service.parseAddress("1001 Harvey Road, Apt 89, College Station, Texas 77840");
		
		assertEquals("1001 Harvey Road, Apt 89, College Station, Texas 77840",address.fullAddress);
		assertEquals("1001 Harvey Road, Apt 89",address.addrline);
		assertEquals("College Station",address.city);
		assertEquals("Texas",address.state);
		assertEquals("77840",address.zip);
		assertEquals(null,address.cntry);
		
		address = service.parseAddress("3902 College Main St.\nApt 1410\nBryan, TX 77801");
		
		assertEquals("3902 College Main St.\nApt 1410\nBryan, TX 77801",address.fullAddress);
		assertEquals("3902 College Main St.\nApt 1410",address.addrline);
		assertEquals("Bryan",address.city);
		assertEquals("TX",address.state);
		assertEquals("77801",address.zip);
		assertEquals(null,address.cntry);
		
		// This one is not great because "College" shouldn't be in the street but the input is ambiguous so this is the best we can do.
		address = service.parseAddress("306 Redmond Drive Apt. 1107 College Station, TX 77840");
		
		assertEquals("306 Redmond Drive Apt. 1107 College Station, TX 77840",address.fullAddress);
		assertEquals("306 Redmond Drive Apt. 1107 College",address.addrline);
		assertEquals("Station",address.city);
		assertEquals("TX",address.state);
		assertEquals("77840",address.zip);
		assertEquals(null,address.cntry);
		
		address = service.parseAddress("301 Holleman E APT 836\nCollege Station, TX 77840");
		
		assertEquals("301 Holleman E APT 836\nCollege Station, TX 77840",address.fullAddress);
		assertEquals("301 Holleman E APT 836",address.addrline);
		assertEquals("College Station",address.city);
		assertEquals("TX",address.state);
		assertEquals("77840",address.zip);
		assertEquals(null,address.cntry);
		
		address = service.parseAddress("2291 FM 2705\nMexia, TX 76667");
		
		assertEquals("2291 FM 2705\nMexia, TX 76667",address.fullAddress);
		assertEquals("2291 FM 2705",address.addrline);
		assertEquals("Mexia",address.city);
		assertEquals("TX",address.state);
		assertEquals("76667",address.zip);
		assertEquals(null,address.cntry);
		
		address = service.parseAddress("5213 Bloomsbury Way\nBryan, TX 77802");
		
		assertEquals("5213 Bloomsbury Way\nBryan, TX 77802",address.fullAddress);
		assertEquals("5213 Bloomsbury Way",address.addrline);
		assertEquals("Bryan",address.city);
		assertEquals("TX",address.state);
		assertEquals("77802",address.zip);
		assertEquals(null,address.cntry);
		
		address = service.parseAddress("3911 Hawk Owl Cove\nCollege Station, TX 77845");
		
		assertEquals("3911 Hawk Owl Cove\nCollege Station, TX 77845",address.fullAddress);
		assertEquals("3911 Hawk Owl Cove",address.addrline);
		assertEquals("College Station",address.city);
		assertEquals("TX",address.state);
		assertEquals("77845",address.zip);
		assertEquals(null,address.cntry);
		
		address = service.parseAddress("2881 F&B Road\nCollege Station\nTX 77845");
		
		assertEquals("2881 F&B Road\nCollege Station\nTX 77845",address.fullAddress);
		assertEquals("2881 F&B Road",address.addrline);
		assertEquals("College Station",address.city);
		assertEquals("TX",address.state);
		assertEquals("77845",address.zip);
		assertEquals(null,address.cntry);
		
		address = service.parseAddress("C/O Dr. Joan Mileski\nMaritime Administration Department\nTexas A&M at Galveston\nPO Box 1675\nGalveston, TX 77554");
		
		assertEquals("C/O Dr. Joan Mileski\nMaritime Administration Department\nTexas A&M at Galveston\nPO Box 1675\nGalveston, TX 77554",address.fullAddress);
		assertEquals("C/O Dr. Joan Mileski\nMaritime Administration Department\nTexas A&M at Galveston\nPO Box 1675",address.addrline);
		assertEquals("Galveston",address.city);
		assertEquals("TX",address.state);
		assertEquals("77554",address.zip);
		assertEquals(null,address.cntry);
		
		address = service.parseAddress("PO Box 442, Meeker, TX 81641");
		
		assertEquals("PO Box 442, Meeker, TX 81641",address.fullAddress);
		assertEquals("PO Box 442",address.addrline);
		assertEquals("Meeker",address.city);
		assertEquals("TX",address.state);
		assertEquals("81641",address.zip);
		assertEquals(null,address.cntry);
		
		address = service.parseAddress("Dept. of Computer Science\nTexas A&M University\nTAMU 3112\nCollege Station, TX 77843-3112");
		
		assertEquals("Dept. of Computer Science\nTexas A&M University\nTAMU 3112\nCollege Station, TX 77843-3112",address.fullAddress);
		assertEquals("Dept. of Computer Science\nTexas A&M University\nTAMU 3112",address.addrline);
		assertEquals("College Station",address.city);
		assertEquals("TX",address.state);
		assertEquals("77843-3112",address.zip);
		assertEquals(null,address.cntry);
		
		address = service.parseAddress("1100 Hensel Dr. Apt:Z1H\nCollege Station, Texas, 77840\nUnited States");
		
		assertEquals("1100 Hensel Dr. Apt:Z1H\nCollege Station, Texas, 77840\nUnited States",address.fullAddress);
		assertEquals("1100 Hensel Dr. Apt:Z1H",address.addrline);
		assertEquals("College Station",address.city);
		assertEquals("Texas",address.state);
		assertEquals("77840",address.zip);
		assertEquals("United States",address.cntry);
		
	}
	
	/**
	 * Test parsing real international postal address
	 */
	@Test
	public void testRealInternationalAddresses() {
		
		
		// Okay, so mia-culpa: some of these tests aren't technicaly correct. But this is a hard problem, so they are testing close enough.
		
		
		Address address = service.parseAddress("4F No14 Aly6 Ln123 Sec 5, Nan-King E Rd, Taipei City, Taiwan, 10570");
		
		assertEquals("4F No14 Aly6 Ln123 Sec 5, Nan-King E Rd, Taipei City, Taiwan, 10570",address.fullAddress);
		assertEquals("4F No14 Aly6 Ln123 Sec 5, Nan-King E Rd",address.addrline);
		assertEquals("Taipei City",address.city);
		assertEquals("Taiwan",address.state);
		assertEquals("10570",address.zip);
		assertEquals(null,address.cntry);
		
		address = service.parseAddress("Ahornweg 45, Spiegel b. Bern 3095\nSwitzerland");
		
		assertEquals("Ahornweg 45, Spiegel b. Bern 3095\nSwitzerland",address.fullAddress);
		assertEquals("Ahornweg 45",address.addrline);
		assertEquals("Spiegel b.",address.city);
		assertEquals("Bern",address.state);
		assertEquals("3095",address.zip);
		assertEquals("Switzerland",address.cntry);
		
		// Get's the country wrong...
		address = service.parseAddress("STAIN Salatiga\nJl. Tentara Pelajar No. 02\nSalatiga, Jawa Tengah, Indoneisa 50721");
		
		assertEquals("STAIN Salatiga\nJl. Tentara Pelajar No. 02\nSalatiga, Jawa Tengah, Indoneisa 50721",address.fullAddress);
		assertEquals("STAIN Salatiga\nJl. Tentara Pelajar No. 02\nSalatiga",address.addrline);
		assertEquals("Jawa Tengah",address.city);
		assertEquals("Indoneisa",address.state);
		assertEquals("50721",address.zip);
		assertEquals(null,address.cntry);
		
		address = service.parseAddress("43-89, N.R.Peta, \nKurnool, AP 518004\nINDIA");
		
		assertEquals("43-89, N.R.Peta, \nKurnool, AP 518004\nINDIA",address.fullAddress);
		assertEquals("43-89, N.R.Peta",address.addrline);
		assertEquals("Kurnool",address.city);
		assertEquals("AP",address.state);
		assertEquals("518004",address.zip);
		assertEquals("INDIA",address.cntry);
		
		address = service.parseAddress("20, Hill Road,\nGandhi Nagar,\nNagpur - 440010\nIndia");
		
		assertEquals("20, Hill Road,\nGandhi Nagar,\nNagpur - 440010\nIndia",address.fullAddress);
		assertEquals("20, Hill Road",address.addrline);
		assertEquals("Gandhi Nagar",address.city);
		assertEquals("Nagpur -",address.state);
		assertEquals("440010",address.zip);
		assertEquals("India",address.cntry);
		
		// Get's the country wrong...
		address = service.parseAddress("Behind Kiran Jyoti School, Indira Nagar, Rewa (M.P.)\n486001");
		
		assertEquals("Behind Kiran Jyoti School, Indira Nagar, Rewa (M.P.)\n486001",address.fullAddress);
		assertEquals("Behind Kiran Jyoti School",address.addrline);
		assertEquals("Indira Nagar",address.city);
		assertEquals("Rewa (M.P.)",address.state);
		assertEquals("486001",address.zip);
		assertEquals(null,address.cntry);
		
		// Get's the country wrong...
		address = service.parseAddress("119-2-10 Xiangyang Building, Hongqing Street, Xi'an City, Shaanxi Province, P.R.China, 710025");
		
		assertEquals("119-2-10 Xiangyang Building, Hongqing Street, Xi'an City, Shaanxi Province, P.R.China, 710025",address.fullAddress);
		assertEquals("119-2-10 Xiangyang Building, Hongqing Street, Xi'an City",address.addrline);
		assertEquals("Shaanxi Province",address.city);
		assertEquals("P.R.China",address.state);
		assertEquals("710025",address.zip);
		assertEquals(null,address.cntry);
		
		address = service.parseAddress("No. 8 Dahuisi Road, Haidian District,building 6, 3-602\n BEIJING, 100081, China");
		
		assertEquals("No. 8 Dahuisi Road, Haidian District,building 6, 3-602\n BEIJING, 100081, China",address.fullAddress);
		assertEquals("No. 8 Dahuisi Road, Haidian District,building 6",address.addrline);
		assertEquals("3-602",address.city);
		assertEquals("BEIJING",address.state);
		assertEquals("100081",address.zip);
		assertEquals("China",address.cntry);
		
		address = service.parseAddress("35,(CHARU-KRIPA), \n7TH CROSS,8TH MAIN, \nG.M. PALYA (NORTH EXTN,\nNEW THIPPASANDRA P.O.,\nBANGALORE 560075, INDIA");
		
		assertEquals("35,(CHARU-KRIPA), \n7TH CROSS,8TH MAIN, \nG.M. PALYA (NORTH EXTN,\nNEW THIPPASANDRA P.O.,\nBANGALORE 560075, INDIA",address.fullAddress);
		assertEquals("35,(CHARU-KRIPA), \n7TH CROSS,8TH MAIN, \nG.M. PALYA (NORTH EXTN",address.addrline);
		assertEquals("NEW THIPPASANDRA P.O.",address.city);
		assertEquals("BANGALORE",address.state);
		assertEquals("560075",address.zip);
		assertEquals("INDIA",address.cntry);
	}

	/** 
	 * Simple extension to the mock attachment object to set the mimeType in the constructor.
	 */
	public static class MockMimeAttachment extends MockAttachment {
		public MockMimeAttachment(String mimeType) {
			this.mimeType = mimeType;
		}
	}
}
