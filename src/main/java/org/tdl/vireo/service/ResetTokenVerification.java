package org.tdl.vireo.service;

import java.time.Instant;

/**
 * Outcome of verifying a password reset token.
 *
 * Only three statuses exist. A redeemed token and a token made stale by an unrelated change are
 * cryptographically indistinguishable, so no third failure status could be populated honestly:
 * every non-expiry failure is {@link Status#INVALID}.
 *
 * The {@link #getEmail()} and {@link #getIssuedAt()} accessors are populated only when the status
 * is {@link Status#VALID}; both failure factories leave them null.
 */
public final class ResetTokenVerification {

    /** The three possible verification outcomes. */
    public enum Status {
        VALID,
        EXPIRED,
        INVALID
    }

    private final Status status;

    private final String email;

    private final Instant issuedAt;

    private ResetTokenVerification(Status status, String email, Instant issuedAt) {
        this.status = status;
        this.email = email;
        this.issuedAt = issuedAt;
    }

    /**
     * A successful verification, carrying the email and the issued-at instant read from the token.
     */
    public static ResetTokenVerification valid(String email, Instant issuedAt) {
        return new ResetTokenVerification(Status.VALID, email, issuedAt);
    }

    /** A token whose signature and binding verified but which has aged past its expiry window. */
    public static ResetTokenVerification expired() {
        return new ResetTokenVerification(Status.EXPIRED, null, null);
    }

    /** Any other failure: malformed, tampered, unknown account, or invalidated by a password change. */
    public static ResetTokenVerification invalid() {
        return new ResetTokenVerification(Status.INVALID, null, null);
    }

    public Status getStatus() {
        return status;
    }

    public boolean isValid() {
        return status == Status.VALID;
    }

    /** The email carried by the token; null unless the status is {@link Status#VALID}. */
    public String getEmail() {
        return email;
    }

    /** The instant the token was issued; null unless the status is {@link Status#VALID}. */
    public Instant getIssuedAt() {
        return issuedAt;
    }

}
