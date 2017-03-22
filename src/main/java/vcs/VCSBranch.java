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

class VCSBranch {

    static void updateRefToBranch(String branchName, String commitHash) throws BranchWriteException {
        Path branchLocation = Paths.get(REFS_LOCATION + File.separator + branchName);
        try {
            Files.write(branchLocation, commitHash.getBytes());
        } catch (IOException e) {
            throw new BranchWriteException();
        }
    }

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

    static boolean branchExists(String branchName) {
        Path branchLocation = Paths.get(REFS_LOCATION + File.separator + branchName);
        return Files.exists(branchLocation);
    }

    static void switchToBranch(String branchName) throws NoGitFoundException, NoBranchFoundException, HeadWriteException,
            BranchReadException, HeadReadException, ContentReadException, ContentWriteException, TreeReadException, DirectioryCreateException {
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

    static void switchToCommit(String commitHash) throws HeadWriteException, NoGitFoundException, NoBranchFoundException,
            TreeReadException, HeadReadException, ContentWriteException, ContentReadException, BranchReadException, DirectioryCreateException,
            LogWriteException, BranchWriteException, BranchAlreadyCreatedException {
        if (!branchExists(commitHash)) {
            VCSBranch.createBranch(commitHash, commitHash);
        }
        switchToBranch(commitHash);
    }

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
