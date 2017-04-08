package VCS.Commands;

import javafx.util.Pair;
import VCS.Data.FileSystem;
import VCS.Exceptions.IncorrectArgsException;
import VCS.Exceptions.UncommittedChangesException;
import VCS.Exceptions.UnstagedChangesException;
import VCS.Objects.*;
import VCS.Objects.GitObjects.Blob;
import VCS.Objects.GitObjects.Commit;
import VCS.Objects.GitObjects.Tree;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

/** Commit command*/
public class CommitCommand extends Command {
    private final String message;
    private String commitHash;

    public CommitCommand(FileSystem fileSystem, String message) {
        super(fileSystem);
        this.message = message;
    }

    /**
     * Commit.
     * <pre>
     * Check that arguments are correct and files are staged;
     * Build blobs from all the files;
     * Build tree from files and blobs' hashes;
     * Build commit from tree hash;
     * If commit is not initial (i.e. current branch exists), then update ref and log
     * </pre>
     * @throws IncorrectArgsException Incorrect args passed
     * @throws IOException Unknown IO problem
     * @throws UnstagedChangesException Changes were not staged
     * @throws UncommittedChangesException Changes were not committed
     */
    @Override
    public void run() throws IncorrectArgsException, IOException, UnstagedChangesException,
            UncommittedChangesException {
        checkArgsCorrectness();
        Pair<List<String>, List<String>> content = fileSystem.splitLines(
                fileSystem.getIndexLocation());
        List<String> filesToCommit = content.getKey();
        List<String> hashesOfFilesToCommit = content.getValue();
        new CheckFilesStateCommand(fileSystem).runWithContent(filesToCommit, hashesOfFilesToCommit);
        for (String s : filesToCommit) {
            Blob blob = Blob.buildBlob(fileSystem, Paths.get(s));
            blob.addObject();
        }
        Tree tree = new Tree(fileSystem, filesToCommit, hashesOfFilesToCommit);
        tree.addObject();
        Commit commit = new Commit(fileSystem, tree.getHash().getBytes(), message);
        commit.addObject();
        commitHash = commit.getHash();
        String currentBranchName = new Head(fileSystem).getCurrentBranchName();
        if (!currentBranchName.equals("")) {
            new Branch(fileSystem, currentBranchName).updateRef(commitHash);
            new Log(fileSystem, currentBranchName).write(commit.toString());
        }
    }

    @Override
    public void checkArgsCorrectness() throws IncorrectArgsException {}

    public String getCommitHash() {
        return commitHash;
    }

}
