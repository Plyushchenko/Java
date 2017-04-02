package VCS.Commands;

import VCS.Data.FileSystem;
import VCS.Exceptions.IncorrectArgsException;
import VCS.Exceptions.UncommitedChangesException;
import VCS.Exceptions.UnstagedChangesException;

import java.io.IOException;

public abstract class Command {
    protected final FileSystem fileSystem;

    protected Command(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    public abstract void run() throws IncorrectArgsException, IOException, UnstagedChangesException,
            UncommitedChangesException;

    public abstract void checkArgsCorrectness() throws IncorrectArgsException, IOException;
}
