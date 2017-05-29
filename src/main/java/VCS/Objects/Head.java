package VCS.Objects;

import VCS.Data.FileSystem;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/** Head */
public class Head {

    @NotNull private final FileSystem fileSystem;

    public Head(@NotNull FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    /**
     * Get current branch name
     * @return Current branch name
     * @throws IOException Unknown IO problems
     */
    @NotNull
    public String getCurrentBranchName() throws IOException {
        return fileSystem.getFileContentAsString(fileSystem.getHeadLocation());
    }

    /**
     * Get current commit hash of current branch
     * @return Current commit hash of current branch
     * @throws IOException Unknown IO problems
     */
    @NotNull
    public String getHeadCommitHash() throws IOException {
        String currentBranchName = getCurrentBranchName();
        return fileSystem.getFileContentAsString(fileSystem.buildRefLocation(currentBranchName));
    }

    /**
     * Update HEAD with branchName
     * @param branchName Branch name
     * @throws IOException Unknown IO problems
     */
    public void updateHead(@NotNull String branchName) throws IOException {
        fileSystem.writeToFile(fileSystem.getHeadLocation(), branchName);
    }

}
