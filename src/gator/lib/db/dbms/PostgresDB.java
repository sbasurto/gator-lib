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
package gator.lib.db.dbms;

import gator.lib.logs.GappLogging;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Types;
import java.util.Properties;

/**
 * PostgresDB is the class to connect to any postgresql database.
 *
 * @author <a href="mailto:sbasurto@soft-gator.com">Sergio Basurto Juárez</a>
 */
public class PostgresDB {
	/**
	 * Driver name as string.
	 */
	public final static String DRIVER_NAME = "org.postgresql.Driver";

	/**
	 * Connection's url.
	 */
	private String url;

	/**
	 * Connection's properties as a Properties object.
	 */
	private Properties props;

	/**
	 * Logger for this class.
	 */
	private final GappLogging logs = new GappLogging();

	/**
	 * Postgresql's varchar data type.
	 */
	public static final int PG_VARCHAR = Types.VARCHAR;
	
	/**
	 * Postgresql's numeric data type.
	 */
	public static final int PG_NUMBER = Types.NUMERIC;
	
	/**
	 * Postgresql's integer data type.
	 */
	public static final int PG_INTEGER = Types.INTEGER;
	
	/**
	 * Constructor for this class.
	 * @param serverName	Database's server name as string.
	 * @param portNum	Database's port number as string.
	 * @param SID		Database's SID as string.
	 * @param ssl		Flag telling if database require SSL connection "true" or "false" as string.
	 * @param user		Database's user.
	 * @param password	Database's password.
	 */
	public PostgresDB(String serverName,String portNum, String SID, String ssl, String user, String password){
		try{
			Class.forName("org.postgresql.Driver");
			this.url = "jdbc:postgresql://" + serverName + (portNum.equals("")?"":":" + portNum)+ "/" + SID;
			this.props = new Properties();
			this.props.setProperty("user",user);
			this.props.setProperty("password",password);
			if(ssl.equals("true") || ssl.equals("false")) {
				this.props.setProperty("ssl","true");
			}
		}catch(ClassNotFoundException e){
			logs.logIt(this.getClass().getName(),"No se encontro la clase de Postgresql: " + logs.getStackTraceString(e) ,  "", "", 0);
		}
	}
        
        /**
	 * Constructor for this class.
	 * @param serverName        Database's server name as string.
	 * @param portNum           Database's port number as string.
	 * @param SID               Database's SID as string.
	 * @param ssl               Flag telling if database require SSL connection "true" or "false" as string.
	 * @param user              Database's user.
	 * @param password          Database's password.
         * @param connectionTimeout Timeout in seconds for socket connection.
	 */
	public PostgresDB(String serverName,String portNum, String SID, String ssl, String user, String password, String connectionTimeout){
		try{
			Class.forName("org.postgresql.Driver");
			this.url = "jdbc:postgresql://" + serverName + (portNum.equals("")?"":":" + portNum)+ "/" + SID;
			this.props = new Properties();
                        this.props.setProperty("connectTimeout", connectionTimeout);
			this.props.setProperty("user",user);
			this.props.setProperty("password",password);
			if(ssl.equals("true") || ssl.equals("false")) {
				this.props.setProperty("ssl","true");
			}
		}catch(ClassNotFoundException e){
			logs.logIt(this.getClass().getName(),"No se encontro la clase de Postgresql: " + logs.getStackTraceString(e) ,  "", "", 0);
		}
	}
	
	/**
	 * Retrieve connection's url.
	 *
	 * @return Connection's url as string.
	 */
	public String getUrl(){
		return this.url;
	}
	
	/**
	 * Retrieve connection properties.
	 *
	 * @return Connection's properties as Properties object.
	 */
	public Properties getProps(){
		return this.props;
	}
	
	/**
	 * Connect to database.
	 *
	 * @return  Connection object or null if something goes wrong.
	 */
	public Connection connect(){
		try {
			return DriverManager.getConnection(this.getUrl(), this.getProps());
		} catch (Exception e){
			logs.logIt(this.getClass().getCanonicalName(), "Falló conexión a DB " + this.getUrl(),  "", "", 11);
			logs.logIt(this.getClass().getCanonicalName(), logs.getStackTraceString(e),  "", "", 11);
			return null;
		}
	}
}
