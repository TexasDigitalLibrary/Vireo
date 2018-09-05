package org.tdl.vireo.model.converter;

import java.security.Key;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class CryptoConverter implements AttributeConverter<String, String> {

    private static final String ALGORITHM = "AES/ECB/PKCS5Padding";

    private static byte[] KEY;

    public static void setKey(String secret) {
        KEY = secret.getBytes();
    }

    @Override
    public String convertToDatabaseColumn(String entityValue) {
        Key key = new SecretKeySpec(KEY, "AES");
        try {
            Cipher c = Cipher.getInstance(ALGORITHM);
            c.init(Cipher.ENCRYPT_MODE, key);
            return new String(Base64.getEncoder().encode(c.doFinal(entityValue.getBytes())));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String convertToEntityAttribute(String dbValue) {
        Key key = new SecretKeySpec(KEY, "AES");
        try {
            Cipher c = Cipher.getInstance(ALGORITHM);
            c.init(Cipher.DECRYPT_MODE, key);
            return new String(c.doFinal(Base64.getDecoder().decode(dbValue)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
