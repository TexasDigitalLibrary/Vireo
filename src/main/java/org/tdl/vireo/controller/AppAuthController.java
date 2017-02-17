package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.ERROR;
import static edu.tamu.framework.enums.ApiResponseType.INVALID;
import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.tdl.vireo.enums.AppRole;
import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.EmailTemplateRepo;
import org.tdl.vireo.model.repo.UserRepo;
import org.tdl.vireo.service.DefaultSubmissionListColumnService;
import org.tdl.vireo.util.TemplateUtility;

import com.fasterxml.jackson.core.JsonProcessingException;

import edu.tamu.framework.aspect.annotation.ApiData;
import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.ApiParameters;
import edu.tamu.framework.controller.CoreAuthController;
import edu.tamu.framework.model.ApiResponse;
import edu.tamu.framework.util.ValidationUtility;
import edu.tamu.framework.validation.ValidationResults;

@Controller
@ApiMapping("/auth")
public class AppAuthController extends CoreAuthController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final static String EMAIL_VERIFICATION_TYPE = "EMAIL_VERIFICATION";

    public static final String REGISTRATION_TEMPLATE = "SYSTEM New User Registration";

    @Value("${app.host}")
    private String host;

    @Value("${server.port}")
    private String port;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private TemplateUtility templateUtility;

    @Autowired
    private EmailTemplateRepo emailTemplateRepo;

    @Autowired
    private DefaultSubmissionListColumnService defaultSubmissionViewColumnService;

    @ApiMapping(value = "/register", method = { POST, GET })
    public ApiResponse registration(@ApiData Map<String, String> dataMap, @ApiParameters Map<String, String[]> parameters) {

        if(parameters.get("email") != null) {

            String email = parameters.get("email")[0];

            if(userRepo.findByEmail(email) != null) {
                logger.debug("Account with email " + email + " already exists!");
                ValidationResults invalidEmail = new ValidationResults();
                invalidEmail.addMessage(ValidationUtility.BUSINESS_MESSAGE_KEY, "verify", "Account with email " + email + " already exists!");
                return new ApiResponse(INVALID, invalidEmail);
            }

            EmailTemplate emailTemplate = emailTemplateRepo.findByNameOverride(REGISTRATION_TEMPLATE);

            String content = "";

            try {
                content = templateUtility.templateParameters(emailTemplate.getMessage(), new String[][] { { "REGISTRATION_URL", host + ":" + port + "/register?token=" + authUtility.generateToken(email, EMAIL_VERIFICATION_TYPE) } });
            } catch (InvalidKeyException | NoSuchPaddingException | NoSuchAlgorithmException | IllegalBlockSizeException | BadPaddingException e1) {
                logger.debug("Unable to generate token! " + email);
                return new ApiResponse(ERROR, "Unable to generate token! " + email);
            }

            try {
                emailSender.sendEmail(email, emailTemplate.getSubject(), content);
            } catch (MessagingException e) {
                logger.debug("Unable to send email! " + email);
                return new ApiResponse(ERROR, "Unable to send email! " + email);
            }

            return new ApiResponse(SUCCESS, "An email has been sent to " + email + ". Please confirm email to continue registration.", parameters);
        }

        String token     = dataMap.get("token");
        String firstName = dataMap.get("firstName");
        String lastName  = dataMap.get("lastName");
        String password  = dataMap.get("password");
        String confirm   = dataMap.get("confirm");

        if((firstName == null || firstName.trim().length() == 0) && (lastName == null || lastName.trim().length() == 0)) {
            logger.debug("Either a first or last name is required!");
            return new ApiResponse(ERROR, "Either a first or last name is required!");
        }

        if(password == null || password.trim().length() == 0) {
            logger.debug("Registration requires a password!");
            return new ApiResponse(ERROR, "Registration requires a password!");
        }

        if(password != null && !password.equals(confirm)) {
            logger.debug("The passwords do not match!");
            return new ApiResponse(ERROR, "The passwords do not match!");
        }

        if(password != null && password.trim().length() < 6) {
            logger.debug("Password must be greater than 6 characters!");
            return new ApiResponse(ERROR, "Password must be greater than 6 characters!");
        }

        String[] content = null;
        try {
            content = authUtility.validateToken(token, EMAIL_VERIFICATION_TYPE);
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) {
            logger.debug("Unable to validate token!");
            return new ApiResponse(ERROR, "Unable to generate token!");
        }

        String tokenCreateTime = content[0];
        String email = content[1];

        Long tokenDaysOld = TimeUnit.MILLISECONDS.toDays(Long.valueOf(tokenCreateTime) - new Date().getTime());

        if(tokenDaysOld >= 2 ) {
            logger.debug("Token has expired!");
            return new ApiResponse(ERROR, "Token has expired! Please begin registration again.");
        }

        User user = userRepo.create(email, firstName, lastName, AppRole.STUDENT);

        user.setPassword(authUtility.encodePassword(password));

        user.setSubmissionViewColumns(defaultSubmissionViewColumnService.getDefaultSubmissionListColumns());

        user = userRepo.save(user);

        return new ApiResponse(SUCCESS, "Registration was successfull. Please login.", user);
    }

    @ApiMapping(value = "/login", method = POST)
    public ApiResponse login(@ApiData Map<String, String> dataMap) {

        String email = dataMap.get("email");
        String password = dataMap.get("password");

        User user = userRepo.findByEmail(email);

        if(user == null) {
            logger.debug("No user found with email " + email + "!");
            ValidationResults invalidEmail = new ValidationResults();
            invalidEmail.addMessage(ValidationUtility.BUSINESS_MESSAGE_KEY, "login", "No user found with email " + email + "!");
            return new ApiResponse(INVALID, invalidEmail);
        }

        if(!authUtility.validatePassword(password, user.getPassword())) {
            logger.debug("Authentication failed!");
            ValidationResults failedAuthenticationResults = new ValidationResults();
            failedAuthenticationResults.addMessage(ValidationUtility.BUSINESS_MESSAGE_KEY, "login", "Authentication failed!");
            return new ApiResponse(INVALID, failedAuthenticationResults);
        }

        try {
            Map<String, String> userMap = new HashMap<String, String>();
            userMap.put("lastName", user.getLastName());
            userMap.put("firstName", user.getFirstName());
            userMap.put("netid", user.getNetid());
            userMap.put("uin", String.valueOf(user.getUin()));
            userMap.put("email", user.getEmail());
            return new ApiResponse(SUCCESS, jwtUtility.makeToken(userMap));
        } catch (InvalidKeyException | JsonProcessingException | NoSuchAlgorithmException | IllegalStateException | UnsupportedEncodingException e) {
            logger.debug("Unable to generate token!");
            return new ApiResponse(ERROR, "Unable to generate token!");
        }
    }

}
