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
import VCS.Exceptions.UncommittedChangesException;
import VCS.Exceptions.UnstagedChangesException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;


import static VCS.Repo.RepoCommand.*;

/** Repo implementation */
public class RepoImpl implements Repo {

    private final Parser parser;
    private final FileSystem fileSystem;

    public RepoImpl(String[] args, Path workingDirectory) {
        parser = new Parser(args);
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
    @Override
    public String execute() throws IncorrectArgsException, IOException,
            UnstagedChangesException, UncommittedChangesException {
        RepoCommand principleCommand = valueOf(parser.getPrincipleCommandAsString());
        if (fileSystem.gitExists() && principleCommand == INIT) {
            throw new IncorrectArgsException("Git already exists");
        }
        if (fileSystem.gitNotExists() && !(principleCommand == INIT)) {
            throw new IncorrectArgsException("Git doesn't exists");
        }
        switch (principleCommand) {
            case ADD:
                return add(parser.extractAddCommandArguments());
            case BRANCH:
                return branch(parser.extractBranchCommandArguments());
            case CHECKOUT:
                return checkout(parser.extractCheckoutCommandArguments());
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
            default:
                throw new IncorrectArgsException("No such command");
        }
    }

    /**
     * Execute 'git add ...'
     * @param args File1, file2, ..., fileN
     * @throws IncorrectArgsException Incorrect args passed
     * @throws IOException Unknown IO problem
     */
    @Override
    public String add(List<String> args) throws IncorrectArgsException, IOException {
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
    @Override
    public String branch(List<String> args) throws IncorrectArgsException, IOException,
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
    @Override
    public String checkout(List<String> args) throws IOException, IncorrectArgsException,
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

    /**
     * Execute 'git commit ...'
     * @param message -m, commit message
     * @throws IncorrectArgsException Incorrect args passed
     * @throws IOException Unknown IO problem
     * @throws UnstagedChangesException Changes were not staged
     */
    @Override
    public String commit(String message) throws IncorrectArgsException, IOException,
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
    @Override
    public String init() throws IncorrectArgsException, IOException, UnstagedChangesException,
            UncommittedChangesException {
        new InitCommand(fileSystem).run();
        return "Initialized Git repository in " + fileSystem.getGitLocation();
    }

    /**
     * Execute 'git log'
     * @throws IOException Unknown IO problem
     */
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
    @Override
    public String merge(String branchName) throws IncorrectArgsException, UncommittedChangesException,
            UnstagedChangesException, IOException {
        new MergeCommand(fileSystem, branchName).run();
        return "'" + branchName + "' branch merged into current branch";
    }

}
