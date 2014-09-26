package org.tdl.vireo.services;

import org.tdl.vireo.services.Utilities;

import org.junit.Test;

import play.test.UnitTest;

/**
 * Test Utilities.
 * 
 * @author Micah Cooper
 *
 */
public class UtilitiesTest extends UnitTest {

	/**
	 * Test validating orcids.
	 */
	@Test
	public void testValidatingOrcid() {
		String badOrcid = "This is bad.";
		String almostOrcid = "abcd-efgh-ijkl-mno9";
		String goodOrcid = "1234-5678-1234-567X";
		
		assertFalse(Utilities.validateOrcidFormat(badOrcid));
		assertFalse(Utilities.validateOrcidFormat(almostOrcid));
		assertTrue(Utilities.validateOrcidFormat(goodOrcid));
	}
	
	/**
	 * Test formatting orcids from various inputs.
	 */
	@Test
	public void testFormattingOrcidToDashedId() {
		String urlOrcid = "http://www.orcid.org/1234-5678-1234-567X/";
		String sslOrcid = "https://www.orcid.org/1234-5678-1234-567X/";
		String shortUrlOrcid = "www.orcid.org/1234-5678-1234-567X";
		String shorterUrlOrcid = "orcid.org/1234-5678-1234-567X";
		String condensedUrlOrcid = "www.orcid.org/123456781234567X";
		String condensedOrcid = "123456781234567X";
		String badOrcid = "I/ am not www an orcid.";
		
		assertEquals("1234-5678-1234-567X", Utilities.formatOrcidAsDashedId(urlOrcid));
		assertEquals("1234-5678-1234-567X", Utilities.formatOrcidAsDashedId(sslOrcid));
		assertEquals("1234-5678-1234-567X", Utilities.formatOrcidAsDashedId(shortUrlOrcid));
		assertEquals("1234-5678-1234-567X", Utilities.formatOrcidAsDashedId(shorterUrlOrcid));
		assertEquals("1234-5678-1234-567X", Utilities.formatOrcidAsDashedId(condensedUrlOrcid));
		assertEquals("1234-5678-1234-567X", Utilities.formatOrcidAsDashedId(condensedOrcid));
		assertEquals("I/ am not www an orcid.", Utilities.formatOrcidAsDashedId(badOrcid));
	}
}
