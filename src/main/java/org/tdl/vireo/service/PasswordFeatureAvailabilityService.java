package org.tdl.vireo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tdl.vireo.config.constant.ConfigurationName;
import org.tdl.vireo.model.repo.ConfigurationRepo;

/**
 * Resolve the password feature configuration settings and report feature availability.
 *
 * Every accessor resolves its setting through
 * {@code configurationRepo.getValueByNameAndType(name, "application")} on each invocation. No
 * resolved setting value is held in a field, so an Administrator save applies to every later
 * request with no application restart.
 */
@Service
public class PasswordFeatureAvailabilityService {

    public static final int DEFAULT_RESET_EXPIRY_HOURS = 24;

    private static final String APPLICATION_TYPE = "application";

    private static final Logger LOG = LoggerFactory.getLogger(PasswordFeatureAvailabilityService.class);

    @Autowired
    private ConfigurationRepo configurationRepo;

    /**
     * Resolve the Password_Login_Setting, {@code auth.pass.enabled}.
     *
     * @return The resolved value, false when absent or unrecognised.
     */
    public boolean isPasswordLoginEnabled() {
        return resolveBoolean(getRawValue(ConfigurationName.APPLICATION_AUTH_PASS_ENABLED), ConfigurationName.APPLICATION_AUTH_PASS_ENABLED);
    }

    /**
     * Resolve {@code auth.pass.change.enabled}.
     *
     * @return The resolved value, false when absent or unrecognised.
     */
    public boolean isChangeEnabled() {
        return resolveBoolean(getRawValue(ConfigurationName.APPLICATION_AUTH_PASS_CHANGE_ENABLED), ConfigurationName.APPLICATION_AUTH_PASS_CHANGE_ENABLED);
    }

    /**
     * Resolve {@code auth.pass.reset.enabled}.
     *
     * @return The resolved value, false when absent or unrecognised.
     */
    public boolean isResetEnabled() {
        return resolveBoolean(getRawValue(ConfigurationName.APPLICATION_AUTH_PASS_RESET_ENABLED), ConfigurationName.APPLICATION_AUTH_PASS_RESET_ENABLED);
    }

    /**
     * Report whether self-service password change is available.
     *
     * Reads the Password_Login_Setting itself, so wiring login to honor that setting later requires
     * no change here.
     *
     * @return True only when the Password_Login_Setting and {@code auth.pass.change.enabled} both
     *         resolve to true.
     */
    public boolean isChangeAvailable() {
        return isPasswordLoginEnabled() && isChangeEnabled();
    }

    /**
     * Report whether administrator-initiated reset is available.
     *
     * Reads the Password_Login_Setting itself, so wiring login to honor that setting later requires
     * no change here.
     *
     * @return True only when the Password_Login_Setting and {@code auth.pass.reset.enabled} both
     *         resolve to true.
     */
    public boolean isResetAvailable() {
        return isPasswordLoginEnabled() && isResetEnabled();
    }

    // TODO: isChangeAvailableFor(User) and isResetAvailableFor(User) are added alongside
    // PasswordEligibilityRule.

    /**
     * Resolve {@code auth.pass.reset.expiryHours}.
     *
     * A value that does not parse as an integer, or that falls outside 1 to
     * {@link Integer#MAX_VALUE} inclusive, yields {@link #DEFAULT_RESET_EXPIRY_HOURS}, leaves the
     * stored value unchanged, and is recorded in the application log.
     *
     * @return The resolved expiry in hours.
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
     * Resolve a raw configuration value as a boolean, comparing with surrounding whitespace and
     * letter case ignored.
     *
     * An absent value resolves false. A value matching neither true nor false resolves false and is
     * recorded in the application log.
     *
     * Visible for test.
     *
     * @param raw The raw stored value.
     * @param settingName The setting name, for the log message.
     *
     * @return The resolved value.
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
