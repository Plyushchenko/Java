import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;


public class ThreadPoolImplTest {

    @Test
    public void testOneTask() throws LightExecutionException, InterruptedException {
        final LightFuture<Integer> five = new ThreadPoolImpl(25).submit(() -> 5);
        assertEquals((Integer)5, five.get());
    }

    @Test
    public void testOneTaskOneThread() throws LightExecutionException, InterruptedException {
        final LightFuture<Integer> five = new ThreadPoolImpl(1).submit(() -> 5);
        assertEquals((Integer)5, five.get());
    }

    @Test
    public void testTwoTasks() throws LightExecutionException, InterruptedException {
        ThreadPoolImpl tpi = new ThreadPoolImpl(1);
        final LightFuture<Integer> sum10_000_000 = tpi.submit(() -> {
            int sum = 0;
            int x = 0;
            while (sum < 10_000_000){
                sum += x++;
            }
            return x;
        });

        final LightFuture<Integer> sum12345678 = tpi.submit(() -> {
            int sum = 0;
            int x = 0;
            while (sum < 12345678){
                sum += x++;
            }
            return x;
        });
        assertEquals(4473, (int)sum10_000_000.get());
        assertEquals(4970, (int)sum12345678.get());
    }

    @Test (expected = LightExecutionException.class)
    public void testException() throws LightExecutionException, InterruptedException {
        LightFuture exceptionCause = new ThreadPoolImpl(10).submit(() -> {
            throw new UnsupportedOperationException();
        });
        exceptionCause.get();
    }

    @Test
    public void thenApplyTest() throws LightExecutionException, InterruptedException {
        ThreadPoolImpl pool = new ThreadPoolImpl(1);
        LightFuture<Integer> sum = pool.submit(() -> 3 + 7);
        LightFuture<Integer> sqr = sum.thenApply((Integer x) -> x * x);
        assertEquals(100, (int)sqr.get());
    }

    @Test
    public void testNThreadsRunning() throws LightExecutionException, InterruptedException {
        final int n = 5;

        ThreadPoolImpl pool = new ThreadPoolImpl(n);
        HashSet<Long> ids = new HashSet<>();

        for (int i = 0; i < 300; i++) {
            ((LightFuture)pool.submit(() -> {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
  //              }
                ids.add(Thread.currentThread().getId());
                return Thread.currentThread().getId();
            })).get();
        }
        assertEquals(n, ids.size());
    }

    @Test (expected = LightExecutionException.class)
    public void testShutdown() throws LightExecutionException, InterruptedException {
        ThreadPoolImpl tpi = new ThreadPoolImpl(10);
        tpi.shutdown();
        ((LightFuture)tpi.submit(() -> 123)).get();
    }
}