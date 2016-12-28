import java.util.function.Function;
import java.util.function.Supplier;

public interface LightFuture<T> {

    boolean isReady();

    T get() throws LightExecutionException, InterruptedException;

    <Y> LightFuture<Y> thenApply(Function<? super T, Y> function);
}
