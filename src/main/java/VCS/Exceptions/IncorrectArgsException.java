package VCS.Exceptions;

import org.jetbrains.annotations.NotNull;

/** Incorrect args passed */
public class IncorrectArgsException extends Exception {

    public IncorrectArgsException(@NotNull String message) {
        super(message);
    }
}
