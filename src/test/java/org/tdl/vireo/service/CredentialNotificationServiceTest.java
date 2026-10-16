package org.tdl.vireo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import javax.mail.MessagingException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.tdl.vireo.config.constant.ConfigurationName;
import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.ConfigurationRepo;
import org.tdl.vireo.model.repo.EmailTemplateRepo;
import org.tdl.vireo.utility.PasswordEligibilityRule;
import org.tdl.vireo.utility.TemplateUtility;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

/**
 * Absent-template and mail-failure cases for the password-changed notification.
 *
 * Validates: Requirements 6.6, 6.7, 14.1, 14.2, 14.4, 14.5, 15.10, 16.11
 *
 * Both failure injections assert the same three things the requirements name: no send is attempted
 * where none can succeed, the failure is recorded in the application log naming the Account and the
 * notification, and no exception escapes, so the already-persisted password change stands.
 *
 * The log assertion attaches a Logback {@link ListAppender} to the service's own logger, since the
 * project carries no existing log-capture helper and no new Maven dependency may be introduced.
 */
@ActiveProfiles(value = { "test", "isolated-test" })
@ExtendWith(MockitoExtension.class)
public class CredentialNotificationServiceTest {

    private static final String TEST_EMAIL = "aggieJack@tamu.edu";

    private static final String TEST_PASSWORD = "sup3rS3cr3tPlaintextPassword";

    private static final String TEST_TOKEN = "eyJ0b2tlbiI6ICJuZXZlci1sb2ctbWUifQ.c2lnbmF0dXJl";

    private static final String TEMPLATE_SUBJECT = "Your password has changed";

    private static final String TEMPLATE_MESSAGE = "Your password changed at {CHANGE_TIME}.";

    private static final String RENDERED_MESSAGE = "Your password changed at 2024-05-06 07:08:09 UTC.";

    private static final String NOTIFICATION_KIND = "password-changed notification";

    @Mock
    private EmailTemplateRepo emailTemplateRepo;

    @Mock
    private TemplateUtility templateUtility;

    @Mock
    private VireoEmailSender emailSender;

    /**
     * Declared for Property 23 only. The service under test holds no field of either type, so
     * neither mock is wired into it, and that is precisely the point under assertion: a dispatch
     * gated on a Password_Management_Setting or on the Password_Login_Setting would have to reach
     * one of these collaborators, which {@code @InjectMocks} would then supply from here.
     */
    @Mock
    private ConfigurationRepo configurationRepo;

    @Mock
    private PasswordFeatureAvailabilityService passwordFeatureAvailabilityService;

    @InjectMocks
    private CredentialNotificationService credentialNotificationService;

    private Logger serviceLogger;

    private Level originalLevel;

    private ListAppender<ILoggingEvent> logAppender;

    private User user;

    @BeforeEach
    public void setup() {
        ReflectionTestUtils.setField(credentialNotificationService, "clock", Clock.fixed(Instant.parse("2024-05-06T07:08:09Z"), ZoneOffset.UTC));

        user = new User();

        user.setEmail(TEST_EMAIL);
        user.setPassword(TEST_PASSWORD);

        logAppender = new ListAppender<ILoggingEvent>();

        logAppender.start();

        serviceLogger = (Logger) LoggerFactory.getLogger(CredentialNotificationService.class);

        // The test configuration pins org.tdl.vireo at ERROR, so the WARN records under assertion
        // here are enabled for this logger only, for the duration of the test, and then restored.
        originalLevel = serviceLogger.getLevel();

        serviceLogger.setLevel(Level.WARN);
        serviceLogger.addAppender(logAppender);
    }

    @AfterEach
    public void teardown() {
        serviceLogger.detachAppender(logAppender);
        serviceLogger.setLevel(originalLevel);

        logAppender.stop();
    }

    /**
     * An absent Email_Template produces a logged skip naming the template, no send, and no
     * exception.
     *
     * The null result is what {@code resolveTemplate} is tested against: the notification must not
     * reach {@code getSubject()} or {@code getMessage()} on a null row.
     */
    @Test
    public void testNotifyPasswordChangedWithAbsentTemplate() {
        when(emailTemplateRepo.findByNameOverride(CredentialNotificationService.PASSWORD_CHANGED_TEMPLATE)).thenReturn(null);

        credentialNotificationService.notifyPasswordChanged(user);

        verifyNoInteractions(emailSender);
        verifyNoInteractions(templateUtility);

        String log = capturedLog();

        assertTrue(log.contains(CredentialNotificationService.PASSWORD_CHANGED_TEMPLATE), "The absence of the email template must be logged naming the template, got: " + log);
        assertTrue(log.contains(NOTIFICATION_KIND), "The skipped notification must be logged naming the notification, got: " + log);

        assertNoSensitiveValueLogged(log);
    }

    /**
     * {@code resolveTemplate} returns null and logs rather than throwing when no row is installed.
     */
    @Test
    public void testResolveTemplateWithAbsentTemplate() {
        when(emailTemplateRepo.findByNameOverride(CredentialNotificationService.PASSWORD_CHANGED_TEMPLATE)).thenReturn(null);

        assertNull(credentialNotificationService.resolveTemplate(CredentialNotificationService.PASSWORD_CHANGED_TEMPLATE), "An absent email template must resolve to null.");

        assertTrue(capturedLog().contains(CredentialNotificationService.PASSWORD_CHANGED_TEMPLATE), "The absence of the email template must be logged naming the template.");
    }

    /**
     * A mail transport failure is logged with the Account identifier and the notification, and no
     * exception escapes, so the completed password change stands.
     */
    @Test
    public void testNotifyPasswordChangedWithMailFailure() throws MessagingException {
        when(emailTemplateRepo.findByNameOverride(CredentialNotificationService.PASSWORD_CHANGED_TEMPLATE)).thenReturn(new EmailTemplate(CredentialNotificationService.PASSWORD_CHANGED_TEMPLATE, TEMPLATE_SUBJECT, TEMPLATE_MESSAGE));

        when(templateUtility.templateParameters(anyString(), any(String[][].class))).thenReturn(RENDERED_MESSAGE);

        doThrow(MessagingException.class).when(emailSender).sendEmail(anyString(), anyString(), anyString());

        credentialNotificationService.notifyPasswordChanged(user);

        verify(emailSender).sendEmail(TEST_EMAIL, TEMPLATE_SUBJECT, RENDERED_MESSAGE);

        String log = capturedLog();

        assertTrue(log.contains(TEST_EMAIL), "The mail failure must be logged with the Account identifier, got: " + log);
        assertTrue(log.contains(NOTIFICATION_KIND), "The mail failure must be logged naming the notification, got: " + log);

        assertNoSensitiveValueLogged(log);
    }

    /**
     * Join every captured log message, so a single assertion can read across the whole record the
     * notification wrote.
     *
     * @return The captured log messages.
     */
    private String capturedLog() {
        List<String> messages = new ArrayList<String>();

        for (ILoggingEvent event : logAppender.list) {
            messages.add(event.getFormattedMessage());
        }

        return String.join("\n", messages);
    }

    /**
     * Neither a password value nor a token value may appear anywhere in the log output.
     *
     * @param log The captured log messages.
     */
    private void assertNoSensitiveValueLogged(String log) {
        assertFalse(log.contains(TEST_PASSWORD), "No password value may appear in the log output, got: " + log);
        assertFalse(log.contains(TEST_TOKEN), "No token value may appear in the log output, got: " + log);
    }

    /*
     * Task 6.4 — Property 23: Credential-change notification is always on, password-changed arm.
     *
     * The email-change arm is added in task 16, once the detection site on /user/update exists.
     */

    private static final long GENERATOR_SEED = 20250101L;

    private static final int RANDOM_CASE_COUNT = 64;

    private static final String APPLICATION_TYPE = "application";

    /**
     * The raw stored setting strings the generator draws from. Both the resolving and the
     * non-resolving shapes are present, since the property holds across the whole domain and not
     * merely across the two values an Administrator can select in the Password Management pane.
     */
    private static final List<String> RAW_SETTING_VALUES = Arrays.asList(null, "", "  ", "true", "TRUE", " true ", "false", "False", "0", "1", "yes", "no", "junk");

    private static final List<String> RAW_EXPIRY_SETTING_VALUES = Arrays.asList(null, "", "0", "-1", "1", "24", "2147483647", "notanumber");

    /**
     * One Account shape under test.
     *
     * The shape carries the two fields the Password_Eligibility_Rule reads, the
     * Institutional_SSO affiliations it deliberately ignores, and a user settings map, so a single
     * case can describe an Account the rule classifies ineligible, an Account provisioned through
     * Institutional_SSO, and an Account carrying user settings that look like a suppression switch.
     */
    private static final class AccountShape {

        private final String label;

        private final String email;

        private final String netid;

        private final String password;

        private final Set<String> affiliations;

        private final Map<String, String> settings;

        private AccountShape(String label, String email, String netid, String password, Set<String> affiliations, Map<String, String> settings) {
            this.label = label;
            this.email = email;
            this.netid = netid;
            this.password = password;
            this.affiliations = affiliations;
            this.settings = settings;
        }

        private User toUser() {
            User user = new User();

            user.setEmail(email);
            user.setNetid(netid);
            user.setPassword(password);
            user.setShibbolethAffiliations(new TreeSet<String>(affiliations));
            user.setSettings(new TreeMap<String, String>(settings));

            return user;
        }

        @Override
        public String toString() {
            return label;
        }

    }

    private static Set<String> affiliations(String... values) {
        return new LinkedHashSet<String>(Arrays.asList(values));
    }

    private static Map<String, String> settings(String... keysAndValues) {
        Map<String, String> settings = new LinkedHashMap<String, String>();

        for (int i = 0; i < keysAndValues.length; i += 2) {
            settings.put(keysAndValues[i], keysAndValues[i + 1]);
        }

        return settings;
    }

    /**
     * The enumerated Account shapes. Every shape the design's Property 23 input domain names is
     * present: an eligible Vireo-managed Account, an Account provisioned through Institutional_SSO,
     * an Account with no stored password, an Account whose stored password is whitespace only, an
     * Account carrying user settings that read like a suppression switch, and an Account whose
     * {@code preferedEmail} setting names a different address.
     */
    private static final List<AccountShape> ACCOUNT_SHAPES = Arrays.asList(
        new AccountShape("eligible", "eligible@tamu.edu", null, TEST_PASSWORD, affiliations(), settings()),
        new AccountShape("sso-provisioned", "netid@tamu.edu", "aggieJack", TEST_PASSWORD, affiliations("student", "staff"), settings()),
        new AccountShape("sso-no-password", "ssoonly@tamu.edu", "aggieJill", null, affiliations("member"), settings()),
        new AccountShape("blank-password", "blank@tamu.edu", null, "   ", affiliations(), settings()),
        new AccountShape("suppression-flavoured-settings", "flagged@tamu.edu", null, TEST_PASSWORD, affiliations(), settings("notifications", "false", "suppressNotifications", "true", "emailNotifications", "off")),
        new AccountShape("preferred-email-elsewhere", "account@tamu.edu", null, TEST_PASSWORD, affiliations("affiliate"), settings("preferedEmail", "somewhere.else@tamu.edu"))
    );

    /**
     * Each case is one Account shape crossed with one combination of the four password settings.
     *
     * The enumerated cross product is every Account shape against every pairing of resolving and
     * non-resolving values for {@code auth.pass.enabled}, {@code auth.pass.change.enabled} and
     * {@code auth.pass.reset.enabled}; the randomized cases then vary all four settings over the
     * wider raw-value domain, including the shapes that resolve to neither true nor false.
     *
     * @return The generated cases, well over one hundred of them.
     */
    private static Stream<Arguments> provideAlwaysOnCases() {
        List<Arguments> cases = new ArrayList<Arguments>();

        List<String> booleanValues = Arrays.asList("true", "false");

        for (AccountShape shape : ACCOUNT_SHAPES) {
            for (String enabledRaw : booleanValues) {
                for (String changeRaw : booleanValues) {
                    for (String resetRaw : booleanValues) {
                        cases.add(Arguments.of(shape, enabledRaw, changeRaw, resetRaw, "24"));
                    }
                }
            }
        }

        Random random = new Random(GENERATOR_SEED);

        for (int i = 0; i < RANDOM_CASE_COUNT; i++) {
            cases.add(Arguments.of(
                ACCOUNT_SHAPES.get(random.nextInt(ACCOUNT_SHAPES.size())),
                RAW_SETTING_VALUES.get(random.nextInt(RAW_SETTING_VALUES.size())),
                RAW_SETTING_VALUES.get(random.nextInt(RAW_SETTING_VALUES.size())),
                RAW_SETTING_VALUES.get(random.nextInt(RAW_SETTING_VALUES.size())),
                RAW_EXPIRY_SETTING_VALUES.get(random.nextInt(RAW_EXPIRY_SETTING_VALUES.size()))
            ));
        }

        return cases.stream();
    }

    /**
     * Property 23: Credential-change notification is always on.
     *
     * Validates: Requirements 12.5, 13.5, 13.6
     *
     * For any Account shape — including an Account provisioned through Institutional_SSO and an
     * Account the Password_Eligibility_Rule classifies ineligible — and for any combination of the
     * four password settings, and for any user settings carried on the Account, the password-changed
     * notification is dispatched exactly once to the Account_Email.
     *
     * Requirement 13 criterion 6 is what the two unwired collaborators pin. The dispatch reads no
     * configuration value and consults no availability conjunction, so a later change that gated the
     * notification on either would draw this test's stubbed values and fail the send assertion.
     *
     * @param shape The Account shape under test.
     * @param enabledRaw The raw stored {@code auth.pass.enabled} value.
     * @param changeRaw The raw stored {@code auth.pass.change.enabled} value.
     * @param resetRaw The raw stored {@code auth.pass.reset.enabled} value.
     * @param expiryRaw The raw stored {@code auth.pass.reset.expiryHours} value.
     */
    @ParameterizedTest(name = "[{index}] account={0}, enabled={1}, change={2}, reset={3}, expiryHours={4}")
    @MethodSource("provideAlwaysOnCases")
    public void testPasswordChangedNotificationIsAlwaysOn(AccountShape shape, String enabledRaw, String changeRaw, String resetRaw, String expiryRaw) throws MessagingException {
        stubSetting(ConfigurationName.APPLICATION_AUTH_PASS_ENABLED, enabledRaw);
        stubSetting(ConfigurationName.APPLICATION_AUTH_PASS_CHANGE_ENABLED, changeRaw);
        stubSetting(ConfigurationName.APPLICATION_AUTH_PASS_RESET_ENABLED, resetRaw);
        stubSetting(ConfigurationName.APPLICATION_AUTH_PASS_RESET_EXPIRY_HOURS, expiryRaw);

        stubAvailability(resolves(enabledRaw), resolves(changeRaw), resolves(resetRaw));

        when(emailTemplateRepo.findByNameOverride(CredentialNotificationService.PASSWORD_CHANGED_TEMPLATE)).thenReturn(new EmailTemplate(CredentialNotificationService.PASSWORD_CHANGED_TEMPLATE, TEMPLATE_SUBJECT, TEMPLATE_MESSAGE));

        when(templateUtility.templateParameters(anyString(), any(String[][].class))).thenReturn(RENDERED_MESSAGE);

        User target = shape.toUser();

        credentialNotificationService.notifyPasswordChanged(target);

        // Requirements 12.5, 13.5: one dispatch, to the Account_Email, for every Account shape and
        // every setting combination. The eligibility classification is recomputed here so the case
        // list is demonstrably not confined to eligible Accounts.
        assertEquals(PasswordEligibilityRule.isEligible(target), PasswordEligibilityRule.isEligible(shape.password, shape.netid), "The shape's eligibility classification is stable across both rule overloads");

        verify(emailSender, times(1)).sendEmail(shape.email, TEMPLATE_SUBJECT, RENDERED_MESSAGE);

        verifyNoMoreInteractions(emailSender);

        // Requirement 13.6: no configuration setting and no availability conjunction participates
        // in the dispatch, so none of them can suppress it.
        verifyNoInteractions(configurationRepo);
        verifyNoInteractions(passwordFeatureAvailabilityService);
    }

    /**
     * Requirement 13.6: the service exposes no suppression switch of any kind.
     *
     * No declared field reaches the configuration store or the availability service, and no public
     * method takes a flag a caller could set to skip the notification, so there is no request
     * parameter the dispatch could be made to honour.
     */
    @Test
    public void testNoSuppressionSwitchExists() {
        for (Field field : CredentialNotificationService.class.getDeclaredFields()) {
            assertFalse(ConfigurationRepo.class.isAssignableFrom(field.getType()), "The notification service holds no configuration store reference, found field: " + field.getName());
            assertFalse(PasswordFeatureAvailabilityService.class.isAssignableFrom(field.getType()), "The notification service holds no availability service reference, found field: " + field.getName());
        }

        for (Method method : CredentialNotificationService.class.getDeclaredMethods()) {
            if (!Modifier.isPublic(method.getModifiers()) || method.isSynthetic()) {
                continue;
            }

            for (Class<?> parameterType : method.getParameterTypes()) {
                assertFalse(boolean.class.equals(parameterType) || Boolean.class.equals(parameterType), "No public method of the notification service takes a flag that could suppress a dispatch, found: " + method.getName());
            }
        }

        Method[] declared = CredentialNotificationService.class.getDeclaredMethods();

        boolean found = false;

        for (Method method : declared) {
            if ("notifyPasswordChanged".equals(method.getName())) {
                found = true;

                assertEquals(1, method.getParameterCount(), "notifyPasswordChanged takes the target Account and nothing else");
                assertEquals(User.class, method.getParameterTypes()[0], "notifyPasswordChanged takes the target Account and nothing else");
            }
        }

        assertTrue(found, "notifyPasswordChanged is declared on the notification service");
    }

    /**
     * The resolution rule the availability service applies, recomputed here so a stubbed
     * availability answer matches what the real service would have returned for the raw value.
     *
     * @param raw The raw stored value.
     *
     * @return The resolved value.
     */
    private static boolean resolves(String raw) {
        return raw != null && raw.trim().equalsIgnoreCase("true");
    }

    /**
     * Stub a raw stored setting value on the unwired configuration store.
     *
     * @param settingName The setting name.
     * @param rawValue The raw stored value.
     */
    private void stubSetting(String settingName, String rawValue) {
        lenient().when(configurationRepo.getValueByNameAndType(eq(settingName), eq(APPLICATION_TYPE))).thenReturn(rawValue);
    }

    /**
     * Stub the availability conjunctions on the unwired availability service.
     *
     * @param enabled The resolved Password_Login_Setting.
     * @param change The resolved {@code auth.pass.change.enabled} value.
     * @param reset The resolved {@code auth.pass.reset.enabled} value.
     */
    private void stubAvailability(boolean enabled, boolean change, boolean reset) {
        lenient().when(passwordFeatureAvailabilityService.isPasswordLoginEnabled()).thenReturn(enabled);
        lenient().when(passwordFeatureAvailabilityService.isChangeEnabled()).thenReturn(change);
        lenient().when(passwordFeatureAvailabilityService.isResetEnabled()).thenReturn(reset);
        lenient().when(passwordFeatureAvailabilityService.isChangeAvailable()).thenReturn(enabled && change);
        lenient().when(passwordFeatureAvailabilityService.isResetAvailable()).thenReturn(enabled && reset);
    }

    /*
     * Task 6.5 — packaged template content assertions for SYSTEM_Password_Changed.email.
     *
     * These are example assertions over the packaged classpath resource, read and split the same
     * way SystemDataLoader.loadSystemEmailTemplate reads and splits it, so what is under assertion
     * is the subject and the body an installed row would actually carry.
     */

    private static final String PASSWORD_CHANGED_RESOURCE = "/emails/SYSTEM_Password_Changed.email";

    private static final String SUPPORT_ADDRESS = "etd-support@jhu.edu";

    private static final String SIGN_OFF = "The Hopkins ETD Team";

    private static final String CHANGE_TIME_PLACEHOLDER = "{CHANGE_TIME}";

    private static final String RESET_URL_PLACEHOLDER = "{RESET_URL}";

    /**
     * Every shape a hyperlink could take in a plain-text template body.
     */
    private static final List<String> HYPERLINK_MARKERS = Arrays.asList("http://", "https://", "www.", "mailto:", "href", "<a ", "ftp://", "{RESET_URL}", "{REGISTRATION_URL}", "{URL}");

    /**
     * The placeholder shape the templates use, matching the {@code REGISTRATION_URL} convention.
     */
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{[A-Za-z0-9_]+\\}");

    /**
     * Requirements 6.3, 13.1: the body states the change and its time through the
     * {@code CHANGE_TIME} parameter, and the comment header names that parameter.
     *
     * @throws IOException If the packaged resource cannot be read.
     */
    @Test
    public void testPackagedPasswordChangedTemplateCarriesChangeTime() throws IOException {
        String raw = readPackagedPasswordChangedTemplate();

        assertTrue(raw.contains("# Parameters: " + CHANGE_TIME_PLACEHOLDER), "The comment header names the CHANGE_TIME parameter, got: " + raw);

        assertTrue(packagedBody(raw).contains(CHANGE_TIME_PLACEHOLDER), "The template body expands the change time through " + CHANGE_TIME_PLACEHOLDER + ", got: " + packagedBody(raw));

        // The name the loader derives from the file name is the name the service resolves.
        assertEquals(CredentialNotificationService.PASSWORD_CHANGED_TEMPLATE, "SYSTEM_Password_Changed".replaceAll("_", " "), "The packaged resource file name decodes to the template name the notification service resolves.");

        assertEquals("Vireo Password Changed", packagedSubject(raw), "The template carries a Subject line the loader can extract.");
    }

    /**
     * Requirements 6.5, 13.4, 15.7: the body carries the support contact address and the
     * established sign-off.
     *
     * @throws IOException If the packaged resource cannot be read.
     */
    @Test
    public void testPackagedPasswordChangedTemplateCarriesSupportContact() throws IOException {
        String body = packagedBody(readPackagedPasswordChangedTemplate());

        assertTrue(body.contains(SUPPORT_ADDRESS), "The template body directs the recipient to " + SUPPORT_ADDRESS + ", got: " + body);
        assertTrue(body.contains(SIGN_OFF), "The template body signs off as '" + SIGN_OFF + "', got: " + body);
    }

    /**
     * Requirements 6.4, 13.2, 13.3: the body carries no hyperlink, no reset-token placeholder, and
     * no password value, and {@code CHANGE_TIME} is the only parameter the body expands.
     *
     * The whole resource is checked for {@code RESET_URL}, not merely the body, so a stray mention
     * in the comment header is caught as well.
     *
     * @throws IOException If the packaged resource cannot be read.
     */
    @Test
    public void testPackagedPasswordChangedTemplateCarriesNoHyperlinkOrToken() throws IOException {
        String raw = readPackagedPasswordChangedTemplate();
        String body = packagedBody(raw);

        assertFalse(raw.contains(RESET_URL_PLACEHOLDER), "No " + RESET_URL_PLACEHOLDER + " may appear anywhere in the password-changed template, got: " + raw);

        for (String marker : HYPERLINK_MARKERS) {
            assertFalse(body.toLowerCase().contains(marker.toLowerCase()), "No hyperlink may appear in the template body, found '" + marker + "' in: " + body);
        }

        Set<String> placeholders = new LinkedHashSet<String>();

        Matcher matcher = PLACEHOLDER_PATTERN.matcher(body);

        while (matcher.find()) {
            placeholders.add(matcher.group());
        }

        assertEquals(new LinkedHashSet<String>(Arrays.asList(CHANGE_TIME_PLACEHOLDER)), placeholders, "CHANGE_TIME is the only parameter the password-changed template body expands.");

        // Requirement 13.2: the body states that the password changed without carrying a value.
        assertFalse(body.contains(TEST_PASSWORD), "No password value may appear in the template body, got: " + body);
        assertFalse(body.toLowerCase().contains("{password"), "No password parameter may appear in the template body, got: " + body);
    }

    /**
     * Read the packaged template resource from the classpath.
     *
     * @return The raw resource content.
     *
     * @throws IOException If the packaged resource cannot be read.
     */
    private String readPackagedPasswordChangedTemplate() throws IOException {
        InputStream stream = CredentialNotificationServiceTest.class.getResourceAsStream(PASSWORD_CHANGED_RESOURCE);

        assertNotNull(stream, "The packaged resource " + PASSWORD_CHANGED_RESOURCE + " is on the classpath.");

        StringBuilder content = new StringBuilder();

        BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));

        try {
            String line = reader.readLine();

            while (line != null) {
                content.append(line).append("\n");

                line = reader.readLine();
            }
        } finally {
            reader.close();
        }

        return content.toString();
    }

    /**
     * Strip the comment lines the way {@code SystemDataLoader} strips them.
     *
     * @param raw The raw resource content.
     *
     * @return The content the loader parses.
     */
    private String strippedOfComments(String raw) {
        return raw.replaceAll("\\s*#.*[\\n\\r]{1}", "");
    }

    /**
     * Extract the subject the loader would extract.
     *
     * @param raw The raw resource content.
     *
     * @return The template subject.
     */
    private String packagedSubject(String raw) {
        Matcher subjectMatcher = Pattern.compile("\\s*Subject:(.*)[\\n\\r]{1}").matcher(strippedOfComments(raw));

        assertTrue(subjectMatcher.find(), "The loader can identify the template's subject.");

        return subjectMatcher.group(1).trim();
    }

    /**
     * Extract the body the loader would extract: everything after the Subject line.
     *
     * @param raw The raw resource content.
     *
     * @return The template body.
     */
    private String packagedBody(String raw) {
        String data = strippedOfComments(raw);

        int index = data.indexOf("\n");

        if (index < 0) {
            index = data.indexOf("\r");
        }

        assertTrue(index > 0, "The template body follows the Subject line.");

        return data.substring(index);
    }

    /*
     * Task 7.6 — Property 26: Leak containment across every outbound artefact, the
     * password-changed-notification arm.
     */

    private static final String[] LEAK_SECRET_SHAPES = { "plainsecret", " spaced secret ", "p@ss|word.{x}", "Ünïcödé-sëcret", "0123456789abcdef", "UPPER-lower-123" };

    private static final String[] LEAK_SCENARIOS = { "sent", "absent-template", "mail-failure" };

    /**
     * Each case is a scenario and the secret material the Account carries. The enumerated cases
     * cross every scenario with every secret shape; the fixed-seed random cases add generated
     * secrets, 21 enumerated plus 100 random.
     *
     * @return The generated cases.
     */
    private static Stream<Arguments> provideLeakContainmentCases() {
        List<Arguments> cases = new ArrayList<Arguments>();

        for (String scenario : LEAK_SCENARIOS) {
            for (String shape : LEAK_SECRET_SHAPES) {
                cases.add(Arguments.of(scenario, shape));
            }
        }

        Random random = new Random(GENERATOR_SEED);

        for (int i = 0; i < 100; i++) {
            cases.add(Arguments.of(LEAK_SCENARIOS[random.nextInt(LEAK_SCENARIOS.length)], Long.toHexString(random.nextLong()) + Long.toHexString(random.nextLong())));
        }

        return cases.stream();
    }

    /**
     * Property 26: Leak containment, password-changed-notification arm.
     *
     * Validates: Requirements 6.4, 13.2, 13.3, 14.4
     *
     * For any password value and token value held by the Account, the dispatched subject and body
     * and every log record written by the notification contain neither.
     *
     * @param scenario The dispatch outcome under test.
     * @param base The secret material the Account's password and token are derived from.
     */
    @ParameterizedTest(name = "[{index}] scenario={0}, base={1}")
    @MethodSource("provideLeakContainmentCases")
    public void testPasswordChangedNotificationLeakContainment(String scenario, String base) throws Exception {
        String password = "pw-" + base;
        String token = "tok." + base;

        user.setPassword(password);
        user.setSettings(new TreeMap<String, String>(settings("resetToken", token)));

        // The real packaged subject and body, so the artefact under assertion is the one shipped.
        String raw = readPackagedPasswordChangedTemplate();

        if ("absent-template".equals(scenario)) {
            when(emailTemplateRepo.findByNameOverride(CredentialNotificationService.PASSWORD_CHANGED_TEMPLATE)).thenReturn(null);
        } else {
            when(emailTemplateRepo.findByNameOverride(CredentialNotificationService.PASSWORD_CHANGED_TEMPLATE)).thenReturn(new EmailTemplate(CredentialNotificationService.PASSWORD_CHANGED_TEMPLATE, packagedSubject(raw), packagedBody(raw)));

            lenient().when(templateUtility.templateParameters(anyString(), any(String[][].class))).thenAnswer(invocation -> {
                String message = invocation.getArgument(0);
                String[][] parameters = invocation.getArgument(1);

                for (String[] parameter : parameters) {
                    message = message.replace("{" + parameter[0] + "}", parameter[1]);
                }

                return message;
            });
        }

        if ("mail-failure".equals(scenario)) {
            doThrow(new MessagingException("transport failure")).when(emailSender).sendEmail(anyString(), anyString(), anyString());
        }

        credentialNotificationService.notifyPasswordChanged(user);

        List<String> artefacts = new ArrayList<String>();

        artefacts.add(capturedLog());

        if ("absent-template".equals(scenario)) {
            verifyNoInteractions(emailSender);
        } else {
            ArgumentCaptor<String> subject = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<String> body = ArgumentCaptor.forClass(String.class);

            verify(emailSender).sendEmail(eq(TEST_EMAIL), subject.capture(), body.capture());

            assertTrue(body.getValue().contains("2024-05-06 07:08:09 UTC"), "The notification states the change time, got: " + body.getValue());

            artefacts.add(subject.getValue());
            artefacts.add(body.getValue());
        }

        for (String artefact : artefacts) {
            for (String secret : Arrays.asList(password, password.trim(), token, token.trim(), base, base.trim())) {
                assertFalse(artefact.contains(secret), "No password or token value may appear in an outbound artefact, found '" + secret + "' in: " + artefact);
            }
        }
    }

}
