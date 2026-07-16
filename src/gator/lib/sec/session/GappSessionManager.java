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
package gator.lib.sec.session;

import com.google.gson.Gson;
import gator.lib.db.GappSQLStatement;
import gator.lib.db.helpers.GappDBHelper;
import gator.lib.sec.GappCrypt;

/**
 * A manager for session.
 * @author      <a href="mailto:sbasurto@soft-gator.com">Sergio Basurto Juárez</a>
 * @version     1.0, 30 January 2021
 */
public class GappSessionManager {
        /**
         * The file for database configuration.
         */
        private String dbConfigFile;
        
        /**
         * A GappCrypt object to use in this manager.
         */
        private final GappCrypt gappCrypt = new GappCrypt("none");
        
        /**
         * Create a session manager with this configuration file.
         * @param dbConfile Configuration file as string.
         */
        public GappSessionManager(String dbConfile) {
                dbConfigFile = dbConfile;
        }
        /**
         * Initialize the session with the information get from db conf file.
         * @param sessionId The session id to be used.
         * @param ipAddress The IP address for this session.
         * @param user The user for this session.
         * @param password The user's password.
         */
        public void initSesssion(String sessionId, String ipAddress, String user, String password) {
                Gson gson = new Gson();
                GappDBHelper helper = new GappDBHelper(dbConfigFile);
                GappSession gappSession = new GappSession();
                gappSession.setIPAddress(ipAddress);
                gappSession.setUser(user, password);
                gappSession.setAccion("alta");
                gappSession.setSessionId(sessionId);
                GappSQLStatement gappSQLStmt = new GappSQLStatement();
                gappSQLStmt.setStoreProcedure("app_fn_admon_session");
                gappSQLStmt.addParam(gson.toJson(gappSession));
                String json = helper.executeStore(gappSQLStmt);
        }
        /**
         * Add data to a session.
         * @param sessionId The session id to add data for.
         * @param key The key for the data.
         * @param value The data itself.
         */
        public void addSessionData(String sessionId, String key, String value) {
                Gson gson = new Gson();
                GappDBHelper helper = new GappDBHelper(dbConfigFile);
                GappSession gappSession = new GappSession();
                gappSession.addData(key, value);
                gappSession.setAccion("alta");
                gappSession.setSessionId(sessionId);
                GappSQLStatement gappSQLStmt = new GappSQLStatement();
                gappSQLStmt.setStoreProcedure("app_fn_admon_session");
                gappSQLStmt.addParam(gson.toJson(gappSession));
                String json = helper.executeStore(gappSQLStmt);
        }
        /**
         * Update the session last access time.
         * @param sessionId The session id to be touched.
         */
        public void touchSession(String sessionId) {
                Gson gson = new Gson();
                GappDBHelper helper = new GappDBHelper(dbConfigFile);
                GappSession gappSession = new GappSession();
                gappSession.setAccion("cambio");
                gappSession.setSessionId(sessionId);
                GappSQLStatement gappSQLStmt = new GappSQLStatement();
                gappSQLStmt.setStoreProcedure("app_fn_admon_session");
                gappSQLStmt.addParam(gson.toJson(gappSession));
                String json = helper.executeStore(gappSQLStmt);
        }
        /**
         * Ends the session.
         * @param sessionId The session id to be terminated.
         */
        public void endSession(String sessionId) {
                Gson gson = new Gson();
                GappDBHelper helper = new GappDBHelper(dbConfigFile);
                GappSession gappSession = new GappSession();
                gappSession.setAccion("cambio");
                gappSession.setSessionId(sessionId);
                GappSQLStatement gappSQLStmt = new GappSQLStatement();
                gappSQLStmt.setStoreProcedure("app_fn_admon_session");
                gappSQLStmt.addParam(gson.toJson(gappSession));
                String json = helper.executeStore(gappSQLStmt);
        }
        /**
         * Get a challenge for this session.
         * @param sessionId The session id for get the challenge.
         * @return The challenge as string.
         */
        public String getChallenge(String sessionId) {
                Gson gson = new Gson();
                GappDBHelper helper = new GappDBHelper(dbConfigFile);
                GappSession gappSession = new GappSession();
                gappSession.setAccion("none");
                gappSession.setSessionId(sessionId);
                GappSQLStatement gappSQLStmt = new GappSQLStatement();
                gappSQLStmt.setStoreProcedure("app_fn_get_session_challenge");
                gappSQLStmt.addParam(gson.toJson(gappSession));
                String json = helper.executeStore(gappSQLStmt);
                GappChallenge challenge = gson.fromJson(json, GappChallenge.class);
                return challenge.getChallenge();                
        }
}
