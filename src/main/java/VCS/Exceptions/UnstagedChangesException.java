package VCS.Exceptions;

import org.jetbrains.annotations.NotNull;

/** Changes were not staged */
public class UnstagedChangesException extends Exception {

    public UnstagedChangesException(@NotNull String message) {
        super(message);
    }

}
