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

public class VCS {

    public enum commands {
        init, add, commit, checkout, branch, log, merge;
    }
    private static final String MASTER_BRANCH = "master";
    private static boolean isInit = false;
    static boolean isInit() {
        return isInit;
    }

    /**
     * init repo; create .git folder and other subfolders and files;
     * commit "initial commit"
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
     */
    public static void checkout(String arg) throws ContentWriteException, HeadWriteException, TreeReadException, NoBranchFoundException,
            BranchWriteException, HeadReadException, BranchReadException, DirectioryCreateException, LogWriteException,
            NoGitFoundException, ContentReadException, BranchAlreadyCreatedException {
        if (isHash(arg)) {
            switchToCommit(arg);
        } else {
            switchToBranch(arg);
        }
    }

    /**
     * create new branch
     */
    public static void createBranch(String branchName) throws HeadReadException, NoGitFoundException, BranchReadException,
            LogWriteException, BranchWriteException, BranchAlreadyCreatedException {
        VCSBranch.createBranch(branchName, getHeadCommitHash());
    }

    /**
     * building log by reading .git/logs/getCurrentBranchName() and returning it as byte array
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
     * building list of branch names like this:
     *   branch1
     *   branch2
     * * currentBranch
     *   branch3
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


    public static void add(List<Path> filesToAdd) throws NoGitFoundException, NoIndexFoundException, AddException,
            IndexReadException, FileToAddNotExistsException, NothingChangedSinceLastAddException {
        if (Files.notExists(GIT_LOCATION))
            throw new NoGitFoundException();
        if (Files.notExists(INDEX_LOCATION)) {
            throw new NoIndexFoundException();
        }
        checkExistenceOfAllFiles(filesToAdd);
        List<String> indexContent = getIndexContent();
        try {
            updateIndex(buildIndexContentAfterAdd(filesToAdd));
        } catch (ContentReadException | IndexWriteException e) {
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
     * don't check whether state of file in user folder is same as the state in .git/index
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
     */
    public static void removeBranch(String branchName) throws NoGitFoundException, NoBranchFoundException, MasterBranchDeleteException {
        if (Files.notExists(GIT_LOCATION)){
            throw new NoGitFoundException();
        }
        if (!branchExists(branchName)){
            throw new NoBranchFoundException();
        }
        if (branchName.equals(MASTER_BRANCH)){
            throw new MasterBranchDeleteException();
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
     */
    public static void merge(String branchToMergeName) throws NoGitFoundException, HeadReadException, ContentWriteException,
            AddException, NoIndexFoundException, FileToAddNotExistsException, NothingChangedSinceLastAddException,
            IndexReadException, BranchWriteException, LogWriteException, BranchReadException, ContentReadException,
            NothingChangedSinceLastCommitException {
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
            e.printStackTrace();
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
