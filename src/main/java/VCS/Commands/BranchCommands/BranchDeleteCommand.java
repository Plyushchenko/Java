package VCS.Commands.BranchCommands;

import VCS.Objects.Branch;
import VCS.Commands.Command;
import VCS.Data.FileSystem;
import VCS.Objects.Head;
import VCS.Exceptions.IncorrectArgsException;
import VCS.Objects.Log;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/** Branch delete command*/
public class BranchDeleteCommand extends Command {

    @NotNull private final Branch branch;

    public BranchDeleteCommand(@NotNull FileSystem fileSystem, @NotNull String branchName) {
        super(fileSystem);
        branch = new Branch(fileSystem, branchName);
    }

    /**
     * Delete branch.
     * <pre>
     * Check that arguments are correct and files are staged and committed
     * Delete log file of branch
     * Dele ref file of branch
     * </pre>
     * @throws IncorrectArgsException Incorrect args passed
     * @throws IOException Unknown IO problem
     */
    @Override
    public void run() throws IOException, IncorrectArgsException {
        checkArgsCorrectness();
        new Log(fileSystem, branch.getBranchName()).delete();
        branch.deleteRef();
    }

    /**
     * Check that branch exists and is not the current branch
     * @throws IncorrectArgsException Incorrect args passed
     * @throws IOException Unknown IO problem
     */
    @Override
    protected void checkArgsCorrectness() throws IncorrectArgsException, IOException {
        if (branch.notExists()) {
            throw new IncorrectArgsException("branch doesn't exists");
        }
        if (new Head(fileSystem).getCurrentBranchName().equals(branch.getBranchName())) {
            throw new IncorrectArgsException("trying to delete current branch");
        }
    }

}
