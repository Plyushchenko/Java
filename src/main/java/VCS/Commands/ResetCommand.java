package VCS.Commands;

import VCS.Data.FileSystem;
import VCS.Exceptions.IncorrectArgsException;
import VCS.Exceptions.UncommittedChangesException;
import VCS.Exceptions.UnstagedChangesException;
import VCS.Objects.Index;
import javafx.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ResetCommand extends Command {

    private final Path pathToFile;
    public ResetCommand(@NotNull FileSystem fileSystem, String fileToReset) {
        super(fileSystem);
        this.pathToFile = Paths.get(fileToReset).toAbsolutePath();
    }

    ResetCommand(FileSystem fileSystem, Path pathToFile) {
        super(fileSystem);
        this.pathToFile = pathToFile;
    }

    @Override
    public void run() throws IncorrectArgsException, IOException, UnstagedChangesException,
            UncommittedChangesException {
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
    }

    @Override
    protected void checkArgsCorrectness() throws IncorrectArgsException, IOException {
        if (fileSystem.notExists(pathToFile)) {
            throw new IncorrectArgsException("file doesn't exist");
        }

    }
}
