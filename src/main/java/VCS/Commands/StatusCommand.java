package VCS.Commands;

import VCS.Data.FileSystem;
import VCS.Exceptions.IncorrectArgsException;
import VCS.Exceptions.UncommittedChangesException;
import VCS.Exceptions.UnstagedChangesException;
import VCS.Objects.GitObjects.Blob;
import javafx.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StatusCommand extends Command {


    @NotNull private String status = "";

    public StatusCommand(@NotNull FileSystem fileSystem) {
        super(fileSystem);
    }

    @Override
    //TODO: можно распихать на 4 списка
    public void run() throws IncorrectArgsException, IOException, UnstagedChangesException,
            UncommittedChangesException {
        List<Path> paths = fileSystem.getFolderContent(
                fileSystem.getFolderWithGitLocation());
        List<Path> pathsNotToShow = fileSystem.getFolderContent(fileSystem.getGitLocation());
        Pair<List<String>, List<String>> indexContent = fileSystem.splitLines(
                fileSystem.getIndexLocation());
        List<String> indexedFiles = indexContent.getKey();
        List<String> indexedHashes = indexContent.getValue();
        List<Boolean> found = new ArrayList<>(Collections.nCopies(
                indexedFiles.size(), Boolean.FALSE));
        for (Path path : paths) {
            if (pathsNotToShow.contains(path)) {
                continue;
            }
            int i = indexedFiles.indexOf(path.toString());
            if (i == -1) {
                status += "unstaged: " + path + "\n";
            } else {
                found.set(i, Boolean.TRUE);
                if (indexedHashes.get(i).equals(Blob.buildBlob(fileSystem, path).getHash())) {
                    status += "staged:   " + path + "\n";
                } else {
                    status += "modified: " + path + "\n";
                }
            }
        }
        for (int i = 0; i < found.size(); i++) {
            if (found.get(i) == Boolean.FALSE) {
                status += "deleted:  " + indexedFiles.get(i) + "\n";
            }
        }
    }

    @Override
    protected void checkArgsCorrectness() throws IncorrectArgsException, IOException {

    }

    @NotNull
    public String getStatus() {
        return status;
    }
}
