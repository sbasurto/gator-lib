package gator.lib.db;

import static org.junit.jupiter.api.Assertions.*;

import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import javax.naming.Context;
import javax.naming.spi.InitialContextFactory;
import javax.sql.DataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Recording only the JDBC boundary: transaction, parsing and validation are real. */
public class ADOJsonTransactionTest {
    private static RecordingJdbc jdbc;
    private String previousFactory;

    @BeforeEach void installDataSource() {
        previousFactory = System.getProperty(Context.INITIAL_CONTEXT_FACTORY);
        System.setProperty(Context.INITIAL_CONTEXT_FACTORY, Factory.class.getName());
        jdbc = new RecordingJdbc();
    }

    @AfterEach void restoreNaming() {
        if (previousFactory == null) System.clearProperty(Context.INITIAL_CONTEXT_FACTORY);
        else System.setProperty(Context.INITIAL_CONTEXT_FACTORY, previousFactory);
    }

    @Test void oneConnectionValidatesBeforeCommitAndRestoresPoolStateAfterCommit() throws Exception {
        String result = database().executeJsonTransaction(statement(), 5000, 20000, json -> {
            assertEquals("{\"code\":\"0\"}", json);
            assertFalse(jdbc.events.contains("commit"));
            jdbc.events.add("validate");
        });
        assertEquals("{\"code\":\"0\"}", result);
        assertEquals(1, jdbc.acquisitions);
        assertEquals(1, jdbc.prepares);
        assertEquals(List.of("literal-'\\body"), jdbc.parameters);
        assertOrder("auto:false", "isolation:2", "readonly:false",
                "sql:SET LOCAL lock_timeout = '5000ms'", "sql:SET LOCAL statement_timeout = '20000ms'",
                "execute", "validate", "commit", "isolation:8", "readonly:true", "auto:true", "close");
        assertFalse(jdbc.events.contains("rollback"));
        assertFalse(jdbc.events.contains("abort"));
    }

    @Test void executionFailureRollsBackBeforeResetAndDoesNotRetry() {
        jdbc.failures.add("execute");
        SQLException failure = assertThrows(SQLException.class,
                () -> database().executeJsonTransaction(statement(), 5000, 20000));
        assertSame(jdbc.executionFailure, failure);
        assertEquals(1, jdbc.acquisitions);
        assertEquals(1, jdbc.prepares);
        assertFalse(jdbc.events.contains("commit"));
        assertOrder("execute", "rollback", "isolation:8", "auto:true", "close");
    }

    @Test void malformedNonObjectNullOversizedOrNonScalarResultsNeverCommit() {
        for (String value : new String[] {null, "", "null", "[]", "{bad}", "{} trailing", "{'a':1}",
                "{\"a\":\"" + "é".repeat(524288) + "\"}",
                "{\"a\":\"" + "x".repeat(1048576) + "\"}"}) {
            jdbc = new RecordingJdbc();
            jdbc.json = value;
            assertThrows(SQLException.class, () -> database().executeJsonTransaction(statement(), 1, 2));
            assertFalse(jdbc.events.contains("commit"));
            assertOrder("rollback", "auto:true", "close");
        }
        for (int[] shape : new int[][] {{0, 1}, {2, 1}, {1, 2}}) {
            jdbc = new RecordingJdbc();
            jdbc.rows = shape[0]; jdbc.columns = shape[1];
            assertThrows(SQLException.class, () -> database().executeJsonTransaction(statement(), 1, 2));
            assertFalse(jdbc.events.contains("commit"));
            assertOrder("rollback", "close");
        }
    }

    @Test void callerSemanticRejectionRollsBackBeforeCommit() {
        SQLException rejected = new SQLException("Unsafe canonical response");
        SQLException actual = assertThrows(SQLException.class,
                () -> database().executeJsonTransaction(statement(), 1, 2, json -> { throw rejected; }));
        assertSame(rejected, actual);
        assertFalse(jdbc.events.contains("commit"));
        assertOrder("rollback", "auto:true", "close");
    }

    @Test void duplicateObjectKeysAreRejectedBeforeCallerValidationAndCommit() {
        for (String value : List.of("{\"code\":\"77\",\"code\":\"0\"}",
                "{\"responses\":[{\"code\":\"77\",\"code\":\"0\"}]}",
                "{\"code\":\"77\",\"\\u0063ode\":\"0\"}")) {
            jdbc = new RecordingJdbc();
            jdbc.json = value;
            assertThrows(SQLException.class, () -> database().executeJsonTransaction(statement(), 1, 2,
                    json -> fail("Duplicate keys must not reach the caller validator")));
            assertFalse(jdbc.events.contains("commit"));
            assertOrder("rollback", "auto:true", "close");
        }
    }

    @Test void uncheckedValidatorFailureAlsoRollsBack() {
        IllegalStateException rejected = new IllegalStateException("Bad response semantics");
        assertSame(rejected, assertThrows(IllegalStateException.class,
                () -> database().executeJsonTransaction(statement(), 1, 2, json -> { throw rejected; })));
        assertOrder("rollback", "auto:true", "close");
        assertFalse(jdbc.events.contains("commit"));
    }

    @Test void parameterResultAndTimeoutFailuresCloseResourcesAndRollBack() {
        for (String point : List.of("bind", "result.close", "sql:SET LOCAL statement_timeout = '2ms'")) {
            jdbc = new RecordingJdbc();
            jdbc.failures.add(point);
            assertThrows(SQLException.class, () -> database().executeJsonTransaction(statement(), 1, 2));
            assertFalse(jdbc.events.contains("commit"));
            assertOrder(point, "rollback", "auto:true", "close");
            if (!point.startsWith("sql:")) assertTrue(jdbc.events.contains("prepared.close"));
        }
    }

    @Test void failedAcquisitionDoesNotFallbackOrRetry() {
        jdbc.failures.add("acquire");
        assertThrows(SQLException.class, () -> database().executeJsonTransaction(statement(), 1, 2));
        assertEquals(1, jdbc.acquisitions);
        assertEquals(0, jdbc.prepares);
        assertEquals(List.of("acquire"), jdbc.events);
    }

    @Test void abortAndCloseFailuresRemainSuppressedBehindTheOriginalExecutionError() {
        jdbc.failures.addAll(Set.of("execute", "rollback", "abort", "close"));
        SQLException actual = assertThrows(SQLException.class,
                () -> database().executeJsonTransaction(statement(), 1, 2));
        assertSame(jdbc.executionFailure, actual);
        assertEquals(2, actual.getSuppressed().length);
        assertOrder("rollback", "abort", "close");
        assertFalse(jdbc.events.contains("auto:true"));
    }

    @Test void commitFailureRemainsUncertainAndDiscardsEvenIfRollbackSucceeds() {
        jdbc.failures.add("commit");
        SQLException actual = assertThrows(SQLException.class,
                () -> database().executeJsonTransaction(statement(), 1, 2));
        assertSame(jdbc.commitFailure, actual);
        assertOrder("commit", "rollback", "abort", "close");
        assertFalse(jdbc.events.contains("auto:true"));
        assertFalse(jdbc.events.contains("isolation:8"));
        assertEquals(1, jdbc.prepares);
    }

    @Test void rollbackFailureDiscardsWithoutImplicitCommitAndKeepsPrimaryError() {
        jdbc.failures.addAll(Set.of("execute", "rollback"));
        SQLException actual = assertThrows(SQLException.class,
                () -> database().executeJsonTransaction(statement(), 1, 2));
        assertSame(jdbc.executionFailure, actual);
        assertEquals(1, actual.getSuppressed().length);
        assertOrder("rollback", "abort", "close");
        assertFalse(jdbc.events.contains("auto:true"));
        assertFalse(jdbc.events.contains("isolation:8"));
    }

    @Test void resetFailureDiscardsAndNeverEnablesAutocommitAfterFailedIsolationReset() {
        jdbc.failures.add("isolation:8");
        assertThrows(SQLException.class, () -> database().executeJsonTransaction(statement(), 1, 2));
        assertOrder("commit", "isolation:8", "abort", "close");
        assertFalse(jdbc.events.contains("auto:true"));
    }

    @Test void closeFailureAfterCommitIsNotReportedAsConfirmedFailureOrRetried() {
        jdbc.failures.add("close");
        assertThrows(SQLException.class, () -> database().executeJsonTransaction(statement(), 1, 2));
        assertOrder("commit", "close", "abort");
        assertEquals(1, jdbc.prepares);
        assertFalse(jdbc.events.contains("rollback"));
    }

    @Test void callerOwnedTransactionIsNotCommittedRolledBackClosedOrReplaced() throws Exception {
        ADO database = database();
        jdbc.autoCommit = false;
        Field connection = ADO.class.getDeclaredField("connection");
        connection.setAccessible(true);
        connection.set(database, jdbc.connection());
        assertThrows(SQLException.class, () -> database.executeJsonTransaction(statement(), 1, 2));
        assertEquals(0, jdbc.acquisitions);
        assertEquals(List.of(), jdbc.events);
    }

    @Test void freshPoolConnectionWithOpenTransactionIsDiscardedWithoutResetOrExecution() {
        jdbc.autoCommit = false;
        assertThrows(SQLException.class, () -> database().executeJsonTransaction(statement(), 1, 2));
        assertEquals(0, jdbc.prepares);
        assertOrder("abort", "close");
        assertFalse(jdbc.events.contains("auto:true"));
        assertFalse(jdbc.events.contains("rollback"));
    }

    @Test void invalidTimeoutsFailBeforeBorrowing() {
        assertThrows(IllegalArgumentException.class, () -> database().executeJsonTransaction(statement(), 0, 20));
        assertThrows(IllegalArgumentException.class, () -> database().executeJsonTransaction(statement(), 5, -1));
        assertEquals(0, jdbc.acquisitions);
    }

    private static ADO database() { return new ADO("localhost", "5432", "rf-test", "test", "", "pgsql"); }
    private static GappSQLStatement statement() {
        GappSQLStatement statement = new GappSQLStatement();
        statement.setQuery("select wms_fn_mobile_receipt(?)");
        statement.addParam("literal-'\\body");
        return statement;
    }
    private static void assertOrder(String... events) {
        int prior = -1;
        for (String event : events) {
            int current = jdbc.events.indexOf(event);
            assertTrue(current > prior, event + " in " + jdbc.events);
            prior = current;
        }
    }

    public static final class Factory implements InitialContextFactory {
        @Override public Context getInitialContext(Hashtable<?, ?> environment) {
            return proxy(Context.class, (method, args) -> {
                if (method.equals("lookup")) {
                    assertEquals("java:/comp/env/rf-test", args[0]);
                    return proxy(DataSource.class, (call, values) -> {
                        if (call.equals("getConnection")) {
                            jdbc.acquisitions++;
                            jdbc.event("acquire");
                            return jdbc.connection();
                        }
                        return null;
                    });
                }
                return null;
            });
        }
    }

    private interface Call { Object invoke(String method, Object[] args) throws Throwable; }
    @SuppressWarnings("unchecked") private static <T> T proxy(Class<T> type, Call call) {
        return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[] {type},
                (object, method, args) -> call.invoke(method.getName(), args));
    }

    private static final class RecordingJdbc {
        final List<String> events = new ArrayList<>();
        final List<String> parameters = new ArrayList<>();
        final Set<String> failures = new HashSet<>();
        final SQLException executionFailure = new SQLException("execution failed", "55P03");
        final SQLException commitFailure = new SQLException("commit outcome unknown", "08006");
        boolean autoCommit = true;
        int acquisitions, prepares, rows = 1, columns = 1;
        String json = "{\"code\":\"0\"}";

        void event(String name) throws SQLException {
            events.add(name);
            if (failures.contains(name)) {
                if (name.equals("execute")) throw executionFailure;
                if (name.equals("commit")) throw commitFailure;
                throw new SQLException(name + " failed");
            }
        }
        Connection connection() {
            return proxy(Connection.class, (method, args) -> {
                switch (method) {
                    case "isClosed": return false;
                    case "getAutoCommit": return autoCommit;
                    case "getTransactionIsolation": return Connection.TRANSACTION_SERIALIZABLE;
                    case "isReadOnly": return true;
                    case "setAutoCommit": event("auto:" + args[0]); autoCommit = (boolean) args[0]; return null;
                    case "setTransactionIsolation": event("isolation:" + args[0]); return null;
                    case "setReadOnly": event("readonly:" + args[0]); return null;
                    case "createStatement": return proxy(Statement.class, (call, values) -> {
                        if (call.equals("execute")) { event("sql:" + values[0]); return false; }
                        if (call.equals("close")) event("settings.close");
                        return null;
                    });
                    case "prepareStatement":
                        prepares++;
                        assertEquals("select wms_fn_mobile_receipt(?)", args[0]);
                        return proxy(PreparedStatement.class, (call, values) -> {
                            if (call.equals("setString")) { event("bind"); parameters.add((String) values[1]); return null; }
                            if (call.equals("executeQuery")) { event("execute"); return resultSet(); }
                            if (call.equals("close")) event("prepared.close");
                            return null;
                        });
                    case "commit", "rollback", "abort", "close": event(method); return null;
                    default: throw new AssertionError("Unexpected JDBC call: " + method);
                }
            });
        }
        ResultSet resultSet() {
            int[] position = {0};
            return proxy(ResultSet.class, (method, args) -> {
                return switch (method) {
                    case "next" -> ++position[0] <= rows;
                    case "getMetaData" -> proxy(ResultSetMetaData.class, (call, values) -> columns);
                    case "getCharacterStream" -> json == null ? null : new StringReader(json);
                    case "close" -> { event("result.close"); yield null; }
                    default -> throw new AssertionError("Unexpected ResultSet call: " + method);
                };
            });
        }
    }
}
