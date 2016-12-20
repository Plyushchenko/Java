import java.util.function.Function;
import java.util.function.Supplier;

public interface LightFuture<T> {

    public boolean isReady();

    public T get() throws LightExecutionException, InterruptedException;

    public <Y> LightFuture<Y> thenApply(Function<T, Y> function);
}
