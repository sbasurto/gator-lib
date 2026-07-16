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
 * Session data for Soft Gator applications.
 * @author <a href="mailto:sbasurto@soft-gator.com">Sergio Basurto Juárez</a>
 */
public class GappSessionData {
        /**
         * Key for the data.
         */
        private String key;
        
        /**
         * Value for the data.
         */
        private String value;
        
        /**
         * Set key => value data.
         * @param _key The key for this data.
         * @param _value The data itself.
         */
        public void setData(String _key, String _value) {
                key = _key;
                value = _value;
        }
        /**
         * Get key.
         * @return Key as string.
         */
        public String getKey() {
                return key;
        }
        /**
         * Get value.
         * @return Value as string.
         */
        public String getValue() {
                return value;
        }
}
