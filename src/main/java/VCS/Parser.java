package VCS;

import VCS.Exceptions.IncorrectArgsException;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Command line args parser*/
class Parser {

    @NotNull private final String[] args;

    Parser(@NotNull String[] args) {
        this.args = args;
    }

    @NotNull
    String getPrincipleCommandAsString() {
        return args[0];
    }

    private void checkAddArgsFormatCorrectness() throws IncorrectArgsException {
        if (args.length < 2) {
            throw new IncorrectArgsException("format: git add path1 path2 ... pathN");
        }
    }

    private void checkBranchArgsFormatCorrectness() throws IncorrectArgsException {
        if (args.length == 3 && !args[1].equals("-d") || args.length > 3) {
                throw new IncorrectArgsException("format: git branch\nOR\n" +
                        "format: git branch branchName\nOR\n" +
                        "format: git branch -d branchName");
        }
    }

    private void checkCheckoutArgsFormatCorrectness() throws IncorrectArgsException {
        if (args.length != 2 && args.length != 3) {
            throw new IncorrectArgsException("format: git checkout branchName\nOR\n" +
                    "format: git checkout commitHash\nOR\n" +
                    "format: git checkout -b branchName");
        }
    }

    private void checkCommitArgsFormatCorrectness() throws IncorrectArgsException {
        if (args.length != 3 || !args[1].equals("-m")) {
            throw new IncorrectArgsException("format: git commit -m message");
        }
    }

    void checkInitArgsFormatCorrectness() throws IncorrectArgsException {
        if (args.length != 1) {
            throw new IncorrectArgsException("format: git init");
        }
    }

    void checkLogArgsFormatCorrectness() throws IncorrectArgsException {
        if (args.length != 1) {
            throw new IncorrectArgsException("format: git log");
        }
    }

    private void checkMergeArgsFormatCorrectness() throws IncorrectArgsException {
        if (args.length != 2) {
            throw new IncorrectArgsException("format: git merge branchName");
        }
    }

    void checkStatusArgsFormatCorrectness() throws IncorrectArgsException {
        if (args.length != 1) {
            throw new IncorrectArgsException("format: git status");
        }
    }

    @NotNull
    List<String> extractAddCommandArguments() throws IncorrectArgsException {
        checkAddArgsFormatCorrectness();
        return new ArrayList<>(Arrays.asList(args).subList(1, args.length));
    }

    @NotNull
    String extractCommitCommandArguments() throws IncorrectArgsException {
        checkCommitArgsFormatCorrectness();
        return args[2];
    }

    @NotNull
    List<String> extractBranchCommandArguments() throws IncorrectArgsException {
        checkBranchArgsFormatCorrectness();
        return new ArrayList<>(Arrays.asList(args).subList(1, args.length));
    }

    @NotNull
    List<String> extractCheckoutCommandArguments() throws IncorrectArgsException {
        checkCheckoutArgsFormatCorrectness();
        return new ArrayList<>(Arrays.asList(args).subList(1, args.length));
    }

    boolean isHash(@NotNull String s) {
        if (s.length() != 40) {
            return false;
        }
        for (int i = 0; i < s.length(); i++){
            char c = s.charAt(i);
            if (!('0' <= c && c <= '9' || 'a' <= c && c <= 'f')){
                return false;
            }
        }
        return true;
    }

    @NotNull
    String extractMergeCommandArguments() throws IncorrectArgsException {
        checkMergeArgsFormatCorrectness();
        return args[1];
    }

    @NotNull
    String extractResetCommandArguments() throws IncorrectArgsException {
        checkResetArgsFormatCorrectness();
        return args[1];
    }

    private void checkResetArgsFormatCorrectness() throws IncorrectArgsException {
        if (args.length != 2) {
            throw new IncorrectArgsException("format: git reset file");
        }
    }

    @NotNull
    String extractRmCommandArguments() throws IncorrectArgsException {
        checkRmArgsFormatCorrectness();
        return args[1];
    }

    private void checkRmArgsFormatCorrectness() throws IncorrectArgsException {
        if (args.length != 2) {
            throw new IncorrectArgsException("format: git rm file");
        }
    }

    void checkCleanArgsFormatCorrectness() throws IncorrectArgsException {
        if (args.length != 1) {
            throw new IncorrectArgsException("format: git clean");
        }
    }
}
