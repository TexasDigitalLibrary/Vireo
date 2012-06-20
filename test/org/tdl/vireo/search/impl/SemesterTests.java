package org.tdl.vireo.search.impl;

import org.junit.Test;
import org.tdl.vireo.search.Semester;

import play.test.UnitTest;

/**
 * Test the very simple graduation object. I probably wouldn't have added a test
 * for a class this simple but that I found a bug in the equals method and it
 * was driving me crazy, so I went and created a test for every freaking case.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 * 
 */
public class SemesterTests extends UnitTest {

	/**
	 * Base case, are they equal.
	 */
	@Test
	public void testEquals() {
		
		Semester semester1 = new Semester(2002,05);
		Semester semester2 = new Semester(2002,05);
		
		assertTrue(semester1.equals(semester2));
	}
	
	/**
	 * When they differ by just a month.
	 */
	@Test
	public void testNotEqualsByMonth() {
		
		Semester semester1 = new Semester(2002,05);
		Semester semester2 = new Semester(2002,06);
		
		assertFalse(semester1.equals(semester2));
	}
	
	/**
	 * When the differ by just a year.
	 */
	@Test
	public void testNotEqualsByYear() {
		
		Semester semester1 = new Semester(2002,05);
		Semester semester2 = new Semester(2003,05);
		
		assertFalse(semester1.equals(semester2));
	}
	
	/**
	 * When both months are null.
	 */
	@Test
	public void testEqualsWithNullMonth() {
		
		Semester semester1 = new Semester(2002,null);
		Semester semester2 = new Semester(2002,null);
		
		assertTrue(semester1.equals(semester2));
	}
	
	/**
	 * When both years are null.
	 */
	@Test
	public void testEqualsWithNullYear() {
		
		Semester semester1 = new Semester(null,05);
		Semester semester2 = new Semester(null,05);
		
		assertTrue(semester1.equals(semester2));
	}
	
	/**
	 * When years are different with null months.
	 */
	@Test
	public void testNotEqualsEqualsWithNullMonth() {
		
		Semester semester1 = new Semester(2002,null);
		Semester semester2 = new Semester(2003,null);
		
		assertFalse(semester1.equals(semester2));
	}
	
	/**
	 * When months are different with null years.
	 */
	@Test
	public void testNotEqualsEqualsWithNullYear() {
		
		Semester semester1 = new Semester(null,06);
		Semester semester2 = new Semester(null,05);
		
		assertFalse(semester1.equals(semester2));
	}
	
	/**
	 * When there's mixed null and real months.
	 */
	@Test
	public void testNotEqualsEqualsWithMixedMonth() {
		
		Semester semester1 = new Semester(2002,05);
		Semester semester2 = new Semester(2003,null);
		
		assertFalse(semester1.equals(semester2));
	}
	
	/**
	 * When there's mixed null and real years.
	 */
	@Test
	public void testNotEqualsEqualsWithMixedYear() {
		
		Semester semester1 = new Semester(null,05);
		Semester semester2 = new Semester(2003,05);
		
		assertFalse(semester1.equals(semester2));
	}
	
	
	
}
