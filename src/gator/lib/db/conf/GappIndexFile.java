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
package gator.lib.db.conf;

import com.google.gson.Gson;
import gator.lib.io.files.GappFiles;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Describes an index file like indexMasterErm.
 * <a href="mailto:sbasurto@soft-gator.com">Sergio Basurto Juárez</a>
 */
public class GappIndexFile {
    /**
     * The name of this file.
     */
    private String file = "";
    
    /**
     * Name that should be displayed in login screen.
     */
    private String nameToDisplay = "";
    
    /**
     * The key store to use for encryption.
     */
    private String keyStore;
    
    /**
     * The system that is been accessed with this file.
     */
    private String system = "";
    
    /**
     * The default language to show at login screen.
     */
    private String language = "es";
    
    /**
     * User to get public key.
     */
    private String pkeyusr = "";
    
    /**
     * Secret to get public key.
     */
    private String pkeysecret = "";
    
    /**
     * Configuration file for security database.
     */
    private String conff = "";
    
    /**
     * File manager for debugger.	 
     */
    private final GappFiles gappFiles = new GappFiles();
    
    /**
     * Array list of database to replicate in real time.
     */
    private HashMap<String,GappReplica> replicas = new HashMap<>();
    
    /**
     * Configuration files for administration interfaces.
     */
    private final ArrayList<String> files = new ArrayList<>();
    
    /**
     * Allows to set the name for this file.
     * @param _file The name of the file "indexMasterErm" for example.
     */
    public void setFile(String _file) {
        this.file = _file;
    }
    
    /**
     * Allows to retrieve the name of this file.
     * 
     * @return The name of this file.
     */
    public String getFile() {
        return file;
    }
    
    /**
     * Allows to set the name to display at login screen.
     * @param name The name to display at login.
     */
    public void setNameToDisplay(String name) {
        this.nameToDisplay = name;
    }
    
    /**
     * Allows to retrieve the name to display at login screen.
     * 
     * @return The name to display at login.
     */
    public String getNameToDisplay() {
        return nameToDisplay;
    }
    
    /**
     * Allows to set the key store to use.
     * @param _keyStore The name of key store.
     */
    public void setKeyStore(String _keyStore) {
        this.keyStore = _keyStore;
    }
    
    /**
     * Allows to retrieve the key store to use.
     * 
     * @return The name of key store.
     */
    public String getKeyStore() {
        return keyStore;
    }
    
    /**
     * Allows to set the system that it's been accessed.
     * @param _system The name of key store.
     */
    public void setSystem(String _system) {
        this.system = _system;
    }
    
    /**
     * Allows to retrieve the system that it's been accessed.
     * 
     * @return The name of key store.
     */
    public String getSystem() {
        return system;
    }
    
    /**
     * Allows to set the language to use in login screen.
     * @param _language Language to use "es" for example.
     */
    public void setLanguage(String _language) {
        this.language = _language;
    }
    
    /**
     * Allows to retrieve the language to use in login screen.
     * 
     * @return Language to use "es" for example.
     */
    public String getLanguage() {
        return language.equals("") || language == null?"es":language;
    }
    
    /**
     * Allows to set the user to get public key.
     * @param _pkeyusr User to get public key.
     */
    public void setPublicKeyUser(String _pkeyusr) {
        this.pkeyusr = _pkeyusr;
    }
    
    /**
     * Allows to retrieve the user to get public key.
     * 
     * @return User to get public key.
     */
    public String getPublicKeyUser() {
        return pkeyusr;
    }
    
    /**
     * Allows to set the password to get public key.
     * @param _pkeysecret Password to get public key.
     */
    public void setPublicKeyPass(String _pkeysecret) {
        this.pkeysecret = _pkeysecret;
    }
    
    /**
     * Allows to retrieve the password to get public key.
     * 
     * @return Password to get public key.
     */
    public String getPublicKeyPass() {
        return pkeysecret;
    }
    
    /**
     * Allows to set the password to get public key.
     * @param _conff Configuration file for database.
     */
    public void setConfigurationFile(String _conff) {
        this.conff = _conff;
    }
    
    /**
     * Allows to retrieve the password to get public key.
     * 
     * @return Configuration file for database.
     */
    public String getConfigurationFile() {
        return conff;
    }
    
    /**
     * Allows to set add a replication database.
     * @param replicaName The name of the replica to write.
     * @param replicationConfFile Configuration file to add new database replication.
     */
    public void addReplica(String replicaName, GappReplica replicationConfFile) {
        this.replicas.put(replicaName, replicationConfFile);
    }
    
    /**
     * Allows to set the replicas for this index file.
     * 
     * @param _replicas An array list of GappReplica.
     */
    public void setReplicas(HashMap<String, GappReplica> _replicas) {
        replicas = _replicas;
    }
    /**
     * Allows to retrieve the password to get public key.
     * 
     * @return Array of configuration files for replication databases.
     */
    public HashMap<String, GappReplica> getReplicas() {
        return replicas;
    }
    
    /**
     * Allows to retrieve the password to get public key.
     * 
     * @return Array of configuration files for replication databases.
     */
    public String getReplicasAsString() {
        String []replicasStr = {"{"};
        this.getReplicas().forEach((key, replica) -> {
                replica.getConfigFile();
                replica.getStatus();
                replicasStr[0] += "\"" + key + "\": {\"configFile\":\"" + replica.getConfigFile() + "\",\"status\":\"" + replica.getStatus() + "\"},";
        });
        replicasStr[0] = replicasStr[0].replaceAll(",$", "");
        replicasStr[0] += "}";
        return replicasStr[0];
    }
    
    /**
     * Allows to retrieve JSON.
     * 
     * @return JSON for this index file.
     */
    public String getJson() {
            Gson gson = new Gson();
            return gson.toJson(this);
    }
    /**
     * Allows to retrieve JSON as simple as possible.
     * 
     * @return JSON for this index file.
     */
    public String getSimplifiedJson() {
            return "{\"file\":\"" + this.getFile() + "\", \"nameToDisplay\":\"" + this.getNameToDisplay() + "\", \"system\":\"" + this.getSystem() + "\", \"language\":\"" + this.getLanguage() + "\", \"files\":[], \"pkeyusr\":\"" + this.getPublicKeyUser() + "\", \"pkeysecret\":\"" + this.getPublicKeyPass() + "\", \"conff\":\"" + this.getConfigurationFile() + "\", \"replicas\": " + this.getReplicasAsString() + "}";
    }
    /**
     * Write the last values to configuration file.
     */
    public void writeMe() {
            gappFiles.setDir(GappFiles.CONF_DIR);
            gappFiles.deleteFile(GappFiles.CONF_DIR + this.getFile());
            gappFiles.write2File(this.getSimplifiedJson(), GappFiles.CONF_DIR + this.getFile());
    }    
}
