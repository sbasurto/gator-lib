/*
 * Copyright (C) 2023 sbasurto
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

/**
 *
 * @author sbasurto
 */
public class GappKeystore {
        private String storeFile;
        private String passphrase;
        private String keyName;
        /**
         * Set store file path and name.
         * 
         * @param file  A string with the path and name for the store file.
         */
        public void setStoreFile(String file) {
            this.storeFile = file;
        }
        
        /**
         * Allow to retrieve the store file.
         * @return Store file path and name.
         */
        public String getStoreFile() {
            return this.storeFile == null?"":this.storeFile;
        }
        /**
         * Set store password to access this specific key store.
         * 
         * @param password  A string with the path and name for the store file.
         */
        public void setStorePassword(String password) {
            this.passphrase = password;
        }
        
        /**
         * Allow to retrieve the store password to access it.
         * @return Password as string.
         */
        public String getStorePassword() {
            return this.passphrase == null?"":this.passphrase;
        }
        /**
         * Set store key name.
         * 
         * @param keyName  The key name as string to use.
         */
        public void setStoreKey(String keyName) {
            this.keyName = keyName;
        }
        
        /**
         * Allow to retrieve the store key name.
         * @return Key name as string.
         */
        public String getStoreKey() {
            return this.keyName == null?"":this.keyName;
        }
        
        /**
         * Customized to string.
         * @return This object values as string.
         */
        @Override
        public String toString() {
            return this.getStoreFile() + ",[redacted]," + this.getStoreKey();
        }
}
        
