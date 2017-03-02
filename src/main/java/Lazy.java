/**
 *Interface providing lazy computations
 *
 * @param <T> is the type computation result
 */
public interface Lazy<T> {

    /**
     * Method returning the computation result
     *
     * @return the computation result
     */
    T get();

}
