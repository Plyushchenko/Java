/**
 *Interface which provides lazy computations
 *
 * @param <T> is the type of result of computation
 */
public interface Lazy<T> {

    /**
     * Method which returns the result of computation
     *
     * @return the result of computation
     */
    T get();

}
