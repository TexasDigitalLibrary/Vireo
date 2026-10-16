package org.tdl.vireo.utility;

/**
 * Decides whether a candidate password is acceptable: non-blank with a trimmed length of at least
 * {@value #MINIMUM_TRIMMED_LENGTH}, the same rule as {@code AuthController.registration}.
 *
 * Static and stateless so it works under the {@code @InjectMocks} wiring used by controller tests.
 */
public final class PasswordValidator {

    /** The outcome of a validation; {@code ACCEPTED} is the only accepting value. */
    public enum Result {
        ACCEPTED, MISSING, TOO_SHORT, MISMATCH
    }

    /** The minimum acceptable trimmed length. */
    public static final int MINIMUM_TRIMMED_LENGTH = 6;

    private PasswordValidator() {
    }

    /** Validates a candidate with no confirmation: {@code MISSING}, {@code TOO_SHORT}, or {@code ACCEPTED}. */
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
     * Validates a candidate against its confirmation, checking {@code MISSING}, {@code MISMATCH},
     * then {@code TOO_SHORT} (the order {@code AuthController.registration} used). The comparison
     * is on untrimmed values.
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
     * Returns the original {@code AuthController} message for a rejection, or null for
     * {@code ACCEPTED}. The {@code TOO_SHORT} text misstates the rule but is kept verbatim to
     * preserve existing responses.
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
