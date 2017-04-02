package VCS;

import VCS.Exceptions.IncorrectArgsException;
import VCS.Exceptions.UncommittedChangesException;
import VCS.Exceptions.UnstagedChangesException;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Git repository
 */
public interface Repo {

    /**
     * List of possible commands
     */
    List COMMANDS = Arrays.asList("add", "branch", "checkout", "commit", "init", "log", "merge");

    /**
     * Choose which command to execute.
     * @throws IncorrectArgsException Incorrect args passed
     * @throws IOException Unknown IO problem
     * @throws UnstagedChangesException Changes were not staged
     * @throws UncommittedChangesException Changes were not committed
     */
    void execute() throws IncorrectArgsException, IOException, UnstagedChangesException,
            UncommittedChangesException;

    /**
     * Execute 'git add ...'
     * @param args File1, file2, ..., fileN
     * @throws IncorrectArgsException Incorrect args passed
     * @throws IOException Unknown IO problem
     */
    void add(List<String> args) throws IncorrectArgsException, IOException;

    /**
     * Execute 'git branch ...'.
     * @param args [[-d,] branch name]
     * @throws IncorrectArgsException Incorrect args passed
     * @throws IOException Unknown IO problem
     * @throws UnstagedChangesException Changes were not staged
     * @throws UncommittedChangesException Changes were not committed
     */
    void branch(List<String> args) throws IncorrectArgsException, IOException,
            UnstagedChangesException, UncommittedChangesException;

    /**
     * Execute 'git checkout ...'
     * @param args [-b,] branch name | commit hash
     * @throws IncorrectArgsException Incorrect args passed
     * @throws IOException Unknown IO problem
     * @throws UnstagedChangesException Changes were not staged
     * @throws UncommittedChangesException Changes were not committed
     */
    void checkout(List<String> args) throws IncorrectArgsException, IOException,
            UnstagedChangesException, UncommittedChangesException;

    /**
     * Execute 'git commit ...'
     * @param message -m, commit message
     * @throws IncorrectArgsException Incorrect args passed
     * @throws IOException Unknown IO problem
     * @throws UnstagedChangesException Changes were not staged
     */
    void commit(String message) throws IncorrectArgsException, IOException,
            UnstagedChangesException;

    /**
     * Execute 'git init'
     * @throws IncorrectArgsException Incorrect args passed
     * @throws IOException Unknown IO problem
     * @throws UnstagedChangesException Changes were not staged
     * @throws UncommittedChangesException Changes were not committed
     */
    void init() throws IncorrectArgsException, IOException, UnstagedChangesException,
            UncommittedChangesException;

    /**
     * Execute 'git log'
     * @throws IOException Unknown IO problem
     */
    void log() throws IOException;

    /**
     * Execute 'git merge ...'
     * @param branchName Branch name
     * @throws IncorrectArgsException Incorrect args passed
     * @throws IOException Unknown IO problem
     * @throws UnstagedChangesException Changes were not staged
     * @throws UncommittedChangesException Changes were not committed
     */
    void merge(String branchName) throws IncorrectArgsException, IOException,
            UnstagedChangesException, UncommittedChangesException;

    /**
     * Get command response
     * @return Command response
     */
    String getResponse();
}
