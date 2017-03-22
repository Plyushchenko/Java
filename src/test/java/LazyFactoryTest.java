import org.junit.Test;

import java.util.*;
import java.util.function.Supplier;

import static java.lang.Math.PI;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class LazyFactoryTest{

    private static final double EPS = 1e-6;

    private static void sleepIterations(int iterationNumber, int bound) {
        for (int i = 0; i < iterationNumber; i++){
            try {
                Thread.sleep(new Random().nextInt(bound));
            } catch (InterruptedException e) {
                //ignore
            }
        }
    }

    private Integer sleepThenReturnNull(){
        sleepIterations(10, 100);
        return null;
    }

    private Double sleepThenReturnPi() {
        sleepIterations(10, 100);
        return PI;
    }

    private long cubesSumFrom1ToN(long n){
        long res = 0;
        for (long i = 1; i <= n; i++){
            res += i * i * i;
        }
        return res;
    }

    @Test
    public void testSupplierReturnsNull(){
        assertNull(LazyFactory.createSingleThreadLazy(this::sleepThenReturnNull).get());
        assertNull(LazyFactory.createMultiThreadLazy(this::sleepThenReturnNull).get());
        assertNull(LazyFactory.createLockFreeLazy(this::sleepThenReturnNull).get());
    }

    @Test
    public void testSupplierReturnsPi(){
        assertEquals(LazyFactory.createSingleThreadLazy(this::sleepThenReturnPi).get(), Math.PI, EPS);
        assertEquals(LazyFactory.createMultiThreadLazy(this::sleepThenReturnPi).get(),  Math.PI, EPS);
        assertEquals(LazyFactory.createLockFreeLazy(this::sleepThenReturnPi).get(), Math.PI, EPS);
    }

    @Test
    public void testCorrectness(){
        for (int i = 0; i < 10; i++){
            long n = new Random().nextInt(50);
            long cubesSum = n * n * (n + 1) * (n + 1) / 4;
            assertEquals((long) LazyFactory.createSingleThreadLazy(() -> cubesSumFrom1ToN(n)).get(), cubesSum);
            assertEquals((long) LazyFactory.createMultiThreadLazy(() -> cubesSumFrom1ToN(n)).get(), cubesSum);
            assertEquals((long) LazyFactory.createLockFreeLazy(() -> cubesSumFrom1ToN(n)).get(), cubesSum);
        }
    }

    private class DateSupplier implements Supplier<Date> {

        @Override
        public Date get() {
            sleepIterations(10, 500);
            return new Date();
        }

    }

    private boolean checkMultiThreadLazyReturnsSameObject(Lazy<Date> lazy){
        List<Thread> threads = new ArrayList<>();
        Set<Date> dates = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            threads.add(new Thread(() -> {
                synchronized (dates){
                    dates.add(lazy.get());
                }
            }));
        }
        threads.forEach(Thread::start);
        for (Thread thread: threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                //ignore
            }
        }
        return dates.size() == 1;
    }

    @Test
    public void testReturnsSameObject(){
        DateSupplier dateSupplier = new DateSupplier();

        Lazy<Date> singleThreadLazy = LazyFactory.createSingleThreadLazy(dateSupplier);
        Set<Date> dates = new HashSet<>();
        for (int i = 0; i < 100; i++){
            dates.add(singleThreadLazy.get());
        }
        assertEquals(dates.size(), 1);

        assertTrue(checkMultiThreadLazyReturnsSameObject(LazyFactory.createMultiThreadLazy(dateSupplier)));
        assertTrue(checkMultiThreadLazyReturnsSameObject(LazyFactory.createLockFreeLazy(dateSupplier)));
    }

    private class LazinessChecker implements Supplier<Double>{
        @Override
        public Double get() {
            while(true){
                sleepIterations(10, 100);
            }
        }
    }

    @Test
    public void testLaziness(){
        LazyFactory.createSingleThreadLazy(new LazinessChecker());
        LazyFactory.createMultiThreadLazy(new LazinessChecker());
        LazyFactory.createLockFreeLazy(new LazinessChecker());
    }

}