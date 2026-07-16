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
package gator.lib.templates;

/**
 * Key = value representation for Soft Gator applications.
 * @author <a href="mailto:sbasurto@soft-gator.com">Sergio Basurto Juárez</a>
 */
public class GatorKeyValue {
        /**
         * The key for this pair.
         */
        private String key;
        /**
         * The value assigned to key.
         */
        private String value;
        
        /**
         * Allows to set the key.
         * @param _key Key as string.
         */
        public void setKey(String _key) {
                key = _key;
        }
        /**
         * Get the key.
         * @return Key as string.
         */
        public String getKey() {
                return key;
        }
        /**
         * Allows to set the value.
         * @param _value Value for the key as string.
         */
        public void setValue(String _value) {
                value = _value;
        }
        /**
         * Get the value.
         * @return Value as string.
         */
        public String getValue() {
                return value;
        }
}
