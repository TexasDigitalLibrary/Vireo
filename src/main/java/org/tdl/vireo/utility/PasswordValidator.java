package org.tdl.vireo.utility;

/**
 * The single server-side decision on whether a candidate password is acceptable.
 *
 * Static and stateless by design: an injected bean would arrive null under the {@code @InjectMocks}
 * wiring every existing controller test uses.
 *
 * The acceptance rule is exactly the existing one in {@code AuthController.registration}: non-blank,
 * trimmed length {@value #MINIMUM_TRIMMED_LENGTH} or greater. No character-class, reuse-history, or
 * expiry constraint is added (Requirement 3 criterion 8).
 */
public final class PasswordValidator {

    /**
     * The outcome of a validation. {@code ACCEPTED} is the only accepting value; the other three name
     * the rejection cause.
     */
    public enum Result {
        ACCEPTED, MISSING, TOO_SHORT, MISMATCH
    }

    /**
     * Requirement 3 criterion 2: the minimum acceptable trimmed length.
     */
    public static final int MINIMUM_TRIMMED_LENGTH = 6;

    private PasswordValidator() {
    }

    /**
     * Requirement 3 criteria 1, 2, 3: a candidate with no confirmation to compare against. Absent or
     * blank is {@code MISSING}; a trimmed length below {@link #MINIMUM_TRIMMED_LENGTH} is
     * {@code TOO_SHORT}; anything else is {@code ACCEPTED}.
     *
     * @param candidate the proposed password, which may be null
     * @return the decision
     */
    public static Result validate(String candidate) {
        if (candidate == null || candidate.trim().length() == 0) {
            return Result.MISSING;
        }

        if (candidate.trim().length() < MINIMUM_TRIMMED_LENGTH) {
            return Result.TOO_SHORT;
        }

        return Result.ACCEPTED;
    }

    /**
     * Requirement 3 criteria 1 through 4. Evaluation order is {@code MISSING}, then {@code MISMATCH},
     * then {@code TOO_SHORT}, matching the existing order in {@code AuthController.registration} so
     * the retrofit is outcome-preserving (Requirement 3 criterion 9).
     *
     * The confirmation comparison is on the untrimmed values, again matching the existing behavior.
     *
     * @param candidate    the proposed password, which may be null
     * @param confirmation the repeated password, which may be null
     * @return the decision
     */
    public static Result validate(String candidate, String confirmation) {
        if (candidate == null || candidate.trim().length() == 0) {
            return Result.MISSING;
        }

        if (!candidate.equals(confirmation)) {
            return Result.MISMATCH;
        }

        if (candidate.trim().length() < MINIMUM_TRIMMED_LENGTH) {
            return Result.TOO_SHORT;
        }

        return Result.ACCEPTED;
    }

    /**
     * Requirement 3 criteria 7, 9: the existing {@code AuthController} message text, verbatim, for
     * each rejection, so the retrofit preserves the response body as well as the {@code ApiStatus}.
     *
     * The {@code TOO_SHORT} text misdescribes a {@code >= 6} rule. It is preserved anyway: outcome
     * preservation is required and the Minimal Change Constraint forbids opportunistic correction.
     *
     * @param result the decision to describe
     * @return the message for a rejection, or null for {@code ACCEPTED}
     */
    public static String messageFor(Result result) {
        switch (result) {
            case MISSING:
                return "Registration requires a password!";
            case MISMATCH:
                return "The passwords do not match!";
            case TOO_SHORT:
                return "Password must be greater than 6 characters!";
            default:
                return null;
        }
    }

}
