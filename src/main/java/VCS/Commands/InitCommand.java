package VCS.Commands;

import VCS.Commands.BranchCommands.BranchCreateCommand;
import VCS.Commands.CheckoutCommands.CheckoutByBranchCommand;
import VCS.Data.FileSystem;
import VCS.Exceptions.IncorrectArgsException;
import VCS.Exceptions.UncommittedChangesException;
import VCS.Exceptions.UnstagedChangesException;
import org.apache.logging.log4j.core.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/** Init command */
public class InitCommand extends Command {

    public InitCommand(@NotNull FileSystem fileSystem, @NotNull Logger logger) {
        super(fileSystem, logger);
    }

    /**
     * Init.
     * <pre>
     * Create directories anf files for repo
     * Execute initial commit and create 'master' branch
     * </pre>
     * @throws IncorrectArgsException Incorrect args passed
     * @throws IOException Unknown IO problem
     * @throws UnstagedChangesException Changes were not staged
     * @throws UncommittedChangesException Changes were not committed
     */
    @Override
    public void run() throws IncorrectArgsException, IOException, UnstagedChangesException,
            UncommittedChangesException {
        logger.info("begin: CommitCommand.run()");
        fileSystem.createGitDirectoriesAndFiles();
        CommitCommand initialCommitCommand =
                new CommitCommand(fileSystem, logger, "initial " + "commit");
        initialCommitCommand.run();
        String initialCommitHash = initialCommitCommand.getCommitHash();
        new BranchCreateCommand(fileSystem, logger, "master").runMaster(initialCommitHash);
         new CheckoutByBranchCommand(fileSystem, logger, "master").run();
        logger.info("end: CommitCommand.run()");
    }

    @Override
    protected void checkArgsCorrectness() throws IncorrectArgsException {}

}
