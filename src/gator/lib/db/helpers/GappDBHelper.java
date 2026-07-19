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
package gator.lib.db.helpers;


import gator.lib.db.ADO;
import gator.lib.db.GappSQLStatement;
import gator.lib.logs.GappLogging;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *  This class contains the helpers needed for db actions.
 *  @author <a href="mailto:sbasurto@soft-gator.com">Sergio Basurto Juárez</a>
 */
public class GappDBHelper {       
    /**
     * ADO for the helper.
     */
    private ADO ado;
    
    /**
     * Log instance to do the log.
     */
    private final GappLogging logs = new GappLogging();       
    
    /**
     * Set configuration data for db as JSON.
     * 
     * @param fileName  The name of the file from which we will be reading the configuration.
     */
    public GappDBHelper(String fileName) {            
            ado = new ADO(fileName);
    }    
    
    /**
     * Set configuration data for db as JSON.
     * 
     * @param fileName  The name of the file from which we will be reading the configuration.
     */
    public void setDbWithFile(String fileName) {            
            ado = new ADO(fileName);
    }
    /**
     * Allows to execute a store procedure in any database described by
     * the db configuration, the db configuration could be from a file 
     * or a JSON string.
     * @param gappSQLStmt   The statement to be executed.     
     * @return The result of executing the store procedure as string.
     */
    public String executeStore (GappSQLStatement gappSQLStmt) {                
        String dbOut = ado.execStore(gappSQLStmt);
        logs.logIt(this.getClass().getCanonicalName(), "Executed: " + gappSQLStmt.getQueryStrForLog(), "security", "executeStore", 0);
        ado.close();
        return dbOut;
    }            
    /**
     * Allows to execute a store procedure in any database described by
     * the db configuration, the db configuration could be from a file 
     * or a JSON string.
     * @param gappSQLStmt   The statement to be executed.     
     * @return The result of executing the store procedure as string.
     */
    public synchronized String executeStoreSync (GappSQLStatement gappSQLStmt) {                
        String dbOut = ado.execStore(gappSQLStmt);
        logs.logIt(this.getClass().getCanonicalName(), "Executed: " + gappSQLStmt.getQueryStrForLog(), "security", "executeStore", 0);
        ado.close();
        return dbOut;
    }
    public ArrayList<HashMap<String, String>> execute (GappSQLStatement gappSQLStmt) {                
        ado.executePreparedStmt(gappSQLStmt);
        ArrayList<HashMap<String, String>> res = ado.getResult2();
        logs.logIt(this.getClass().getCanonicalName(), "Executed: " + gappSQLStmt.getQueryStrForLog(), "security", "execute", 0);
        ado.close();
        return res;
    }
}
