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
package gator.lib.sec.session;

/**
 * Store challenge and key.
 * @author <a href="mailto:sbasurto@soft-gator.com">Sergio Basurto Juárez</a>
 */
public class GappChallenge {
        /**
         * The challenge.
         */
        private String challenge;
        
        /**
         * The key to crypt.
         */
        private String mainkey;
        
        /**
         * Allows to set challenge.
         * @param _challenge The string representing the challenge.
         */
        public void setChallenge(String _challenge) {
                challenge = _challenge;
        }
        /**
         * Get the challenge string.
         * @return The challenge string.
         */
        public String getChallenge() {
                return challenge;
        }
        
        /**
         * Allows to set main key for any crypt task.
         * @param _mainkey The key as string.
         */
        public void setKey(String _mainkey) {
                mainkey = _mainkey;
        }
        
        /**
         * Get the main key as string.
         * @return The key as string.
         */
        public String getKey() {
                return mainkey;
        }
}
