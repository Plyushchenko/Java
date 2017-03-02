import java.util.function.Supplier;

/**
 * class containing methods for creating instances of classes implementing Lazy interface
 */
public class LazyFactory {

    /**
     * method creating instance of SingleThreadLazy class
     * @param supplier which result is required
     * @param <T> is the type of computation result
     * @return instance of SingleThreadLazy class
     */
    public static <T> Lazy<T> createSingleThreadLazy(Supplier<T> supplier){
        return new SingleThreadLazy<>(supplier);
    }

    /**
     * method creating instance of MultiThreadLazy class
     * @param supplier which result is required
     * @param <T> is the type of computation result
     * @return instance of MultiThreadLazy class
     */
    public static <T> Lazy<T> createMultiThreadLazy(Supplier<T> supplier){
        return new MultiThreadLazy<>(supplier);
    }

    /**
     * method creating instance of LockFreeLazy class
     * @param supplier which result is required
     * @param <T> is the type of computation result
     * @return instance of LockFreeLazy class
     */
    public static <T> Lazy<T> createLockFreeLazy(Supplier<T> supplier){
        return new LockFreeLazy<>(supplier);
    }

}
