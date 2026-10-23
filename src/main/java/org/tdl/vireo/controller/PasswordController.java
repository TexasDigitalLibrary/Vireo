package org.tdl.vireo.controller;

import static edu.tamu.weaver.response.ApiStatus.ERROR;
import static edu.tamu.weaver.response.ApiStatus.INVALID;
import static edu.tamu.weaver.response.ApiStatus.SUCCESS;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.Map;

import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.UserRepo;
import org.tdl.vireo.service.CredentialNotificationService;
import org.tdl.vireo.service.PasswordFeatureAvailabilityService;
import org.tdl.vireo.service.ResetTokenService;
import org.tdl.vireo.service.ResetTokenVerification;
import org.tdl.vireo.service.VireoEmailSender;
import org.tdl.vireo.utility.PasswordEligibilityRule;
import org.tdl.vireo.utility.PasswordValidator;
import org.tdl.vireo.utility.TemplateUtility;

import edu.tamu.weaver.auth.annotation.WeaverUser;
import edu.tamu.weaver.auth.service.CryptoService;
import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.validation.results.ValidationResults;
import edu.tamu.weaver.validation.utility.ValidationUtility;

/**
 * Vireo-managed password operations. No response carries a password value or reset token.
 */
@RestController
@RequestMapping("/password")
public class PasswordController {

    private static final Logger LOG = LoggerFactory.getLogger(PasswordController.class);

    @Value("${app.url}")
    private String url;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordFeatureAvailabilityService availabilityService;

    @Autowired
    private CredentialNotificationService credentialNotificationService;

    @Autowired
    private CryptoService cryptoService;

    @Autowired
    private ResetTokenService resetTokenService;

    @Autowired
    private TemplateUtility templateUtility;

    @Autowired
    private VireoEmailSender emailSender;

    /**
     * Replaces the password on the authenticated Account only; nothing in the request body can
     * target another Account. Checks run in order (availability, eligibility, current password,
     * new password) and nothing is written if any rejects.
     *
     * @param data {@code currentPassword}, {@code newPassword}, and {@code confirm}.
     */
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @RequestMapping(value = "/change", method = POST)
    public ApiResponse changePassword(@WeaverUser User user, @RequestBody Map<String, String> data) {

        if (!availabilityService.isChangeAvailable()) {
            LOG.debug("Rejected a password change, because the capability is not available.");

            return new ApiResponse(INVALID, "Changing your password is not available.");
        }

        if (!PasswordEligibilityRule.isEligible(user)) {
            LOG.debug("Rejected a password change, because the account is not eligible.");

            return new ApiResponse(INVALID, "This account does not use a Vireo-managed password.");
        }

        String currentPassword = data.get("currentPassword");
        String newPassword = data.get("newPassword");
        String confirm = data.get("confirm");

        if (!cryptoService.validatePassword(currentPassword, user.getPassword())) {
            LOG.debug("Authentication failed!");

            // Matches AuthController.login, so the client surfaces the failure the same way.
            ValidationResults failedAuthenticationResults = new ValidationResults();
            failedAuthenticationResults.addMessage(ValidationUtility.BUSINESS_MESSAGE_KEY, "password", "Authentication failed!");

            return new ApiResponse(INVALID, failedAuthenticationResults);
        }

        PasswordValidator.Result passwordResult = PasswordValidator.validate(newPassword, confirm);

        if (passwordResult != PasswordValidator.Result.ACCEPTED) {
            String passwordMessage = PasswordValidator.messageFor(passwordResult);
            LOG.debug(passwordMessage);

            return new ApiResponse(INVALID, passwordMessage);
        }

        // Encode the untrimmed value so surrounding whitespace is preserved.
        user.setPassword(cryptoService.encodePassword(newPassword));

        user = userRepo.update(user);

        credentialNotificationService.notifyPasswordChanged(user);

        return new ApiResponse(SUCCESS, "Your password has been changed.");
    }

    /**
     * Sends a single-use reset link to the holder of the Account identified by its email. The
     * stored password is never written on this path and no response carries a token or password
     * value; the administrator only learns a link was sent. Checks run in order (availability,
     * eligibility, template resolution) and nothing is sent if any rejects.
     *
     * @param data {@code email} identifying the target Account.
     */
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @RequestMapping(value = "/reset", method = POST)
    public ApiResponse initiateReset(@RequestBody Map<String, String> data) {

        if (!availabilityService.isResetAvailable()) {
            LOG.debug("Rejected a password reset, because the capability is not available.");

            return new ApiResponse(INVALID, "Resetting a password is not available.");
        }

        String email = data.get("email");

        User user = userRepo.findByEmail(email);

        if (!PasswordEligibilityRule.isEligible(user)) {
            LOG.debug("Rejected a password reset, because the account is not eligible.");

            return new ApiResponse(INVALID, "This account does not use a Vireo-managed password.");
        }

        EmailTemplate emailTemplate = credentialNotificationService.resolveTemplate(CredentialNotificationService.PASSWORD_RESET_REQUEST_TEMPLATE);

        if (emailTemplate == null) {
            LOG.debug("Unable to send a password reset, because the email template '" + CredentialNotificationService.PASSWORD_RESET_REQUEST_TEMPLATE + "' is not installed.");

            return new ApiResponse(ERROR, "The email template '" + CredentialNotificationService.PASSWORD_RESET_REQUEST_TEMPLATE + "' is not installed.");
        }

        String token = resetTokenService.construct(user);

        String resetLink = url + "/reset-password?token=" + token;

        String content = templateUtility.templateParameters(emailTemplate.getMessage(), new String[][] { { "RESET_URL", resetLink } });

        try {
            emailSender.sendEmail(user.getEmail(), emailTemplate.getSubject(), content);
        } catch (MessagingException e) {
            LOG.debug("Unable to send a password reset email, because: " + e.getMessage());

            return ApiResponse.fromException(ERROR, "Unable to send the password reset email.", e);
        }

        return new ApiResponse(SUCCESS, "A password reset link has been sent.");
    }

    /**
     * Redeems a reset token, replacing the stored password of the Account the token names.
     * Deliberately un-annotated: this handler is anonymously reachable exactly as
     * {@code /auth/register} is, because redemption happens before sign-in. Checks run in order
     * (availability, token verification, eligibility, new password) and nothing is written if any
     * rejects; a rejected candidate leaves the token itself usable until it expires. No response
     * carries the Account_Email, the token, or the stored hash, and a successful redemption leaves
     * the caller signed out.
     *
     * @param data {@code token}, {@code newPassword}, and {@code confirm}.
     */
    @RequestMapping(value = "/reset/redeem", method = POST)
    public ApiResponse redeemReset(@RequestBody Map<String, String> data) {

        if (!availabilityService.isResetAvailable()) {
            LOG.debug("Rejected a password reset redemption, because the capability is not available.");

            return new ApiResponse(INVALID, "Resetting a password is not available.");
        }

        String token = data.get("token");

        ResetTokenVerification verification = resetTokenService.verify(token);

        if (verification.getStatus() == ResetTokenVerification.Status.EXPIRED) {
            LOG.debug("Rejected a password reset redemption, because the token has expired.");

            return new ApiResponse(INVALID, "This password reset link has expired. Please ask an administrator to send a new one.");
        }

        if (verification.getStatus() != ResetTokenVerification.Status.VALID) {
            LOG.debug("Rejected a password reset redemption, because the token is not valid.");

            return new ApiResponse(INVALID, "This password reset link is no longer valid.");
        }

        User user = userRepo.findByEmail(verification.getEmail());

        if (!PasswordEligibilityRule.isEligible(user)) {
            LOG.debug("Rejected a password reset redemption, because the account is not eligible.");

            return new ApiResponse(INVALID, "This account does not use a Vireo-managed password.");
        }

        String newPassword = data.get("newPassword");
        String confirm = data.get("confirm");

        PasswordValidator.Result passwordResult = PasswordValidator.validate(newPassword, confirm);

        if (passwordResult != PasswordValidator.Result.ACCEPTED) {
            String passwordMessage = PasswordValidator.messageFor(passwordResult);
            LOG.debug(passwordMessage);

            // Leave the token usable: it is unaffected by a rejected candidate.
            return new ApiResponse(INVALID, passwordMessage);
        }

        // Encode the untrimmed value so surrounding whitespace is preserved.
        user.setPassword(cryptoService.encodePassword(newPassword));

        user = userRepo.update(user);

        // The new stored hash invalidates this token and every sibling token for this Account.
        credentialNotificationService.notifyPasswordChanged(user);

        return new ApiResponse(SUCCESS, "Your password has been set. Please sign in.");
    }

}
