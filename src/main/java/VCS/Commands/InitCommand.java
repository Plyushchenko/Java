package VCS.Commands;

import VCS.Commands.BranchCommands.BranchCreateCommand;
import VCS.Commands.CheckoutCommands.CheckoutByBranchCommand;
import VCS.Data.FileSystem;
import VCS.Exceptions.IncorrectArgsException;
import VCS.Exceptions.UncommittedChangesException;
import VCS.Exceptions.UnstagedChangesException;

import java.io.IOException;

public class InitCommand extends Command {

    public InitCommand(FileSystem fileSystem) {
        super(fileSystem);
    }

    @Override
    public void run() throws IncorrectArgsException, IOException, UnstagedChangesException,
            UncommittedChangesException {
        createGitDirectoriesAndFiles();
        CommitCommand initialCommitCommand = new CommitCommand(fileSystem, "initial commit");
        initialCommitCommand.run();
        String initialCommitHash = initialCommitCommand.getCommitHash();
        new BranchCreateCommand(fileSystem, "master").runMaster(initialCommitHash);
        new CheckoutByBranchCommand(fileSystem, "master").run();
    }

    private void createGitDirectoriesAndFiles() throws IOException {
        fileSystem.createDirectory(fileSystem.getGitLocation());
        fileSystem.createDirectory(fileSystem.getLogsLocation());
        fileSystem.createDirectory(fileSystem.getObjectsLocation());
        fileSystem.createDirectory(fileSystem.getRefsLocation());
        fileSystem.createFileOrClearIfExists(fileSystem.getHEADLocation());
        fileSystem.createFileOrClearIfExists(fileSystem.getIndexLocation());
    }

    @Override
    public void checkArgsCorrectness() throws IncorrectArgsException {

    }

}
