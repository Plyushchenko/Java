import java.util.function.Supplier;

class MultiThreadLazy<T> implements Lazy<T> {

    private Object notComputedYet = new Object();
    private Supplier<T> supplier;
    private volatile Object computationResult = notComputedYet;

    MultiThreadLazy(Supplier<T> supplier){
        this.supplier = supplier;
    }

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
