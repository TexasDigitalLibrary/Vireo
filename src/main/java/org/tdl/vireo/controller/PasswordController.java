package org.tdl.vireo.controller;

import static edu.tamu.weaver.response.ApiStatus.INVALID;
import static edu.tamu.weaver.response.ApiStatus.SUCCESS;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.UserRepo;
import org.tdl.vireo.service.CredentialNotificationService;
import org.tdl.vireo.service.PasswordFeatureAvailabilityService;
import org.tdl.vireo.utility.PasswordEligibilityRule;
import org.tdl.vireo.utility.PasswordValidator;

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

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordFeatureAvailabilityService availabilityService;

    @Autowired
    private CredentialNotificationService credentialNotificationService;

    @Autowired
    private CryptoService cryptoService;

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

}
