package VCS.Commands.BranchCommands;

import VCS.Commands.CheckFilesStateCommand;
import VCS.Commands.Command;
import VCS.Data.FileSystem;
import VCS.Exceptions.IncorrectArgsException;
import VCS.Exceptions.Messages;
import VCS.Exceptions.UncommittedChangesException;
import VCS.Exceptions.UnstagedChangesException;
import VCS.Objects.Branch;
import VCS.Objects.Head;
import VCS.Objects.Log;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Date;

/** Branch create command */
public class BranchCreateCommand extends Command {

    @NotNull private final Head repoHead;
    @NotNull private final Branch branch;

    public BranchCreateCommand(@NotNull FileSystem fileSystem, @NotNull String branchName) {
        super(fileSystem);
        branch = new Branch(fileSystem, branchName);
        repoHead = new Head(fileSystem);
    }

    /**
     * Create branch.
     * <pre>
     * Check that arguments are correct and files are staged and committed
     * Update log with information about branch creation
     * Update ref of branch with head commit hash of current branch
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
        new Log(fileSystem, repoHead.getCurrentBranchName()).write(
                buildCreateInformation(repoHead.getHeadCommitHash()));
        branch.updateRef(repoHead.getHeadCommitHash());
    }

    /**
     * Create 'master' branch.
     * Update log with information about 'master' branch creation
     * @param initialCommitHash Hash of initial commit
     * @throws IOException Unknown IO problem
     */
    public void runMaster(@NotNull String initialCommitHash) throws IOException {
        new Log(fileSystem, "master").write(
                buildCreateInformation(initialCommitHash));
        branch.updateRef(initialCommitHash);
    }

    /**
     * Check that branch doesn't exist
     * @throws IncorrectArgsException Incorrect args passed
     */
    @Override
    protected void checkArgsCorrectness() throws IncorrectArgsException {
        if (branch.exists()) {
            throw new IncorrectArgsException(Messages.BRANCH_ALREADY_EXISTS);
        }
    }

    @NotNull
    private String buildCreateInformation(@NotNull String commitHash) throws IOException {
        String currentBranchName = new Head(fileSystem).getCurrentBranchName();
        if (currentBranchName.equals("")) {
            currentBranchName = "new repo";
        } else {
            currentBranchName = "'" + currentBranchName + "' branch";
        }
        return commitHash + " branch created from " + currentBranchName + " " +
                new Date(System.currentTimeMillis()) + "\n";
    }

}
