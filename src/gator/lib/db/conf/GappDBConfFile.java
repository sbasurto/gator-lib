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
package gator.lib.db.conf;

/**
 * GappDBConfFile is the class that manage connection files as described in
 * Soft Gator standard for database configuration files.
 *
 * @author <a href="mailto:sbasurto@soft-gator.com">Sergio Basurto Juárez</a>
 */
public class GappDBConfFile {
        /**
         * The kind of database, could be POSTGRESQL, MYSQL, ORACLE, etc,.
         */
        private String tipoDb = "pgsql";
        
        /**
         * The database server IP address or name to connect.
         */
        private String servidor = "localhost";
        
        /**
         * The port to connect to database.
         */
        private String puerto = "5432";
        
        /**
         * The database SID.
         */
        private String sid = "";
        
        /**
         * User for database.
         */
        private String usuario = "";
        
        /**
         * Password for database.
         */
        private String password = "";
        
        /**
         * Server human readable name.
         */
        private String servidorNombre = "My Server";
        
        /**
         * Database human readable name.
         */
        private String dbNombre = "My Database";
        
        /**
         * Server unique identification.
         */
        private String servidorId = "";
        
        /**
         * Server unique identification.
         */
        private String ssl = "";
                    
        
        /**
         * Get database kind.
         * 
         * @return A string representing the kind of database.
         */
        public String getDbKind() {
                return tipoDb;
        }
        
        /**
         * Set database kind.
         * 
         * @param dbKind    A string representing database kind.
         */
        public void setDBKind(String dbKind) {
                tipoDb = dbKind;
        }
        /**
         * Get server IP address or name.
         * 
         * @return A string representing server IP address or name.
         */
        public String getServer() {
                return servidor;
        }
        
        /**
         * Set server IP address or name.
         * 
         * @param server    A string representing server name or IP address.
         */
        public void setServer(String server) {
                servidor = server;
        }
        
        /**
         * Get database port number.
         * 
         * @return A string representing database port number.
         */
        public String getPortNumber() {
                return puerto;
        }
        
        /**
         * Set database port number.
         * 
         * @param portNumber    A string representing database port number.
         */
        public void setPortNumber(String portNumber) {
                puerto = portNumber;
        }
        
        /**
         * Get database SID.
         * 
         * @return A string representing database SID.
         */
        public String getSID() {
                return sid;
        }
        
        /**
         * Set database SID.
         * 
         * @param dbSID    A string representing database SID.
         */
        public void setSID(String dbSID) {
                sid = dbSID;
        }
        
        /**
         * Get database user.
         * 
         * @return A string representing database user.
         */
        public String getUser() {
                return usuario;
        }
        
        /**
         * Set database user.
         * 
         * @param user    A string representing database user.
         */
        public void setUser(String user) {
                usuario = user;
        }
        
        /**
         * Get database password.
         * 
         * @return A string representing database password.
         */
        public String getSecret() {
                return password;
        }
        
        /**
         * Set database password.
         * 
         * @param secret    A string representing database password.
         */
        public void setSecret(String secret) {
                password = secret;
        }
        /**
         * Get server human readable name.
         * 
         * @return A string representing server human readable name.
         */
        public String getServerName() {
                return servidorNombre;
        }
        
        /**
         * Set server human readable name.
         * 
         * @param serverName    A string representing server human readable name.
         */
        public void setServerName(String serverName) {
                servidorNombre = serverName;
        }
        
        /**
         * Get database human readable name.
         * 
         * @return A string representing database human readable name.
         */
        public String getDatabaseName() {
                return servidorNombre;
        }
        
        /**
         * Set database human readable name.
         * 
         * @param databaseName    A string representing database human readable name.
         */
        public void setDatabaseName(String databaseName) {
                servidorNombre = databaseName;
        }
        
        /**
         * Get database human readable name.
         * 
         * @return A string representing database human readable name.
         */
        public String getServerUUID() {
                return servidorId;
        }
        /**
         * Get database ssl parameter.
         * 
         * @return A string representing database ssl parameter.
         */
        public String getSSL() {
                return ssl;
        }
        
        /**
         * Set database ssl parameter.
         * 
         * @param dbSSL    A string representing database ssl parameter.
         */
        public void setSSL(String dbSSL) {
                ssl = dbSSL;
        }        
}
