package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.ERROR;
import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;

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

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.tdl.vireo.enums.Role;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.UserRepo;
import org.tdl.vireo.util.AuthUtility;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.Data;
import edu.tamu.framework.aspect.annotation.Parameters;
import edu.tamu.framework.model.ApiResponse;
import edu.tamu.framework.util.EmailUtility;

@Controller
@ApiMapping("/auth")
public class AuthController {

private final static String EMAIL_VERIFICATION_TYPE = "EMAIL_VERIFICATION";
    
    @Value("${app.host}")
    private String host;
    
    @Value("${server.port}")
    private String port;
    
    @Autowired
    private UserRepo userRepo;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private AuthUtility authUtility;
    
    @Autowired
    private EmailUtility emailUtility;
    
    private static final Logger logger = Logger.getLogger(AuthController.class);
    
    @ApiMapping(value = "/register")
    public ApiResponse registration(@Data String data, @Parameters Map<String, String[]> parameters) {
        
        if(parameters.get("email") != null) {
            
            String email = parameters.get("email")[0];
            
            if(userRepo.findByEmail(email) != null) {
                logger.debug("Account with email " + email + " already exists!");                
                return new ApiResponse(ERROR, "Account with email " + email + " already exists!");
            }
            
            String subject = "Vireo 4 Registration";
            String content = "Verify email to continue registration.\n\n";
           
            try {
                content += host + ":" + port + "/register?token=" + authUtility.generateToken(email, EMAIL_VERIFICATION_TYPE);
            } catch (InvalidKeyException | NoSuchPaddingException | NoSuchAlgorithmException | IllegalBlockSizeException | BadPaddingException e1) {
                logger.debug("Unable to generate token! " + email);
                return new ApiResponse(ERROR, "Unable to generate token! " + email);
            }
            
            try {
                emailUtility.sendEmail(email, subject, content);
            } catch (MessagingException e) {                
                logger.debug("Unable to send email! " + email);                
                return new ApiResponse(ERROR, "Unable to send email! " + email);
            }
            
            return new ApiResponse(SUCCESS, parameters);
        }
        
        Map<String, String> dataMap = new HashMap<String, String>();      
        try {
            dataMap = objectMapper.readValue(data, new TypeReference<HashMap<String, String>>(){});
        } catch (Exception e) {
            e.printStackTrace();
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
        
        User user = userRepo.create(email, firstName, lastName, Role.USER);
        user.setPassword(authUtility.encodePassword(password));
        user = userRepo.save(user);
        
        return new ApiResponse(SUCCESS, user);
    }
    
    @ApiMapping("/login")
    public ApiResponse login(@Data String data) {
        
        Map<String,String> dataMap = new HashMap<String,String>();      
        try {
            dataMap = objectMapper.readValue(data, new TypeReference<HashMap<String,String>>(){});
        } catch (Exception e) {
            e.printStackTrace();
        }  
        
        String email = dataMap.get("email");
        String password = dataMap.get("password");
        
        User user = userRepo.findByEmail(email);
        
        if(user == null) {
            logger.debug("No user found with email " + email + "!");
            return new ApiResponse(ERROR, "No user found with email " + email + "!");
        }
        
        if(!authUtility.validatePassword(password, user.getPassword())) {
            logger.debug("Authentication failed!");
            return new ApiResponse(ERROR, "Authentication failed!");
        }
        
        try {
            return new ApiResponse(SUCCESS, authUtility.makeToken(user));
        } catch (InvalidKeyException | JsonProcessingException | NoSuchAlgorithmException | IllegalStateException | UnsupportedEncodingException e) {
            logger.debug("Unable to generate token!");
            return new ApiResponse(ERROR, "Unable to generate token!");
        }
    }
    
}
