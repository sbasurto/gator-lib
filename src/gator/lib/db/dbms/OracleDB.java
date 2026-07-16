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

import gator.lib.logs.GappLog;
import gator.lib.logs.GappLogging;
import java.sql.*;
import oracle.jdbc.*;
import oracle.jdbc.pool.*;

/**
 * OracleDB is the class for make connection to oracle's databases.
 *
 * @author <a href="mailto:sbasurto@soft-gator.com">Sergio Basurto Juárez</a>
 */
public class OracleDB {
	/**
	 * Database's driver string.
	 */
	public final static String DRIVER_NAME = "oracle.jdbc.driver.OracleDriver";

	/**
	 * Connection's url.
	 */
	private String url = "";

	/**
	 * Oracle's datasource.
	 */
	private OracleDataSource oracleDS;

	/**
	 * Connection object.
	 */
	private Connection connection;

	/**
	 * String matrix for get results.
	 */
	private String [][] result;

	/**
	 * Integer representing number of columns.
	 */
	private int numOfCol = 0;
        
        /**
	 * Integer representing number of columns.
	 */
	private String server = "localhost";
        private String port = "1521";
        private String sid = "";

	/**
	 * Integer representing number of rows.
	 */
	private int numOfRow = 0;

	/**
	 * String matrix with column names for each row.
	 */
	private String[] columnNames;

	/**
	 * Logger for this class.
	 */
	private final GappLogging logs = new GappLogging();

	/**
	 * Oracle's varchar data type.
	 */
	public static final int ORA_VARCHAR = OracleTypes.VARCHAR;

	/**
	 * Oracle's number data type.
	 */
	public static final int ORA_NUMBER = OracleTypes.NUMBER;

	/**
	 * Oracle's integer data type.
	 */
	public static final int ORA_INTEGER = OracleTypes.INTEGER;
        
        

	/**
	 * Constructor for this class.
	 * @param serverName	Server's name as string.
	 * @param portNum	Port number as string.
	 * @param SID		Database's sid as string.
	 */
	public OracleDB(String serverName,String portNum, String SID){                            
                url = "jdbc:oracle:thin:@//" + serverName + ":" + portNum + "/" + SID;
		//url = "jdbc:oracle:thin:@" + serverName + ":" + portNum + ":" + SID;
	}

	/**
	 * Retrieve database's url.
	 *
	 * @return String representation of connection's url.
	 */
	public String getUrl(){
		return this.url;
	}

	/**
	 * Does connection to database.
	 *
	 * @param user		Database's user.
	 * @param password	Database's password.
	 *
	 * @return Connection object for database or null if something goes wrong.
	 */
	public Connection connect(String user, String password){
		try{
                        GappLog gappLog = new GappLog();                        
                        gappLog.setName("OracleConnection");
                        gappLog.addMessage("Connecting to Oracle database: " + url);                        
                        gappLog.setLevel(0);
                        logs.logIt(gappLog);
			oracleDS = new OracleDataSource();
			oracleDS.setUser(user);
			oracleDS.setPassword(password);
			oracleDS.setURL(url);
			connection = oracleDS.getConnection();
			return connection;
		}catch(SQLException e){
			logs.logIt(this.getClass().getSimpleName(), logs.getStackTraceString(e),  "", "", 11);
			return null;
		}
	}
}
