package VCS.Commands.CheckoutCommands;

import VCS.Commands.Command;
import VCS.Commands.CommitCommand;
import VCS.Data.FileSystem;
import VCS.Exceptions.IncorrectArgsException;
import VCS.Exceptions.UncommittedChangesException;
import VCS.Exceptions.UnstagedChangesException;
import VCS.Objects.Branch;
import VCS.Objects.HEAD;
import VCS.Objects.Log;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Date;

public class CheckoutByCommitCommand extends Command {
    private final String commitHash;

    public CheckoutByCommitCommand(FileSystem fileSystem, String commitHash) {
        super(fileSystem);
        this.commitHash = commitHash;
    }

    @Override
    public void run() throws IncorrectArgsException, IOException, UnstagedChangesException,
            UncommittedChangesException {
        checkArgsCorrectness();
        new CommitCommand(fileSystem).checkFiles();
        Path treeLocation = fileSystem.buildObjectLocation(fileSystem.getFileContentAsString(
                fileSystem.buildObjectLocation(commitHash)));
        fileSystem.restoreFiles(fileSystem.splitLines(treeLocation));
        fileSystem.copyFile(treeLocation, fileSystem.getIndexLocation());
        String currentBranch = new HEAD(fileSystem).getCurrentBranch();
        new Branch(fileSystem, currentBranch).updateRef(commitHash);
        new Log(fileSystem, currentBranch).write(buildCheckoutByCommitInformation());
    }

    private String buildCheckoutByCommitInformation() {
        return commitHash + " checkout by commit " + new Date(System.currentTimeMillis());
    }

    @Override
    public void checkArgsCorrectness() throws IncorrectArgsException, IOException {
        if (fileSystem.notExists(fileSystem.buildObjectLocation(commitHash))) {
            throw new IncorrectArgsException("commit doesn't exist");
        }
    }
}
