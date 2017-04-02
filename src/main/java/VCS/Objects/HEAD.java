package VCS.Objects;

import VCS.Data.FileSystem;

import java.io.IOException;

/**
 * HEAD
 */
public class HEAD {
    private final FileSystem fileSystem;

    /**
     * Build HEAD instance
     * @param fileSystem File system
     */
    public HEAD(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    /**
     * Get current branch name
     * @return Current branch name
     * @throws IOException Unknown IO problems
     */
    public String getCurrentBranch() throws IOException {
        return fileSystem.getFileContentAsString(fileSystem.getHEADLocation());
    }

    /**
     * Get current commit hash of current branch
     * @return Current commit hash of current branch
     * @throws IOException Unknown IO problems
     */
    public String getHEADCommitHash() throws IOException {
        String currentBranch = getCurrentBranch();
        return fileSystem.getFileContentAsString(fileSystem.buildRefLocation(currentBranch));
    }

    /**
     * Update HEAD
     * @param branchName Branch name
     * @throws IOException Unknown IO problems
     */
    public void updateHEAD(String branchName) throws IOException {
        fileSystem.writeToFile(fileSystem.getHEADLocation(), branchName);
    }

}
