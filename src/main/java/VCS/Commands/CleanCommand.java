package VCS.Commands;

import VCS.Data.FileSystem;
import VCS.Exceptions.IncorrectArgsException;
import VCS.Exceptions.UncommittedChangesException;
import VCS.Exceptions.UnstagedChangesException;
import org.apache.logging.log4j.core.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/** Clean command */
public class CleanCommand extends Command {

    public CleanCommand(@NotNull FileSystem fileSystem, @NotNull Logger logger) {
        super(fileSystem, logger);
    }

    /**
     * Delete all untracked files
     * @throws IncorrectArgsException Incorrect args passed
     * @throws IOException Unknown IO problem
     * @throws UnstagedChangesException Changes were not staged
     * @throws UncommittedChangesException Changes were not committed
     */
    @Override
    public void run() throws IncorrectArgsException, IOException, UnstagedChangesException,
            UncommittedChangesException {
        logger.info("begin: CleanCommand.run()");
        StatusCommand statusCommand = new StatusCommand(fileSystem, logger);
        statusCommand.runWithFolder(fileSystem.getWorkingDirectory());
        List<Path> untracked = statusCommand.getUntracked();
        for (Path path : untracked) {
            fileSystem.deleteFile(path);
        }
        logger.info("end: CleanCommand.run()");
    }

    @Override
    protected void checkArgsCorrectness() throws IncorrectArgsException, IOException {}

}
