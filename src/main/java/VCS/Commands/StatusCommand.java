package VCS.Commands;

import VCS.Data.FileSystem;
import VCS.Exceptions.IncorrectArgsException;
import VCS.Exceptions.UncommittedChangesException;
import VCS.Exceptions.UnstagedChangesException;
import VCS.Objects.GitObjects.Blob;
import VCS.Objects.Head;
import javafx.util.Pair;
import org.apache.logging.log4j.core.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Status command*/
public class StatusCommand extends Command {

    @NotNull private final List<Path> untracked = new ArrayList<>();
    @NotNull private final List<Path> staged = new ArrayList<>();
    @NotNull private final List<Path> modified = new ArrayList<>();
    @NotNull private final List<Path> deleted = new ArrayList<>();

    public StatusCommand(@NotNull FileSystem fileSystem, @NotNull Logger logger) {
        super(fileSystem, logger);
    }

    /**
     * Run 'status' command.
     * Default folder is the folder where '.mygit' directory is located
     * @throws IncorrectArgsException Incorrect args passed
     * @throws IOException Unknown IO problem
     * @throws UnstagedChangesException Changes were not staged
     * @throws UncommittedChangesException Changes were not committed
     */
    @Override
    public void run() throws IncorrectArgsException, IOException, UnstagedChangesException,
            UncommittedChangesException {
        logger.info("begin: StatusCommand.run()");
        runWithFolder(fileSystem.getFolderWithGitLocation());
        logger.info("end: StatusCommand.run()");
    }

    /**
     * Run 'status' command on specified folder.
     * @param folderPath Specified folder
     * @throws IOException Unknown IO problem
     */
    void runWithFolder(@NotNull Path folderPath) throws IOException {
        logger.info("begin: StatusCommand.run(" + folderPath + ")");
        List<Path> paths = fileSystem.getFolderContent(folderPath);
        List<Path> pathsNotToShow = fileSystem.getFolderContent(fileSystem.getGitLocation());
        Pair<List<String>, List<String>> indexContent = fileSystem.splitLines(
                fileSystem.getIndexLocation());
        List<String> indexedFiles = indexContent.getKey();
        List<String> indexedHashes = indexContent.getValue();
        Pair<List<String>, List<String>> headCommitContent = fileSystem.splitLines(fileSystem
                .buildTreeLocation(new Head(fileSystem).getCurrentBranchName()));
        List<String> headCommitFiles = headCommitContent.getKey();
        List<String> headCommitHashes = headCommitContent.getValue();
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
                    int j = headCommitFiles.indexOf(path.toString());
                    if (j != -1 && indexedHashes.get(i).equals(headCommitHashes.get(j))) {
                       continue;
                    }
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
        logger.info("end: StatusCommand.run(" + folderPath + ")");
    }

    @Override
    protected void checkArgsCorrectness() throws IncorrectArgsException, IOException {}

    @NotNull
    public String getStatus() {
        StringBuilder status = new StringBuilder();
        for (Path path : modified) {
            status.append("modified: ").append(path).append("\n");
        }
        for (Path path : staged) {
            status.append("staged: ").append(path).append("\n");
        }
        for (Path path : deleted) {
            status.append("deleted: ").append(path).append("\n");
        }
        for (Path path : untracked) {
            status.append("untracked: ").append(path).append("\n");
        }
        return status.toString();
    }


    @NotNull
    List<Path> getUntracked() {
        return untracked;
    }

    @NotNull
    public List<Path> getStaged() {
        return staged;
    }

    @NotNull
    public List<Path> getModified() {
        return modified;
    }

    @NotNull
    public List<Path> getDeleted() {
        return deleted;
    }

}
