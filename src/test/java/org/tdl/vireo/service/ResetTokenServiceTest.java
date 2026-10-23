package org.tdl.vireo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.tdl.vireo.model.Role;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.UserRepo;

/**
 * Property tests for {@link ResetTokenService}. Cases are enumerated boundary values crossed with a
 * fixed-seed {@link Random}, fed through {@link ParameterizedTest}. No property-based testing
 * library is used.
 */
@ActiveProfiles(value = { "test", "isolated-test" })
@ExtendWith(MockitoExtension.class)
public class ResetTokenServiceTest {

    private static final String SIGNING_SECRET = "verysecretsecret";

    private static final long GENERATOR_SEED = 20250101L;

    private static final int DEFAULT_EXPIRY_HOURS = 24;

    @Mock
    private UserRepo userRepo;

    @Mock
    private PasswordFeatureAvailabilityService availabilityService;

    @Spy
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private ResetTokenService resetTokenService;

    private void inject(Clock clock) {
        ReflectionTestUtils.setField(resetTokenService, "signingSecret", SIGNING_SECRET);
        ReflectionTestUtils.setField(resetTokenService, "clock", clock);
    }

    private static User userWith(String email, String storedHash) {
        User user = new User(email, "First", "Last", storedHash, Role.ROLE_STUDENT);
        return user;
    }

    // ---------------------------------------------------------------------
    // Property 1: Reset token round trip. Validates Requirements 8.1, 8.10, 16.4.
    // ---------------------------------------------------------------------

    static Stream<Arguments> roundTripCases() {
        List<String> emails = new ArrayList<>(Arrays.asList(
            "student@example.edu",
            "a@b.co",
            // email containing a literal '|' — the case right-to-left parsing exists for
            "weird|pipe@example.edu",
            "two|pipes|here@example.edu",
            "UPPER.Case@Example.EDU"
        ));

        List<String> hashes = new ArrayList<>(Arrays.asList(
            "$2a$10$abcdefghijklmnopqrstuuABCDEFGHIJKLMNOPQRSTUVWXYZ012345",
            "plainhash",
            "$2a$10$0000000000000000000000.0000000000000000000000000000000"
        ));

        // instants: boundary epoch-seconds plus a sub-second-nanos instant that must truncate
        List<Instant> instants = new ArrayList<>(Arrays.asList(
            Instant.ofEpochSecond(0),
            Instant.ofEpochSecond(1_700_000_000L),
            // sub-second nanos: epoch-second precision must truncate to 1_700_000_123
            Instant.ofEpochSecond(1_700_000_123L, 987_654_321L)
        ));

        List<Arguments> cases = new ArrayList<>();
        for (String email : emails) {
            for (String hash : hashes) {
                for (Instant instant : instants) {
                    cases.add(Arguments.of(email, hash, instant));
                }
            }
        }

        Random random = new Random(GENERATOR_SEED);
        while (cases.size() < 120) {
            String email = "user" + random.nextInt(100000) + (random.nextBoolean() ? "|p" : "") + "@example.edu";
            String hash = "$2a$10$" + random.nextLong();
            Instant instant = Instant.ofEpochSecond(
                1_000_000L + (long) (random.nextDouble() * 1_000_000_000L),
                random.nextInt(1_000_000_000));
            cases.add(Arguments.of(email, hash, instant));
        }

        return cases.stream();
    }

    @ParameterizedTest
    @MethodSource("roundTripCases")
    public void roundTripVerifiesValid(String email, String storedHash, Instant issuedAt) {
        Clock fixed = Clock.fixed(issuedAt, ZoneOffset.UTC);
        inject(fixed);

        User user = userWith(email, storedHash);
        when(userRepo.findByEmail(email)).thenReturn(user);
        when(availabilityService.getResetExpiryHours()).thenReturn(DEFAULT_EXPIRY_HOURS);

        String token = resetTokenService.construct(user);
        ResetTokenVerification result = resetTokenService.verify(token);

        assertEquals(ResetTokenVerification.Status.VALID, result.getStatus(),
            "round-tripped token should verify VALID for email " + email);
        assertTrue(result.isValid());
        assertEquals(email, result.getEmail());
        // issued-at carries epoch-second precision only; sub-second nanos must truncate
        assertEquals(issuedAt.getEpochSecond(), result.getIssuedAt().getEpochSecond());
        assertEquals(Instant.ofEpochSecond(issuedAt.getEpochSecond()), result.getIssuedAt());
    }

    // ---------------------------------------------------------------------
    // Property 2: A stored-password change invalidates every earlier token.
    // Validates Requirements 8.8, 16.5.
    // ---------------------------------------------------------------------

    static Stream<Arguments> passwordChangeCases() {
        List<String> emails = Arrays.asList(
            "change@example.edu",
            "pipe|user@example.edu");

        // The load-bearing case is the EQUAL-VALUE pair: re-encoding the same password value
        // yields a different hash (fresh salt), so a token built before the change must fail after.
        List<String> oldPasswords = Arrays.asList("CorrectHorse", "password123", "CorrectHorse");
        List<String> newPasswords = Arrays.asList("CorrectHorse", "password123!", "different");

        List<Arguments> cases = new ArrayList<>();
        Random random = new Random(GENERATOR_SEED);
        for (String email : emails) {
            for (int i = 0; i < oldPasswords.size(); i++) {
                cases.add(Arguments.of(email, oldPasswords.get(i), newPasswords.get(i)));
            }
        }
        while (cases.size() < 100) {
            String pw = "pw" + random.nextInt(1_000_000);
            boolean equalValue = random.nextBoolean();
            String email = "u" + random.nextInt(100000) + "@example.edu";
            cases.add(Arguments.of(email, pw, equalValue ? pw : pw + "x"));
        }
        return cases.stream();
    }

    @ParameterizedTest
    @MethodSource("passwordChangeCases")
    public void storedPasswordChangeInvalidatesToken(String email, String oldPassword, String newPassword) {
        Clock fixed = Clock.fixed(Instant.ofEpochSecond(1_700_000_000L), ZoneOffset.UTC);
        inject(fixed);

        String oldHash = passwordEncoder.encode(oldPassword);
        User userBefore = userWith(email, oldHash);

        when(userRepo.findByEmail(email)).thenReturn(userBefore);
        lenient().when(availabilityService.getResetExpiryHours()).thenReturn(DEFAULT_EXPIRY_HOURS);

        String token = resetTokenService.construct(userBefore);

        // Sanity: token verifies before the change.
        assertEquals(ResetTokenVerification.Status.VALID, resetTokenService.verify(token).getStatus());

        // Re-encode (even the SAME value re-salts to a different hash) and swap the stored Account.
        String newHash = passwordEncoder.encode(newPassword);
        assertNotEquals(oldHash, newHash, "BCrypt re-encoding must produce a different hash");
        User userAfter = userWith(email, newHash);
        when(userRepo.findByEmail(email)).thenReturn(userAfter);

        ResetTokenVerification result = resetTokenService.verify(token);
        assertEquals(ResetTokenVerification.Status.INVALID, result.getStatus(),
            "token built against old hash must be INVALID after the stored password changes");
    }

    // ---------------------------------------------------------------------
    // Property 3: Expiry boundary. Validates Requirements 8.5, 8.7, 16.6.
    // VALID in [0, expiryHours), EXPIRED at or beyond expiryHours, INVALID for negative offsets.
    // ---------------------------------------------------------------------

    static Stream<Arguments> expiryCases() {
        List<Integer> expiryHoursValues = Arrays.asList(1, 24, 72);
        // offsets expressed as a fraction/multiple relative to the boundary, in whole hours
        List<Long> hourOffsets = new ArrayList<>(Arrays.asList(
            -100L, -24L, -1L,           // negative -> INVALID
            0L,                         // exactly issued -> VALID
            1L, 23L,                    // within window -> VALID (for larger expiries)
            24L, 25L, 72L, 100L));      // at/after boundary -> EXPIRED (depending on expiry)

        List<Arguments> cases = new ArrayList<>();
        for (Integer expiry : expiryHoursValues) {
            for (Long offset : hourOffsets) {
                cases.add(Arguments.of(expiry, offset));
            }
        }

        Random random = new Random(GENERATOR_SEED);
        while (cases.size() < 100) {
            int expiry = 1 + random.nextInt(100);
            long offset = -50L + random.nextInt(200);
            cases.add(Arguments.of(expiry, offset));
        }
        return cases.stream();
    }

    @ParameterizedTest
    @MethodSource("expiryCases")
    public void expiryPartitionHolds(int expiryHours, long hourOffset) {
        String email = "expiry@example.edu";
        String hash = "$2a$10$abcdefghijklmnopqrstuuABCDEFGHIJKLMNOPQRSTUVWXYZ012345";

        Instant issuedAt = Instant.ofEpochSecond(1_700_000_000L);

        // Construct at issuedAt.
        inject(Clock.fixed(issuedAt, ZoneOffset.UTC));
        User user = userWith(email, hash);
        when(userRepo.findByEmail(email)).thenReturn(user);
        when(availabilityService.getResetExpiryHours()).thenReturn(expiryHours);

        String token = resetTokenService.construct(user);

        // Move the clock to issuedAt + hourOffset hours and verify.
        Instant verifyAt = issuedAt.plusSeconds(hourOffset * 3600L);
        ReflectionTestUtils.setField(resetTokenService, "clock", Clock.fixed(verifyAt, ZoneOffset.UTC));

        ResetTokenVerification result = resetTokenService.verify(token);

        ResetTokenVerification.Status expected;
        if (hourOffset < 0) {
            expected = ResetTokenVerification.Status.INVALID;
        } else if (hourOffset < expiryHours) {
            expected = ResetTokenVerification.Status.VALID;
        } else {
            expected = ResetTokenVerification.Status.EXPIRED;
        }

        assertEquals(expected, result.getStatus(),
            "expiry=" + expiryHours + "h offset=" + hourOffset + "h");
    }

    // ---------------------------------------------------------------------
    // Property 4: Single use and sibling invalidation. Validates Requirements 8.9, 9.6, 16.7.
    // The clock stays inside the expiry window throughout, so an expiry result cannot be mistaken
    // for the single-use result.
    // ---------------------------------------------------------------------

    static Stream<Arguments> singleUseCases() {
        List<String> emails = Arrays.asList(
            "single@example.edu",
            "sib|ling@example.edu");
        List<String> passwords = Arrays.asList("FirstPass", "another-pass", "Zz9");
        List<Integer> siblingCounts = Arrays.asList(1, 2, 3);

        List<Arguments> cases = new ArrayList<>();
        Random random = new Random(GENERATOR_SEED);
        for (String email : emails) {
            for (String password : passwords) {
                for (Integer siblings : siblingCounts) {
                    cases.add(Arguments.of(email, password, siblings));
                }
            }
        }
        while (cases.size() < 100) {
            cases.add(Arguments.of(
                "u" + random.nextInt(100000) + "@example.edu",
                "pw" + random.nextInt(1_000_000),
                1 + random.nextInt(4)));
        }
        return cases.stream();
    }

    @ParameterizedTest
    @MethodSource("singleUseCases")
    public void singleUseInvalidatesRedeemedAndSiblingTokens(String email, String password, int siblingCount) {
        // Keep the clock inside the expiry window the entire time.
        Instant issuedAt = Instant.ofEpochSecond(1_700_000_000L);
        inject(Clock.fixed(issuedAt, ZoneOffset.UTC));

        String oldHash = passwordEncoder.encode(password);
        User userBefore = userWith(email, oldHash);
        when(userRepo.findByEmail(email)).thenReturn(userBefore);
        lenient().when(availabilityService.getResetExpiryHours()).thenReturn(DEFAULT_EXPIRY_HOURS);

        // Issue the redeemed token plus a number of sibling tokens, all against the old hash.
        String redeemed = resetTokenService.construct(userBefore);
        List<String> siblings = new ArrayList<>();
        for (int i = 0; i < siblingCount; i++) {
            siblings.add(resetTokenService.construct(userBefore));
        }

        // All verify while the hash is unchanged.
        assertEquals(ResetTokenVerification.Status.VALID, resetTokenService.verify(redeemed).getStatus());
        for (String sibling : siblings) {
            assertEquals(ResetTokenVerification.Status.VALID, resetTokenService.verify(sibling).getStatus());
        }

        // Redeem: the stored password changes (re-salted). Clock stays in-window.
        String newHash = passwordEncoder.encode(password);
        User userAfter = userWith(email, newHash);
        when(userRepo.findByEmail(email)).thenReturn(userAfter);

        // The redeemed token is now INVALID (single use), not EXPIRED.
        assertEquals(ResetTokenVerification.Status.INVALID, resetTokenService.verify(redeemed).getStatus(),
            "redeemed token must be single-use INVALID, not EXPIRED");

        // Every sibling issued against the old hash is also INVALID (cross-invalidation).
        for (String sibling : siblings) {
            assertEquals(ResetTokenVerification.Status.INVALID, resetTokenService.verify(sibling).getStatus(),
                "sibling token must be invalidated by the stored password change");
        }
    }

    // ---------------------------------------------------------------------
    // Property 15: Token tamper rejection. Validates Requirements 8.2.
    // Any mutation to the encoded payload segment or the signature segment of a well-formed,
    // otherwise-valid token must verify as INVALID (NOT EXPIRED, NOT VALID). The clock is kept
    // inside the expiry window so an untampered baseline verifies VALID; this proves the INVALID
    // result comes from tampering, not from expiry.
    // ---------------------------------------------------------------------

    /** The base64url alphabet, used to substitute a character for a different-but-valid one. */
    private static final String BASE64URL_ALPHABET =
        "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_";

    /** Which of the two token segments a mutation targets. */
    private enum Segment { PAYLOAD, SIGNATURE }

    /** The kind of single-character mutation applied to a segment. */
    private enum Mutation { FLIP_BIT, INSERT, DELETE, SUBSTITUTE_BASE64URL }

    static Stream<Arguments> tamperCases() {
        List<String> emails = Arrays.asList(
            "tamper@example.edu",
            "pipe|tamper@example.edu");
        List<String> hashes = Arrays.asList(
            "$2a$10$abcdefghijklmnopqrstuuABCDEFGHIJKLMNOPQRSTUVWXYZ012345",
            "plainhash");

        // Every segment crossed with every mutation kind — both segments the criterion names,
        // and both the "decode fails" and "decode succeeds but content changed" paths.
        List<Arguments> cases = new ArrayList<>();
        Random random = new Random(GENERATOR_SEED);
        for (String email : emails) {
            for (String hash : hashes) {
                for (Segment segment : Segment.values()) {
                    for (Mutation mutation : Mutation.values()) {
                        // A few randomized positions per (segment, mutation) pair to reach >= 100.
                        for (int i = 0; i < 4; i++) {
                            cases.add(Arguments.of(email, hash, segment, mutation, random.nextInt(1_000_000)));
                        }
                    }
                }
            }
        }
        while (cases.size() < 100) {
            String email = "u" + random.nextInt(100000) + "@example.edu";
            cases.add(Arguments.of(
                email,
                "$2a$10$" + random.nextLong(),
                Segment.values()[random.nextInt(Segment.values().length)],
                Mutation.values()[random.nextInt(Mutation.values().length)],
                random.nextInt(1_000_000)));
        }
        return cases.stream();
    }

    @ParameterizedTest
    @MethodSource("tamperCases")
    public void tamperedTokenVerifiesInvalid(String email, String storedHash, Segment segment,
            Mutation mutation, int positionSeed) {
        Instant issuedAt = Instant.ofEpochSecond(1_700_000_000L);
        inject(Clock.fixed(issuedAt, ZoneOffset.UTC));

        User user = userWith(email, storedHash);
        when(userRepo.findByEmail(email)).thenReturn(user);
        lenient().when(availabilityService.getResetExpiryHours()).thenReturn(DEFAULT_EXPIRY_HOURS);

        String token = resetTokenService.construct(user);

        // Baseline sanity: the untampered token verifies VALID for the same inputs, so a broken
        // baseline cannot mask the property.
        assertEquals(ResetTokenVerification.Status.VALID, resetTokenService.verify(token).getStatus(),
            "untampered baseline token must verify VALID for email " + email);

        String[] parts = token.split("\\.", -1);
        String payloadSeg = parts[0];
        String signatureSeg = parts[1];

        String targetSeg = (segment == Segment.PAYLOAD) ? payloadSeg : signatureSeg;
        String mutatedSeg = mutate(targetSeg, mutation, positionSeed);

        String tampered = (segment == Segment.PAYLOAD)
            ? mutatedSeg + "." + signatureSeg
            : payloadSeg + "." + mutatedSeg;

        // Guard against a no-op mutation accidentally reproducing the original token.
        assumeTrue(!tampered.equals(token), "mutation was a no-op; skipping this case");

        ResetTokenVerification result = resetTokenService.verify(tampered);
        assertEquals(ResetTokenVerification.Status.INVALID, result.getStatus(),
            "tampered " + segment + " via " + mutation + " must verify INVALID, not "
                + result.getStatus());
    }

    /**
     * Applies a single-character mutation to a segment. Position is derived from {@code positionSeed}
     * so cases are reproducible under the fixed generator seed.
     */
    private static String mutate(String segment, Mutation mutation, int positionSeed) {
        if (segment.isEmpty()) {
            // Degenerate; append a character so the result still differs from the empty input.
            return "A";
        }
        int index = Math.floorMod(positionSeed, segment.length());
        char[] chars = segment.toCharArray();
        switch (mutation) {
            case FLIP_BIT:
                // XOR a low bit — may or may not leave a valid base64url character, exercising the
                // "decode fails" path as well as the "signature mismatch on a decodable string" path.
                chars[index] = (char) (chars[index] ^ 0x01);
                return new String(chars);
            case INSERT:
                return segment.substring(0, index) + 'A' + segment.substring(index);
            case DELETE:
                if (segment.length() == 1) {
                    // Deleting the only character yields an empty segment; append instead so the
                    // tampered token remains a two-segment, non-empty structure that still differs.
                    return segment + "A";
                }
                return segment.substring(0, index) + segment.substring(index + 1);
            case SUBSTITUTE_BASE64URL:
                // Swap for a DIFFERENT valid base64url character, keeping the segment decodable so
                // the content-changed-but-still-decodes path is exercised.
                char original = chars[index];
                char replacement = original;
                int offset = 1 + Math.floorMod(positionSeed, BASE64URL_ALPHABET.length() - 1);
                int originalPos = BASE64URL_ALPHABET.indexOf(original);
                if (originalPos < 0) {
                    // Original is not an alphabet character (e.g. padding); just pick 'A' vs 'B'.
                    replacement = (original == 'A') ? 'B' : 'A';
                } else {
                    replacement = BASE64URL_ALPHABET.charAt(
                        (originalPos + offset) % BASE64URL_ALPHABET.length());
                }
                chars[index] = replacement;
                return new String(chars);
            default:
                throw new IllegalStateException("unhandled mutation " + mutation);
        }
    }

    // ---------------------------------------------------------------------
    // Property 19: No side effects on initiation or verification (verification arm).
    // Validates Requirements 7.5, 8.3.
    //
    // ResetTokenService.verify reads no persisted token state and writes no Account. Across a full
    // construct-and-verify cycle the only repository interaction is the read-only findByEmail
    // lookup: verify must never invoke userRepo.update or userRepo.save, and the Account's stored
    // password hash must be byte-identical before and after the verify call. The whole partition of
    // verification outcomes is exercised (VALID, EXPIRED, INVALID) so no outcome is left able to
    // hide a write.
    // ---------------------------------------------------------------------

    /** Which verification outcome a case is engineered to produce, so the whole partition is covered. */
    private enum VerifyOutcome { VALID, EXPIRED, INVALID }

    static Stream<Arguments> noSideEffectCases() {
        List<String> emails = new ArrayList<>(Arrays.asList(
            "noeffect@example.edu",
            "a@b.co",
            "pipe|user@example.edu",
            "UPPER.Case@Example.EDU"));

        List<String> hashes = new ArrayList<>(Arrays.asList(
            "$2a$10$abcdefghijklmnopqrstuuABCDEFGHIJKLMNOPQRSTUVWXYZ012345",
            "plainhash",
            "$2a$10$0000000000000000000000.0000000000000000000000000000000"));

        List<Integer> expiryHoursValues = Arrays.asList(1, 24, 72);

        List<Arguments> cases = new ArrayList<>();
        for (String email : emails) {
            for (String hash : hashes) {
                for (Integer expiry : expiryHoursValues) {
                    for (VerifyOutcome outcome : VerifyOutcome.values()) {
                        cases.add(Arguments.of(email, hash, expiry, outcome));
                    }
                }
            }
        }

        Random random = new Random(GENERATOR_SEED);
        while (cases.size() < 120) {
            String email = "user" + random.nextInt(100000) + (random.nextBoolean() ? "|p" : "") + "@example.edu";
            String hash = "$2a$10$" + random.nextLong();
            int expiry = 1 + random.nextInt(100);
            VerifyOutcome outcome = VerifyOutcome.values()[random.nextInt(VerifyOutcome.values().length)];
            cases.add(Arguments.of(email, hash, expiry, outcome));
        }

        return cases.stream();
    }

    @ParameterizedTest
    @MethodSource("noSideEffectCases")
    public void verifyWritesNoAccountAndReadsNoPersistedTokenState(String email, String storedHash,
            int expiryHours, VerifyOutcome outcome) {
        Instant issuedAt = Instant.ofEpochSecond(1_700_000_000L);
        inject(Clock.fixed(issuedAt, ZoneOffset.UTC));

        User user = userWith(email, storedHash);
        // Lenient: the INVALID arm feeds a structurally broken token that short-circuits before the
        // email lookup, so this stub is unused for those cases.
        lenient().when(userRepo.findByEmail(email)).thenReturn(user);
        lenient().when(availabilityService.getResetExpiryHours()).thenReturn(expiryHours);

        // Construct against the live Account, then engineer the chosen verification outcome.
        String token = resetTokenService.construct(user);

        String tokenToVerify;
        switch (outcome) {
            case VALID:
                // Verify well inside the window; the clock stays at issuedAt.
                tokenToVerify = token;
                break;
            case EXPIRED:
                // Advance the clock to or beyond the expiry boundary; the token itself is untouched.
                ReflectionTestUtils.setField(resetTokenService, "clock",
                    Clock.fixed(issuedAt.plusSeconds((long) expiryHours * 3600L), ZoneOffset.UTC));
                tokenToVerify = token;
                break;
            case INVALID:
            default:
                // A structurally broken token forces the INVALID path without touching the Account.
                tokenToVerify = "not-a-valid-token";
                break;
        }

        // Record the stored hash by identity and value, so any in-place mutation would be caught.
        String hashBefore = user.getPassword();

        ResetTokenVerification result = resetTokenService.verify(tokenToVerify);

        // The engineered outcome actually occurred, so each arm of the partition is genuinely exercised.
        ResetTokenVerification.Status expected;
        switch (outcome) {
            case VALID:
                expected = ResetTokenVerification.Status.VALID;
                break;
            case EXPIRED:
                expected = ResetTokenVerification.Status.EXPIRED;
                break;
            case INVALID:
            default:
                expected = ResetTokenVerification.Status.INVALID;
                break;
        }
        assertEquals(expected, result.getStatus(), "engineered outcome for " + outcome + " / " + email);

        // The Account is never written: verify reads persisted state at most, never mutates it.
        verify(userRepo, never()).update(any(User.class));
        verify(userRepo, never()).save(any(User.class));

        // The stored password hash is byte-identical before and after a verify call.
        assertEquals(hashBefore, user.getPassword(),
            "the stored password hash must be unchanged across a verify call for " + email);
        assertTrue(storedHash.equals(user.getPassword()),
            "the stored password hash must be byte-identical to the original for " + email);
    }

}
