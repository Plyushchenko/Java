package VCS.Commands;

import javafx.util.Pair;
import VCS.Data.FileSystem;
import VCS.Exceptions.IncorrectArgsException;
import VCS.Exceptions.UncommitedChangesException;
import VCS.Exceptions.UnstagedChangesException;
import VCS.Objects.*;
import VCS.Objects.GitObjects.Blob;
import VCS.Objects.GitObjects.Commit;
import VCS.Objects.GitObjects.Tree;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

public class CommitCommand extends Command {
    private final String message;
    private String commitHash;

    public CommitCommand(FileSystem fileSystem, String message) {
        super(fileSystem);
        this.message = message;
    }

    public CommitCommand(FileSystem fileSystem) {
        super(fileSystem);
        this.message = null;
    }


    @Override
    public void run() throws IncorrectArgsException, IOException, UnstagedChangesException {
        checkArgsCorrectness();
        Pair<List<String>, List<String>> content = fileSystem.splitLines(
                fileSystem.getIndexLocation());
        List<String> filesToCommit = content.getKey();
        List<String> hashesOfFilesToCommit = content.getValue();
        checkForUnstagedFiles(filesToCommit, hashesOfFilesToCommit);
        for (String s : filesToCommit) {
            Blob blob = Blob.buildBlob(fileSystem, Paths.get(s));
            blob.addObject();
        }
        Tree tree = new Tree(fileSystem, filesToCommit, hashesOfFilesToCommit);
        tree.addObject();
        Commit commit = new Commit(fileSystem, tree.getHash().getBytes(), message);
        commit.addObject();
        commitHash = commit.getHash();
        String currentBranch = new HEAD(fileSystem).getCurrentBranch();
        if (!currentBranch.equals("")) {
            new Branch(fileSystem, currentBranch).updateRef(commitHash);
            new Log(fileSystem, currentBranch).write(commit.toString());
        }
    }

    @Override
    public void checkArgsCorrectness() throws IncorrectArgsException {

    }

    public String getCommitHash() {
        return commitHash;
    }

    public void checkFiles() throws IOException, UnstagedChangesException,
            UncommitedChangesException {
        if (new HEAD(fileSystem).getCurrentBranch().equals("")){
            return;
        }
        checkForUnstagedFiles();
        checkForUncommitedFiles();
    }

    private void checkForUnstagedFiles() throws IOException, UnstagedChangesException {
        Pair<List<String>, List<String>> p = fileSystem.splitLines(fileSystem.getIndexLocation());
        checkForUnstagedFiles(p.getKey(), p.getValue());
    }

    private void checkForUnstagedFiles(
            List<String> filesToCommit, List<String> hashesOfFilesToCommit) throws
            IOException, UnstagedChangesException {
        String unstagedFiles = "";
        for (int i = 0; i < filesToCommit.size(); i++) {
            if (!Blob.buildBlob(fileSystem, Paths.get(filesToCommit.get(i))).getHash().equals(
                    hashesOfFilesToCommit.get(i))) {
                unstagedFiles += filesToCommit.get(i) + "\n";
            }
        }
        if (!unstagedFiles.equals("")) {
            unstagedFiles = "Add these files:\n" + unstagedFiles;
            throw new UnstagedChangesException(unstagedFiles);
        }
    }

    private void checkForUncommitedFiles() throws IOException, UncommitedChangesException {
        Pair<List<String>, List<String>> indexContent = fileSystem.splitLines(
                fileSystem.getIndexLocation());
        List<String> indexedFiles = indexContent.getKey();
        List<String> indexedHashes = indexContent.getValue();
        Pair<List<String>, List<String>> treeContent = fileSystem.splitLines(fileSystem
                .buildTreeLocation(new HEAD(fileSystem).getCurrentBranch()));
        System.out.println("commit command: treeContent: " + treeContent.toString());
        List<String> commitedFiles = treeContent.getKey();
        List<String> commitedHashes = treeContent.getValue();
        String uncommitedFiles = "";
        for (int i = 0; i < indexedFiles.size(); i++) {
            int j = commitedFiles.indexOf(indexedFiles.get(i));
            if (j == -1 || !commitedHashes.get(j).equals(indexedHashes.get(i))) {
                uncommitedFiles += indexedFiles.get(i) + "\n";
            }
        }
        if (!uncommitedFiles.equals("")) {
            uncommitedFiles = "Commit these files:\n" + uncommitedFiles;
            throw new UncommitedChangesException(uncommitedFiles);
        }

    }

}
