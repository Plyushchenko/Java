package vcs;

import vcs.vcsexceptions.BranchReadException;
import vcs.vcsexceptions.HeadReadException;
import vcs.vcsexceptions.HeadWriteException;
import vcs.vcsexceptions.NoGitFoundException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static vcs.VCSBranch.getCurrentBranchName;
import static vcs.VCSFileUtils.GIT_LOCATION;
import static vcs.VCSFileUtils.HEAD_LOCATION;
import static vcs.VCSFileUtils.REFS_LOCATION;

class VCSHead {

    static String getHeadCommitHash() throws NoGitFoundException, HeadReadException, BranchReadException {
        if (Files.notExists(GIT_LOCATION))
            throw new NoGitFoundException();

        Path branchLocation = Paths.get(REFS_LOCATION + File.separator + getCurrentBranchName());
        try {
            return new String(Files.readAllBytes(branchLocation));
        } catch (IOException e) {
            throw new BranchReadException();
        }
    }

    static void updateHead(String branchName) throws HeadWriteException {
        try {
            Files.write(HEAD_LOCATION, branchName.getBytes());
        } catch (IOException e) {
            throw new HeadWriteException();
        }
    }

}
