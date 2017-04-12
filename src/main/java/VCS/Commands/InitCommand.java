package VCS.Commands;

import VCS.Commands.BranchCommands.BranchCreateCommand;
import VCS.Commands.CheckoutCommands.CheckoutByBranchCommand;
import VCS.Data.FileSystem;
import VCS.Exceptions.IncorrectArgsException;
import VCS.Exceptions.UncommittedChangesException;
import VCS.Exceptions.UnstagedChangesException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/** Init command */
public class InitCommand extends Command {

    public InitCommand(@NotNull FileSystem fileSystem) {
        super(fileSystem);
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
        fileSystem.createGitDirectoriesAndFiles();
        CommitCommand initialCommitCommand = new CommitCommand(fileSystem, "initial commit");
        initialCommitCommand.run();
        String initialCommitHash = initialCommitCommand.getCommitHash();
        new BranchCreateCommand(fileSystem, "master").runMaster(initialCommitHash);
        new CheckoutByBranchCommand(fileSystem, "master").run();
    }

    @Override
    protected void checkArgsCorrectness() throws IncorrectArgsException {}

}
