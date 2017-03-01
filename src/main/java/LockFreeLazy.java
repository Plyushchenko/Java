import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.function.Supplier;

class LockFreeLazy<T> implements Lazy<T> {

    private Object notComputedYet = new Object();
    private Supplier<T> supplier;
    private volatile Object computationResult = notComputedYet;
    private static final AtomicReferenceFieldUpdater<LockFreeLazy, Object> computationResultUpdater =
            AtomicReferenceFieldUpdater.newUpdater(LockFreeLazy.class, Object.class, "computationResult");

    LockFreeLazy(Supplier<T> supplier){
        this.supplier = supplier;
    }

    @Override
    public T get() {
        if (computationResult == notComputedYet){
            Supplier<T> tmpSupplier = supplier;
            if (tmpSupplier != null) {
                computationResultUpdater.compareAndSet(this, notComputedYet, tmpSupplier.get());
                supplier = null;
            }
        }
        return (T)computationResult;
    }

}
