package org.tdl.vireo.utility;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.context.ActiveProfiles;
import org.tdl.vireo.model.User;

/**
 * Tests the eligibility predicate over the cross product of stored password, netid and affiliations.
 * The rule is static and stateless, so no Spring context is started.
 */
@ActiveProfiles(value = { "test", "isolated-test" })
public class PasswordEligibilityRuleTest {

    // U+00A0 is included because String.trim only strips characters at or below U+0020, so it counts as non-blank.
    private static final List<String> PASSWORDS = Arrays.asList(
        // Absent and blank.
        null,
        "",
        " ",
        "   ",
        "\t\n",
        "\r\n ",

        // Non-blank.
        "$2a$10$abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123",
        "x",
        "abc",
        "  padded  ",
        "\t spaced \n",
        "\u00a0"
    );

    // Blank netids matter: any non-null netid is ineligible, and updateUserByCredentials can store an empty one.
    private static final List<String> NETIDS = Arrays.asList(
        // The only eligible netid.
        null,

        // Non-null, so ineligible.
        "",
        " ",
        "\t",
        "jdoe1",
        "JDOE1",
        "a-very-long-institutional-net-identifier-0123456789-0123456789"
    );

    // Varied while the expected result stays fixed; the User constructor installs a TreeSet, so no null elements.
    private static final List<Set<String>> AFFILIATIONS = Arrays.asList(
        null,
        affiliations(),
        affiliations("student"),
        affiliations("staff", "student"),
        affiliations("alum", "faculty", "member", "staff", "student")
    );

    private static Set<String> affiliations(String... values) {
        Set<String> set = new TreeSet<String>();

        set.addAll(Arrays.asList(values));

        return set;
    }

    // The full cross product, since eligibility is an "exactly when" over the combination.
    private static Stream<Arguments> providePasswordNetidAndAffiliationCombinations() {
        List<Arguments> cases = new ArrayList<Arguments>();

        for (String password : PASSWORDS) {
            for (String netid : NETIDS) {
                for (Set<String> affiliation : AFFILIATIONS) {
                    cases.add(Arguments.of(password, netid, affiliation));
                }
            }
        }

        return cases.stream();
    }

    // Affiliations are deliberately not a parameter.
    private static boolean expectedEligible(String password, String netid) {
        return netid == null && password != null && password.trim().length() > 0;
    }

    private static User accountOf(String password, String netid, Set<String> affiliation) {
        User user = new User();

        user.setPassword(password);
        user.setNetid(netid);
        user.setShibbolethAffiliations(affiliation);

        return user;
    }

    // Eligible exactly when the password is non-blank and the netid is null; both overloads are checked against the independent predicate.
    @ParameterizedTest(name = "[{index}] password=''{0}'' netid=''{1}'' affiliations={2}")
    @MethodSource("providePasswordNetidAndAffiliationCombinations")
    public void testEligibilityPredicate(String password, String netid, Set<String> affiliation) {
        boolean expected = expectedEligible(password, netid);

        String description = "Eligibility for password '" + password + "', netid '" + netid + "', affiliations " + affiliation;

        assertEquals(
            Boolean.valueOf(expected),
            Boolean.valueOf(PasswordEligibilityRule.isEligible(accountOf(password, netid, affiliation))),
            description + " through the Account form");

        assertEquals(
            Boolean.valueOf(expected),
            Boolean.valueOf(PasswordEligibilityRule.isEligible(password, netid)),
            description + " through the field-level form");

        // A repeat evaluation on an equivalent Account classifies the same way.
        assertEquals(
            Boolean.valueOf(expected),
            Boolean.valueOf(PasswordEligibilityRule.isEligible(accountOf(password, netid, affiliation))),
            description + " on a repeat evaluation of an equivalent Account");
    }

    // The affiliation parameter is unused; it only keeps the generator shared with the predicate test.
    @ParameterizedTest(name = "[{index}] password=''{0}'' netid=''{1}''")
    @MethodSource("providePasswordNetidAndAffiliationCombinations")
    public void testAffiliationsAreNotConsulted(String password, String netid, Set<String> affiliation) {
        // The field-level overload cannot see affiliations, so it is the baseline.
        boolean withoutAffiliations = PasswordEligibilityRule.isEligible(password, netid);

        Set<Boolean> observed = new LinkedHashSet<Boolean>();

        for (Set<String> candidate : AFFILIATIONS) {
            boolean actual = PasswordEligibilityRule.isEligible(accountOf(password, netid, candidate));

            observed.add(Boolean.valueOf(actual));

            assertEquals(
                Boolean.valueOf(withoutAffiliations),
                Boolean.valueOf(actual),
                "Affiliations " + candidate + " altered the classification for password '" + password + "' and netid '" + netid + "'");
        }

        assertEquals(
            1,
            observed.size(),
            "Distinct classifications observed across the affiliation shapes for password '" + password + "' and netid '" + netid + "'");

        // Guards against a rule that is invariant only because it returns a constant.
        assertEquals(
            Boolean.valueOf(expectedEligible(password, netid)),
            Boolean.valueOf(withoutAffiliations),
            "Classification for password '" + password + "' and netid '" + netid + "'");
    }

    // A null Account fails closed.
    @Test
    public void testNullAccountIsIneligible() {
        assertFalse(PasswordEligibilityRule.isEligible((User) null), "A null Account classified as eligible");
    }

}
