package VCS.Objects;

import VCS.Data.FileSystem;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * Branch
 */
public class Branch {

    @NotNull private final FileSystem fileSystem;
    @NotNull private final String branchName;

    public Branch(@NotNull FileSystem fileSystem, @NotNull String branchName) {
        this.fileSystem = fileSystem;
        this.branchName = branchName;
    }

    /**
     * Update reference at current commit
     * @param commitHash Commit hash
     * @throws IOException Unknown IO problem
     */
    public void updateRef(@NotNull String commitHash) throws IOException {
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

    @NotNull
    public String getBranchName() {
        return branchName;
    }

}
