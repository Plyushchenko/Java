package VCS.Objects;

import VCS.Data.FileSystem;

import java.io.IOException;

public class HEAD {
    private final FileSystem fileSystem;
    public HEAD(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    public String getCurrentBranch() throws IOException {
        return fileSystem.getFileContentAsString(fileSystem.getHEADLocation());
    }

    public String getHEADCommitHash() throws IOException {
        String currentBranch = getCurrentBranch();
        return fileSystem.getFileContentAsString(fileSystem.buildRefLocation(currentBranch));
    }

    public void updateHEAD(String branchName) throws IOException {
        fileSystem.writeToFile(fileSystem.getHEADLocation(), branchName);
    }

}
