package org.tdl.vireo.model;

import java.util.HashMap;
import java.util.Map;

/**
 * A system wide configuration for vireo.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public interface Configuration extends AbstractModel {

	/**
	 * @return The name of the configuration.
	 */
	public String getName();

	/**
	 * @param name
	 *            The new name of the configuration.
	 */
	public void setName(String name);

	/**
	 * @return The value of the configuration
	 */
	public String getValue();

	/**
	 * @param value
	 *            The new value of the configuration.
	 */
	public void setValue(String value);
	
	
	/**
	 * Configuration defaults
	 * 
	 * Any component may register system-wide defaults for configuration
	 * parameters. Whenever a call to settingRepo.getConfigValue(name) is
	 * called, if that parameter is not defined then the value registered with
	 * DEFAULTS is returned.
	 */
	public static class DEFAULTS {

		// The singleton DEFAULTS map.
		protected static final Map<String, String> singleton = new HashMap<String,String>();

		private DEFAULTS() {
			/** We're a singleton instance **/
		}

		/**
		 * Return the default value for the provided configuration parameter.
		 * 
		 * @param name
		 *            The name of the configuration parameter.
		 * @return The value, or null if none registered.
		 */
		public static String get(String name) {
			return singleton.get(name);
		}

		/**
		 * Register a default value.
		 * 
		 * @param name
		 *            The name of the parameter.
		 * @param value
		 *            The default value of the parameter.
		 */
		public static void register(String name, String value) {
			singleton.put(name, value);
		}

		/**
		 * Un-Register a default value.
		 * 
		 * @param name
		 *            The name of the parameter to unregister.
		 */
		public static void unregister(String name) {
			singleton.remove(name);
		}
	} // DEFAULTS class
	
}
