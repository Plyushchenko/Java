package VCS.Exceptions;

import org.jetbrains.annotations.NotNull;

/** Changes were not committed */
public class UncommittedChangesException extends Exception {

    public UncommittedChangesException(@NotNull String message) {
        super(message);
    }

}
