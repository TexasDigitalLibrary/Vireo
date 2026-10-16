package org.tdl.vireo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tdl.vireo.config.constant.ConfigurationName;
import org.tdl.vireo.model.repo.ConfigurationRepo;

/**
 * Resolves the password feature settings and reports feature availability.
 *
 * Settings are read on every call and never cached, so an administrator save takes effect without
 * a restart.
 */
@Service
public class PasswordFeatureAvailabilityService {

    public static final int DEFAULT_RESET_EXPIRY_HOURS = 24;

    private static final String APPLICATION_TYPE = "application";

    private static final Logger LOG = LoggerFactory.getLogger(PasswordFeatureAvailabilityService.class);

    @Autowired
    private ConfigurationRepo configurationRepo;

    /** Resolves {@code auth.pass.enabled}; false when absent or unrecognised. */
    public boolean isPasswordLoginEnabled() {
        return resolveBoolean(getRawValue(ConfigurationName.APPLICATION_AUTH_PASS_ENABLED), ConfigurationName.APPLICATION_AUTH_PASS_ENABLED);
    }

    /** Resolves {@code auth.pass.change.enabled}; false when absent or unrecognised. */
    public boolean isChangeEnabled() {
        return resolveBoolean(getRawValue(ConfigurationName.APPLICATION_AUTH_PASS_CHANGE_ENABLED), ConfigurationName.APPLICATION_AUTH_PASS_CHANGE_ENABLED);
    }

    /** Resolves {@code auth.pass.reset.enabled}; false when absent or unrecognised. */
    public boolean isResetEnabled() {
        return resolveBoolean(getRawValue(ConfigurationName.APPLICATION_AUTH_PASS_RESET_ENABLED), ConfigurationName.APPLICATION_AUTH_PASS_RESET_ENABLED);
    }

    /** True only when both {@code auth.pass.enabled} and {@code auth.pass.change.enabled} are true. */
    public boolean isChangeAvailable() {
        return isPasswordLoginEnabled() && isChangeEnabled();
    }

    /** True only when both {@code auth.pass.enabled} and {@code auth.pass.reset.enabled} are true. */
    public boolean isResetAvailable() {
        return isPasswordLoginEnabled() && isResetEnabled();
    }

    // TODO: isChangeAvailableFor(User) and isResetAvailableFor(User) are added alongside
    // PasswordEligibilityRule.

    /**
     * Resolves {@code auth.pass.reset.expiryHours}. A value that is absent, not an integer, or below
     * 1 yields {@link #DEFAULT_RESET_EXPIRY_HOURS}; the stored value is left unchanged.
     */
    public int getResetExpiryHours() {
        String raw = getRawValue(ConfigurationName.APPLICATION_AUTH_PASS_RESET_EXPIRY_HOURS);

        if (raw == null) {
            LOG.debug("No value stored for '" + ConfigurationName.APPLICATION_AUTH_PASS_RESET_EXPIRY_HOURS + "', using " + DEFAULT_RESET_EXPIRY_HOURS + ".");

            return DEFAULT_RESET_EXPIRY_HOURS;
        }

        int hours;

        try {
            hours = Integer.parseInt(raw.trim());
        } catch (NumberFormatException e) {
            LOG.warn("Rejected value '" + raw + "' for '" + ConfigurationName.APPLICATION_AUTH_PASS_RESET_EXPIRY_HOURS + "', using " + DEFAULT_RESET_EXPIRY_HOURS + ".");

            return DEFAULT_RESET_EXPIRY_HOURS;
        }

        if (hours < 1) {
            LOG.warn("Rejected value '" + raw + "' for '" + ConfigurationName.APPLICATION_AUTH_PASS_RESET_EXPIRY_HOURS + "', using " + DEFAULT_RESET_EXPIRY_HOURS + ".");

            return DEFAULT_RESET_EXPIRY_HOURS;
        }

        return hours;
    }

    /**
     * Resolves a raw value as a boolean, ignoring whitespace and case. Absent or unrecognised values
     * resolve false (unrecognised ones are logged). Visible for test.
     */
    static boolean resolveBoolean(String raw, String settingName) {
        if (raw == null) {
            LOG.debug("No value stored for '" + settingName + "', treating as false.");

            return false;
        }

        String trimmed = raw.trim();

        if (trimmed.equalsIgnoreCase("true")) {
            return true;
        }

        if (!trimmed.equalsIgnoreCase("false")) {
            LOG.warn("Rejected value '" + raw + "' for '" + settingName + "', treating as false.");
        }

        return false;
    }

    private String getRawValue(String settingName) {
        return configurationRepo.getValueByNameAndType(settingName, APPLICATION_TYPE);
    }

}
