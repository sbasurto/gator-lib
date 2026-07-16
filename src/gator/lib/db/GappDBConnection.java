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
package gator.lib.db;

/**
 * GappDBConnection is the class that describes Database connection with
 * all its attributes.
 *
 * @author <a href="mailto:sbasurto@soft-gator.com">Sergio Basurto Juárez</a>
 * @version     0.1, 01 Jan 2006
 */
public class GappDBConnection {
	/**
	 * Server identifier.
	 */
	private final String serverId;

	/**
	 * Connection ipv4 address.
	 */
	private final String ipv4;

	/**
	 * Connection ipv6 address.
         */
	private final String ipv6;

	/**
	 * Database instance.
	 */
	private final String sid;

	/**
	 * Database port.
	 */
	private final String port;

	/**
	 * Database user.
	 */
	private final String user;

	/**
	 * Database password.
	 */
	private final String passwd;

	/**
	 * Server name string.
	 */
	private final String serverName;

	/**
	 * Database name string.
	 */
	private final String dbName;

	/**
	 * Kind of db, it could be postgresql, mysql, oracle, etc.
	 */
	private final String dbKind;

	/**
	 * Group for this connection.
	 */
	private String dbGroup;
        
        /**
	 * Timeout for this connection.
	 */
	private String timeout = "10000";

	/**
	 * Database connection constructor.
	 * @param serverId	Server identifier.
	 * @param ipv4		Connection ip address.
	 * @param ipv6		Connection ipv6 address.
	 * @param serverName	Server name string.
	 * @param sid		Database instance.
	 * @param port		Database port.
	 * @param user		Database user.
	 * @param passwd	Database password.
	 * @param dbName	Database name string.
	 * @param dbKind	Database kind.
	 */
	public GappDBConnection(String serverId, String ipv4, String ipv6, String serverName,String sid, String port, String user, String passwd, String dbName, String dbKind){
		this.serverId = serverId;
		this.ipv4 = ipv4;
		this.ipv6 = ipv6 == null ? "" : ipv6;
		this.serverName = serverName;
		this.sid = sid;
		this.port = port;
		this.user = user;
		this.passwd = passwd;
		this.dbName = dbName;
		this.dbKind = dbKind;
	}

	/**
	 * Returns server ip address.
	 *
	 * @param whichVersion A string telling if version is v4 or v6.
	 * @return The ipvx address as requested.
	 */
	public String getServerIP(String whichVersion){
		if(whichVersion.equals("v4")) {
			return this.ipv4;
		} else {
			return this.ipv6;
		}
	}

	/**
	 * Retrieve server name string.
	 *
	 * @return The server name string.
	 */
	public String getServerName(){
		return this.serverName;
	}

	/**
	 * Retrieve database instace.
	 *
	 * @return Database instace string.
	 */
	public String getSID(){
		return sid;
	}

	/**
	 * Retrieve database's user.
	 *
	 * @return Database's user as string.
	 */
	public String getUser(){
		return user;
	}

	/**
	 * Retrieve database's password.
	 *
	 * @return Database's password as string.
	 */
	public String getPasswd(){
		return passwd;
	}

	/**
	 * Retrieve database's port.
	 *
	 * @return Database's port as string.
	 */
	public String getPort(){
		return port;
	}

	/**
	 * Retrieve database's name.
	 *
	 * @return Database's name as string.
	 */
	public String getDBName(){
		return this.dbName;
	}

	/**
	 * Retrieve server's identifier.
	 *
	 * @return Server's identifier as string.
	 */
	public String getServerID(){
		return this.serverId;
	}

	/**
	 * Retrieve database's kind.
	 *
	 * @return Database's kind as string.
	 */
	public String getDBKind(){
		return this.dbKind;
	}

	/**
	 * Retrieve connection as simple string.
	 * @return String with connection information.
	 */
	@Override
	public String toString(){
		return "serverID: " + serverId + ",ipv4:" + ipv4 + ",ipv6:" + ipv6 + ",sid:" + sid + ",port:" + port + ",user:" + user + ",passwd:[redacted],serverName:" + serverName + ",dbName:" + dbName + ",dbKind: " + dbKind;
	}

	/**
	 * Allows to define database's group.
	 *
	 * @param group Database group as string.
	 */
	public void setDBGroup(String group){
		this.dbGroup = group;
	}

	/**
	 * Retrieve database's group.
	 *
	 * @return Database's group.
	 */
	public String getDBGroup(){
		return this.dbGroup;
	}
        
        /**
	 * Retrieve database's connection timeout.
	 *
	 * @return Database's connection timeout.
	 */
	public String getTimeout(){
		return this.timeout;
	}
}
