package VCS;

import VCS.Commands.*;
import VCS.Commands.BranchCommands.BranchCreateCommand;
import VCS.Commands.BranchCommands.BranchDeleteCommand;
import VCS.Commands.BranchCommands.BranchListCommand;
import VCS.Commands.CheckoutCommands.CheckoutByBranchCommand;
import VCS.Commands.CheckoutCommands.CheckoutByCommitCommand;
import VCS.Data.FileSystem;
import VCS.Data.FileSystemImpl;
import VCS.Data.LoggerBuilder;
import VCS.Exceptions.IncorrectArgsException;
import VCS.Exceptions.Messages;
import VCS.Exceptions.UncommittedChangesException;
import VCS.Exceptions.UnstagedChangesException;
import org.apache.logging.log4j.core.Logger;
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
    @NotNull private Logger logger;

    public RepoImpl(@NotNull String[] args, @NotNull Path workingDirectory) throws IOException {
        parser = new Parser(args);
        this.workingDirectory = workingDirectory;
        fileSystem = new FileSystemImpl(workingDirectory);
        logger = LoggerBuilder.buildLogger(fileSystem);
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
        logger.trace("execute()");
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
     * Execute 'mygit add ...'
     * @param args File1, file2, ..., fileN
     * @throws IncorrectArgsException Incorrect args passed
     * @throws IOException Unknown IO problem
     */
    @NotNull
    @Override
    public String add(@NotNull List<String> args) throws IncorrectArgsException, IOException {
        logger.trace("begin add()");
        new AddCommand(fileSystem, logger, args).run();
        logger.trace("end add()");
        return "successful add";
    }

    /**
     * Execute 'mygit branch ...'.
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
            logger.trace("begin branchList");
            BranchListCommand branchListCommand = new BranchListCommand(fileSystem, logger);
            branchListCommand.run();
            logger.trace("end branchList");
            return branchListCommand.getBranchList();
        }
        String branchName;
        if (args.size() == 1) {
            logger.trace("begin branchCreate");
            branchName = args.get(0);
            BranchCreateCommand branchCreateCommand =
                    new BranchCreateCommand(fileSystem, logger, branchName);
            branchCreateCommand.run();
            logger.trace("end branchCreate");
            return "branch " + branchName + " created";
        } else {
            logger.trace("begin branchDelete");
            branchName = args.get(1);
            BranchDeleteCommand branchDeleteCommand =
                    new BranchDeleteCommand(fileSystem, logger, branchName);
            branchDeleteCommand.run();
            logger.trace("end branchDelete");
            return "branch " + branchName + " deleted";
        }
    }

    /**
     * Execute 'mygit checkout ...'
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
                logger.trace("begin CheckoutByCommit");
                String commitHash = args.get(0);
                new CheckoutByCommitCommand(fileSystem, logger, commitHash).run();
                logger.trace("end CheckoutByCommit");
                return "checkout: " +  commitHash + " commit";
            } else {
                logger.trace("begin CheckoutByBranch");
                String branchName = args.get(0);
                new CheckoutByBranchCommand(fileSystem, logger, branchName).run();
                logger.trace("end CheckoutByBranch");
                return "checkout: '" + branchName + "' branch";
            }
        } else {
            logger.trace("begin BranchCreate&CheckoutByBranch");
            String branchName = args.get(1);
            new BranchCreateCommand(fileSystem, logger,branchName).run();
            new CheckoutByBranchCommand(fileSystem, logger, branchName).run();
            logger.trace("end BranchCreate&CheckoutByBranch");
            return "create & checkout: '" + branchName + "' branch";
        }
    }

    @NotNull
    @Override
    public String clean() throws IncorrectArgsException, IOException, UnstagedChangesException,
            UncommittedChangesException {
        logger.trace("begin clean()");
        new CleanCommand(fileSystem, logger).run();
        logger.trace("end clean()");
        return "cleaned";
    }
    /**
     * Execute 'mygit commit ...'
     * @param message -m, commit message
     * @throws IncorrectArgsException Incorrect args passed
     * @throws IOException Unknown IO problem
     * @throws UnstagedChangesException Changes were not staged
     */
    @NotNull
    @Override
    public String commit(@NotNull String message) throws IncorrectArgsException, IOException,
            UnstagedChangesException, UncommittedChangesException {
        logger.trace("begin commit()");
        CommitCommand commitCommand = new CommitCommand(fileSystem, logger, message);
        commitCommand.run();
        logger.trace("end commit()");
        return commitCommand.getCommitHash();
    }

    /**
     * Execute 'mygit init'
     * @throws IncorrectArgsException Incorrect args passed
     * @throws IOException Unknown IO problem
     * @throws UnstagedChangesException Changes were not staged
     * @throws UncommittedChangesException Changes were not committed
     */
    @NotNull
    @Override
    public String init() throws IncorrectArgsException, IOException, UnstagedChangesException,
            UncommittedChangesException {
        logger.trace("begin init()");
        fileSystem = new FileSystemImpl(workingDirectory, true);
        new InitCommand(fileSystem, logger).run();
        logger.trace("end init()");
        return "Initialized Git repository in " + fileSystem.getGitLocation();
    }

    /**
     * Execute 'mygit log'
     * @throws IOException Unknown IO problem
     */
    @NotNull
    @Override
    public String log() throws IOException {
        logger.trace("begin log()");
        LogCommand logCommand = new LogCommand(fileSystem, logger);
        logCommand.run();
        logger.trace("end log()");
        return logCommand.getLog();
    }

    /**
     * Execute 'mygit merge ...'.
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
        logger.trace("begin merge()");
        new MergeCommand(fileSystem, logger, branchName).run();
        logger.trace("end merge()");
        return "'" + branchName + "' branch merged into current branch";
    }

    @NotNull
    @Override
    public String reset(@NotNull String fileToReset) throws IncorrectArgsException, IOException,
            UnstagedChangesException, UncommittedChangesException {
        logger.trace("begin reset()");
        new ResetCommand(fileSystem, logger, fileToReset).run();
        logger.trace("end reset()");
        return "reset file: " + fileToReset;
    }

    @NotNull
    @Override
    public String rm(@NotNull String fileToRm) throws IncorrectArgsException, IOException,
            UnstagedChangesException, UncommittedChangesException {
        logger.trace("begin rm()");
        new RmCommand(fileSystem, logger, fileToRm).run();
        logger.trace("end rm()");
        return "rm file: " + fileToRm;
    }

    @NotNull
    @Override
    public String status()throws IncorrectArgsException, IOException,
            UnstagedChangesException, UncommittedChangesException {
        logger.trace("begin status()");
        StatusCommand statusCommand = new StatusCommand(fileSystem, logger);
        statusCommand.run();
        logger.trace("end status()");
        return statusCommand.getStatus();
    }

    @NotNull
    public FileSystem getFileSystem() {
        return fileSystem;
    }

}
