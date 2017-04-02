package VCS;


import VCS.Exceptions.IncorrectArgsException;
import VCS.Exceptions.NoSuchCommandException;
import VCS.Exceptions.UncommitedChangesException;
import VCS.Exceptions.UnstagedChangesException;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public interface Repo {

    List COMMANDS = Arrays.asList("add", "branch", "checkout", "commit", "init", "log", "merge");

    void execute() throws NoSuchCommandException, IncorrectArgsException, IOException,
            UnstagedChangesException, UncommitedChangesException;

    void add(List<String> args) throws IncorrectArgsException, IOException;

    void branch(List<String> args) throws IOException, IncorrectArgsException,
            UnstagedChangesException, UncommitedChangesException;

    void checkout(List<String> args) throws IOException, IncorrectArgsException,
            UnstagedChangesException, UncommitedChangesException;

    void commit(String message) throws IncorrectArgsException, IOException,
            UnstagedChangesException;

    void init() throws IncorrectArgsException, IOException, UnstagedChangesException,
            UncommitedChangesException;

    void log() throws IOException;

    void merge(String branchName) throws IncorrectArgsException, UncommitedChangesException,
            UnstagedChangesException, IOException;

    String getResponse();
}
