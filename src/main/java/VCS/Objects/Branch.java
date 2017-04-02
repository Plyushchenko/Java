package VCS.Objects;

import VCS.Data.FileSystem;

import java.io.IOException;

/**
 * Branch
 */
public class Branch {

    private final FileSystem fileSystem;
    private final String branchName;

    /**
     * Create Branch instance.
     * @param fileSystem File system
     * @param branchName Branch name
     */
    public Branch(FileSystem fileSystem, String branchName) {
        this.fileSystem = fileSystem;
        this.branchName = branchName;
    }

    /**
     * Update reference at current commit
     * @param commitHash Commit hash
     * @throws IOException Unknown IO problem
     */
    public void updateRef(String commitHash) throws IOException {
        fileSystem.writeToFile(fileSystem.buildRefLocation(branchName), commitHash);
    }

    /**
     * Delete reference
     * @throws IOException Unknown IO problem
     */
    public void deleteRef() throws IOException {
        fileSystem.deleteFile(fileSystem.buildRefLocation(branchName));
    }

    /**
     * Does branch exist?
     * @return 'true' if branch exists, 'false' otherwise
     */
    public boolean exists() {
        return fileSystem.exists(fileSystem.buildRefLocation(branchName));
    }

    /**
     * Does not branch exist?
     * @return 'true' if branch doesn't exists, 'false' otherwise
     */
    public boolean notExists() {
        return !exists();
    }

    /**
     * Get branch name
     * @return Branch name
     */
    public String getBranchName() {
        return branchName;
    }

}
