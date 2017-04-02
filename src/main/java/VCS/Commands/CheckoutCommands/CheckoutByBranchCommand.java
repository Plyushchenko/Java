package VCS.Commands.CheckoutCommands;

import VCS.Commands.Command;
import VCS.Commands.CommitCommand;
import VCS.Data.FileSystem;
import VCS.Exceptions.IncorrectArgsException;
import VCS.Exceptions.UncommitedChangesException;
import VCS.Exceptions.UnstagedChangesException;
import VCS.Objects.Branch;
import VCS.Objects.HEAD;

import java.io.IOException;
import java.nio.file.Path;

public class CheckoutByBranchCommand extends Command {
    private final String branchName;
    private final HEAD repoHEAD;
    public CheckoutByBranchCommand(FileSystem fileSystem, String branchName) {
        super(fileSystem);
        this.branchName = branchName;
        repoHEAD = new HEAD(fileSystem);
        System.out.println("checkout by branch: " + branchName);
    }

    @Override
    public void run() throws IncorrectArgsException, IOException, UnstagedChangesException,
            UncommitedChangesException {
        checkArgsCorrectness();
        new CommitCommand(fileSystem).checkFiles();
        Path treeLocation = fileSystem.buildTreeLocation(branchName);
        System.out.println("checkout by branch: treeLocation " + treeLocation);
        fileSystem.restoreFiles(fileSystem.splitLines(treeLocation));
        fileSystem.copyFile(treeLocation, fileSystem.getIndexLocation());
        repoHEAD.updateHEAD(branchName);
    }

    @Override
    public void checkArgsCorrectness() throws IncorrectArgsException, IOException {
        if (repoHEAD.getCurrentBranch().equals(branchName)) {
            throw new IncorrectArgsException("this is current branch");
        }
        if (new Branch(fileSystem, branchName).notExists()) {
            throw new IncorrectArgsException("branch doesn't exists");
        }
    }
}
