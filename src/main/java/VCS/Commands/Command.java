package VCS.Commands;

import VCS.Data.FileSystem;
import VCS.Exceptions.IncorrectArgsException;
import VCS.Exceptions.UncommittedChangesException;
import VCS.Exceptions.UnstagedChangesException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * Git command
 */
public abstract class Command {

    @NotNull protected final FileSystem fileSystem;

    protected Command(@NotNull FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    /**
     * Run the command
     * @throws IncorrectArgsException Incorrect args passed
     * @throws IOException Unknown IO problem
     * @throws UnstagedChangesException Changes were not staged
     * @throws UncommittedChangesException Changes were not committed
     */
    public abstract void run() throws IncorrectArgsException, IOException, UnstagedChangesException,
            UncommittedChangesException;

    protected abstract void checkArgsCorrectness() throws IncorrectArgsException, IOException;

}
