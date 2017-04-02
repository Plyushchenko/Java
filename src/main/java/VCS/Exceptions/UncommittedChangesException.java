package VCS.Exceptions;

public class UncommittedChangesException extends Exception {
    public UncommittedChangesException(String message) {
        super(message);
    }
}
