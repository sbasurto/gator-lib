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
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import gator.lib.date.GappDateFactory;
import gator.lib.db.GappSQLStatement;
import gator.lib.db.helpers.GappDBHelper;
import gator.lib.db.helpers.GappDBResponse;
import gator.lib.logs.GappLogging;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/**
 * Helps to authenticate a user and validate some data.
 * @author <a href="mailto:sbasurto@soft-gator.com">Sergio Basurto Juárez</a>
 */
public class GappAuth {
        /**
	 * Stores data base responses.
	 */
	private final GappDBResponse response = new GappDBResponse();

	/**
	 * DB helper.
	 */
	private final GappDBHelper dbHelper;      
        
        /**
	 * Date factory.
	 */
	private final GappDateFactory dateFactory = new GappDateFactory();
        
        /**
         * Log instance to do the log.
         */
        private final GappLogging logs;
        
        /**
         * Stores attributes like accounts, groups, equipment.
         */
        private String userAttributes = "{}";
        
        /**
         * Store user information like user, password, user name, etc.,.
         */
        private String userInfo = "{}";
        
        /**
         * Log instance to do the log.
         */
        private String token = "unauth";
        
        /**
         * Stores real debug level.
         */
        private String debugLevel = "0";
        
        /**
         * Constructor.
         * 
         * @param fileName  Database configuration file.
         */
        public GappAuth(String fileName) {
                dbHelper = new GappDBHelper(fileName);
                logs = new GappLogging();
        }
        
        /**
         * Verify if the login is right.
         * 
         * @param usuario       The user to be authenticated.
         * @param password      Encrypted password.
         * @param salt          Salt for encryption.
         * @param auid          Authentication id.
         * @param email         User email
         * @param debugLevel    Debug level to apply.
         * @param token         A unique string for this run.
         * @param ip            The IP address that is connecting in this run.
         * 
         * @return  If authentication is alright.
         */
        public boolean checkLogin(String usuario, String password, String salt, String auid, String email, String debugLevel, String token, String ip) {	
                Gson gson = new Gson();                
                JsonObject jsonObj = new JsonObject();
                jsonObj.addProperty("auid", auid);
                jsonObj.addProperty("usuario", usuario);
                jsonObj.addProperty("esGoogle", 0);
                jsonObj.addProperty("email", email);
                jsonObj.addProperty("debugLevel", debugLevel);
                GappSQLStatement gappSQLStmt = new GappSQLStatement();
                gappSQLStmt.setStoreProcedure("sec_fn_get_usuario2");
                gappSQLStmt.addParam(gson.toJson(jsonObj));		
                String res = dbHelper.executeStore(gappSQLStmt);                
                logs.logIt(this.getClass().getCanonicalName(), res, usuario, "checkLogin", 11);
                if(res.equals("none")) {
                        return false;
                } else {
                        userInfo = res;
                        JsonElement element = JsonParser.parseString(res);
                        JsonObject jsonObject = element.getAsJsonObject();
                        setUserInfoNeeded(jsonObject);
                        if(verify(password, jsonObject.get("passwd").getAsString(), salt)) {
                                this.token = token;
                                jsonObj = new JsonObject();
                                jsonObj.addProperty("auid", auid);
                                jsonObj.addProperty("usuario", usuario);
                                jsonObj.addProperty("token", token);
                                jsonObj.addProperty("debugLevel", debugLevel);
                                gappSQLStmt = new GappSQLStatement();
                                gappSQLStmt.setStoreProcedure("sec_fn_set_usr_attrs");
                                gappSQLStmt.addParam(gson.toJson(jsonObj));
                                userAttributes = dbHelper.executeStore(gappSQLStmt);
                                return true;
                        } else {
                                return false;
                        }
                }
        }
        /**
         * Verify the password sent.
         * @param passwd The password sent by user.
         * @param passwdSHA512 The password hashed.
         * @param salt The salt to be used.
         * @return A boolean flag telling if the password match or not.
         */
        public boolean verify(String passwd, String passwdSHA512, String salt){                
                return passwd.equals(sha512(passwdSHA512 + salt));                
        }
        /**
         * Hash a string with SHA512.
         * @param phrase String to be hashed.
         * @return String hashed.
         */
        public String sha512(String phrase){
                try {
                        byte[] digest = MessageDigest.getInstance("SHA-512")
                                .digest(phrase.getBytes(StandardCharsets.UTF_8));
                        return HexFormat.of().formatHex(digest);
                } catch (NoSuchAlgorithmException ex) {
                        throw new IllegalStateException("SHA-512 is unavailable", ex);
                }
        }
        /**
         * Allow to get user attributes.
         * 
         * @return User attributes as JSON string.
         */
        public String getUserAttrs() {
                return userAttributes;
        }
        /**
         * Allow to get user information.
         * 
         * @return User info as JSON string.
         */
        public String getUserInfo() {
                return userInfo;
        }
        /**
         * Sign out a user
         * @param auid The unique identifier for this call.
         * @param token A unique string for the call.
         * @param ip The IP address that is asking the logout.
         * @param debugLevel The debug level to be used.
         */
        public void logout(String auid, String token, String ip, String debugLevel) {
            Gson gson = new Gson();            
            JsonObject jsonObj = new JsonObject();                    
            jsonObj.addProperty("auid", auid);
            jsonObj.addProperty("ip", ip);
            jsonObj.addProperty("token", token);
            jsonObj.addProperty("debugLevel", debugLevel);
            GappSQLStatement gappSQLStmt = new GappSQLStatement();                
            gappSQLStmt.setStoreProcedure("app_fn_end_session");                
            gappSQLStmt.addParam(gson.toJson(jsonObj));
            dbHelper.executeStore(gappSQLStmt);
        }
        
        /**
         * Get the some attributes from user info that will be needed after.
         * 
         * @param json  The JSON object that contains the user info object.
         */
        private void setUserInfoNeeded(JsonObject json) {
            this.debugLevel = json.get("user_debug_level").getAsString();
        }
        
        /**
         * Allow to retrieve debug level for the user.
         * @return Return debug level for this user.
         */
        public String getUserDebugLevel() {
            return this.debugLevel;
        }
}
