package org.tdl.vireo.service;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.UserRepo;

/**
 * Builds and verifies self-contained, signed password reset tokens.
 *
 * Nothing about a reset is persisted. The token carries all of its own state in a signed payload,
 * built entirely from {@link javax.crypto.Mac} and {@link java.util.Base64} in the JDK (no new
 * dependency).
 *
 * <pre>
 * token         = base64url(payload) + "." + base64url(signature)
 * payload       = email + "|" + bindingValue + "|" + issuedAtEpochSeconds   (UTF-8 bytes)
 * signature     = HMAC-SHA256(key = signingSecret, message = base64url(payload) as US-ASCII bytes)
 * bindingValue  = base64url(HMAC-SHA256(key = signingSecret, message = storedPasswordHash UTF-8 bytes))
 * </pre>
 *
 * The binding value ties the token to the stored password hash: any stored-password change
 * re-salts the hash, so the binding derived at verification time no longer matches, invalidating
 * the token (single use plus cross-invalidation of every sibling).
 */
@Service
public class ResetTokenService {

    private static final String HMAC_ALGORITHM = "HmacSHA256";

    private static final Logger LOG = LoggerFactory.getLogger(ResetTokenService.class);

    @Value("${app.security.secret}")
    private String signingSecret;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordFeatureAvailabilityService availabilityService;

    /** Swappable with {@code ReflectionTestUtils.setField} for expiry-boundary tests. */
    private Clock clock = Clock.systemUTC();

    /**
     * Builds a signed reset token for the given Account.
     *
     * @param user the Account to issue a token for
     * @return the encoded token string
     */
    public String construct(User user) {
        String email = user.getEmail();
        String storedHash = user.getPassword();

        String bindingValue = deriveBindingValue(storedHash);

        Instant issuedAt = clock.instant();
        long issuedAtEpochSeconds = issuedAt.getEpochSecond();

        String payload = email + "|" + bindingValue + "|" + issuedAtEpochSeconds;
        byte[] payloadBytes = payload.getBytes(StandardCharsets.UTF_8);

        String encodedPayload = base64UrlEncode(payloadBytes);

        byte[] signature = hmacSha256(encodedPayload.getBytes(StandardCharsets.US_ASCII));

        return encodedPayload + "." + base64UrlEncode(signature);
    }

    /**
     * Verifies a reset token. Never throws: every decode and parse failure is caught and converted
     * to a failure result.
     *
     * The expiry check runs last, so {@link ResetTokenVerification.Status#EXPIRED} is reachable only
     * for a genuine unredeemed token whose signature verified and binding still matches.
     *
     * @param token the encoded token string
     * @return the verification outcome
     */
    public ResetTokenVerification verify(String token) {
        if (token == null || token.trim().isEmpty()) {
            return ResetTokenVerification.invalid();
        }

        String[] segments = token.split("\\.", -1);
        if (segments.length != 2 || segments[0].isEmpty() || segments[1].isEmpty()) {
            return ResetTokenVerification.invalid();
        }

        String encodedPayload = segments[0];
        String encodedSignature = segments[1];

        byte[] submittedSignature;
        try {
            submittedSignature = base64UrlDecode(encodedSignature);
        } catch (IllegalArgumentException e) {
            return ResetTokenVerification.invalid();
        }

        byte[] expectedSignature = hmacSha256(encodedPayload.getBytes(StandardCharsets.US_ASCII));
        if (!MessageDigest.isEqual(submittedSignature, expectedSignature)) {
            return ResetTokenVerification.invalid();
        }

        String payload;
        try {
            byte[] payloadBytes = base64UrlDecode(encodedPayload);
            payload = new String(payloadBytes, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            return ResetTokenVerification.invalid();
        }

        int lastPipe = payload.lastIndexOf('|');
        if (lastPipe < 0) {
            return ResetTokenVerification.invalid();
        }
        int priorPipe = payload.lastIndexOf('|', lastPipe - 1);
        if (priorPipe < 0) {
            return ResetTokenVerification.invalid();
        }

        String email = payload.substring(0, priorPipe);
        String bindingValue = payload.substring(priorPipe + 1, lastPipe);
        String issuedAtText = payload.substring(lastPipe + 1);

        long issuedAtEpochSeconds;
        try {
            issuedAtEpochSeconds = Long.parseLong(issuedAtText);
        } catch (NumberFormatException e) {
            return ResetTokenVerification.invalid();
        }

        User user = userRepo.findByEmail(email);
        if (user == null || user.getPassword() == null) {
            return ResetTokenVerification.invalid();
        }

        String currentBinding = deriveBindingValue(user.getPassword());
        if (!MessageDigest.isEqual(
                currentBinding.getBytes(StandardCharsets.UTF_8),
                bindingValue.getBytes(StandardCharsets.UTF_8))) {
            return ResetTokenVerification.invalid();
        }

        int expiryHours = availabilityService.getResetExpiryHours();

        Duration elapsed = Duration.between(Instant.ofEpochSecond(issuedAtEpochSeconds), clock.instant());
        if (elapsed.isNegative()) {
            return ResetTokenVerification.invalid();
        }
        if (elapsed.toHours() >= expiryHours) {
            return ResetTokenVerification.expired();
        }

        return ResetTokenVerification.valid(email, Instant.ofEpochSecond(issuedAtEpochSeconds));
    }

    /**
     * Derives the binding value for a stored password hash:
     * {@code base64url(HMAC-SHA256(signingSecret, storedPasswordHash UTF-8 bytes))}.
     *
     * @param storedPasswordHash the stored, encoded password
     * @return the base64url-encoded binding value
     */
    public String deriveBindingValue(String storedPasswordHash) {
        byte[] mac = hmacSha256(storedPasswordHash.getBytes(StandardCharsets.UTF_8));
        return base64UrlEncode(mac);
    }

    private byte[] hmacSha256(byte[] message) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(signingSecret.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM));
            return mac.doFinal(message);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            // HmacSHA256 is guaranteed present on every JVM and the key is non-empty; this is unreachable.
            LOG.error("Unable to compute HMAC-SHA256 for reset token", e);
            throw new IllegalStateException(e);
        }
    }

    private static String base64UrlEncode(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private static byte[] base64UrlDecode(String value) {
        return Base64.getUrlDecoder().decode(value);
    }

}
