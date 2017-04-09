package VCS.Commands;

import VCS.Data.FileSystem;
import VCS.Exceptions.IncorrectArgsException;
import VCS.Exceptions.UncommittedChangesException;
import VCS.Exceptions.UnstagedChangesException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RmCommand extends Command{

    private final Path pathToFile;
    public RmCommand(@NotNull FileSystem fileSystem, String fileToRm) {
        super(fileSystem);
        pathToFile = Paths.get(fileToRm).toAbsolutePath();
    }

    @Override
    public void run() throws IncorrectArgsException, IOException, UnstagedChangesException,
            UncommittedChangesException {
        checkArgsCorrectness();
        new ResetCommand(fileSystem, pathToFile).run();
        fileSystem.deleteFile(pathToFile);
    }

    @Override
    protected void checkArgsCorrectness() throws IncorrectArgsException, IOException {
        if (fileSystem.notExists(pathToFile)) {
            throw new IncorrectArgsException("file doesn't exist");
        }
    }
}
