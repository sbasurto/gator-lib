/*
 * Copyright (C) 2022 Sergio Basurto Juárez
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
package gator.lib.net.cookies;

/**
 *
 * @author <a href="mailto:sbasurto@soft-gator.com">Sergio Basurto Juárez</a>
 */
public class GappCookie {
        /**
         * Id of this cookie.
         */
        private String id;
        /**
         * Name of this cookie.
         */
        private String name;

        /**
         * Value for this cookie.
         */
        private String value;

        /**
         * Max age in seconds.
         */
        private int maxAge;

        /**
         * Expiration date.
         */
        private String fechaExp;

        /**
         * Days defined for expiration.
         */
        private int days;


        /**
         * Construct new Gator App Cookie, with empty name and value.
         *         
         */
        public GappCookie() {
            ;                
        }
        /**
         * Construct new Gator App Cookie, with name and value given.
         *
         * @param _name The name for this cookie.
         * @param _value The value for this cookie.
         */
        public GappCookie(String _name, String _value) {
                name = _name;
                value = _value;
        }
        /**
         * Set cookie id with the argument.
         *
         * @param _id The new id for this cookie.
         */
        public void setId(String _id) {
                id = _id;
        }
        /**
         * Set cookie name with the argument.
         *
         * @param _name The new name for this cookie.
         */
        public void setName(String _name) {
                name = _name;
        }
        /**
         * Set cookie value with the argument.
         *
         * @param _value The new value for this cookie.
         */
        public void setValue(String _value) {
                value = _value;
        }
        /**
         * Set cookie max age with the argument.
         *
         * @param _maxAge The new max age for this cookie.
         */
        public void setMaxAge(int _maxAge) {
                maxAge = _maxAge;
        }
        /**
         * Set cookie expiration date.
         *
         * @param _fechaExp The expiration date for this cookie.
         */
        public void setExpirationDate(String _fechaExp) {
                fechaExp = _fechaExp;
        }
        /**
         * Get cookie id as a String.
         *
         * @return The cookie's name.
         */
        public String getId() {
                return id == null?"":id;
        }
        /**
         * Get cookie name as a String.
         *
         * @return The cookie's name.
         */
        public String getName() {
                return name == null?"":name;
        }
        /**
         * Get cookie value as a String.
         *
         * @return The cookie's value.
         */
        public String getValue() {
                return value == null?"":value;
        }
        /**
         * Get cookie max age as an int.
         *
         * @return The cookie's max age.
         */
        public int getMaxAge() {
                return maxAge;
        }
        /**
         * Get cookie expiration date as a String.
         *
         * @return The cookie's value.
         */
        public String getExpirationDate() {
                return fechaExp == null?"":fechaExp;
        }
}
