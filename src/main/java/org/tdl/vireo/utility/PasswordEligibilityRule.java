package org.tdl.vireo.utility;

import org.tdl.vireo.model.User;

/**
 * The server-side predicate that classifies an Account as eligible for Vireo-managed password
 * operations, or as ineligible.
 *
 * Static and stateless by design: an injected bean would arrive null under the {@code @InjectMocks}
 * wiring every existing controller test uses.
 *
 * Reads {@code User.password} and {@code User.netid} only, and consults
 * {@code User.shibbolethAffiliations} in no part of the classification (Requirement 2 criterion 8).
 */
public final class PasswordEligibilityRule {

    private PasswordEligibilityRule() {
    }

    /**
     * Requirement 2 criteria 1, 2: eligible if and only if the password holds a non-whitespace
     * character and the netid is null. A null {@link User} is ineligible.
     *
     * @param user the Account to classify, which may be null
     * @return true when the Account is an Eligible_Account
     */
    public static boolean isEligible(User user) {
        if (user == null) {
            return false;
        }

        return isEligible(user.getPassword(), user.getNetid());
    }

    /**
     * Field-level form, so the rule can be evaluated without a {@link User} instance.
     *
     * Requirement 2 criteria 1, 2, 3: the classification is derived from the supplied stored field
     * values at evaluation time, with no administrator tagging and no migration.
     *
     * @param password the stored password value, which may be null
     * @param netid    the stored netid value, which may be null
     * @return true when the values describe an Eligible_Account
     */
    public static boolean isEligible(String password, String netid) {
        if (netid != null) {
            return false;
        }

        return password != null && !password.trim().isEmpty();
    }

}
