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

/**
 *
 * <a href="mailto:sbasurto@soft-gator.com">Sergio Basurto Juárez</a>
 */

import gator.lib.logs.GappLogging;
import gator.lib.db.GappDBConnection;
import gator.lib.db.ADO;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.*;

/**
 * This class is not longer supported and/or used, will be removed soon.
 * 
 * @author <a href="mailto:sbasurto@soft-gator.com">Sergio Basurto Juárez</a>
 * @deprecated In favor of ADO.
 * @see ADO
 */
public class DB {
    private String[][] resultado;
    private String[] columnNames;
    private List <HashMap<String, String>> resultado2 = new ArrayList<HashMap<String, String>>();
    private int numOfRows;
    private int numOfCols;
    /**
     * Log instance to do the log.
     */
    private final GappLogging logs = new GappLogging();
    /**
     *
     */
    public  static int SECURITY = 0;

    /**
     *
     */
    public  static int DATA = 1;
    private GappDBConnection [] conns = new GappDBConnection[2];
    private int whichServer = 1;
    private int whichDat = 1;
    private final Lock  dbLock = new ReentrantLock();
    private DBConf  sec = new DBConf();
    private DBConfData data = new DBConfData();

    /**
     * This constructor is called when the security has been initialized.
     * @param data The connection information for the DB that we want to access.
     */
    public DB(DBConfData data){
        sec = data;
        this.data = data;
        sec.setConf();
        conns[0] = new GappDBConnection(sec.getServerID(),sec.getIPv4(),sec.getIPv6(),sec.getServerName(),sec.getSid(),sec.getPuerto(),sec.getUser(),sec.getPassword(),sec.getDBName(),sec.getDBKind());
        //conns[1] = new Connection(data.getServerID(),data.getIPv4(),data.getIPv6(),data.getServerName(),data.getSid(),data.getPuerto(),data.getUser(),data.getPassword(),data.getDBName(),conns[0].getDBKind());
        conns[1] = new GappDBConnection(data.getServerID(),data.getIPv4(),data.getIPv6(),data.getServerName(),data.getSid(),data.getPuerto(),data.getUser(),data.getPassword(),data.getDBName(),data.getDBKind());
        logs.logIt(this.getClass().getCanonicalName(),"ServerS Id: " + sec.getServerID() + " ipv4:" + sec.getIPv4() +" ipv6:" + sec.getIPv6() + " Server Name:" + sec.getServerName() + " sid: " + sec.getSid() + " puerto:" + sec.getPuerto() + " usuario:" + sec.getUser() + " contraseña:[redacted] DB Name:" + sec.getDBName() + " DB Kind:" + sec.getDBKind(),  "", "", 5);
        logs.logIt(this.getClass().getCanonicalName(),"ServerD Id: " + data.getServerID() + " ipv4:" + data.getIPv4() +" ipv6:" + data.getIPv6() + " Server Name:" + data.getServerName() + " sid: " + data.getSid() + " puerto:" + data.getPuerto() + " usuario:" + data.getUser() + " contraseña:[redacted] DB Name:" + data.getDBName() + " DB Kind:" +  data.getDBKind(),   "", "", 5);
    }
    /**
     * This constructor is called to initialize security.
     * @param server The index for security server that has been choose by the user.
     * @param dat The index for data that has been choose by user.
     */
    public DB(int server, int dat){        
        sec.setConf();
        conns[0] = new GappDBConnection(sec.getServerID(),sec.getIPv4(),sec.getIPv6(),sec.getServerName(),sec.getSid(),sec.getPuerto(),sec.getUser(),sec.getPassword(),sec.getDBName(),sec.getDBKind());
        //conns[1] = new Connection(data.getServerID(),data.getIPv4(),data.getIPv6(),data.getServerName(),data.getSid(),data.getPuerto(),data.getUser(),data.getPassword(),data.getDBName(),conns[0].getDBKind());
        conns[1] = new GappDBConnection(data.getServerID(),data.getIPv4(),data.getIPv6(),data.getServerName(),data.getSid(),data.getPuerto(),data.getUser(),data.getPassword(),data.getDBName(),data.getDBKind());
        logs.logIt(this.getClass().getCanonicalName(),"ServerS Id: " + sec.getServerID() + " ipv4:" + sec.getIPv4() +" ipv6:" + sec.getIPv6() + " Server Name:" + sec.getServerName() + " sid: " + sec.getSid() + " puerto:" + sec.getPuerto() + " usuario:" + sec.getUser() + " contraseña:[redacted] DB Name:" + sec.getDBName() + " DB Kind:" + sec.getDBKind(),   "", "", 5);
        logs.logIt(this.getClass().getCanonicalName(),"ServerD Id: " + data.getServerID() + " ipv4:" + data.getIPv4() +" ipv6:" + data.getIPv6() + " Server Name:" + data.getServerName() + " sid: " + data.getSid() + " puerto:" + data.getPuerto() + " usuario:" + data.getUser() + " contraseña:[redacted] DB Name:" + data.getDBName() + " DB Kind:" +  data.getDBKind(),   "", "", 5);
        logs.logIt(this.getClass().getCanonicalName(),"whicServer: " + server + " whichDat:" + dat,  "", "", 5);
        whichServer = server;
        whichDat = dat;
    }

    /**
     * Execute an instruction into database.
     * @param query The query to be executed.
     * @return Boolean flag telling if the execution was successful.
     */
    public synchronized boolean doDB(String query){
        ADO dbPtr = null;
        try{
            dbLock.lock();
            numOfRows = 0;
            numOfCols = 0;
            logs.logIt("DB.doDB","Running on Server:" + conns[whichServer].getServerIP("v4") + ", Port: " +  conns[whichServer].getPort() + ", sid:" + conns[whichServer].getSID() + ", DB Kind:" + conns[whichServer].getDBKind(),   "", "", 8);
            dbPtr = new ADO(conns[whichServer].getServerIP("v4"),
                                conns[whichServer].getPort(),
                                conns[whichServer].getSID(),
                                conns[whichServer].getUser(),
                                conns[whichServer].getPasswd(),
                                conns[whichServer].getDBKind()
                                );
            /*if(!dbPtr.execute(query)){
                resultado = new String[1][1];
                resultado[0][0] = "No hay registros";
                numOfRows = 1;
                numOfCols = 1;
                dbPtr.close();
                return false;
            } */           
            resultado = new String[dbPtr.getNumOfRow()][dbPtr.getNumOfCol()];
            //resultado = dbPtr.getResult();
            numOfRows = dbPtr.getNumOfRow();
            numOfCols = dbPtr.getNumOfCol();
            //columnNames = dbPtr.getColumnNames();
            resultado2 = dbPtr.getResult2();            
            return true;
        }finally{   
            if(dbPtr != null)
                dbPtr.close();
            dbLock.unlock();
        }
    }

    /**
     * Allows to get the result of last executed query.
     * @return The result as a matrix of two dimensions.
     */
    public String[][] getResultado(){
        return resultado;
    }

    /**
     * Allows to get the total number of rows return by last executed query.
     * @return An integer with the total number of rows.
     */
    public int getNumOfRows(){
        return numOfRows;
    }

    /**
     * Allows to get the total number of columns returned by last executed query.
     * @return An integer with the total number of columns.
     */
    public int getNumOfCols(){
        return numOfCols;
    }

    /**
     * Allows to get the names of the columns as an string array.
     * @return A string array with the names of the columns for last executed query.
     */
    public String[] getColumnNames(){
        return columnNames;
    }

    /**
     * Get the security configuration.
     * @return Database configuration for security as DBConf object.
     */
    public DBConf getDBConfSec(){
        return sec;
    }

    /**
     * Get general data database configuration.
     * @return Database configuration for data as DBConfData object
     */
    public DBConfData getDBConfData(){
        return data;
    }
    /**
     * Get the results of last query executed.
     * @return  As hash map &lt;String, String\&gt;
     */
    public List<HashMap<String, String>> getResultado2(){
        return resultado2;
    }
}
