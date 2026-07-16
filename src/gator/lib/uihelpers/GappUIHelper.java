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
package gator.lib.uihelpers;

import gator.lib.db.conf.GappIndexFile;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import gator.lib.db.forDeprecation.DBConfData;
import gator.lib.io.files.GappFiles;
import gator.lib.logs.GappLogging;

/**
 * Helper for Soft Gator Applications user interface.
 * <a href="mailto:sbasurto@soft-gator.com">Sergio Basurto Juárez</a>
 */
public class GappUIHelper {
    /**
     * Log instance to do the log.
     */
    private final GappLogging logs = new GappLogging();
    
    /**
     * User to log.
     */
    private String user = "System";
    
    /**
     * System to log.
     */
    private String system = "Gapp File System";
    
    /**
     * Get the initial configuration of any Soft Gator system.
     * @param fileName      The initial configuration file name.
     * @return An array list of string with configuration data.
     */
    public GappIndexFile getIndexConfig(String fileName) {
        GappFiles gappFiles = new GappFiles();
        gappFiles.setDir(GappFiles.CONF_DIR);
        gappFiles.readFromFile(GappFiles.CONF_DIR + fileName);
	logs.logIt("GappUIHelper", "Configuration file:" + GappFiles.CONF_DIR + fileName, "helper", "getIndexConfig2", 0);
        Gson gson = new Gson();
        GappIndexFile gappIndexFile = gson.fromJson(gappFiles.getReadedLinesAsString(), GappIndexFile.class);        	
        return gappIndexFile;
    }    
    /**
     * Set data base initial configuration and return it as DBConf.
     * 
     * @param initConf     Initial system configuration.
     * @return Database initial configuration.
     */
    public DBConfData setNewConf(String initConf) {        
        JsonElement jsonEl = JsonParser.parseString(initConf);
        JsonObject jsonObj = jsonEl.getAsJsonObject();
        String configurationFile = jsonObj.get("conff").getAsString();
        DBConfData dbConf = new DBConfData();
        if(!dbConf.isConfigured()) {
            dbConf.setConf(configurationFile);
        }
        return dbConf;
    }
    
    /**
     * Get some attribute from initial configuration.
     * 
     * @param initConf  Initial configuration.
     * @param attribute Attribute to get.
     * 
     * @return String representing attribute.
     */
    public String getStore(String initConf, String attribute) {        
        String attr = "To check wh used.";
        return attr;
    }
    
    /**
     * Get the initial configuration of any Soft Gator system.
     * @param fileName      The initial configuration file name.
     * @param user          The user that is asking for configuration, always will be guest.
     * @return Hash map with the values obtained from configuration.
     */
    public GappIndexFile getIndexConfig2(String fileName, String user){
        GappFiles gappFiles = new GappFiles();
        gappFiles.setDir(GappFiles.CONF_DIR);
        gappFiles.readFromFile(GappFiles.CONF_DIR + fileName);
	logs.logIt("GappUIHelper", "Configuration file:" + GappFiles.CONF_DIR + fileName, "helper", "getIndexConfig2", 0);
        Gson gson = new Gson();
        GappIndexFile gappIndexFile = gson.fromJson(gappFiles.getReadedLinesAsString(), GappIndexFile.class);        	
        return gappIndexFile;
    }
}
