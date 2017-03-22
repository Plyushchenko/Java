public class LightExecutionException extends Exception {
    public LightExecutionException(Throwable t) {
        initCause(t);
    }

//  I don't know what to do here
}
