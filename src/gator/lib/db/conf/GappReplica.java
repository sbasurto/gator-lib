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

import gator.lib.date.GappDateFactory;
import gator.lib.io.files.GappFiles;

/**
 * Describes a replication object.
 * @author <a href="mailto:sbasurto@soft-gator.com">Sergio Basurto Juárez</a>
 */
public class GappReplica {
        /**
         * Database configuration file.
         */
        private String configFile = "";
         
        /**
         * Last UUID message.
         */
        private String lastUUIDMessage = "";
         
        /**
         * Database status.
         */
        private String status = "";
                
        /**
         * File manager for debugger.	 
         */
        private final GappFiles gappFiles = new GappFiles();
         
        /**
         * Allows to set configuration file for this replica.
         * 
         * @param _configFile The name of the configuration file.
         */
        public void setConfigFile(String _configFile) {
                this.configFile = _configFile;
        }
         
        /**
         * Retrieve the configuration file name.
         * 
         * @return Configuration file's name.
         */
        public String getConfigFile() {
                return this.configFile;
        }
        /**
         * Allows to set last UUID message for this replica.
         * 
         * @param uuid The last UUID of the last message received from db.
         */
        public void setLastUUIDMessage(String uuid) {
                this.lastUUIDMessage = uuid;
        }
         
        /**
         * Retrieve the last UUID message for this replica.
         * 
         * @return Last UUID of the last message received from db.
         */
        public String getLastUUIDMessage() {
                return this.lastUUIDMessage;
        }
        
        /**
         * Allows to set status for this replica.
         * 
         * @param _status The status of this replica.
         */
        public void setStatus(String _status) {
                this.status = _status;
        }
         
        /**
         * Retrieve the status for this replica.
         * 
         * @return The status of this replica.
         */
        public String getStatus() {
                return this.status;
        }
        /**
         * Retrieve the status for this replica.
         * 
         * @param name Name of the replica to write statement.
         * @param statement The statement to execute in the replica.
         * 
         */
        public void writeStatment(String name, String statement) {
                GappDateFactory dateFactory = new GappDateFactory();
                gappFiles.setDir(GappFiles.REPLICAS_DIR);
                gappFiles.write2File(dateFactory.getDateForId() + ";" + statement + GappFiles.NEW_LINE, GappFiles.REPLICAS_DIR + name);
        }
}