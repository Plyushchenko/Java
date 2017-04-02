package VCS.Commands.BranchCommands;

import VCS.Commands.Command;
import VCS.Commands.CommitCommand;
import VCS.Data.FileSystem;
import VCS.Exceptions.IncorrectArgsException;
import VCS.Exceptions.UncommitedChangesException;
import VCS.Exceptions.UnstagedChangesException;
import VCS.Objects.Branch;
import VCS.Objects.HEAD;

import java.io.IOException;
import java.util.Date;

public class BranchCreateCommand extends Command {
    private final Branch branch;
    public BranchCreateCommand(FileSystem fileSystem, String branchName) {
        super(fileSystem);
        branch = new Branch(fileSystem, branchName);
    }

    @Override
    public void run() throws IOException, IncorrectArgsException, UnstagedChangesException,
            UncommitedChangesException {
        checkArgsCorrectness();
        new CommitCommand(fileSystem).checkFiles();
        String HEADCommitHash = new HEAD(fileSystem).getHEADCommitHash();
        branch.updateLog(buildCreateInformation(HEADCommitHash));
        branch.updateRef(HEADCommitHash);
    }

    public void runMaster(String initialCommitHash) throws IOException {
        branch.updateLog(buildCreateInformation(initialCommitHash));
        branch.updateRef(initialCommitHash);
    }

    @Override
    public void checkArgsCorrectness() throws IncorrectArgsException {
        if (branch.exists()) {
            throw new IncorrectArgsException("branch already exists");
        }
    }

    private String buildCreateInformation(String commitHash) throws IOException {
        String currentBranch = new HEAD(fileSystem).getCurrentBranch();
        if (currentBranch.equals("")) {
            currentBranch = "new repo";
        } else {
            currentBranch = "'" + currentBranch + "' branch";
        }
        return commitHash + " branch created from " + currentBranch + " " +
                new Date(System.currentTimeMillis()) + "\n";
    }
}
