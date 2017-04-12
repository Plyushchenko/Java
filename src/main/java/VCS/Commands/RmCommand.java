package VCS.Commands;

import VCS.Data.FileSystem;
import VCS.Exceptions.IncorrectArgsException;
import VCS.Exceptions.Messages;
import VCS.Exceptions.UncommittedChangesException;
import VCS.Exceptions.UnstagedChangesException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/** Rm command*/
public class RmCommand extends Command {

    private final Path pathToFile;
    public RmCommand(@NotNull FileSystem fileSystem, String fileToRm) {
        super(fileSystem);
        pathToFile = Paths.get(fileToRm).toAbsolutePath();
    }

    /**
     * Rm.
     * Reset file and delete it
     * @throws IncorrectArgsException Incorrect args passed
     * @throws IOException Unknown IO problem
     * @throws UnstagedChangesException Changes were not staged
     * @throws UncommittedChangesException Changes were not committed
     */
    @Override
    public void run() throws IncorrectArgsException, IOException, UnstagedChangesException,
            UncommittedChangesException {
        checkArgsCorrectness();
        new ResetCommand(fileSystem, pathToFile).run();
        fileSystem.deleteFile(pathToFile);
    }

    /**
     * Check that file exists
     * @throws IncorrectArgsException Incorrect args passed
     * @throws IOException Unknown IO problem
     */
    @Override
    protected void checkArgsCorrectness() throws IncorrectArgsException, IOException {
        if (fileSystem.notExists(pathToFile)) {
            throw new IncorrectArgsException(Messages.FILE_DOESN_T_EXIST);
        }
    }

}
