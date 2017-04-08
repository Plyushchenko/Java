package VCS.Commands.CheckoutCommands;

import VCS.Commands.CheckFilesStateCommand;
import VCS.Commands.Command;
import VCS.Data.FileSystem;
import VCS.Exceptions.IncorrectArgsException;
import VCS.Exceptions.UncommittedChangesException;
import VCS.Exceptions.UnstagedChangesException;
import VCS.Objects.Branch;
import VCS.Objects.Head;
import VCS.Objects.Log;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Date;

/** Checkout by commit command */
public class CheckoutByCommitCommand extends Command {
    private final String commitHash;

    public CheckoutByCommitCommand(FileSystem fileSystem, String commitHash) {
        super(fileSystem);
        this.commitHash = commitHash;
    }

    /**
     * Checkout by commit.
     * <pre>
     * Check that arguments are correct and files are staged and committed
     * Set states of files to their states at commit
     * Set index content to content of commit
     * Update ref of current branch
     * Update log of current branch
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
        new CheckFilesStateCommand(fileSystem).run();
        Path treeLocation = fileSystem.buildObjectLocation(fileSystem.getFileContentAsString(
                fileSystem.buildObjectLocation(commitHash)));
        fileSystem.restoreFiles(fileSystem.splitLines(treeLocation));
        fileSystem.copyFile(treeLocation, fileSystem.getIndexLocation());
        String currentBranchName = new Head(fileSystem).getCurrentBranchName();
        new Branch(fileSystem, currentBranchName).updateRef(commitHash);
        new Log(fileSystem, currentBranchName).write(buildCheckoutByCommitInformation());
    }

    private String buildCheckoutByCommitInformation() {
        return commitHash + " checkout by commit " + new Date(System.currentTimeMillis());
    }

    /**
     * Check that commit with passed hash exists
     * @throws IncorrectArgsException Incorrect args passed
     * @throws IOException Unknown IO problem
     */
    @Override
    public void checkArgsCorrectness() throws IncorrectArgsException, IOException {
        if (fileSystem.notExists(fileSystem.buildObjectLocation(commitHash))) {
            throw new IncorrectArgsException("commit doesn't exist");
        }
    }
}
