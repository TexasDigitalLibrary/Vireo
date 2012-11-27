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
		assertEquals(" USA",address.cntry);
		
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
		assertEquals(" USA",address.cntry);
		
		// On one line
		address = service.parseAddress("4606 Baron Creek Dr APT 11 Somewhere TX 77845 USA");
		
		assertEquals("4606 Baron Creek Dr APT 11 Somewhere TX 77845 USA",address.fullAddress);
		assertEquals("4606 Baron Creek Dr APT 11",address.addrline);
		assertEquals("Somewhere",address.city);
		assertEquals("TX",address.state);
		assertEquals("77845",address.zip);
		assertEquals(" USA",address.cntry);
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
