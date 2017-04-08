package VCS.Commands;

import VCS.Data.FileSystem;
import VCS.Exceptions.IncorrectArgsException;
import VCS.Exceptions.UncommittedChangesException;
import VCS.Exceptions.UnstagedChangesException;

import java.io.IOException;

/**
 * Git command
 */
public abstract class Command {

    protected final FileSystem fileSystem;
    protected Command(FileSystem fileSystem) {
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

    public abstract void checkArgsCorrectness() throws IncorrectArgsException, IOException;
}
