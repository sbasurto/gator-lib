package gator.lib.db;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class ADOExecutionPoolTest {
    @Test
    void closeStopsReplicationExecutor() {
        ADO database = new ADO("localhost", "5432", "test", "test", "test", "pgsql");
        database.close();
        assertTrue(database.executionPool.isShutdown());
    }
}
