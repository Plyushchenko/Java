import java.util.function.Supplier;

/**
 * class provides multithreaded lazy computation which can be run only once
 *
 * @param <T> is the type of computation result
 */

class MultiThreadLazy<T> implements Lazy<T> {

    private static final Object notComputedYet = new Object();
    private Supplier<T> supplier;
    private volatile Object computationResult = notComputedYet;

    MultiThreadLazy(Supplier<T> supplier){
        this.supplier = supplier;
    }

    /**
     * method returning the result of computation
     * if computation hasn't been run then
     * lock and get computation result from supplier
     * then supplier is assigned null (just to let GC collect it)
     *
     * @return the result of computation
     */

    @Override
    public T get() {
        if (computationResult != notComputedYet) {
            return (T)computationResult;
        }

        synchronized (this) {
            if (computationResult == notComputedYet) {
                computationResult = supplier.get();
                supplier = null;
            }

            return (T)computationResult;
        }
    }

}
