package org.tdl.vireo.model.repo.custom;

import org.tdl.vireo.model.Configuration;

public interface ConfigurationRepoCustom {

    /**
     * Creates a configuration
     *
     * Will create a non-isSystemRequired
     *
     * @param name
     * @param value
     * @param type
     * @return
     */
    public Configuration create(String name, String value, String type);

    /**
     * Resets existing configuration to its system value
     *
     *
     * @param configuration
     * @return
     */
    public Configuration reset(Configuration configuration);

    /**
     * Gets a Configuration from the repo
     *
     * Will always pick a non-isSystemRequired if it exists.
     * @param name
     * @return
     */
    public Configuration getByName(String name);

    /**
     * Gets a String value from the configuration repo.
     *
     * If it's not found in DB, a fallback value is used instead.
     *
     * @param name
     * @param fallback
     * @return config value or fallback
     */
    public String getValue(String name, String fallback);

    /**
     * Gets an Integer value from the configuration repo.
     *
     * If it's not found in DB, a fallback value is used instead.
     *
     * @param name
     * @param fallback
     * @return config value or fallback
     */
    public Integer getValue(String name, Integer fallback);

}
