package VCS.Commands.CheckoutCommands;

import VCS.Commands.CheckFilesStateCommand;
import VCS.Commands.Command;
import VCS.Data.FileSystem;
import VCS.Exceptions.IncorrectArgsException;
import VCS.Exceptions.UncommittedChangesException;
import VCS.Exceptions.UnstagedChangesException;
import VCS.Objects.Branch;
import VCS.Objects.Head;

import java.io.IOException;
import java.nio.file.Path;

/** Checkout by branch command */
public class CheckoutByBranchCommand extends Command {
    private final Branch branch;
    private final Head repoHead;

    public CheckoutByBranchCommand(FileSystem fileSystem, String branchName) {
        super(fileSystem);
        branch = new Branch(fileSystem, branchName);
        repoHead = new Head(fileSystem);
    }

    /**
     * Checkout by branch.
     * <pre>
     * Check that arguments are correct and files are staged and committed
     * Set states of files to their states at head commit of 'branchName' branch
     * Set index content to content of head commit of 'branchName' branch
     * Set HEAD to 'branchName'
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
        Path treeLocation = fileSystem.buildTreeLocation(branch.getBranchName());
        fileSystem.restoreFiles(fileSystem.splitLines(treeLocation));
        fileSystem.copyFile(treeLocation, fileSystem.getIndexLocation());
        repoHead.updateHead(branch.getBranchName());
    }

    /**
     * Check that passed branch is not current and exists
     * @throws IncorrectArgsException Incorrect args passed
     * @throws IOException Unknown IO problem
     */
    @Override
    public void checkArgsCorrectness() throws IncorrectArgsException, IOException {
        if (repoHead.getCurrentBranchName().equals(branch.getBranchName())) {
            throw new IncorrectArgsException("this is current branch");
        }
        if (new Branch(fileSystem, branch.getBranchName()).notExists()) {
            throw new IncorrectArgsException("branch doesn't exists");
        }
    }
}
