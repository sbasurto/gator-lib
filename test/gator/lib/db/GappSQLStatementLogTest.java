package gator.lib.db;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class GappSQLStatementLogTest {

    @AfterEach
    void clearProperty() {
        System.clearProperty("gator.db.logParameters");
    }

    @Test
    void logsValuesOnlyWhenExplicitlyEnabled() {
        GappSQLStatement statement = new GappSQLStatement();
        statement.setStoreProcedure("app_fn_admon_session");
        statement.addParam("{\"accion\":\"consulta\",\"debugLevel\":\"9\","
                + "\"sessionId\":\"bearer-token\",\"sessionObject\":\"serialized-secret\"}");

        String normal = statement.getQueryStrForLog();
        assertTrue(normal.contains("app_fn_admon_session"));
        assertTrue(normal.contains("parameters=1"));
        assertFalse(normal.contains("bearer-token"));

        System.setProperty("gator.db.logParameters", "true");
        String diagnostic = statement.getQueryStrForLog();
        assertTrue(diagnostic.contains("consulta"));
        assertTrue(diagnostic.contains("bearer-token"));
        assertTrue(diagnostic.contains("serialized-secret"));
    }
    @Test
    void scaleTokenIsNeverLoggedAndBindingIsUnchanged() throws Exception {
        GappSQLStatement statement = new GappSQLStatement();
        statement.setStoreProcedure("public.APP_FN_ADMON_BASCULA");
        String parameter = "{\"basculaToken\":\"scale-secret\"}";
        statement.addParam(parameter);
        System.setProperty("gator.db.logParameters", "true");
        assertFalse(statement.getQueryStrForLog().contains("scale-secret"));
        assertTrue(statement.getQueryStrForLog().contains("parameters=1"));
        assertTrue(statement.getQueryStr().contains("scale-secret"));
        var bound = new java.util.HashMap<Integer, String>();
        var callable = (java.sql.CallableStatement) java.lang.reflect.Proxy.newProxyInstance(
                getClass().getClassLoader(), new Class<?>[]{java.sql.CallableStatement.class},
                (proxy, method, args) -> {
                    if (method.getName().equals("setString")) bound.put((Integer) args[0], (String) args[1]);
                    return null;
                });
        statement.setParameters(callable);
        org.junit.jupiter.api.Assertions.assertEquals(parameter, bound.get(2));
    }
}
