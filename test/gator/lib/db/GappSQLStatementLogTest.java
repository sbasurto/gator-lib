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
}
