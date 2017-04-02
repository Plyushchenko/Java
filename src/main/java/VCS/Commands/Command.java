package VCS.Commands;

import VCS.Data.FileSystem;
import VCS.Exceptions.IncorrectArgsException;
import VCS.Exceptions.UncommittedChangesException;
import VCS.Exceptions.UnstagedChangesException;

import java.io.IOException;

public abstract class Command {
    protected final FileSystem fileSystem;

    protected Command(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    public abstract void run() throws IncorrectArgsException, IOException, UnstagedChangesException,
            UncommittedChangesException;

    public abstract void checkArgsCorrectness() throws IncorrectArgsException, IOException;
}
