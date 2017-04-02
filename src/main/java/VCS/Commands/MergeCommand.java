package VCS.Commands;

import VCS.Data.FileSystem;
import VCS.Exceptions.IncorrectArgsException;
import VCS.Exceptions.UncommitedChangesException;
import VCS.Exceptions.UnstagedChangesException;
import javafx.util.Pair;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class MergeCommand extends Command {

    private final String branchName;
    public MergeCommand(FileSystem fileSystem, String branchName) {
        super(fileSystem);
        this.branchName = branchName;
    }

    @Override
    public void run() throws UnstagedChangesException, IOException, UncommitedChangesException,
            IncorrectArgsException {
        new CommitCommand(fileSystem).checkFiles();

        Pair<List<String>, List<String>> indexContent = fileSystem.splitLines(
                fileSystem.getIndexLocation());
        List<String> indexedFiles = indexContent.getKey();
        List<String> indexedHashes = indexContent.getValue();
        Pair<List<String>, List<String>> treeContent = fileSystem.splitLines(fileSystem
                .buildTreeLocation(branchName));
        List<String> mergingFiles = treeContent.getKey();
        List<String> mergingHashes = treeContent.getValue();
        List<String> filesToAdd = new ArrayList<>();
        for (int i = 0; i < mergingFiles.size(); i++) {
            int j = indexedFiles.indexOf(mergingFiles.get(i));
            if (j == -1) {
                indexedFiles.add(mergingFiles.get(i));
                indexedHashes.add(mergingHashes.get(i));
                filesToAdd.add(mergingFiles.get(i));
                fileSystem.writeToFile(Paths.get(mergingFiles.get(i)),
                        fileSystem.getFileContentAsByteArray(
                                fileSystem.buildObjectLocation(mergingHashes.get(i))));
            } else {
                if (!indexedHashes.get(j).equals(mergingHashes.get(i))) {
                    indexedHashes.set(j, mergingHashes.get(i));
                }
                filesToAdd.add(mergingFiles.get(i));
                fileSystem.writeToFile(Paths.get(mergingFiles.get(i)),
                        fileSystem.getFileContentAsByteArray(
                                fileSystem.buildObjectLocation(mergingHashes.get(i))));
            }
        }
        new AddCommand(fileSystem, filesToAdd);
        new CommitCommand(fileSystem, "merge '" + branchName + "' branch").run();
    }

    @Override
    public void checkArgsCorrectness() throws IncorrectArgsException {

    }

}
