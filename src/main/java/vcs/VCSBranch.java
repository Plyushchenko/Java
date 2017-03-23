package vcs;

import vcs.vcsexceptions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static vcs.VCS.*;
import static vcs.VCSFileUtils.*;
import static vcs.VCSHead.getHeadCommitHash;
import static vcs.VCSHead.updateHead;
import static vcs.VCSLog.updateLog;

/**
 * class with methods to work with branches
 */
class VCSBranch {

    /**
     * update .git/refs/branchName by writing commitHash
     */
    static void updateRefToBranch(String branchName, String commitHash) throws BranchWriteException {
        Path branchLocation = Paths.get(REFS_LOCATION + File.separator + branchName);
        try {
            Files.write(branchLocation, commitHash.getBytes());
        } catch (IOException e) {
            throw new BranchWriteException();
        }
    }

    /**
     * create new branch;
     * create new .git/log/branchName file with information about branch creation;
     * write commitHash into .git/refs/branchName
     */
    static void createBranch(String branchName, String commitHash)
            throws NoGitFoundException, BranchAlreadyCreatedException, LogWriteException, BranchWriteException {
        if (Files.notExists(GIT_LOCATION))
            throw new NoGitFoundException();
        if (branchExists(branchName)) {
            throw new BranchAlreadyCreatedException();
        }
        updateLog(branchName, commitHash + " branch created\n");
        try {
            updateRefToBranch(branchName, commitHash);
        } catch (BranchWriteException e) {
            try {
                Path logLocation = Paths.get(LOGS_LOCATION + File.separator + branchName);
                Files.deleteIfExists(logLocation);
            } catch (IOException e1) {
                //ignore
            }
            throw e;
        }
    }

    /**
     * check whether branch exists
     */
    static boolean branchExists(String branchName) {
        Path branchLocation = Paths.get(REFS_LOCATION + File.separator + branchName);
        return Files.exists(branchLocation);
    }

    /**
     * switch to branchName
     * update .git/HEAD with by writing branchName
     * restore all the files from last commit on branchName
     */
    static void switchToBranch(String branchName) throws NoGitFoundException, NoBranchFoundException, HeadWriteException,
            BranchReadException, HeadReadException, ContentReadException, ContentWriteException, TreeReadException, DirectoryCreateException {
        if (Files.notExists(GIT_LOCATION))
            throw new NoGitFoundException();
        if (!isInit() && !branchExists(branchName)) {
            throw new NoBranchFoundException();
        }
        updateHead(branchName);
        if (!isInit()) {
            restoreFiles(getHeadCommitHash());
        }
    }

    /**
     * create (if not exists) branch commitName and switch to this branch
     */
    static void switchToCommit(String commitHash) throws HeadWriteException, NoGitFoundException, NoBranchFoundException,
            TreeReadException, HeadReadException, ContentWriteException, ContentReadException, BranchReadException, DirectoryCreateException,
            LogWriteException, BranchWriteException {
        if (!branchExists(commitHash)) {
            try {
                VCSBranch.createBranch(commitHash, commitHash);
            } catch (BranchAlreadyCreatedException e) {
                //ignore
            }
        }
        switchToBranch(commitHash);
    }

    /**
     * getting current branch name by reading from .git/HEAD
     */
    static String getCurrentBranchName() throws NoGitFoundException, HeadReadException {
        if (Files.notExists(GIT_LOCATION))
            throw new NoGitFoundException();
        try {
            return new String(Files.readAllBytes(HEAD_LOCATION));
        } catch (IOException e) {
            throw new HeadReadException();
        }
    }

}
