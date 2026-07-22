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
package gator.lib.db;

import gator.lib.db.dbms.PostgresDB;
import gator.lib.logs.GappLogging;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 *
 * @author <a href="mailto:sbasurto@soft-gator.com">Sergio Basurto Juárez</a>
 */
public class GappSQLStatement {
        /**
         * Query to be executed.
         */
        private String query;
        
        /**
         * Store procedure name.
         */
        private String storeProcedure = "";
        
        /**
         * Store db kind.
         */
        private String dbKind = "pgsql";
        
        /**
         * Parameters for query or store, in former case in parameters.
         */
        private final ArrayList<String> inParams = new ArrayList<>();
        
        /**
         * Out parameters for store execution.
         */
        private final ArrayList<String> outParams = new ArrayList<>();
        
        /**
         * In type parameters for store execution.
         */
        private final ArrayList<Integer> paramTypes = new ArrayList<>();
        
        /**
	 * Log instance to do the log.
	 */
	private final GappLogging logs = new GappLogging();
        
        /**
         * Stores no replicable store procedures.
         */
	private final ArrayList<String> storeNoRep;
        
        /**
         * Stores no replicable store procedures.
         */
	private final ArrayList<String> storeNotLike;
                
        /**
         * Constructor for GappSQLStatement.
         */
        public GappSQLStatement() {
                this.storeNoRep = new ArrayList<>();
                this.storeNotLike = new ArrayList<>();
                this.storeNoRep.add("app_fn_get_board");
                this.storeNoRep.add("app_fn_get_submenus2_1");
                this.storeNoRep.add("app_fn_get_submenus2_1");
                this.storeNotLike.add("fn_scr_get");
                this.storeNotLike.add("mail_fn_");
                this.storeNoRep.add("sec_fn_get_usuario");
                this.storeNoRep.add("app_fn_get_usuario_info");
                this.storeNoRep.add("app_fn_get_menus");
                this.storeNoRep.add("sec_fn_get_tipos_grupo");
                this.storeNoRep.add("app_fn_get_constantes");
                this.storeNoRep.add("app_fn_get_impresoras");
                this.storeNoRep.add("app_fn_get_reporte");
                this.storeNoRep.add("app_fn_get_pool");
                this.storeNoRep.add("app_fn_admon_session");
        }
        
        /**
         * Allows to set query for this statement.
         * 
         * @param _query The query to be set.
         */
        public void setQuery(String _query) {
                this.query = _query;
        }
        
        /**
         * Allows to set query for this statement.
         * 
         * @return Query of this statement as it is.
         */
        public String getQuery() {
                return !this.getStoreProcedure().isBlank()?this.getStoreForExecution():this.query;
        }
        
        /**
         * Allows to add parameters.
         * 
         * @param param Parameter to be add as a string.
         */
        public void addParam(String param) {
                this.inParams.add(param);                
        }
        
        /**
         * Allows to add out parameters.
         * 
         * @param param Out parameter to be add as a string.
         */
        public void addOutParam(String param) {
                this.outParams.add(param);
                this.paramTypes.add(PostgresDB.PG_VARCHAR);
        }
        
        /**
         * Retrieve the in parameters for this statement.
         * 
         * @return The array list of in parameters.
         */
        public ArrayList<String> getInParams() {
                return this.inParams;
        }
        
        /**
         * Retrieve the out parameters for this statement.
         * 
         * @return The array list of out parameters.
         */
        public ArrayList<String> getOutParams() {
                return this.outParams;
        }
        
        /**
         * Retrieve the out parameters types for this statement.
         * 
         * @return The array list of out parameters types.
         */
        public ArrayList<Integer> getOutParamsTypes() {
                return this.paramTypes;
        }
        
        /**
         * Allows to register the out parameters for the SQL statement.
         * @param statement The statement where the parameters will be registered.
         * @return The statement with parameters registered.
         * @throws SQLException Throw an SQL exception.
         */
        public CallableStatement registerOutParams(CallableStatement statement) throws SQLException {
                if(this.outParams.isEmpty()) this.addOutParam("");
                int numOfParams = 1;
                for(int type: this.paramTypes) {
                        statement.registerOutParameter(numOfParams, type);
                        numOfParams++;
                }                
                return statement;
        }
        
        /**
         * Allows to set parameters for query execution.
         * @param statement The statement where the parameters will be settled.
         * @return The statement with parameters ready.
         * @throws SQLException  Throw an SQL exception.
         */
        public CallableStatement setParameters(CallableStatement statement) throws SQLException {
                // The parameters start at 2 because in position 1 is the outparameter.
                int numOfParams = 2;
                logs.logIt("setParameters", this.inParams.size() + "",  "GappSQLStatement", "GappSQLStatement", 0);
                for(String param: this.inParams) {
                        statement.setString(numOfParams, param);
                        numOfParams++;
                }
                return statement;
        }
        
        /**
         * Retrieve the execution string as an SQL statement.
         * @return String representing the execution as run in database.
         */
        public String getStoreStr() {
                return "select " + this.getStoreProcedure() + "('" + String.join("','", this.inParams) + "');";
        }
        
        /**
         * Retrieve the execution string as an SQL statement.
         * @return String representing the execution as run in database.
         */
        public String getStore() {
                String paramsForExec = "";
                for(String element: this.inParams) {
                    paramsForExec += "?,";
                }
                paramsForExec = paramsForExec.replaceAll(",$", "");
                return this.getStoreProcedure() + "(" + paramsForExec + ")";
        }
        /**
         * Allows to set store procedure for this statement.
         * 
         * @param _storeProcedure The store procedure.
         */
        public void setStoreProcedure(String _storeProcedure) {
                this.storeProcedure = _storeProcedure;
        }
        
        /**
         * Allows to set store procedure for this statement.
         * 
         * @return Store procedure.
         */
        public String getStoreProcedure() {
                return this.storeProcedure;
        }
        
        /**
         * Allows to set store procedure for this statement.
         * 
         * @return Store procedure.
         */
        public String getStoreForExecution() {
                return this.getExecStr().replaceAll("::store_name::", this.getStore());
        }
        /**
         * Get an store procedure execution string for any kind of database.         
         * @return The execution string for the database kind.
         */
        private String getExecStr(){
            return switch (this.getDbKind()) {
                case "oracle" -> "BEGIN ? := ::store_name::; END;";
                case "pgsql" -> "{ ?= call ::store_name:: }";
                default -> "{ ?= call ::store_name:: }";
            };
        }
        /**
         * Allows to set dbKind for this statement.
         * 
         * @param kind The database kind to be set.
         */
        public void setDbKind(String kind) {
                this.dbKind = kind;
        }
        
        /**
         * Allows to set query for this statement.
         * 
         * @return The database kind.
         */
        public String getDbKind() {
                return this.dbKind;
        }        
        /**
         * Check if query is replicable.
         * @return Flag telling if is a replicable query.
         */
        public boolean isReplicable() {
                Pattern isInsert = Pattern.compile("(?i)(insert|delete|update|create)");
		Pattern isFile = Pattern.compile("^(?i)\\\\i");
                return isInsert.matcher(this.getQuery()).find() || isFile.matcher(query).find();                        
        }  
        /**
         * Check if store procedure is replicable.
         * @return Flag telling if is a replicable store procedure.
         */
        public boolean isStoreReplicable() {                
                return !this.storeNoRep.contains(this.getStoreProcedure()) && !this.storeProcedureLike();
        }
        /**
         * Check if store procedure is like the ones not replicable.
         * @return Flag telling if the store procedure is like the ones not replicable. 
         */
        private boolean storeProcedureLike() {
                for(String notAllowed: this.storeNotLike) {
                        if(this.getStoreProcedure().contains(notAllowed)) {
                                return true;
                        }
                }
                return false;
        }
        /**
         * Allows to get a prepared statement for execution.
         * @param conn An SQL connection to get the prepare statement.
         * @return The prepare statement ready to use.
         * @throws SQLException Throw an SQL exception.
         */
        public PreparedStatement getPrepStmt(Connection conn) throws SQLException {
                PreparedStatement prep = conn.prepareStatement(this.getQuery(), ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                for(int i = 0;i < this.inParams.size(); i++) {
                        prep.setString(i + 1, this.inParams.get(i));
                }
                return prep;
        }
        
        /**
         * Allows to get a prepared statement for execution.
         * @param conn An SQL connection to get the prepare statement.
         * @return The prepare statement ready to use.
         * @throws SQLException Throw an SQL exception.
         */
        public CallableStatement getCallableStmt(Connection conn) throws SQLException {
                CallableStatement stmtCallable = conn.prepareCall(this.getQuery());                
                return stmtCallable;
        }
        /**
         * Allows to set query string for this statement.
         * 
         * @return Query of this statement as it is.
         */
        public String getQueryStr() {
                return !this.getStoreProcedure().isBlank()?this.getStoreStr():this.getQueryForShow();
        }

        /**
         * Returns the query shape by default or the complete statement when explicitly enabled.
         * Enable parameter diagnostics with {@code -Dgator.db.logParameters=true}.
         * @return Safe statement description for logs.
         */
        public String getQueryStrForLog() {
                return Boolean.getBoolean("gator.db.logParameters")
                        ? getQueryStr()
                        : this.getQuery() + " [parameters=" + this.inParams.size() + "]";
        }
        /**
         * Returns the query ready for printing.
         * @return 
         */
        private String getQueryForShow() {
                String queryTmp = this.query;
                for(String param: this.inParams) {
                        queryTmp = queryTmp.replaceFirst("\\?", "'" + param + "'");
                }
                return queryTmp + ";";
        }
}
