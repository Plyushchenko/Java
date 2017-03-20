package vcs;

import vcs.vcsexceptions.LogWriteException;
import vcs.vcsobjects.Commit;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import static vcs.VCSFileUtils.LOGS_LOCATION;

class VCSLog {

    static void updateLog(String branchName, Commit commit) throws LogWriteException {
        Path logLocation = Paths.get(LOGS_LOCATION + File.separator + branchName);
        try {
            Files.write(logLocation, commit.getAllInformation().getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new LogWriteException();
        }
    }

    static void updateLog(String branchName, String message) throws LogWriteException {
        Path logLocation = Paths.get(LOGS_LOCATION + File.separator + branchName);
        try {
            Files.write(logLocation, message.getBytes());
        } catch (IOException e) {
            throw new LogWriteException();
        }
    }

}
