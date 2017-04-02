package VCS;

import VCS.Commands.AddCommand;
import VCS.Commands.BranchCommands.BranchCreateCommand;
import VCS.Commands.BranchCommands.BranchDeleteCommand;
import VCS.Commands.BranchCommands.BranchListCommand;
import VCS.Commands.CheckoutCommands.CheckoutByBranchCommand;
import VCS.Commands.CheckoutCommands.CheckoutByCommitCommand;
import VCS.Commands.CommitCommand;
import VCS.Commands.InitCommand;
import VCS.Commands.LogCommand;
import VCS.Data.FileSystem;
import VCS.Data.FileSystemImpl;
import VCS.Exceptions.IncorrectArgsException;
import VCS.Exceptions.NoSuchCommandException;
import VCS.Exceptions.UncommitedChangesException;
import VCS.Exceptions.UnstagedChangesException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class RepoImpl implements Repo {

    private final Parser parser;
    private final FileSystem fileSystem;
    private String response;

    public RepoImpl(String[] args, Path workingDirectory) {
        parser = new Parser(args);
        fileSystem = new FileSystemImpl(workingDirectory);
    }

    @Override
    public void execute() throws NoSuchCommandException, IncorrectArgsException, IOException,
            UnstagedChangesException, UncommitedChangesException {
        String principleCommandAsString = parser.getPrincipleCommandAsString();
        if (!COMMANDS.contains(principleCommandAsString)) {
            throw new NoSuchCommandException("No such command; please read howto.txt");
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
                merge();
                break;
        }
    }

    @Override
    public void add(List<String> args) throws IncorrectArgsException, IOException {
        new AddCommand(args, fileSystem).run();
    }


    @Override
    public void branch(List<String> args) throws IOException, IncorrectArgsException,
            UnstagedChangesException, UncommitedChangesException {

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

    @Override
    public void checkout(List<String> args) throws IOException, IncorrectArgsException,
            UnstagedChangesException, UncommitedChangesException {
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

    @Override
    public void commit(String message) throws IncorrectArgsException, IOException, UnstagedChangesException {
        CommitCommand commitCommand = new CommitCommand(fileSystem, message);
        commitCommand.run();
        response = commitCommand.getCommitHash();
    }

    @Override
    public void init() throws IncorrectArgsException, IOException, UnstagedChangesException,
            UncommitedChangesException {
        new InitCommand(fileSystem).run();
    }

    @Override
    public void log() throws IOException {
        LogCommand logCommand = new LogCommand(fileSystem);
        logCommand.run();
        response = logCommand.getLog();
    }

    @Override
    public void merge() {

    }

    @Override
    public String getResponse() {
        return response;
    }
}
