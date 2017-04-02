package VCS.Exceptions;

public class UncommitedChangesException extends Exception {
    public UncommitedChangesException(String message) {
        super(message);
    }
}
