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
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/** File state checker */
public class CheckFilesStateCommand extends Command {

    public CheckFilesStateCommand(@NotNull FileSystem fileSystem, @NotNull Logger logger) {
        super(fileSystem, logger);
    }

    /**
     * Check that files were staged and committed
     * @throws IncorrectArgsException Incorrect args passed
     * @throws IOException Unknown IO problem
     * @throws UnstagedChangesException Changes were not staged
     * @throws UncommittedChangesException Changes were not committed
     */
    @Override
    public void run() throws IncorrectArgsException, IOException, UnstagedChangesException,
            UncommittedChangesException {
        logger.info("begin: CheckFilesStateCommand.run()");
        if (new Head(fileSystem).getCurrentBranchName().equals("")){
            logger.info("end: CheckFilesStateCommand.run(), nothing to check");
            return;
        }
        checkForUnstagedFiles();
        checkForUncommittedFiles();
        logger.info("end: CheckFilesStateCommand.run()");
    }

    void runWithContent(@NotNull List<String> filesToCommit,
                        @NotNull List<String> hashesOfFilesToCommit)
            throws IOException, UnstagedChangesException, UncommittedChangesException {
        String loggerInfo = "CheckFilesStateCommand.runWithContent([" +
                filesToCommit.stream().collect(Collectors.joining(",")) + "], [" +
                hashesOfFilesToCommit.stream().collect(Collectors.joining(",")) + "])";
        logger.info("begin: " + loggerInfo);
        checkForUnstagedFiles(filesToCommit, hashesOfFilesToCommit);
        logger.info("end: " + loggerInfo);
    }

    private void checkForUnstagedFiles() throws IOException, UnstagedChangesException {
        Pair<List<String>, List<String>> p = fileSystem.splitLines(fileSystem.getIndexLocation());
        checkForUnstagedFiles(p.getKey(), p.getValue());
    }

    private void checkForUnstagedFiles(
            @NotNull List<String> filesToCommit, @NotNull List<String> hashesOfFilesToCommit) throws
            IOException, UnstagedChangesException {
        String unstagedFiles = "";
        for (int i = 0; i < filesToCommit.size(); i++) {
            if (fileSystem.notExists(Paths.get(filesToCommit.get(i)))
                    || !Blob.buildBlob(fileSystem, Paths.get(filesToCommit.get(i))).getHash()
                    .equals(hashesOfFilesToCommit.get(i))) {
                unstagedFiles += filesToCommit.get(i) + "\n";
            }
        }
        if (!unstagedFiles.equals("")) {
            unstagedFiles = "Add/remove these files:\n" + unstagedFiles;
            throw new UnstagedChangesException(unstagedFiles);
        }
    }

    private void checkForUncommittedFiles() throws IOException, UncommittedChangesException {
        Pair<List<String>, List<String>> indexContent = fileSystem.splitLines(
                fileSystem.getIndexLocation());
        List<String> indexedFiles = indexContent.getKey();
        List<String> indexedHashes = indexContent.getValue();
        Pair<List<String>, List<String>> treeContent = fileSystem.splitLines(
                fileSystem.buildTreeLocation(new Head(fileSystem).getCurrentBranchName()));
        List<String> committedFiles = treeContent.getKey();
        List<String> committedHashes = treeContent.getValue();
        String UncommittedFiles = "";
        for (int i = 0; i < indexedFiles.size(); i++) {
            int j = committedFiles.indexOf(indexedFiles.get(i));
            if (j == -1 || !committedHashes.get(j).equals(indexedHashes.get(i))) {
                UncommittedFiles += indexedFiles.get(i) + "\n";
            }
        }
        if (!UncommittedFiles.equals("")) {
            UncommittedFiles = "Commit these files:\n" + UncommittedFiles;
            throw new UncommittedChangesException(UncommittedFiles);
        }
    }

    @Override
    protected void checkArgsCorrectness() throws IncorrectArgsException, IOException {}

}
