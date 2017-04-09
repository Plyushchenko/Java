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
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StatusCommand extends Command {

    @NotNull private List<Path> untracked;
    @NotNull private List<Path> staged;
    @NotNull private List<Path> modified;
    @NotNull private List<Path> deleted;
    public StatusCommand(@NotNull FileSystem fileSystem) {
        super(fileSystem);
        untracked = new ArrayList<>();
        staged = new ArrayList<>();
        modified = new ArrayList<>();
        deleted = new ArrayList<>();
    }

    @Override
    public void run() throws IncorrectArgsException, IOException, UnstagedChangesException,
            UncommittedChangesException {
        runWithFolder(fileSystem.getFolderWithGitLocation());
    }

    void runWithFolder(@NotNull Path folderPath) throws IOException {
        List<Path> paths = fileSystem.getFolderContent(folderPath);
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
                untracked.add(path);
            } else {
                found.set(i, Boolean.TRUE);
                if (indexedHashes.get(i).equals(Blob.buildBlob(fileSystem, path).getHash())) {
                    staged.add(path);
                } else {
                    modified.add(path);
                }
            }
        }
        for (int i = 0; i < found.size(); i++) {
            if (found.get(i) == Boolean.FALSE) {
                deleted.add(Paths.get(indexedFiles.get(i)));
            }
        }
    }

    @Override
    protected void checkArgsCorrectness() throws IncorrectArgsException, IOException {}

    @NotNull
    public String getStatus() {
        String status = "";
        for (Path path : modified) {
            status += "modified: " + path + "\n";
        }
        for (Path path : staged) {
            status += "staged: " + path + "\n";
        }
        for (Path path : deleted) {
            status += "deleted: " + path + "\n";
        }
        for (Path path : untracked) {
            status += "untracked: " + path + "\n";
        }
        return status;
    }


    @NotNull
    List<Path> getUntracked() {
        return untracked;
    }

    /*
    @NotNull
    public List<Path> getStaged() {
        return staged;
    }
    */

    /*
    @NotNull
    public List<Path> getModified() {
        return modified;
    }
    */

    /*
    @NotNull
    public List<Path> getDeleted() {
        return deleted;
    }
    */

}
