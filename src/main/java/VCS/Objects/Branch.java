package VCS.Objects;

import VCS.Data.FileSystem;

import java.io.IOException;

public class Branch {

    private final FileSystem fileSystem;
    private final String branchName;
    public Branch(FileSystem fileSystem, String branchName) {
        this.fileSystem = fileSystem;
        this.branchName = branchName;
    }

    public void updateRef(String commitHash) throws IOException {
        fileSystem.writeToFile(fileSystem.buildRefLocation(branchName), commitHash);
    }

    public void deleteRef() throws IOException {
        fileSystem.deleteFile(fileSystem.buildRefLocation(branchName));
    }

    public void updateLog(String commitHash) throws IOException {
        fileSystem.appendToFile(fileSystem.buildLogLocation(branchName), commitHash);
    }

    public void deleteLog() throws IOException {
        fileSystem.deleteFile(fileSystem.buildLogLocation(branchName));
    }

    public boolean exists() {
        return fileSystem.exists(fileSystem.buildRefLocation(branchName));
    }

    public boolean notExists() {
        return !exists();
    }

    public String getBranchName() {
        return branchName;
    }

}
