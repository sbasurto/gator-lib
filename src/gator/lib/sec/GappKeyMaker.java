package gator.lib.sec;

import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.KeyGenerator;

/** Generates keys in the existing AES-128 / Base64 format. */
public class GappKeyMaker {
    public String getNewKey() {
        try {
            KeyGenerator generator = KeyGenerator.getInstance("AES");
            generator.init(128);
            return Base64.getEncoder().encodeToString(generator.generateKey().getEncoded());
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("AES is unavailable", ex);
        }
    }
}
