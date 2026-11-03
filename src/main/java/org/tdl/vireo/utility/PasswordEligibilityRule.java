package org.tdl.vireo.utility;

import org.tdl.vireo.model.User;

/**
 * Classifies an Account as eligible for Vireo-managed password operations. Reads only
 * {@code User.password} and {@code User.netid}.
 *
 * Static and stateless so it works under the {@code @InjectMocks} wiring used by controller tests.
 */
public final class PasswordEligibilityRule {

    private PasswordEligibilityRule() {
    }

    /** Eligible only if the password has a non-whitespace character and the netid is null. */
    public static boolean isEligible(User user) {
        if (user == null) {
            return false;
        }

        return isEligible(user.getPassword(), user.getNetid());
    }

    /** Field-level form of {@link #isEligible(User)}. */
    public static boolean isEligible(String password, String netid) {
        if (netid != null) {
            return false;
        }

        return password != null && !password.trim().isEmpty();
    }

}
