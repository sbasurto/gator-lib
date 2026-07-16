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

import com.google.gson.Gson;
import gator.lib.db.conf.GappDBConfFile;
import gator.lib.db.dbms.MysqlDB;
import gator.lib.db.dbms.OracleDB;
import gator.lib.db.dbms.PostgresDB;
import gator.lib.io.files.GappFiles;
import gator.lib.logs.GappLogging;
import gator.lib.sec.ids.GappUUIDFactory;
import gator.lib.db.conf.GappIndexFile;
import gator.lib.uihelpers.GappUIHelper;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.naming.InitialContext;
import javax.sql.DataSource;

/**
 * ADO is the class that allow access to different types of databases
 * at this moment the only supported databases are:
 * <ol>
 * <li>Postgresql</li>
 * <li>Mysql</li>
 * <li>Oracle</li>
 * </ol>
 * the oracle and mysql support will be dropped soon, giving preference to open
 * source databases like postgresql.
 *
 * @author <a href="mailto:sbasurto@soft-gator.com">Sergio Basurto Juárez</a>
 * @version     0.1, 01 Jan 2006
 * @version     1.0, 06 Jun 2013
 */
public class ADO {
        /**
         * Connection unique identifier.
         */
         private String connUUID = "";

         private final GappUUIDFactory gappUUIDFactory = new GappUUIDFactory();

	/**
	 * The database's connection.
	 */
	private Connection connection;

	/**
	 * The number of columns returned by query.
	 */
	private int numOfCol = 0;

	/**
	 * The numbers of rows returned by query.
	 */
	private int numOfRow = 0;

	/**
	 * The new way to store results, as an array list of hash map.
	 */
	private ArrayList <HashMap<String,String>> result2 = new ArrayList<>();

	/**
	 * Log instance to do the log.
	 */
	private final GappLogging logs = new GappLogging();

	/**
	 * Database's configuration file.
	 */
	private GappDBConfFile adoDBConfigFile = new GappDBConfFile();

        private GappIndexFile gappIndexFile;

        ExecutorService executionPool;

	/**
	 * Initialize the ADO object with database parameters.
	 * @param server	Server name or IP address.
	 * @param portNumber	Database's port number.
	 * @param SID		Database's SID.
	 * @param user		Database's user.
	 * @param secret	Database's secret.
	 * @param dbKind	Database's kind, could be psql, oracle, mysql.
	 */
	public ADO(String server,String portNumber, String SID, String user, String secret, String dbKind){
		adoDBConfigFile.setServer(server);
		adoDBConfigFile.setPortNumber(portNumber);
		adoDBConfigFile.setSID(SID);
		adoDBConfigFile.setDBKind(dbKind);
		adoDBConfigFile.setServerName(user);
		adoDBConfigFile.setSecret(secret);
                executionPool = Executors.newFixedThreadPool(6000);
	}

	/**
	 * Initialize the ADO object with database parameters.
         *
	 * @param gappConfigFile    The name of the file that has all the configuration as JSON, and example of JSON is:
         * <pre>
         * {@code
	 * 	{
	 * 		"file":"indexMaster",
	 * 		"nameToDisplay":"GatorW Master",
	 * 		"keyStore": "default",
	 * 		"signature":"",
	 * 		"language":"es",
	 * 		"files": ["initGatorWMaster","initGatorSMaster","initGatorEMaster"],
	 * 		"pkeyusr":"myUser",
	 * 		"pkeysecret":"3456451$#2c21s/464fs$872f%%1be511683d0e",
	 * 		"conff":"pg_wms_master",
         *              "replications": ["pg_a","pg_b","pg_c"]
	 * 	}
         * }
         * </pre>
	 * and the file to connect to database is conff and example is:
         * <pre>
         * {@code
	 * 	{
	 * 		"tipodb":"pgsql",
	 * 		"servidor":"127.0.0.1",
	 * 		"puerto":"5432",
	 * 		"sid":"database name (sid)",
	 * 		"usuario":"the user to connect to db",
	 * 		"password":"a very secure password",
	 * 		"servidorNombre":"Master",
	 * 		"dbNombre":"Name of System"
	 * 	}.
         * }
         * </pre>
	 * 	ADO Search for file in the configuration path as stated in GappFiles constant CONF_DIR.
         * @see GappUIHelper
         * @see GappFiles
	 */
	public ADO(String gappConfigFile) {
                Gson gson = new Gson();
		GappFiles gappFiles = new GappFiles();
		GappUIHelper uiHelper = new GappUIHelper();
		gappIndexFile = uiHelper.getIndexConfig2(gappConfigFile, "anonymous");
		String json = gappIndexFile.getJson();
		logs.logIt("ADO", "Db info (1):" + json,  "ADO", "ADO", 0);
		logs.logIt("ADO", "Db info (2):" + gappIndexFile.getConfigurationFile(),  "ADO", "ADO", 0);
		gappFiles.setDir(GappFiles.CONF_DIR);
		gappFiles.readFromFile(GappFiles.CONF_DIR + gappIndexFile.getConfigurationFile(), "UTF-8");
		String dbJson = gappFiles.getReadedLinesAsString();
		adoDBConfigFile = gson.fromJson(dbJson, GappDBConfFile.class);
                executionPool = Executors.newFixedThreadPool(6000);
		logs.logIt("GappJNDIRealm.setJndiDataSourceName", "Db info (3):" + adoDBConfigFile.getSID(),  "ADO", "ADO", 0);
		logs.logIt("GappJNDIRealm.setJndiDataSourceName", "Db info (4):" + adoDBConfigFile.getServerName(),  "ADO", "ADO", 0);
	}
        /**
	 * Initialize the ADO object with database file directly.
         *
	 * @param dbConfFile    The name of the file that has all the configuration as JSON, and example of JSON is:
         * <pre>
         * {@code
	 * 	{
	 * 		"tipodb":"pgsql",
	 * 		"servidor":"127.0.0.1",
	 * 		"puerto":"5432",
	 * 		"sid":"database name (sid)",
	 * 		"usuario":"the user to connect to db",
	 * 		"password":"a very secure password",
	 * 		"servidorNombre":"Master",
	 * 		"dbNombre":"Name of System"
	 * 	}.
         * }
         * </pre>
	 * 	ADO Search for file in the configuration path as stated in GappFiles constant CONF_DIR.
         * @param isDb Flag to tell that file has the DataBase configuration directly.
         * @see GappUIHelper
         * @see GappFiles
	 */
	public ADO(String dbConfFile, boolean isDb) {
                Gson gson = new Gson();
                GappFiles gappFiles = new GappFiles();
		gappFiles.setDir(GappFiles.CONF_DIR);
		gappFiles.readFromFile(GappFiles.CONF_DIR + dbConfFile, "UTF-8");
		String dbJson = gappFiles.getReadedLinesAsString();
		adoDBConfigFile = gson.fromJson(dbJson, GappDBConfFile.class);
                executionPool = Executors.newFixedThreadPool(6000);
		logs.logIt("GappJNDIRealm.setJndiDataSourceName", "Db info (3):" + adoDBConfigFile.getSID(),  "ADO", "ADO", 0);
		logs.logIt("GappJNDIRealm.setJndiDataSourceName", "Db info (4):" + adoDBConfigFile.getServerName(),  "ADO", "ADO", 0);
	}
        /**
	 * Allow to execute prepared statements.
	 *
         * @param gappSQLStmt   The statement to be executed.
	 */
	public void executePreparedStmt(GappSQLStatement gappSQLStmt) {
		if(this.connection == null) startPool4DBKind();
		result2 = new ArrayList<>();
		try {
                        logs.logIt(this.getClass().getCanonicalName(),"Parametros: " + String.join(",", gappSQLStmt.getInParams()),  "ADO", "executePreparedStmt", 0);
                        try {
                                if(!this.connection.isValid(0)) startPool4DBKind();
                        } catch (Exception e) {
                                close();
                                startPool4DBKind();
                        }
			PreparedStatement prep = gappSQLStmt.getPrepStmt(this.connection);
			ResultSet resultSet = prep.executeQuery();
			ResultSetMetaData resMetaData = resultSet.getMetaData();
			int index = 0;
			numOfCol = resMetaData.getColumnCount();
			resultSet.last();
			numOfRow = resultSet.getRow() == 0 ? 1 : resultSet.getRow();
                        logs.logIt(this.getClass().getCanonicalName(),"renglones:" + numOfRow,  "ADO", "executePreparedStmt", 0);
			resultSet.beforeFirst();

			if(resultSet.next()){
				resultSet.beforeFirst();
				while(resultSet.next()){
					HashMap<String, String> temp = new HashMap<>(resMetaData.getColumnCount());
					for(int i = 1; i <= numOfCol; i++) {
						temp.put(resMetaData.getColumnName(i), resultSet.getString(i));
					}
					result2.add(temp);
					index++;
				}
			}
                        logs.logIt(this.getClass().getCanonicalName(),"done",  "ADO", "executePreparedStmt", 0);
                        prep.close();
                        resultSet.close();
			if(gappSQLStmt.isReplicable()) {
                        	executionPool.execute(() -> {
                            		replicatePreparedStmt(gappSQLStmt);
                        	});
			}
		} catch(Exception e) {
			logs.logIt(this.getClass().getCanonicalName(),"Sentencia prep query on exception: " + gappSQLStmt.getQuery(),  "ADO", "executePreparedStmt", 0);
			logs.logIt(this.getClass().getCanonicalName(),"Sentencia prep: " + logs.getStackTraceString(e),  "ADO", "executePreparedStmt", 0);
		}
	}       
        /**
	 * Allow to execute prepared statements.
	 *
         * @param gappSQLStmt   The statement to be executed.
	 */
	public ResultSet executePreparedStmtRset(GappSQLStatement gappSQLStmt) {
		if(this.connection == null) startPool4DBKind();
		result2 = new ArrayList<>();
		try {
                        logs.logIt(this.getClass().getCanonicalName(),"Parametros: " + String.join(",", gappSQLStmt.getInParams()),  "ADO", "executePreparedStmt", 0);
                        try {
                                if(!this.connection.isValid(0)) startPool4DBKind();
                        } catch (Exception e) {
                                close();
                                startPool4DBKind();
                        }
			PreparedStatement prep = gappSQLStmt.getPrepStmt(this.connection);
			ResultSet resultSet = prep.executeQuery();
                        return resultSet;			
		} catch(Exception e) {
			logs.logIt(this.getClass().getCanonicalName(),"Sentencia prep query on exception: " + gappSQLStmt.getQuery(),  "ADO", "executePreparedStmt", 0);
			logs.logIt(this.getClass().getCanonicalName(),"Sentencia prep: " + logs.getStackTraceString(e),  "ADO", "executePreparedStmt", 0);
                        return null;
		}
	}       
        /**
         * Allows to replicate a statement to any number of servers.
         * @param gappSQLStmt The statement object to be replicated.
         */
        private void replicatePreparedStmt(GappSQLStatement gappSQLStmt) {
                Gson gson = new Gson();
                gappIndexFile.getReplicas().forEach((key, replica) -> {
                    logs.logIt(this.getClass().getCanonicalName(),"Replicating statment:" + gappSQLStmt.getQuery(),  "ADO", "replicatePreparedStmt", 0);
                    if(replica.getStatus().equals("enabled")) {
                            GappFiles gappFiles = new GappFiles();
                            gappFiles.setDir(GappFiles.CONF_DIR);
                            gappFiles.readFromFile(GappFiles.CONF_DIR + GappFiles.FILE_SEP + replica.getConfigFile());
                            GappDBConfFile dbFile = gson.fromJson(gappFiles.getReadedLinesAsString(), GappDBConfFile.class);
                            gappSQLStmt.setDbKind(dbFile.getDbKind());
                            logs.logIt(this.getClass().getCanonicalName(), "Replicating to server:" + key,  "ADO", "replicatePreparedStmt", 0);
                            try {
                                    Connection conn = getConnection(dbFile);
                                    PreparedStatement prep = gappSQLStmt.getPrepStmt(conn);
                                    prep.executeQuery();
                                    prep.close();
                                    conn.close();
                            } catch (Exception e) {
                                    replica.setStatus("down");
                                    gappIndexFile.writeMe();
                                    replica.writeStatment(key, gappSQLStmt.getQueryStr());
                                    logs.logIt(this.getClass().getCanonicalName(), "Fallo ejecuccion: " + logs.getStackTraceString(e),  "ADO", "replicatePreparedStmt", 0);
                                    logs.logIt(this.getClass().getCanonicalName(), "Sentencia fallida: " + gappSQLStmt.getQuery(),  "ADO", "execStore", 0);
                                    logs.logIt(this.getClass().getCanonicalName(), "Orale: " + gappIndexFile.getSimplifiedJson(),  "ADO", "replicatePreparedStmt", 0);
                            }
                            logs.logIt(this.getClass().getCanonicalName(), "Done replicating to server:" + key,  "ADO", "replicatePreparedStmt", 0);
                    } else if(replica.getStatus().equals("down")) {
                            replica.writeStatment(key, gappSQLStmt.getQueryStr());
                    }
                });
        }
	/**
	 * Execute a store procedure.
	 *
         * @param gappSQLStmt The statement to be executed.
	 *
	 * @return A string with the result of the call.
	 */
	public String execStore(GappSQLStatement gappSQLStmt){
		if(this.connection == null) startPool4DBKind();
		try{
                        try {
                                if(!this.connection.isValid(0)) startPool4DBKind();
                        } catch (Exception e) {
                                logs.logIt(this.getClass().getCanonicalName(),"Fallo ejecuccion (0): " + logs.getStackTraceString(e) ,  "ADO", "execStore", 0);
                                close();
                                startPool4DBKind();
                        }
                        gappSQLStmt.setDbKind(adoDBConfigFile.getDbKind());
			CallableStatement stmtCallable = this.connection.prepareCall(gappSQLStmt.getQuery());
                        stmtCallable = gappSQLStmt.registerOutParams(stmtCallable);
			stmtCallable = gappSQLStmt.setParameters(stmtCallable);
			stmtCallable.execute();
			Object obj = stmtCallable.getObject(1);
			if(stmtCallable != null) {
				stmtCallable.close();
			}
                        if(gappSQLStmt.isStoreReplicable()) {
                                executionPool.execute(() -> {
                                        replicateStore(gappSQLStmt);
                                });
                        }
			return obj.toString();
		}catch(Exception e){
			logs.logIt(this.getClass().getCanonicalName(),"Fallo ejecuccion: " + logs.getStackTraceString(e) ,  "ADO", "execStore", 0);
			logs.logIt(this.getClass().getCanonicalName(),"sentencia: " + gappSQLStmt.getQuery(),  "ADO", "execStore", 0);
                        logs.logIt(this.getClass().getCanonicalName(),"sentencia as str: " + gappSQLStmt.getQueryStr(),  "ADO", "execStore", 0);
			return "-1";
		}
	}
        /**
	 * Execute a store procedure.
	 *
         * @param gappSQLStmt The statement to be executed.
	 *
	 * @return A string with the result of the call.
	 */
	public synchronized String execStoreSync(GappSQLStatement gappSQLStmt){
		if(this.connection == null) startPool4DBKind();
		try{
                        try {
                                if(!this.connection.isValid(0)) startPool4DBKind();
                        } catch (Exception e) {
                                logs.logIt(this.getClass().getCanonicalName(),"Fallo ejecuccion (0): " + logs.getStackTraceString(e) ,  "ADO", "execStore", 0);
                                close();
                                startPool4DBKind();
                        }
                        gappSQLStmt.setDbKind(adoDBConfigFile.getDbKind());
			CallableStatement stmtCallable = this.connection.prepareCall(gappSQLStmt.getQuery());
                        stmtCallable = gappSQLStmt.registerOutParams(stmtCallable);
			stmtCallable = gappSQLStmt.setParameters(stmtCallable);
			stmtCallable.execute();
			Object obj = stmtCallable.getObject(1);
			if(stmtCallable != null) {
				stmtCallable.close();
			}
                        if(gappSQLStmt.isStoreReplicable()) {
                                executionPool.execute(() -> {
                                        replicateStore(gappSQLStmt);
                                });
                        }
			return obj.toString();
		}catch(Exception e){
			logs.logIt(this.getClass().getCanonicalName(),"Fallo ejecuccion: " + logs.getStackTraceString(e) ,  "ADO", "execStore", 0);
			logs.logIt(this.getClass().getCanonicalName(),"sentencia: " + gappSQLStmt.getQuery(),  "ADO", "execStore", 0);
                        logs.logIt(this.getClass().getCanonicalName(),"sentencia as str: " + gappSQLStmt.getQueryStr(),  "ADO", "execStore", 0);
			return "-1";
		}
	}
	/**
	 * Execute a store procedure and returns a result set.
	 *
         * @param gappSQLStmt   The statement to be executed.
	 *
	 * @return	The result set for the execution.
	 */
	public ResultSet execStoreRset(GappSQLStatement gappSQLStmt) {
		if(this.connection == null) startPool4DBKind();
		try {
                        try {
                                if(!this.connection.isValid(0)) startPool4DBKind();
                        } catch (Exception e) {
                                logs.logIt(this.getClass().getCanonicalName(),"Fallo ejecuccion: " + logs.getStackTraceString(e) ,  "ADO", "execStoreRset", 0);
                                close();
                                startPool4DBKind();
                        }
                        gappSQLStmt.setDbKind(adoDBConfigFile.getDbKind());
			CallableStatement stmtCallable = this.connection.prepareCall(gappSQLStmt.getQuery());
                        stmtCallable = gappSQLStmt.registerOutParams(stmtCallable);
			stmtCallable = gappSQLStmt.setParameters(stmtCallable);
			stmtCallable.execute();
			ResultSet obj = (ResultSet)stmtCallable.getObject(1);
			if(stmtCallable != null) {
				stmtCallable.close();
			}
                        if(gappSQLStmt.isStoreReplicable()) {
                                executionPool.execute(() -> {
                                        replicateStore(gappSQLStmt);
                                });
                        }
			return obj;
		}catch(Exception e){
			logs.logIt(this.getClass().getCanonicalName(),"Fallo ejecuccion: " + logs.getStackTraceString(e) ,  "ADO", "execStoreRset", 0);
			logs.logIt(this.getClass().getCanonicalName(),"sentencia: " + gappSQLStmt.getStoreProcedure(),  "ADO", "execStoreRset", 0);
			return null;
		}
	}
        /**
         * Allows to replicate a store procedure call to any number of servers.
         * @param gappSQLStmt The statement object to be replicated.
         */
        private void replicateStore(GappSQLStatement gappSQLStmt) {
		Gson gson = new Gson();
		gappIndexFile.getReplicas().forEach((key, replica) -> {
			logs.logIt(this.getClass().getCanonicalName(),"Replicating store:" + gappSQLStmt.getQuery(),  "ADO", "replicateStore", 0);
			if(replica.getStatus().equals("enabled")) {
				GappFiles gappFiles = new GappFiles();
				gappFiles.setDir(GappFiles.CONF_DIR);
				gappFiles.readFromFile(GappFiles.CONF_DIR + GappFiles.FILE_SEP + replica.getConfigFile());
				GappDBConfFile dbFile = gson.fromJson(gappFiles.getReadedLinesAsString(), GappDBConfFile.class);
				gappSQLStmt.setDbKind(dbFile.getDbKind());
				logs.logIt(this.getClass().getCanonicalName(), "Replicating to server:" + key,  "ADO", "replicateStore", 0);
				try {
					Connection conn = getConnection(dbFile);
					CallableStatement stmtCallable = gappSQLStmt.getCallableStmt(conn);
					stmtCallable = gappSQLStmt.registerOutParams(stmtCallable);
					stmtCallable = gappSQLStmt.setParameters(stmtCallable);
					stmtCallable.executeQuery();
					stmtCallable.close();
					conn.close();
				} catch (Exception e) {
					replica.setStatus("down");
					gappIndexFile.writeMe();
					replica.writeStatment(key, gappSQLStmt.getQueryStr());
					logs.logIt(this.getClass().getCanonicalName(), "Fallo ejecuccion: " + logs.getStackTraceString(e),  "ADO", "replicateStore", 0);
					logs.logIt(this.getClass().getCanonicalName(), "Sentencia fallida: " + gappSQLStmt.getQuery(),  "ADO", "replicateStore", 0);
				}
			} else if(replica.getStatus().equals("down")) {
				replica.writeStatment(key, gappSQLStmt.getQueryStr());
			}
		});
	}

	/**
	 * Allows to close database's connection.
	 */
	public void close(){
		try{
			logs.logIt(this.getClass().getCanonicalName(),"Cerrando conexion a db... " ,  "ADO", "close", 4);
			if(this.connection != null) {
				this.connection.close();
				logs.logIt(this.getClass().getCanonicalName(),"La conexion fue cerrada:" + this.connection.isClosed() ,  "ADO", "close", 4);
			}
                        logs.logIt("startPool4DBKind", "CLOSE CONNECTION (" + connUUID + ")",  "ADO", "close", 0);
		}catch(Exception e){
			logs.logIt(this.getClass().getCanonicalName(),"Al cerrar conexion: " + logs.getStackTraceString(e) ,  "ADO", "close", 0);
		}
	}

	/**
	 * Allow to retrieve the number of columns returned by last execution.
	 *
	 * @return The number of columns for last execution.
	 */
	public int getNumOfCol(){
		return this.numOfCol;
	}

	/**
	 * Allow to get the number of rows returned by last execution.
	 *
	 * @return The number of rows of last execution.
	 */
	public int getNumOfRow(){
		return this.numOfRow;
	}

	/**
	 * Allows to retrieve rows returned by last execution as ArrayList.
	 *
	 * @return	An array list of hash map (string, string) for last execution.
	 */
	public ArrayList<HashMap<String, String>> getResult2() {
		return result2;
	}


	/**
	 * Retrieve the status of connection.
	 * @return If the connection was succeeded or if was fail.
	 */
	public boolean isConnectionOk () {
		return this.connection != null;
	}

        private Connection getConnection(GappDBConfFile dbFile) {
                PostgresDB pgdbTmp;
                MysqlDB mysql;
                OracleDB oradb;
                connUUID = gappUUIDFactory.getUUID() + "-PLAIN-CONNECTION";
                switch(dbFile.getDbKind()) {
                    case "oracle" -> {
                        oradb = new OracleDB(adoDBConfigFile.getServer(),adoDBConfigFile.getPortNumber(),adoDBConfigFile.getSID());
                        return oradb.connect(adoDBConfigFile.getUser(), adoDBConfigFile.getSecret());
                    }
                    case "mysql" -> {
                        mysql =  new MysqlDB(dbFile.getServer(), dbFile.getPortNumber(), dbFile.getSID(), dbFile.getSSL(), dbFile.getUser(), dbFile.getSecret());
                        return mysql.connect();
                    }
                    case "pgsql" -> {
                        pgdbTmp = new PostgresDB(dbFile.getServer(),dbFile.getPortNumber(),dbFile.getSID(), dbFile.getSSL(), dbFile.getUser(), dbFile.getSecret());
                        return pgdbTmp.connect();
                    }
                    default -> {
                        pgdbTmp = new PostgresDB(dbFile.getServer(),dbFile.getPortNumber(),dbFile.getSID(), dbFile.getSSL(), dbFile.getUser(), dbFile.getSecret());
                        return pgdbTmp.connect();
                    }
                }
        }
	/**
	 * Initialize a database's connection, this happens only if a connection pool returns an error or if there is not pool or
	 * if the JNDI resource was not located.
	 */
	private void startDB4DBKind(){
		logs.logIt(this.getClass().getCanonicalName() + " startDB4DBKind (tipo db)", adoDBConfigFile.getDbKind(),  "ADO", "startDB4DBKind", 0);
                logs.logIt(this.getClass().getCanonicalName() + " startDB4DBKind (servidor)", adoDBConfigFile.getServer() + ", " + adoDBConfigFile.getSID() + ", " + adoDBConfigFile.getPortNumber(),  "ADO", "startDB4DBKind", 0);
                this.connection = this.getConnection(adoDBConfigFile);
                logs.logIt("startPool4DBKind", "OPEN CONNECTION2(" + connUUID + ")",  "ADO", "startDB4DBKind", 0);
	}

	/**
	 * Initialize a connection pool for this object, if for some reason there is not pool then will create
	 * a normal connection to the database and wont print the exception in logs if debug level is under 200.
	 */
	private void startPool4DBKind() {
		try {
                        System.setProperty("javax.sql.DataSource.Factory", "org.apache.commons.dbcp2.BasicDataSourceFactory");
			InitialContext cxt = new InitialContext();
			DataSource ds = (DataSource) cxt.lookup( "java:/comp/env/" + this.adoDBConfigFile.getSID() );
			Class.forName("org.postgresql.Driver");
			logs.logIt("startPool4DBKind", "adoDBConfigFile.getSID():" + this.adoDBConfigFile.getSID(),  "ADO", "startPool4DBKind", 0);
			this.connection = ds.getConnection();
                        logs.logIt("startPool4DBKind", "Start Pool For DB Kind",  "ADO", "startPool4DBKind", 0);
                        connUUID = gappUUIDFactory.getUUID();
                        logs.logIt("startPool4DBKind", "OPEN CONNECTION(" + connUUID + ")",  "ADO", "startPool4DBKind", 0);
		} catch(Exception e) {
			logs.logIt("startPool4DBKind", "ADO(SID): " + this.adoDBConfigFile.getSID(),  "ADO (SID)", "startPool4DBKind", 0);
			logs.logIt("startPool4DBKind", "Could not find JNDI resource (" + adoDBConfigFile.getSID() + ") so I will try to bind it.",  "ADO", "startPool4DBKind", 0);
                        logs.logIt("startPool4DBKind", "If you want to see the ugly exception, up the debug level above 200\n\n\n\n\n",  "ADO", "startPool4DBKind", 0);
                        logs.logIt("startPool4DBKind", "ADO: " + logs.getStackTraceString(e),  "ADO", "startPool4DBKind", 200);
			startDB4DBKind();
		}
	}

	/**
	 * Allows to retrieve database's configuration file.
	 *
	 * @return A GappDBConfFile.
	 */
	public GappDBConfFile getDbInfo() {
		return adoDBConfigFile;
	}
}
