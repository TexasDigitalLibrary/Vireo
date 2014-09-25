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
		String goodOrcid = "1234-5678-1234-567x";
		
		assertFalse(Utilities.validateOrcidFormat(badOrcid));
		assertFalse(Utilities.validateOrcidFormat(almostOrcid));
		assertTrue(Utilities.validateOrcidFormat(goodOrcid));
	}
}
