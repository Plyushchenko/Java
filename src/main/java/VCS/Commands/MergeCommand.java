package VCS.Commands;

import VCS.Data.FileSystem;
import VCS.Exceptions.IncorrectArgsException;
import VCS.Exceptions.UncommittedChangesException;
import VCS.Exceptions.UnstagedChangesException;
import VCS.Objects.Head;
import javafx.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MergeCommand extends Command {

    @NotNull private final String branchName;

    public MergeCommand(@NotNull FileSystem fileSystem, @NotNull String branchName) {
        super(fileSystem);
        this.branchName = branchName;
    }

    /**
     * If any file has different states at these two branches then the state of this file will be
     * equal to the state of this file at 'branchName' branch
     * @throws IncorrectArgsException Incorrect args passed
     * @throws IOException Unknown IO problem
     * @throws UnstagedChangesException Changes were not staged
     * @throws UncommittedChangesException Changes were not committed
     */
    @Override
    public void run() throws IncorrectArgsException, IOException, UnstagedChangesException,
            UncommittedChangesException {
        new CheckFilesStateCommand(fileSystem).run();
        Pair<List<String>, List<String>> indexContent = fileSystem.splitLines(
                fileSystem.getIndexLocation());
        List<String> indexedFiles = indexContent.getKey();
        List<String> indexedHashes = indexContent.getValue();
        Pair<List<String>, List<String>> treeContent = fileSystem.splitLines(
                fileSystem.buildTreeLocation(branchName));
        List<String> mergingFiles = treeContent.getKey();
        List<String> mergingHashes = treeContent.getValue();
        List<String> filesToAdd = new ArrayList<>();
        for (int i = 0; i < mergingFiles.size(); i++) {
            int j = indexedFiles.indexOf(mergingFiles.get(i));
            if (j == -1 || !indexedHashes.get(j).equals(mergingHashes.get(i))) {
                if (j == -1) {
                    indexedFiles.add(mergingFiles.get(i));
                    indexedHashes.add(mergingHashes.get(i));
                } else {
                    indexedHashes.set(j, mergingHashes.get(i));
                }
                filesToAdd.add(mergingFiles.get(i));
                fileSystem.restoreFile(mergingFiles.get(i), mergingHashes.get(i));
            }
        }
        new AddCommand(fileSystem, filesToAdd).run();
        new CommitCommand(fileSystem, "merge '" + branchName + "' branch").run();
    }

    @Override
    protected void checkArgsCorrectness() throws IncorrectArgsException, IOException {
        if (branchName.equals(new Head(fileSystem).getCurrentBranchName())) {
            throw new IncorrectArgsException("this is a current branch");
        }
    }

}
