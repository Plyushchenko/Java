package VCS.Commands.CheckoutCommands;

import VCS.Commands.CheckFilesStateCommand;
import VCS.Commands.Command;
import VCS.Data.FileSystem;
import VCS.Exceptions.IncorrectArgsException;
import VCS.Exceptions.Messages;
import VCS.Exceptions.UncommittedChangesException;
import VCS.Exceptions.UnstagedChangesException;
import VCS.Objects.Branch;
import VCS.Objects.Head;
import org.apache.logging.log4j.core.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;

/** Checkout by branch command */
public class CheckoutByBranchCommand extends Command {

    @NotNull private final String branchName;
    @NotNull private final Head repoHead;

    public CheckoutByBranchCommand(@NotNull FileSystem fileSystem, @NotNull Logger logger,
                                   @NotNull String branchName) {
        super(fileSystem, logger);
        this.branchName = branchName;
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
        logger.info("begin: CheckoutByBranchCommand.run()");
        checkArgsCorrectness();
        new CheckFilesStateCommand(fileSystem, logger).run();
        Path treeLocation = fileSystem.buildTreeLocation(branchName);
        fileSystem.restoreFiles(fileSystem.splitLines(treeLocation));
        fileSystem.copyFile(treeLocation, fileSystem.getIndexLocation());
        repoHead.updateHead(branchName);
        logger.info("end: CheckoutByBranchCommand.run()");
    }

    /**
     * Check that branch exists and is not the current branch
     * @throws IncorrectArgsException Incorrect args passed
     * @throws IOException Unknown IO problem
     */
    @Override
    protected void checkArgsCorrectness() throws IncorrectArgsException, IOException {
        if (repoHead.getCurrentBranchName().equals(branchName)) {
            throw new IncorrectArgsException(Messages.THIS_IS_THE_CURRENT_BRANCH);
        }
        if (new Branch(fileSystem, branchName).notExists()) {
            throw new IncorrectArgsException(Messages.BRANCH_DOESN_T_EXIST);
        }
    }

}
