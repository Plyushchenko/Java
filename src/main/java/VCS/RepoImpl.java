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

/**
 * Repo implementation
 */
public class RepoImpl implements Repo {

    private final Parser parser;
    private final FileSystem fileSystem;
    private String response;

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
    public void execute() throws IncorrectArgsException, IOException,
            UnstagedChangesException, UncommittedChangesException {
        String principleCommandAsString = parser.getPrincipleCommandAsString();
        if (!COMMANDS.contains(principleCommandAsString)) {
            throw new IncorrectArgsException("No such command");
        }
        if (fileSystem.gitExists() && principleCommandAsString.equals("init")) {
            throw new IncorrectArgsException("Git already exists");
        }
        if (fileSystem.gitNotExists() && !principleCommandAsString.equals("init")) {
            throw new IncorrectArgsException("Git doesn't exists");
        }
        switch (principleCommandAsString) {
            case "add":
                add(parser.extractAddCommandArguments());
                break;
            case "branch":
                branch(parser.extractBranchCommandArguments());
                break;
            case "checkout":
                checkout(parser.extractCheckoutCommandArguments());
                break;
            case "commit":
                commit(parser.extractCommitCommandArguments());
                break;
            case "init":
                parser.checkInitArgsFormatCorrectness();
                init();
                break;
            case "log":
                parser.checkLogArgsFormatCorrectness();
                log();
                break;
            case "merge":
                merge(parser.extractMergeCommandArguments());
                break;
        }
    }

    /**
     * Execute 'git add ...'
     * @param args File1, file2, ..., fileN
     * @throws IncorrectArgsException Incorrect args passed
     * @throws IOException Unknown IO problem
     */
    @Override
    public void add(List<String> args) throws IncorrectArgsException, IOException {
        new AddCommand(fileSystem, args).run();
        response = "successful add";
    }


    /**
     * Execute 'git branch ...'.
     *<pre>
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
    public void branch(List<String> args) throws IncorrectArgsException, IOException,
            UnstagedChangesException, UncommittedChangesException {
        if (args.size() == 0) {
            BranchListCommand branchListCommand = new BranchListCommand(fileSystem);
            branchListCommand.run();
            response = branchListCommand.getBranchList();
        } else {
            String branchName;
            if (args.size() == 1) {
                branchName = args.get(0);
                BranchCreateCommand branchCreateCommand = new BranchCreateCommand(fileSystem,
                        branchName);
                branchCreateCommand.run();
                response = "branch " + branchName + " created";
            } else {
                branchName = args.get(1);
                BranchDeleteCommand branchDeleteCommand = new BranchDeleteCommand(fileSystem,
                        branchName);
                branchDeleteCommand.run();
                response = "branch " + branchName + " deleted";
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
    public void checkout(List<String> args) throws IOException, IncorrectArgsException,
            UnstagedChangesException, UncommittedChangesException {
        if (args.size() == 1) {
            if (parser.isHash(args.get(0))) {
                new CheckoutByCommitCommand(fileSystem, args.get(0)).run();
            } else {
                new CheckoutByBranchCommand(fileSystem, args.get(0)).run();
            }
        } else {
            new BranchCreateCommand(fileSystem, args.get(1)).run();
            new CheckoutByBranchCommand(fileSystem, args.get(1)).run();
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
    public void commit(String message) throws IncorrectArgsException, IOException,
            UnstagedChangesException {
        CommitCommand commitCommand = new CommitCommand(fileSystem, message);
        commitCommand.run();
        response = commitCommand.getCommitHash();
    }

    /**
     * Execute 'git init'
     * @throws IncorrectArgsException Incorrect args passed
     * @throws IOException Unknown IO problem
     * @throws UnstagedChangesException Changes were not staged
     * @throws UncommittedChangesException Changes were not committed
     */
    @Override
    public void init() throws IncorrectArgsException, IOException, UnstagedChangesException,
            UncommittedChangesException {
        new InitCommand(fileSystem).run();
        response = "Initialized empty Git repository in " + fileSystem.getGitLocation();
    }

    /**
     * Execute 'git log'
     * @throws IOException Unknown IO problem
     */
    @Override
    public void log() throws IOException {
        LogCommand logCommand = new LogCommand(fileSystem);
        logCommand.run();
        response = logCommand.getLog();
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
    public void merge(String branchName) throws IncorrectArgsException, UncommittedChangesException,
            UnstagedChangesException, IOException {
        new MergeCommand(fileSystem, branchName).run();
        response = "'" + branchName + "' branch merged into current branch";
    }

    /**
     * Get command response
     * @return Command response
     */
    @Override
    public String getResponse() {
        return response;
    }
}
