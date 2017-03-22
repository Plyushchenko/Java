import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.function.Supplier;

/**
 * class provides lock-free lazy computation which can be run multiple times
 * @param <T> is the type of computation result
 */
class LockFreeLazy<T> implements Lazy<T> {

    private static final Object NOT_COMPUTED_YET = new Object();
    private Supplier<T> supplier;
    private volatile T computationResult = (T)NOT_COMPUTED_YET;
    private static final AtomicReferenceFieldUpdater<LockFreeLazy, Object> computationResultUpdater =
            AtomicReferenceFieldUpdater.newUpdater(LockFreeLazy.class, Object.class, "computationResult");

    LockFreeLazy(Supplier<T> supplier){
        this.supplier = supplier;
    }

    /**
     * method returning the result of computation
     * if computation hasn't been run then
     * create a copy of supplier (to avoid race during supplier.get())
     * and update the computation result in a lock-free way
     * then supplier is assigned null (just to let GC collect it)
     * @return the result of computation
     */
    @Override
    public T get() {
        if (computationResult == NOT_COMPUTED_YET){
            Supplier<T> tmpSupplier = supplier;
            if (tmpSupplier != null) {
                computationResultUpdater.compareAndSet(this, NOT_COMPUTED_YET, tmpSupplier.get());
                supplier = null;
            }
        }
        return computationResult;
    }

}
