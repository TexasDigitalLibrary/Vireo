package org.tdl.vireo.services;


/**
 * A catch-all class for various Vireo utilities
 * 
 * @author Alexey Maslov
 */
public class Utilities {

	private static final String[] CONTROL_RANGES = {
		"\u0000-\u0009", // CO Control (including: Bell, Backspace, and Horizontal Tab)
		"\u000E-\u001F", // CO Control (including: Escape)
		"\u007F",        // CO Control (Delete Character)
		"\u0080-\u009F"  // C1 Control
	};
	
	public static String scrubControl(String input, String replace) {
		
		if (input == null)
			return null;
		if ("".equals(input))
			return "";
		
		return input.replaceAll("[" + CONTROL_RANGES[0] + CONTROL_RANGES[1] + 
				CONTROL_RANGES[2] + CONTROL_RANGES[3] + "]", replace);
	}
	
}
