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
package gator.lib.db.dbms;

import gator.lib.logs.GappLogging;
import java.sql.*;
import java.util.Properties;

/**
 * MysqlDB class manage any connection of mysql database.
 *
 * @author <a href="mailto:sbasurto@soft-gator.com">Sergio Basurto Juárez</a>
 */
public class MysqlDB {
	/**
	 * Driver name string.
	 */
	public final static String DRIVER_NAME = "com.mysql.jdbc.Driver";

	/**
	 * Connection's url.
	 */
	private String url;

	/**
	 * Connection's properties.
	 */
	private Properties props;

	/**
	 * The logger for this class.
	 */
	private final GappLogging logs = new GappLogging();

	/**
	 * Constructor for this class.
	 *
	 * @param serverName 	String representing server name.
	 * @param portNum	Port number as string.
	 * @param SID		Database sid as string.
	 * @param ssl		Flag telling if this connection is through ssl "true" or "false" as string.
	 * @param user		Database's user.
	 * @param password	Database password.
	 */
	public MysqlDB(String serverName,String portNum, String SID, String ssl, String user, String password){
		try{
			Class.forName("com.mysql.jdbc.Driver");
			url = "jdbc:mysql://" + serverName + (portNum.equals("")?"":":" + portNum)+ "/" + SID;
			this.props = new Properties();
			this.props.setProperty("user",user);
			this.props.setProperty("password",password);
			if(ssl.equals("true") || ssl.equals("false")) {
				props.setProperty("ssl","true");
			}
		}catch(ClassNotFoundException e){
			logs.logIt(this.getClass().getCanonicalName(),"No se encontro la clase de Mysql: " + e.toString() ,  "", "", 0);
		}
	}

	/**
	 * Retrieves connection's url.
	 *
	 * @return The connection's url as string for mysql.
	 */
	public String getUrl(){
		return this.url;
	}

	/**
	 * Retrieve the connection's properties.
	 *
	 * @return A Properties object with connection's properties.
	 */
	public Properties getProps(){
		return this.props;
	}

	/**
	 * Make connection to database.
	 *
	 * @return Connection object of connection was successful null otherwise.
	 */
	public Connection connect(){
		try {
			return DriverManager.getConnection("jdbc:apache:commons:dbcp:softgator",getProps());
		} catch (Exception e){
			logs.logIt(this.getClass().getSimpleName(), logs.getStackTraceString(e),  "", "", 11);
			return null;
		}
	}
}
