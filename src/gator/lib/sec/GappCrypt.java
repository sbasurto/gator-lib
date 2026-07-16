/*
 * Copyright (C) 2023 Sergio Basurto Juárez
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package gator.lib.sec;


import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.io.FileInputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.KeyPair;
import java.security.cert.Certificate;
import javax.crypto.Cipher;
import gator.lib.io.files.GappFiles;
import gator.lib.logs.GappLogging;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * GatorCrypt is the class that will manage all the crypt for SOFTGATOR APPS
 * keytool -genkeypair -keyalg RSA -keystore [certStore] -alias [keyAlias]
 * openssl pkcs12 -export -name softgkeys -in newSoftCert -inkey privkey.pem -out keystore.p12
 * keytool -importkeystore -destkeystore softgKeys -srckeystore keystore.p12 -srcstoretype pkcs12 -alias softgKeys
 * openssl x509 -in /etc/letsencrypt/live/soft-gator.com/cert.pem -pubkey -noout &gt;&gt; llavePublica.txt
 * The key given by SAT authority is in format PKCS#8 DER.
 * openssl pkcs8 -inform DER -outform PEM -in Claveprivada_FIEL.key -out Claveprivada_FIEL.key.pem
 * openssl dgst -sha512 -sign Claveprivada_FIEL.key.pem -out ocprueba.pdf.sha512 ocprueba.pdf
 * openssl enc -base64 -in ocprueba.pdf.sha512 -out ocprueba.pdf.sha512.base64
 * openssl enc -d -A -base64 -in signature.txt -out signature.sha512
 * openssl dgst -sha512 -verify Claveprivada_FIEL.key.pem -signature ocprueba.pdf.sha512 ocprueba.pdf
 * 
 * openssl pkcs12 -export -name softgkeys2 -in /etc/letsencrypt/live/soft-gator.com/cert.pem -inkey /etc/letsencrypt/live/soft-gator.com/privkey.pem -out keystore.p12
 * keytool -importkeystore -destkeystore softgWSKeys -srckeystore keystore.p12 -srcstoretype pkcs12 -alias softgkeys2
 * openssl x509 -in /etc/letsencrypt/live/soft-gator.com/cert.pem -pubkey -noout &gt; /var/www/localhost/htdocs/webservice/llavePublica.txt
 * @author <a href="mailto:sbasurto@soft-gator.com">Sergio Basurto Juárez</a>
 * @version     0.1, 14 JUn 2016
 * 
 */
public class GappCrypt {
        /**
	 * File manager for debugger.
	 */
	private final GappFiles gappFiles = new GappFiles();
        /**
         * Log instance to do the log.
         */
        private GappLogging logs;
        
        private String where2look = "default";
        private String alias = "softgKeys";
        private String [] approvedKeys = new String[1];
        private SecretKey secKey;
        private byte[] iv;
        
        /**
         * Receive the keyring to be used.
         * @param where2look Where the key is stored.
         */
        public GappCrypt(String where2look) {
            this.where2look = where2look;
            this.logs  = new GappLogging();
        }
        
        /**
         * Receive the keyring to be used and and alias to retrieve information.
         * @param where2look Location where the key is stored.
         * @param alias An alias to retrieve the key.
         */
        public GappCrypt(String where2look, String alias) {
            this.where2look = where2look;
            this.logs  = new GappLogging();
        }
        
        /**
         * Allow to sign any string.
         * @param toSig The string to be signed.
         * @return Sing of the string.
         */
        public String signString(String toSig) {
            return this.signString(toSig, false);
        }
        /**
         * For validating the system will call an URL that will tell us where to find the 
         * key pair the alias and the password.
         * @param toSign The string to be signed.
         * @param mustCount A flag telling if the failed will be counted.
         * @return String signed.
         */        
	public String signString(String toSign, boolean mustCount) {
            try {                
                KeyPair pair = getKeyPair(mustCount);
                byte[] data = toSign.getBytes("UTF8");
                Signature sign = Signature.getInstance("SHA512withRSA");
                sign.initSign(pair.getPrivate());
                sign.update(data);
                byte [] signedBytes = sign.sign();
                return new String(GappBase64.encode(signedBytes));
            } catch (Exception ex) {
                logs.logIt(this.getClass().getCanonicalName(), logs.getStackTraceString(ex), "security", "signString", 0);
                return "";
            }
	}
        /**
         * Get the key pair from the keyring defined.
         * @param mustCount If failures must be logged for count.
         * @return A key pair object with the keys ready to be used.
         */
	private KeyPair getKeyPair(boolean mustCount) {
            String serviceUrl = getSetting("gator.keys.url", "GATOR_KEYS_URL");
            String apiKey = getSetting("gator.keys.apiKey", "GATOR_KEYS_API_KEY");
            if(serviceUrl.isBlank() || apiKey.isBlank()) {
                return getKeyPairLocal(mustCount);
            }
            String url = serviceUrl + "?who=" + URLEncoder.encode(where2look, StandardCharsets.UTF_8)
                    + "&apikey=" + URLEncoder.encode(apiKey, StandardCharsets.UTF_8);
            try {                
                logs.logIt(this.getClass().getName(), "Requesting remote key pair", "security", "getKeyPair", 0);
                String where = gappFiles.readUrl(url);
                GappKeystore gappKeystore = this.getKeyStore(where);
                logs.logIt(this.getClass().getName(), gappKeystore.toString(), "", "", 0);
                KeyStore store = KeyStore.getInstance(KeyStore.getDefaultType());                
                store.load(new FileInputStream(gappKeystore.getStoreFile()), gappKeystore.getStorePassword().toCharArray());
                Key key = (PrivateKey) store.getKey(gappKeystore.getStoreKey(), gappKeystore.getStorePassword().toCharArray());
                Certificate cert = store.getCertificate(gappKeystore.getStoreKey());
                PublicKey publicKey = cert.getPublicKey();
                return new KeyPair(publicKey, (PrivateKey) key);
            } catch (Exception ex) {                
                logs.logIt(this.getClass().getCanonicalName(), logs.getStackTraceString(ex), "security", "getKeyPair", 0);
                if(mustCount) {
                    gappFiles.setDir(GappFiles.CONF_DIR);
                    gappFiles.write2File("-\n", GappFiles.CONF_DIR + "tries");
                }
                return getKeyPairLocal(mustCount);
            }
	}
        private GappKeystore getKeyStore(String stringToProc) {
                Gson gson = new Gson();
                GappKeystore gappKeyStore;
                try {
                        gappKeyStore = gson.fromJson(stringToProc, GappKeystore.class); 
                        gappKeyStore.setStoreFile(GappFiles.CONF_DIR + gappKeyStore.getStoreFile());
                } catch (JsonSyntaxException ex) {
                        logs.logIt(this.getClass().getCanonicalName(), logs.getStackTraceString(ex), "security", "getKeyPair", 0);
                        String []stuff = stringToProc.split("::@@::");
                        gappKeyStore = new GappKeystore();
                        gappKeyStore.setStoreFile(stuff[0]);
                        gappKeyStore.setStorePassword(stuff[1]);
                        gappKeyStore.setStoreKey(alias);
                }
                return gappKeyStore;
        }
        /**
         * Get key pair from a local keyring.
         * @param mustCount If failures must be logged for count.
         * @return A key pair object with the keys ready to be used.
         */
        private KeyPair getKeyPairLocal(boolean mustCount) {
            try {
                int howmany = 0;
                if(mustCount){
                    gappFiles.setDir(GappFiles.CONF_DIR);
                    gappFiles.setFileName("tries");
                    gappFiles.readFromFile(GappFiles.CONF_DIR + "tries");                
                    howmany = gappFiles.getReadedLines().length;
                }                                
                if(howmany > 500 && mustCount) {
                    logs.logIt(this.getClass().getCanonicalName(), "more than allowed", "security", "getKeyPairLocal", 0);
                    return null;
                } else {
                    String storeFile = getSetting("gator.keystore.file", "GATOR_KEYSTORE_FILE");
                    String storePassword = getSetting("gator.keystore.password", "GATOR_KEYSTORE_PASSWORD");
                    String storeAlias = getSetting("gator.keystore.alias", "GATOR_KEYSTORE_ALIAS");
                    if(storeFile.isBlank() || storePassword.isBlank()) {
                        logs.logIt(this.getClass().getCanonicalName(), "Keystore is not configured", "security", "getKeyPairLocal", 0);
                        return null;
                    }
                    if(storeAlias.isBlank()) storeAlias = alias;
                    KeyStore store = KeyStore.getInstance(KeyStore.getDefaultType());                
                    store.load(new FileInputStream(storeFile), storePassword.toCharArray());
                    Key key = (PrivateKey) store.getKey(storeAlias, storePassword.toCharArray());
                    Certificate cert = store.getCertificate(storeAlias);
                    PublicKey publicKey = cert.getPublicKey();
                    return new KeyPair(publicKey, (PrivateKey) key);
                }
            } catch (Exception ex) {
                logs.logIt(this.getClass().getCanonicalName(), logs.getStackTraceString(ex), "security", "getKeyPairLocal", 0);
                return null;
            }
        }
        private static String getSetting(String property, String environment) {
            String value = System.getProperty(property);
            return value == null || value.isBlank() ? System.getenv().getOrDefault(environment, "") : value;
        }
        /**
         * Verify if a sign is valid.
         * @param toVerify  String to be verified.
         * @param signature Signature.
         * @return True if sign is right, false otherwise.
         */
        public boolean isSignRight(String toVerify, String signature) {
            return this.isSignRight(toVerify, signature, false);
        }
        /**
         * Verify if a sign is valid and allow to define if must count failures.
         * @param toVerify String to be verified.
         * @param signature Signature.
         * @param mustCount If failures must be logged for count.
         * @return True if sign is right, false otherwise.
         */
        public boolean isSignRight(String toVerify, String signature, boolean mustCount) {
            try{
                logs.logIt(this.getClass().getCanonicalName(), toVerify, "", "", 8);
                byte []data = toVerify.getBytes();
                byte []signedBytes = GappBase64.decode(signature);
                KeyPair pair = getKeyPair(mustCount);                
                Signature sign = Signature.getInstance("SHA512withRSA");
                sign.initVerify(pair.getPublic());
                sign.update(data);
                return sign.verify(signedBytes);
            } catch(Exception ex) {
                logs.logIt(this.getClass().getCanonicalName(), logs.getStackTraceString(ex), "security", "isSignRight", 0);
                return false;
            }
        }
        /**
         * Crypt a string.
         * @param _toCrypt String to be crypt.
         * @return The string ciphered.
         */
        public String crytpString(String _toCrypt) {
            return this.crytpString(_toCrypt, false);
        }
        /**
         * Crypt a string.
         * @param _toCrypt String to be crypt.
         * @param mustCount If failures must be logged for count.
         * @return The string ciphered.
         */
        public String crytpString(String _toCrypt, boolean mustCount) {
            try {
                KeyPair pair = getKeyPair(mustCount);
                Cipher cipher = Cipher.getInstance("RSA");
                cipher.init(Cipher.ENCRYPT_MODE, pair.getPublic());
                byte[] cypted = cipher.doFinal(_toCrypt.getBytes());
                return new String(GappBase64.encode(cypted));
            }catch(Exception ex){
                logs.logIt(this.getClass().getCanonicalName(), logs.getStackTraceString(ex), "security", "crytpString", 0);
                return _toCrypt;
            }
        }
        /**
         * Decrypt a string.
         * @param _toDecrypt String to be decrypted.
         * @return The original string.
         */
        public String decryptString(String _toDecrypt) {
            return this.decryptString(_toDecrypt, false);
        }
        /**
         * Decrypt a string.
         * @param _toDecrypt String to be decrypted.
         * @param mustCount If failures must be logged for count.
         * @return The original string.
         */
        public String decryptString(String _toDecrypt, boolean mustCount) {
            try {
                KeyPair pair = getKeyPair(mustCount);
                Cipher cipher = Cipher.getInstance("RSA");
                cipher.init(Cipher.DECRYPT_MODE, pair.getPrivate());                
                logs.logIt(this.getClass().getSimpleName(), "_toDec: " + _toDecrypt, "security", "crytpString", 6);
                byte[] decrypted = cipher.doFinal(GappBase64.decode(_toDecrypt));
                return new String(decrypted);
            }catch(Exception ex) {
                logs.logIt(this.getClass().getCanonicalName(), logs.getStackTraceString(ex), "security", "crytpString", 0);
                return "Can not crypt see logs";
            }
        }
        /**
         * Generate and AES key pair.
         */
        private void generateAESKey() {
            try {
                KeyGenerator generator = KeyGenerator.getInstance("AES");
                generator.init(256); // The AES key size in number of bits
                secKey = generator.generateKey();
            } catch (NoSuchAlgorithmException ex) {
                logs.logIt(this.getClass().getCanonicalName(), logs.getStackTraceString(ex), "security", "crytpString", 0);
            }
        }
        /**
         * Get the AES key.
         * @return String representation of AES key as base 64 encode.
         */
        public String getAESKey() {
                if(secKey == null) generateAESKey();                
                return new String(GappBase64.encode(secKey.getEncoded()));
        }
        /**
         * Get the AES key.
         * @return String representation of AES key as base 64 encode.
         */
        public SecretKey getAESSecretKey() {
                if(secKey == null) generateAESKey();                
                return this.secKey;
        }
        /**
         * Set an AES key.
         * @param strSecKey The string representation of an AES key.
         */
        public void setAESKey(String strSecKey) {
            byte [] decryptedKey = GappBase64.decode(strSecKey);
            secKey =  new SecretKeySpec(decryptedKey , 0, decryptedKey.length, "AES");
        }
        /**
         * Crypt a string with an AES key.
         * @param _toCrypt String to be crypt.
         * @return The string ciphered.
         */
        public String crytpStringAES(String _toCrypt) {
            try {
                if(secKey == null) generateAESKey();                
                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");                               
                cipher.init(Cipher.ENCRYPT_MODE, this.getAESSecretKey());
                byte[] cypted = cipher.doFinal(_toCrypt.getBytes());
                setIV(cipher.getIV());
                return new String(GappBase64.encode(cypted));
            }catch(Exception ex){
                logs.logIt(this.getClass().getCanonicalName(), logs.getStackTraceString(ex), "security", "crytpStringAES", 0);
                return _toCrypt;
            }
        }        
        /**
         * Crypt a string with an AES key.
         * @param _toCrypt String to be crypt.
         * @param _iv Secure random bytes.
         * @return The string ciphered.
         */
        public String crytpStringAES(String _toCrypt, String _iv) {
            try {
                if(secKey == null) generateAESKey(); 
                setIV(_iv);
                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");                               
                cipher.init(Cipher.ENCRYPT_MODE, this.getAESSecretKey(), this.getIVParam());
                byte[] cypted = cipher.doFinal(_toCrypt.getBytes());
                return new String(GappBase64.encode(cypted));
            }catch(Exception ex){
                logs.logIt(this.getClass().getCanonicalName(), logs.getStackTraceString(ex), "security", "crytpStringAES", 0);
                return _toCrypt;
            }
        }
        /**
         * Decrypt a string ciphered with an AES key.
         * @param _toDecrypt String to decrypt.
         * @param strSecKey The AES key as a string.
         * @return The original string.
         */
        public String decryptStringAES(String _toDecrypt, String strSecKey) {
            try {
                setAESKey(strSecKey);
                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                cipher.init(Cipher.DECRYPT_MODE, this.getAESSecretKey());
                logs.logIt(this.getClass().getSimpleName(), "_toDec: " + _toDecrypt, "security", "decryptStringAES", 6);
                byte[] decrypted = cipher.doFinal(GappBase64.decode(_toDecrypt));
                return new String(decrypted);
            }catch(Exception ex) {
                logs.logIt(this.getClass().getCanonicalName(), logs.getStackTraceString(ex), "security", "decryptStringAES", 0);
                System.out.println(logs.getStackTraceString(ex));
                return "Can not decrypt see logs";
            }
        }
        /**
         * Decrypt a string ciphered with an AES key.
         * @param _toDecrypt String to decrypt.
         * @param strSecKey The AES key as a string.
         * @param _iv Secure random string.
         * @return The original string.
         */
        public String decryptStringAES(String _toDecrypt, String strSecKey, String _iv) {
            try {
                setAESKey(strSecKey);
                setIV(_iv);
                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                cipher.init(Cipher.DECRYPT_MODE, this.getAESSecretKey(), this.getIVParam());
                logs.logIt(this.getClass().getSimpleName(), "_toDec: " + _toDecrypt, "security", "decryptStringAES", 6);
                byte[] decrypted = cipher.doFinal(GappBase64.decode(_toDecrypt));
                return new String(decrypted);
            }catch(Exception ex) {
                logs.logIt(this.getClass().getCanonicalName(), logs.getStackTraceString(ex), "security", "decryptStringAES", 0);
                System.out.println(logs.getStackTraceString(ex));
                return "Can not decrypt see logs";
            }
        }
        /**
         * Get the public key from a PEM string.
         * @param pubKeyStr The string representing the public key.
         * @return The RSA public key.
         */
        private RSAPublicKey getPublicKeyFromPem(String pubKeyStr) {
                String publicKeyPEM = pubKeyStr;
                publicKeyPEM = publicKeyPEM.replaceAll("-----BEGIN PUBLIC KEY-----[\n]*", "");
                publicKeyPEM = publicKeyPEM.replace("-----END PUBLIC KEY-----", "");
                System.out.println("PEM:" + publicKeyPEM + ":PEM");
                byte[] encoded = GappBase64.decode(publicKeyPEM);
                try {
                        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                        RSAPublicKey pubKey = (RSAPublicKey) keyFactory.generatePublic(new X509EncodedKeySpec(encoded));
                        return pubKey;                
                } catch (Exception ex) {
                        logs.logIt(this.getClass().getCanonicalName(), logs.getStackTraceString(ex), "security", "getPublicKeyFromPem", 0);
                        System.out.println(logs.getStackTraceString(ex));
                        return null;
                }
        }
        /**
         * Get the private key from a PEM string.
         * @param privateKey The string representing the private key.
         * @return The RSA private key.
         */
        private RSAPrivateKey getPrivateKeyFromPem(String privateKey) {
                try {
                        String privateKeyPEM = privateKey;
                        privateKeyPEM = privateKeyPEM.replace("-----BEGIN PRIVATE KEY-----\n", "");
                        privateKeyPEM = privateKeyPEM.replace("-----END PRIVATE KEY-----", "");
                        byte[] encoded = GappBase64.decode(privateKeyPEM);
                        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
                        RSAPrivateKey privKey = (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
                        return privKey;
                } catch(Exception e) {
                        logs.logIt(this.getClass().getCanonicalName(), logs.getStackTraceString(e), "security", "getPrivateKeyFromPem", 0);
                        System.out.println(logs.getStackTraceString(e));
                        return null;                        
                }
        }
        /**
         * Encrypt a string with a PEM key.
         * @param _toCrypt String to be crypt.
         * @param pem The public key as PEM string.
         * @return The string ciphered.
         */
        public String crytpStringWithPEM(String _toCrypt, String pem) {
            try {                
                Cipher cipher = Cipher.getInstance("RSA");
                cipher.init(Cipher.ENCRYPT_MODE, getPublicKeyFromPem(pem));
                byte[] cypted = cipher.doFinal(_toCrypt.getBytes());
                return new String(GappBase64.encode(cypted));
            }catch(Exception ex){
                System.out.println(logs.getStackTraceString(ex));
                logs.logIt(this.getClass().getCanonicalName(), logs.getStackTraceString(ex), "security", "crytpStringWithPEM", 0);
                return _toCrypt;
            }
        }
        /**
         * Encrypt a string with a PEM key.
         * @param _toCrypt String to be crypt.
         * @param pem The public key as PEM string.
         * @return The string ciphered.
         */
        public String crytpStringWithPrivatePEM(String _toCrypt, String pem) {
            try {                
                Cipher cipher = Cipher.getInstance("RSA");
                cipher.init(Cipher.ENCRYPT_MODE, this.getPrivateKeyFromPem(pem));
                byte[] cypted = cipher.doFinal(_toCrypt.getBytes());
                return new String(GappBase64.encode(cypted));
            }catch(Exception ex){
                System.out.println(logs.getStackTraceString(ex));
                logs.logIt(this.getClass().getCanonicalName(), logs.getStackTraceString(ex), "security", "crytpStringWithPEM", 0);
                return _toCrypt;
            }
        }
        /**
         * Decrypt a string with a PEM key.
         * @param _toDecrypt String to be decrypt.
         * @param pem The private key as PEM string.
         * @return The original string.
         */
        public String decryptStringWithPem(String _toDecrypt, String pem) {
            try {                
                Cipher cipher = Cipher.getInstance("RSA");
                cipher.init(Cipher.DECRYPT_MODE, getPrivateKeyFromPem(pem));                
                logs.logIt(this.getClass().getSimpleName(), "_toDec: " + _toDecrypt, "security", "decryptStringWithPem", 0);
                byte[] decrypted = cipher.doFinal(GappBase64.decode(_toDecrypt));
                return new String(decrypted);
            }catch(Exception ex) {
                System.out.println(logs.getStackTraceString(ex));
                logs.logIt(this.getClass().getCanonicalName(), logs.getStackTraceString(ex), "security", "decryptStringWithPem", 0);
                return "Can not decrypt see logs";
            }
        }
        private void setIV(String ivStr) {
                this.iv = GappBase64.decode(ivStr);
        }
        private void setIV(byte []_iv) {
                this.iv = _iv;
        }
        private void setIV() {
                this.generateAESIV();
        }
        public byte[] getIV() {
                if(this.iv == null) this.setIV();
                return this.iv;
        }
        public String getIVStr() {
                if(this.getIV() == null) this.setIV();
                return new String(GappBase64.encode(iv));
        }
        private IvParameterSpec getIVParam() {                      
                return new IvParameterSpec(getIV());
        }
        /**
         * Generate and AES IV.
         */
        private void generateAESIV() {
                try {
                        byte[] bytes = new byte[16];
                        SecureRandom.getInstanceStrong().nextBytes(bytes);
                        this.iv = bytes;
                } catch (Exception e) {
                        logs.logIt(this.getClass().getCanonicalName(), logs.getStackTraceString(e), "security", "decryptStringAES", 0);
                        System.out.println(logs.getStackTraceString(e));                        
                }
        }
}
