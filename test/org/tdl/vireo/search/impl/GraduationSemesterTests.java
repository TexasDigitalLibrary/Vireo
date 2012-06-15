package org.tdl.vireo.search.impl;

import org.junit.Test;
import org.tdl.vireo.search.GraduationSemester;

import play.test.UnitTest;

/**
 * Test the very simple graduation object. I probably wouldn't have added a test
 * for a class this simple but that I found a bug in the equals method and it
 * was driving me crazy, so I went and created a test for every freaking case.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 * 
 */
public class GraduationSemesterTests extends UnitTest {

	/**
	 * Base case, are they equal.
	 */
	@Test
	public void testEquals() {
		
		GraduationSemester semester1 = new GraduationSemester(2002,05);
		GraduationSemester semester2 = new GraduationSemester(2002,05);
		
		assertTrue(semester1.equals(semester2));
	}
	
	/**
	 * When they differ by just a month.
	 */
	@Test
	public void testNotEqualsByMonth() {
		
		GraduationSemester semester1 = new GraduationSemester(2002,05);
		GraduationSemester semester2 = new GraduationSemester(2002,06);
		
		assertFalse(semester1.equals(semester2));
	}
	
	/**
	 * When the differ by just a year.
	 */
	@Test
	public void testNotEqualsByYear() {
		
		GraduationSemester semester1 = new GraduationSemester(2002,05);
		GraduationSemester semester2 = new GraduationSemester(2003,05);
		
		assertFalse(semester1.equals(semester2));
	}
	
	/**
	 * When both months are null.
	 */
	@Test
	public void testEqualsWithNullMonth() {
		
		GraduationSemester semester1 = new GraduationSemester(2002,null);
		GraduationSemester semester2 = new GraduationSemester(2002,null);
		
		assertTrue(semester1.equals(semester2));
	}
	
	/**
	 * When both years are null.
	 */
	@Test
	public void testEqualsWithNullYear() {
		
		GraduationSemester semester1 = new GraduationSemester(null,05);
		GraduationSemester semester2 = new GraduationSemester(null,05);
		
		assertTrue(semester1.equals(semester2));
	}
	
	/**
	 * When years are different with null months.
	 */
	@Test
	public void testNotEqualsEqualsWithNullMonth() {
		
		GraduationSemester semester1 = new GraduationSemester(2002,null);
		GraduationSemester semester2 = new GraduationSemester(2003,null);
		
		assertFalse(semester1.equals(semester2));
	}
	
	/**
	 * When months are different with null years.
	 */
	@Test
	public void testNotEqualsEqualsWithNullYear() {
		
		GraduationSemester semester1 = new GraduationSemester(null,06);
		GraduationSemester semester2 = new GraduationSemester(null,05);
		
		assertFalse(semester1.equals(semester2));
	}
	
	/**
	 * When there's mixed null and real months.
	 */
	@Test
	public void testNotEqualsEqualsWithMixedMonth() {
		
		GraduationSemester semester1 = new GraduationSemester(2002,05);
		GraduationSemester semester2 = new GraduationSemester(2003,null);
		
		assertFalse(semester1.equals(semester2));
	}
	
	/**
	 * When there's mixed null and real years.
	 */
	@Test
	public void testNotEqualsEqualsWithMixedYear() {
		
		GraduationSemester semester1 = new GraduationSemester(null,05);
		GraduationSemester semester2 = new GraduationSemester(2003,05);
		
		assertFalse(semester1.equals(semester2));
	}
	
	
	
}
