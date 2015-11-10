package org.tdl.vireo.util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.tdl.vireo.model.User;

import com.fasterxml.jackson.core.JsonProcessingException;

import edu.tamu.auth.model.jwt.JWTtoken;

@Service
public class AuthUtility {
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    private final static String ENCRYPTION_ALGORITHM = "AES";
    private final static String RAW_DATA_DELIMETER   = ":";
    
    @Value("${app.security.secret}")
    private String secret;
    
    @Value("${auth.security.jwt.secret-key}")
    private String secretKey;
        
    @Value("${auth.security.jwt.expiration}")
    private Long expiration;

    public String generateToken(String content, String type) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {        
    	Date now =  new Date();
        String rawToken = now.getTime() + RAW_DATA_DELIMETER + content + RAW_DATA_DELIMETER + type;
        SecretKeySpec skeySpec = new SecretKeySpec(secret.getBytes(), ENCRYPTION_ALGORITHM);
        Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        return Base64.encodeBase64URLSafeString(cipher.doFinal(rawToken.getBytes()));
    }
    
    public String[] validateToken(String token, String type) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        SecretKeySpec skeySpec = new SecretKeySpec(secret.getBytes(), ENCRYPTION_ALGORITHM);
        Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        return new String(cipher.doFinal(Base64.decodeBase64(token))).split(RAW_DATA_DELIMETER);
    }
    
    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }
    
    public boolean validatePassword(String password, String encodedPassword) {
       return passwordEncoder.matches(password, encodedPassword);
    }
    
    public JWTtoken makeToken(User user) throws InvalidKeyException, JsonProcessingException, NoSuchAlgorithmException, IllegalStateException, UnsupportedEncodingException {        
        JWTtoken token = new JWTtoken(secretKey, expiration);        
        token.makeClaim("lastName", user.getLastName());
        token.makeClaim("firstName", user.getFirstName());
        token.makeClaim("netid", user.getNetid());
        token.makeClaim("uin", String.valueOf(user.getUin()));
        token.makeClaim("email", user.getEmail());
        return token;       
    }
        
}

