package gator.lib.db;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class TestADOReplicationPool {
    @Test
    void boundsTenThousandTasksWithBackpressureAndExpires() throws Exception {
        ThreadPoolExecutor pool = replicationPool();
        assertEquals(0, pool.getCorePoolSize());
        assertEquals(60, pool.getMaximumPoolSize());
        assertEquals(60, pool.getKeepAliveTime(TimeUnit.SECONDS));
        assertTrue(pool.getRejectedExecutionHandler() instanceof ThreadPoolExecutor.CallerRunsPolicy);

        CountDownLatch started = new CountDownLatch(60);
        CountDownLatch release = new CountDownLatch(1);
        AtomicBoolean daemonWorkers = new AtomicBoolean(true);
        for(int i = 0; i < 60; i++) {
            pool.execute(() -> {
                daemonWorkers.compareAndSet(true,
                        Thread.currentThread().isDaemon()
                        && Thread.currentThread().getName().equals("gator-db-replication"));
                started.countDown();
                await(release);
            });
        }
        assertTrue(started.await(5, TimeUnit.SECONDS));

        Thread caller = Thread.currentThread();
        AtomicInteger callerRuns = new AtomicInteger();
        for(int i = 60; i < 10_000; i++) {
            pool.execute(() -> {
                if(Thread.currentThread() == caller) callerRuns.incrementAndGet();
            });
        }
        assertEquals(9_940, callerRuns.get());
        assertEquals(60, pool.getLargestPoolSize());
        assertTrue(daemonWorkers.get());

        release.countDown();
        awaitIdle(pool);
        pool.setKeepAliveTime(10, TimeUnit.MILLISECONDS);
        try {
            awaitExpired(pool);
        } finally {
            pool.setKeepAliveTime(60, TimeUnit.SECONDS);
        }
    }

    @Test
    void closeOnlyClosesJdbcConnection() throws Exception {
        ADO database = new ADO("localhost", "5432", "test", "test", "test", "pgsql");
        database.close();
        assertFalse(replicationPool().isShutdown());
    }

    private static ThreadPoolExecutor replicationPool() throws Exception {
        Field field = ADO.class.getDeclaredField("REPLICATION_POOL");
        field.setAccessible(true);
        return (ThreadPoolExecutor) field.get(null);
    }

    private static void await(CountDownLatch latch) {
        try {
            latch.await();
        } catch(InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AssertionError(e);
        }
    }

    private static void awaitIdle(ThreadPoolExecutor pool) throws InterruptedException {
        long timeout = System.nanoTime() + TimeUnit.SECONDS.toNanos(5);
        while(pool.getActiveCount() != 0 && System.nanoTime() < timeout) Thread.sleep(10);
        assertEquals(0, pool.getActiveCount());
    }

    private static void awaitExpired(ThreadPoolExecutor pool) throws InterruptedException {
        long timeout = System.nanoTime() + TimeUnit.SECONDS.toNanos(5);
        while(pool.getPoolSize() != 0 && System.nanoTime() < timeout) Thread.sleep(10);
        assertEquals(0, pool.getPoolSize());
    }
}
