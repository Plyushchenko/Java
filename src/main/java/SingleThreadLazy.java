import java.util.function.Supplier;

class SingleThreadLazy<T> implements Lazy<T> {

    private Object notComputedYet = new Object();
    private Supplier<T> supplier;
    private volatile Object computationResult = notComputedYet;

    SingleThreadLazy(Supplier<T> supplier){
        this.supplier = supplier;
    }

    @Override
    public T get() {
        if (computationResult == notComputedYet){
            computationResult = supplier.get();
            supplier = null;
        }
        return (T)computationResult;
    }

}
