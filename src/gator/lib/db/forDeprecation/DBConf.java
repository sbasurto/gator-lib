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
package gator.lib.db.forDeprecation;

import gator.lib.io.files.GappFiles;
import gator.lib.db.GappDBConnection;
import gator.lib.logs.GappLogging;
import java.util.concurrent.locks.*;

/**
 * This class is the database's configuration, this could be get from a file or settled manually.
 *
 * @author <a href="mailto:sbasurto@soft-gator.com">Sergio Basurto Juárez</a>
 *
 * @deprecated 	This is deprecated in favor of GappDBConnection that use json configuration instead.
 * @see GappDBConnection
 */
@Deprecated
public class DBConf {
	/**
	 *  Database's port.
	 */
	protected String puerto;

	/**
	 * Database's sid.
	 */
	protected String sid;

	/**
	 * Database's user.
	 */
	protected String usr;

	/**
	 * Database's password.
	 */
	protected String passwd;

	/**
	 * Database's server IP address v4.
	 */
	protected String ipv4;

	/**
	 * Database's server IP address v6.
	 */
	protected String ipv6;

	/**
	 * Database's server name.
	 */
	protected String serverName;

	/**
	 * Database's name.
	 */
	protected String dbName;

	/**
	 * Database's server identifier.
	 */
	protected String serverID;

	/**
	 * Database's kind, pgsql, mysql, etc.,.
	 */
	protected String dbKind;

	/**
	 * System to be logged.
	 */
	protected String system;

	/**
	 * Flag telling if configuration is set and complete.
	 */
	protected boolean isSetConf = false;

	/**
	 * File manager for this class.
	 */
	private final GappFiles gappFiles = new GappFiles();

	/**
	 * Lock to threading.
	 */
	protected Lock confLock = new ReentrantLock();

	/**
	 * Log instance to do the log.
	 */
	private final GappLogging logs = new GappLogging();

	/**
	 * Database's configuration file
	 */
	private String dbConfigFile = "";

	/**
	 * Set configuration from Utils object..
	 */
	public synchronized void setConf(){
		try{
			confLock.lock();
			gappFiles.setDir(GappFiles.CONF_DIR);
			logs.logIt(this.getClass().getCanonicalName(),"Configuration File location and name: " + GappFiles.CONF_DIR + getDBConfFile(),  "", "", 5);
			gappFiles.readFromFile(GappFiles.CONF_DIR + getDBConfFile());
			for(String line: gappFiles.getReadedLines()){
				if(line != null){
					if(line.contains("tipodb:")) {
						dbKind = line.substring(line.indexOf(": ")).replace(": ", "");
					}
					if(line.contains("servidor:")) {
						ipv4 = line.substring(line.indexOf(": ")).replace(": ", "");
					}
					if(line.contains("servidorv6:")) {
						ipv6 = line.substring(line.indexOf(": ")).replace(": ", "");
					}
					if(line.contains("puerto:")) {
						puerto = line.substring(line.indexOf(": ")).replace(": ", "");
					}
					if(line.contains("sid:")) {
						sid = line.substring(line.indexOf(": ")).replace(": ", "");
					}
					if(line.contains("usr:")) {
						usr = line.substring(line.indexOf(": ")).replace(": ", "");
					}
					if(line.contains("passwd:")) {
						passwd = line.substring(line.indexOf(": ")).replace(": ", "");
					}
					if(line.contains("servername:")) {
						serverName = line.substring(line.indexOf(": ")).replace(": ", "");
					}
					if(line.contains("dbname:")) {
						dbName = line.substring(line.indexOf(": ")).replace(": ", "");
					}
					if(line.contains("serverid:")) {
						serverID = line.substring(line.indexOf(": ")).replace(": ", "");
					}
				}
			}
			logs.logIt(this.getClass().getCanonicalName(), "ipv4:" + ipv4 + " ipv6:" + ipv6 + " puerto:" + puerto + " sid:" + sid + " usr:" + usr + " passwd:[redacted] serverName:" + serverName + " dbName:" + dbName + " serverID:" + serverID,  "", "", 5);
			isSetConf = true;
		}finally{
			confLock.unlock();
		}
	}

	/**
	 * Set configuration from file.
	 * @param conFile   File where to look for configuration.
	 */
	public synchronized void setConf(String conFile){
		try{
			confLock.lock();
			gappFiles.setDir(GappFiles.CONF_DIR);
			gappFiles.readFromFile(GappFiles.CONF_DIR + conFile);
			setDBConfFile(conFile);
			for(String line: gappFiles.getReadedLines()){
				if(line != null){
					if(line.contains("tipodb:")) {
						dbKind = line.substring(line.indexOf(": ")).replace(": ", "");
					}
					if(line.contains("servidor:")) {
						ipv4 = line.substring(line.indexOf(": ")).replace(": ", "");
					}
					if(line.contains("servidorv6:")) {
						ipv6 = line.substring(line.indexOf(": ")).replace(": ", "");
					}
					if(line.contains("puerto:")) {
						puerto = line.substring(line.indexOf(": ")).replace(": ", "");
					}
					if(line.contains("sid:")) {
						sid = line.substring(line.indexOf(": ")).replace(": ", "");
					}
					if(line.contains("usr:")) {
						usr = line.substring(line.indexOf(": ")).replace(": ", "");
					}
					if(line.contains("passwd:")) {
						passwd = line.substring(line.indexOf(": ")).replace(": ", "");
					}
					if(line.contains("servername:")) {
						serverName = line.substring(line.indexOf(": ")).replace(": ", "");
					}
					if(line.contains("dbname:")) {
						dbName = line.substring(line.indexOf(": ")).replace(": ", "");
					}
					if(line.contains("serverid:")) {
						serverID = line.substring(line.indexOf(": ")).replace(": ", "");
					}
				}
			}
			logs.logIt(this.getClass().getCanonicalName(), "ipv4:" + ipv4 + " ipv6:" + ipv6 + " puerto:" + puerto + " sid:" + sid + " usr:" + usr + " passwd:[redacted] serverName:" + serverName + " dbName" + dbName + " serverID" + serverID,  "", "", 5);
			isSetConf = true;
		}finally{
			confLock.unlock();
		}
	}

	/**
	 * Allows to get IP address v4 for this database server.
	 * @return  IP address in v4 format.
	 */
	public String getIPv4(){
		return ipv4;
	}

	/**
	 * Allows to get IP address v6 for this database server.
	 * @return  IP address in v6 format.
	 */
	public String getIPv6(){
		return ipv6;
	}

	/**
	 * Allows to get database port.
	 * @return  Data base port.
	 */
	public String getPuerto(){
		return puerto;
	}

	/**
	 * Allows to get database's SID.
	 * @return  Database SID.
	 */
	public String getSid(){
		return sid;
	}

	/**
	 * Allows to get database user.
	 * @return  Database user.
	 */
	public String getUser(){
		return usr;
	}

	/**
	 * Allows to get database password.
	 * @return  Database password.
	 */
	public String getPassword(){
		return passwd;
	}

	/**
	 * Allows to get database server name.
	 * @return  Database server name.
	 */
	public String getServerName(){
		return serverName;
	}

	/**
	 * Allows to get database name.
	 * @return  Database name.
	 */
	public String getDBName(){
		return dbName;
	}

	/**
	 * Allows to get database server identifier.
	 * @return  Database server idetifier.
	 */
	public String getServerID(){
		return serverID;
	}

	/**
	 * Allows to get the kind of database.
	 * @return  Database kind.
	 */
	public String getDBKind(){
		return dbKind;
	}
	/**
	 * Allows to set database configuration file.
	 * @param  dbConfigFile Database configuration file.
	 */
	public void setDBConfFile(String dbConfigFile){
		this.dbConfigFile = dbConfigFile;
	}

	/**
	 * Allows to set database configuration file.
	 * @return  dbConfigFile
	 */
	public String getDBConfFile(){
		return this.dbConfigFile;
	}
	/**
	 * Allows to check if configuration is settled and complete.
	 * @return A boolean flag telling if data is configured.
	 */
	public synchronized boolean isConfigured(){
		try{
			confLock.lock();
			return isSetConf;
		}finally{
			confLock.unlock();
		}
	}
	/**
	 * Allow to set IPv4.
	 *
	 * @param ip The IPv4 address to connect to database.
	 */
	public void setIPv4(String ip) {
		ipv4 = ip;
	}
	/**
	 * Allow to set IPv4.
	 *
	 * @param ip The IPv6 address to connect to database.
	 */
	public void setIPv6(String ip) {
		ipv6 = ip;
	}
	/**
	 * Allow to set port number.
	 *
	 * @param portNumber The port number to connect to database.
	 */
	public void setPortNumber(String portNumber) {
		puerto = portNumber;
	}
	/**
	 * Allow to set database's SID.
	 *
	 * @param dbSID The database's SID.
	 */
	public void setSID(String dbSID) {
		sid = dbSID;
	}
	/**
	 * Allow to set user.
	 *
	 * @param user The database's SID.
	 */
	public void setUser(String user) {
		usr = user;
	}
	/**
	 * Allow to set database's secret.
	 *
	 * @param secret The database's secret.
	 */
	public void setSecret(String secret) {
		passwd = secret;
	}
	/**
	 * Allow to set database's server name.
	 *
	 * @param serverName The database's server name.
	 */
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	/**
	 * Allow to set database's name.
	 *
	 * @param databaseName The database's name.
	 */
	public void setDatabaseName(String databaseName) {
		dbName = databaseName;
	}
	/**
	 * Allow to set database's name.
	 *
	 * @param serverId The database's server id.
	 */
	public void setServerId(String serverId) {
		serverID = serverId;
	}
	/**
	 * Allow to set database's name.
	 *
	 * @param setConf Boolean that said how many
	 */
	public void setConf(boolean setConf) {
		isSetConf = setConf;
	}
	/**
	 * Allow to set database's kind.
	 *
	 * @param kind The database's kind.
	 */
	public void setDbKind(String kind) {
		dbKind = kind;
	}
	/**
	 * Allow to set database's kind.
	 *
	 * @param flag A Boolean flag telling if the configuration is set or not.
	 */
	public void setConfOn(boolean flag) {
		isSetConf = flag;
	}
}
