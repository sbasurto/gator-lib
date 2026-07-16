/*
 * Copyright (C) 2024 sbasurto
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
package gator.lib.net.endpoints;

import gator.lib.logs.GappLogging;
import gator.lib.sec.GappCrypt;

/**
 *
 * @author sbasurto
 */
public class GappEndPoint {
        private String aesKey;
        private String iv;
        private final GappLogging logger = new GappLogging();
        /**
         * Allows to encrypt a string using AES-CBC.
         * 
         * @param toEncrypt The string to be encrypted.
         * 
         * @return The string encrypted with current key and vi.
         */
        public String encryptAES(String toEncrypt) {
                GappCrypt gappCrypt = new GappCrypt("default");
                gappCrypt.setAESKey(this.getAESKey());    
                this.setIV(gappCrypt.getIVStr());
                return gappCrypt.crytpStringAES(toEncrypt, this.getIV());
        }
        /**
         * Allows to retrieve an AES key, if there is not will create one to use.
         * @return Return a new AES key if does not exist or the current one.
         */
        public String getAESKey() {
                GappCrypt gappCrypt = new GappCrypt("default");
                if(this.aesKey == null) {
                        this.aesKey = gappCrypt.getAESKey();
                        return this.aesKey;
                } else {
                        return this.aesKey;
                }
        }
        /**
         * Allows to set client IV for encryption and decryption.
         * @param iv The IV for encryption and decryption.
         */
        public void setIV(String iv) {
                this.iv = iv;
        }
        /**
         * Allows to ask for public key for encryption.
         * @return The client's public key for encryption.
         */
        public String getIV() {
                return this.iv;
        }
        /**
         * Allows to decrypt a string using AES-CBC.
         * 
         * @param toDecrypt The string to be encrypted.
         * @param iv The IV to use to decrypt.
         * 
         * @return The string decrypted with current key and vi.
         */
        public String decryptAES(String toDecrypt, String iv) {
                GappCrypt gappCrypt = new GappCrypt("default");
                logMe("GappWSClient.decryptAES", "Key:" + this.getAESKey(), "status", 0);
                gappCrypt.setAESKey(this.getAESKey());                    
                return gappCrypt.decryptStringAES(toDecrypt, this.getAESKey(), iv);
        }
        /**
         * Allows to logs anything.
         * @param who   Which class.method is creating the log.
         * @param message   The actual message to log.
         * @param destination Where the log suppose to be logged. 
         * @param debugLevel The level of debug that we want to log in.
         */
        private void logMe(String who, String message, String destination, int debugLevel) {
                logger.logIt(who, message, "websocket.log", "system", "system", debugLevel);                                
        }
}
