package vcs;

import vcs.vcsexceptions.*;
import vcs.vcsobjects.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

import static vcs.VCSBranch.*;
import static vcs.VCSFileUtils.*;
import static vcs.VCSHead.*;
import static vcs.VCSIndex.*;
import static vcs.VCSLog.*;
import static vcs.vcsobjects.Blob.buildBlob;
import static vcs.vcsobjects.VCSObject.isHash;

/**
 * class provides methods for user commands: init, add, commit, checkout, branch, log, merge
 */
public class VCS {

    public enum commands {
        init, add, commit, checkout, branch, log, merge
    }
    private static final String MASTER_BRANCH = "master";
    private static boolean isInit = false;
    static boolean isInit() {
        return isInit;
    }

    /**
     * init repo:
     * create .git/ and other subfolders and files;
     * commit "initial commit" at master branch
     * @throws GitAlreadyInitedException for .git/ already exists which means git already has been initialised
     * @throws GitInitException for problems during repo initialisation
     */
    public static void init() throws GitAlreadyInitedException, GitInitException {
        isInit = true;
        if (Files.exists(GIT_LOCATION)) {
            throw new GitAlreadyInitedException();
        }
        try {
            createGitDirectoriesAndFiles();
            String initialCommitHash = commit("initial commit");
            VCSBranch.createBranch(MASTER_BRANCH, initialCommitHash);
            switchToBranch(MASTER_BRANCH);
        } catch (Exception e) {
            e.printStackTrace();
            throw new GitInitException();
        }
        isInit = false;
    }

    /**
     * checkout by commit (if arg is SHA1 hash) or branch
     * @param arg for name of branch/commit
     * @throws NoGitFoundException for repo doesn't exist
     * @throws NoBranchFoundException for branch arg doesn't exist
     * list of exceptions for file system problems (don't know how to handle them properly):
     * @throws ContentReadException for reading content of file (as blob) problem
     * @throws ContentWriteException for writing content of file (as blob) problem
     * @throws HeadReadException for reading from .git/HEAD problems
     * @throws HeadWriteException for writing into .git/HEAD problems
     * @throws BranchReadException for reading from .git/refs/ problems
     * @throws BranchWriteException for writing into .git/refs/ problems
     * @throws TreeReadException for reading tree content problem
     * @throws DirectoryCreateException for creating folder problems
     * @throws LogWriteException for writing into .git/log/ problems
     */
    public static void checkout(String arg) throws ContentWriteException, HeadWriteException, TreeReadException, NoBranchFoundException,
            BranchWriteException, HeadReadException, BranchReadException, DirectoryCreateException, LogWriteException,
            NoGitFoundException, ContentReadException {
        if (isHash(arg)) {
            switchToCommit(arg);
        } else {
            switchToBranch(arg);
        }
    }

    /**
     * creates (if not exists) branch branchName
     * @param branchName for name of branch
     * @throws NoGitFoundException for repo doesn't exist
     * @throws BranchAlreadyCreatedException for branch already exists
     * list of exceptions for file system problems (don't know how to handle them properly):
     * @throws HeadReadException for reading from .git/HEAD problems
     * @throws BranchReadException for reading from .git/refs/ problems
     * @throws BranchWriteException for writing into .git/refs/ problems
     * @throws LogWriteException for writing into .git/log/ problems
     */
    public static void createBranch(String branchName) throws HeadReadException, NoGitFoundException, BranchReadException,
            LogWriteException, BranchWriteException, BranchAlreadyCreatedException {
        VCSBranch.createBranch(branchName, getHeadCommitHash());
    }

    /**
     * building log of currentBranch by reading .git/logs/getCurrentBranchName()
     * @return log is byte array
     * @throws NoGitFoundException for repo doesn't exist
     * list of exceptions for file system problems (don't know how to handle them properly):
     * @throws HeadReadException for reading from .git/HEAD problems
     * @throws LogReadException for reading from .git/log/ problems
     */
    public static byte[] getCurrentBranchLog() throws NoGitFoundException, HeadReadException, LogReadException {
        if (Files.notExists(GIT_LOCATION))
            throw new NoGitFoundException();
        Path logLocation = Paths.get(LOGS_LOCATION + File.separator + getCurrentBranchName());
        try {
            return Files.readAllBytes(logLocation);
        } catch (IOException e) {
            throw new LogReadException();
        }
    }

    /**
     * build list of branch names like this:
     *   branch1
     *   branch2
     * * currentBranch
     *   branch3
     * by getting all names of files in .git/refs/
     * @return list of branch names
     * @throws NoGitFoundException for repo doesn't exist
     * list of exceptions for file system problems (don't know how to handle them properly):
     * @throws BranchReadException for reading from .git/refs/ problems
     * @throws HeadReadException for reading from .git/HEAD problems
     */
    public static List<String> buildBranchNamesList() throws NoGitFoundException, BranchReadException, HeadReadException {
        if (Files.notExists(GIT_LOCATION)) {
            throw new NoGitFoundException();
        }
        String currentBranchName = getCurrentBranchName();
        List<String> branchNames = new ArrayList<>();
        try {
            for (Path filePath : Files.walk(REFS_LOCATION).filter(file -> file.toFile().isFile()).collect(Collectors.toList())) {
                String branchName = filePath.getFileName().toString();
                if (branchName.equals(currentBranchName)) {
                    branchNames.add("* " + branchName);
                } else {
                    branchNames.add(" " + branchName);
                }
            }
        } catch (IOException e) {
            throw new BranchReadException();
        }
        return branchNames;
    }

    /**
     * perform git add for all files from list
     * by getting .git/index and updating it
     * @param filesToAdd list of paths
     * @throws NoGitFoundException for repo doesn't exist
     * @throws FileToAddNotExistsException for any file doesn't exist
     * @throws NothingChangedSinceLastAddException for no changes since last add
     * list of exceptions for file system problems (don't know how to handle them properly):
     * @throws AddException for unknown problems; .git/index has state as before add
     * @throws NoIndexFoundException for .git/index doesn't exist
     */
    public static void add(List<Path> filesToAdd) throws NoGitFoundException, NoIndexFoundException, AddException,
            FileToAddNotExistsException, NothingChangedSinceLastAddException {
        if (Files.notExists(GIT_LOCATION))
            throw new NoGitFoundException();
        if (Files.notExists(INDEX_LOCATION)) {
            throw new NoIndexFoundException();
        }
        checkExistenceOfAllFiles(filesToAdd);
        List<String> indexContent = null;
        try{
            indexContent = getIndexContent();
            updateIndex(buildIndexContentAfterAdd(filesToAdd));
        } catch (ContentReadException | IndexWriteException | IndexReadException e) {
            try {
                updateIndex(indexContent);
            } catch (IndexWriteException e1) {
                //ignore
            }
            throw new AddException();
        }
    }

    /**
     * commit all the files from .git/index
     * WARNING: unfortunately, no check whether state of file in user folder is same as the state in .git/index
     * get index content like this:
     * file1
     * hash1
     * file2
     * hash2
     * ...
     * build blobs from each of file content
     * build tree object from blobs' hashes and files' paths
     * build commit from tree content
     * update .git/refs/getCurrentBranchName() with commit hash
     * add line about commit to .git/log/getCurrentBranchName()
     * @param message for text of commit message
     * @return hash of commit
     * @throws NoGitFoundException for repo doesn't exist
     * @throws NothingChangedSinceLastCommitException for no changes since last commit
     * list of exceptions for file system problems (don't know how to handle them properly):
     * @throws ContentReadException for reading content of file (as blob) problem
     * @throws ContentWriteException for writing content of file (as blob) problem
     * @throws HeadReadException for reading from .git/HEAD problems
     * @throws BranchReadException for reading from .git/refs/ problems
     * @throws BranchWriteException for writing into .git/refs/ problems
     * @throws LogWriteException for writing into .git/log/ problems
     * @throws IndexReadException for reading from .git/index problems
     */
    public static String commit(String message) throws NoGitFoundException, ContentReadException, ContentWriteException,
            IndexReadException, HeadReadException, BranchWriteException, LogWriteException, BranchReadException,
            NothingChangedSinceLastCommitException {
        if (Files.notExists(GIT_LOCATION))
            throw new NoGitFoundException();

        List<String> indexContent = getIndexContent();
        List<Path> filesToCommit = new ArrayList<>();
        List<String> hashesOfFilesToCommit = new ArrayList<>();
        for (int i = 0; i < indexContent.size(); i += 2) {
            filesToCommit.add(Paths.get(indexContent.get(i)));
            hashesOfFilesToCommit.add(indexContent.get(i + 1));
        }

        for (Path fileToCommit : filesToCommit) {
            Blob blob = buildBlob(fileToCommit);
            blob.addObject(OBJECTS_LOCATION);
        }
        Tree tree = new Tree(filesToCommit, hashesOfFilesToCommit);
        tree.addObject(OBJECTS_LOCATION);
        Commit commit = new Commit(tree.getHash().getBytes(), message);
        commit.addObject(OBJECTS_LOCATION);
        if (!isInit) {
            if (commit.getHash().equals(getHeadCommitHash())) {
                throw new NothingChangedSinceLastCommitException();
            }
            String currentBranchName = getCurrentBranchName();
            updateRefToBranch(currentBranchName, commit.getHash());
            updateLog(currentBranchName, commit);
        }

        return commit.getHash();
    }

    /**
     * remove branch by name
     * by removing .git/refs/branchName and .git/log/branchName files
     * @param branchName for name of branch
     * @throws NoGitFoundException for repo doesn't exist
     * @throws NoBranchFoundException for branch arg doesn't exist
     * @throws MasterBranchDeleteException for trying to delete master branch
     * @throws CurrentBranchDeleteException for trying do delete current branch
     * list of exceptions for file system problems (don't know how to handle them properly):
     * @throws HeadReadException for reading from .git/HEAD problems
     */
    public static void removeBranch(String branchName) throws NoGitFoundException, NoBranchFoundException, MasterBranchDeleteException,
            HeadReadException, CurrentBranchDeleteException {
        if (Files.notExists(GIT_LOCATION)){
            throw new NoGitFoundException();
        }
        if (!branchExists(branchName)){
            throw new NoBranchFoundException();
        }
        if (branchName.equals(MASTER_BRANCH)){
            throw new MasterBranchDeleteException();
        }
        if (branchName.equals(getCurrentBranchName())){
            throw new CurrentBranchDeleteException();
        }
        try {
            Files.delete(Paths.get(REFS_LOCATION + File.separator + branchName));
            Files.delete(Paths.get(LOGS_LOCATION + File.separator + branchName));
        } catch (IOException e) {
            //ignore
        }

    }

    /**
     * merge branchToMergeName into current branch;
     * if some file has different states in these 2 branches
     * then state from branchToMergeName overrides state from currentBranch;
     * also all these files will be added to .git/index and
     * then git add then git commit with message about merge (for all files in .git/index)
     * @param branchToMergeName for name of branch to merge
     * @throws NoGitFoundException for repo doesn't exist
     * @throws FileToAddNotExistsException for any file doesn't exist
     * @throws NothingChangedSinceLastAddException for no changes since last add
     * @throws NothingChangedSinceLastCommitException for no changes since last commit
     * list of exceptions for file system problems (don't know how to handle them properly):
     * @throws ContentReadException for reading content of file (as blob) problem
     * @throws ContentWriteException for writing content of file (as blob) problem
     * @throws HeadReadException for reading from .git/HEAD problems
     * @throws BranchReadException for reading from .git/refs/ problems
     * @throws BranchWriteException for writing into .git/refs/ problems
     * @throws LogWriteException for writing into .git/log/ problems
     * @throws IndexReadException for reading from .git/index problems
     * @throws AddException for unknown problems; .git/index has state as before add
     * @throws NoIndexFoundException for .git/index doesn't exist
     */
    public static void merge(String branchToMergeName) throws NoGitFoundException, HeadReadException, ContentWriteException,
            AddException, NoIndexFoundException, FileToAddNotExistsException, NothingChangedSinceLastAddException,
            IndexReadException, BranchWriteException, LogWriteException, BranchReadException, ContentReadException,
            NothingChangedSinceLastCommitException, TreeReadException {
        if (Files.notExists(GIT_LOCATION)){
            throw new NoGitFoundException();
        }
        String currentBranchName = getCurrentBranchName();
        Path branchToMergeRefLocation = Paths.get(REFS_LOCATION + File.separator + branchToMergeName);

        List<String> filePathsAndHashesToMerge = null;
        try {
            String commitHashToMergeLocation = new String(Files.readAllBytes(
                    Paths.get(OBJECTS_LOCATION + File.separator + new String(Files.readAllBytes(branchToMergeRefLocation)))));
            filePathsAndHashesToMerge = Files.readAllLines(
                    Paths.get(OBJECTS_LOCATION + File.separator + commitHashToMergeLocation));
        } catch (IOException e) {
            throw new TreeReadException();
        }
        List<Path> filesToAdd = new ArrayList<>();

        for (int i = 0; i < (filePathsAndHashesToMerge == null ? 0 : filePathsAndHashesToMerge.size()); i += 2) {
            Path path = Paths.get(filePathsAndHashesToMerge.get(i));
            filesToAdd.add(path);
            String hash = filePathsAndHashesToMerge.get(i + 1);
            try {
                byte[] content = Files.readAllBytes(Paths.get(OBJECTS_LOCATION + File.separator + hash));
                createEmptyFile(path);
                Files.write(path, content);
            } catch (IOException e) {
                throw new ContentWriteException();
            }
        }
        try {
            add(filesToAdd);
        } catch (NothingChangedSinceLastAddException e) {
            //ignore
        }
        commit("merge " + branchToMergeName + " into " + currentBranchName);
    }

}
