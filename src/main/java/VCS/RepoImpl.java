package VCS;

import VCS.Commands.*;
import VCS.Commands.BranchCommands.BranchCreateCommand;
import VCS.Commands.BranchCommands.BranchDeleteCommand;
import VCS.Commands.BranchCommands.BranchListCommand;
import VCS.Commands.CheckoutCommands.CheckoutByBranchCommand;
import VCS.Commands.CheckoutCommands.CheckoutByCommitCommand;
import VCS.Data.FileSystem;
import VCS.Data.FileSystemImpl;
import VCS.Exceptions.IncorrectArgsException;
import VCS.Exceptions.Messages;
import VCS.Exceptions.UncommittedChangesException;
import VCS.Exceptions.UnstagedChangesException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;


import static VCS.Repo.RepoCommand.*;

/** Repo implementation */
public class RepoImpl implements Repo {

    @NotNull private final Parser parser;
    @NotNull private final Path workingDirectory;
    @NotNull private FileSystem fileSystem;

    public RepoImpl(@NotNull String[] args, @NotNull Path workingDirectory) {
        parser = new Parser(args);
        this.workingDirectory = workingDirectory;
        fileSystem = new FileSystemImpl(workingDirectory);
    }

    /**
     * Choose which command to execute.
     * Extract principle command, extract or validate args, choose which method to call
     * @throws IncorrectArgsException Incorrect args passed
     * @throws IOException Unknown IO problem
     * @throws UnstagedChangesException Changes were not staged
     * @throws UncommittedChangesException Changes were not committed
     */
    @NotNull
    @Override
    public String execute() throws IncorrectArgsException, IOException,
            UnstagedChangesException, UncommittedChangesException {
        RepoCommand principleCommand = valueOf(parser.getPrincipleCommandAsString().toUpperCase());
        if (fileSystem.gitExists() && principleCommand == INIT) {
            throw new IncorrectArgsException(Messages.GIT_ALREADY_EXISTS);
        }
        if (fileSystem.gitNotExists() && !(principleCommand == INIT)) {
            throw new IncorrectArgsException(Messages.GIT_DOESN_T_EXIST);
        }
        switch (principleCommand) {
            case ADD:
                return add(parser.extractAddCommandArguments());
            case BRANCH:
                return branch(parser.extractBranchCommandArguments());
            case CHECKOUT:
                return checkout(parser.extractCheckoutCommandArguments());
            case CLEAN:
                parser.checkCleanArgsFormatCorrectness();
                return clean();
            case COMMIT:
                return commit(parser.extractCommitCommandArguments());
            case INIT:
                parser.checkInitArgsFormatCorrectness();
                return init();
            case LOG:
                parser.checkLogArgsFormatCorrectness();
                return log();
            case MERGE:
                return merge(parser.extractMergeCommandArguments());
            case RESET:
                return reset(parser.extractResetCommandArguments());
            case RM:
                return rm(parser.extractRmCommandArguments());
            case STATUS:
                parser.checkStatusArgsFormatCorrectness();
                return status();
            default:
                throw new IncorrectArgsException(Messages.NO_SUCH_COMMAND);
        }
    }

    /**
     * Execute 'git add ...'
     * @param args File1, file2, ..., fileN
     * @throws IncorrectArgsException Incorrect args passed
     * @throws IOException Unknown IO problem
     */
    @NotNull
    @Override
    public String add(@NotNull List<String> args) throws IncorrectArgsException, IOException {
        new AddCommand(fileSystem, args).run();
        return "successful add";
    }

    /**
     * Execute 'git branch ...'.
     * <pre>
     * git branch: build branch list (as String)
     * git branch branchName: create 'branchName' branch
     * git branch -d branchName: delete 'branchName' branch
     * </pre>
     * @param args [[-d,] branch name]
     * @throws IncorrectArgsException Incorrect args passed
     * @throws IOException Unknown IO problem
     * @throws UnstagedChangesException Changes were not staged
     * @throws UncommittedChangesException Changes were not committed
     */
    @NotNull
    @Override
    public String branch(@NotNull List<String> args) throws IncorrectArgsException, IOException,
            UnstagedChangesException, UncommittedChangesException {
        if (args.size() == 0) {
            BranchListCommand branchListCommand = new BranchListCommand(fileSystem);
            branchListCommand.run();
            return branchListCommand.getBranchList();
        } else {
            String branchName;
            if (args.size() == 1) {
                branchName = args.get(0);
                BranchCreateCommand branchCreateCommand = new BranchCreateCommand(fileSystem,
                        branchName);
                branchCreateCommand.run();
                return "branch " + branchName + " created";
            } else {
                branchName = args.get(1);
                BranchDeleteCommand branchDeleteCommand = new BranchDeleteCommand(fileSystem,
                        branchName);
                branchDeleteCommand.run();
                return "branch " + branchName + " deleted";
            }
        }
    }

    /**
     * Execute 'git checkout ...'
     * <pre>
     * git checkout branchName: switch to 'branchName' branch
     * git checkout -b branchName: create 'branchName' branch and switch to it
     * git checkout commitHash: switch to commit which hash is 'commitHash'
     * </pre>
     * @param args ([-b,] branch name) | commit hash
     * @throws IncorrectArgsException Incorrect args passed
     * @throws IOException Unknown IO problem
     * @throws UnstagedChangesException Changes were not staged
     * @throws UncommittedChangesException Changes were not committed
     */
    @NotNull
    @Override
    public String checkout(@NotNull List<String> args) throws IncorrectArgsException, IOException,
            UnstagedChangesException, UncommittedChangesException {
        if (args.size() == 1) {
            if (parser.isHash(args.get(0))) {
                String commitHash = args.get(0);
                new CheckoutByCommitCommand(fileSystem, commitHash).run();
                return "checkout: " +  commitHash + " commit";
            } else {
                String branchName = args.get(0);
                new CheckoutByBranchCommand(fileSystem, branchName).run();
                return "checkout: '" + branchName + "' branch";
            }
        } else {
            String branchName = args.get(1);
            new BranchCreateCommand(fileSystem, branchName).run();
            new CheckoutByBranchCommand(fileSystem, branchName).run();
            return "create & checkout: '" + branchName + "' branch";
        }
    }

    @NotNull
    @Override
    public String clean() throws IncorrectArgsException, IOException, UnstagedChangesException,
            UncommittedChangesException {
        new CleanCommand(fileSystem).run();
        return "cleaned";
    }
    /**
     * Execute 'git commit ...'
     * @param message -m, commit message
     * @throws IncorrectArgsException Incorrect args passed
     * @throws IOException Unknown IO problem
     * @throws UnstagedChangesException Changes were not staged
     */
    @NotNull
    @Override
    public String commit(@NotNull String message) throws IncorrectArgsException, IOException,
            UnstagedChangesException, UncommittedChangesException {
        CommitCommand commitCommand = new CommitCommand(fileSystem, message);
        commitCommand.run();
        return commitCommand.getCommitHash();
    }

    /**
     * Execute 'git init'
     * @throws IncorrectArgsException Incorrect args passed
     * @throws IOException Unknown IO problem
     * @throws UnstagedChangesException Changes were not staged
     * @throws UncommittedChangesException Changes were not committed
     */
    @NotNull
    @Override
    public String init() throws IncorrectArgsException, IOException, UnstagedChangesException,
            UncommittedChangesException {
        fileSystem = new FileSystemImpl(workingDirectory, true);
        new InitCommand(fileSystem).run();
        return "Initialized Git repository in " + fileSystem.getGitLocation();
    }

    /**
     * Execute 'git log'
     * @throws IOException Unknown IO problem
     */
    @NotNull
    @Override
    public String log() throws IOException {
        LogCommand logCommand = new LogCommand(fileSystem);
        logCommand.run();
        return logCommand.getLog();
    }

    /**
     * Execute 'git merge ...'.
     * Merge 'branchName' branch into current branch.
     * @param branchName Branch name
     * @throws IncorrectArgsException Incorrect args passed
     * @throws IOException Unknown IO problem
     * @throws UnstagedChangesException Changes were not staged
     * @throws UncommittedChangesException Changes were not committed
     */
    @NotNull
    @Override
    public String merge(@NotNull String branchName) throws IncorrectArgsException, IOException,
            UnstagedChangesException, UncommittedChangesException {
        new MergeCommand(fileSystem, branchName).run();
        return "'" + branchName + "' branch merged into current branch";
    }

    @NotNull
    @Override
    public String reset(@NotNull String fileToReset) throws IncorrectArgsException, IOException,
            UnstagedChangesException, UncommittedChangesException {
        new ResetCommand(fileSystem, fileToReset).run();
        return "reset file: " + fileToReset;
    }

    @NotNull
    @Override
    public String rm(@NotNull String fileToRm) throws IncorrectArgsException, IOException,
            UnstagedChangesException, UncommittedChangesException {
        new RmCommand(fileSystem, fileToRm).run();
        return "rm file: " + fileToRm;
    }

    @NotNull
    @Override
    public String status()throws IncorrectArgsException, IOException,
            UnstagedChangesException, UncommittedChangesException {
        StatusCommand statusCommand = new StatusCommand(fileSystem);
        statusCommand.run();
        return statusCommand.getStatus();
    }

    public FileSystem getFileSystem() {
        return fileSystem;
    }
}
