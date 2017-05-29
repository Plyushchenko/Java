package VCS;

import VCS.Exceptions.IncorrectArgsException;
import VCS.Exceptions.UncommittedChangesException;
import VCS.Exceptions.UnstagedChangesException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/** Git repository */
public interface Repo {

    /** List of possible commands */
    enum RepoCommand {
        ADD, BRANCH, CHECKOUT, CLEAN, COMMIT, INIT, LOG, MERGE, RESET, RM, STATUS
    }

    /**
     * Choose which command to execute.
     * @return Response about command execution
     * @throws IncorrectArgsException Incorrect args passed
     * @throws IOException Unknown IO problem
     * @throws UnstagedChangesException Changes were not staged
     * @throws UncommittedChangesException Changes were not committed
     */
    @NotNull
    String execute() throws IncorrectArgsException, IOException, UnstagedChangesException,
            UncommittedChangesException;

    /**
     * Execute 'mygit add ...'
     * @param args File1, file2, ..., fileN
     * @return Response about adding
     * @throws IncorrectArgsException Incorrect args passed
     * @throws IOException Unknown IO problem
     */
    @NotNull
    String add(@NotNull List<String> args) throws IncorrectArgsException, IOException;

    /**
     * Execute 'mygit branch ...'.
     * @param args [[-d,] branch name]
     * @return Response about branch creation/deletion
     * @throws IncorrectArgsException Incorrect args passed
     * @throws IOException Unknown IO problem
     * @throws UnstagedChangesException Changes were not staged
     * @throws UncommittedChangesException Changes were not committed
     */
    @NotNull
    String branch(@NotNull List<String> args) throws IncorrectArgsException, IOException,
            UnstagedChangesException, UncommittedChangesException;

    /**
     * Execute 'mygit checkout ...'
     * @param args [-b,] branch name | commit hash
     * @return Response about checkout
     * @throws IncorrectArgsException Incorrect args passed
     * @throws IOException Unknown IO problem
     * @throws UnstagedChangesException Changes were not staged
     * @throws UncommittedChangesException Changes were not committed
     */
    @NotNull
    String checkout(@NotNull List<String> args) throws IncorrectArgsException, IOException,
            UnstagedChangesException, UncommittedChangesException;

    @NotNull
    String clean() throws IncorrectArgsException, IOException, UnstagedChangesException,
            UncommittedChangesException;

    /**
     * Execute 'mygit commit ...'
     * @param message -m, commit message
     * @return Commit hash
     * @throws IncorrectArgsException Incorrect args passed
     * @throws IOException Unknown IO problem
     * @throws UnstagedChangesException Changes were not staged
     */
    @NotNull
    String commit(@NotNull String message) throws IncorrectArgsException, IOException,
            UnstagedChangesException, UncommittedChangesException;

    /**
     * Execute 'mygit init'
     * @return Response about initialization
     * @throws IncorrectArgsException Incorrect args passed
     * @throws IOException Unknown IO problem
     * @throws UnstagedChangesException Changes were not staged
     * @throws UncommittedChangesException Changes were not committed
     */
    @NotNull
    String init() throws IncorrectArgsException, IOException, UnstagedChangesException,
            UncommittedChangesException;

    /**
     * Execute 'mygit log'
     * @throws IOException Unknown IO problem
     */
    @NotNull
    String log() throws IOException;

    /**
     * Execute 'mygit merge ...'
     * @param branchName Branch name
     * @return Response about merging
     * @throws IncorrectArgsException Incorrect args passed
     * @throws IOException Unknown IO problem
     * @throws UnstagedChangesException Changes were not staged
     * @throws UncommittedChangesException Changes were not committed
     */
    @NotNull
    String merge(@NotNull String branchName) throws IncorrectArgsException, IOException,
            UnstagedChangesException, UncommittedChangesException;

    @NotNull
    String reset(@NotNull String fileToReset) throws IncorrectArgsException, IOException,
            UnstagedChangesException, UncommittedChangesException;

    @NotNull
    String rm(@NotNull String fileToRm) throws IncorrectArgsException, IOException,
            UnstagedChangesException, UncommittedChangesException;

    @NotNull
    String status() throws IncorrectArgsException, IOException, UnstagedChangesException,
            UncommittedChangesException;

}
