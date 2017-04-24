package VCS.Commands;

import VCS.Data.FileSystem;
import VCS.Exceptions.IncorrectArgsException;
import VCS.Exceptions.UncommittedChangesException;
import VCS.Exceptions.UnstagedChangesException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class ResetCommand extends Command {

    protected ResetCommand(@NotNull FileSystem fileSystem) {
        super(fileSystem);
    }

    @Override
    public void run() throws IncorrectArgsException, IOException, UnstagedChangesException,
            UncommittedChangesException {

    }

    @Override
    protected void checkArgsCorrectness() throws IncorrectArgsException, IOException {

    }
}
