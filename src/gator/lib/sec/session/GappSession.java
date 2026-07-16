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

import java.util.ArrayList;

/**
 * A session for Soft Gator applications.
 * @author <a href="mailto:sbasurto@soft-gator.com">Sergio Basurto Juárez</a>
 */
public class GappSession {
        /**
         * IP address for the session.
         */
        private String ipAddress;
        
        /**
         * User id who the session belongs to.
         */
        private String userId;
        
        /**
         * The pass phrase used to authenticate user.
         */
        private String password;
        
        /**
         * The session data as GappSessionData object.
         * @see GappSessionData
         */
        private ArrayList<GappSessionData> sessionData;
        
        /**
         * The action to be performed.
         */
        private String accion = "0";
        
        /**
         * The id for this session.
         */
        private String sessionId;
        
        /**
         * Allows to set the IP address of the session.
         * @param _ipAddress The IP address as string, either IPv4 or IPv6.
         */
        public void setIPAddress(String _ipAddress) {
                ipAddress = _ipAddress;
        }
        /**
         * Get the IP address registered for this session.
         * @return The IP address as string.
         */
        public String getIPAddress() {
                return ipAddress;
        }
        /**
         * Allows to set the session's user.
         * @param _userId User id.
         * @param _password Pass phrase for this user.
         */
        public void setUser(String _userId, String _password) {
                userId = _userId;
                password = _password;
        }
        
        /**
         * Get the session's user.
         * @return User id as string.
         */
        public String getUserId() {
                return userId;
        }
        /**
         * Get the user's password.
         * @return Password for this user.
         */
        public String getPassword() {
                return password;
        }
        /**
         * Add data to session.
         * @param key The key for the data to be added to session.
         * @param value The value of the key.
         */
        public void addData(String key, String value) {
                GappSessionData data = new GappSessionData();
                data.setData(key, value);
                sessionData.add(data);
        }
        /**
         * Get list of session data.
         * @return A list of the session data as array list of GappSessionData object.
         * @see GappSessionData
         */
        public ArrayList<GappSessionData> getSesssionData() {
                return sessionData;
        }
        /**
         * Set the action to be performed.
         * @param _accion A string representation of the action to be performed.
         */
        public void setAccion(String _accion) {
                accion = _accion;
        }
        /**
         * Get the action.
         * @return Action as string.
         */
        public String getAccion() {
                return accion;
        }
        /**
         * Allows to set the session id.
         * @param _sessionId Session id as string.
         */
        public void setSessionId(String _sessionId) {
                sessionId = _sessionId;
        }
        /**
         * Get the session id.
         * @return Session id as a string.
         */
        public String getSessionId() {
                return sessionId;
        }
}
