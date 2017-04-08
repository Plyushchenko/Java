package VCS;

import VCS.Exceptions.IncorrectArgsException;
import VCS.Exceptions.UncommittedChangesException;
import VCS.Exceptions.UnstagedChangesException;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/** Git repository */
public interface Repo {

    /** List of possible commands */
    enum RepoCommand {
        ADD, BRANCH, CHECKOUT, COMMIT, INIT, LOG, MERGE
    }

    /**
     * Choose which command to execute.
     * @return Response about command execution
     * @throws IncorrectArgsException Incorrect args passed
     * @throws IOException Unknown IO problem
     * @throws UnstagedChangesException Changes were not staged
     * @throws UncommittedChangesException Changes were not committed
     */
    String execute() throws IncorrectArgsException, IOException, UnstagedChangesException,
            UncommittedChangesException;

    /**
     * Execute 'git add ...'
     * @param args File1, file2, ..., fileN
     * @return Response about adding
     * @throws IncorrectArgsException Incorrect args passed
     * @throws IOException Unknown IO problem
     */
    String add(List<String> args) throws IncorrectArgsException, IOException;

    /**
     * Execute 'git branch ...'.
     * @param args [[-d,] branch name]
     * @return Response about branch creation/deletion
     * @throws IncorrectArgsException Incorrect args passed
     * @throws IOException Unknown IO problem
     * @throws UnstagedChangesException Changes were not staged
     * @throws UncommittedChangesException Changes were not committed
     */
    String branch(List<String> args) throws IncorrectArgsException, IOException,
            UnstagedChangesException, UncommittedChangesException;

    /**
     * Execute 'git checkout ...'
     * @param args [-b,] branch name | commit hash
     * @return Response about checkout
     * @throws IncorrectArgsException Incorrect args passed
     * @throws IOException Unknown IO problem
     * @throws UnstagedChangesException Changes were not staged
     * @throws UncommittedChangesException Changes were not committed
     */
    String checkout(List<String> args) throws IncorrectArgsException, IOException,
            UnstagedChangesException, UncommittedChangesException;

    /**
     * Execute 'git commit ...'
     * @param message -m, commit message
     * @return Commit hash
     * @throws IncorrectArgsException Incorrect args passed
     * @throws IOException Unknown IO problem
     * @throws UnstagedChangesException Changes were not staged
     */
    String commit(String message) throws IncorrectArgsException, IOException,
            UnstagedChangesException, UncommittedChangesException;

    /**
     * Execute 'git init'
     * @return Response about initialization
     * @throws IncorrectArgsException Incorrect args passed
     * @throws IOException Unknown IO problem
     * @throws UnstagedChangesException Changes were not staged
     * @throws UncommittedChangesException Changes were not committed
     */
    String init() throws IncorrectArgsException, IOException, UnstagedChangesException,
            UncommittedChangesException;

    /**
     * Execute 'git log'
     * @throws IOException Unknown IO problem
     */
    String log() throws IOException;

    /**
     * Execute 'git merge ...'
     * @param branchName Branch name
     * @return Response about merging
     * @throws IncorrectArgsException Incorrect args passed
     * @throws IOException Unknown IO problem
     * @throws UnstagedChangesException Changes were not staged
     * @throws UncommittedChangesException Changes were not committed
     */
    String merge(String branchName) throws IncorrectArgsException, IOException,
            UnstagedChangesException, UncommittedChangesException;

}
