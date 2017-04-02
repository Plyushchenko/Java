package VCS.Commands.BranchCommands;

import VCS.Objects.Branch;
import VCS.Commands.Command;
import VCS.Data.FileSystem;
import VCS.Objects.HEAD;
import VCS.Exceptions.IncorrectArgsException;

import java.io.IOException;

public class BranchDeleteCommand extends Command {

    private final Branch branch;
    public BranchDeleteCommand(FileSystem fileSystem, String branchName) {
        super(fileSystem);
        branch = new Branch(fileSystem, branchName);
    }

    @Override
    public void run() throws IOException, IncorrectArgsException {
        checkArgsCorrectness();
        branch.deleteLog();
        branch.deleteRef();
    }

    @Override
    public void checkArgsCorrectness() throws IncorrectArgsException, IOException {
        if (branch.notExists()) {
            throw new IncorrectArgsException("branch doesn't exists");
        }
        if (new HEAD(fileSystem).getCurrentBranch().equals(branch.getBranchName())) {
            throw new IncorrectArgsException("trying to delete current branch");
        }
    }
}
