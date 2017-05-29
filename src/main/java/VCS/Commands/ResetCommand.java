package VCS.Commands;

import VCS.Data.FileSystem;
import VCS.Exceptions.IncorrectArgsException;
import VCS.Exceptions.UncommittedChangesException;
import VCS.Exceptions.UnstagedChangesException;
import VCS.Objects.Index;
import javafx.util.Pair;
import org.apache.logging.log4j.core.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ResetCommand extends Command {

    private final Path pathToFile;

    public ResetCommand(@NotNull FileSystem fileSystem, @NotNull Logger logger,
                        @NotNull String fileToReset) {
        super(fileSystem, logger);
        this.pathToFile = Paths.get(fileToReset).toAbsolutePath();
    }

    ResetCommand(@NotNull FileSystem fileSystem, @NotNull Logger logger, @NotNull Path
            pathToFile) {
        super(fileSystem, logger);
        this.pathToFile = pathToFile;
    }

    /**
     * Find the file in index and remove path and hash from index.
     * If file was not found in index then do nothing
     * @throws IncorrectArgsException Incorrect args passed
     * @throws IOException Unknown IO problem
     * @throws UnstagedChangesException Changes were not staged
     * @throws UncommittedChangesException Changes were not committed
     */
    @Override
    public void run() throws IncorrectArgsException, IOException, UnstagedChangesException,
            UncommittedChangesException {
        logger.info("begin: ResetCommand.run()");
        checkArgsCorrectness();
        Pair<List<String>, List<String>> indexContent = fileSystem.splitLines(
                fileSystem.getIndexLocation());
        List<String> indexedFiles = indexContent.getKey();
        List<String> indexedHashes = indexContent.getValue();
        int i = indexedFiles.indexOf(pathToFile.toString());
        if (i == -1) {
            return;
        }
        indexedFiles.remove(i);
        indexedHashes.remove(i);
        new Index(fileSystem).setContent(indexedFiles, indexedHashes);
        logger.info("end: ResetCommand.run()");
    }

    /**
     * Check that file exists
     * @throws IncorrectArgsException Incorrect args passed
     * @throws IOException Unknown IO problem
     */
    @Override
    protected void checkArgsCorrectness() throws IncorrectArgsException, IOException {}

}
